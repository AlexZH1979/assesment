package ru.zhmyd.spring.config;

public interface GetTokenService {
    String getToken(String username, String password) throws Exception;
}
