package apps;

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
import java.util.HashMap;

import util.SimpleCommandLineParser;



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
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args);
		String type = parser.getValue("type", "t");
		if(type.equals("all"))
			loadAllDictionaries();
		else
			loadDictionaries();
		
	}

	private static void loadAllDictionaries() throws IOException {
		PrintWriter pwr = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("_UMLSALL.txt"), "UTF-8")));
		HashMap<String, String> cui_type = new HashMap<String, String>();
		
		BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream("/media/XIQ_HDD_5/extractedFiles/sjonnalagadda/Desktop/Projects_Summer_2010/i2b2_backUp/i2b2_java/dict/MRSTY.RRF"), "UTF-8"));
		while(br1.ready()){
			String line = br1.readLine();
			String[] tokens = line.split("\\|");
			String CUI = tokens[0];
			String semanticType = tokens[3];
			String currentType = cui_type.get(CUI);
			if(currentType==null)
				cui_type.put(CUI, semanticType);
			else
				cui_type.put(CUI, semanticType+"_"+currentType);
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/media/XIQ_HDD_5/extractedFiles/sjonnalagadda/Desktop/Projects_Summer_2010/i2b2_backUp/i2b2_java/dict/MRCONSO_agg.RRF"), "UTF-8"));
		while(br.ready()){
			String line = br.readLine();
			String[] tokens = line.split("\\|");
			if(tokens.length<14)
				continue;
			String CUI = tokens[0];
			String entry = tokens[14];
			pwr.println(entry+"\t"+cui_type.get(CUI));
		}
		
		br.close();br1.close();
		
	}

	private static void loadDictionaries() throws IOException {
		String[] problemTypes = {"pathologic functions", "disease or syndrome", "mental or behavioral dysfunction", "cell or molecular dysfunction", "congenital abnormality", "acquired abnormality", "injury or poisoning", "anatomicabnormality", "neoplastic process", "virus/bacterium", "sign or symptom"};
		ArrayList<String> problemTypesList = new ArrayList<String>( Arrays.asList(problemTypes));
		ArrayList<String> problemCUIs = new ArrayList<String>();
		PrintWriter pwrProblems = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("_UMLSproblems.txt"), "UTF-8")));
		
		String[] testTypes = {"laboratory procedure", "diagnostic procedure"};
		ArrayList<String> testTypesList = new ArrayList<String>( Arrays.asList(testTypes));
		ArrayList<String> testCUIs = new ArrayList<String>();
		PrintWriter pwrTests = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("_UMLStests.txt"), "UTF-8")));
		
		String[] treatmentTypes = {"therapeutic or preventive procedure", "medical device", "steroid", "pharmacologic substance", "biomedical or dental material", "antibiotic", "clinical drug", "drug delivery device"};
		ArrayList<String> treatmentTypesList = new ArrayList<String>( Arrays.asList(treatmentTypes));
		ArrayList<String> treatmentCUIs = new ArrayList<String>();
		PrintWriter pwrTreatments = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("_UMLStreatment.txt"), "UTF-8")));

		PrintWriter pwrOthers = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("_UMLSOthers.txt"), "UTF-8")));
		
		BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream("/media/XIQ_HDD_5/extractedFiles/sjonnalagadda/Desktop/Projects_Summer_2010/i2b2_backUp/i2b2_java/dict/MRSTY.RRF"), "UTF-8"));
		while(br1.ready()){
			String line = br1.readLine();
			String[] tokens = line.split("\\|");
			String CUI = tokens[0];
			String semanticType = tokens[3];
			if(problemTypesList.contains(semanticType.toLowerCase())){
				//log(line);
				problemCUIs.add(CUI);
			}
			if(testTypesList.contains(semanticType.toLowerCase())){
				//log(line);
				testCUIs.add(CUI);
			}
			if(treatmentTypesList.contains(semanticType.toLowerCase())){
				//log(line);
				treatmentCUIs.add(CUI);
			}
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/media/XIQ_HDD_5/extractedFiles/sjonnalagadda/Desktop/Projects_Summer_2010/i2b2_backUp/i2b2_java/dict/MRCONSO_agg.RRF"), "UTF-8"));
		while(br.ready()){
			String line = br.readLine();
			String[] tokens = line.split("\\|");
			String CUI = tokens[0];
			String entry = tokens[14];
			boolean notFound=true;
			if(problemCUIs.contains(CUI)){
				pwrProblems.println(entry);notFound=false;
			}
			if(testCUIs.contains(CUI)){
				pwrTests.println(entry);notFound=false;
			}
			if(treatmentCUIs.contains(CUI)){
				pwrTreatments.println(entry);notFound=false;
			}
			if(notFound)
				pwrOthers.println(entry);
		}
		
		br.close();br1.close();pwrProblems.close();pwrTests.close();pwrTreatments.close();pwrOthers.close();
	}

}