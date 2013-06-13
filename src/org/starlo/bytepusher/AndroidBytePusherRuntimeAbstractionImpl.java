package org.starlo.bytepusher;

import java.util.TimerTask;

import coder36.BytePusherVM;
import coder36.FrameRate;
import android.content.Context;
import android.os.*;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AndroidBytePusherRuntimeAbstractionImpl implements Handler.Callback {
	
	public final static int VIDEO_MEMORY_BASE_ADDRESS = 0x010000;
	private static final int SCREEN_DIMENSION = 256;
	
	private TimerTask mTask;
	private FrameRate mFrameRate;
	private BytePusherVM mVirtualMachine;
	
	private SurfaceHolder mHolder;
	private Bitmap mBitmap;
	private Bitmap mScaledBitmap;
	private int mScreenWidth;
	
	AndroidBytePusherRuntimeAbstractionImpl(Context context, SurfaceView surfaceView, BytePusherVM virtualMachine){
		mTask = new AndroidFrameTask();
		mFrameRate = new FrameRate();
		mVirtualMachine = virtualMachine;
	    loadMemory();

		Resources r = surfaceView.getResources();
		mHolder = surfaceView.getHolder();
		mBitmap = Bitmap.createBitmap(SCREEN_DIMENSION, SCREEN_DIMENSION, Config.ARGB_8888);
		mScreenWidth = 
			Float.valueOf(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, r.getConfiguration().screenWidthDp, r.getDisplayMetrics())).intValue();
	}

	public TimerTask getFrameTask(){
		return mTask;
	}
	
	public boolean handleMessage (Message msg){
		int[] data = (int[])msg.obj;
		int[] colors = new int[data.length];
		for (int y=0; y < SCREEN_DIMENSION; y++) {
			for (int x=0; x < SCREEN_DIMENSION; x++) {
				int color = getRGBAtCoordinate(data[(y*SCREEN_DIMENSION)+x]);
				colors[(y*SCREEN_DIMENSION)+x] = 0xFF << 24|color;
			}
		}
		if(mScaledBitmap != null)
			mScaledBitmap.recycle();
		Canvas canvas = mHolder.lockCanvas();
		if(canvas != null){
			canvas.drawColor(Color.BLACK);
			mBitmap.setPixels(colors, 0, SCREEN_DIMENSION, 0, 0, SCREEN_DIMENSION, SCREEN_DIMENSION);
			mScaledBitmap = Bitmap.createScaledBitmap(mBitmap, mScreenWidth, mScreenWidth, false);
			canvas.drawBitmap(mScaledBitmap, 0, 0, null);
			mHolder.unlockCanvasAndPost(canvas);
		}

		return true;
	}
	
	private void loadMemory(){
		
		char[] memory = mVirtualMachine.mem;

		memory[0] = 0x00;
		memory[1] = 0x00;
		memory[2] = 0x00;
		memory[3] = 0x05;
		memory[4] = 0x00;
		memory[5] = 0x01;
		memory[6] = 0x00;
		memory[7] = 0x01;
		
		memory[8]    = 0x00;
		memory[9]    = 0x00;
		memory[0xA]  = 0x00;
		memory[0xB]  = 0x00;
		memory[0xC]  = 0x00;
		memory[0xD]  = 0x00;
		memory[0xE]  = 0x00;
		memory[0xF]  = 0x00;
		memory[0x10] = 0x08;
	}
	
	private class AndroidFrameTask extends TimerTask {
		public void run() {
			mFrameRate.update("gpu");
			mVirtualMachine.run();
		}
	}
	
	private int getRGBAtCoordinate(int data){
		int color = 0;
		int datum = Character.toChars(data)[0];
		if ( datum < 216 ) {
			int blue = datum % 6;
			int green = ((datum - blue) / 6) % 6;
			int red = ((datum - blue - (6 * green)) / 36) % 6;
			
			color = (red*0x33 << 16) + (green*0x33 <<8) + (blue*0x33 );
		}
		
		return color;
	}
}
