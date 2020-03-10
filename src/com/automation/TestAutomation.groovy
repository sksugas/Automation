package com.automation;
def TestResult, PassedPercentage, FailedPercentage, PassedTestCases, FailedTestCases, TotalCasesExecuted, TotalExecutionTime
def TestAutomationMethod(config,def Src_Path){
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

			writeFile file: 'output.txt', text: "\n-----------------------------------------------------------------\nStage - $STAGE_NAME on ${NODE_NAME} Results\n-----------------------------------------------------------------\nPassedPercentage ${PassedPercentage}%\nFailedPercentage ${FailedPercentage}%\nPassedTestCases ${PassedTestCases}\nFailedTestCases ${FailedTestCases}\nTotalCasesExecuted ${TotalCasesExecuted}\nTotalExecutionTime ${TotalExecutionTime}"
			bat """
				::copy output.txt $WORKSPACE\\finaloutput.txt
				type output.txt >> $WORKSPACE\\finaloutput.txt
				
				mkdir ${WORKSPACE}\\reports
				mkdir $WORKSPACE\\stagereports
				
				copy "${WORKSPACE}\\${Src_Path}\\jenkins\\TestExecution_Summary_Report.html" "$WORKSPACE\\stagereports\\${STAGE_NAME}_TestExecution_Summary_Report.html"
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
