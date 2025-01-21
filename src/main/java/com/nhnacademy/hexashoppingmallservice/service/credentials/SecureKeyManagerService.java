package com.nhnacademy.hexashoppingmallservice.service.credentials;

import com.nhnacademy.hexashoppingmallservice.dto.credentials.KeyResponseDto;
import com.nhnacademy.hexashoppingmallservice.exception.credentials.KeyManagerException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SecureKeyManagerService {

    @Value("${url}")
    private String url;

    @Value("${appkey}")
    private String appKey;

    @Value("${keyStoreFilePath}")
    private String keyStoreFilePath;

    @Value("${password}")
    private String password;

    public String fetchSecretFromKeyManager(String keyId) {
        try {
            // 키 저장소 객체를 만들되 키 유형이 PKCS12인 인스턴스를 가져오기
            KeyStore clientStore = KeyStore.getInstance("PKCS12");

            // 클래스패스에서 키스토어 파일을 InputStream으로 로드
            InputStream keyStoreInputStream = getClass().getClassLoader().getResourceAsStream(keyStoreFilePath);
            if (keyStoreInputStream == null) {
                throw new KeyManagerException("Hexa_certificate.p12 not found in classpath at " + keyStoreFilePath);
            }

            // Java KeyStore (JKS) 객체에 키 저장소 파일이랑 비밀번호 입력
            clientStore.load(keyStoreInputStream, password.toCharArray());

            // SSL TLS 연결을 설정하는 과정
            // 1. 프로토콜은 TLS
            // 2. clientStore 안의 private 키 값을 가져오기 위해서 clientStore 와 password 로 private key 값 로드
            // 3. 인증 기관은 자체 서명된 인증서로 로드 할 수 있게 만듬
            SSLContext sslContext = SSLContexts.custom()
                    .setProtocol("TLS")
                    .loadKeyMaterial(clientStore,
                            password.toCharArray()) // 키를 로드하기 위해 p12 파일과 비밀번호로 비밀키 꺼내서 sslcontext에 넣음
                    .loadTrustMaterial(new TrustSelfSignedStrategy()) // 자체 서명된 인증서를 신뢰하도록 TrustManager 설정
                    .build();

            SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslContext)
                    .build();

            HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();

            // CloseableHttpClient (httpclient + 자원 관리) 객체를 만들고 HttpClients.custom()로 httpclient 객체 커스텀
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .evictExpiredConnections()
                    .build();

            // HttpComponentsClientHttpRequestFactory는 Spring에서 HTTP 요청을 처리할 때 사용하는 클래스
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);

            RestTemplate restTemplate = new RestTemplate(requestFactory);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            // URL과 경로 지정해서 URI 생성
            URI uri = UriComponentsBuilder
                    .fromUriString(url)
                    .path("/keymanager/v1.0/appkey/{appkey}/secrets/{keyid}")
                    .encode()
                    .build()
                    .expand(appKey, keyId)
                    .toUri();

            ResponseEntity<KeyResponseDto> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    KeyResponseDto.class
            );

            if (response.getBody() != null && response.getBody().getBody() != null) {
                return response.getBody().getBody().getSecret();
            } else {
                throw new KeyManagerException("Invalid response from Key Manager");
            }

        } catch (KeyStoreException | IOException | CertificateException
                 | NoSuchAlgorithmException | UnrecoverableKeyException
                 | KeyManagementException e) {
            throw new KeyManagerException("Error while fetching secret: " + e.getMessage());
        }
    }
}
