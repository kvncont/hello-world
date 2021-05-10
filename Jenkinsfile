pipeline {
    agent any
    options  {
        buildDiscarder(logRotator(numToKeepStr: "15"))
        disableConcurrentBuilds()
        timeout(time: 15, unit: "MINUTES")
        timestamps()
    }
    environment {
        CONTAINER_REGISTRY = "docker.io/kvncont"
        IMAGE_NAME = "hello-world"
        IMAGE_TAG = "${BRANCH_NAME}-${GIT_COMMIT}"
    }
    stages {
        stage("Maven - Build") {
            agent {
                docker { image "maven:3.8.1-jdk-11-slim" }
            }
            steps {
                sh """
                    mvn dependency:tree
                    mvn clean package
                """
            }
        }
        stage("Sonarqube - Code Analysis") {
            agent {
                docker { 
                    image "maven:3.8.1-jdk-11-slim"
                    reuseNode true
                }
            }
            steps {
                script {
                    withSonarQubeEnv("SONARQUBE_CLOUD") {
                        sh "mvn sonar:sonar -Dsonar.branch.name=${BRANCH_NAME}"
                    }
                }
            }
        }
        stage("Sonarqube - Quality Gate") {
            steps {
                timeout(time: 5, unit: "MINUTES") {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}