/**
 * @author siddhartha
 */
package proteinTagging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import util.tokenizer.BobTokenizer;

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
		BufferedReader br = new BufferedReader(new FileReader("outputs/bc2geneMention/testingBC2_half_proteins_2000d_5.labels"));
		//BufferedReader br = new BufferedReader(new FileReader("outputs/bc2geneMention/trainingBCAuto.labels"));
		String outputFile = "outputs/bc2geneMention/testingBC2_half_proteins_2000d_5.in";
		//String outputFile = "outputs/bc2geneMention/trainingBCAuto.in";
		BufferedReader br_txt = new BufferedReader(new FileReader("dict/bc2geneMention/train/train_7500_1.in"));
		//BufferedReader br_txt = new BufferedReader(new FileReader("dict/bc2geneMention/train/train.in"));

		log("----------------------------------------------------------");
		ArrayList<String> labels = new ArrayList<String>();
		while(br.ready()){
			labels.add(br.readLine().trim());
		}
		br.close();
		
		int labelReadCount=0;

		PrintWriter pwr = new PrintWriter(new FileWriter(outputFile));
		
		int lineNum=0;
		while(br_txt.ready()){
			lineNum++;

			String lineString = br_txt.readLine();
			String sentId = lineString.substring(0,lineString.indexOf(" "));
			String sent = lineString.substring(lineString.indexOf(" ")+1);
			String[] tokens = new BobTokenizer().getTokens(sent).toArray(new String[0]);
			String[] tokenLabels = new String[tokens.length];
			for(int count=0; count<tokens.length; count++){
				tokenLabels[count]=labels.get(labelReadCount);
				labelReadCount++;
			}

			int entStart=-1, entEnd=-1;int noSpaceCharCount=0;
			for(int i=0; i < tokens.length; i++){
					if(entStart!=-1&&tokenLabels[i].equals("O")){
						log(sentId+"|"+entStart+" "+entEnd+"|");
						pwr.println(sentId+"|"+entStart+" "+entEnd+"|");
						entStart=-1; entEnd=-1;
					}
					else if(entStart!=-1&&tokenLabels[i].equals("I")){
						entEnd+=tokens[i].length();
					}
					else if(entStart==-1&&tokenLabels[i].equals("I")){
						entStart=noSpaceCharCount; entEnd=noSpaceCharCount+tokens[i].length()-1;
					}
					
					noSpaceCharCount+=tokens[i].length();
			}
				
			if(labels.get(labelReadCount).equals(""))
				labelReadCount++;
			else 
				System.err.println("warning");
		}
		br_txt.close();pwr.close();


	}

	private static void log(String string) {
		System.out.println(string);
	}

}
