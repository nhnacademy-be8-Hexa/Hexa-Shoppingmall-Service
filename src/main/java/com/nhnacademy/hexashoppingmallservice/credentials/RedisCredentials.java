package com.nhnacademy.hexashoppingmallservice.credentials;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class RedisCredentials {
    private Map<String, String> credentialsMap = new HashMap<>();

    // 생성자: info는 JSON 형식의 문자열
    public RedisCredentials(String info) {
        parseJsonToMap(info);
    }

    // info(JSON 형식의 문자열)을 Map으로 변환하는 함수
    private void parseJsonToMap(String info) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(info); // info를 JSON으로 변환

            // JSON의 모든 필드와 그 값을 Map에 저장
            jsonNode.fields().forEachRemaining(field -> {
                credentialsMap.put(field.getKey(), field.getValue().asText());
            });
        } catch (Exception e) {
            e.printStackTrace(); // 예외 처리 (파일 읽기 오류나 잘못된 JSON 등)
        }
    }

    // credentialsMap 반환 함수
    public Map<String, String> getCredentialsMap() {
        return credentialsMap;
    }
}
