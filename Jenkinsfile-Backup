pipeline{
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: "15"))
        disableConcurrentBuilds()
        timeout(time: 15, unit: "MINUTES")
        timestamps()
    }
    parameters{
        string(name: "CHART_VERSION", defaultValue: "${params.CHART_VERSION}", description: "This version number should be incremented each time you make changes. Versions are expected to follow Semantic Versioning 0.1.0 (https://semver.org/)")
        choice(name: "CHOICE", choices: ["All Stages", "Helm Stage"], description: "Pick 'All Stages' is you one to run the complete pipeline (Maven, Helm, Trigger: CD_Helm), or Helm is you only want to run the stage (Helm)")
        booleanParam(name: "DEPLOY", defaultValue: true, description: "Do you want to deploy the chart in production?")
    }
    environment{
        CONTAINER_REGISTRY = "docker.io/kvncont"
        IMAGE_NAME = "hello-world"
        IMAGE_TAG = "${GIT_COMMIT}"
        CHART_NAME = "hello-world"
        CHART_VERSION = "${params.CHART_VERSION}"
        REPOSITORY_NAME = "chartmuseum"
        REPOSITORY_URL = "http://chart-museum:8080"
    }
    stages{
        stage("Maven"){
            when { equals expected: "All Stages", actual: "${params.CHOICE}" }
            tools{
                maven "apache-maven-3.6.3"
            }
            steps{
                script{
                    def image = "${CONTAINER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
                    withCredentials([
                    usernamePassword(credentialsId: "DOCKER_REGISTRY", passwordVariable: "PASSWORD", usernameVariable: "USERNAME")
                    ]) {
                        sh "mvn clean package -Djib.to.image=${image} -Djib.to.tags=${IMAGE_TAG} -Djib.to.auth.username=${USERNAME} -Djib.to.auth.password=${PASSWORD}"
                    }
                }
            }
            post{
                always{
                    echo "========always========"
                }
                success{
                    echo "========A executed successfully========"
                }
                failure{
                    echo "========A execution failed========"
                }
            }
        }
        stage("Helm"){
            steps{
                sh "helm repo add ${REPOSITORY_NAME} ${REPOSITORY_URL}"
                sh "helm repo update"
                sh "helm search repo ${CHART_NAME}"
                // If you want to use this line you need to install de push plugin
                // helm plugin install https://github.com/chartmuseum/helm-push.git
                sh "helm push hello-world/ chartmuseum --version ${CHART_VERSION}"
                sh "helm repo update"
                sh "helm search repo ${CHART_NAME}"
            }
            post{
                always{
                    echo "========always========"
                }
                success{
                    echo "========A executed successfully========"
                }
                failure{
                    echo "========A execution failed========"
                }
            }
        }
        stage ("Trigger: CD_Helm") {
            when { 
                branch "master"
                equals expected: "true", actual: "${params.DEPLOY}"
            }
            steps {
                build job: "CD_Helm/main", wait: false, propagate: false,
                parameters: [
                    string(name: "CHART_NAME", value: "${CHART_NAME}"),
                    string(name: "CHART_VERSION", value: "${CHART_VERSION}")
                ]
            }
        }
    }
    post{
        always{
            echo "========always========"
        }
        success{
            echo "========pipeline executed successfully ========"
        }
        failure{
            echo "========pipeline execution failed========"
        }
    }
}