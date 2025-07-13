package db;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    public static void init() {
        initTableAndUsers();
    }

    public static boolean loginUser(String username, String password) {
        boolean gotUser = false;

        String url = "jdbc:sqlite:chess.db";
        String sql = "SELECT id, username, password FROM users";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String dbUsername = rs.getString("username");
                String dbPassword = rs.getString("password");

                if(username.equals(dbUsername) && password.equals(dbPassword)) {
                    gotUser = true;
                    break;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return gotUser;
    }

    static void initTableAndUsers() {
        String url = "jdbc:sqlite:chess.db";

        // Init table
        {
            String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                    + " id       INTEGER PRIMARY KEY,\n"
                    + " username TEXT    NOT NULL,\n"
                    + " password TEXT    NOT NULL\n"
                    + ");";

            try(Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        // Inster users if there aren't any
        {
            int usersCount = 0;
            String sql = "SELECT id, username, password FROM users";

            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    usersCount += 1;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            if(usersCount == 0) {
                insertUser("user_01", "123");
                insertUser("user_02", "xyz");
            }
        }
    }

    static void insertUser(String username, String password) {
        String url = "jdbc:sqlite:chess.db";
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
