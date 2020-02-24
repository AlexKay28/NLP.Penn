package concepts.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 * Compares the outputs of two different models for the i2b2 task
 * @author siddhartha
 *
 */
public class OutputComparator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		String compTxt = "/home/siddhartha/Desktop/competition_txt/";
		String trnIndex = "/home/siddhartha/Desktop/i2b2OnlyTrn_positional_index/";
		String output = "/home/siddhartha/Desktop/output.tab";
		String rcp = "/home/siddhartha/Desktop/competition_groundTruth/";
		String scp1 = "/home/siddhartha/Desktop/competition_con_baseline/";
		String scp2 = "/home/siddhartha/Desktop/competition_con_local_10_clinical_h2000_5_6_20/";
		
		IndexReader reader = IndexReader.open(new SimpleFSDirectory(new File(trnIndex)), true);
		PrintWriter pwr = new PrintWriter(output);
		
		String[] conFilenames = new File(rcp).list(); 
		for (String conFilename : conFilenames) {
			if(!conFilename.endsWith(".con"))
				continue;
			HashSet<String> rcpLines = getLinesLower(rcp+conFilename);
			HashSet<String> scp1Lines = getLinesLower(scp1+conFilename);
			HashSet<String> scp2Lines = getLinesLower(scp2+conFilename);
			HashSet<String> allLines = new HashSet<String>();
			allLines.addAll(rcpLines);allLines.addAll(scp1Lines);allLines.addAll(scp2Lines);
			
			ArrayList<String> textLines = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(compTxt+conFilename.substring(0, conFilename.lastIndexOf("."))+".txt"));
			while(br.ready()){
				textLines.add(br.readLine());
			}
			
			for (String string : allLines) {
				boolean tp1,tp2,tn1,tn2,fp1,fp2,fn1,fn2;
				tp1=rcpLines.contains(string)&&scp1Lines.contains(string);
				fp1=!rcpLines.contains(string)&&scp1Lines.contains(string);
				fn1=rcpLines.contains(string)&&!scp1Lines.contains(string);
				tn1=!rcpLines.contains(string)&&!scp1Lines.contains(string);
				
				tp2=rcpLines.contains(string)&&scp2Lines.contains(string);
				fp2=!rcpLines.contains(string)&&scp2Lines.contains(string);
				fn2=rcpLines.contains(string)&&!scp2Lines.contains(string);
				tn2=!rcpLines.contains(string)&&!scp2Lines.contains(string);
				
				
				String newTerms = "";
				String con = string.substring(3, string.indexOf("\"", 3));
				int lineNum = Integer.parseInt(string.substring(string.lastIndexOf(" ")+1).split(":")[0])-1; //starts with 1
				for (String token : con.split("\\s")) {
					if(reader.docFreq(new Term("contents", token))==0){
						newTerms+=token+" ";
					}
				}
				newTerms=newTerms.trim();
				
				pwr.println(conFilename+"\t"+string+"\t"+tp1+"\t"+tp2+"\t"+fp1+"\t"+fp2+"\t"+fn1+"\t"+fn2+"\t"+tn1+"\t"+tn2+"\t"+newTerms+"\t"+textLines.get(lineNum));
			}
			
			
		}

		pwr.close();
	}

	private static HashSet<String> getLinesLower(String rcp) {
		
		HashSet<String> ret = new HashSet<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(rcp));
			while(br.ready()){
				ret.add(br.readLine().toLowerCase().trim());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}

}
