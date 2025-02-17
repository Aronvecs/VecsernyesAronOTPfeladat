package Classes;

import java.util.Date;

public class Payment {
    public String getWebshopID() {
        return WebshopID;
    }

    public String getCostumerId() {
        return CostumerId;
    }

    public String getPayingMethod() {
        return PayingMethod;
    }

    public int getSum() {
        return Sum;
    }

    public String getBankszamla() {
        return bankszamla;
    }

    public String getBankCardNum() {
        return BankCardNum;
    }

    public Date getDate() {
        return date;
    }

    public Payment(String webshopID, String costumerId, String payingMethod, int sum, String bankszamla, String bankCardNum, Date date) {
        WebshopID = webshopID;
        CostumerId = costumerId;
        PayingMethod = payingMethod;
        Sum = sum;
        this.bankszamla = bankszamla;
        BankCardNum = bankCardNum;
        this.date = date;
    }

    private final String WebshopID;
    private final String CostumerId;
    private final String PayingMethod;
    private final int Sum;
    private final String bankszamla;
    private final String BankCardNum;
    private final Date date;

    @Override
    public String toString() {
        return "Payment{" +
                "WebshopID='" + WebshopID + '\'' +
                ", CostumerId='" + CostumerId + '\'' +
                ", PayingMethod='" + PayingMethod + '\'' +
                ", Sum=" + Sum +
                ", bankszamla='" + bankszamla + '\'' +
                ", BankCardNum='" + BankCardNum + '\'' +
                ", date=" + date +
                '}';
    }
}
