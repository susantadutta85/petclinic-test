pipeline {
    agent {
        label 'agent1'
    }
    triggers {
        pollSCM('H/5 * * * *') // Watches the `dev` branch
    }
    environment {
        DOCKERHUB_CREDENTIALS = credentials('nalexx6_docker_pass')
        DOCKERHUB_USERNAME = "nalexx6"
        NEXUS_SNAPHOT_URL = 'http://localhost:8081/repository/maven-snapshots'
        ARTIFACT_PATH = "target"

    }
    stages {
        stage('Checkout Code') {
            steps {
                git url: 'https://github.com/Nalexx06/spring-petclinic.git', branch: 'dev'

            }
        }
        stage('Build & Test') {
            steps {
                sh './mvnw clean verify'
            }
        }
        stage('Upload to Nexus') {
            steps {
                script {
                    def ARTIFACT_VERSION = sh(script: "./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                    sh """./mvnw deploy:deploy-file -DgroupId=org -DartifactId=petclinic -Dversion=${ARTIFACT_VERSION} -Dpackaging=jar -Dfile=./target/petclinic-${ARTIFACT_VERSION}.jar -DrepositoryId=maven-snapshots -Durl=${NEXUS_SNAPHOT_URL} --settings settings.xml"""
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    def ARTIFACT_VERSION = sh(script: "./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                    sh "DOCKER_BUILDKIT=0 docker build --build-arg ARTIFACT_VERSION=${ARTIFACT_VERSION} --build-arg ARTIFACT_PATH=${ARTIFACT_PATH} -t petclinic:${ARTIFACT_VERSION} ."
                    sh "docker tag petclinic:${ARTIFACT_VERSION} ${DOCKERHUB_USERNAME}/petclinic:${ARTIFACT_VERSION}"
                    sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_CREDENTIALS}"
                    sh "docker push ${DOCKERHUB_USERNAME}/petclinic:${ARTIFACT_VERSION}"
                }
            }
        }
    }
}
