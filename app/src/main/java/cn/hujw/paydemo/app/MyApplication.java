package cn.hujw.paydemo.app;

import android.app.Application;
import android.content.Context;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.hujw.paydemo.common.Constant;

public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        //第二种方式注册
        //initSdk();
    }

    private void initSdk() {
        //注册微信
        IWXAPI msgApi = WXAPIFactory.createWXAPI(mContext, Constant.WX_PAY_APP_ID);
        // 将该app id 注册到微信   AppID: 申请到的AppID
        msgApi.registerApp(Constant.WX_PAY_APP_ID);

    }

    public static Context getContext() {
        return mContext;
    }

}
