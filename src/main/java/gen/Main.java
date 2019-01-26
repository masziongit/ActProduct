package gen;

import connect.DbManagement;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.beanio.StreamFactory;
import util.Constant;

import java.io.FileInputStream;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        try {

            if (args.length > 0){

                String config = System.getProperty("config.file");
                if (StringUtils.isEmpty(config)) {
                    config = "config.properties";
                }

                Properties prop = new Properties();
                prop.load(new FileInputStream(config));

                DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
                String fileName = "OACSYN"+dateFormat.format(new Date())+".R01";
//                String fileName = args[1];

                //Not Need
//                if (!(fileName.length() == 16 && fileName.matches(prop.getProperty("file.format")))){
//                    throw new Exception("File Name is not Proper as per Format.");
//                }

                // create a StreamFactory
                StreamFactory factory = StreamFactory.newInstance();
                // load the mapping file
                factory.load(prop.getProperty("mapping.file"));

                DbManagement db = new DbManagement(prop);
                Connection con = db.connection();

                logger.info(args[0].toUpperCase()+" File "+fileName);
                switch (args[0].toLowerCase()){
                    case Constant.Mode.WRITE :
                        new PaymentHubFileWriter(prop,factory,con,fileName);
                        break;
                    case Constant.Mode.READ :
                        new PaymentHubFileReader(prop,factory,con,fileName);
                        break;
                }
            }else {
                usage();
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.error(e);
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
