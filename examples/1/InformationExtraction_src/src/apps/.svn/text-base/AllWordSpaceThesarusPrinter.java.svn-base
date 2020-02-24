package apps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import pitt.search.semanticvectors.CloseableVectorStore;
import pitt.search.semanticvectors.CompoundVectorBuilder;
import pitt.search.semanticvectors.ObjectVector;
import pitt.search.semanticvectors.VectorStore;
import pitt.search.semanticvectors.VectorStoreRAM;
import pitt.search.semanticvectors.VectorStoreReader;
import pitt.search.semanticvectors.VectorUtils;
import util.SimpleCommandLineParser;
import util.tokenizer.LuceneTokenizer;

/**
 * CommandLine Example: 
 * java -cp bin/:lucene-core-3.0.2.jar apps.AllWordSpaceThesarusPrinter --term /home/siddhartha/currentProjects/IE/IE_java/models/i2b2_permtermvectors.bin --out i2b2_500d_permutation_thesarus_match.txt
 * @author siddhartha
 *
 */

public class AllWordSpaceThesarusPrinter {

	
	public AllWordSpaceThesarusPrinter(){
		
	}
	
	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String termVectorFile = "C:/Users/sjonnalagadda/Documents/drxntermvectors.bin";
		String categoriesFile = "../IE_data/_UMLSALL_uniq.txt";
		int numSims=10; 
		float simThreshold = -1;
		String outputFile = "../IE_data/multi_thesarusMatch_weighted.txt";
		
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args);
		if(parser.getValue("termVectorFile", "term")!=null)
			termVectorFile = parser.getValue("termVectorFile", "term");
		if(parser.getValue("categoriesFile", "cat")!=null)		
			categoriesFile = parser.getValue("categoriesFile", "cat");
		if(parser.getValue("numSims", "num")!=null)		
			numSims = Integer.parseInt(parser.getValue("numSims", "num"));
		if(parser.getValue("simThreshold")!=null)		
			simThreshold = Float.parseFloat(parser.getValue("simThreshold"));
		if(parser.getValue("outputFile", "out")!=null)		
			outputFile = parser.getValue("outputFile", "out");
		
		doAllWordPrinting(termVectorFile, categoriesFile, outputFile, numSims, simThreshold);
		
	}

	private static void doAllWordPrinting(String termVectorFile,
			String categoriesFile, String outputFile, int numSims,
			float simThreshold) throws IOException {
		VectorStoreRAM  vecReader= new VectorStoreRAM();
		vecReader.InitFromFile(termVectorFile);
				
		HashMap<String, String> phraseCategory = new HashMap<String, String>();
		HashMap<String, float[]> phraseVec = new HashMap<String, float[]>();
		BufferedReader br = new BufferedReader(new FileReader(categoriesFile));
		while(br.ready()){
			String line = br.readLine().trim().toLowerCase();
			String[] phraseCat = line.split("\t");
			
			if(!phraseCat[0].contains(" "))
				continue;
			
			phraseCategory.put(phraseCat[0].toLowerCase(), phraseCat[1]);
			
			float [] strValue=CompoundVectorBuilder.getQueryVector(vecReader, null, LuceneTokenizer.tokenize(phraseCat[0]).toArray(new String[0]));
			if(strValue!=null && !VectorUtils.isZeroVector(strValue)){
				System.out.println("found vector for "+phraseCat[0]);
				phraseVec.put(phraseCat[0].toLowerCase(), strValue);
			}
		}
		br.close();
		

		PrintWriter pwr = new PrintWriter(new FileWriter(outputFile));
		for (String str1 : phraseVec.keySet()) {
			String origCategory = phraseCategory.get(str1);
			float [] str1Value=phraseVec.get(str1);
			pwr.print(str1+"\t"+origCategory);
			HashMap<String, Float> maxs = new HashMap<String, Float>();
			for (String str2 : phraseVec.keySet()) {
				if(str1.equals(str2))
					continue;
				float [] str2Value=phraseVec.get(str2);
				float cosine=VectorUtils.scalarProduct(str1Value, str2Value);
				maxs = addMax(maxs,str2,cosine,numSims,simThreshold);
			}
			
			for (String neighbor : maxs.keySet()) {//printing both the neighbor and its similarity value
				pwr.print("\t"+phraseCategory.get(neighbor)+"_"+maxs.get(neighbor));
			}
			pwr.println();
		}
		
		pwr.close();
		
	}

	
	private static HashMap<String, Float> addMax(HashMap<String, Float> maxs,
			String str2, float sim, int numSims, float simThreshold) {
		if(maxs.size()<numSims && sim>simThreshold)
			maxs.put(str2, sim);
		else{
			float min = Integer.MAX_VALUE;String minKey=null;
			Set<String> maxKeySet = maxs.keySet();
			for (String key : maxKeySet) {
				if(min>maxs.get(key)){
					min=maxs.get(key);
					minKey=key;
				}					
			}
			if(sim>min){
				maxs.remove(minKey);
				maxs.put(str2, sim);
			}
		}
		return maxs;
	}


}
