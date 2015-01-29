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

    private static final String GET_AUTHOR_BY_ID = "SELECT * FROM Authors WHERE id=?";
    private static final String GET_UNIVERSE_BY_ID = "SELECT name FROM Universes WHERE id=?";
    private static final String GET_CATEGORY_BY_ID = "SELECT name FROM Categories WHERE id=?";
    private static final String GET_GENRES_BY_QID = "SELECT genre_id FROM QuestGenres WHERE quest_id=?";
    private static final String GET_QUEST_LIKES = "SELECT val FROM Likes WHERE qid=?";

    private static Quest extractQuest(ResultSet quests, Connection connection) throws SQLException {
        int questID = quests.getInt("id");
        String name = quests.getString("name");

        // getting author from another table
        String author = null;
        int authorID = quests.getInt("author_id");
        try (PreparedStatement psAuthor = connection.prepareStatement(GET_AUTHOR_BY_ID)) {
            psAuthor.setInt(1, authorID);
            ResultSet authors = psAuthor.executeQuery();
            while (authors.next()) {
                author = authors.getString("login");
            }
        }

        //getting universe
        String universe = null;
        int universeID = quests.getInt("universe_id");
        try (PreparedStatement psUniverse = connection.prepareStatement(GET_UNIVERSE_BY_ID)) {
            psUniverse.setInt(1, universeID);
            ResultSet categories = psUniverse.executeQuery();
            while (categories.next()) {
                universe = categories.getString("name");
            }
        }

        // getting category
        String category = null;
        int categoryID = quests.getInt("category_id");
        try (PreparedStatement psCategory = connection.prepareStatement(GET_CATEGORY_BY_ID)) {
            psCategory.setInt(1, categoryID);
            ResultSet categories = psCategory.executeQuery();
            while (categories.next()) {
                category = categories.getString("name");
            }
        }

        // getting genres
        String genres = null;
        try (PreparedStatement psGenres = connection.prepareStatement(GET_GENRES_BY_QID)) {
            psGenres.setInt(1, questID);
            ResultSet genresSet = psGenres.executeQuery();

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

        int totalRate = 0;
        try (PreparedStatement psTotal = connection.prepareStatement(GET_QUEST_LIKES)) {
            psTotal.setInt(1, questID);
            ResultSet questLikes = psTotal.executeQuery();
            while (questLikes.next()) {
                totalRate += questLikes.getInt("val");
            }
        }

        return new Quest(
            questID, name, authorID, author, universeID, universe,
            category, genres, characters,
            description, imageURL, totalRate
        );
    }

    private static List<Quest> getAllQuests(ResultSet quests, Connection connection) throws SQLException {
        List<Quest> result = new ArrayList<>();
        while (quests.next()) {
            result.add(extractQuest(quests, connection));
        }
        return result;
    }

    private static final String LAST_MODIFIED_QUESTS = "SELECT * FROM Quests WHERE universe_id=? ORDER BY creation_date DESC LIMIT 5";

    public static List<Quest> getTopQuests(int universeId) {
        List<Quest> result = null;
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement psOQuests = connection.prepareStatement(LAST_MODIFIED_QUESTS))
        {
            psOQuests.setInt(1, universeId);
            ResultSet quests = psOQuests.executeQuery();
            result = getAllQuests(quests, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Author getAuthorByID(int authorID) {
        Author author = null;
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement psAuthor = connection.prepareStatement(GET_AUTHOR_BY_ID)
        ) {
            psAuthor.setInt(1, authorID);
            ResultSet authorSet = psAuthor.executeQuery();

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

    private static final String GET_QUEST_BY_ID = "SELECT * FROM Quests WHERE id=?";

    public static Quest getQuestByID(int questId) {
        Quest quest = null;
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement psQuest = connection.prepareStatement(GET_QUEST_BY_ID)
        ) {
            psQuest.setInt(1, questId);
            ResultSet quests = psQuest.executeQuery();
            quests.next();
            quest = extractQuest(quests, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quest;
    }

    private static final String GET_QUESTS_BY_AUTHOR = "SELECT * FROM Quests WHERE author_id=?";

    public static List<Quest> authorQuests(int authorId) {
        List<Quest> quests = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement psAQuests = connection.prepareStatement(GET_QUESTS_BY_AUTHOR)
        ) {
            psAQuests.setInt(1, authorId);
            ResultSet questSet = psAQuests.executeQuery();
            quests = getAllQuests(questSet, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quests;
    }

    private static final String GET_AUTHOR_BY_LOGIN = "SELECT * FROM Authors WHERE login=?";

    public static Author validate(String login, String pass) {
        if (login == null || pass == null) {
            return null;
        }

        Author author = null;
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement psALogin = connection.prepareStatement(GET_AUTHOR_BY_LOGIN);
        ) {
            psALogin.setString(1, login);
            ResultSet authors = psALogin.executeQuery();

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

    private static final String GET_SITUATION = "SELECT * FROM Situations WHERE quest_id=? AND sit_id=?";

    public static Situation getSituation(int questID, int sitID) {
        Situation situation = null;
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement psSit = connection.prepareStatement(GET_SITUATION)
        ) {
            psSit.setInt(1, questID);
            psSit.setInt(2, sitID);
            ResultSet situations = psSit.executeQuery();

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

    private static final String GET_LIKE_PAIR = "SELECT val FROM Likes WHERE qid=? AND aid=?";
    private static final String UPDATE_LIKES = "UPDATE Likes SET val=? WHERE qid=? AND aid=?";
    private static final String INSERT_LIKES = "INSERT INTO Likes (qid, aid, val) VALUES (?, ?, ?)";

    public static int  addLike(int qid, int aid, int rate) {
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement psPLike = connection.prepareStatement(GET_LIKE_PAIR)
        ) {
            psPLike.setInt(1, qid);
            psPLike.setInt(2, aid);
            ResultSet questLikes = psPLike.executeQuery();

            if (questLikes.next()) {
                if (questLikes.getInt("val") == rate) return 0;

                try (PreparedStatement psUpdate = connection.prepareStatement(UPDATE_LIKES)) {
                    psUpdate.setInt(1, rate);
                    psUpdate.setInt(2, qid);
                    psUpdate.setInt(3, aid);
                    psUpdate.executeUpdate();
                }
            } else {
                try (PreparedStatement psInsert = connection.prepareStatement(INSERT_LIKES)) {
                    psInsert.setInt(1, qid);
                    psInsert.setInt(2, aid);
                    psInsert.setInt(3, rate);
                    psInsert.executeUpdate();
                }
            }

            int totalRate = 0;
            try (PreparedStatement psTotal = connection.prepareStatement(GET_QUEST_LIKES)) {
                psTotal.setInt(1, qid);
                ResultSet questRate = psTotal.executeQuery();
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

    private static final String GET_AUTHOR_BY_LOGIN_OR_EMAIL = "SELECT * FROM Authors WHERE login=? OR email=?";
    private static final String INSERT_AUTHOR = "INSERT INTO Authors (login, pass, email) VALUES (?, ?, ?)";

    public static boolean addUser(String login, String pass, String email) {
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement psALE = connection.prepareStatement(GET_AUTHOR_BY_LOGIN_OR_EMAIL)
        ) {
            psALE.setString(1, login);
            psALE.setString(2, email);
            ResultSet users = psALE.executeQuery();

            if (!users.next()) {
                try (PreparedStatement psInsert = connection.prepareStatement(INSERT_AUTHOR)) {
                    psInsert.setString(1, login);
                    psInsert.setString(2, pass);
                    psInsert.setString(3, email);
                    psInsert.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static final String SORTED_AUTHORS = "SELECT * FROM Authors ORDER BY login";

    public static List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement psASorted = connection.prepareStatement(SORTED_AUTHORS)
        ) {
            ResultSet allAuthors = psASorted.executeQuery();

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

    private static final String GET_POPULAR_QUESTS =
        "SELECT * FROM ((SELECT " +
          "QuestLikes.id, QuestLikes.name, QuestLikes.author_id, QuestLikes.universe_id, " +
          "QuestLikes.category_id, QuestLikes.characters, QuestLikes.description, QuestLikes.img_url, QuestLikes.creation_date, " +
          "sum(QuestLikes.val) AS LikesNum " +
        "FROM ((SELECT * FROM Quests LEFT JOIN Likes ON Quests.id=Likes.qid) AS QuestLikes)" +
        "GROUP BY id ) AS GQuests) ORDER BY CASE WHEN (GQuests.LikesNum IS NULL) THEN 0 ELSE GQuests.LikesNum END DESC";

    public static List<Quest> getPopularQuests() {
        List<Quest> quests = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement psPop = connection.prepareStatement(GET_POPULAR_QUESTS)
        ) {
            ResultSet popQuests = psPop.executeQuery();
            quests = getAllQuests(popQuests, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quests;
    }

    private static final String UPDATE_AVATAR = "UPDATE Authors SET avatar_url=? WHERE id=?";

    public static void updateUserAvatar(int authorId, String avatarURL) {
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement psUpdate = connection.prepareStatement(UPDATE_AVATAR)
        ) {
            psUpdate.setString(1, avatarURL);
            psUpdate.setInt(2, authorId);
            psUpdate.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
