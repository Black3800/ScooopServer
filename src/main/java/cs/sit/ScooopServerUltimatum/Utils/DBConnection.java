package cs.sit.ScooopServerUltimatum.Utils;

import javax.validation.constraints.Null;
import java.sql.*;

public class DBConnection {
    private static final String db_URL = "REDACTED";

    public static Connection getMySQLConnection() throws Exception {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection dbConnection = DriverManager.getConnection(db_URL,"REDACTED","REDACTED");
            return dbConnection;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void close(Connection c, Statement s, ResultSet r) {
        try {
            r.close();
            s.close();
            c.close();
        } catch(NullPointerException e) {
            // Ignore
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
