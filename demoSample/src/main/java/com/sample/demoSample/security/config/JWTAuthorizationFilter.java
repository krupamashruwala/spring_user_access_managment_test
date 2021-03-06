package com.sample.demoSample.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.sample.demoSample.security.config.JWTAuthenticationFilter.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        try {
            String token = request.getHeader(HEADER_STRING);
            if (token != null) {
                // parse the token.
                final JwtParser jwtParser = Jwts.parser()
                        .requireId("sampleDemo")
                        .setSigningKey(SECRET.getBytes());

                final Jws claimsJws = jwtParser.parseClaimsJws(token.replace(TOKEN_PREFIX, ""));

                final Claims claims = (Claims) claimsJws.getBody();

                final Collection authorities =
                        Arrays.stream(claims.get("auth").toString().split(","))
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                String user = claims.getSubject();

                if (user != null) {
                    return new UsernamePasswordAuthenticationToken(user, null, authorities);
                }
                return null;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}