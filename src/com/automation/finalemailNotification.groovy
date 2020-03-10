package com.automation;

def finalemailNotificationMethod(config){
    script{
		echo "Final email notification for all the stages - ${currentBuild.result}"
		def displayoutput=readFile "$WORKSPACE\\finaloutput.txt"
		emailext attachmentsPattern: 'reports\\*.html', 
		subject:"$STAGE_NAME ${currentBuild.result}: Final Results of GSAP Automation Test ${currentBuild.fullDisplayName}",
		body: "Final email notification for all the stages : ${currentBuild.result}\nGiven PassingCriteria: ${params.PassingCriteria}%\nOverall details:\n${displayoutput}\n${env.BUILD_URL} ",
		to: "${config.EmailIDs}"
	}
}
