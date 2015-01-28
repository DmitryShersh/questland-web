package com.questland.servlet;

import com.questland.db.DBWorker;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "QuestUploadServlet", urlPatterns = {"/upload"})
public class UploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String authorId = null;
        // process only if its multipart content
        if (ServletFileUpload.isMultipartContent(request)){
            try {
                List<FileItem> multiparts = new ServletFileUpload(
                        new DiskFileItemFactory()
                ).parseRequest(request);

                for (FileItem item : multiparts) {
                    if (item.getFieldName().equals("authorId")) {
                        authorId = new String(item.get());
                        break;
                    }
                }

                // creating FILE_PATH
                ServletContext servletContext = getServletContext();
                String contextPath = servletContext.getRealPath(request.getContextPath());
                String filePath = contextPath + "/images/author";

                for (FileItem item : multiparts){
                    if (item.getFieldName().equals("load_img")) {
                        String uploadedFileName = item.getName();
                        String newName = "author" + authorId + uploadedFileName.substring(uploadedFileName.lastIndexOf('.'));
                        System.out.println(filePath + File.separator + newName);
                        item.write(new File(filePath + File.separator + newName));

                        DBWorker.updateUserAvatar(Integer.valueOf(authorId), newName);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        RequestDispatcher view = request.getRequestDispatcher("/Main.htm");
        view.forward(request, response);
    }
}
