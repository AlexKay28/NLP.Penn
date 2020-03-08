package apps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import pitt.search.semanticvectors.CloseableVectorStore;
import pitt.search.semanticvectors.ObjectVector;
import pitt.search.semanticvectors.VectorStoreReader;
import util.SimpleCommandLineParser;

/**
 * generates ARFF format files
 * @author sjonnalagadda
 *
 */
public class UMLS2ARFF {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String termVectorFile = "data/drxntermvectors.bin";
		String problemsListFile = "data/_UMLSproblems_uniq.txt";
		String testsListFile = "data/_UMLStests_uniq.txt";
		String treatmentsListFile = "data/_UMLStreatments_uniq.txt";
		String otherListFile = "data/_UMLSothers_uniq.txt";
		
		String outputFile = "UMLS_4svm.arff";
		System.setOut(new PrintStream(outputFile));
		
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
		if(parser.getValue("outputFile", "out")!=null)		
			outputFile = parser.getValue("outputFile", "out");
		
		CloseableVectorStore  vecReader= VectorStoreReader.openVectorStore(termVectorFile);
		
		//HashMap<String, float[]> allVecs = new HashMap<String, float[]>();
		
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
		boolean printHeader=true;
		while(vecs.hasMoreElements()){
			ObjectVector objVec = vecs.nextElement();
			if(printHeader){
				System.out.println("@RELATION DistSem");
				for(int i=0;i<objVec.getVector().length;i++){
					System.out.println("@ATTRIBUTE dimension"+i+ " NUMERIC");
				}
				System.out.println("@ATTRIBUTE class        {pr,te,tr,ot}\n@DATA");
				printHeader=false;
			}
			
			if(problems.contains(objVec.getObject())){
				for (float p : objVec.getVector()) {
					System.out.print(String.format("%.2g", p)+",");
				}
				System.out.println("pr");
			}
			if(tests.contains(objVec.getObject())){
				for (float p : objVec.getVector()) {
					System.out.print(String.format("%.2g", p)+",");
				}
				System.out.println("te");
			}
			if(treatments.contains(objVec.getObject())){
				for (float p : objVec.getVector()) {
					System.out.print(String.format("%.2g", p)+",");
				}
				System.out.println("tr");
			}
			if(others.contains(objVec.getObject())){
				for (float p : objVec.getVector()) {
					System.out.print(String.format("%.2g", p)+",");
				}
				System.out.println("ot");
			}
		}
	}

}
