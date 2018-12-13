public class flowjson{
	String body;
	def thread;
	String external_thread_id;
	String flow_token;
	String title;
	def author;
	String event;
	
	public flowjson(threadbody,thread,external_thread_id,flow_token,title,author,event){
		this.body=threadbody;
		this.thread=thread;
		this.external_thread_id=external_thread_id;
		this.flow_token=flow_token;
		this.title=title;
		this.author=author;
		this.event=event;
	}
}
