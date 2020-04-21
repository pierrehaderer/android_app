package com.twoplayer.legend.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/**")
	    .permitAll();

        http.httpBasic();
    }
    
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("user").password("{bcrypt}$2a$10$NjamupAIRbDdZWufnGSvT.qnxo9/Z0x1eyScAgY0xJRxx.r2yldVO").roles("user");
    }
	
	public static void main(String[] args) {
		System.out.println(new BCryptPasswordEncoder().encode(""));
	}

}
