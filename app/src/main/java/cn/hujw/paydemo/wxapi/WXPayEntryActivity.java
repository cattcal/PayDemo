package cn.hujw.paydemo.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.hujw.paydemo.common.Constant;


public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constant.WX_PAY_APP_ID);
        api.handleIntent(getIntent(), this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);

    }

    @Override
    public void onReq(BaseReq req) {

    }

    /**
     * 处理结果的回调
     *
     * @param resp
     */
    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            switch (resp.errCode) {
                case 0://支付成功
                    Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResp: resp.errCode = 0   支付成功");
                    break;
                case -1://错误，可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
                    Toast.makeText(this, "支付错误" + resp.errCode, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResp: resp.errCode = -1  支付错误");
                    break;
                case -2://用户取消，无需处理。发生场景：用户不支付了，点击取消，返回APP。
                    Log.d(TAG, "onResp: resp.errCode = -2  用户取消");
                    Toast.makeText(this, "用户取消" + resp.errCode, Toast.LENGTH_SHORT).show();
                    break;

            }

            finish();//这里需要关闭该页面
        }
    }


}
