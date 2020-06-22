package com.sample.demoSample;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AppConfig implements EnvironmentAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    private Environment env;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public Environment getEnvironment() {
        return this.env;
    }

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }
}
