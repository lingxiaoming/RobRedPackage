package com.zyy.rob.robredpackage.tools.alipay;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author xiazehong
 * @Class RechargeInfo
 * @Description 充值信息
 * @date 2015年6月10日 下午3:56:14
 */
public class RechargeInfo implements Parcelable {
    //    "order_id": "U20150610142916100001",
    //    "title": "用户充值",
    //    "desc": "用户充值",
    //    "amount": "1",
    //    "return_url": "http://localhost/DefaultWorkspace10/funchat_test/public/user/login",
    //    "alipay_pid": "2088211165663450",
    //    "alipay_account": "1507689613@qq.com",
    //    "alipay_public_key": "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDKNaZdW5/VMLOdj50K/dXUHqlLUTbM1PM7oX7qcBVMIk6D3C+2rWuQ8c4JbFqj0EEBaEXoT3gKRqjTDhZ+XL30LhoK9JJsAbbzNiqcb0WMrE/neO72KtS2yjtACOs86PHUblmZqjxiTBYnU7w6T8krU1sm9MuQ4RCtWNCKhSsJiQIDAQAB",
    //    "alipay_private_key": "MIICXQIBAAKBgQDKNaZdW5/VMLOdj50K/dXUHqlLUTbM1PM7oX7qcBVMIk6D3C+2rWuQ8c4JbFqj0EEBaEXoT3gKRqjTDhZ+XL30LhoK9JJsAbbzNiqcb0WMrE/neO72KtS2yjtACOs86PHUblmZqjxiTBYnU7w6T8krU1sm9MuQ4RCtWNCKhSsJiQIDAQABAoGBAIGjI6V2uGHVd1HsYKJLWmtDvQWsa5WmcMoqH1Ptx7mnxO6Em5JiXDPw0kb4UKP1P7fHQgpN6IeuBU5KGeGpU520pme+BXYA2wtXHhB8jrTILaJeVH2H6eH61teXNTLmvQgD0rDJlI/cOrJ1Hee1WaxCKYlKbQDPxMW+UTGMJtiBAkEA6BOfksJKDLXOXS7lz+KBRjDPYprA7Xo+V/HMPnPQl/9qbsEtXYVjC0jLeYb2y0lz3byf2gysizvg7Ag2wtVemwJBAN8N2bXEHgTjbwWTKUy5HfoXbQXP2KY0MghD4Xmra1231QiYr/pnSv7WPPNCipDGF5tPsFEzzraBBh5DdCQQCKsCQCIxgflllvN4dgdUuZd/j/x/hI7KrlJPCJB8l9M7zSgYAd5/p+d3l7g56YDmWcZp9CBfgk3mQCXjlKgyLZ9XQLECQG9resIKSjiE53NwdGPn4KqJwyLxFTbIwelRBzOAxmwVJSxbISTDLjooCGA3cAIrgaVKZDfb2jleMBEz/rL482sCQQCP6dd7EMXg7L5DSpsKWVi36tyU4O795DLQ84mXgehMfcoz0ko6/PWHsE3ZTbYfXhhjL3oUWmMShz77b7Q575fl"

    public boolean isAlipay;

    //begin：支付宝支付
    /**
     * 订单id
     */
    private String order_id;
    /**
     * 商品名称
     */
    private String title;
    /**
     * 商品描述
     */
    private String desc;
    /**
     * 充值金额
     */
    private String amount;
    /**
     * 服务器异步通知
     */
    private String return_url;
    /**
     * 商家id
     */
    private String alipay_pid;
    /**
     * 支付宝账户
     */
    private String alipay_account;
    /**
     * 商家公钥
     */
    private String alipay_public_key;
    /**
     * 商家私钥
     */
    private String alipay_private_key;
    //end：支付宝支付

    //begin：微信支付
    /**
     * 老外趣聊appid
     */
    public String wx_app_id;
    /**
     * 微信支付商户号
     */
    public String wx_mch_account;
    /**
     * 商品名称
     */
    private String wx_trade_title;
    /**
     * 商户订单号
     */
    private String wx_out_trade_no;
    /**
     * 充值金额（单位：分）
     */
    private String wx_total_fee;
    /**
     * 服务器异步通知
     */
    private String wx_notify_url;
    /**
     * API密钥
     */
    private String wx_api_key;

