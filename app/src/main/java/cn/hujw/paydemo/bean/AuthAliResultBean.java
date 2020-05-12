package cn.hujw.paydemo.bean;

/**
 * author : hujw
 * date : 2020/5/12
 * describe:
 */
public class AuthAliResultBean {

    private AuthResult mAuthResult;
    private String authid;

    public AuthResult getAuthResult() {
        return mAuthResult;
    }

    public void setAuthResult(AuthResult authResult) {
        mAuthResult = authResult;
    }

    public String getAuthid() {
        return authid;
    }

    public void setAuthid(String authid) {
        this.authid = authid;
    }
}
