package com.zyy.rob.robredpackage.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zyy.rob.robredpackage.MyApplication;
import com.zyy.rob.robredpackage.R;
import com.zyy.rob.robredpackage.tools.DeleteWordDialog;
import com.zyy.rob.robredpackage.tools.LogUtils;
import com.zyy.rob.robredpackage.tools.PrefsUtils;

import java.util.ArrayList;
import java.util.List;

public class AutoNextLineLayout extends ViewGroup implements View.OnLongClickListener {
	private int viewMargin = 12;

	private static final String TAG = "AutoNextLineLayout";
	private Context context;

	private int textSize;
	private static final int DEFAULT_SIZE = 16;

    private int type = 0;//0过滤词，1抢红包后自动回复

	public AutoNextLineLayout(Context context) {
		super(context);
		this.context = context;
	}

	public AutoNextLineLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		
		 TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextViewItem, defStyle, 0);
		 textSize = a.getDimensionPixelSize(0, DEFAULT_SIZE);
	     a.recycle();
	}

	public AutoNextLineLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.context = context;
	}

    public void setType(int type){
        this.type = type;
    }


	@Override
	public boolean onLongClick(final View v) {
		if(v instanceof TextView){
			TextView textView = (TextView) v;
			final String content = textView.getText().toString();
			DeleteWordDialog dialog = new DeleteWordDialog(context, content, new AlertDialog.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeView(v);
                    Toast.makeText(context, "Delete "+content, Toast.LENGTH_SHORT).show();
                    if(type == 0) {
                        MyApplication.getInstance().filters.remove(content);
                        PrefsUtils.getInstance().saveStringSetByKey(PrefsUtils.KEY_FILTER_WORDS, MyApplication.getInstance().filters);
                    }else if(type == 1){
                        MyApplication.getInstance().replys.remove(content);
                        PrefsUtils.getInstance().saveStringSetByKey(PrefsUtils.KEY_REPLY_WORDS, MyApplication.getInstance().replys);
                    }
				}
			});
			dialog.show();
		}

		return false;
	}

	public void setViewMargin(int viewMargin) {
		this.viewMargin = viewMargin;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
		final int count = getChildCount();
		View child = null;
		int row = 0;
		int width;
		int height;
		int lengthX = getPaddingLeft();
		int lengthY = getPaddingTop();
		for (int index = 0; index < count; index++) {
			child = getChildAt(index);
			// measure
			if (child.getVisibility() != View.GONE) {
				child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
				width = child.getMeasuredWidth();
				height = child.getMeasuredHeight();
				lengthX += width + viewMargin;
				lengthY = row * (height + viewMargin) + viewMargin + height
						+ getPaddingTop();

				/*
				 * if it can't drawing on a same line , skip to next line
				 */
				if (lengthX > maxWidth - getPaddingRight()) {
					lengthX = width + viewMargin + getPaddingLeft();
					row++;
				}
				lengthY = row * (height + viewMargin) + viewMargin + height
						+ getPaddingTop();
			}
			LogUtils.d(TAG, "onMeasure lengthY: " + lengthY);
		}

		setMeasuredDimension(maxWidth, lengthY + getPaddingBottom());
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		final int count = getChildCount();
		int row = 0;// which row lay you view relative to parent
		int lengthX = getPaddingLeft(); // right position of child relative to
										// parent
		int lengthY = getPaddingTop(); // bottom position of child relative to
										// parent
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			lengthX += width + viewMargin;
			lengthY = row * (height + viewMargin) + viewMargin + height
					+ getPaddingTop();
			// if it can't drawing on a same line , skip to next line
			if (lengthX > getWidth() - getPaddingRight()) {
				lengthX = width + viewMargin + getPaddingLeft();
				row++;
				lengthY = row * (height + viewMargin) + viewMargin + height
						+ getPaddingTop();
			}

			child.layout(lengthX - width, lengthY - height - viewMargin/2, lengthX, lengthY - viewMargin/2);
		}
	}

	public void addChildList(List<String> views) {
		this.removeAllViews();
		for (String content : views) {
			View view = buildTextView(content);
			addView(view, content);
		}
	}

	public void addChild(String content){
		View view = buildTextView(content);
		addView(view, content);
	}

	private void addView(View view, String content){
		if(stringList.contains(content)) return;
		stringList.add(content);
        if(type == 0){
            PrefsUtils.getInstance().saveStringSetByKey(PrefsUtils.KEY_FILTER_WORDS, stringList);
            MyApplication.getInstance().filters = stringList;
        }else if(type == 1){
            PrefsUtils.getInstance().saveStringSetByKey(PrefsUtils.KEY_REPLY_WORDS, stringList);
            MyApplication.getInstance().replys = stringList;
        }

		this.addView(view);
	}

	List<String> stringList = new ArrayList<>();//保存所有内容


	private TextView buildTextView(String content) {

		TextView textview = new TextView(context);
		textview.setTextSize(textSize);
		textview.setText(content);
		textview.setSingleLine(true);
		textview.setPadding(textSize * 3, textSize, textSize * 3, textSize);
		textview.setTextColor(Color.parseColor("#999999"));
		textview.setBackgroundResource(R.drawable.selector_corner17);
		textview.setTag(content);
		textview.setOnLongClickListener(this);
		return textview;
	}
}
