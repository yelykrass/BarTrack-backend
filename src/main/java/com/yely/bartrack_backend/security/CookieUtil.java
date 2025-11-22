package com.yely.bartrack_backend.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

/**
 * Utility for creating / reading / deleting HttpOnly cookies.
 * Note: setting SameSite via Cookie API is not standardized in older servlet
 * API,
 * so we set the header manually when needed.
 */
public final class CookieUtil {

    private CookieUtil() {
    }

    public static Optional<String> getCookieValue(HttpServletRequest req, String name) {
        if (req.getCookies() == null)
            return Optional.empty();
        return Arrays.stream(req.getCookies())
                .filter(c -> c.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst();
    }

    public static Cookie buildCookie(String name, String value, int maxAgeSeconds, boolean secure, boolean httpOnly,
            String sameSite, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath(path == null ? "/" : path);
        cookie.setMaxAge(maxAgeSeconds);
        // SameSite is set via header when adding to response to support older servlet
        // containers.
        return cookie;
    }

    public static void addCookie(HttpServletResponse resp, Cookie cookie, String sameSite) {
        // Some servlet containers don't expose SameSite via Cookie API, so we construct
        // header.
        StringBuilder sb = new StringBuilder();
        sb.append(cookie.getName()).append("=").append(cookie.getValue())
                .append("; Path=").append(cookie.getPath() == null ? "/" : cookie.getPath());

        if (cookie.getMaxAge() >= 0)
            sb.append("; Max-Age=").append(cookie.getMaxAge());
        if (cookie.getSecure())
            sb.append("; Secure");
        if (cookie.isHttpOnly())
            sb.append("; HttpOnly");
        if (sameSite != null && !sameSite.isBlank())
            sb.append("; SameSite=").append(sameSite);

        resp.addHeader("Set-Cookie", sb.toString());
    }

    public static void deleteCookie(HttpServletResponse resp, String name, String path, String sameSite) {
        Cookie c = new Cookie(name, "");
        c.setPath(path == null ? "/" : path);
        c.setMaxAge(0);
        c.setHttpOnly(true);
        c.setSecure(true);
        addCookie(resp, c, sameSite);
    }
}
