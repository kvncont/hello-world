pipeline {
    agent any
    options  {
        buildDiscarder(logRotator(numToKeepStr: "15"))
        disableConcurrentBuilds()
        timeout(time: 15, unit: "MINUTES")
        timestamps()
    }
    environment {
        IMAGE_NAME = "docker.io/kvncont/hello-world"
        IMAGE_TAG = "${BRANCH_NAME}.${BUILD_NUMBER}.${GIT_COMMIT}"
    }
    stages {
        stage("Maven - Package") {
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
        stage('Parallel Stage') {
            parallel {
                stage("Containerization"){
                    stages {
                        stage("Docker Build"){
                            steps{
                                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                            }
                        }
                        stage("Vulnerability Scan"){
                            steps {
                                sh "docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy ${IMAGE_NAME}:${IMAGE_TAG}"
                            }
                        }
                    }
                }
                stage("Sonar") {
                    stages {
                        stage("SonarQube - Code Analysis") {
                            agent {
                                docker {
                                    image "maven:3.8.1-jdk-11-slim"
                                    reuseNode true
                                }
                            }
                            steps {
                                script {
                                    withSonarQubeEnv("SONAR_CLOUD") {
                                        sh "mvn sonar:sonar -Dsonar.branch.name=${BRANCH_NAME}"
                                    }
                                }
                            }
                        }
                        stage("SonarQube - Quality Gate") {
                            steps {
                                timeout(time: 5, unit: "MINUTES") {
                                    waitForQualityGate abortPipeline: true
                                }
                            }
                        }
                    }
                }
            }
        }
        stage("Docker Push"){
            when {
                anyOf {
                    branch "master"
                    branch "develop"
                }
            }
            steps{
                script {
                    withDockerRegistry(credentialsId: 'DOCKER_REGISTRY') {
                        sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                    }
                }
            }
        }
    }
    post{
        always{
            sh "docker rmi ${IMAGE_NAME}:${IMAGE_TAG}"
        }
    }
}