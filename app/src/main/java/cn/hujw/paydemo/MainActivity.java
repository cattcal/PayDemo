package cn.hujw.paydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import cn.hujw.paydemo.bean.AliAuthBean;
import cn.hujw.paydemo.bean.AliPayBean;
import cn.hujw.paydemo.bean.WxPayBean;
import cn.hujw.paydemo.utils.PayUtils;

/**
 * 支付示例
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, PayUtils.AliPayResultListener, PayUtils.AliAuthResultListener {

    private PayUtils mPayUtils;

    private Button mWechatPayView;
    private Button mAliPayView;

    private Button mAliAuthView;
    private Button mGetALiSdkVersionView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化View
        initView();

        //设置监听事件
        setListener();

    }

    /**
     * 设置监听事件
     */
    private void setListener() {
        mWechatPayView.setOnClickListener(this);
        mAliPayView.setOnClickListener(this);
        mAliAuthView.setOnClickListener(this);
        mGetALiSdkVersionView.setOnClickListener(this);

        //支付宝支付回调
        mPayUtils.setAliPayResultListener(this);
        //支付宝授权回调
        mPayUtils.setAliAuthResultListener(this);


    }

    /**
     * 初始化View
     */
    private void initView() {
        mWechatPayView = findViewById(R.id.btn_wechat_pay);
        mAliPayView = findViewById(R.id.btn_ali_pay);
        mAliAuthView = findViewById(R.id.btn_ali_auth);
        mGetALiSdkVersionView = findViewById(R.id.btn_get_ali_sdk_version);

        mPayUtils = new PayUtils(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wechat_pay:
                //调起微信支付
                mPayUtils.wechatPay(new WxPayBean());
                break;
            case R.id.btn_ali_pay:
                //调起支付宝支付
                mPayUtils.aliPay(new AliPayBean());
                break;
            case R.id.btn_ali_auth:
                //调起支付宝授权
                mPayUtils.aliAuth(new AliAuthBean());
                break;
            case R.id.btn_get_ali_sdk_version:
                //获取支付宝SDK版本号
                Toast.makeText(this, mPayUtils.getAliSdkVersion(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void aliPaySuccess() {
        Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void aliPayCancel() {
        Toast.makeText(this, "取消支付", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //避免内存泄露
        mPayUtils.release();
    }

    @Override
    public void aliAuthSuccess() {
        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void aliAuthCancel() {
        Toast.makeText(this, "取消成功", Toast.LENGTH_SHORT).show();
    }
}
