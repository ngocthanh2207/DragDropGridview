package com.example.androidimageviewlist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.LinearLayout;

public class LinearLayoutAbsListView extends LinearLayout {
	
	AbsListView absListView;

	public LinearLayoutAbsListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public LinearLayoutAbsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public LinearLayoutAbsListView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public void setAbsListView(AbsListView alv){
		absListView = alv;
	}

}
