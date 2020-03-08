/**
 * 
 */
package subtasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Sid;

/**
 * @author sjonnal3
 *
 */
public class extractIntPatterns {

	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File dir_rel=new File("../trn_rel");
		String[] relFiles = dir_rel.list();
		
		HashSet<String> relPatternsWTags = new HashSet<String>();
		HashSet<String> relPatternsOnly = new HashSet<String>();
		boolean useStopWords=false;
		
		
		PrintWriter pwr = new PrintWriter(new FileWriter("relPatternsWTags_withdetails.txt"));
		PrintWriter pwr1 = new PrintWriter(new FileWriter("relPatternsWTags.txt"));
		
		
		ArrayList<String> stopList = new ArrayList<String>();
		BufferedReader brStops = new BufferedReader(new FileReader("dict/stopFileFox_reduced.txt"));
		while(brStops.ready()){
			stopList.add(brStops.readLine().trim().toLowerCase());
		}
		
		for (String relFile : relFiles) {
			Sid.log("\n\n\n\n\n"+relFile);
			BufferedReader br = new BufferedReader(new FileReader("../trn_rel/"+relFile));
			
			while(br.ready()){
				String annotation = br.readLine();
				Pattern locPattern = Pattern.compile("\\d+:\\d+");
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
				BufferedReader br1 = new BufferedReader(new FileReader("../trn_txt/"+relFile.substring(0,relFile.length()-3)+"txt"));
				String line="";
				int count=lineNumber;
				while(count>0 && br1.ready()){
					count--;
					line = br1.readLine();
				}
				br1.close();
				
				/**
				 * REPLACING CONCEPTS WITH IDENTIFIERS
				 */
				String[] tokens = line.split("\\s");
				BufferedReader br2 = new BufferedReader(new FileReader("../trn_con/"+relFile.substring(0,relFile.length()-3)+"con"));
				//int id=0;
				while(br2.ready()){
					String concept = br2.readLine();
					Pattern conPattern = Pattern.compile("\\d+:(\\d+) \\d+:(\\d+)");
					Matcher conMatcher = conPattern.matcher(concept);
					int conLineNumber=-1;
					if(conMatcher.find()){
						conLineNumber=Integer.parseInt(conMatcher.group().split(":")[0]);
						if(conLineNumber==lineNumber){
							String classification = (concept.split("\\|\\|t=\"")[1].split("\"")[0].toUpperCase());
							int start=Integer.parseInt(conMatcher.group(1));
							int end=Integer.parseInt(conMatcher.group(2));
							tokens[start]=classification;
							for(int i=end; i>start; i--){
								tokens[i]="";
							}
							
							//change for the specific concept
							if(annotation.contains(conMatcher.group()))
								tokens[start]+="_";
							
						}
					}					
				}
				br2.close();
				String modLine = "";
				for (String token : tokens) {
					if(token.length()>0&&(!stopList.contains(token.toLowerCase())||!useStopWords))
						modLine +=token+" ";
				}
				modLine=modLine.trim();
				Sid.log(line);
				Sid.log(modLine);
				String annLabel = (annotation.substring(annotation.indexOf("||r=\"")+5).split("\"")[0]);
				pwr.println(line+"\n"+annotation+"\n"+modLine+"\n");
				//relPatternsWTags.add(modLine+"||"+annLabel+"\t"+annotation+"\t"+line);
				relPatternsWTags.add(modLine+"||"+annLabel);
				pwr1.println(modLine+"||"+annLabel);
				relPatternsOnly.add(modLine);
				Sid.log(annotation);
				
			}
			br.close();
		}
	
		
		File dir_txt=new File("../trn_txt");
		String[] txtFiles = dir_txt.list();
		
		for (String txtFile : txtFiles) {
			Sid.log("\n\n\n\n\n"+txtFile);
			BufferedReader br = new BufferedReader(new FileReader("../trn_txt/"+txtFile));
			int lineNumber =0;
			while(br.ready()){
				lineNumber++;
				String line = br.readLine();
				Sid.log(line);
				/**
				 * REPLACING CONCEPTS WITH IDENTIFIERS
				 */
				String[] tokens = line.split("\\s");
				BufferedReader br2 = new BufferedReader(new FileReader("../trn_con/"+txtFile.substring(0,txtFile.length()-3)+"con"));
				//int id=0;
				int replacements=0;
				while(br2.ready()){
					String concept = br2.readLine();
					Pattern conPattern = Pattern.compile("\\d+:(\\d+) \\d+:(\\d+)");
					Matcher conMatcher = conPattern.matcher(concept);
					int conLineNumber=-1;
					if(conMatcher.find()){
						conLineNumber=Integer.parseInt(conMatcher.group().split(":")[0]);
						if(conLineNumber==lineNumber){
							String classification = (concept.split("\\|\\|t=\"")[1].split("\"")[0].toUpperCase());
							int start=Integer.parseInt(conMatcher.group(1));
							int end=Integer.parseInt(conMatcher.group(2));
							//tokens[start]=classification+id++;
							tokens[start]=classification;
							replacements++;
							for(int i=end; i>start; i--){
								tokens[i]="";
							}
						}
					}					
				}
				br2.close();
				String modLine = "";
				for (String token : tokens) {
					if(token.length()>0&&(!stopList.contains(token.toLowerCase())||!useStopWords))
						modLine +=token+" ";
				}
				modLine=modLine.trim();
				if(replacements<2 || relPatternsOnly.contains(modLine))
					continue;
				pwr.println(line+"\n"+"NONE"+"\n"+modLine+"\n");
				relPatternsWTags.add(modLine+"||"+"NONE");
				pwr1.println(modLine+"||"+"NONE");
			}
			br.close();
		}
		
		/*Iterator<String> itr = relPatternsWTags.iterator();
		while(itr.hasNext()) {
			pwr1.println(itr.next());
		}*/
		pwr1.close();
		pwr.close();
	}

}
