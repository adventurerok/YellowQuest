package com.ithinkrok.yellowquest;

public class StringFormatter {

	
	public static String format(String in, Object...args){
		StringBuilder result = new StringBuilder();
		for(int d = 0; d < in.length(); ++d){
			char c = in.charAt(d);
			if(c != '{'){
				result.append(c);
				continue;
			}
			StringBuilder num = new StringBuilder();
			for(d += 1; d < in.length(); ++d){
				c = in.charAt(d);
				if(c == '}') break;
				num.append(c);
			}
			int iNum = Integer.parseInt(num.toString());
			if(args.length > iNum) result.append(args[iNum]);
		}
		return result.toString();
	}
}
