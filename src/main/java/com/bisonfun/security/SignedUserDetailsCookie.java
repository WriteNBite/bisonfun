package com.bisonfun.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignedUserDetailsCookie extends Cookie {

    public static final String NAME = "UserDetails";
    private static final String PATH = "/";
    private static final Pattern UID_PATTERN = Pattern.compile("username=([A-Za-z0-9]*)");
    private static final Pattern HMAC_PATTERN = Pattern.compile("hmac=([A-Za-z0-9+/=]*)");
    private static final String HMAC_SHA_512 = "HmacSHA512";

    private final Payload payload;
    private final String hmac;

    public SignedUserDetailsCookie(CustomUserDetails userDetails, String cookieHmacKey) {
        super(NAME, "");
        this.payload = new Payload(userDetails.getUsername());
        this.hmac = calculateHmac(this.payload, cookieHmacKey);
        this.setPath(PATH);
        this.setMaxAge((int) Duration.of(365, ChronoUnit.DAYS).toSeconds());
        this.setHttpOnly(true);
    }

    public SignedUserDetailsCookie(Cookie cookie, String cookieHmacKey){
        super(NAME, "");

        if(!NAME.equals(cookie.getName())){
            throw new IllegalArgumentException("No "+NAME+" Cookie");
        }

        this.hmac = parse(cookie.getValue(), HMAC_PATTERN).orElse(null);
        if(hmac == null){
            throw new CookieVerificationFailedException("Cookie not signed (no HMAC)");
        }

        String username = parse(cookie.getValue(), UID_PATTERN).orElseThrow(() -> new IllegalArgumentException(NAME + " Cookie contains no UID"));
        this.payload = new Payload(username);

        if(!hmac.equals(calculateHmac(payload, cookieHmacKey)))
        {
            throw new CookieVerificationFailedException("Cookie signature (HMAC) invalid");
        }

        this.setPath(cookie.getPath());
        this.setMaxAge(cookie.getMaxAge());
        this.setHttpOnly(cookie.isHttpOnly());
    }

    private Optional<String> parse(String value, Pattern pattern){
        Matcher matcher = pattern.matcher(value);
        if(!matcher.find()){
            return Optional.empty();
        }
        if(matcher.groupCount() < 1){
            return Optional.empty();
        }

        String match = matcher.group(1);
        if(match == null || match.trim().isEmpty()){
            return Optional.empty();
        }

        return Optional.of(match);
    }

    private String calculateHmac(Payload payload, String secretKey){
        byte[] secretKeyBytes = Objects.requireNonNull(secretKey).getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = Objects.requireNonNull(payload).toString().getBytes(StandardCharsets.UTF_8);

        try{
            Mac mac = Mac.getInstance(HMAC_SHA_512);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, HMAC_SHA_512);
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(valueBytes);
            return Base64.getEncoder().encodeToString(hmacBytes);
        }catch (NoSuchAlgorithmException | InvalidKeyException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getValue() {
        return payload.toString()+"&hmac="+hmac;
    }

    public CustomUserDetails getUserDetails(){
        return new CustomUserDetails(payload.username);
    }

    private static class Payload{
        private final String username;

        private Payload(String username){
            this.username = username;
        }

        @Override
        public String toString() {
            return "username=" + username;
        }
    }
}
