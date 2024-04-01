package com.bisonfun.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{

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

    @Bean
    public SecurityFilterChain securityChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable()

                .securityContext().securityContextRepository(cookieSecurityContextRepository)
                .and().logout().permitAll().deleteCookies(SignedUserDetailsCookie.NAME)

                .and().requestCache().disable()
                .exceptionHandling().authenticationEntryPoint(loginWithTargetUrlAuthenticationEntryPoint)

                .and().formLogin(loginForm -> loginForm
                        .loginPage(LOGIN_FORM_URL).permitAll()
                        .successHandler(redirectToOriginalUrlAuthenticationSuccessHandler)
                )
                .authorizeHttpRequests(request -> request
                        .requestMatchers(LOGIN_FORM_URL).permitAll()
                        .requestMatchers("/cabinet").authenticated()
                        .requestMatchers("/wtw").authenticated()
                        .anyRequest().permitAll()
                )
                .authenticationProvider(authenticationProvider);

        return http.build();
    }
}
