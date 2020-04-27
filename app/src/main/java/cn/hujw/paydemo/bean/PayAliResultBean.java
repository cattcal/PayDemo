package cn.hujw.paydemo.bean;

public class PayAliResultBean {

    private PayResult mPayResult;
    private String orderid;

    public PayResult getPayResult() {
        return mPayResult;
    }

    public void setPayResult(PayResult payResult) {
        mPayResult = payResult;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }
}
