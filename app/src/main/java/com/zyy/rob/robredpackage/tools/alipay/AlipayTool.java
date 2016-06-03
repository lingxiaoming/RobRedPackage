package com.zyy.rob.robredpackage.tools.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.zyy.rob.robredpackage.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AlipayTool {
    //商户PID
    //public static final String PARTNER        = "2088211165663450";
    //商户收款账号
    //public static final String SELLER         = "1507689613@qq.com";
    //商户私钥，pkcs8格式
    //public static final String RSA_PRIVATE    = "";
    //支付宝公钥
    //public static final String RSA_PUBLIC     = "";

    public static final String TAG = "AlipayTool";

    //自定义支付状态
    public static final int PAY_SUCCESS = 1;
    public static final int PAY_COMFIRMING = 2;
    public static final int PAY_FAIL = 3;
    public static final int PAY_UNKNOW = 4;

    public static final int SDK_PAY_FLAG = 1;
    public static final int SDK_CHECK_FLAG = 2;

    private Activity mActivity;
    private PayResultListner mPayResultListner;

    public interface PayResultListner {
        /**
         * @param result: 1支付成功; 2支付结果待确认; 3支持失败; 4检查结果
         * @return void
         * @function onResult
         * @Description
         * @author xiazehong
         * @date 2015年7月24日下午4:31:37
         */
        void onResult(int result, String msg);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AlipayTool.SDK_PAY_FLAG: {
                    if (null == msg.obj) {
                        mPayResultListner.onResult(PAY_FAIL, mActivity.getString(R.string.PayFail));
                        return;
                    }
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    //  String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        if (null != mPayResultListner) {
                            mPayResultListner.onResult(PAY_SUCCESS, mActivity.getString(R.string.PaySuccess));
                        }
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            if (null != mPayResultListner) {
                                mPayResultListner.onResult(PAY_COMFIRMING, mActivity.getString(R.string.payChecking));
                            }
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            if (null != mPayResultListner) {
                                mPayResultListner.onResult(PAY_FAIL, mActivity.getString(R.string.PayFail));
                            }
                        }
                    }
                    break;
                }
                case AlipayTool.SDK_CHECK_FLAG: {
                    if (null != mPayResultListner) {
                        mPayResultListner.onResult(PAY_UNKNOW, mActivity.getString(R.string.checkResult) + msg.obj);
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /**
     * @return AlipayTool
     * @function newInstance
     * @Description 必须在“主线程”中调用
     * @author xiazehong
     * @date 2015年7月24日下午4:46:23
     */
    public static AlipayTool newInstance() {
        return new AlipayTool();
    }

    /**
     * @param rechargeInfo
     * @return void
     * @function pay
     * @Description 调用SDK支付, 必须在“主线程”中调用
     * @author xiazehong
     * @date 2015年6月10日下午5:48:40
     */
    public PayCommonResult pay(RechargeInfo rechargeInfo, Activity activity, PayResultListner payResultListner) {
        PayCommonResult payResult = new PayCommonResult();
        UnUIThreadCalledException.checkUIThread();
        if (null == rechargeInfo || null == activity || null == payResultListner) {
            Log.e(TAG, "rechargeInfo...parameter is null");
            payResult.resultCode = PayCommonResult.RESULT_PARAM_ERROR;
            payResult.reasonDes = "parameter is null";
            return payResult;
        }

        String errorReason = null;
        if (null == rechargeInfo.getAlipay_account()) {
            errorReason = "Alipay_account can not be empty";
        } else if (null == rechargeInfo.getAlipay_pid()) {
            errorReason = "Alipay_pid can not be empty";
        } else if (null == rechargeInfo.getAlipay_private_key()) {
            errorReason = "Alipay_private_key can not be empty";
        } else if (null == rechargeInfo.getAlipay_public_key()) {
            errorReason = "Alipay_public_key can not be empty";
        } else if (null == rechargeInfo.getAmount()) {
            errorReason = "Amount can not be empty";
        } else if (null == rechargeInfo.getDesc()) {
            errorReason = "Desc can not be empty";
        } else if (null == rechargeInfo.getOrder_id()) {
//            errorReason = "Order_id can not be empty";
        } else if (null == rechargeInfo.getReturn_url()) {
            errorReason = "Return_url can not be empty";
        } else if (null == rechargeInfo.getTitle()) {
            errorReason = "Title can not be empty";
        }

        if (!TextUtils.isEmpty(errorReason)) {
            payResult.resultCode = PayCommonResult.RESULT_PARAM_ERROR;
            payResult.reasonDes = errorReason;
            return payResult;
        }

        mPayResultListner = payResultListner;
        mActivity = activity;
        // 生成订单信息
        String orderInfo = getOrderInfo(rechargeInfo);

        // 订单做RSA签名
        String sign = sign(orderInfo, rechargeInfo.getAlipay_private_key());
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        // 必须异步调用
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mActivity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }).start();

        payResult.resultCode = PayCommonResult.RESULT_OK;
        payResult.reasonDes = "pay ok";
        return payResult;
    }

    /**
     * @return void
     * @function check
     * @Description check whether the device has authentication alipay account. 查询终端设备是否存在支付宝认证账户
     * @author xiazehong
     * @date 2015年6月10日下午6:22:12
     */
    public void check() {
        Runnable checkRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(mActivity);
                // 调用查询接口，获取查询结果
//                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
//                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();
    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(mActivity);
        String version = payTask.getVersion();
        Toast.makeText(mActivity, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param rechargeInfo
     * @return String
     * @function getOrderInfo
     * @Description create the order info. 创建订单信息
     * @author xiazehong
     * @date 2015年6月10日下午6:21:48
     */
    public String getOrderInfo(RechargeInfo rechargeInfo) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + rechargeInfo.getAlipay_pid() + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + rechargeInfo.getAlipay_account() + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + rechargeInfo.getTitle() + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + rechargeInfo.getDesc() + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + rechargeInfo.getAmount() + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + rechargeInfo.getReturn_url() + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    public String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 20);
        return key;
    }

    /**
     * @param orderInfo  待签名订单信息
     * @param RSAPrivate 私钥
     * @return String
     * @function sign
     * @Description sign the order info. 对订单信息进行签名
     * @author xiazehong
     * @date 2015年6月10日下午6:20:53
     */
    public String sign(String orderInfo, String RSAPrivate) {
        return SignUtils.sign(orderInfo, RSAPrivate);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

}