    //end：微信支付

    /**
     * @return the order_id
     */

    public String getOrder_id() {
        return order_id;
    }

    //begin：支付宝支付

    /**
     * @param order_id the order_id to set
     */

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getAlipay_pid() {
        return alipay_pid;
    }

    public void setAlipay_pid(String alipay_pid) {
        this.alipay_pid = alipay_pid;
    }

    public String getAlipay_account() {
        return alipay_account;
    }

    public void setAlipay_account(String alipay_account) {
        this.alipay_account = alipay_account;
    }

    public String getAlipay_public_key() {
        return alipay_public_key;
    }

    public void setAlipay_public_key(String alipay_public_key) {
        this.alipay_public_key = alipay_public_key;
    }

    public String getAlipay_private_key() {
        return alipay_private_key;
    }

    public void setAlipay_private_key(String alipay_private_key) {
        this.alipay_private_key = alipay_private_key;
    }

    //end：支付宝支付

    //begin：微信支付
    public String getWx_app_id() {
        return wx_app_id;
    }

    public void setWx_app_id(String wx_app_id) {
        this.wx_app_id = wx_app_id;
    }

    public String getWx_mch_account() {
        return wx_mch_account;
    }

    public void setWx_mch_account(String wx_mch_account) {
        this.wx_mch_account = wx_mch_account;
    }

    public String getWx_trade_title() {
        return wx_trade_title;
    }

    public void setWx_trade_title(String wx_trade_title) {
        this.wx_trade_title = wx_trade_title;
    }

    public String getWx_out_trade_no() {
        return wx_out_trade_no;
    }

    public void setWx_out_trade_no(String wx_out_trade_no) {
        this.wx_out_trade_no = wx_out_trade_no;
    }

    public String getWx_total_fee() {
        return wx_total_fee;
    }

    public void setWx_total_fee(String wx_total_fee) {
        this.wx_total_fee = wx_total_fee;
    }

    public String getWx_notify_url() {
        return wx_notify_url;
    }

    public void setWx_notify_url(String wx_notify_url) {
        this.wx_notify_url = wx_notify_url;
    }

    public String getWx_api_key() {
        return wx_api_key;
    }

    public void setWx_api_key(String wx_api_key) {
        this.wx_api_key = wx_api_key;
    }
    //end：微信支付


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(isAlipay ? (byte) 1 : (byte) 0);
        dest.writeString(this.order_id);
        dest.writeString(this.title);
        dest.writeString(this.desc);
        dest.writeString(this.amount);
        dest.writeString(this.return_url);
        dest.writeString(this.alipay_pid);
        dest.writeString(this.alipay_account);
        dest.writeString(this.alipay_public_key);
        dest.writeString(this.alipay_private_key);
        dest.writeString(this.wx_app_id);
        dest.writeString(this.wx_mch_account);
        dest.writeString(this.wx_trade_title);
        dest.writeString(this.wx_out_trade_no);
        dest.writeString(this.wx_total_fee);
        dest.writeString(this.wx_notify_url);
        dest.writeString(this.wx_api_key);
    }

    public RechargeInfo() {
    }

    protected RechargeInfo(Parcel in) {
        this.isAlipay = in.readByte() != 0;
        this.order_id = in.readString();
        this.title = in.readString();
        this.desc = in.readString();
        this.amount = in.readString();
        this.return_url = in.readString();
        this.alipay_pid = in.readString();
        this.alipay_account = in.readString();
        this.alipay_public_key = in.readString();
        this.alipay_private_key = in.readString();
        this.wx_app_id = in.readString();
        this.wx_mch_account = in.readString();
        this.wx_trade_title = in.readString();
        this.wx_out_trade_no = in.readString();
        this.wx_total_fee = in.readString();
        this.wx_notify_url = in.readString();
        this.wx_api_key = in.readString();
    }

    public static final Creator<RechargeInfo> CREATOR = new Creator<RechargeInfo>() {
        public RechargeInfo createFromParcel(Parcel source) {
            return new RechargeInfo(source);
        }

        public RechargeInfo[] newArray(int size) {
            return new RechargeInfo[size];
        }
    };
}
