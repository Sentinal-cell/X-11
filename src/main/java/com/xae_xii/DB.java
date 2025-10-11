package com.xae_xii;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class DB {
    public static String url = "jdbc:mysql://localhost:3306/X11";;
    public static String dbusername = "a3on";
    public static String password = "a3on";
    private static final Logger logger = LogManager.getLogger(DB.class);
    public boolean check(String tusr, long userid){
        try (Connection conn = DriverManager.getConnection(url, dbusername, password)) {
            
            String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1"; 
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tusr);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                logger.info("User "+tusr+" found...");
                conn.close();
                return true;
            } else {
                logger.info("Invalid username by "+ userid);
                conn.close();
                return false;
            }

        } catch (Exception e) {
            logger.error("Dtabase error "+ e);
            return false;
        }
    }
    public String[] ret(String username){
        String[] uinf = new String[3];
        Statement statement;
        try (Connection conn = DriverManager.getConnection(url, dbusername, password)) {
            statement = conn.createStatement();
            String query = "SELECT cname, id FROM users WHERE username='"+ username +"'";
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
            uinf[0] = resultSet.getString("cname");
            uinf[1] = String.valueOf(resultSet.getLong("id"));
        }
            return uinf;
        }catch(Exception e){
            logger.error("Database error "+e);
            return null;
        }
    }
}
