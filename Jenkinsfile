pipeline {
    agent any

    stages {
        stage('ECR Upload') {
            steps{
                dir('./backend') {
                    sh 'aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin ${ECR_REPOSITORY_URI}'
                    sh 'docker build -t ${DOCKER_IMAGE_NAME} .'
                    sh 'docker tag ${DOCKER_IMAGE_NAME}:latest ${ECR_REPOSITORY_URI}/${DOCKER_IMAGE_NAME}:latest'
                    sh 'docker push ${ECR_REPOSITORY_URI}/${DOCKER_IMAGE_NAME}:latest'
                }
            }
            post {
                success {
                    echo "The ECR Upload stage successfully."
                }
                failure {
                    echo "The ECR Upload stage failed."
                }
            }
        }
    }
}