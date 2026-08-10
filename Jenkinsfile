pipeline {
    agent any

    environment {
        PATH = "/Applications/Docker.app/Contents/Resources/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"

        DOCKER_REGISTRY = 'docker.io/dayan78'
        IMAGE_NAME = 'library-app'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        FULL_IMAGE = "${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"

        K8S_NAMESPACE = 'library'
        K8S_DEPLOYMENT = 'library-app'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Check Docker') {
                steps {
                    sh '''
                        echo "PATH=$PATH"
                        which docker
                        docker --version
                        docker ps
                    '''
                }
            }

        stage('Build & Test') {
            steps {
                sh '''
                    java -version
                    mvn -version
                    mvn clean verify -B
                '''
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    dockerImage = docker.build("${FULL_IMAGE}")
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-registry-credentials') {
                        dockerImage.push()
                        dockerImage.push('latest')
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-credentials', variable: 'KUBECONFIG')]) {
                    sh """
                        kubectl set image deployment/${K8S_DEPLOYMENT} \
                            ${K8S_DEPLOYMENT}=${FULL_IMAGE} \
                            -n ${K8S_NAMESPACE}

                        kubectl rollout status deployment/${K8S_DEPLOYMENT} \
                            -n ${K8S_NAMESPACE} --timeout=120s
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Deployment successful: ${FULL_IMAGE}"
        }
        failure {
            echo "Pipeline failed — check logs above."
        }
    }
}