package com.rynrama.simakerjabackend.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

public final class CookieUtil {

    private CookieUtil() {}

    public static Optional<Cookie> getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) return Optional.empty();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return Optional.of(cookie);
            }
        }
        return Optional.empty();
    }

    /**
     * Adds a cookie to the response using Spring's ResponseCookie API,
     * which properly supports SameSite and all modern cookie attributes.
     *
     * @param response   the HTTP response
     * @param name       cookie name
     * @param value      cookie value
     * @param maxAge     max age in seconds (-1 for session cookie)
     * @param httpOnly   whether JS can access the cookie
     * @param secure     whether only sent over HTTPS
     * @param path       URL path scope for the cookie
     */
    public static void addCookie(
            HttpServletResponse response,
            String name,
            String value,
            int maxAge,
            boolean httpOnly,
            boolean secure,
            String path
    ) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .maxAge(Duration.ofSeconds(maxAge))
                .path(path)
                .httpOnly(httpOnly)
                .secure(secure)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Deletes a cookie by setting its value to empty and max-age to 0.
     */
    public static void deleteCookie(
            HttpServletResponse response,
            String name,
            String path
    ) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .maxAge(0)
                .path(path)
                .httpOnly(true)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(
                SerializationUtils.serialize(object)
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(String base64, Class<T> clazz) {
        return clazz.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(base64)
                )
        );
    }
}
