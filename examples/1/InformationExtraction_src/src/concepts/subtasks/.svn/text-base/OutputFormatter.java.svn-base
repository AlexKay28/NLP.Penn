/**
 * @author siddhartha
 */
package concepts.subtasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import util.SimpleCommandLineParser;

/**
 * outputs the labels and prints the .con files in the concept folder
 * @author sjonnalagadda
 *
 */
public class OutputFormatter {

	/**
	 * takes the labels and 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args);
		String input="outputs/competition__SimFind_close_local_10_clinical_h2000_5_6_10.labels";
		String output_dir_string = "outputs/competition__SimFind_close_local_10_clinical_h2000_5_6_10_labels/";
		
		input = parser.getValue("input","in");
		output_dir_string = parser.getValue("out", "output", "outdir");
		if(!output_dir_string.endsWith("/")){
			System.err.println("output directory path should end with /");
			return;
		}	
		
		ArrayList<String> labels = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(input));
		//BufferedReader br = new BufferedReader(new FileReader("/home/siddhartha/currentProjects/mallet_hg3/i2b2ConceptExtraction/competition_output_complete_noSimFind.txt"));
		while(br.ready()){
			labels.add(br.readLine().trim());
		}
		br.close();
		
		int labelReadCount=0;
		
		
		File output_dir = new File(output_dir_string);
		output_dir.mkdirs();
		
		File dir_txt=new File("../IE_data/competition_txt");
		String[] txtFiles = dir_txt.list();
		for (String txtFile : txtFiles) {
			
			PrintWriter pwr = new PrintWriter(new FileWriter(output_dir_string+txtFile.substring(0,txtFile.length()-3)+"con"));
			
			BufferedReader br_txt = new BufferedReader(new FileReader("../IE_data/competition_txt/"+txtFile));
			
			int lineNum=0;
			while(br_txt.ready()){
				lineNum++;
				
				String sent = br_txt.readLine();
				String[] tokens = sent.split("\\s+");
				String[] tokenLabels = new String[tokens.length];
				for(int count=0; count<tokens.length; count++){
					tokenLabels[count]=labels.get(labelReadCount);
					labelReadCount++;
				}
				
				for(int i=0; i < tokens.length; i++){
					for(int j=i; j<tokens.length;j++){
						String label = tokenLabels[i];
						boolean same = true;
						for(int k=i+1;k<=j;k++)
							if(!tokenLabels[k].equals(label))
								same=false;
						if((i==0||!tokenLabels[i].equals(tokenLabels[i-1]))
								&& (j==tokens.length-1||!tokenLabels[j].equals(tokenLabels[j+1]))
								&&same){
						
							String aString = "";
							for(int k=i; k<=j; k++)
								aString+=tokens[k]+" ";
							aString=aString.trim();
							
							if(label.equals("Iproblem")){
								pwr.println("c=\""+aString+"\" "+lineNum+":"+i+" "+lineNum+":"+j+"||t=\"problem\"");
								//Sid.log("c=\""+aString+"\" "+lineNum+":"+i+" "+lineNum+":"+j+"||t=\"problem\"");
							}
							else if(label.equals("Itreatment")){
								pwr.println("c=\""+aString+"\" "+lineNum+":"+i+" "+lineNum+":"+j+"||t=\"treatment\"");
								//Sid.log("c=\""+aString+"\" "+lineNum+":"+i+" "+lineNum+":"+j+"||t=\"treatment\"");
							}
							else if(label.equals("Itest")){
								pwr.println("c=\""+aString+"\" "+lineNum+":"+i+" "+lineNum+":"+j+"||t=\"test\"");
								//Sid.log("c=\""+aString+"\" "+lineNum+":"+i+" "+lineNum+":"+j+"||t=\"test\"");
							}
							
							
						}
					}
				}
				if(labels.get(labelReadCount).equals(""))
					labelReadCount++;
				else 
					System.err.println("warning");
			}
			br_txt.close();pwr.close();
		}
		
	}

}
