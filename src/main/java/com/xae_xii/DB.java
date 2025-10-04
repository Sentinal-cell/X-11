package com.xae_xii;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class DB {
    private static final Logger logger = LogManager.getLogger(DB.class);
    public boolean check(String tusr, long userid){
        String url = "jdbc:mysql://localhost:3306/X11";
        String username = "root";
        String password = "root";
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            
            String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1"; 
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tusr);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                logger.info("User "+tusr+" found...");
                return true;
            } else {
                logger.info("Invalid username by "+ userid);
                return false;
            }

        } catch (Exception e) {
            logger.error("Dtabase error "+ e);
            return false;
        }
    }
}
