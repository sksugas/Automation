package com.automation;

def stageSkippedMethod(def stageType,config){
    script{
		if("${currentBuild.result}"=="FAILURE"){
			def stage = ["Smoke","Regression","Acceptance"]
			int i=0
			if("${stageType}"=="${stage[0]}"){
				writeFile file: 'stageskipped.txt', text: "\n-----------------------------------------------------------------\nStage - ${stage[1]} on ${NODE_NAME} - Skipped\n-----------------------------------------------------------------\n-----------------------------------------------------------------\nStage - ${stage[2]} on ${NODE_NAME} - Skipped\n-----------------------------------------------------------------\n"
				bat "type stageskipped.txt >> $WORKSPACE\\finaloutput.txt"
			}
			else if("${stageType}"=="${stage[1]}"){
				writeFile file: 'stageskipped.txt', text: "\n-----------------------------------------------------------------\nStage - ${stage[2]} on ${NODE_NAME} - Skipped\n-----------------------------------------------------------------\n"
				bat "type stageskipped.txt >> $WORKSPACE\\finaloutput.txt"
			}
		}
    }
}
