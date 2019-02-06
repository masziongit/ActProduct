package util;

public final class Constant {

    //common
    public final class Mode{
        public static final String WRITE = "write";
        public static final String READ = "read";

        public static final char UPLOAD = 'U';
        public static final char DOWNLOAD = 'D';
    }


    //sql field
    public final class SqlField {
        public static final String RecordIndentifer = "";
        public static final String AcctCtl1 = "";
        public static final String AcctCtl2 = "";
        public static final String AcctCtl3 = "branch_code";
        public static final String AcctCtl4 = "";
        public static final String AcctNumber = "account_id";
        public static final String AccountProductCode = "scheme_code";
        public static final String AvailableBalance = "acct_bal_amt";
        public static final String Additional2 = "";
        public static final String Additional3 = "";
        public static final String Additional4 = "";
        public static final String Additional5 = "";
        public static final String Additional6 = "";
    }

    public final class SqlQuery {
//        //sql query
//        public static final String SELECT = "SELECT branch_code, scheme_code, acct_bal_amt, acct_crncy, account_id, scheme_type " +
//                "FROM ext_general_acct_table " +
//                "WHERE bank_id = '011' AND scheme_type != 'OAB' AND LENGTH(account_id) = 10";
//
//        public static final String UPDATE = "UPDATE EXT_GENERAL_ACCT_TABLE " +
//                "SET acct_bal_amt=?,scheme_code=? " +
//                "WHERE account_id =? AND  bank_id ='011'";
    }
}