package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DocSaving {

    private static final String DB_URL = "jdbc:sqlite:userIDs.db"; // 替换为你的数据库路径

    /**
     * Saves the given user ID into the database.
     *
     * @param userId The user ID to save.
     */
    public void saveUserId(String userId) {
        String sql = "INSERT INTO users (user_id) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving user ID to database: " + e.getMessage());
        }
    }

    /**
     * Initializes the database and creates the necessary tables if they do not exist.
     */
    public void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id TEXT NOT NULL UNIQUE);";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.execute();

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
