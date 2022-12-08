package com.bisonfun.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
public class CookieSecurityContextRepository implements SecurityContextRepository {
    private static final String EMPTY_CREDENTIALS = "";
    private static final String ANONYMOUS_USER = "anonymousUser";

    private final String cookieHmacKey;

    public CookieSecurityContextRepository(@Value("${auth.cookie.hmac-key}") String cookieHmacKey) {
        this.cookieHmacKey = cookieHmacKey;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        HttpServletResponse response = requestResponseHolder.getResponse();
        requestResponseHolder.setResponse(new SaveToCookieResponseWrapper(request, response));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        readUserDetailsFromCookie(request).ifPresent(userDetails ->
                context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, EMPTY_CREDENTIALS, userDetails.getAuthorities())));

        return context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        SaveToCookieResponseWrapper responseWrapper = (SaveToCookieResponseWrapper) response;
        if(!responseWrapper.isContextSaved()){
            responseWrapper.saveContext(context);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return readUserDetailsFromCookie(request).isPresent();
    }

    private Optional<CustomUserDetails> readUserDetailsFromCookie(HttpServletRequest request){
        return readCookieFromRequest(request).map(this::createUserDetails);
    }

    private Optional<Cookie> readCookieFromRequest(HttpServletRequest request){
        if(request.getCookies() == null){
            return Optional.empty();
        }

        Optional<Cookie> maybeCookie = Stream.of(request.getCookies())
                .filter(cookie -> SignedUserDetailsCookie.NAME.equals(cookie.getName())).findFirst();

        if(maybeCookie.isEmpty()){
            log.debug("No {} cookie in request", SignedUserDetailsCookie.NAME);
        }

        return maybeCookie;
    }

    private CustomUserDetails createUserDetails(Cookie cookie){
        log.debug("Cookie: {}", cookie.getValue());
        CustomUserDetails userDetails =  new SignedUserDetailsCookie(cookie, cookieHmacKey).getUserDetails();
        log.debug("UserDetails: {}", userDetails);
        return userDetails;
    }

    private class SaveToCookieResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper{
        private final HttpServletRequest request;

        SaveToCookieResponseWrapper(HttpServletRequest request, HttpServletResponse response){
            super(response, true);
            this.request = request;
        }

        @Override
        protected void saveContext(SecurityContext context) {
            HttpServletResponse response = (HttpServletResponse) getResponse();
            Authentication authentication = context.getAuthentication();
            if(authentication == null){
                log.debug("No securityContext.authentication, skip saveContext");
                return;
            }

            if(ANONYMOUS_USER.equals(authentication.getPrincipal())){
                log.debug("Anonymous User SecurityContext, skip saveContext");
                return;
            }

            if(!(authentication.getPrincipal() instanceof CustomUserDetails)){
                log.warn("securityContext.authentication.principal of unexpected type {}, skip saveContext", authentication.getPrincipal());
                return;
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            SignedUserDetailsCookie cookie = new SignedUserDetailsCookie(userDetails, cookieHmacKey);
            cookie.setSecure(request.isSecure());
            response.addCookie(cookie);
            log.debug("SecurityContext for principal '{}' saved in Cookie", userDetails.getUsername());

        }
    }
}
