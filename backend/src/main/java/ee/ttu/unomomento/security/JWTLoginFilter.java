package ee.ttu.unomomento.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.ttu.unomomento.db.tables.pojos.Account;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    public JWTLoginFilter(String url, AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException, IOException {
        Account account = new ObjectMapper()
            .readValue(req.getInputStream(), Account.class);
        return getAuthenticationManager().authenticate(
            new UsernamePasswordAuthenticationToken(
                account.getUsername(),
                account.getPassword(),
                Collections.emptyList()
            )
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException {
        TokenAuthenticationService
            .addAuthentication(res, auth.getName());
    }
}