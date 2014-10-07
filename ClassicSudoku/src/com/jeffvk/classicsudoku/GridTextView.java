package com.jeffvk.classicsudoku;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class GridTextView extends CheckedTextView{

	private int value, row, col, zone;
	
	public GridTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		char temp = this.getText().charAt(0);
		if(temp != '0')
			value = (int)(temp-48);
	}
	
	public void setValue(int val)
	{
		value = val;
		this.setText((char)(value+48));
	}

}
