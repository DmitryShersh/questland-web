package com.questland.servlet;

import com.questland.db.DBWorker;
import com.questland.pojo.Author;
import com.questland.pojo.Quest;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@WebServlet(name = "Authors", urlPatterns = {"/author/*"})
public class AuthorServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
        System.out.println(request.getRequestURI());
        RequestDispatcher view = request.getRequestDispatcher("/Author.htm");
        view.forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        String authorParam = request.getParameter("type");
        if (authorParam == null) {
            System.out.println("null author type");
            return;
        }

        switch (authorParam) {
            case "id": {
                int authorID = Integer.parseInt(request.getParameter("value"));
                Author author = DBWorker.getAuthorByID(authorID);
                if (author == null) {
                    System.out.println("no such author :(");
                    return;
                }
                List<Quest> quests = DBWorker.authorQuests(authorID);

                JSONObject authorJSON = author.toJSON();
                JSONArray authorQuests = new JSONArray();
                for (int i = 0; i < quests.size(); i++) {
                    authorQuests.put(i, quests.get(i).toJSON());
                }
                authorJSON.put("quests", authorQuests);

                response.getOutputStream().write(authorJSON.toString().getBytes("UTF-8"));

                break;
            }
            case "rate": {
                int authorID = Integer.parseInt(request.getParameter("aVal"));
                int questID = Integer.parseInt(request.getParameter("qVal"));
                int rate = Integer.parseInt(request.getParameter("rVal"));

                if (rate != 1 && rate != -1) {
                    System.out.println("wrong rate");
                    return;
                }

                int resultRate = DBWorker.addLike(questID, authorID, rate);
                System.out.println("result: " + resultRate);
                JSONObject resultJSON = new JSONObject();
                resultJSON.put("rate", resultRate);
                response.getOutputStream().write(resultJSON.toString().getBytes("UTF-8"));

                break;
            }
            case "allAuthors": {
                List<Author> authors = DBWorker.getAllAuthors();
                JSONArray jsonAuthors = new JSONArray();

                for (int i = 0; i < authors.size(); i++) {
                    jsonAuthors.put(i, authors.get(i).toJSON());
                }

                response.getOutputStream().write(jsonAuthors.toString().getBytes("UTF-8"));
                break;
            }
        }


    }
}
