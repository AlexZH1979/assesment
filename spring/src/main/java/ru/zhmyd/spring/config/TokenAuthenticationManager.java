package ru.zhmyd.spring.config;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;

@AllArgsConstructor
@Service
public class TokenAuthenticationManager implements AuthenticationManager {
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof TokenAuthentication) {
            TokenAuthentication readyTokenAuthentication = processAuthentication((TokenAuthentication) authentication);
            return readyTokenAuthentication;
        } else {
            authentication.setAuthenticated(false);
            return authentication;
        }
    }

    private TokenAuthentication processAuthentication(TokenAuthentication authentication) throws AuthenticationException {
        String token = authentication.getToken();
        String key = "key123";
        DefaultClaims claims;
        try {
            claims = (DefaultClaims) Jwts.parser().setSigningKey(key).parse(token).getBody();
        } catch (Exception ex) {
            throw new AuthenticationServiceException("Token corrupted");
        }
        if (claims.get("TOKEN_EXPIRATION_DATE", Long.class) == null)
            throw new AuthenticationServiceException("Invalid token");
        Date expiredDate = new Date(claims.get("TOKEN_EXPIRATION_DATE", Long.class));
        if (expiredDate.after(new Date()))
            return buildFullTokenAuthentication(authentication, claims);
        else
            throw new AuthenticationServiceException("Token expired date error");
    }

    private TokenAuthentication buildFullTokenAuthentication(TokenAuthentication authentication, DefaultClaims claims) {
        User user = (User) userDetailsService.loadUserByUsername(claims.get("USERNAME", String.class));
        if (user.isEnabled()) {
            Collection<GrantedAuthority> authorities = user.getAuthorities();
            TokenAuthentication fullTokenAuthentication =
                    new TokenAuthentication(authentication.getToken(), authorities, true, user);
            return fullTokenAuthentication;
        } else {
            throw new AuthenticationServiceException("User disabled");
        }
    }
}
