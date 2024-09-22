package com.company_name.ums.security;
import com.company_name.ums.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


@EnableWebSecurity
public class SecurityConfig{

    @Autowired
    UserTokenAuthFilter userTokenAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/v1/user/**").hasRole("USER")
                .antMatchers("/api/v1/login", "/api/v1/register").permitAll()
                .anyRequest().authenticated() // Protect all other endpoints
                .and()
//                .exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
//                    System.out.println(request.getHeader("token"));
//                    ObjectMapper mapper = new ObjectMapper();
//                    ErrorResponse errorResponse = new ErrorResponse(response.getStatus(),authException.getMessage());
//                    response.setHeader("content-type", "application/json");
//                    response.setStatus(response.getStatus());
//                    String responseMsg = mapper.writeValueAsString(errorResponse);
//                    response.getWriter().write(responseMsg);})
//                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(userTokenAuthFilter, BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }
}
