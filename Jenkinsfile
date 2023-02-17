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

                    docker build -t 34.123.150.92:8083/springapp:${VERSION} .
                    
                    docker login -u admin -p $nexus_creds 34.123.150.92:8083

                    docker push 34.123.150.92:8083/springapp:${VERSION} 

                    docker rmi 34.123.150.92:8083/springapp:${VERSION}

                    '''
                    }
                    
                }
            }
        }
    }
}
