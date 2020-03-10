import jenkins.model.Jenkins;

def call(body) {

	def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

	def TestResult, PassedPercentage, FailedPercentage, PassedTestCases, FailedTestCases, TotalCasesExecuted, TotalExecutionTime
	pipeline {
		agent {label "${config.Agents[0]}"}
		tools 
		{ 
    	    maven 'Jenkins-Maven' 
	        jdk 'Jenkins-Java' 
	    }
		stages{ 
			stage ("Smoke"){
				parallel {
					stage('Smoke-1'){
						agent {label "${config.Agents[0]}"}
						steps{
							script{
								TestAutomation(config,"${config.Smoke_src_path}")
							}
						}
						post{
							failure{
								postFailure("${config.Smoke_src_path}","Smoke",config)
							}
							success{
								postSuccess("${config.Smoke_src_path}","Smoke",config)
							}
						}
					}
					stage('Smoke-2'){
						agent {label "${config.Agents[1]}"}
						steps{
							script{
								TestAutomation(config,"${config.Smoke_src_path}")
							}
						}
						post{
							failure{
								postFailure("${config.Smoke_src_path}","Smoke",config)
							}
							success{
								postSuccess("${config.Smoke_src_path}","Smoke",config)
							}
						}
					}
				}
			}
			stage ("Regression"){
				parallel {
					stage('Regression-1'){
						agent {label "${config.Agents[0]}"}
						steps{
							script{
								TestAutomation(config,"${config.Regression_src_path}")
							}
						}
						post{
							failure{
								postFailure("${config.Regression_src_path}","Regression",config)
							}
							success{
								postSuccess("${config.Regression_src_path}","Regression",config)
							}
						}
					}
					stage('Regression-2'){
						agent {label "${config.Agents[1]}"}
						steps{
							script{
								TestAutomation(config,"${config.Regression_src_path}")
							}
						}
						post{
							failure{
								postFailure("${config.Regression_src_path}","Regression",config)
							}
							success{
								postSuccess("${config.Regression_src_path}","Regression",config)
							}
						}
					}
				}
			}
			/*stage ("Acceptance"){
				parallel {
					stage('Acceptance-1'){
						steps{
							script{
								TestAutomation(config,"${config.Acceptance_src_path}")
							}
						}
						post{
							failure{
								script{
									echo "Acceptance Test stage FAILURE"	
								}
							}
							success{
								script{
									echo "Acceptance Stage SUCCESS- Test notifications for result: ${currentBuild.result}"
								}
							}
						}
					}
					stage('Acceptance-2'){
						agent {label 'GSAP_AUTOMATION_QA_G1NWQG2UA002'}
						steps{
							script{
								TestAutomation(config,"${config.Smoke_src_path}")
							}
						}
						post{
							failure{
								script{
									echo "Acceptance Test stage FAILURE"	
								}
							}
							success{
								script{
									echo "Acceptance Stage SUCCESS - Test notifications for result: ${currentBuild.result}"
								}
							}
						}
					}
				}
			}*/
		}
	}
}

def TestAutomation(config,def Src_Path){
	dir("${WORKSPACE}\\${Src_Path}"){
		script{	
			bat " mvn clean test "
			
			TestResult = readFile "${config.TestResult}"
			echo "Test Passed percentage result = ${TestResult}%"
			
			PassedPercentage = readFile "${config.PassedPercentage}"
			FailedPercentage = readFile "${config.FailedPercentage}"
			PassedTestCases = readFile "${config.PassedTestCases}"
			FailedTestCases = readFile "${config.FailedTestCases}"
			TotalCasesExecuted = readFile "${config.TotalCasesExecuted}"
			TotalExecutionTime = readFile "${config.TotalExecutionTime}"

			writeFile file: 'output.txt', text: "\n-------------------------------------------\nStage - $STAGE_NAME on ${NODE_NAME} Results\n-------------------------------------------\nPassedPercentage ${PassedPercentage}%\nFailedPercentage ${FailedPercentage}%\nPassedTestCases ${PassedTestCases}\nFailedTestCases ${FailedTestCases}\nTotalCasesExecuted ${TotalCasesExecuted}\nTotalExecutionTime ${TotalExecutionTime}"
			bat """
				::copy output.txt $WORKSPACE\\finaloutput.txt
				type output.txt >> $WORKSPACE\\finaloutput.txt
				IF EXIST ${WORKSPACE}\\reports (echo "reports directory exists") ELSE ( mkdir ${WORKSPACE}\\reports /q)
				copy "${WORKSPACE}\\${Src_Path}\\jenkins\\TestExecution_Summary_Report.html" "$WORKSPACE\\reports\\${STAGE_NAME}_TestExecution_Summary_Report.html"
				IF $TestResult GEQ $params.PassingCriteria (echo "Test Result = $TestResult is greater than Passing Criteria ${params.PassingCriteria} value") ELSE ( exit 1)
			"""

			publishHTML (target: [
				keepAll: true,
            	reportDir: "$WORKSPACE\\reports",
            	reportFiles: "${STAGE_NAME}_TestExecution_Summary_Report.html",
            	reportName: "${STAGE_NAME}_Results"
            ])

		}
	}
}

def postFailure(def Src_Path,def TestType,config){
	emailNotification("${Src_Path}","${TestType}",config)
}

def postSuccess(def Src_Path,def TestType,config){
	emailNotification("${Src_Path}","${TestType}",config)
}

def emailNotification(def Src_Path,def TestType,config){
	script{
		
		echo "Test notifications for stage $STAGE_NAME result - ${currentBuild.result}"
		TestResult = readFile "${Src_Path}\\${config.TestResult}"
		def displayoutput=readFile "$WORKSPACE\\finaloutput.txt"
		//def displayoutput=readFile "${Src_Path}\\output.txt"
		emailext attachmentsPattern: 'reports\\*.html', 
		subject: "${TestType}: ${STAGE_NAME} ${currentBuild.result}: GSAP Automation Test Results: ${currentBuild.fullDisplayName}",
		body: "${TestType} Stage - Test notifications for stage $STAGE_NAME result: ${currentBuild.result}\nGiven PassingCriteria: ${params.PassingCriteria}%\nTest Passed Percentage is:${TestResult}%\nOverall details:\n${displayoutput}\n${env.BUILD_URL} ",
		to: "${config.EmailIDs}"
	}
}
