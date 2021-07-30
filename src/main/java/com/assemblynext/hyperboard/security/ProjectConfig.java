package com.assemblynext.hyperboard.security;

import com.assemblynext.hyperboard.services.AuthenticationProviderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class ProjectConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationProviderService authenticationProvider;

    @Bean
        public BCryptPasswordEncoder BCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/{\\d+}").permitAll()
                .antMatchers("/tag/**").permitAll()
                .antMatchers("/entries/**").permitAll()
                .antMatchers("/report/**").permitAll()
                .antMatchers("/mod/home").hasAuthority("MOD")
                .antMatchers("/mod/unban").hasAuthority("MOD")
                .antMatchers("/mod/archive").hasAuthority("MOD")
                .antMatchers("/mod/admin").hasAuthority("ADMIN")
                .antMatchers("/mod/admin/*").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/auth")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/auth", true)
                .permitAll()
                .and()
            .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**");
        web.ignoring().antMatchers("/images/**");
        web.ignoring().antMatchers("/uploads/**");
        web.ignoring().antMatchers("/thumbs/**");
    }
}
