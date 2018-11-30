package org.foo

class BuildStage implements Serializable{
    
	def jobname=""
    
    def script

    BuildStage(script) {
        println "initialization BuildStage"
        this.script = script
    }
    
    def buildStageExecution(){
         this.script.build job: "job-a"
    }
    
    def deployStageExecution(){
        this.script.build job: "job-b"
    }
	
	
	def getjob1Name(){
		jobname="job-a"
		return jobname
	}
	
	def getjob2Name(){
		jobname="job-b"
		return jobname
	}

}