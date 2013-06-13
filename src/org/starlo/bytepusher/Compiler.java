package org.starlo.bytepusher;

import android.util.Log;
import android.view.View;
import android.os.*;

public class Compiler
{
	private Handler mHandler;
	private Thread mThread;

    public Compiler(final String filePath, Handler handler)
    {
		mHandler = handler;
		compile(filePath);
    }
	
	public void deployBinary(){
		stop();
        mThread = new Thread(){
			public void run(){
				deploy();
			}
        };
		mThread.start();		
	}
	
    private native void compile(String filePath);
    private native void deploy();
    private native void stop();

    private void MemWrite(int[] value){
		Message msg = new Message();
		msg.obj = value;
		mHandler.sendMessage(msg);
    }

    static {
        System.loadLibrary("sccas09");
    }
}
