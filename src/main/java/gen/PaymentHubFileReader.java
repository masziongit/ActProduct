package gen;

import connect.FileSFTP;
import field.PaymentHub;
import field.PaymentHubFooter;
import org.apache.log4j.Logger;
import org.beanio.BeanReader;
import org.beanio.StreamFactory;
import util.Constant;
import util.Util;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PaymentHubFileReader {

    final static Logger logger = Logger.getLogger(PaymentHubFileReader.class);

    public PaymentHubFileReader(Properties prop, StreamFactory factory, Connection con, String fileName) throws Exception {

        new FileSFTP(prop, 'D', fileName);
        // use a StreamFactory to create a BeanWriter
        File file = new File(fileName);
        BeanReader in = factory.createReader(prop.getProperty("stream.name"), file);
        //Read file
        List<PaymentHub> paymentHubFiles = new ArrayList<>();
        try {
            Object record;
            PaymentHubFooter footer = null;
            while ((record = in.read()) != null) {

                if ("record".equals(in.getRecordName())) {
                    PaymentHub paymentHub = (PaymentHub) record;

                    if (paymentHub.getRecordIndentifer().equals("D")) {
                        paymentHubFiles.add(paymentHub);
                    }

                }
//                else if ("trailer".equals(in.getRecordName())) {
//                    footer = (PaymentHubFooter) record;
//                    if (!footer.getRecordIndentifer().equals("T")){
//                        throw new Exception("Last Record is not of type T in the file.");
//                    }
//
//                }
            }

            if (paymentHubFiles.isEmpty())
                throw new Exception("File has no records");


        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return;
        }

        //Add query
        PreparedStatement selectStmt = con.prepareStatement(Constant.SqlQuery.SELECT);
        try {
            ResultSet rs = selectStmt.executeQuery();
            PreparedStatement updateStmt = con.prepareStatement(Constant.SqlQuery.UPDATE);

            int updateNum = 0;

            while (rs.next()) {

                try {
                    PaymentHub updateData = getUpdateObj(paymentHubFiles, rs);

                    if (updateData == null) {
//                    logger.error(SqlField.AcctNumber + " : " + accountNumber + " no need to update");
                    } else {
                        int i = 0;


                        //set
                        updateStmt.setBigDecimal(++i, Util.strToBigDec(updateData.getAvailableBalance()));
                        updateStmt.setInt(++i, Integer.valueOf(updateData.getAccountProductCode()));
                        //Where
                        updateStmt.setString(++i, updateData.getAcctNumber());
                        updateStmt.executeQuery();

                        logger.info(Constant.SqlField.AcctNumber + " : " + updateData.getAcctNumber() +
                                " Update " + Constant.SqlField.AccountProductCode +
                                " from " + rs.getInt(Constant.SqlField.AccountProductCode) +
                                " to " + updateData.getAccountProductCode() +
                                " Update " + Constant.SqlField.AvailableBalance +
                                " from " + rs.getInt(Constant.SqlField.AvailableBalance) +
                                " to " + updateData.getAvailableBalance() +
                                " Successfully Updated");

                        updateNum++;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("Can't Update " + Constant.SqlField.AcctNumber + " " +
                            rs.getLong(Constant.SqlField.AcctNumber) + " : " + e);
                }
            }

            if (updateNum < 1) {
                logger.info("No Data Updated");
            }

//            updateStmt.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            in.close();
            file.delete();
            logger.info("Remove local file");
            con.close();
            logger.info("Connection close");
        }


    }

    private PaymentHub getUpdateObj(List<PaymentHub> list, ResultSet rs) throws Exception {
        for (PaymentHub obj : list) {
            BigDecimal oB = Util.strToBigDec(obj.getAvailableBalance());
            BigDecimal rsB = rs.getBigDecimal(Constant.SqlField.AvailableBalance).setScale(2);

            if (Long.valueOf(obj.getAcctNumber()).equals(rs.getLong(Constant.SqlField.AcctNumber))
                    && (!(Integer.valueOf(obj.getAccountProductCode()).equals(rs.getInt(Constant.SqlField.AccountProductCode))
                    && (oB.compareTo(rsB) == 0)))) {
                return obj;

            }

        }
        return null;
    }
}