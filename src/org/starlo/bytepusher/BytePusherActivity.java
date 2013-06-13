package org.starlo.bytepusher;

import org.starlo.bytepusher.R;
import android.os.*;
import android.app.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import android.view.View;

public class BytePusherActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{

	public static final String DEMO_FILE_NAME = "demo.c";
	
	private File mSourceFile;
	private BytePusherFragment mFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push_layout);
		try {
			File file = getLocalFile(DEMO_FILE_NAME);
			
			if (!file.exists())
				copyDefaultSourceFile(file);
			
			mFragment = (BytePusherFragment)getFragmentManager().findFragmentById(R.id.bpm_fragment);
			populateSourceView(file);
			findViewById(R.id.deploy_button).setOnClickListener(this);

			ListView sourceFileList = (ListView)findViewById(R.id.source_file_list);
			sourceFileList.setOnItemClickListener(this);
			sourceFileList.setAdapter(
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getSourceFiles())
			);
		}catch (Exception e) {
			//Do nothing!
		}
	}
	
	@Override
	public void onClick(View view){
		saveSourceFile();
		String sourceFilePath = mSourceFile.getPath();
		mFragment.setCurrentSourceFile(sourceFilePath);
		File binaryFile = new File(sourceFilePath.substring(0, sourceFilePath.length()-1)+"s");
		if(binaryFile.exists()){
			assembleBinary(binaryFile);
			mFragment.getCompiler().deployBinary();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		TextView sourceFileView = (TextView)view;
		File sourceFile = getLocalFile(sourceFileView.getText().toString());
		populateSourceView(sourceFile);
	}
	
	private void assembleBinary(File file){
		Assembler8080.assemble(file);	
	}
	
	private byte[] getFileData(File file){
		byte[] buffer = null;
		try {
			FileInputStream in = new FileInputStream(file);
			int size = in.available();
			buffer = new byte[size];
			in.read(buffer);
			in.close();
		}catch (Exception e) {
			//Do nothing!
		}
		
		return buffer;
	}
	
	private File getLocalFile(String fileName){
		File dir = getFilesDir();
		File file = new File(dir, fileName);
		
		return file;
	}
	
	private void populateSourceView(File file){
		mSourceFile = file;
		TextView code = (TextView)findViewById(R.id.c_code_editor);
		code.setText(new String(getFileData(file)));
		mFragment.setCurrentSourceFile(file.getPath());
	}
	
	private void copyDefaultSourceFile(File destFile){
		try {
			// To load text file
			InputStream input;
			input = getAssets().open(DEMO_FILE_NAME);
			
			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();
			
			FileOutputStream out = new FileOutputStream(destFile);
			out.write(buffer);
			out.close();
		}
		catch (Exception e) {
			//Do nothing!
		}
	}
	
	private void saveSourceFile(){
		try {
			String text = ((TextView)findViewById(R.id.c_code_editor)).getText().toString();
			FileOutputStream out = new FileOutputStream(mSourceFile);
			out.write(text.getBytes());
			out.close();
		}
		catch (Exception e) {
			//Do nothing!
		}
	}
	
	private String[] getSourceFiles(){
		String[] localDataFiles = fileList();
		List<String> sourceFiles = new ArrayList<String>();
		for (String file: localDataFiles) {
			if (file.indexOf(".c") != -1)
				sourceFiles.add(file);
		}
		
		return sourceFiles.toArray(new String[sourceFiles.size()]);		
	}
}
