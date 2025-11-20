package com.milktea.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * @author MilkTea Team
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.path:uploads/}")
    private String uploadPath;
    
    @Value("${file.upload.url-prefix:/uploads/}")
    private String urlPrefix;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问
        registry.addResourceHandler(urlPrefix + "**")
                .addResourceLocations("file:" + uploadPath);
        
        // 添加默认的静态资源处理
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
