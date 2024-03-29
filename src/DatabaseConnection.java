import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/HMS";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "sevvalK3.";

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("No Connection", e);
        }
    }
}