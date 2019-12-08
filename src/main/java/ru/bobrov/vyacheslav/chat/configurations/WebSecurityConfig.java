package ru.bobrov.vyacheslav.chat.configurations;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.bobrov.vyacheslav.chat.controllers.filters.JwtRequestFilter;
import ru.bobrov.vyacheslav.chat.services.authentication.JwtAuthenticationEntryPoint;
import ru.bobrov.vyacheslav.chat.services.authentication.JwtUserDetailsService;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@FieldDefaults(level = PRIVATE)
@NonNull
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    static final String[] PUBLIC = {
            "/api/v1/authentication/registration",
            "/api/v1/authentication",
            "/webjars/**",
            "/swagger*/**",
            "/h2-console/**",
            "/v2/api-docs",
            "/chat-messaging/**"
    };
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    JwtUserDetailsService jwtUserDetailsService;
    JwtRequestFilter jwtRequestFilter;
    PasswordEncoder passwordEncoder;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .headers().frameOptions().sameOrigin().and()
                .csrf().disable()
                .authorizeRequests().antMatchers(PUBLIC).permitAll()
                .anyRequest().authenticated().and().

                exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint).and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
