package com.milktea.config;

import com.milktea.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

/**
 * Spring Security 配置类 
 * @author MilkTea Team
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（API项目通常禁用）
            .csrf(AbstractHttpConfigurer::disable)
            // 无状态会话
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置访问权限
            .authorizeHttpRequests(auth -> auth
                // 公开接口
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/product/page", "/product/categories", "/product/recommend", "/product/hot").permitAll()
                .requestMatchers(new RegexRequestMatcher("^/product/[0-9]+$", "GET")).permitAll()
                .requestMatchers(HttpMethod.GET, "/banner/**", "/activity/**", "/marketing/banners", "/marketing/activities", "/marketing/activity/*").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // 添加JWT过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
