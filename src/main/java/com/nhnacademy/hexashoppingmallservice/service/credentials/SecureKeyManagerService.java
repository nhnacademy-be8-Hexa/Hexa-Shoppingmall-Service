package com.nhnacademy.hexashoppingmallservice.service.credentials;

import com.nhnacademy.hexashoppingmallservice.dto.credentials.KeyResponseDto;
import com.nhnacademy.hexashoppingmallservice.exception.credentials.KeyManagerException;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class SecureKeyManagerService {

    @Value("${url}")
    private String url;

    @Value("${appkey}")
    private String appKey;

    @Value("${keyid}")
    private String keyId;

    @Value("${keyStoreFilePath}")
    private String keyStoreFilePath;

    @Value("${password}")
    private String password;

    public String fetchSecretFromKeyManager() {
        try {


            // 키 저장소 객체를 만들되 키 유형이 PKCS12인 인스턴스를 가져오기
            KeyStore clientStore = KeyStore.getInstance("PKCS12");

            // keyStoreFilePath 의 파일을 가져와 FileInputStream 형태로 가져오기
            FileInputStream keyStoreInputStream = new FileInputStream(Objects.requireNonNull(getClass().getClassLoader().getResource(keyStoreFilePath)).getFile());


            // Java KeyStore (JKS) 객체에 키 저장소 파일이랑 비밀번호 입력
            clientStore.load(keyStoreInputStream, password.toCharArray());

            // SSL TLS 연결을 설정하는 과정
            // 1. 프로토콜은 TLS
            // 2. clientStore 안의 private 키 값을 가져오기 위해서 clientStore 와 password 로 private key 값 로드
            // 3. 인증 기관은 자체 서명된 인증서로 로드 할 수 있게 만듬

            SSLContext sslContext = SSLContexts.custom()
                    .setProtocol("TLS")
                    .loadKeyMaterial(clientStore, password.toCharArray()) // 키를 로드하기위해서 p12 파일과 비밀번호로 비밀키 꺼내서 sslcontext 에 넣음
                    .loadTrustMaterial(new TrustSelfSignedStrategy()) // SSL/TLS 연결을 설정할 때 자체 서명된 인증서를 허용하도록 TrustManager를 구성하는 데 사용
                    .build();


            SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslContext)
                    .build();

            HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();



            // CloseableHttpClient ( httpclient + 자원 관리 ) 객체를 만들고  HttpClients.custom()로 httpclient 객체 커스텀 앞서 만든 sslcontext 등록 해서 build
            CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).evictExpiredConnections().build();






            // HttpComponentsClientHttpRequestFactory는 Spring에서 HTTP 요청을 처리할 때 사용하는 클래스

            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

            requestFactory.setHttpClient(httpClient);


            RestTemplate restTemplate = new RestTemplate(requestFactory);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));


            // url 과 경로 지정해서 url 생성
            URI uri = UriComponentsBuilder
                    .fromUriString(url)
                    .path("/keymanager/v1.0/appkey/{appkey}/secrets/{keyid}")
                    .encode()
                    .build()
                    .expand(appKey, keyId)
                    .toUri();

            ResponseEntity<KeyResponseDto> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), KeyResponseDto.class);
            return ((KeyResponseDto)response.getBody()).getBody().getSecret();



        } catch (KeyStoreException | IOException | CertificateException
                 | NoSuchAlgorithmException | UnrecoverableKeyException
                 | KeyManagementException e) {
            throw new KeyManagerException("Error while fetching secret: " + e.getMessage());
        }
    }
}
