package gen;

import connect.DbManagement;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.beanio.StreamFactory;
import util.Constant;

import java.io.File;
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

            if (args.length > 0) {

                String config = System.getProperty("config.file");
                if (StringUtils.isEmpty(System.getProperty("config.file"))) {
                    config = "config.properties";
                }

                Properties prop = new Properties();
                prop.load(new FileInputStream(config));
                //custom log file
                if (!StringUtils.isEmpty(prop.getProperty("log.config.file"))){
                    PropertyConfigurator.configure(prop.getProperty("log.config.file"));
                }

                logger.info("Load configuration file");
                logger.debug("from "+config);

                DateFormat dateFormat = new SimpleDateFormat(prop.getProperty("file.name.dateformat"));

                String prefix = args[0].equals(Constant.Mode.READ)?
                        prop.getProperty("download.file.name.prefix"):prop.getProperty("upload.file.name.prefix");
                String fileName = prefix.trim() + dateFormat.format(new Date()) + "."+prop.getProperty("file.name.type").trim();
                logger.debug("File name is "+fileName);

                logger.debug("Create a StreamFactory ");
                StreamFactory factory = StreamFactory.newInstance();
                logger.debug("Load factory mapping file from "+prop.getProperty("mapping.file"));
                factory.load(prop.getProperty("mapping.file"));

                DbManagement db = new DbManagement(prop);
                Connection con = db.connection();

                if (con != null) {
                    logger.info(args[0].toUpperCase() + " File " + fileName);
                    switch (args[0].toLowerCase()) {
                        case Constant.Mode.WRITE:
                            logger.debug("File format is " + prop.getProperty("upload.file.format"));
                            if (!(fileName.matches(prop.getProperty("upload.file.format")))) {
                                throw new Exception("File Name is not Proper as per Format.");
                            }
                            logger.debug("Match file name format!!");
                            fileName = prop.getProperty("file.upload.path").trim() + fileName;
                            new PaymentHubFileWriter(prop, factory, con, fileName);
                            break;
                        case Constant.Mode.READ:
                            logger.debug("File format is " + prop.getProperty("download.file.format"));
                            if (!(fileName.matches(prop.getProperty("download.file.format")))) {
                                throw new Exception("File Name is not Proper as per Format.");
                            }
                            logger.debug("Match file name format!!");
                            fileName = prop.getProperty("file.download.path") + fileName;
                            new PaymentHubFileReader(prop, factory, con, fileName);
                            break;
                        default:
                            usage();
                            break;
                    }
                }

            } else {
                usage();
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    private static void usage() {
        System.out.println("Usage command");
        System.out.println("\tjava -Dconfig.file=${config.properties} -jar ${ActProduct.jar} ${mode} ${fileName}");
        System.out.println("\tUse -Dconfig.file=${config.properties} to get your config");
        System.out.println("\tUse -jar ${ActProduct.jar} to get your jarfile to run");
        System.out.println("\tUse ${mode} to set your mode to run");
        System.out.println("\t\tuse \"write\" to Write data from database to file");
        System.out.println("\t\tuse \"read\" to Read data from to file to database");
//        System.out.println("\tUse ${fileName} to set your file name");
    }

}
