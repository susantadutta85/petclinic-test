pipeline{
    agent any
    
    tools{
        maven "maven3"
    }
    /*
    triggers{
        cron("* * * * *")
    }
    */
    stages{
        stage("Checkout"){
            steps{
                echo "========executing checkout========"
                git url:"https://github.com/A-hash-bit/spring-petclinic.git", branch:"main"
            }
                       
        }
    
        stage("Build"){
            steps{
                sh "mvn package"
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
