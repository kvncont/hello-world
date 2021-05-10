pipeline {
    agent any
    options  {
        buildDiscarder(logRotator(numToKeepStr: "15"))
        disableConcurrentBuilds()
        timeout(time: 15, unit: "MINUTES")
        timestamps()
    }
    environment {
        REGISTRY_CREDENTIAL = "DOCKER_REGISTRY"
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
        stage("Docker Build"){
            steps{
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }
        stage("Vulnerability Scan"){
            agent {
                docker {
                    image "aquasec/trivy"
                    args "-v /var/run/docker.sock:/var/run/docker.sock ${IMAGE_NAME}:${IMAGE_TAG}"
                    reuseNode true
                }
            }
            steps {
                // sh "docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy ${IMAGE_NAME}:${IMAGE_TAG}"
                echo "Analyzing image ${IMAGE_NAME}:${IMAGE_TAG}..."
            }
        }
        stage("Docker Push"){
            steps{
                script {
                    // docker.withRegistry( '', registryCredential ) {
                    //     IMAGE.push("${IMAGE_TAG}")
                    //     IMAGE.push('latest')
                    // }
                    echo "Pushing Image..."
                }
            }
        }
    }
}