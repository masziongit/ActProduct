package gen;

import connect.FileSFTP;
import field.PaymentHub;
import field.PaymentHubFooter;
import field.SqlLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.beanio.*;
import util.SqlField;
import util.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PaymentHubFileReader {

    final static Logger logger = Logger.getLogger(PaymentHubFileReader.class);

    public PaymentHubFileReader(Properties prop, StreamFactory factory, Connection con,String fileName) throws Exception {

        new FileSFTP(prop, 'D',fileName);
        // use a StreamFactory to create a BeanWriter
        File file = new File(fileName);
        BeanReader in = factory.createReader(prop.getProperty("stream.name"), file);
        //Read file
        List<PaymentHub> paymentHubFiles = new ArrayList<>();
        try {
            Object record;
            PaymentHubFooter footer = null;
            while ((record = in.read()) != null) {

                RecordContext context = in.getRecordContext(0);
                if ("record".equals(in.getRecordName())) {

                    String errMsg = fileValidate(context);
                    if(StringUtils.isEmpty(errMsg)){
                        PaymentHub paymentHub = (PaymentHub) record;
                        paymentHubFiles.add(paymentHub);
                    }else {
                        throw new Exception(errMsg);
                    }

                } else if ("trailer".equals(in.getRecordName())) {
                    footer = (PaymentHubFooter) record;
                    if (!footer.getRecordIndentifer().equals("T")){
                        throw new Exception("Last Record is not of type T in the file.");
                    }

                }
            }

            if (paymentHubFiles.isEmpty())
                throw new Exception("File has no records");

            if (footer == null)
                throw new Exception("Last Record is not of type T in the file.");

        } catch (UnidentifiedRecordException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return;
        } catch (InvalidRecordException e) {
            e.printStackTrace();
            logger.error("Date present at T record doesnot matches with BOD date.");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return;
        }

        StringBuilder queryBuilder = new StringBuilder(prop.getProperty("db.oracle.query.select"));
        //Add query
        queryBuilder.append("AND " + SqlField.AcctNumber + " IN (");
        for (PaymentHub acct : paymentHubFiles) {
            queryBuilder.append(acct.getAcctNumber());
            if (paymentHubFiles.get(paymentHubFiles.size() - 1) != acct)
                queryBuilder.append(" , ");
        }
        queryBuilder.append(" )");

        PreparedStatement selectStmt = con.prepareStatement(queryBuilder.toString());
        try {
            ResultSet rs = selectStmt.executeQuery();
            PreparedStatement updateStmt = con.prepareStatement(prop.getProperty("db.oracle.query.update"));

            //Loging
            StreamFactory logFactory = StreamFactory.newInstance();
            // load the mapping file
            logFactory.load(prop.getProperty("mapping.file"));

            BeanWriter logOut = logFactory.createWriter("sqlLog", new FileWriter ("report.csv"));

            while (rs.next()) {

                PaymentHub updateData = getUpdateObj(paymentHubFiles,rs);

                if (updateData == null) {
//                    logger.error(SqlField.AcctNumber + " : " + accountNumber + " no need to update");
                } else {
                    int i = 0;
                    //set
                    updateStmt.setBigDecimal(++i, Util.strToBigDec(updateData.getAvailableBalance()));
                    updateStmt.setInt(++i, Integer.valueOf(updateData.getAccountProductCode()));
                    //Where
                    updateStmt.setString(++i, updateData.getAcctNumber());
                    updateStmt.addBatch();
                    logger.info(SqlField.AcctNumber + " : " + updateData.getAcctNumber() +
                            " Update " + SqlField.AccountProductCode + " to "
                            + updateData.getAccountProductCode()+" Successfully Updated");

                    writeLogCsv(logOut,updateData,rs);

                }
            }

            updateStmt.executeBatch();
            logger.info("Successfully Updated");
            logger.info("Write LogDB to csv");
            logOut.flush();
            logOut.close();

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            in.close();
            file.delete();
            logger.info("Remove local file");
            con.close();
            logger.info("Connection close");
        }


    }

    private void writeLogCsv(BeanWriter logOut, PaymentHub updateData, ResultSet rs) throws SQLException {
        SqlLog sqlLog = new SqlLog();
        sqlLog.setAcctNumber(rs.getString(SqlField.AcctNumber));
        sqlLog.setAccountProductCode(rs.getString(SqlField.AccountProductCode));
        sqlLog.setAvailableBalance(rs.getString(SqlField.AvailableBalance));
        sqlLog.setUpdateAccountProductCode(updateData.getAccountProductCode());
        sqlLog.setUpdateAvailableBalance(updateData.getAvailableBalance());
        sqlLog.setStatus("Success");
        logOut.write(sqlLog);
    }

    private String fileValidate(RecordContext context) {

        String msg = null;

        if (context.getRecordText().length() < 151){
            msg = "Length of record  is less than the prescribed format.";
        }
        if (context.getRecordText().length() > 151){
            msg = "Length of record  is greater than the prescribed format.";
        }

        if(context.getFieldText("accountProductCode").length() < 4){
            msg = "Length of record  is greater than the prescribed format.";
        }
        if(context.getFieldText("availableBalance").length() < 20){
            msg = "Account Balance not Present in Record.";
        }

        if(!context.getFieldText("availableBalance").matches("\\d{20}")){
            msg = "Account Bal contains special character(s).";
        }

        if(!context.getFieldText("accountProductCode").matches("\\d{4}")){
            msg = "Account Product Code contains special character(s).";
        }

        return msg;
    }

    private PaymentHub getUpdateObj(List<PaymentHub> list,ResultSet rs) throws SQLException {
        for (PaymentHub obj : list) {

            if (Long.valueOf(obj.getAcctNumber()).equals(rs.getLong(SqlField.AcctNumber))
                    && !(Integer.valueOf(obj.getAccountProductCode()).equals(rs.getInt(SqlField.AccountProductCode)))) {
                return obj;
            }
        }
        return null;
    }
    //        List<PaymentHub> updateList = paymentHubFiles.stream()
//                            .filter(p -> (Long.valueOf(p.getAcctNumber())
//                                    .equals(accountNumber)) &&
//                                    !(Integer.valueOf(p.getAccountProductCode())
//                                            .equals(accountProductCode)))
//                            .collect(Collectors.toList());
}