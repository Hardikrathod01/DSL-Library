package org.foo

import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

class FlowDockUtil implements Serializable{
    def script

    FlowDockUtil(script) {
        println "initialization BuildStage"
        this.script = script
    }

    def flowDockMessage(){
        def postUrl = "" //update the url of restapi here
        URL url1 = new URL(postUrl);
        HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
        connection1.setRequestMethod("POST");
        connection1.setDoOutput(true);
        connection1.setRequestProperty("Content-Type", "application/json");
        def jsonToBePosted="" //update the json to be passed here
        OutputStreamWriter osw = null;
        osw = new OutputStreamWriter(connection1.getOutputStream());
        osw.write(jsonToBePosted);
        osw.flush();
        osw.close();
    }
}