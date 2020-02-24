/**
 * @author siddhartha
 */
package relationsPhase2;

import util.Sid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * outputs the labels and prints the .rel files in the relations folder
 * @author sjonnalagadda
 *
 */
public class RelOutputFormatter {

	/**
	 * takes the labels and 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ArrayList<String> labels = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader("outputs/CompetitionCorpus_testing_CRF_v2_labels_noSpace.txt"));
		while(br.ready()){
			labels.add(br.readLine().trim());
			labels.remove("");
		}
		br.close();
		
		
		ArrayList<String> instances = new ArrayList<String>();
		br = new BufferedReader(new FileReader("models/CompetitionCorpus_testing_v2.txt"));
		while(br.ready()){
			String line=br.readLine();
			instances.add(line.substring(0,line.indexOf(' ')).trim());
			instances.remove("");
		}
		br.close();
		
		if(labels.size()!=instances.size()){
			System.err.println("error");
			System.exit(-1);
		}
		
		Pattern instPattern = Pattern.compile("^(.+\\.)txt.+_(\\d+:\\d+_\\d+:\\d+).+_(\\d+:\\d+_\\d+:\\d+)$");
		for(int i=0; i<instances.size(); i++){
			String instanceName = instances.get(i);
			Matcher instMatcher = instPattern.matcher(instanceName);
			String label=labels.get(i);
			if(label.matches("None"))
				continue;
			if(instMatcher.find()){
				String fileName="../competition_groundTruth/"+instMatcher.group(1)+"con";
				String con1="";String con2="";
				BufferedReader br1 = new BufferedReader(new FileReader(fileName));
				while(br1.ready()){
					String text=br1.readLine();
					if(text.contains(instMatcher.group(2).replace('_', ' '))){
						Sid.log(text);
						con1=text.split("\\|")[0];
					}
					else if(text.contains(instMatcher.group(3).replace('_', ' '))){
						Sid.log(text);
						con2=text.split("\\|")[0];
					}
				}
				br1.close();
				
				PrintWriter pwr = new PrintWriter(new FileWriter("outputs/competition3_rel/"+instMatcher.group(1)+"rel",true));
				pwr.println(con1+"||r=\""+label+"\"||"+con2);
				pwr.close();
			}
			else{
				System.err.println("error in instance");
			}
		}
		
		
		/*int labelReadCount=0;
		
		String output_dir_string = "../tst_rel_v1/";
		File output_dir = new File(output_dir_string);
		output_dir.mkdir();
		
		File dir_txt=new File("../trn_txt");
		String[] txtFiles = dir_txt.list();
		for (String txtFile : txtFiles) {
			
			PrintWriter pwr = new PrintWriter(new FileWriter(output_dir_string+txtFile.substring(0,txtFile.length()-3)+"rel"));
			
			BufferedReader br_txt = new BufferedReader(new FileReader("../trn_txt/"+txtFile));
			
			int lineNum=0;
			while(br_txt.ready()){
				lineNum++;
				
				String sent = br_txt.readLine();
				
				
				
				labelReadCount++;
			}
			br_txt.close();pwr.close();
		}*/
		
	}

}
