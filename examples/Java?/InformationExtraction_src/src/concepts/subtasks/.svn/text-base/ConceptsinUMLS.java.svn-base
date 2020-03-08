package concepts.subtasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import util.Sid;

/**
 * Extracts names of the concepts
 * @author sjonnal3
 *
 */
public class ConceptsinUMLS {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		loadDictionaries();

	}

	private static void loadDictionaries() throws IOException {
		String[] problemTypes = {"pathologic functions", "disease or syndrome", "mental or behavioral dysfunction", "cell or molecular dysfunction", "congenital abnormality", "acquired abnormality", "injury or poisoning", "anatomicabnormality", "neoplastic process", "virus/bacterium", "sign or symptom"};
		ArrayList<String> problemTypesList = new ArrayList<String>( Arrays.asList(problemTypes));
		ArrayList<String> problemCUIs = new ArrayList<String>();
		PrintWriter pwrProblems = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("UMLSproblems.txt"), "UTF-8")));
		
		String[] testTypes = {"laboratory procedure", "diagnostic procedure"};
		ArrayList<String> testTypesList = new ArrayList<String>( Arrays.asList(testTypes));
		ArrayList<String> testCUIs = new ArrayList<String>();
		PrintWriter pwrTests = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("UMLStests.txt"), "UTF-8")));
		
		String[] treatmentTypes = {"therapeutic or preventive procedure", "medical device", "steroid", "pharmacologic substance", "biomedical or dental material", "antibiotic", "clinical drug", "drug delivery device"};
		ArrayList<String> treatmentTypesList = new ArrayList<String>( Arrays.asList(treatmentTypes));
		ArrayList<String> treatmentCUIs = new ArrayList<String>();
		PrintWriter pwrTreatments = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("UMLStreatment.txt"), "UTF-8")));
		
		BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream("dict/MRSTY.RRF"), "UTF-8"));
		while(br1.ready()){
			String line = br1.readLine();
			String[] tokens = line.split("\\|");
			String CUI = tokens[0];
			String semanticType = tokens[3];
			if(problemTypesList.contains(semanticType.toLowerCase())){
				Sid.log(line);
				problemCUIs.add(CUI);
			}
			if(testTypesList.contains(semanticType.toLowerCase())){
				Sid.log(line);
				testCUIs.add(CUI);
			}
			if(treatmentTypesList.contains(semanticType.toLowerCase())){
				Sid.log(line);
				treatmentCUIs.add(CUI);
			}
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("dict/MRCONSO_agg.RRF"), "UTF-8"));
		while(br.ready()){
			String line = br.readLine();
			String[] tokens = line.split("\\|");
			String CUI = tokens[0];
			String entry = tokens[14];
			if(problemCUIs.contains(CUI)){
				/*Sid.log(CUI+"\t"+entry);
				problems.add(entry);*/
				pwrProblems.println(entry);
			}
			if(testCUIs.contains(CUI)){
				/*Sid.log(CUI+"\t"+entry);
				tests.add(entry);*/
				pwrTests.println(entry);
			}
			if(treatmentCUIs.contains(CUI)){
				/*Sid.log(CUI+"\t"+entry);
				treatments.add(entry);*/
				pwrTreatments.println(entry);
			}
		}
		
		br.close();br1.close();pwrProblems.close();pwrTests.close();pwrTreatments.close();
	}

}
