package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public List<String> requestMessage = new ArrayList<>();
    public String method, URI, httpVersion;

    public HttpRequest(InputStream in) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        requestMessage.add(line);
        while(!"".equals(line)){
            line = br.readLine();
            requestMessage.add(line);
            if(line == null) return;
        }
        // body
        if(requestMessage.get(3) != null){
            int contentLength = getContentLength();
            String requestBody = IOUtils.readData(br, contentLength);
            requestMessage.add(requestBody);
        }
        String requestLine = requestMessage.get(0);
        String[] tokens = requestLine.split(" ");
        method = tokens[0];
        URI = tokens[1];
        httpVersion = tokens[2];
    }

    public Map<String, String> getRequestParameter() {
        // get
        log.debug("method : {}", method);
        if(method.equals("GET")){
            String uri = getQueryString(URI);
            log.debug("uri : {}", uri);
            return HttpRequestUtils.parseQueryString(uri);
        }
        // post
        String requestBody = requestMessage.get(requestMessage.size()-1);
        log.debug("requestBody : {}", requestBody);
        return HttpRequestUtils.parseQueryString(requestBody);
    }

    public String getQueryString(String requestLine){
        return requestLine.split("\\?")[1];
    }

    public int getContentLength(){
        String line = requestMessage.get(3);
        String[] tokens = line.split(" ");
        return Integer.parseInt(tokens[1]);
    }

}
