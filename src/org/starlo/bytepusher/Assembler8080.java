package org.starlo.bytepusher;

import java.io.*;
import java.util.ArrayList;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

public class Assembler8080 {

	private static ArrayList<Byte> mBinary = null;
	private static String[] mTokens = null;

	public static void assemble(File file){
		try {
			FileInputStream fileStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				if (!line.contains(";")) {
					if(line.length() > 0 && line.charAt(0) == '?')
						buffer.append("\t"+line);
					else
						buffer.append(line);
				}
			}
			String rawString = buffer.toString();
			String finalString = rawString.substring(rawString.indexOf("main:")+5, rawString.indexOf("ret")).trim();
			mTokens = finalString.split("\\s");
			Opcodes8080 opcode = null;
			mBinary = new ArrayList<Byte>();
			for (int i=0; i<mTokens.length-1; i+=2) {
				try{
					opcode = Opcodes8080.valueOf(mTokens[i].toUpperCase(Locale.US));
				}catch(Exception e){
					continue;
				}
				int ordinal = opcode.ordinal();
				if (ordinal <= 8){
					switch(opcode){
						case JMP:
							//singleParamOutput(opcode, Integer.valueOf(mTokens[i+1]));
							break;
						case CALL:
							singleParamOutput(opcode, Platformresources8080.valueOf(mTokens[i+1].substring(1).toUpperCase(Locale.US)).ordinal());
							break;
						default:
							singleParamOutput(opcode, Platformresources8080.valueOf(mTokens[i+1].toUpperCase(Locale.US)).ordinal());
							break;
					}
				}else if (ordinal <= 10) {
					doubleParamOutput(i);
				}
			}
			reader.close();
			writeDebugBinary();
		}catch (Exception e) {
			//Do nothing!
		}
	}
	
	private static void writeDebugBinary(){
		File binaryFile = Environment.getExternalStorageDirectory();
		FileOutputStream binary;
		try {
			binary = new FileOutputStream(binaryFile.getAbsolutePath()+"/binary");
			byte[] bytes = new byte[mBinary.size()];
			for(int i = 0; i < bytes.length; i++)
				bytes[i] = mBinary.get(i);
			binary.write(bytes);
			binary.close();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void singleParamOutput(Opcodes8080 opcode, int resource){
		mBinary.add((byte) opcode.ordinal());
		mBinary.add((byte) resource);
	}
	
	private static void doubleParamOutput(int firstTokenIndex){
		String[] params = mTokens[firstTokenIndex+1].split(",");
		Log.v("Double", mTokens[firstTokenIndex]+" "+params[0]+" "+params[1]);
	}

}
