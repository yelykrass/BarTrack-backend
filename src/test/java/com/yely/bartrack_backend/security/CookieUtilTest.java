package com.yely.bartrack_backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import jakarta.servlet.http.Cookie;

import java.util.Optional;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class CookieUtilTest {

    @Test
    void getCookieValue_returnsEmptyWhenNoCookies() {

        MockHttpServletRequest req = new MockHttpServletRequest();

        Optional<String> opt = CookieUtil.getCookieValue(req, "missing");
        assertThat(opt.isPresent(), is(false));
    }

    @Test
    void getCookieValue_findsExistingCookie() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        Cookie c1 = new Cookie("a", "1");
        Cookie c2 = new Cookie("target", "value123");
        req.setCookies(c1, c2); // Використовуємо вбудований метод Spring Mock

        Optional<String> opt = CookieUtil.getCookieValue(req, "target");
        assertThat(opt.isPresent(), is(true));
        assertThat(opt.get(), is("value123"));
    }

    @Test
    void getCookieValue_returnsEmptyWhenCookieNotFound() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("a", "1"), new Cookie("b", "2"));

        Optional<String> opt = CookieUtil.getCookieValue(req, "missing");
        assertThat(opt.isPresent(), is(false));
    }

    @Test
    void buildCookie_setsPropertiesAndDefaults() {
        Cookie c = CookieUtil.buildCookie("name", "val", 3600, true, false, "Strict", null);
        assertThat(c.getName(), is("name"));
        assertThat(c.getValue(), is("val"));
        assertThat(c.getPath(), is("/")); // null path becomes "/"
        assertThat(c.getMaxAge(), is(3600));
        assertThat(c.getSecure(), is(true));
        assertThat(c.isHttpOnly(), is(false));
    }

    @Test
    void addCookie_constructsSetCookieHeader_withAllAttributes() {
        MockHttpServletResponse resp = new MockHttpServletResponse(); // Використовуємо Spring Mock
        Cookie cookie = CookieUtil.buildCookie("sid", "abc123", 7200, true, true, "Lax", "/app");
        CookieUtil.addCookie(resp, cookie, "Lax");

        // Spring MockHttpServletResponse має прямий метод отримання заголовків
        List<String> headers = resp.getHeaders("Set-Cookie");
        assertThat(headers, hasSize(1));
        String header = headers.get(0);

        assertThat(header, containsString("sid=abc123"));
        assertThat(header, containsString("Path=/app"));
        assertThat(header, containsString("Max-Age=7200"));
        assertThat(header, containsString("Secure"));
        assertThat(header, containsString("HttpOnly"));
        assertThat(header, containsString("SameSite=Lax"));
    }

    // Тест для покриття гілки: if (cookie.getMaxAge() >= 0)
    @Test
    void addCookie_omitsMaxAgeWhenNegative() {
        MockHttpServletResponse resp = new MockHttpServletResponse();
        Cookie cookie = CookieUtil.buildCookie("session", "xyz", -1, true, true, "Strict", "/");
        CookieUtil.addCookie(resp, cookie, "Strict");

        String header = resp.getHeader("Set-Cookie");
        assertThat(header, not(containsString("Max-Age")));
    }

    @Test
    void addCookie_omitsSameSiteWhenBlankOrNull() {
        MockHttpServletResponse resp1 = new MockHttpServletResponse();
        Cookie cookie1 = CookieUtil.buildCookie("k", "v", 10, false, false, null, null);
        CookieUtil.addCookie(resp1, cookie1, null);
        String header1 = resp1.getHeader("Set-Cookie");
        assertThat(header1, not(containsString("SameSite=")));

        MockHttpServletResponse resp2 = new MockHttpServletResponse();
        Cookie cookie2 = CookieUtil.buildCookie("k2", "v2", -1, false, false, null, null);
        CookieUtil.addCookie(resp2, cookie2, "   "); // blank should be treated as absent
        String header2 = resp2.getHeader("Set-Cookie");
        assertThat(header2, not(containsString("SameSite=")));
    }

    @Test
    void deleteCookie_buildsExpiredSecureHttpOnlyCookie() {
        MockHttpServletResponse resp = new MockHttpServletResponse();
        CookieUtil.deleteCookie(resp, "token", null, "Strict");
        List<String> headers = resp.getHeaders("Set-Cookie");
        assertThat(headers, hasSize(1));
        String h = headers.get(0);

        assertThat(h, containsString("token="));
        assertThat(h, containsString("Path=/"));
        assertThat(h, containsString("Max-Age=0"));
        assertThat(h, containsString("Secure"));
        assertThat(h, containsString("HttpOnly"));
        assertThat(h, containsString("SameSite=Strict"));
    }
}