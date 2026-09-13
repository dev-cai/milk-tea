package com.milktea.filter;

import com.milktea.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.milktea.mapper.UserMapper;
import com.milktea.entity.User;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 * @author MilkTea Team
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    
    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserMapper userMapper) {
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
    }
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) 
            throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token) && jwtUtils.validateToken(token) && !jwtUtils.isRefreshToken(token)) {
            try {
                String username = jwtUtils.getUsernameFromToken(token);
                String userType = jwtUtils.getUserTypeFromToken(token);
                Long userId = jwtUtils.getUserIdFromToken(token);
                User user = userMapper.selectById(userId);
                Integer tokenVersion = jwtUtils.getTokenVersionFromToken(token);
                if (user == null || user.getStatus() == null || user.getStatus() == 0
                        || (tokenVersion != null && !java.util.Objects.equals(tokenVersion, user.getTokenVersion() == null ? 0 : user.getTokenVersion()))) {
                    filterChain.doFilter(request, response);
                    return;
                }
                request.setAttribute("authenticatedUserId", userId);
                
                // 创建认证对象
                String role = "1".equals(userType) ? "ROLE_ADMIN" : "ROLE_USER";
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.error("JWT认证失败: {}", e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
