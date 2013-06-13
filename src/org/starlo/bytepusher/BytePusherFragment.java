package org.starlo.bytepusher;

import org.starlo.bytepusher.R;

import coder36.BytePusherVM;
import android.os.*;
import android.view.*;
import android.app.Fragment;
import java.util.*;

public class BytePusherFragment extends Fragment{
	
	public final static int CLOCK_FREQUENCY = 60;
	
	private AndroidBytePusherIODriverImpl mDriver;
	private AndroidBytePusherRuntimeAbstractionImpl mAbstraction;
	private Compiler mCompiler;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.display_layout, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		SurfaceView surface = (SurfaceView) view.findViewById(R.id.display);
		mDriver = new AndroidBytePusherIODriverImpl();
		mAbstraction = new AndroidBytePusherRuntimeAbstractionImpl(getActivity(), surface, new BytePusherVM(mDriver));
		new Timer().schedule(mAbstraction.getFrameTask(), 0, 1000/CLOCK_FREQUENCY);
		final int[] keyIds = 
			new int[]{
				R.id.key_0, R.id.key_1, R.id.key_2, R.id.key_3,
				R.id.key_4, R.id.key_5, R.id.key_6, R.id.key_7,
				R.id.key_8, R.id.key_9, R.id.key_A, R.id.key_B,
				R.id.key_C, R.id.key_D, R.id.key_E, R.id.key_F
		};
		
		for(int id: keyIds)
			view.findViewById(id).setOnTouchListener(mDriver);
	}
	
	public void setCurrentSourceFile(String sourceFilePath){
		mCompiler = new Compiler(sourceFilePath, new Handler(mAbstraction));
	}
	
	public Compiler getCompiler(){
		return mCompiler;
	}
}
