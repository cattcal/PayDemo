package cn.hujw.paydemo.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.hujw.paydemo.bean.PayResult;
import cn.hujw.paydemo.R;
import cn.hujw.paydemo.bean.AliPayBean;
import cn.hujw.paydemo.bean.PayAliResultBean;
import cn.hujw.paydemo.bean.WxPayBean;
import cn.hujw.paydemo.common.Constant;

public class PayUtils {

    private static final int SDK_PAY_FLAG = 1;

    private Activity activity;

    public PayUtils(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayAliResultBean payAliResultBean = (PayAliResultBean) msg.obj;

                    PayResult payResult = payAliResultBean.getPayResult();
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus	 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) { //9000 订单支付成功
                        if (null != mAliPayResultListener) {
                            mAliPayResultListener.aliPaySuccess();
                        }
                    } else if (TextUtils.equals(resultStatus, "6001")) { //用户中途取消
                        if (null != mAliPayResultListener) {
                            mAliPayResultListener.aliPayCancel();
                        }
                    } else {
                        Toast.makeText(activity, R.string.pay_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    };

    /**
     * 使用支付宝支付
     * @param aliPayBean
     */
    public void aliPay(final AliPayBean aliPayBean) {
        // 需要处理后台服务器返回的数据信息 后台返回的ali 信息
        if (TextUtils.isEmpty(aliPayBean.getOrderid())) return;

        if (TextUtils.isEmpty(Constant.ALI_APPID)) {
            showAlert(activity, activity.getString(R.string.error_missing_appid_rsa_private));
            return;
        }

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(aliPayBean.getOrderstring(), true);
                Log.i("msp", result.toString());


                PayAliResultBean payAliResultBean = new PayAliResultBean();
                payAliResultBean.setPayResult(new PayResult(result));
                payAliResultBean.setOrderid(aliPayBean.getOrderid());

                try {
                    JSONObject jsonObject = new JSONObject(payAliResultBean.getPayResult().getResult());
                    Log.d("PayUtils", "支付宝返回结果" + jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = payAliResultBean;
                mHandler.sendMessage(msg);

            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    /**
     * 微信支付
     * <p>
     * 注意： 每次调用微信支付的时候都会校验 appid 、包名 和 应用签名的。 这三个必须保持一致才能够成功调起微信
     *
     * @param wxPayBean
     */
    public void wechatPay(WxPayBean wxPayBean) {

        //这里的appid，替换成自己的即可
        IWXAPI api = WXAPIFactory.createWXAPI(activity, Constant.WX_PAY_APP_ID);
        api.registerApp(Constant.WX_PAY_APP_ID);

        //这里的bean，是服务器返回的json生成的bean
        PayReq payRequest = new PayReq();
        payRequest.appId = Constant.WX_PAY_APP_ID;
        payRequest.partnerId = wxPayBean.getPartnerid();
        payRequest.prepayId = wxPayBean.getPrepayid();
        payRequest.packageValue = "Sign=WXPay";//固定值
        payRequest.nonceStr = wxPayBean.getNoncestr();
        payRequest.timeStamp = wxPayBean.getTimestamp();
        payRequest.sign = wxPayBean.getSign();

        //发起请求，调起微信前去支付
        api.sendReq(payRequest);
    }

    /**
     * 判断微信是否安装
     * @param context
     * @return true 已安装   false 未安装
     */
    public  static boolean isWxAppInstalled(Context context) {
        IWXAPI wxApi = WXAPIFactory.createWXAPI(context, null);
        wxApi.registerApp(Constant.WX_PAY_APP_ID);
        boolean bIsWXAppInstalled = false;
        bIsWXAppInstalled = wxApi.isWXAppInstalled();
        return bIsWXAppInstalled;
    }

    /**
     * 阿里支付接口回调
     */
    public interface AliPayResultListener {
        /**
         * 完成支付
         */
        void aliPaySuccess();

        /**
         * 取消支付
         */
        void aliPayCancel();
    }

    private AliPayResultListener mAliPayResultListener;

    public AliPayResultListener getAliPayResultListener() {
        return mAliPayResultListener;
    }

    public void setAliPayResultListener(AliPayResultListener aliPayResultListener) {
        mAliPayResultListener = aliPayResultListener;
    }

    public void release() {
        mHandler.removeCallbacksAndMessages(null);
    }


    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton(R.string.confirm, null)
                .setOnDismissListener(onDismiss)
                .show();
    }
}
