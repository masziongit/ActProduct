package gen;

import connect.DbManagement;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.beanio.StreamFactory;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Properties;

public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        try {

            if (args.length == 2){

                if (args[1].length() == 15 && args[1].matches("OACSYN[0-9]{6}(\\.)R01")){
                    throw new Exception("File Name is not Proper as per Format.");
                }

                String config = System.getProperty("config.file");
                if (StringUtils.isEmpty(config)) {
                    config = "config.properties";
                }

                Properties prop = new Properties();
                prop.load(new FileInputStream(config));

                // create a StreamFactory
                StreamFactory factory = StreamFactory.newInstance();
                // load the mapping file
                factory.load(prop.getProperty("mapping.file"));

                DbManagement db = new DbManagement(prop);
                Connection con = db.connection();

                logger.info(args[0].toUpperCase()+" file "+args[1]);
                switch (args[0]){
                    case "write" :
                        new PaymentHubFileWriter(prop,factory,con,args[1]);
                        break;
                    case "read":
                        new PaymentHubFileReader(prop,factory,con,args[1]);
                        break;
                }
            }else {
                usage();
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private static void usage() {
        System.out.println("Usage command");
        System.out.println("\tjava -Dconfig.file=${config.properties} -jar ${PaymentHub.jar} ${mode} ${fileName}");
        System.out.println("\tUse -Dconfig.file=${config.properties} to get your config");
        System.out.println("\tUse -jar ${PaymentHub.jar} to get your jarfile to run");
        System.out.println("\tUse ${mode} to set your mode to run");
        System.out.println("\t\tuse \"write\" to Write data from database to file");
        System.out.println("\t\tuse \"read\" to Read data from to file to database");
        System.out.println("\tUse ${fileName} to set your file name");
    }

}
