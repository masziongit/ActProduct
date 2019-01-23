package field;

import java.util.Date;

public class PaymentHubFooter {


    private String recordIndentifer;
    private Date processDate;

    public String getRecordIndentifer() {
        return recordIndentifer;
    }

    public void setRecordIndentifer(String recordIndentifer) {
        this.recordIndentifer = recordIndentifer;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }
}