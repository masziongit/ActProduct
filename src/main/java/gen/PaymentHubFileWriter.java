package gen;

import field.PaymentHub;
import field.PaymentHubFooter;
import connect.FileSFTP;
import org.apache.log4j.Logger;
import org.beanio.BeanWriter;
import org.beanio.StreamFactory;
import util.Constant;
import util.Util;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

public class PaymentHubFileWriter {

    final static Logger logger = Logger.getLogger(PaymentHubFileWriter.class);

    public PaymentHubFileWriter(Properties prop, StreamFactory factory, Connection con,String fileName) throws SQLException {

            logger.debug("PreparedStatement : "+prop.getProperty("db.select"));
            PreparedStatement preStmt = con.prepareStatement(prop.getProperty("db.select"));

            try {
                ResultSet rs = preStmt.executeQuery();
                logger.debug("PreparedStatement executeQuery complete!!");

                logger.debug("Create local file");
                File file = new File(fileName);
                file.getParentFile().mkdirs();
                logger.debug("Mapping writer file from stream name is "+prop.getProperty("stream.name"));
                BeanWriter out = factory.createWriter(prop.getProperty("stream.name"), file);

                while (rs.next()) {
                    PaymentHub ph = Util.convertRsToPH(rs);
                    ph.setAccountProductCode(null);
                    ph.setAvailableBalance(null);
                    out.write(ph);
                }
                logger.debug("Write record complete!!");

                //set footer
                PaymentHubFooter paymentHubFooter = new PaymentHubFooter();
                paymentHubFooter.setRecordIndentifer("T");
                paymentHubFooter.setProcessDate(new Date());
                out.write(paymentHubFooter);
                logger.debug("Write trailer complete!!");

                out.flush();
                out.close();

                logger.debug("Write file local complete!!");

                try {
                    new FileSFTP(prop,'U',file);

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e);
                }finally {
//                    file.delete();
//                    logger.debug("Remove local file");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                logger.error(e);
            } finally {
                preStmt.close();
                con.close();
                logger.info("Database Connection close");
            }


    }
}