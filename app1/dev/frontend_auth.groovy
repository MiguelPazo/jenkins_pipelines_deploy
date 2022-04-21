#!/usr/bin/env groovy
/**
 * Created by Miguel Pazo (https://miguelpazo.com)
 */
package dev

def modules = [:]

pipeline {
    agent any

    tools { nodejs "nodejs_14" }

    environment {
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
                    modules._general.loadCredentials('dev')
                }
            }
        }

        stage('Download from Github') {
            steps {
                slackSend(color: "good", message: "Job: ${JOB_NAME} - starting deployment - User: ${USER_DEPLOYER}")

                dir('frontend_app') {
                    git branch: 'main',
                            credentialsId: 'github',
                            url: 'git@github.com:juangura19/jgr-frontend-base.git'
                }
            }
        }

        stage('Preparing config files') {
            steps {
                sh 'mv deploy_projects/app1/__projects/frontend_auth/environment.dev.ts frontend_app/src/environments/environment.prod.ts'
                sh 'rm -rf deploy_projects'
            }
        }

        stage('Replace params credentials') {
            steps {
                script {
                    modules._general.replaceParams('frontend_app/src/environments/environment.prod.ts')
                }
            }
        }

        stage('Downloading dependecies') {
            steps {
                dir('frontend_app') {
                    sh 'npm install'
                }
            }
        }

        stage('SonarQube analysis') {
            steps {
                sh "echo 'sonar.projectKey=${JOB_NAME}' >> frontend_app/sonar-project.properties"
                sh "echo 'sonar.sources=src' >> frontend_app/sonar-project.properties"
                sh "echo 'sonar.tests=src' >> frontend_app/sonar-project.properties"
                sh "echo 'sonar.test.inclusions=src/**/*.spec.ts' >> frontend_app/sonar-project.properties"
                sh "echo 'sonar.exclusions=node_modules,src/controllers/*,src/filters/*,src/message/*,src/constants/*,src/dto/*,src/auth/*,src/middlewares/*,src/common/*,src/server.ts,src/*Config.ts' >> frontend_app/sonar-project.properties"

                sh 'cat frontend_app/sonar-project.properties'

                withSonarQubeEnv('SonarQube') {
                    dir('frontend_app') {
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

        stage('Compiling') {
            steps {
                dir('frontend_app') {
                    sh 'node --max_old_space_size=1024 ./node_modules/@angular/cli/bin/ng build --configuration production'
                    sh 'cd dist/iasd-frontend-main && tar cfz app.tar.gz *'
                }
            }
        }

        stage('Deploying') {
            steps {
                sshagent(['app1_jenkins_ssh']) {
                    dir('frontend_app') {
                        sh """
                        ssh -tt -o 'StrictHostKeyChecking no' jenkins@${APP1_SERVER1_HOST} <<EOF                            
                            sudo rm -rf /var/www/app1_frontend_auth
                            sudo mkdir /var/www/app1_frontend_auth
                            sudo chown jenkins:jenkins /var/www/app1_frontend_auth
                            
                            exit
                        EOF
                        """

                        sh 'scp dist/iasd-frontend-main/app.tar.gz jenkins@${APP1_SERVER1_HOST}:/var/www/app1_frontend_auth'

                        sh """
                        ssh -tt -o 'StrictHostKeyChecking no' jenkins@${APP1_SERVER1_HOST} <<EOF
                          cd /var/www/app1_frontend_auth
                          tar xfz app.tar.gz
                          rm -rf app.tar.gz
                                                    
                          sudo chmod 775 /var/www/app1_frontend_auth -R
                          sudo chown www-data:www-data -R /var/www/app1_frontend_auth

                          exit
                        EOF
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            deleteDir()
        }
        success {
            slackSend(color: "good", message: "Job: ${JOB_NAME} - deploy success - User: ${USER_DEPLOYER}")
        }
        unstable {
            slackSend(color: "warning", message: "Job: ${JOB_NAME} - is unstable - User: ${USER_DEPLOYER}")
        }
        failure {
            slackSend(color: "danger", message: "Job: ${JOB_NAME} - deploy failed - User: ${USER_DEPLOYER}")
        }
        changed {
            slackSend(color: "warning", message: "Job: ${JOB_NAME} - has changed status - User: ${USER_DEPLOYER}")
        }
    }

    triggers {
        GenericTrigger(
                genericRequestVariables: [
                        [key: 'job', regexpFilter: '']
                ],

                causeString: 'WebHook',
                tokenCredentialId: 'app1_trigger_webhook_token',

                printContributedVariables: true,
                printPostContent: true,
                silentResponse: true,

                regexpFilterText: '$job',
                regexpFilterExpression: '^' + JOB_NAME + '$'
        )
    }
}
