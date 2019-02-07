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
                logger.debug("Mapping writer file from stream name is "+prop.getProperty("stream.name"));
                BeanWriter out = factory.createWriter(prop.getProperty("stream.name"), file);

                while (rs.next()) {

                    PaymentHub paymentHub = new PaymentHub();
                    paymentHub.setRecordIndentifer("D");
                    paymentHub.setAcctCtl1(String.format("%04d", 11));
                    paymentHub.setAcctCtl2(String.format("%04d",1));
//                    rs.getString("ACCT_CRNCY")
                    paymentHub.setAcctCtl3(String.format("%04d", rs.getInt(Constant.SqlField.AcctCtl3)));
                    paymentHub.setAcctCtl4(String.format("%04d",0));
//                    rs.getInt("ACCT_CTL4"))Account type code
                    paymentHub.setAcctNumber(String.format("%010d",rs.getLong(Constant.SqlField.AcctNumber)));
                    paymentHub.setAccountProductCode(String.format("%04d",rs.getInt(Constant.SqlField.AccountProductCode)));
                    paymentHub.setAvailableBalance(Util.bigDecToStr("%020d",
                                rs.getBigDecimal(Constant.SqlField.AvailableBalance)));
//                    paymentHub.setAdditional2(rs.getString("ADDITIONAL2"));
//                    paymentHub.setAdditional3(rs.getString("ADDITIONAL3"));
//                    paymentHub.setAdditional4(rs.getString("ADDITIONAL4"));
//                    paymentHub.setAdditional5(rs.getString("ADDITIONAL5"));
//                    paymentHub.setAdditional6(rs.getString("ADDITIONAL6"));

                    // write an Employee object directly to the BeanWriter
                    out.write(paymentHub);
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

                logger.debug("Write local complete!!");

                try {
                    new FileSFTP(prop,'U',fileName);

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e);
                }finally {
                    file.delete();
                    logger.debug("Remove local file");
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