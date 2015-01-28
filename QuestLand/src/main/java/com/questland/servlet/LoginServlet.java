package com.questland.servlet;

import com.questland.db.DBWorker;
import com.questland.pojo.Author;
import com.questland.pojo.Quest;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "QuestLoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String type = request.getParameter("type");
        if (type == null) {
            System.out.println("null login type");
            return;
        }

        switch (type) {
            case "id" :
            case "author" : {
                int authorID = Integer.parseInt(request.getParameter("value"));
                Author author = DBWorker.getAuthorByID(authorID);

                if (author == null) {
                    System.out.println("null author :(");
                    return;
                }

                response.getOutputStream().write(author.toJSON().toString().getBytes("UTF-8"));
                break;
            }
            case "login" : {
                Author author = DBWorker.validate(request.getParameter("login"), request.getParameter("passwd"));
                if (author == null) {
                    System.out.println("null author " + request.getParameter("login") + " " + request.getParameter("passwd"));
                    RequestDispatcher view = getServletContext().getRequestDispatcher("/Main.htm");
                    view.forward(request, response);
                } else {
                    System.out.println("login success!!!");
                    Cookie cookie = new Cookie("qLandLogin", String.valueOf(author.getId()));
                    System.out.println("author id: " + author.getId());
                    cookie.setMaxAge(60 * 30); // setting cookie to expire in 30 minutes
                    response.addCookie(cookie);
                    response.sendRedirect("/Main.htm");
                }
                break;
            }
            case "logout" : {
                Author author = DBWorker.getAuthorByID(Integer.valueOf(request.getParameter("authorId")));
                if (author == null) {
                    System.out.println("null author " + request.getParameter("login") + " " + request.getParameter("passwd"));
                    RequestDispatcher view = getServletContext().getRequestDispatcher("/Main.htm");
                    view.forward(request, response);
                } else {
                    System.out.println("logout success!!!");
                    Cookie cookie = new Cookie("qLandLogin", null);
                    cookie.setMaxAge(0); // setting cookie to expire in 30 minutes
                    response.addCookie(cookie);
                }
                break;
            }
        }
    }
}
