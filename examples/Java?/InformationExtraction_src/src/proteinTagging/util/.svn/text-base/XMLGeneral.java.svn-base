package proteinTagging.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLGeneral {

	public static String removeAllTagsExcept_prot(String input){
		
		Pattern protPattern = Pattern.compile("<(/?prot)>");
		
		Matcher anotherMatcher = protPattern.matcher(input);
		while(anotherMatcher.find()){
			input = input.substring(0, anotherMatcher.start()) + "&" + anotherMatcher.group(1) + "#" + input.substring(anotherMatcher.end()); 
		}
		
		input = removeAllTags(input);
		
		Pattern revProtPattern = Pattern.compile("&(/?prot)#");
		Matcher yetAnotherMatcher = revProtPattern.matcher(input);
		while(yetAnotherMatcher.find()){
			input = input.substring(0, yetAnotherMatcher.start()) + "<" + yetAnotherMatcher.group(1) + ">" + input.substring(yetAnotherMatcher.end()); 
		}
		
		removeExtraSpaces(input);
		return input;
	}
	
	
	public static String removeAllTags(String input){
		//Pattern xmlPattern = Pattern.compile("(?<=<[^\\p{P}]{10}>).+(?=</[^\\p{P}]+>)");
		Pattern xmlPattern = Pattern.compile("<([^<]+)>([^<]+)</([^<]+)>");
		
		Matcher aMatcher = xmlPattern.matcher(input);
		while(aMatcher.find() 
				&& aMatcher.groupCount()==3 &&
				aMatcher.group(1).startsWith(aMatcher.group(3))){
			input = input.substring(0, aMatcher.start()) +  aMatcher.group(2) + input.substring(aMatcher.end()); 
			//Sid.log(input);
			aMatcher.reset(input);
		}
		
		input = removeExtraSpaces(input);
		
		return input;
	}
	
	
	public static String removeExtraSpaces(String input) {
		/**
		 * to remove extra spaces
		 */
		String[] tokens = input.split(" ");
		input = "";
		for (String string : tokens){
			if(string.length()>0 && !string.equals(",") && !string.equals("."))
				input += " "+string;
			else
				input +=string;
		}
		input = input.trim();
		//Sid.log(input);
		
		return input;
	}

	public static void main(String[] args) throws IOException{
		
		File dir = new File("interactions");
		String[] automataFiles = dir.list();
		
		for (String inputFileName : automataFiles) {
			BufferedReader br = new BufferedReader(new FileReader("interactions/"+inputFileName));
			PrintWriter pwr = new PrintWriter(new FileWriter("interactions_notag/"+inputFileName+"_tagsRemoved"));
			PrintWriter pwr_prot = new PrintWriter(new FileWriter("interactions_notag_prot/"+inputFileName+"_tagsRemoved_prot"));
			while(br.ready()){
				String line = br.readLine();
				String line1 = removeAllTags(line);
				pwr.println(line1);
				String line2 = removeAllTagsExcept_prot(line);
				pwr_prot.println(line2);
			}
			
			br.close();pwr.close(); pwr_prot.close();			
		}
		
	}
	
}
