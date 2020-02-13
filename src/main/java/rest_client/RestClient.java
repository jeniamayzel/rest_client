package rest_client;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Map;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestClient {

    private RestTemplate restTemplate;
    private HttpEntity<String> requestEntity;
    private Gson gson = new Gson();

    private RestTemplate buildSecured() {
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        requestFactory.setHttpClient(httpClient);
        requestFactory.setConnectionRequestTimeout((int) Duration.ofMinutes(5).toMillis());
        requestFactory.setReadTimeout((int) Duration.ofMinutes(5).toMillis());
        requestFactory.setConnectTimeout((int) Duration.ofMinutes(5).toMillis());
        return new RestTemplate(SSLClientFactory.getClientHttpRequestFactory(SSLClientFactory.HttpClientType.OkHttpClient));
    }

    public RestClient(Boolean isSecured) throws Exception {
        if (!isSecured) {
            this.restTemplate = new RestTemplate();
        }else{
            this.restTemplate = buildSecured();
        }
    }

    public ResponseEntity send(Request request) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        objectMapper.registerModule(module);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        requestEntity = new HttpEntity<>(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request.body), request.requestHeaders);
        if (request.body instanceof String){
            requestEntity = new HttpEntity<>(request.body.toString(), request.requestHeaders);
        }
        ResponseEntity exchange = restTemplate.exchange(
                buildUri(request.url, request.uriParams, request.queryParams, request.print),
                request.method, requestEntity, request.responseObjectClass);
        return exchange;
    }

    public <T> ResponseEntity send(Request request, ParameterizedTypeReference<T> responseType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        objectMapper.registerModule(module);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        requestEntity = new HttpEntity<>(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request.body), request.requestHeaders);
        if (request.body instanceof String){
            requestEntity = new HttpEntity<>(request.body.toString(), request.requestHeaders);
        }
        ResponseEntity exchange = restTemplate.exchange(
                buildUri(request.url, request.uriParams, request.queryParams, request.print),
                request.method, requestEntity, responseType);
        return exchange;
    }

    public ResponseEntity get(Request request) throws IOException {
        return send(request.withMethod(HttpMethod.GET));
    }

    public ResponseEntity put(Request request) throws IOException {
        return send(request.withMethod(HttpMethod.PUT));
    }

    public ResponseEntity post(Request request) throws IOException {
        return send(request.withMethod(HttpMethod.POST));
    }

    public ResponseEntity delete(Request request) throws IOException {
        return send(request.withMethod(HttpMethod.DELETE));
    }

    /*
    URI creation according the input parameters given
     */
    private URI buildUri(String url, Map<String, String> uriParams , Map<String, String> queryParams,boolean isPrint) throws IllegalArgumentException{
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        URI uri;
        if (queryParams != null)
            for (String key : queryParams.keySet()){
                builder.queryParam(key,queryParams.get(key));}
        if (uriParams != null)
            uri = builder.buildAndExpand(uriParams).toUri();
        else
            uri = builder.build(false).toUri();
        if (isPrint)
            System.out.println(uri);
        return uri;
    }

}