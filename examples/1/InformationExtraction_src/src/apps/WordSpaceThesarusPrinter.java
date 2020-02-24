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
import pitt.search.semanticvectors.ObjectVector;
import pitt.search.semanticvectors.VectorStoreReader;
import pitt.search.semanticvectors.VectorUtils;
import util.SimpleCommandLineParser;

/**
 * CommandLine Example: 
 * java -cp bin/:lucene-core-3.0.2.jar app.WordSpaceThesarusPrinter --term /home/siddhartha/currentProjects/IE/IE_java/models/i2b2_permtermvectors.bin --out i2b2_500d_permutation_thesarus_match.txt
 * @author siddhartha
 *
 */

public class WordSpaceThesarusPrinter {

	
	public WordSpaceThesarusPrinter(){
		
	}
	
	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String termVectorFile = "data/clinicalTrials_permtermvectors_2000d.bin";
		String problemsListFile = "data/_UMLSproblems_uniq.txt";
		String testsListFile = "data/_UMLStests_uniq.txt";
		String treatmentsListFile = "data/_UMLStreatments_uniq.txt";
		String otherListFile = "data/_UMLSothers_uniq.txt";
		int numSims=10; 
		float simThreshold = -1;
		String outputFile = "_clinicalTrials_2000d_permutation_thesarusMatch_weighted.txt";
		
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args);
		if(parser.getValue("termVectorFile", "term")!=null)
			termVectorFile = parser.getValue("termVectorFile", "term");
		if(parser.getValue("problemsListFile", "pr")!=null)		
			problemsListFile = parser.getValue("problemsListFile", "pr");
		if(parser.getValue("testsListFile", "te")!=null)		
			testsListFile = parser.getValue("testsListFile", "te");
		if(parser.getValue("treatmentsListFile", "tr")!=null)		
			treatmentsListFile = parser.getValue("treatmentsListFile", "tr");
		if(parser.getValue("otherListFile", "ot")!=null)		
			otherListFile = parser.getValue("otherListFile", "ot");
		if(parser.getValue("numSims", "num")!=null)		
			numSims = Integer.parseInt(parser.getValue("numSims", "num"));
		if(parser.getValue("simThreshold")!=null)		
			simThreshold = Float.parseFloat(parser.getValue("simThreshold"));
		if(parser.getValue("outputFile", "out")!=null)		
			outputFile = parser.getValue("outputFile", "out");
		
		doSingleWordTesting(termVectorFile, problemsListFile, testsListFile, treatmentsListFile, otherListFile, outputFile, numSims, simThreshold);
		
	}

	private static void doSingleWordTesting(String termVectorFile, String problemsListFile, String testsListFile, String treatmentsListFile, String otherListFile, String outputFile, int numSims, float simThreshold) throws IOException {
		
		CloseableVectorStore  vecReader= VectorStoreReader.openVectorStore(termVectorFile);
		
		HashMap<String, float[]> allVecs = new HashMap<String, float[]>();
		
		ArrayList<String> problems = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(problemsListFile));
		while(br.ready()){
			String line = br.readLine().trim().toLowerCase();
			if(line.contains(" "))
				continue;
			problems.add(line);
		}
		br.close();
		
		ArrayList<String> tests = new ArrayList<String>();
		br = new BufferedReader(new FileReader(testsListFile));
		while(br.ready()){
			String line = br.readLine().trim().toLowerCase();
			if(line.contains(" "))
				continue;
			tests.add(line);
		}
		br.close();
		
		ArrayList<String> treatments = new ArrayList<String>();
		br = new BufferedReader(new FileReader(treatmentsListFile));
		while(br.ready()){
			String line = br.readLine().trim().toLowerCase();
			if(line.contains(" "))
				continue;
			treatments.add(line);
		}
		br.close();
		
		ArrayList<String> others = new ArrayList<String>();
		br = new BufferedReader(new FileReader(otherListFile));
		while(br.ready()){
			String line = br.readLine().trim().toLowerCase();
			if(line.contains(" "))
				continue;
			others.add(line);
		}
		br.close();
		
		Enumeration<ObjectVector> vecs = vecReader.getAllVectors();
		while(vecs.hasMoreElements()){
			ObjectVector objVec = vecs.nextElement();
			if(problems.contains(objVec.getObject())){
				allVecs.put((String) objVec.getObject(), objVec.getVector());
			}
			if(tests.contains(objVec.getObject())){
				allVecs.put((String) objVec.getObject(), objVec.getVector());
			}
			if(treatments.contains(objVec.getObject())){
				allVecs.put((String) objVec.getObject(), objVec.getVector());
			}
			if(others.contains(objVec.getObject())){
				allVecs.put((String) objVec.getObject(), objVec.getVector());
			}
		}
		
		PrintWriter pwr = new PrintWriter(new FileWriter(outputFile));
		for (String str1 : allVecs.keySet()) {
			String origCategory = getCategory(problems, tests, treatments, str1);
			pwr.print(str1+"\t"+origCategory);
			HashMap<String, Float> maxs = new HashMap<String, Float>();
			for (String str2 : allVecs.keySet()) {
				float [] str1Value=allVecs.get(str1), str2Value=allVecs.get(str2);
				float cosine=VectorUtils.scalarProduct(str1Value, str2Value);
				maxs = addMax(maxs,str2,cosine,numSims,simThreshold);
			}
			for (String neighbor : maxs.keySet()) {//printing both the neighbor and its similarity value
				pwr.print("\t"+getCategory(problems, tests, treatments, neighbor)+"_"+maxs.get(neighbor));
			}
			pwr.println();
		}

		pwr.close();
		
		vecReader.close();
		
	}

	private static String getCategory(ArrayList<String> problems,
			ArrayList<String> tests, ArrayList<String> treatments, String str1) {
		if(problems.contains(str1))
			return "pr";
		else if(tests.contains(str1))
			return "te";
		else if(treatments.contains(str1))
			return "tr";
		else 
			return "ot";
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
