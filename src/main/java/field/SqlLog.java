package field;

import java.util.Date;

public class SqlLog {

    public SqlLog() {
        this.updateDate = new Date();
    }

    private Date updateDate;
    private String acctNumber;
    private String accountProductCode;
    private String availableBalance;
    private String updateAccountProductCode;
    private String updateAvailableBalance;
    private String status;

    public String getAcctNumber() {
        return acctNumber;
    }

    public void setAcctNumber(String acctNumber) {
        this.acctNumber = acctNumber;
    }

    public String getAccountProductCode() {
        return accountProductCode;
    }

    public void setAccountProductCode(String accountProductCode) {
        this.accountProductCode = accountProductCode;
    }

    public String getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    public String getUpdateAccountProductCode() {
        return updateAccountProductCode;
    }

    public void setUpdateAccountProductCode(String updateAccountProductCode) {
        this.updateAccountProductCode = updateAccountProductCode;
    }

    public String getUpdateAvailableBalance() {
        return updateAvailableBalance;
    }

    public void setUpdateAvailableBalance(String updateAvailableBalance) {
        this.updateAvailableBalance = updateAvailableBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
