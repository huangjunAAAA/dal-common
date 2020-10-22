package com.boring.dal.cache.sanitizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class UrlEncodeSanitizer implements KeySanitizer {

    private static final Logger logger = LogManager.getLogger(UrlEncodeSanitizer.class);

    @Override
    public String sanitize(Object s) {
        if (s == null)
            return "NULL";
        try {
            return URLEncoder.encode(s.toString(), "utf8");
        } catch (UnsupportedEncodingException e) {
            logger.warn(e, e);
        }
        return s.toString();
    }
}
