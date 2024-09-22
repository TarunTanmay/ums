package com.company_name.ums.security;

import com.company_name.ums.exception.BadEntryException;
import com.company_name.ums.exception.ErrorResponse;
import com.company_name.ums.model.User;
import com.company_name.ums.model.UserLogin;
import com.company_name.ums.repository.UserLoginRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@Component
public class UserTokenAuthFilter extends OncePerRequestFilter {

    @Autowired
    UserLoginRepository userLoginRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/login") || path.startsWith("/api/v1/signup")) {
            filterChain.doFilter(request, response);
           return;
        }

        String token = extractTokenFromRequest(request);
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        try {
            UserLogin userLogin = userLoginRepository.findByToken(token).orElseThrow(() -> new BadEntryException("Invalid token"));
            if (!userLogin.isExpired() && !userLogin.isDeleted()) {
                User user = userLogin.getUser();
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user.getId(), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                ObjectMapper mapper = new ObjectMapper();
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "unauthorized");
                response.setHeader("content-type", "application/json");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                String responseMsg = mapper.writeValueAsString(errorResponse);
                response.getWriter().write(responseMsg);
                return;
            }

        } catch (Exception e) {
            ObjectMapper mapper = new ObjectMapper();
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Invalid Token Status");
            response.setHeader("content-type", "application/json");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            String responseMsg = mapper.writeValueAsString(errorResponse);
            response.getWriter().write(responseMsg);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("token");
    }
}
