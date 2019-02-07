package util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Util {

    public static String bigDecToStr(String format, BigDecimal value){

        if (value == null){
            value = BigDecimal.ZERO;
        }
        //remove .
        String valstr = String.valueOf(value).replace(".", "");
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
        System.out.println("Run other class");
        String output = String.format("OACSYN%05d.R01",19020711);
        System.out.printf(output);
    }

}
