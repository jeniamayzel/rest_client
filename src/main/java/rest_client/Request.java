package rest_client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import sun.misc.BASE64Encoder;

public class Request {
    public static final String BASIC = "Basic ";
    public static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String X_AUTH_TOKEN = "X-Auth-Token";
    public Object body = "";
    public Map<String, String> uriParams = new HashMap<>();
    public Map<String, String> queryParams = new HashMap<>();
    public String url;
    public boolean print = false;
    public Class responseObjectClass = String.class;
    public HttpMethod method;
    public HttpHeaders requestHeaders = new HttpHeaders();


    public Request(){
        this.requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        this.requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    }

    public Request withHeader(String headerName, String headerValue){
        requestHeaders.set(headerName, headerValue);
        return this;
    }

    public Request withAccept(List<MediaType> mediaTypeList){
        requestHeaders.setAccept(mediaTypeList);
        return this;
    }

    /*
    if file the pyload should be as follows:
        payload = new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset()));
 */
    public Request withBody(Object payload){
        this.body = payload;
        return this;
    }

    public Request withUriParam(String key, String value){
        this.uriParams.put(key, value);
        return this;
    }

    public Request withQueryParam(String key, String value){
        this.queryParams.put(key, value);
        return this;
    }

    public Request withUrl(String url){
        this.url = url;
        return this;
    }

    public Request withPrint(Boolean print){
        this.print = print;
        return this;
    }

    public Request withResponseClass(Class responseObjectClass){
        this.responseObjectClass = responseObjectClass;
        return this;
    }

    public Request withMethod(HttpMethod method){
        this.method = method;
        return this;
    }

    public Request withXauthToken(String accessToken){
        requestHeaders.set(X_AUTH_TOKEN, accessToken);
        return this;
    }

    public Request withOAuthToken(String accessToken){
        requestHeaders.set(AUTHORIZATION_HEADER_KEY, BEARER_PREFIX + accessToken);
        return this;
    }

    public Request withBasicAuth(String username, String password){
        BASE64Encoder encoder = new BASE64Encoder();
        requestHeaders.set(AUTHORIZATION_HEADER_KEY, BASIC + new String(encoder
                .encodeBuffer(String.format("%s:%s", username, password).getBytes()))
                .replace("\n", "").replace("\r", ""));
        return this;
    }

}
