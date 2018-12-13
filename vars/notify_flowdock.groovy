
def call(body){
	
	def config=[:]
	body.resolveStrategy=Closure.DELEGATE_FIRST
	body.delegate = config
	body()
	
	echo "inside method"
	
	def labels=new ArrayList<fields>();
	
	echo "inside labels"
	
	String[] label ="${config.Labels}".split('\\|')
	echo "after aleble split $label"
	
	for(String values : label){
		println "printing values ${values}"
		String[] array = "${values}".split(':',2)
		if(array.size() > 1){
			labels.add(new fields(array[0],array[1]))
			println "added"
		} else { 
				labels.add(new fields(array[0],""))
		}
	}
	
	def jsonData=new groovy.json.JsonOutput()
	
	println "size of lables ${labels}"
	
	println "after label prepare ${config.StatusColor} ${config.StatusValue}"
	
	def Status=new status("${config.StatusColor}","${config.StatusValue}")
	
	println "after label status"
	def Thread=new thread("${config.Thread_Body}",labels,"${config.Thread_ExternalUrl}",Status,"${config.Thread_Title}")
	println "after label thread"
	
	def author = new author("${config.Avatar}","${config.Author}")
	def flowjson1=new flowjson("${config.Body}",Thread,"${config.ExternalThreadId}","${config.FlowToken}","${config.Title}",author,"${config.event}")
	println "after label flowjson"
	String json = jsonData.toJson(flowjson1)
	println(json)
	
	println "json object created"
	
	
	}
