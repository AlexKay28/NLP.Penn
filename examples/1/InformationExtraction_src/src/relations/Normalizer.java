package relations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Sid;

public class Normalizer {

	
	public static void main(String[] args){
		String input="PROBLEM_ , most likely PROBLEM_";
		Sid.log(normalizeSentence(input));
	}
	
	
	public static String normalizeSentence(String input){
		
		//1. Number Example:10.34 to NUMBER
		String[] tokens=input.split("\\s");
		input="";
		for (String token : tokens) {
			if(token.matches("(\\d\\p{Punct}?)+")){
				token="NUMBER";
			}
			input+=token+" ";
		}
		input=input.trim();
		
		//2. Number range
		//4. DIR
		//5. TIME
		//6. THIRD PERSON PRONOUNS - THIRDPP
		//7. Remove de-identifications
		tokens=input.split("\\s");
		input="";
		for (String token : tokens) {
			if(token.matches("\\d+(\\.\\d+)?-\\d+(\\.\\d+)?")){
				token="NUMBER TO NUMBER";
			}
			else if(token.matches("\\d+[-\\/]\\d+[-\\/]\\d+")){
				token="DATE";
			}
			else if(token.matches("right|left")){
				token="DIR";
			}
			else if(token.matches("hours?|days?|years?|secs?|seconds?|minutes?|mins?")){
				token="TIME";
			}
			else if(token.matches("he|him|himself|his|his|she|her|herself|hers|her|they|them|themselves|theirs|their")){
				token="THIRDPP";
			}
			else if(token.contains("**PLACE")){
				token="PLACE";
			}
			else if(token.contains("**STREET-ADDRESS")){
				token="STREET-ADDRESS";
			}
			else if(token.contains("**DATE")){//DATE is seen as a NUMBER
				token="NUMBER";
			}
			else if(token.contains("**ID-NUM")){//ID-NUM is seen as a NUMBER
				token="NUMBER";
			}
			else if(token.contains("**PHONE")){//PHONE is seen as a NUMBER
				token="NUMBER";
			}
			else if(token.contains("**NAME")){
				token="NAME";
			}
			else if(token.contains("**AGE")){//AGE is seen as a NUMBER
				token="NUMBER";
			}
			else if(token.contains("**INSTITUTION")){
				token="INSTITUTION";
			}
			input+=token+" ";
		}
		input=input.trim();
		
		//3. Remove &[a-z]+; &#\\d+;
		tokens=input.split("\\s");
		input="";
		for (String token : tokens) {
			if(token.matches("&[a-z]+")||token.matches("&#\\d+")
					||token.matches(";")){
				token="";
			}
			else if(token.length()>0)
				input+=token+" ";
		}
		input=input.trim();
		
		Pattern aPattern = Pattern.compile("&[a-z]+|&#\\d+|\"");
		Matcher aMatcher = aPattern.matcher(input);
		while(aMatcher.find()){
			input=input.substring(0,aMatcher.start())+input.substring(aMatcher.end());
			aMatcher.reset(input);
		}
		
		//8. remove coordinations
		/*aPattern = Pattern.compile("(TREATMENT[,/;\\s]*)+TREATMENT_");
		aMatcher = aPattern.matcher(input);
		if(aMatcher.find()){
			input=input.substring(0,aMatcher.start())+"TREATMENT_"+input.substring(aMatcher.end());
			//aMatcher.reset(input);
		}*/
		input+=" ";
		aPattern = Pattern.compile("PROBLEM\\s[,(and)(or)]+\\sPROBLEM_\\s");
		aMatcher = aPattern.matcher(input);
		while(aMatcher.find()){
			//Sid.log(aMatcher.group());
			input=input.substring(0,aMatcher.start())+"PROBLEM_ "+input.substring(aMatcher.end());
			aMatcher.reset(input);
			//Sid.log(input);
		}
		
		aPattern = Pattern.compile("PROBLEM_\\s[,(and)(or)]+\\sPROBLEM\\s");
		aMatcher = aPattern.matcher(input);
		while(aMatcher.find()){
			//Sid.log(aMatcher.group());
			input=input.substring(0,aMatcher.start())+"PROBLEM_ "+input.substring(aMatcher.end());
			aMatcher.reset(input);
			//Sid.log(input);
		}
		
		aPattern = Pattern.compile("PROBLEM\\s[,(and)(or)]+\\sPROBLEM\\s");
		aMatcher = aPattern.matcher(input);
		while(aMatcher.find()){
			//Sid.log(aMatcher.group());
			input=input.substring(0,aMatcher.start())+"PROBLEM "+input.substring(aMatcher.end());
			aMatcher.reset(input);
			//Sid.log(input);
		}
		
		aPattern = Pattern.compile("TEST\\s[,(and)(or)]+\\sTEST_\\s");
		aMatcher = aPattern.matcher(input);
		while(aMatcher.find()){
			//Sid.log(aMatcher.group());
			input=input.substring(0,aMatcher.start())+"TEST_ "+input.substring(aMatcher.end());
			aMatcher.reset(input);
			//Sid.log(input);
		}
		
		aPattern = Pattern.compile("TEST_\\s[,(and)(or)]+\\sTEST\\s");
		aMatcher = aPattern.matcher(input);
		while(aMatcher.find()){
			//Sid.log(aMatcher.group());
			input=input.substring(0,aMatcher.start())+"TEST_ "+input.substring(aMatcher.end());
			aMatcher.reset(input);
			//Sid.log(input);
		}
		
		aPattern = Pattern.compile("TEST\\s[,(and)(or)]+\\sTEST\\s");
		aMatcher = aPattern.matcher(input);
		while(aMatcher.find()){
			//Sid.log(aMatcher.group());
			input=input.substring(0,aMatcher.start())+"TEST "+input.substring(aMatcher.end());
			aMatcher.reset(input);
			//Sid.log(input);
		}
		
		aPattern = Pattern.compile("TREATMENT\\s[,(and)(or)]+\\sTREATMENT_\\s");
		aMatcher = aPattern.matcher(input);
		while(aMatcher.find()){
			//Sid.log(aMatcher.group());
			input=input.substring(0,aMatcher.start())+"TREATMENT_ "+input.substring(aMatcher.end());
			aMatcher.reset(input);
			//Sid.log(input);
		}
		
		aPattern = Pattern.compile("TREATMENT_\\s[,(and)(or)]+\\sTREATMENT\\s");
		aMatcher = aPattern.matcher(input);
		while(aMatcher.find()){
			//Sid.log(aMatcher.group());
			input=input.substring(0,aMatcher.start())+"TREATMENT_ "+input.substring(aMatcher.end());
			aMatcher.reset(input);
			//Sid.log(input);
		}
		
		aPattern = Pattern.compile("TREATMENT\\s[,(and)(or)]+\\sTREATMENT\\s");
		aMatcher = aPattern.matcher(input);
		while(aMatcher.find()){
			//Sid.log(aMatcher.group());
			input=input.substring(0,aMatcher.start())+"TREATMENT "+input.substring(aMatcher.end());
			aMatcher.reset(input);
			//Sid.log(input);
		}
		
		//9. remove stopwords [don't remove punctuation]
		if(MatchInteractions.stopList==null||MatchInteractions.stopList.size()==0)
			MatchInteractions.loadStopWords();
		tokens=input.split("\\s");
		input="";
		for (String token : tokens) {
			if(!MatchInteractions.stopList.contains(token.toLowerCase().trim())){
				input+=token+" ";
			}
			//exception for CAPITAL NOTATIONS
			else if(token.matches("[A-Z]+")){
				input+=token+" ";
			}
			
		}
		input=input.trim();
		
		
		
/*		Pattern aPattern = Pattern.compile("(.*)\\d+(\\.\\d+)?(.*)");
		Matcher aMatcher = aPattern.matcher(input);
		
		while(aMatcher.find()){
			input=aMatcher.group(1)+"NUMBER"+aMatcher.group(3);
			aMatcher.reset(input);
		}*/
		return input;
	}
	
}
