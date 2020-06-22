package com.sample.demoSample.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
