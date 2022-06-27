package com.ubs.backend.util;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class JSPFunctions {
    /**
     * Generates HTTP Parameters for Loading the correct Page if you log in to the Admintool
     *
     * @param request the HTTP Request
     * @return a String containing the Parameters
     */
    public static String generateLoadPageParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("activeButtonID", request.getParameter("activeButtonID"));
        params.put("targetPage", request.getParameter("targetPage"));
        params.put("objectID", request.getParameter("objectID"));

        StringBuilder target = new StringBuilder("?");
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (param.getValue() != null)
                target.append(param.getKey()).append("=").append(param.getValue()).append("&");
        }

        return target.toString();
    }
}
