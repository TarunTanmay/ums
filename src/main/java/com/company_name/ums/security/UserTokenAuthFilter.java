package com.company_name.ums.security;

import com.company_name.ums.exception.BadEntryException;
import com.company_name.ums.exception.ErrorResponse;
import com.company_name.ums.exception.UnauthorizedException;
import com.company_name.ums.model.User;
import com.company_name.ums.model.UserLogin;
import com.company_name.ums.repository.UserLoginRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
        String authHeader = request.getRequestURI();
        if (authHeader != null && (authHeader.startsWith("/api/v1/login") || authHeader.startsWith("/api/v1/register"))) {
            filterChain.doFilter(request, response);
            return;
        }


        String accessToken = request.getHeader("token");
        if (validateToken(!userLoginRepository.findByToken(accessToken).isPresent(), HttpStatus.BAD_REQUEST, "Invalid token", response))
            return;

        UserLogin token = userLoginRepository.findByToken(accessToken).orElseThrow(() ->
                new BadEntryException("User not found")
        );
        if (validateToken(token.isExpired(), HttpStatus.UNAUTHORIZED, "Token expired", response)) return;

        User user = token.getUser();
        if (user != null) {
            try {
                if (null == SecurityContextHolder.getContext().getAuthentication()) {
                    if (!user.isDeleted()) {
                        Collection<GrantedAuthority> authorities = Arrays.asList(
                                new SimpleGrantedAuthority("ROLE_USER"),
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        );
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        user.getId(), null, authorities);
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext()
                                .setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            } catch (IllegalArgumentException e) {
                logger.error("Unable to fetch JWT Token");
                throw new BadEntryException("Unable to fetch JWT Token");
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token is expired");
                throw new BadEntryException("JWT Token is expired");
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new BadEntryException(e.getMessage());
            }
        } else {
            logger.warn("JWT Token missing");
            throw new BadEntryException("user Token missing");
        }
        filterChain.doFilter(request, response);
    }

    private boolean validateToken(boolean userLoginRepository, HttpStatus badRequest, String Invalid_token, HttpServletResponse response) throws IOException {
        if (userLoginRepository) {
            ObjectMapper mapper = new ObjectMapper();
            ErrorResponse errorResponse = new ErrorResponse(badRequest.value(), Invalid_token);
            response.setHeader("content-type", "application/json");
            response.setStatus(badRequest.value());
            String responseMsg = mapper.writeValueAsString(errorResponse);
            response.getWriter().write(responseMsg);
            return true;
        }
        return false;
    }
}
