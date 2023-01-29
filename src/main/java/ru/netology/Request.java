package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.List;

public class Request {

    private final String requestLine;
    private final String method;
    private final String resourcePath;
    private final String query;
    private final List<NameValuePair> queryParams;


    public Request(String requestLine) {
        this.requestLine = requestLine;

        String[] requestLineParts = requestLine.split(" ");
        this.method = requestLineParts[0];

        int indexSplit = requestLineParts[1].indexOf('?');

        this.resourcePath = requestLineParts[1].substring(0, indexSplit);
        // Format: ?**********
        this.query = requestLineParts[1].substring(indexSplit+1, requestLineParts[1].length());

        this.queryParams = URLEncodedUtils.parse(URI.create(requestLineParts[1]), "UTF-8");

    }

    public List<NameValuePair> getQueryParams(){
        return queryParams;
    }

    public String getQueryParam(String name){
        return queryParams.stream()
                .filter(p -> p.getName().equals(name))
                .map(p -> p.getValue())
                .findFirst().get();
    }

    public String getResourcePath(){
        return resourcePath;
    }



    public String getQuery() {
        return query;
    }
}
