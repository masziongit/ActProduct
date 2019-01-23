package util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class Util {

    public static String bigDecToStr(String format, BigDecimal value){

        if (value == null){
            value = BigDecimal.ZERO;
        }
        //remove .
        String valstr = String.valueOf(value).replace(".", "");
        //fill zero by regex
        String result = String.format(format, Integer.valueOf(valstr));

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
        String str = "OACSYN190122.R01";
        String format = "OACSYN[0-9]{6}(\\.)R01";
        System.out.println(str.matches(format));
    }

}
