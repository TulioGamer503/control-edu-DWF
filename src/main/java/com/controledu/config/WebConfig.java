package com.controledu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Handlers para recursos estáticos
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        // Swagger UI resources
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirecciones simples
        registry.addViewController("/").setViewName("redirect:/auth/login");
        registry.addViewController("/auth/login").setViewName("auth/login");
        registry.addViewController("/dashboard").setViewName("dashboard");
        registry.addViewController("/auth/access-denied").setViewName("auth/access-denied");

        // Swagger UI
        registry.addViewController("/swagger").setViewName("redirect:/swagger-ui/index.html");
        registry.addViewController("/api-docs").setViewName("redirect:/v3/api-docs");
    }
}