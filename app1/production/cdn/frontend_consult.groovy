#!/usr/bin/env groovy
/**
 * Created by Miguel Pazo (https://miguelpazo.com)
 */

def modules = [:]

pipeline {
    agent any

    tools { nodejs "nodejs_14" }

    environment {
        BASE_URL = 'https://auth.app1.com'
        PULUMI_PROJECT_NAME = 'mpazo-app1-cdn-auth'
        PULUMI_PROJECT_TAG = 'app1'
        PULUMI_STACK = 'production'
        PULUMI_AWS_REGION = 'us-east-1'
        PULUMI_CDN_CERTIFICATE_ARN = 'arn:aws:acm:us-east-1:xxxxxx:certificate/xxxxxxxxxxxxxxxx'
        PULUMI_CDN_NAME = 'cdn-app1-auth'
        PULUMI_CDN_DOMAIN = 'auth.app1.com'
        PULUMI_CDN_TTL = 86400
        PATH = "$PATH:$HOME/.pulumi/bin"
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
                            credentialsId: 'gitlab',
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

        stage('Download from GitLab') {
            steps {
                dir('content') {
                    git branch: 'main',
                            credentialsId: 'gitlab',
                            url: 'git@github.com:juangura19/jgr-frontend-base.git'
                }

                dir('deploy_cdn') {
                    git branch: 'master',
                            url: 'https://github.com/MiguelPazo/pulumi_aws_deploy_cdn.git'
                }
            }
        }

        stage('Preparing config files') {
            steps {
                sh 'mv deploy_projects/app1/__projects/frontend_auth/environment.production.ts content/src/environments/environment.prod.ts'
                sh 'rm -rf deploy_projects'
            }
        }

        stage("Install pulumi") {
            steps {
                sh "curl -fsSL https://get.pulumi.com | sh"
                sh "$HOME/.pulumi/bin/pulumi version"
            }
        }

        stage('Configuring pulumi project name') {
            steps {
                sh "sed -i -- 's/deploy_cdn/${PULUMI_PROJECT_NAME}/g' deploy_cdn/Pulumi.yaml"
            }
        }

        stage('Replace params credentials') {
            steps {
                script {
                    modules._general.replaceParams('content/src/environments/environment.prod.ts')
                }
            }
        }

        stage('Downloading dependecies') {
            steps {
                sh 'cd content/ && npm install'
            }
        }

        stage('Compiling') {
            steps {
                sh 'cd content/ && node --max_old_space_size=1024 ./node_modules/@angular/cli/bin/ng build --configuration production'
            }
        }

        //only required on the first deployment of the stack
        stage('Configuring pulumi init stack') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh 'pulumi stack init ${PULUMI_STACK} --cwd deploy_cdn/'
                }
            }
        }

        stage('Deploy on AWS') {
            steps {
                sh 'mv deploy_cdn/data/cdn_errors content/dist/app/'
                sh 'rm -rf deploy_cdn/data/*'
                sh 'cp -R content/dist/app/* deploy_cdn/data/'
                sh 'cd deploy_cdn && npm install'
                sh 'pulumi stack select ${PULUMI_STACK} --cwd deploy_cdn/'
                sh 'pulumi config set aws:region ${PULUMI_AWS_REGION} --cwd deploy_cdn/'
                sh 'pulumi config set generalTagName ${PULUMI_PROJECT_TAG} --cwd deploy_cdn/'
                sh 'pulumi config set certificateArn ${PULUMI_CDN_CERTIFICATE_ARN} --cwd deploy_cdn/'
                sh 'pulumi config set cdnName ${PULUMI_CDN_NAME} --cwd deploy_cdn/'
                sh 'pulumi config set targetDomain ${PULUMI_CDN_DOMAIN} --cwd deploy_cdn/'
                sh 'pulumi config set ttl ${PULUMI_CDN_TTL} --cwd deploy_cdn/'
                sh 'cat deploy_cdn/Pulumi.yaml'
                sh 'cat deploy_cdn/Pulumi.${PULUMI_STACK}.yaml'
                sh 'pulumi up --yes --cwd deploy_cdn/'
//                sh 'pulumi refresh --yes --cwd deploy_cdn/'
//                sh 'pulumi destroy --yes --cwd deploy_cdn/'
            }
        }
    }

    post {
        always {
            deleteDir()
        }
    }
}