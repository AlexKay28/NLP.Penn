/**
 * 
 */
package subtasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Sid;

/**
 * @author sjonnal3
 *
 */
public class extractInteractionSentences {

	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File dir_rel=new File("../_rel");
		String[] relFiles = dir_rel.list();
		
		for (String relFile : relFiles) {
			Sid.log("\n\n\n\n\n"+relFile);
			BufferedReader br = new BufferedReader(new FileReader("../_rel/"+relFile));
			
			while(br.ready()){
				String annotation = br.readLine();
				Pattern locPattern = Pattern.compile("\\d+:\\d");
				Matcher locMatcher = locPattern.matcher(annotation);
				int lineNumber=-1;
				while(locMatcher.find()){
					HashSet<String> lineNumbers = new HashSet<String>();
					lineNumbers.add(locMatcher.group().split(":")[0]);
					if(lineNumbers.size()>1)
						Sid.log("DANGER");
					lineNumber=Integer.parseInt(lineNumbers.iterator().next());
				}
				
				Sid.log("\n"+relFile.substring(0,relFile.length()-3)+"txt");
				Sid.log(lineNumber);
				/**
				 * Finding relevant text
				 */
				BufferedReader br1 = new BufferedReader(new FileReader("../_txt/"+relFile.substring(0,relFile.length()-3)+"txt"));
				String line="";
				while(lineNumber>0 && br1.ready()){
					lineNumber--;
					line = br1.readLine();
				}
				br1.close();
				Sid.log(line);
				Sid.log(annotation);
				/**
				 * Finding assertions
				 */
				String firstEnt=annotation.split("\\|\\|")[0];
				String secondEnt=annotation.split("\\|\\|")[2];				
				BufferedReader br2 = new BufferedReader(new FileReader("../_ast/"+relFile.substring(0,relFile.length()-3)+"ast"));
				while(br2.ready()){
					String assertion = br2.readLine();
					if(assertion.startsWith(firstEnt)||assertion.startsWith(secondEnt))
						Sid.log(assertion);
				}
				br2.close();
			}
			br.close();
		}
	
	}

}
