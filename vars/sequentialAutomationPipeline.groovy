import jenkins.model.Jenkins;

def call(body) {

	def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

	def automation = new com.automation.TestAutomation()
	def postfailure = new com.automation.postFailure()
	def postsuccess = new com.automation.postSuccess()
	def finalemailnotification = new com.automation.finalemailNotification()
	
	pipeline {
		agent {label "${Agent}"}
		tools 
		{ 
    	    maven 'Jenkins-Maven' 
	        jdk 'Jenkins-Java' 
	    }
		stages{ 
			stage ('Smoke'){
				steps{
					script{
						automation.TestAutomationMethod(config,"${config.Smoke_src_path}")
					}
				}
				post{
					failure{
						script{ postfailure.postFailureMethod("${config.Smoke_src_path}","Smoke",config)}
						script {finalemailnotification.finalemailNotificationMethod(config) }
					}
					success{
						script{ postsuccess.postSuccessMethod("${config.Smoke_src_path}","Smoke",config)}
					}
				}
			}
			stage ('Acceptance'){
				steps{
					script{
						automation.TestAutomationMethod(config,"${config.Acceptance_src_path}")
					}
				}
				post{
					failure{
						script{ postfailure.postFailureMethod("${config.Acceptance_src_path}","Acceptance",config)}
						script {finalemailnotification.finalemailNotificationMethod(config) }
					}
					success{
						script{ postsuccess.postSuccessMethod("${config.Acceptance_src_path}","Acceptance",config)}
					}
				}
			}
			stage ('Regression'){
				steps{
					script{
						automation.TestAutomationMethod(config,"${config.Regression_src_path}")
					}
				}
				post{
					failure{
						script{ postfailure.postFailureMethod("${config.Regression_src_path}","Regression",config)}
						script {finalemailnotification.finalemailNotificationMethod(config) }
					}
					success{
						//script{ postsuccess.postSuccessMethod("${config.Regression_src_path}","Regression",config)}
						script {finalemailnotification.finalemailNotificationMethod(config) }
					}
				}
			}
		}
	}
}
