package com.sample.demoSample.security.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.demoSample.persistence.model.Role;
import com.sample.demoSample.persistence.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component("loginSuccessHandler")
public class LoginCustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json");
        Map<String, Object> payload = new HashMap<>();
        payload.put("role", ((Role) (((User) authentication.getPrincipal()).getRoles().toArray())[0]).getName());
        String json = new ObjectMapper().writeValueAsString(payload);
        response.getWriter().append(json);
        response.setStatus(200);
        clearAuthenticationAttributes(request);
    }
}
