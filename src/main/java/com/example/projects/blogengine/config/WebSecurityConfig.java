package com.example.projects.blogengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/").permitAll()
            .antMatchers(HttpMethod.GET, "/css/**", "/js/**", "/fonts/**", "/img/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/init").permitAll()
            .antMatchers(HttpMethod.GET, "/api/settings").permitAll()
            .antMatchers(HttpMethod.GET, "/api/post").permitAll()
            .antMatchers(HttpMethod.GET, "/api/post/byDate").permitAll()
            .antMatchers(HttpMethod.GET, "/api/post/byTag").permitAll()
            .antMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .antMatchers(HttpMethod.GET, "/api/auth/captcha").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .antMatchers(HttpMethod.GET, "/api/auth/check").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/restore").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/password").permitAll()
                .antMatchers(HttpMethod.GET, "/login/change-password/**").permitAll()
                //.antMatchers(HttpMethod.GET, "/api/post/**").permitAll()
                .regexMatchers(HttpMethod.GET, "\\/api\\/post\\/\\d+$").permitAll()
            .anyRequest().authenticated()
            .and()
            .csrf().disable()
            .formLogin().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
