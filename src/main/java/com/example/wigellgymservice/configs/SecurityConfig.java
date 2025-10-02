package com.example.wigellgymservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;

@Controller
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf->csrf.disable())
                .httpBasic(Customizer.withDefaults())
                ;

        return http.build();

    }

    @Bean
    public UserDetailsService userDetailsService() {

        ///admin
        UserDetails admin = User
                .withUsername("simon")
                .password("{noop}simon")
                .roles("ADMIN")
                .build();

        ///user
        UserDetails user1 = User
                .withUsername("sara")
                .password("{noop}sara")
                .roles("USER")
                .build();

        UserDetails user2 = User
                .withUsername("amanda")
                .password("{noop}amanda")
                .roles("USER")
                .build();

        UserDetails user3 = User
                .withUsername("alex")
                .password("{noop}alex")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user1, user2, user3);
    }
}
