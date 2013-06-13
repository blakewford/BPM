package org.starlo.bytepusher;

import coder36.BytePusherIODriver;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnTouchListener;

public class AndroidBytePusherIODriverImpl implements BytePusherIODriver, OnTouchListener {

	private short mKeyState;
	
	AndroidBytePusherIODriverImpl(){
		mKeyState = 0x0000;
	}
	
	@Override
	public short getKeyPress() {
		return mKeyState;
	}

	@Override
	public void renderAudioFrame(char[] data) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void renderDisplayFrame(char[] data) {

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int keyFlag = (1<<Integer.valueOf(((TextView)view).getText().toString(), 16));
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				mKeyState = (short)(mKeyState|keyFlag);	
				break;
			case MotionEvent.ACTION_UP:
				mKeyState = (short)(mKeyState^keyFlag);
				break;
		}
		return false;
	}
}
