/**
 * email: sid.kgp@gmail.com
 */
package concepts.distsem.ml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pitt.search.semanticvectors.CloseableVectorStore;
import pitt.search.semanticvectors.ObjectVector;
import pitt.search.semanticvectors.VectorStoreReader;

import util.Sid;
import util.SimpleCommandLineParser;
import util.chunk.SidChunker;
import dragon.nlp.Sentence;
import dragon.nlp.Token;
import dragon.nlp.Word;
import dragon.nlp.tool.HeppleTagger;
import dragon.nlp.tool.lemmatiser.EngLemmatiser;

/**
 * all features including the offset conjuctions
 * @author sjonnalagadda
 *
 */
public class SVM_FeaturePrinter {
	
	
	public String sent;
	public Sentence sentence;
	public ArrayList<Token> tokens;
	public String[] stringTokens;
	public String[] posTags;
	public String[] phraseChunks;
	
	public ArrayList<String> labels;
	
	public Pattern conPattern;
	
	
	public SVM_FeaturePrinter() {
		conPattern = Pattern.compile("c=\"(.+)\"\\s+(\\d+):(\\d+)\\s+\\d+:(\\d+)\\|\\|t=\"(.+)\"");
	}
	
	
	/**
	 * 
	 */
	public SVM_FeaturePrinter(String i2b2Thesarus, String clinicalThesarus) {
		conPattern = Pattern.compile("c=\"(.+)\"\\s+(\\d+):(\\d+)\\s+\\d+:(\\d+)\\|\\|t=\"(.+)\"");
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args);
		String termVectorFile = "../drxntermvectors_h2000_5_6.bin";
		
		/*String i2b2Thesarus = "models/simFindStores_i2b2.txt";
		String clinicalThesarus = parser.getValue("thesarus", "th");*/
		String outputFile = "../i2b2OUT_4svmV2.arff";
		System.setOut(new PrintStream(outputFile));
		
		SVM_FeaturePrinter fp = new SVM_FeaturePrinter();
		HashSet<String> tokenTypeSet = new HashSet<String>();
		//PrintWriter pwr = new PrintWriter(new FileWriter(outputFile));
				//"outputs/trainingCorpusI2B2_SimFind_close_local_10_clinical_h2000_5_6_20.txt"));
		
		boolean training = false; 
		
		File dir_txt=new File("../competition_txt");
		String[] txtFiles = dir_txt.list();
		for (String txtFile : txtFiles) {
			fp.stringTokens = null;
			fp.posTags = null;
			fp.phraseChunks = null;
			//Sid.log(txtFile);
			
			int line=0;
			BufferedReader br_txt = new BufferedReader(new FileReader("../competition_txt/"+txtFile));
			while(br_txt.ready()){
				
				fp.sent=br_txt.readLine();
				fp.stringTokens = fp.sent.split("\\s+");
				
				fp.labels = new ArrayList<String>();
				for (int index = 0; index < fp.stringTokens.length; index++) {
					fp.labels.add("O");				
				}
				
				
				
				if (training) {
					BufferedReader br_con = new BufferedReader(
							new FileReader(
									"../trn_con/"
											+ txtFile.substring(0,
													txtFile.length() - 3)
											+ "con"));
					while (br_con.ready()) {
						String conLine = br_con.readLine();
						Matcher mCon = fp.conPattern.matcher(conLine);
						if (mCon.find()) {
							int lineNum = Integer.parseInt(mCon.group(2)) - 1;
							if (lineNum != line)
								continue;
							int init = Integer.parseInt(mCon.group(3));
							int end = Integer.parseInt(mCon.group(4));
							String label = mCon.group(5);
							for (int i = init; i <= end; i++) {
								fp.labels.set(i, "I" + label);
							}
							/*System.out.println(conLine);
							System.out.println("lineNum:\t"+mCon.group(2));
							System.out.println("init:\t"+mCon.group(3));
							System.out.println("end:\t"+mCon.group(4));
							System.out.println("label:\t"+mCon.group(5));*/
						}
					}
					br_con.close();
				}
				
				for (int index = 0; index < fp.stringTokens.length; index++) {
					//System.out.println(fp.stringTokens[index]+"\t"+fp.labels.get(index));
					tokenTypeSet.add(fp.stringTokens[index].toLowerCase()+"\t"+fp.labels.get(index));
				}
				 
				//pwr.println();
				line++;
			}
			br_txt.close();
			//pwr.flush();
		}
		
		CloseableVectorStore  vecReader= VectorStoreReader.openVectorStore(termVectorFile);
		Enumeration<ObjectVector> vecs = vecReader.getAllVectors();
		boolean printHeader=true;
		while(vecs.hasMoreElements()){
			ObjectVector objVec = vecs.nextElement();
			
			if(printHeader){
				System.out.println("@RELATION DistSem");
				//System.out.println("@ATTRIBUTE term STRING");
				for(int i=0;i<objVec.getVector().length;i++){
					System.out.println("@ATTRIBUTE dimension"+i+ " NUMERIC");
				}
				System.out.println("@ATTRIBUTE class        {pr,te,tr,ot}\n@DATA");
				printHeader=false;
			}
			
			if(tokenTypeSet.contains(objVec.getObject()+"\tIproblem")&&
					!tokenTypeSet.contains(objVec.getObject()+"\tItest")&&
					!tokenTypeSet.contains(objVec.getObject()+"\tItreatment")&&
					!tokenTypeSet.contains(objVec.getObject()+"\tO")){
				System.out.println("%\t"+objVec.getObject().toString());
				for (float p : objVec.getVector()) {
					System.out.print(String.format("%.2g", p)+",");
				}
				System.out.println("pr");
			}
			
			else if(!tokenTypeSet.contains(objVec.getObject()+"\tIproblem")&&
					tokenTypeSet.contains(objVec.getObject()+"\tItest")&&
					!tokenTypeSet.contains(objVec.getObject()+"\tItreatment")&&
					!tokenTypeSet.contains(objVec.getObject()+"\tO")){
				System.out.println("%\t"+objVec.getObject().toString());
				for (float p : objVec.getVector()) {
					System.out.print(String.format("%.2g", p)+",");
				}
				System.out.println("te");
			}
			
			else if(!tokenTypeSet.contains(objVec.getObject()+"\tIproblem")&&
					!tokenTypeSet.contains(objVec.getObject()+"\tItest")&&
					tokenTypeSet.contains(objVec.getObject()+"\tItreatment")&&
					!tokenTypeSet.contains(objVec.getObject()+"\tO")){
				System.out.println("%\t"+objVec.getObject().toString());
				for (float p : objVec.getVector()) {
					System.out.print(String.format("%.2g", p)+",");
				}
				System.out.println("tr");
			}
			
			else if(!tokenTypeSet.contains(objVec.getObject()+"\tIproblem")&&
					!tokenTypeSet.contains(objVec.getObject()+"\tItest")&&
					!tokenTypeSet.contains(objVec.getObject()+"\tItreatment")&&
					tokenTypeSet.contains(objVec.getObject()+"\tO")){
				System.out.println("%\t"+objVec.getObject().toString());
				for (float p : objVec.getVector()) {
					System.out.print(String.format("%.2g", p)+",");
				}
				System.out.println("ot");
			}
			
		}
		//pwr.close();
				
	}
	
}
