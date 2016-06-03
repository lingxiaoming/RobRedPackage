package com.zyy.rob.robredpackage.tools.alipay;

/**
 * @Description: 支付结果返回信息
 * @author: jianghongen
 * @date: 2016-01-15 14:12
 */
public class PayCommonResult {
    //成功
    public final static int RESULT_OK           = 0;   
    //客户端未安装
    public final static int RESULT_UNINSTALL    = 1;
    //参数有误
    public final static int RESULT_PARAM_ERROR  =  2;
    //结果码
    public int resultCode;
    //原因描述，客户端可以根据结果码自己定义
    public String  reasonDes;
}
