package com.questland.db;

import com.questland.pojo.Author;
import com.questland.pojo.Quest;
import com.questland.pojo.Situation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBWorker {
    private static final String URL = "jdbc:mysql://localhost:3306/quest_schema";
    private static final String LOGIN = "root";
    private static final String PASSWORD = "root";

    static {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Quest extractQuest(ResultSet quests, Connection connection) throws SQLException {
        int questID = quests.getInt("id");
        String name = quests.getString("name");

        // getting author from another table
        String author = null;
        int authorID = quests.getInt("author_id");
        try (Statement authorSt = connection.createStatement()) {
            ResultSet authors = authorSt.executeQuery("SELECT login FROM Authors WHERE id=" + authorID + ";");
            while (authors.next()) {
                author = authors.getString("login");
            }
        }

        //getting universe
        String universe = null;
        int universeID = quests.getInt("universe_id");
        try (Statement categorySt = connection.createStatement()) {
            ResultSet categories = categorySt.executeQuery("SELECT name FROM Universes WHERE id=" + universeID + ";");
            while (categories.next()) {
                universe = categories.getString("name");
            }
        }

        // getting category
        String category = null;
        int categoryID = quests.getInt("category_id");
        try (Statement categorySt = connection.createStatement()) {
            ResultSet categories = categorySt.executeQuery("SELECT name FROM Categories WHERE id=" + categoryID + ";");
            while (categories.next()) {
                category = categories.getString("name");
            }
        }

        // getting genres
        String genres = null;
        try (Statement questGenreSt = connection.createStatement()) {
            ResultSet genresSet = questGenreSt.executeQuery("SELECT genre_id FROM QuestGenres WHERE quest_id=" + questID + ";");

            String genreQuery = "SELECT name FROM Genres WHERE ";
            List<String> genreIDS = new ArrayList<>();
            while (genresSet.next()) {
                genreIDS.add("id=" + String.valueOf(genresSet.getInt("genre_id")));
            }
            genreQuery += String.join(" OR ", genreIDS) + ";";

            try (Statement genreSt = connection.createStatement()) {
                ResultSet genresNames = genreSt.executeQuery(genreQuery);
                List<String> names = new ArrayList<>();
                while (genresNames.next()) {
                    names.add(genresNames.getString("name"));
                }
                genres = String.join(", ", names);
            }
        }

        String characters = quests.getString("characters");
        String description = quests.getString("description");
        String imageURL = quests.getString("img_url");
        int rate = 0;

        try (Statement questLikesSt = connection.createStatement()) {
            ResultSet questLikes = questLikesSt.executeQuery("SELECT val FROM Likes WHERE qid=" + questID + ";");

            while (questLikes.next()) {
                rate += questLikes.getInt("val");
            }
        }

        return new Quest(
            questID, name, authorID, author, universeID, universe,
            category, genres, characters,
            description, imageURL, rate
        );
    }

    private static List<Quest> getAllQuests(ResultSet quests, Connection connection) throws SQLException {
        List<Quest> result = new ArrayList<>();
        while (quests.next()) {
            result.add(extractQuest(quests, connection));
        }
        return result;
    }

    public static List<Quest> getTopQuests(int universeID) {
        final String st1 = "SELECT * FROM Quests WHERE universe_id=";
        final String st2 = " ORDER BY creation_date DESC LIMIT 5;";

        List<Quest> result = null;

        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             Statement questSt = connection.createStatement())
        {
            ResultSet quests = questSt.executeQuery(st1 + universeID + st2);
            result = getAllQuests(quests, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Author getAuthorByID(int authorID) {
        Author author = null;
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             Statement st = connection.createStatement()
        ) {
            ResultSet authorSet = st.executeQuery("SELECT * FROM Authors WHERE id=" + authorID + ";");
            while (authorSet.next()) {
                int id = authorSet.getInt("id");
                String login = authorSet.getString("login");
                String pass = authorSet.getString("pass");
                String email = authorSet.getString("email");
                String description = authorSet.getString("description");
                String avatarURL = authorSet.getString("avatar_url");

                author = new Author(id, login, pass, email, description, avatarURL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return author;
    }

    public static Quest getQuestByID(int questID) {
        Quest quest = null;
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             Statement st = connection.createStatement()
        ) {
            ResultSet quests = st.executeQuery("SELECT * FROM Quests WHERE id=" + questID + ";");
            quests.next();
            quest = extractQuest(quests, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quest;
    }

    public static List<Quest> authorQuests(int authorID) {
        List<Quest> quests = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             Statement st = connection.createStatement()
        ) {
            ResultSet questSet = st.executeQuery("SELECT * FROM Quests WHERE author_id=" + authorID + ";");
            quests = getAllQuests(questSet, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quests;
    }

    public static Author validate(String login, String pass) {
        if (login == null || pass == null) {
            return null;
        }

        Author author = null;
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             Statement st = connection.createStatement()
        ) {
            ResultSet authors = st.executeQuery("SELECT * FROM Authors WHERE login=\"" + login + "\";");
            while (authors.next()) {
                int id = authors.getInt("id");
                String realPass = authors.getString("pass");
                if (!realPass.equals(pass)) {
                    break;
                }

                String email = authors.getString("email");
                String description = authors.getString("description");
                String avatarURL = authors.getString("avatar_url");

                author = new Author(id, login, pass, email, description, avatarURL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return author;
    }

    public static Situation getSituation(int questID, int sitID) {
        Situation situation = null;
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             Statement st = connection.createStatement()
        ) {
            ResultSet situations = st.executeQuery("SELECT * FROM Situations WHERE quest_id=" + questID + " AND sit_id=" + sitID + ";");
            while (situations.next()) {
                String imageURL = situations.getString("img");
                String smallDescription = situations.getString("small_text");
                String bigDescription = situations.getString("big_text");
                boolean draft = (situations.getInt("draft") == 1);
                List<String> choices = new ArrayList<>(5);
                List<Integer> moves = new ArrayList<>(5);
                for (int i = 1; i <= 5; i++) {
                    String choice = situations.getString("choice" + i);
                    if (choice == null) {
                        break;
                    }
                    int move = situations.getInt("next" + i);

                    choices.add(choice);
                    moves.add(move);
                }

                situation = new Situation(
                    questID, sitID, imageURL,
                    smallDescription, bigDescription, draft,
                    choices, moves
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return situation;
    }

    public static int  addLike(int qid, int aid, int rate) {
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             Statement addLikeSt = connection.createStatement()
        ) {
            ResultSet questLikes = addLikeSt.executeQuery("SELECT val FROM Likes WHERE qid=" + qid + " AND aid=" + aid + ";");
            if (questLikes.next()) {
                if (questLikes.getInt("val") == rate) return 0;

                try (Statement insertSt = connection.createStatement()) {
                    insertSt.executeUpdate("UPDATE Likes SET val=" + rate + " WHERE qid=" + qid + " AND aid=" + aid + ";");
                }
            } else {
                try (Statement insertSt = connection.createStatement()) {
                    insertSt.executeUpdate("INSERT INTO Likes (qid, aid, val) VALUES (" + qid + ", " + aid + ", " + rate + ");");
                }
            }

            int totalRate = 0;
            try (Statement resRateSt = connection.createStatement()) {
                ResultSet questRate = resRateSt.executeQuery("SELECT val FROM Likes WHERE qid=" + qid + ";");
                while (questRate.next()) {
                    totalRate += questRate.getInt("val");
                }
            }
            return totalRate;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static boolean addUser(String login, String pass, String mail) {
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             Statement getUserSt = connection.createStatement()
        ) {
            ResultSet users = getUserSt.executeQuery("SELECT * FROM Authors WHERE login=\"" + login + "\";");
            if (!users.next()) {

                try (Statement emailSt = connection.createStatement()) {
                    ResultSet emails = emailSt.executeQuery("SELECT * FROM Authors WHERE email=\"" + mail + "\";");
                    if (emails.next()) {
                        return false;
                    }
                }

                try (Statement insertSt = connection.createStatement()) {
                    insertSt.executeUpdate("INSERT INTO Authors (login, pass, email) " +
                            "VALUES (\"" + login + "\", \"" + pass + "\", \"" + mail + "\");");
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             Statement getAuthorsSt = connection.createStatement()
        ) {
            ResultSet allAuthors = getAuthorsSt.executeQuery("SELECT * FROM Authors ORDER BY login");

            while (allAuthors.next()) {
                int id = allAuthors.getInt("id");
                String login = allAuthors.getString("login");
                String pass = allAuthors.getString("pass");
                String email = allAuthors.getString("email");
                String description = allAuthors.getString("description");
                String avatarURL = allAuthors.getString("avatar_url");

                authors.add(new Author(id, login, pass, email, description, avatarURL));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  authors;
    }

    public static List<Quest> getPopularQuests() {
        List<Quest> quests = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             Statement getQuestsSt = connection.createStatement()
        ) {
            ResultSet popQuests = getQuestsSt.executeQuery(
                "SELECT " +
                  "QuestLikes.id, QuestLikes.name, QuestLikes.author_id, QuestLikes.universe_id, " +
                  "QuestLikes.category_id, QuestLikes.characters, QuestLikes.description, QuestLikes.img_url, QuestLikes.creation_date, " +
                  "sum(QuestLikes.val) AS LikesNum " +
                "FROM ((SELECT * FROM " +
                "    Quests " +
                "  LEFT JOIN " +
                "    Likes " +
                "  ON Quests.id=Likes.qid) AS QuestLikes) " +
                "  GROUP BY " +
                "    id " +
                "  ORDER BY LikesNum DESC;"
            );
            quests = getAllQuests(popQuests, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quests;
    }

    public static void updateUserAvatar(int authorId, String avatarURL) {
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             Statement updateAuthorAvaSt = connection.createStatement()
        ) {
            updateAuthorAvaSt.executeUpdate("UPDATE Authors SET avatar_url=\"" + avatarURL + "\" WHERE id=" + authorId + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
