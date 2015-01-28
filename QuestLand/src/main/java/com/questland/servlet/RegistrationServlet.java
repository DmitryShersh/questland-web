package com.questland.servlet;


import com.questland.db.DBWorker;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "QuestRegistrationServlet", urlPatterns = {"/register"})
public class RegistrationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String type = request.getParameter("type");
        if (type == null || !type.equals("reg")) {
            System.out.println("Wrong type");
            return;
        }

        String login = request.getParameter("name");
        String pass = request.getParameter("passwd");
        String email = request.getParameter("email");

        JSONObject resp = new JSONObject();
        boolean added = DBWorker.addUser(login, pass, email);
        System.out.println(added);
        resp.put("ok", added);

        ServletOutputStream out = response.getOutputStream();
        out.write(resp.toString().getBytes("UTF-8"));
        response.flushBuffer();
    }
}
