package connect;

import gen.AESCrypt;
import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;
import util.Constant;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
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

//        logger.debug("Oracle JDBC Driver Registered!");
        Connection connection = null;

        try {

            logger.debug(String.format("Connecting to %s",prop.getProperty("db.oracle.url")));
            connection = DriverManager.getConnection(
                    prop.getProperty("db.oracle.url")
                    , prop.getProperty("db.oracle.user")
                    ,decrypt(prop.getProperty("db.oracle.pass")));

        } catch (SQLException e) {
            logger.error("Connection Failed! Check output console");
            return null;

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (connection != null) {
            logger.info("Connection database complete!!");
            return connection;
        } else {
            logger.error("Failed to make connection");
        }
        return null;
    }



    public String decrypt(String value) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(Constant.Cryto.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = new BASE64Decoder().decodeBuffer(value);
        byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue, "utf-8");
        return decryptedValue;

    }

    private  Key generateKey() throws Exception {
        Key key = new SecretKeySpec(Constant.Cryto.KEY.getBytes(),Constant.Cryto.ALGORITHM);
        return key;
    }


}
