package ru.zhmyd.spring.config;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class GetTokenServiceImpl implements GetTokenService {
    private final UserDetailsService userDetailsService;

    @Override
    public String getToken(String username, String password) throws Exception {
        if (username == null || password == null)
            return null;
        User user = (User) userDetailsService.loadUserByUsername(username);
        Map<String, Object> tokenData = new HashMap<>();
        if (password.equals(user.getPassword())) {
            tokenData.put("clientType", "user");
            tokenData.put("username", user.getUsername());
            tokenData.put("authorities", user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(toList()));
            tokenData.put("token_create_date", new Date().getTime());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 100);
            tokenData.put("token_expiration_date", calendar.getTime());
            JwtBuilder jwtBuilder = Jwts.builder();
            jwtBuilder.setExpiration(calendar.getTime());
            jwtBuilder.setClaims(tokenData);
            String key = "abc123";
            String token = jwtBuilder.signWith(SignatureAlgorithm.HS512, key).compact();
            return token;
        } else {
            throw new Exception("Authentication error");
        }
    }
}
