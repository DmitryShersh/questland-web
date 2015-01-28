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

@WebServlet(name = "Quests", urlPatterns = {"/quest/*"})
public class QuestServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
        RequestDispatcher view = request.getRequestDispatcher("/Quest.htm");
        view.forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
        String questParam = request.getParameter("type");
        if (questParam == null) {
            System.out.println("null quest type");
            return;
        }

        switch (questParam) {
            case "id" : {
                int questID = Integer.parseInt(request.getParameter("value"));
                Quest quest = DBWorker.getQuestByID(questID);
                if (quest == null) {
                    System.out.println("no such quest :(");
                    return;
                }

                JSONObject questJSON = quest.toJSON();
                System.out.println(questJSON.toString());
                response.getOutputStream().write(questJSON.toString().getBytes("UTF-8"));

                break;
            }
            case "popularQuests": {
                List<Quest> quests = DBWorker.getPopularQuests();
                JSONArray jsonQuests = new JSONArray();

                for (int i = 0; i < quests.size(); i++) {
                    jsonQuests.put(i, quests.get(i).toJSON());
                }

                response.getOutputStream().write(jsonQuests.toString().getBytes("UTF-8"));

                break;
            }
        }
    }
}
