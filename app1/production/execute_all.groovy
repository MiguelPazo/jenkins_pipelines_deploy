#!/usr/bin/env groovy
/**
 * Created by Miguel Pazo (https://miguelpazo.com)
 */
package qa

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
                stage('production-app1_frontend_auth') {
                    steps {
                        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                            build job: 'production-app1_frontend_auth', wait: true
                        }
                    }
                }

                stage('production-app1_webservice_auth') {
                    steps {
                        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                            build job: 'production-app1_webservice_auth', wait: true
                        }
                    }
                }
            }
        }
    }
}