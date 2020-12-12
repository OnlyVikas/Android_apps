package com.example.orderfoods.Model;

public class Token {

    private String token;
private  boolean isServerToken;

        public Token() {
        }

        public Token(String token, boolean isServerToken) {
        this.isServerToken = isServerToken;
        this.token=token;
        }

public String getToken() {
        return token;
        }

public void setToken(String token) {
        this.token = token;
        }

public boolean isServerToken() {
        return isServerToken;
        }

public void setServerToken(boolean serverToken) {
        isServerToken = serverToken;
        }
}
