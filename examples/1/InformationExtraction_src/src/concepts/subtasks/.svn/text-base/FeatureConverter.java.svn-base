/**
 * @author siddhartha
 */
package concepts.subtasks;

import util.Sid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;


/**
 * coverts the features to numbers - not using
 * @author sjonnalagadda
 *
 */
public class FeatureConverter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		NumberFormat nf=NumberFormat.getInstance(); // Get Instance of NumberFormat
		nf.setMinimumIntegerDigits(3);  // The minimum Digits required is 3
		nf.setMaximumIntegerDigits(3); // The maximum Digits required is 3
		
		
		String trainingCorpus = "models/trainingCorpusI2B2version3_OffsetConj.txt";
		
		File dir = new File(trainingCorpus.substring(0, trainingCorpus.lastIndexOf('.')));
		dir.mkdir();
		
		BufferedReader br = new BufferedReader(new FileReader(trainingCorpus));
		int count=0;
		while(br.ready()){
			PrintWriter pwr = new PrintWriter(trainingCorpus.substring(0, trainingCorpus.lastIndexOf('.'))+"/"+count+".txt");
			
			String input = br.readLine();
			Sid.log(input);
			
			if(input.equals("")){
				pwr.close();count++;
				continue;
			}
			
			String output = "";
			for (int i = 0; i < input.length(); i++) {
				if(input.charAt(i)=='\t')
					output+=input.charAt(i);
				else
					output+=  (nf.format((int)input.charAt(i)));
			}
			output=output.substring(0, output.lastIndexOf("\t"));
			pwr.print(output);
			Sid.log(output);
			
			String[] parts = output.split("\t");
			String decode="";
			for (int i1=0; i1<parts.length; i1++) {
				String part = parts[i1];
				for (int i = 0; i < part.length()-2; i=i+3) {
					decode+=  (char)Integer.parseInt(part.substring(i,i+3));
				}
				decode+="\t";
			}			
			Sid.log(decode.trim()+"\n\n\n");
			
			pwr.close();count++;
		}
		br.close();
		
		
		
		String input = "hare\tkrishna";
		String output = "";
		for (int i = 0; i < input.length(); i++) {
			if(input.charAt(i)=='\t')
				output+=input.charAt(i);
			else
				output+=  (nf.format((int)input.charAt(i)));
		}
		Sid.log(output);
		
		String[] parts = output.split("\t");
		String decode="";
		for (String part : parts) {
			for (int i = 0; i < part.length()-2; i=i+3) {
				decode+=  (char)Integer.parseInt(part.substring(i,i+3));
			}
			decode+="\t";
		}
		
		Sid.log("\n\n\n"+decode.trim());
		
	}

}
