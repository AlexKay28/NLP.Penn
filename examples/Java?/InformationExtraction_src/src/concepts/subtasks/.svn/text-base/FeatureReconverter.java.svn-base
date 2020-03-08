/**
 * @author siddhartha
 */
package concepts.subtasks;

import util.Sid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * @author sjonnalagadda
 *
 */
public class FeatureReconverter {

	/**
	 * converts decimal features to binary features 
	 * prints the documents
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		HashMap<Integer, String> featureMap = new HashMap<Integer, String>();
		
		BufferedReader br1=new BufferedReader(new FileReader("models/trainingCorpusI2B2version3_OffsetConj.txt"));
		
		int lineNum=0;
		while(br1.ready()){
			String line = br1.readLine();
			Sid.log(line);
			if(line.equals(""))
				featureMap.put(lineNum, line);
			else
				featureMap.put(lineNum, line.substring(line.lastIndexOf('\t')+1));
			lineNum++;
		}
		br1.close();
		
		BufferedReader br = new BufferedReader(new FileReader("models/incremental_docvectors_features3.txt"));
		
		
		Sid.log(br.readLine());
		while(br.ready()){
			String line = br.readLine();
			String[] tokens = line.split("\\|");
			int instanceNum = Integer.parseInt(tokens[0].substring(tokens[0].lastIndexOf('/')+1, tokens[0].lastIndexOf('.')));
			String featureSet="";
			for(int i=1; i<tokens.length; i++){
				Double tokenValue = Double.parseDouble(tokens[i]);
				if(tokenValue>0.03)//1/sqrt(1000)
					featureSet+="FEATURE"+i+"POS"+"\t";
				else if(tokenValue<-0.03)
					featureSet+="FEATURE"+i+"NEG"+"\t";
			}
			featureMap.put(instanceNum, featureSet+featureMap.get(instanceNum));
			Sid.log(featureSet);
			Sid.log(line);
		}
		br.close();
		
		PrintWriter pwr = new PrintWriter(new FileWriter("incremental_docvectors_features3_malletFormat.txt"));
		for(int i=0; i< featureMap.size(); i++){
			pwr.println(featureMap.get(i));
		}
		pwr.close();
	}

}
