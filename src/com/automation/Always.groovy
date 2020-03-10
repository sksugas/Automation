
package com.automation;


def alwaysMethod(def Src_Path,def TestType,config){
        def emailnotification = new com.automation.emailNotification()
        emailnotification.emailNotificationMethod("${Src_Path}","${TestType}",config)
}
