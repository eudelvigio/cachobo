package com.rf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.rf.data.dto.ErrorJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Based on the helpful answer at http://stackoverflow.com/q/25356781/56285,
 * with error details in response body added.
 *
 * @author Joni Karppinen
 * @since 20.2.2015
 */
@RestController
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(value = PATH)
    ErrorJson error(HttpServletRequest request, HttpServletResponse response) {
        // Appropriate HTTP response code (e.g. 404 or 500) is automatically set by Spring. 
        // Here we just define response body.
        return new ErrorJson(response.getStatus(), getErrorAttributes(request, true));
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }

}
