package com.questland.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet(name = "QuestImage", urlPatterns = {"/image_sss"})
public class ImageServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
        ServletContext servletContext = getServletContext();
        String contextPath = servletContext.getRealPath(request.getContextPath());
        System.out.println("Context: " + contextPath);

        String requestURI = request.getRequestURI();
        System.out.println("URI: " + requestURI);
        if (requestURI.equals("/")) {
            requestURI = "/Main.htm";
        }

        if (requestURI.startsWith("/image")) {
            String filePath = contextPath + requestURI;
            String mimeType = servletContext.getMimeType(filePath); // getting image web parameters
            if (mimeType == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            response.setContentType(mimeType);
            File file = new File(filePath);
            response.setContentLength((int) file.length());

            FileInputStream in = new FileInputStream(file);
            OutputStream out = response.getOutputStream();

            // Copy the contents of the file to the output stream
            byte[] buf = new byte[1024];
            int count;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
            in.close();
        }
    }
}
