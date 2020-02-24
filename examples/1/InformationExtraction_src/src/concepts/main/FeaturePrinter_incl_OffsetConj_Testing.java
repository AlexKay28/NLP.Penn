/**
 * Copyright of Lnx Research, LLC.
 * email: sid.kgp@gmail.com
 */
package concepts.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;


import util.Sid;
import util.SimpleCommandLineParser;

/**
 * basic + offset conjunctions + only for testing 
 * @author sjonnalagadda
 *
 */
public class FeaturePrinter_incl_OffsetConj_Testing {

	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args);
		String i2b2Thesarus = "models/simFindStores_i2b2.txt";
		String clinicalThesaurus = "models/Thesarus_clinicalTrials_h2000_5_6_20.txt";
		String otherThesaurus = parser.getValue("thesaurus", "th");
		String outputFile = parser.getValue("out");
		
		FeaturePrinter_incl_OffsetConj fp = new FeaturePrinter_incl_OffsetConj(i2b2Thesarus, clinicalThesaurus, otherThesaurus);
		PrintWriter pwr = new PrintWriter(new FileWriter(outputFile));
				//"outputs/competition__SimFind_close_local_10_clinical_h2000_5_6_20.txt"));
		
		File dir_txt=new File("../IE_data/competition_txt");
		String[] txtFiles = dir_txt.list();
		for (String txtFile : txtFiles) {
			fp.section = "";
			fp.stringTokens = null;
			fp.posTags = null;
			fp.phraseChunks = null;
			Sid.log(txtFile);
			
			int line=0;
			BufferedReader br_txt = new BufferedReader(new FileReader("../IE_data/competition_txt/"+txtFile));
			while(br_txt.ready()){
				
				fp.sent=br_txt.readLine();
				
				//handle blank lines
				if(fp.sent.matches("\\s*"))
					fp.sent="-";
				
				if(fp.sent.matches(".+\\s:"))
					fp.section=fp.sent;
				
				fp.doTokenization();
				fp.doPOSSentence();
				fp.doChunking();
				
				fp.featureSets = new ArrayList<String>();
				fp.dictLabels = new ArrayList<String>();
				for (int i=0; i<fp.tokens.size(); i++) {
					fp.dictLabels.add("O");
				}
				
				BufferedReader br_dict = new BufferedReader(new FileReader("../IE_data/competition_dictCon_txt/"+txtFile.substring(0,txtFile.length()-3)+"dic"));
				while(br_dict.ready()){
					String conLine = br_dict.readLine();
					Matcher mCon = fp.conPattern.matcher(conLine);
					if(mCon.find()){
						int lineNum = Integer.parseInt(mCon.group(2))-1;
						if(lineNum!=line)
							continue;
						int init = Integer.parseInt(mCon.group(3));
						int end = Integer.parseInt(mCon.group(4));
						String label = mCon.group(5);
						for (int i=init; i<=end; i++) {
							fp.dictLabels.set(i,label);
						}
						/*System.out.println(conLine);
						System.out.println("dictlineNum:\t"+mCon.group(2));
						System.out.println("dictinit:\t"+mCon.group(3));
						System.out.println("dictend:\t"+mCon.group(4));
						System.out.println("dictlabel:\t"+mCon.group(5));*/
					}
				}
				br_dict.close();
				
				
				for (int index = 0; index < fp.tokens.size(); index++) {
					String featureSet = fp.getFeatureSet(index);
					fp.featureSets.add(featureSet);
					//pwr.println(featureSet+fp.labels.get(index));
					
				}
				
				for (int index = 0; index < fp.tokens.size(); index++) {
					String finalFeature="";
					if(index>1)
						finalFeature+=fp.featureSets.get(index-2).replaceAll("\t", "_2\t");
					if(index>0)
						finalFeature+=fp.featureSets.get(index-1).replaceAll("\t", "_1\t");
					finalFeature+=fp.featureSets.get(index);
					if(index<fp.tokens.size()-1)
						finalFeature+=fp.featureSets.get(index+1).replaceAll("\t", "+1\t");
					if(index<fp.tokens.size()-2)
						finalFeature+=fp.featureSets.get(index+2).replaceAll("\t", "+2\t");
					//Sid.log(finalFeature); 
					pwr.println(finalFeature.replace(' ', '_').replace('\t', ' ').trim());
				}
				
				//Sid.log(""); 
				pwr.println();
				line++;
			}
			br_txt.close();
			pwr.flush();
		}
		
		pwr.close();
				
	}

}
