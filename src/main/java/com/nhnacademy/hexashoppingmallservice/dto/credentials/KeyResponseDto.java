package com.nhnacademy.hexashoppingmallservice.dto.credentials;

public class KeyResponseDto {

    // KeyResponseDto에 각각 헤더랑 바디 등록



    private Header header;
    private Body body;

    public Body getBody() {
        return body;
    }

    public Header getHeader() {
        return header;
    }

    public static class Body {

        private String secret;

        public String getSecret() {
            return secret;
        }
    }

    public static class Header {

        // 헤더에는 응답 코드 , 응답 메서드 , 성공적인지 확인

        private Integer resultCode;
        private String resultMessage;
        private boolean isSuccessful;

        public Integer getResultCode() {
            return resultCode;
        }

        public String getResultMessage() {
            return resultMessage;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }
    }
}
