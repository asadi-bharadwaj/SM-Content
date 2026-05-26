package com.sm.content.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletRequest;

/**
 * **Summary:** Configuration class for Spring Security.
 * 
 * **Flow:** This class sets up the security filter chain, defines authorization rules for different endpoints, 
 * and configures pre-authenticated authentication filters for header-based authentication. It specifically 
 * handles an internal API secret check for the purge endpoint and extracts user IDs from request headers.
 * 
 * **Features:** Implements stateless security, custom authorization for internal purge operations, 
 * and header-based user identification.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${internal.api.secret:super-secret-internal-key}")
    private String internalSecret;

    /**
     * **Summary:** Configures the SecurityFilterChain.
     * 
     * **Flow:** 
     * 1. Disables CSRF, form login, and HTTP basic authentication to support a stateless API.
     * 2. Sets up authorization rules:
     *    - Requests to "/posts/purge/**" require a valid internal secret in the "X-Internal-Secret" header.
     *    - All other requests must be authenticated.
     * 3. Adds a custom `RequestHeaderAuthenticationFilter` before the standard `RequestHeaderAuthenticationFilter` to handle user ID headers.
     * 
     * **Features:** API security lockdown, internal secret validation, stateless session management.
     * 
     * @param http The HttpSecurity object to configure.
     * @return The built SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/posts/purge/**").access((authentication, context) -> {
                    HttpServletRequest request = context.getRequest();
                    String secret = request.getHeader("X-Internal-Secret");
                    return new org.springframework.security.authorization.AuthorizationDecision(internalSecret.equals(secret));
                })
                .anyRequest().authenticated()
            )
            .addFilterBefore(requestHeaderAuthenticationFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * **Summary:** Creates a filter for header-based authentication.
     * 
     * **Flow:** 
     * 1. Instantiates a `RequestHeaderAuthenticationFilter`.
     * 2. Sets "X-Authenticated-User-Id" as the header to look for the user ID.
     * 3. Configures it to not throw an exception if the header is missing (allowing other auth mechanisms or custom handling).
     * 4. Assigns a `ProviderManager` with a `PreAuthenticatedAuthenticationProvider`.
     * 
     * **Features:** Header-based authentication, user ID extraction.
     * 
     * @return The configured RequestHeaderAuthenticationFilter.
     */
    @Bean
    public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter() {
        RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
        filter.setPrincipalRequestHeader("X-Authenticated-User-Id");
        filter.setExceptionIfHeaderMissing(false);
        filter.setAuthenticationManager(new ProviderManager(preAuthenticatedAuthenticationProvider()));
        return filter;
    }

    /**
     * **Summary:** Configures the authentication provider for pre-authenticated tokens.
     * 
     * **Flow:** 
     * 1. Creates a `PreAuthenticatedAuthenticationProvider`.
     * 2. Sets the custom `userDetailsService` to load user details based on the pre-authenticated principal.
     * 
     * **Features:** Support for pre-authenticated security context.
     * 
     * @return The configured PreAuthenticatedAuthenticationProvider.
     */
    @Bean
    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(userDetailsService());
        return provider;
    }

    /**
     * **Summary:** Defines a service to load user details from pre-authenticated tokens.
     * 
     * **Flow:** 
     * 1. Receives a `PreAuthenticatedAuthenticationToken`.
     * 2. Extracts the principal (the user ID from the header).
     * 3. Returns a Spring Security `User` object with the user ID as the username and "ROLE_USER" authority.
     * 
     * **Features:** User detail mapping from request headers.
     * 
     * @return An AuthenticationUserDetailsService implementation.
     */
    @Bean
    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> userDetailsService() {
        return token -> new User(
                (String) token.getPrincipal(),
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}