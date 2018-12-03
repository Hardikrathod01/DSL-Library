package org.foo

class BuildStage implements Serializable{
    
	def jobname=""
    
    def script

    BuildStage(script) {
        println "initialization BuildStage"
        this.script = script
    }
    
    def scmStageExecution(){
         this.script.build job: this.getScmJobName()
    }
    
    def buildStageExecution(){
        this.script.build job: this.getBuildJobName()
    }
	
	def deployStageExecution(){
		this.script.build job: this.getDeployJobName()
	}
	
	
	def getScmJobName(){
		jobname="SCM-checkout"
		return jobname
	}
	
	def getBuildJobName(){
		jobname="Build"
		return jobname
	}
	def getDeployJobName(){
		jobname="Deploy"
		return jobname
	}

}
