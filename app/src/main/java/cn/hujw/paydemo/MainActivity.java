package cn.hujw.paydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.hujw.paydemo.bean.AliPayBean;
import cn.hujw.paydemo.bean.WxPayBean;
import cn.hujw.paydemo.utils.PayUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PayUtils.AliPayResultListener {

    private PayUtils mPayUtils;

    private Button mWechatPayView;
    private Button mAliPayView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();

        initData();

        setListener();

    }

    private void initData() {
        mPayUtils = new PayUtils(this);
    }

    private void setListener() {
        mWechatPayView.setOnClickListener(this);
        mAliPayView.setOnClickListener(this);

    }

    private void initView() {
        mWechatPayView = findViewById(R.id.btn_wechat_pay);
        mAliPayView = findViewById(R.id.btn_ali_pay);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wechat_pay:
                mPayUtils.wechatPay(new WxPayBean());
                break;
            case R.id.btn_ali_pay:
                Log.e("TAG","ali pay button");
                mPayUtils.aliPay(new AliPayBean());
                break;
        }
    }

    @Override
    public void aliPaySuccess() {
        Toast.makeText(this,"支付成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void aliPayCancel() {
        Toast.makeText(this,"取消支付",Toast.LENGTH_SHORT).show();
    }
}
