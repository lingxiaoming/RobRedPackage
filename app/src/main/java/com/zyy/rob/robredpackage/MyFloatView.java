package com.zyy.rob.robredpackage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyFloatView extends LinearLayout {
    private final String TAG = MyFloatView.class.getSimpleName();
    public static int TOOL_BAR_HIGH = 0;
    public static WindowManager.LayoutParams params = new WindowManager.LayoutParams();
    private float startX;
    private float startY;
    private float x;
    private float y;
    private float float1 = 0.0f;
    private float float2 = 0.01f;
    WindowManager wm = (WindowManager) getContext().getApplicationContext()
            .getSystemService(Context.WINDOW_SERVICE);

    public MyFloatView(Context context) {
        super(context);
        this.setBackgroundColor(Color.argb(100, 140, 160, 150));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 触摸点相对于屏幕左上角坐标
        x = event.getRawX();
        y = event.getRawY() - TOOL_BAR_HIGH;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                updatePosition();
                break;
            case MotionEvent.ACTION_UP:
                updatePosition();
                startX = startY = 0;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float1 += 0.001f;
        float2 += 0.001f;
        if (float2 > 1.0) {
            float1 = 0.0f;
            float2 = 0.01f;
        }
    }

    /**
     * 更新浮动窗口位置参数
     */
    private void updatePosition() {
        // View的当前位置
        params.x = (int) (x - startX);
        params.y = (int) (y - startY);
        wm.updateViewLayout(this, params);
    }
}
