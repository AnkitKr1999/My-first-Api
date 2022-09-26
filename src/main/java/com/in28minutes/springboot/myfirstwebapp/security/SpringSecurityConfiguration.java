package com.in28minutes.springboot.myfirstwebapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.function.Function;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SpringSecurityConfiguration {

    @Bean
    public InMemoryUserDetailsManager createUserDetailsManager()
    {
        UserDetails userDetails1 = createNewUser("Ankit", "dummy");
        UserDetails userDetails2 = createNewUser("Kumar", "jimmy");
        return  new InMemoryUserDetailsManager(userDetails1,userDetails2);
    }

    private UserDetails createNewUser(String username, String password) {
        Function<String, String> passwordEncoder=input-> passwordEncoder().encode(input);
        UserDetails userDetails = User.builder().passwordEncoder(passwordEncoder)
                                                .username(username)
                                                .password(password)
                                                .roles("USER", "ADMIN")
                                                .build();
        return userDetails;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//        all url request is to be authenticated
        http.authorizeHttpRequests(
                auth -> auth.anyRequest().authenticated());
//        if unauthorized request show login form collect username and password
        http.formLogin(withDefaults());  // static import is needed to be done
//        disable CSRF
        http.csrf().disable();
//        enable frames in our application
        http.headers().frameOptions().disable();

        return http.build();
    }
}
