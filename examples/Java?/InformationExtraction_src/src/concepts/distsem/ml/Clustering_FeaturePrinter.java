/**
 * email: sid.kgp@gmail.com
 */
package concepts.distsem.ml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashSet;

import pitt.search.semanticvectors.CloseableVectorStore;
import pitt.search.semanticvectors.ObjectVector;
import pitt.search.semanticvectors.VectorStoreReader;
import util.SimpleCommandLineParser;
import dragon.nlp.Sentence;

/**
 * all features including the offset conjuctions
 * @author sjonnalagadda
 *
 */
public class Clustering_FeaturePrinter {
	
	
	public String sent;
	public Sentence sentence;
	public String[] stringTokens;
	
	public Clustering_FeaturePrinter() {
	}
	
	
	/**
	 * 
	 */
	public Clustering_FeaturePrinter(String i2b2Thesarus, String clinicalThesarus) {
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args);
		String termVectorFile = "../IE_data/drxntermvectors_h2000_5_6.bin";
		if(parser.getValue("termvector") != null)
			termVectorFile=parser.getValue("termvector");
		
		/*String i2b2Thesarus = "models/simFindStores_i2b2.txt";
		String clinicalThesarus = parser.getValue("thesarus", "th");*/
		String outputFile = "../IE_data/i2b2_4clusteringAll.arff";
		System.setOut(new PrintStream(outputFile));
		
		Clustering_FeaturePrinter fp = new Clustering_FeaturePrinter();
		HashSet<String> tokens = new HashSet<String>();
		//PrintWriter pwr = new PrintWriter(new FileWriter(outputFile));
				//"outputs/trainingCorpusI2B2_SimFind_close_local_10_clinical_h2000_5_6_20.txt"));
		
		File dir_txt=new File("../IE_data/trn_txt");
		String[] txtFiles = dir_txt.list();
		for (String txtFile : txtFiles) {
			fp.stringTokens = null;
			int line=0;
			BufferedReader br_txt = new BufferedReader(new FileReader("../IE_data/trn_txt/"+txtFile));
			while(br_txt.ready()){
				fp.sent=br_txt.readLine();
				fp.stringTokens = fp.sent.split("\\s+");
				
				for (int index = 0; index < fp.stringTokens.length; index++) {
					//System.out.println(fp.stringTokens[index]+"\t"+fp.labels.get(index));
					tokens.add(fp.stringTokens[index].toLowerCase());
				}
				 
				//pwr.println();
				line++;
			}
			br_txt.close();
			//pwr.flush();
		}
		
		dir_txt=new File("../IE_data/competition_txt");
		txtFiles = dir_txt.list();
		for (String txtFile : txtFiles) {
			fp.stringTokens = null;
			int line=0;
			BufferedReader br_txt = new BufferedReader(new FileReader("../IE_data/competition_txt/"+txtFile));
			while(br_txt.ready()){
				fp.sent=br_txt.readLine();
				fp.stringTokens = fp.sent.split("\\s+");
				
				for (int index = 0; index < fp.stringTokens.length; index++) {
					//System.out.println(fp.stringTokens[index]+"\t"+fp.labels.get(index));
					tokens.add(fp.stringTokens[index].toLowerCase());
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
				System.out.println("@RELATION DistSemClusters");
				//System.out.println("@ATTRIBUTE term STRING");
				for(int i=0;i<objVec.getVector().length;i++){
					System.out.println("@ATTRIBUTE dimension"+i+ " NUMERIC");
				}
				System.out.println("@DATA");
				printHeader=false;
			}
			
			if(tokens.contains(objVec.getObject())){
				//System.out.print(objVec.getObject().toString().replaceAll(",", "COMMA")+",");
				System.out.println("%\t"+objVec.getObject().toString());
				for (float p : objVec.getVector()) {
					System.out.print(String.format("%.2g", p)+",");
				}
				System.out.println("");
			}
		}
		//pwr.close();
				
	}
	
}
