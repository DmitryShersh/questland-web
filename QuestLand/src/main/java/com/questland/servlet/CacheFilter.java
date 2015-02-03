package com.questland.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CacheFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("Cache-Control", "max-age=1800");

        chain.doFilter(request, response);
    }

    public void destroy() { }
    public void init(FilterConfig arg0) throws ServletException { }
}
