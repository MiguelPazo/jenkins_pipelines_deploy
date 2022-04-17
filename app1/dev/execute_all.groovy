#!/usr/bin/env groovy
/**
 * Created by Miguel Pazo (https://miguelpazo.com)
 */
package dev

pipeline {
    agent any

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

        stage('Execute group 1') {
            parallel {
                stage('dev-app1_frontend_auth') {
                    steps {
                        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                            build job: 'dev-app1_frontend_auth', wait: true
                        }
                    }
                }

                stage('dev-app1_webservice_auth') {
                    steps {
                        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                            build job: 'dev-app1_webservice_auth', wait: true
                        }
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
}