package util;

import field.PaymentHub;
import gen.PaymentHubFileWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Util {

    final static Logger logger = Logger.getLogger(PaymentHubFileWriter.class);

    public static String bigDecToStr(String format, BigDecimal value){

        if (value == null){
            value = BigDecimal.ZERO;
        }
        //remove .
        String val[] = String.valueOf(value).split("\\.");
        String valstr = val.length > 1?(val[0]+val[1]):val[0]+"00";
        //fill zero by regex
        String result = String.format(format, new BigInteger(valstr));

        return result;
    }

    public static BigDecimal strToBigDec(String value){

        if (StringUtils.isEmpty(value)){
            return BigDecimal.ZERO.setScale(2);
        }else {
            //remove zero
            value = value.substring(0,value.length()-2) + "." + value.substring(value.length()-2, value.length());
            //to big
            BigDecimal result = new BigDecimal(value).setScale(2);
            return result;
        }
    }

    public static PaymentHub convertRsToPH(ResultSet rs) throws SQLException {
        String acctNumber = String.format("%010d",rs.getLong(Constant.SqlField.AcctNumber));

        PaymentHub paymentHub = new PaymentHub();
        paymentHub.setRecordIndentifer("D");
        paymentHub.setAcctCtl1(String.format("%04d", 11));
        paymentHub.setAcctCtl2(String.format("%04d",1));
//                    rs.getString("ACCT_CRNCY")
        paymentHub.setAcctCtl3(String.format("%04d", rs.getInt(Constant.SqlField.AcctCtl3)));

        int acctCtl4 = 0;
        switch (acctNumber.substring(3,4)){
            case "2":
            case "7":
            case "9":
                acctCtl4 = 200;
                break;
        }
        paymentHub.setAcctCtl4(String.format("%04d",acctCtl4));
//        logger.debug(acctNumber+" type "+acctNumber.substring(3,4)+" is ACCT_CTL4 = "+paymentHub.getAcctCtl4());

        paymentHub.setAcctNumber(acctNumber);


        paymentHub.setAccountProductCode(String.format("%04d",rs.getInt(Constant.SqlField.AccountProductCode)));

        paymentHub.setAvailableBalance(Util.bigDecToStr("%020d",
                rs.getBigDecimal(Constant.SqlField.AvailableBalance)));
//                    paymentHub.setAdditional2(rs.getString("ADDITIONAL2"));
//                    paymentHub.setAdditional3(rs.getString("ADDITIONAL3"));
//                    paymentHub.setAdditional4(rs.getString("ADDITIONAL4"));
//                    paymentHub.setAdditional5(rs.getString("ADDITIONAL5"));
//                    paymentHub.setAdditional6(rs.getString("ADDITIONAL6"));

        // write an Employee object directly to the BeanWriter



        return paymentHub;
    }

    public static String[] getSelectFields(String sql){

        String sub = sql.substring(sql.indexOf("SELECT"),sql.indexOf("FROM"))
                .replace("SELECT","").trim();

        String[] ss = sub.split("\\s*,\\s*");

//        for (int i=0;i<ss.length;i++){
//            System.out.println("index : "+i+" val : "+ss[i]);
//        }

        return ss;
    }


    //Test
    public static void main(String[] args) throws Exception{
        String u = bigDecToStr("%020d",new BigDecimal("350"));
        System.out.println(u);
        BigDecimal d = strToBigDec(u);
        System.out.println(d);
    }

}
