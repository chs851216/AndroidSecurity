package csie.mcu.edu.tw.group5.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		String regex = "([(https?|ftp|file)://]*[-a-zA-Z0-9+&@#/%?=~_|!,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
		String regex = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\.(\\d{1,5})";
		String smaliFilePathes = "C:/Users/user/Desktop";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher;
		
        smaliFilePathes += "/1.txt";
        try {
            FileReader fileReader = new FileReader (smaliFilePathes);
            BufferedReader bufferedReader = new BufferedReader (fileReader);
            
            String tmpData = null;
            while ((tmpData = bufferedReader.readLine()) != null) {
                              
                String [] stringArray = tmpData.split(" ");
                for (String line : stringArray) {
                    line = line.replaceAll("\"", "");
                    matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                    	String pathName = "C:/Users/user/Desktop";
                        pathName += "/URLs.txt";

                        FileWriter fileWriter = new FileWriter(pathName, true);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        
                        String a = matcher.group();
                        
                        if(a.indexOf(".") != -1 && a.lastIndexOf(".")-a.indexOf(".") > 3) {
                        	a = a.substring(0, a.lastIndexOf("."));
	                        bufferedWriter.write(a + "\n");
	                        bufferedWriter.flush();
                        }
                        bufferedWriter.close();
                    }
                }
            }
            bufferedReader.close();
        } catch (IOException read) {read.printStackTrace();}
	}
}