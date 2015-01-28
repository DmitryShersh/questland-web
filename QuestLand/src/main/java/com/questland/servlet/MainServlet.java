package com.questland.servlet;

import com.questland.db.DBWorker;
import com.questland.pojo.Quest;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "QuestDBRequest", urlPatterns = {"/main"})
public class MainServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        if (request.getParameter("type").equals("init")) {
            System.out.println("not init :(");
            return;
        }
        ServletOutputStream out = response.getOutputStream();

        JSONObject resObj = new JSONObject();

        for (int universeID = 1; universeID <= 4; universeID++) {
            List<Quest> quests = DBWorker.getTopQuests(universeID);
            JSONArray questsArray = new JSONArray();

            for (int i = 0; i < quests.size(); i++) {
                questsArray.put(i, quests.get(i).toJSON());
            }

            resObj.put(String.valueOf(universeID), questsArray);
        }

        out.write(resObj.toString().getBytes("UTF-8"));

        response.flushBuffer();
    }
}
