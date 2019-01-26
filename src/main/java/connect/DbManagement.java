package connect;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Properties;

public class DbManagement {

    final static Logger logger = Logger.getLogger(DbManagement.class);

    private Properties prop;

    public DbManagement(Properties prop) {
        this.prop = prop;
    }

    public Connection connection() {

        logger.info("Oracle JDBC Connection");

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch (ClassNotFoundException e) {

            logger.error("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return null;

        }

        logger.info("Oracle JDBC Driver Registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(
                    prop.getProperty("db.oracle.url")
                    , prop.getProperty("db.oracle.user")
                    , prop.getProperty("db.oracle.pass"));

        } catch (SQLException e) {
            logger.error("Connection Failed! Check output console");
            return null;

        }

        if (connection != null) {
            logger.info("You made it, take control your database now!");
            return connection;
        } else {
            logger.error("Failed to make connection!");
        }
        return null;
    }
}
