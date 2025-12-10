package com.Bisag.QuizApp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.Bisag.QuizApp.repository.CsuserRepo;
import com.Bisag.QuizApp.repository.PortallogRepo;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    

        @Autowired
        private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

        @Autowired
        private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(12);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/signup", "/login", "/admin/login", "/autoauth/{email}/{password}",
                                                                "/error", "/quizzes",
                                                                "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.ico")
                                                .permitAll()
                                                .requestMatchers("/dash", "/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/quiz/**").authenticated()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/")
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                .loginProcessingUrl("/login")
                                                 .successHandler(customAuthenticationSuccessHandler)
                                                 .failureHandler(customAuthenticationFailureHandler)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .clearAuthentication(true)
                                                .permitAll())
                                .exceptionHandling(ex -> ex
                                                .accessDeniedPage("/")
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        String requestURI = request.getRequestURI();
                                                        if (requestURI.contains("/admin") || requestURI.contains("/dash")) {
                                                                response.sendRedirect("/login");
                                                        } else {
                                                                response.sendRedirect("/");
                                                        }
                                                }))
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionFixation().newSession()
                                                .invalidSessionUrl("/")
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                                .maximumSessions(1)
                                                .maxSessionsPreventsLogin(true)
                                                .expiredUrl("/")
                                                .sessionRegistry(this.sessionRegistry()));

                return http.build();
        }

        @Bean
        AuthenticationManager authenticationManager(HttpSecurity http,
                        CustomUserDetailsService customUserDetailsService,
                        PasswordEncoder passwordEncoder) throws Exception {
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);

                authenticationManagerBuilder.userDetailsService(customUserDetailsService)
                                .passwordEncoder(passwordEncoder);

                return authenticationManagerBuilder.build();
        }

        @Bean
        public SessionRegistry sessionRegistry() {
                return new SessionRegistryImpl();
        }

}
