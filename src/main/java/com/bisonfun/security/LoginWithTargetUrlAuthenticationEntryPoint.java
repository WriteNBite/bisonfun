package com.bisonfun.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginWithTargetUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    public LoginWithTargetUrlAuthenticationEntryPoint() {
        super(SecurityConfiguration.LOGIN_FORM_URL);
    }

    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        return UriComponentsBuilder.fromUriString(super.determineUrlToUseForThisRequest(request, response, exception))
                .queryParam(SecurityConfiguration.TARGET_AFTER_SUCCESSFUL_LOGIN_PARAM, request.getRequestURI())
                .toUriString();
    }
}
