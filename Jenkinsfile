pipeline {

    agent any
    environment {
        VERSION = "${env.BUILD_ID}"
    }

    stages{
        stage('Sonar Quality Check') {
            agent{

                docker {
                    image 'maven'
                }

            }
            steps{
                script{
                    withSonarQubeEnv(credentialsId: 'sonarqube') {
                        sh 'cd 3.00-starting-project && mvn clean package sonar:sonar'
    }
                }
            }
        }



        stage('Quality Gate Status') {
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'sonarqube'
                }
            }
        }

        stage('Docker Build & Docker push to Nexus repo') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'nexus-password', variable: 'nexus_creds')]) {
                    sh '''
                    cd 3.00-starting-project 

                    docker build -t 34.123.150.92:8087/springapp:${VERSION} .
                    
                    docker login -u admin -p $nexus_creds 34.123.150.92:8087

                    docker push 34.123.150.92:8087/springapp:${VERSION} 

                    docker rmi 34.123.150.92:8087/springapp:${VERSION}

                    '''
                    }
                }
            }
        }
        stage('Identifying misconfigs using datree in helm charts'){
            steps {
                script {
                    dir('3.00-starting-project/kubernetes/') {
                        sh 'helm datree test myapp/'
                    }
                }
            }
        }


    }
    post {
        always {
            slackSend color: "#00FFFF", message: "Project : ${env.JOB_NAME} | Build Number : ${env.BUILD_NUMBER}" 
        }
        success {
            slackSend color: "good", message: "Everything is working great ===>  Project : ${env.JOB_NAME} | Build Number : ${env.BUILD_NUMBER}" 
        }
        failure {
            slackSend color: "danger", message: "The build has failed ===>  Project : ${env.JOB_NAME} | Build Number : ${env.BUILD_NUMBER}" 
        }
    }
}
