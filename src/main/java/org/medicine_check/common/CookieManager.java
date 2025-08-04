package org.medicine_check.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CookieManager {

    public Cookie getCookieByName(
            HttpServletRequest request,
            String cookieName) {

        Cookie[] cookies = request.getCookies();
        Cookie cookie = null;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(cookieName)) {
                    cookie = c;
                }
            }
        }
        return cookie;
    }

    public List<String> getCookieList(Cookie cookie) {
        if (cookie == null) {
            return null;
        }
        // role:text, role:text, ...
        String cookieString = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
        List<String> cookieList = new ArrayList<>(Arrays.asList(cookieString.split(",")));
        return cookieList;
    }

}
