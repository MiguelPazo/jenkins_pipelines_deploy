#!/usr/bin/env groovy
/**
 * Created by Miguel Pazo (https://miguelpazo.com)
 */
package production

def modules = [:]

pipeline {
    agent any

    tools { nodejs "nodejs_14" }

    environment {
        SLS_SERVICE_NAME = 'app1-webservice-auth-ts'
        SONAR_RUNNER_HOME = tool 'sonarqube_4.6'
    }

    stages {
        stage('Setting environments variables') {
            steps {
                script {
                    wrap([$class: 'BuildUser']) {
                        try {
                            env.USER_DEPLOYER = BUILD_USER
                        } catch (Throwable e) {
                            echo "Caught ${e.toString()}"
                            env.USER_DEPLOYER = "Jenkins"
                            currentBuild.result = "SUCCESS"
                        }
                    }
                }
            }
        }

        stage('Loading deploy_projects') {
            steps {
                dir('deploy_projects') {
                    git branch: 'main',
                            credentialsId: 'github',
                            url: 'git@github.com:MiguelPazo/jenkins_pipelines_deploy.git'
                }

                sh 'mv deploy_projects/app1/_general.groovy ./_general.groovy'
            }
        }

        stage('Loading credentials') {
            steps {
                script {
                    modules._general = load "_general.groovy"
                    modules._general.loadCredentials('production')
                }
            }
        }

        stage('Download from Github') {
            steps {
                dir('webservice_app') {
                    git branch: 'main',
                            credentialsId: 'github',
                            url: 'git@github.com:MiguelPazo/aws_serverless_base_ts.git'
                }
            }
        }

        stage('Setting SLS service name') {
            steps {
                dir('webservice_app') {
                    sh "sed -i '0,/service/{/service/d;}' serverless.yml"
                    sh "echo 'service: ${SLS_SERVICE_NAME}' > serverless2.yml"
                    sh "cat serverless.yml >> serverless2.yml"
                    sh "rm -rf serverless.yml"
                    sh "mv serverless2.yml serverless.yml"
                    sh "cat serverless.yml"
                }
            }
        }

        stage('Preparing config files') {
            steps {
                sh 'mv deploy_projects/app1/__projects/webservice_auth/params-production.yml webservice_app/env/params-production.yml'
                sh 'mv deploy_projects/app1/__projects/webservice_auth/params-production-vpc.yml webservice_app/env/vpc.yml'
                sh 'mv deploy_projects/app1/__projects/webservice_auth/params-production-custom.yml webservice_app/env/custom.yml'
                sh 'mv deploy_projects/app1/__projects/webservice_auth/.mocharc.js webservice_app/.mocharc.js'
                sh 'rm -rf deploy_projects'

                sh 'cat webservice_app/env/vpc.yml >> webservice_app/env/params-production.yml'
                sh 'cat webservice_app/env/custom.yml >> webservice_app/env/params-production.yml'

                sh "sed -i '/profile:/d' webservice_app/serverless.yml"
                sh "sed -i '/DYNAMODB_ACCESS_KEY/d' webservice_app/src/databases/dynamoDb.ts"
                sh "sed -i '/DYNAMODB_SECRET_KEY/d' webservice_app/src/databases/dynamoDb.ts"

                sh 'cat webservice_app/serverless.yml'
            }
        }

        stage('Replace params credentials') {
            steps {
                script {
                    modules._general.replaceParams('webservice_app/env/params-production.yml')
                }
            }
        }

        stage('Replace params credentials to Unitest') {
            steps {
                script {
                    modules._general.replaceParams('webservice_app/.mocharc.js')
                }
            }
        }

        stage('Downloading dependecies') {
            steps {
                dir('webservice_app') {
                    sh 'npm install'
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                catchError {
                    dir('webservice_app') {
                        sh 'npm run test'
                    }
                }
            }

            post {
                success {
                    echo 'Unit Test success'
                }
                failure {
                    error('Build is aborted due to Unit Test failed')
                }
            }
        }

        stage('SonarQube analysis') {
            steps {
                sh "echo 'sonar.projectKey=${JOB_NAME}' >> webservice_app/sonar-project.properties"
                sh "echo 'sonar.sources=src' >> webservice_app/sonar-project.properties"
                sh "echo 'sonar.tests=src' >> webservice_app/sonar-project.properties"
                sh "echo 'sonar.test.inclusions=src/**/*.spec.ts' >> webservice_app/sonar-project.properties"
                sh "echo 'sonar.exclusions=node_modules,src/controllers/*,src/filters/*,src/message/*,src/constants/*,src/dto/*,src/auth/*,src/middlewares/*,src/common/*,src/server.ts,src/*Config.ts' >> webservice_app/sonar-project.properties"
                sh "echo 'sonar.typescript.lcov.reportPaths=coverage/lcov.info' >> webservice_app/sonar-project.properties"

                sh 'cat webservice_app/sonar-project.properties'

                withSonarQubeEnv('SonarQube') {
                    dir('webservice_app') {
                        sh "${SONAR_RUNNER_HOME}/bin/sonar-scanner -X"
                    }
                }
            }
        }

        stage("Quality Gate") {
            steps {
                catchError {
                    timeout(time: 10, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }

            post {
                success {
                    echo 'Build stage successful'
                }
                failure {
                    error('Build is aborted due to not pass Quality Gate')
                }
            }
        }

        stage('Compiling app') {
            steps {
                dir('webservice_app') {
                    sh 'tsc'
                    sh 'cp -R ./storage .build/'
                }
            }
        }

        stage('Deploying serverless') {
            steps {
                dir('webservice_app') {
                    sh 'sls create_domain -s production'
                    sh 'sls deploy -s production'
                }
            }
        }
    }

    post {
        always {
            deleteDir()
        }
    }
}
