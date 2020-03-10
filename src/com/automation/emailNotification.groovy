package com.automation;

def emailNotificationMethod(def Src_Path,def TestType,config){
	script{
		def stageskipped = new com.automation.stageSkipped()
		stageskipped.stageSkippedMethod("${TestType}",config)
		echo "Test notifications for stage $STAGE_NAME result - ${currentBuild.result}"
		TestResult = readFile "${Src_Path}\\${config.TestResult}"
		//def displayoutput=readFile "$WORKSPACE\\finaloutput.txt"
		def displayoutput=readFile "${Src_Path}\\output.txt"
		//emailext attachmentsPattern: '${Src_Path}\\jenkins\\*.html', 
		emailext attachmentsPattern: 'stagereports\\*.html',
		subject: "${STAGE_NAME} ${currentBuild.result}: GSAP Automation Test Results: ${currentBuild.fullDisplayName}",
		body: "${TestType} Stage - Test notifications for stage $STAGE_NAME result: ${currentBuild.result}\nGiven PassingCriteria: ${params.PassingCriteria}%\nTest Passed Percentage is:${TestResult}%\nOverall details:\n${displayoutput}\n${env.BUILD_URL} ",
		to: "${config.EmailIDs}"

		bat "rmdir ${WORKSPACE}\\stagereports /s /q"
	}
}
