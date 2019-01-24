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

            PreparedStatement preStmt = con.prepareStatement(Constant.SqlQuery.SELECT);

            try {
                ResultSet rs = preStmt.executeQuery();

                Date today = new Date();

                // use a StreamFactory to create a BeanWriter
                File file = new File(fileName);
                BeanWriter out = factory.createWriter(prop.getProperty("stream.name"), file);

                while (rs.next()) {

                    PaymentHub paymentHub = new PaymentHub();
                    paymentHub.setRecordIndentifer("D");
                    paymentHub.setAcctCtl1(String.format("%04d", 11));
                    paymentHub.setAcctCtl2(String.format("%04d",11));
//                    rs.getString("ACCT_CRNCY")
                    paymentHub.setAcctCtl3(String.format("%04d", rs.getInt(Constant.SqlField.AcctCtl3)));
                    paymentHub.setAcctCtl4(String.format("%04d",0));
//                    rs.getInt("ACCT_CTL4"))
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

                //set footer
                PaymentHubFooter paymentHubFooter = new PaymentHubFooter();
                paymentHubFooter.setRecordIndentifer("T");
                paymentHubFooter.setProcessDate(today);

                out.write(paymentHubFooter);
                out.flush();
                out.close();

                try {
                    new FileSFTP(prop,'U',fileName);
                    logger.info("Write file complete");
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }finally {
                    file.delete();
                    logger.info("Remove local file");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            } finally {
                preStmt.close();
                con.close();
                logger.info("Connection close");
            }


    }
}