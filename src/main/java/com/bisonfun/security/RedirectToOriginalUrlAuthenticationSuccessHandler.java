package com.bisonfun.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class RedirectToOriginalUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final String DEFAULT_TARGET_URL = "/cabinet";

    public RedirectToOriginalUrlAuthenticationSuccessHandler(){
        super(DEFAULT_TARGET_URL);
        this.setTargetUrlParameter(SecurityConfiguration.TARGET_AFTER_SUCCESSFUL_LOGIN_PARAM);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String targetUrl = super.determineTargetUrl(request, response, authentication);
        if(UrlUtils.isAbsoluteUrl(targetUrl)){
            log.warn("Absolute target URL {} identified and suppressed", targetUrl);
            return DEFAULT_TARGET_URL;
        }
        return targetUrl;
    }
}
