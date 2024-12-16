package com.nhnacademy.hexashoppingmallservice.credentials;

public class DatabaseCredentials {

    private String url;
    private String username;
    private String password;


    public DatabaseCredentials(String info) {
        String [] sources = info.split("\n");
        this.url = sources[0];
        this.username = sources[1];
        this.password = sources[2];
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


}
