package proteinTagging;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import util.tokenizer.BobTokenizer;

public class DataSetFormatter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String baseinputFileName="dict/bc2geneMention/train/train.in";
		String labelFileName="dict/bc2geneMention/train/GENE.eval";
		//String i2b2InputFileName = "dict/bc2geneMention/train_BC2i2b2Format.txt";
		
		ArrayList<String> labelLines = new ArrayList<String>();
		BufferedReader br_labelFile = new BufferedReader(new FileReader(labelFileName));
		while(br_labelFile.ready()){
			labelLines.add(br_labelFile.readLine());
		}		
		br_labelFile.close();
		
		
		BufferedReader br = new BufferedReader(new FileReader(baseinputFileName));
		while(br.ready()){
			String line=br.readLine();
			String sent = line.substring(line.indexOf(" ")+1);
			String[] tokens = new BobTokenizer().getTokens(sent).toArray(new String[0]);
			String[] labels = getLabels(line,tokens,labelLines);
		}
		
		br.close();
	}

	private static String[] getLabels(String line, String[] tokens,
			ArrayList<String> labelLines) throws IOException {
		String[] labels = new String[tokens.length];
		int[] labelStarts=new int[tokens.length];int noSpaceCharCount=0;
		for (int i = 0; i < labels.length; i++) {
			labels[i]="O";
			labelStarts[i]=noSpaceCharCount;
			noSpaceCharCount+=tokens[i].length();
		}
		
		for (String conLine: labelLines) {
			String lineId = conLine.split("\\|")[0];
			if(!line.startsWith(lineId))
				continue;
			int entStart = Integer.parseInt(conLine.split("\\|")[1].split("\\s")[0]);
			int entEnd = Integer.parseInt(conLine.split("\\|")[1].split("\\s")[1]);
			for (int i = 0; i < labels.length; i++) {
				if(labelStarts[i]>=entStart&&labelStarts[i]<=entEnd)
					labels[i]="I";
			}
		}
		
		return labels;
	}

}
