package com.ithinkrok.yellowquest.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class DisableButton extends Button {

	public DisableButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}

	public DisableButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public DisableButton(Context context) {
		super(context);
		
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		if(!enabled){
			InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.restartInput(this);
		}
	}

}
