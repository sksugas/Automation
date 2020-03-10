package com.automation;

def postFailureMethod(def Src_Path,def TestType,config){
	def emailnotification = new com.automation.emailNotification()
	emailnotification.emailNotificationMethod("${Src_Path}","${TestType}",config)
}
