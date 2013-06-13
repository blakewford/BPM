package org.starlo.bytepusher;

import java.io.*;
import java.util.Locale;

public class Assembler8080 {
	
	private static String[] mTokens = null;

	public static void main(String[] args){
		try {
			FileInputStream fileStream = new FileInputStream("demo.s");
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				if (!line.contains(";")) {
					buffer.append(line);
				}
			}
			String rawString = buffer.toString();
			String finalString = rawString.substring(rawString.indexOf("main:")+5, rawString.indexOf("ret")).trim();
			mTokens = finalString.split("\\s");
			Opcodes8080 opcode = null;
			for (int i=0; i<mTokens.length-1; i+=2) {
				opcode = Opcodes8080.valueOf(mTokens[i].toUpperCase(Locale.US));
				int ordinal = opcode.ordinal();
				if (ordinal <= 8){
					singleParamOutput(i);
				}else if (ordinal <= 10) {
					doubleParamOutput(i);
				}
			}
			reader.close();
		}catch (Exception e) {
			//Do nothing!
		}
	}
	
	private static void singleParamOutput(int firstTokenIndex){
		System.out.println(mTokens[firstTokenIndex]+" "+mTokens[firstTokenIndex+1]);
	}
	
	private static void doubleParamOutput(int firstTokenIndex){
		String[] params = mTokens[firstTokenIndex+1].split(",");
		System.out.println(mTokens[firstTokenIndex]+" "+params[0]+" "+params[1]);
	}

}
