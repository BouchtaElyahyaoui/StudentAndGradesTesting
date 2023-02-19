pipeline {

    agent any
    environment {
        VERSION = "${env.BUILD_ID}"
        PROJECT_ID = 'protean-bit-376817'
        CLUSTER_NAME = 'cluster-1'
        LOCATION = 'us-central1-c'
        CREDENTIALS_ID = 'protean-bit-376817'
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
                    withEnv(['DATREE_TOKEN=fc6dac4f-90b1-472c-b58c-5bbb207b0e7b']) {
                    dir('3.00-starting-project/kubernetes/') {
                        sh 'helm datree test myapp/'
                    }
                    }
                }
            }
        }


        stage('Pushing the helm charts to nexus') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'nexus-password', variable: 'nexus_creds')]) {
                    dir('3.00-starting-project/kubernetes/') {    
                    sh '''
                    helmversion=$( helm show chart myapp | grep version | cut -d: -f 2 | tr -d ' ')
                    tar -czvf myapp-${helmversion}.tgz myapp/
                    curl -u admin:$nexus_creds http://34.123.150.92:8081/repository/helm-hosted/ --upload-file myapp-${helmversion}.tgz -v
                    '''
                    }
                    }
                }
            }
        }
        stage('Deploy to K8s') {
            steps{
                echo "Deployment started ..."
            dir('3.00-starting-project/kubernetes/') {    
                sh 'ls -ltr'
                sh 'pwd'
                sh 'helm upgrade --install --set image.repository="34.123.150.92:8087/springapp" --set image.tag="${VERSION}" myjavaapp myapp/ --kubeconfig /home/bouchta/kconfig'
                step([$class: 'KubernetesEngineBuilder', \
                  projectId: env.PROJECT_ID, \
                  clusterName: env.CLUSTER_NAME, \
                  location: env.LOCATION, \
                  manifestPattern: 'deployment.yaml', \
                  credentialsId: env.CREDENTIALS_ID, \
                  verifyDeployments: true])
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
