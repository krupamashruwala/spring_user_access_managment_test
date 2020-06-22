package com.sample.demoSample.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.demoSample.persistence.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 86400000; // 1 day
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(req.getInputStream(), User.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            user.getPassword(),
                            new ArrayList<>())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        final String authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date expiry = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        String token = Jwts.builder().setId("sampleDemo").claim("auth", authorities)
                .setSubject(((User) auth.getPrincipal()).getEmail())
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
                .compact();

        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
        res.setHeader("Access-Control-Expose-Headers", "Authorization, Access-Control-Request-Headers, Access-Control-Allow-Headers");
        Map<String, Object> json = new HashMap<>();
        json.put("Authorization", TOKEN_PREFIX + token);
        json.put("tokenExpiry", expiry.getTime());

        List<String> userRoles = ((User) auth.getPrincipal()).getRoles().stream().map(r -> r.getName()).collect(Collectors.toList());
        json.put("roles", userRoles);
        json.put("userId", ((User) auth.getPrincipal()).getId());
        json.put("firstName", ((User) auth.getPrincipal()).getFirstName());
        json.put("lastName", ((User) auth.getPrincipal()).getLastName());

        LOGGER.info("JWTAuthenticationFilter - userObj.getEmail(): " + ((User) auth.getPrincipal()).getEmail());

        res.getWriter().println(new ObjectMapper().writeValueAsString(json));
        res.getWriter().flush();
        res.getWriter().close();
    }
}