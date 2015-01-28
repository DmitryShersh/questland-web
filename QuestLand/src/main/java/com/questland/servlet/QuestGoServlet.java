package com.questland.servlet;

import com.questland.db.DBWorker;
import com.questland.pojo.Quest;
import com.questland.pojo.Situation;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet(name = "QuestsGo", urlPatterns = {"/questgo/*"})
public class QuestGoServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
        System.out.println("Start quest");
        RequestDispatcher view = request.getRequestDispatcher("/Questgo.htm");
        view.forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
        System.out.println("get post!");
        String questParam = request.getParameter("type");
        if (questParam == null || !questParam.equals("id")) {
            System.out.println("null quest type");
            return;
        }

        int questID = Integer.parseInt(request.getParameter("questValue"));
        int sitID = Integer.parseInt(request.getParameter("situationValue"));
        int move = Integer.parseInt(request.getParameter("nextMove"));
        Situation situation = DBWorker.getSituation(questID, sitID);
        if (situation == null) {
            System.out.println("no such quest :(");
            return;
        }

        if (move != 0) {
            situation = DBWorker.getSituation(questID, situation.getMoves().get(move - 1));
        }

        JSONObject situationJSON = situation.toJSON();
        System.out.println(situationJSON);
        response.getOutputStream().write(situationJSON.toString().getBytes("UTF-8"));
    }
}
