/** 
 * @Title UnUIThreadCalledException.java 
 * @Package com.base.exception 
 * @Description  
 * @Copyright FeiZhu Technology Co., Ltd. All Right Reserved  
 * @author xiazehong
 * @date 2015年7月27日 上午10:50:05 
 * @version 1.0 
 */
package com.zyy.rob.robredpackage.tools.alipay;

import android.os.Looper;

/** 
 * @Class UnUIThreadCalledException 
 * @Description
 * @author xiazehong 
 * @date 2015年7月27日 上午10:50:05 
 */

public class UnUIThreadCalledException extends RuntimeException
{
    public UnUIThreadCalledException()
    {
        initCause(new Throwable("This method must be called in main(UI) thread"));
    }
    
    /** 
     * @function isInUIThread 
     * @Description 
     * @author xiazehong
     * @date 2015年7月27日上午10:54:14
     * @return void
     */
    public static void checkUIThread()
    {
        if (Looper.myLooper() != Looper.getMainLooper())
        {
            throw new UnUIThreadCalledException();
        }
    }
    
}
