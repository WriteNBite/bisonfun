package com.bisonfun.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    static final String LOGIN_FORM_URL = "/login";
    static final String TARGET_AFTER_SUCCESSFUL_LOGIN_PARAM = "target";

    private final CookieSecurityContextRepository cookieSecurityContextRepository;
    private final LoginWithTargetUrlAuthenticationEntryPoint loginWithTargetUrlAuthenticationEntryPoint;
    private final RedirectToOriginalUrlAuthenticationSuccessHandler redirectToOriginalUrlAuthenticationSuccessHandler;
    private final DatabaseAuthenticationProvider authenticationProvider;

    protected SecurityConfiguration(CookieSecurityContextRepository cookieSecurityContextRepository,
                                    LoginWithTargetUrlAuthenticationEntryPoint loginWithTargetUrlAuthenticationEntryPoint,
                                    RedirectToOriginalUrlAuthenticationSuccessHandler redirectToOriginalUrlAuthenticationSuccessHandler,
                                    DatabaseAuthenticationProvider databaseAuthenticationProvider){
        super();
        this.cookieSecurityContextRepository = cookieSecurityContextRepository;
        this.loginWithTargetUrlAuthenticationEntryPoint = loginWithTargetUrlAuthenticationEntryPoint;
        this.redirectToOriginalUrlAuthenticationSuccessHandler = redirectToOriginalUrlAuthenticationSuccessHandler;
        this.authenticationProvider = databaseAuthenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth){
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{

        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable()

                .securityContext().securityContextRepository(cookieSecurityContextRepository)
                .and().logout().permitAll().deleteCookies(SignedUserDetailsCookie.NAME)

                .and().requestCache().disable()
                .exceptionHandling().authenticationEntryPoint(loginWithTargetUrlAuthenticationEntryPoint)

                .and().formLogin()
                .loginPage(LOGIN_FORM_URL).permitAll()
                .successHandler(redirectToOriginalUrlAuthenticationSuccessHandler)

                .and().authorizeRequests()
                .antMatchers(LOGIN_FORM_URL).permitAll()
                .antMatchers("/cabinet").authenticated()
                .antMatchers("/wtw").authenticated()
                .anyRequest().permitAll();

    }
}
