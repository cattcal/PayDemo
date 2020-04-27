package cn.hujw.paydemo.bean;

public class WxPayBean {
    private String appid;//微信开放平台审核通过的应用APPID
    private String partnerid;//微信支付分配的商户号
    private String prepayid;//微信返回的支付交易会话ID
    //        private String package;// 扩展字段 暂填写固定值Sign=WXPay
    private String noncestr;//随机字符串,随机字符串，不长于32位。推荐随机数生成算法
    private String sign;//签名，详见签名生成算法注意：签名方式一定要与统一下单接口使用的一致
    private String timestamp;//时间戳，请见接口规则-参数规定

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
