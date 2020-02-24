package concepts.subtasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;

import util.Sid;

/**
 * just remove duplicates
 * @author sjonnalagadda
 *
 */
public class BuildConceptDictionaries {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String dictFileName = "dict/UMLStreatment.txt";
		String dictChangedName = "dict/UMLStreatmentsUnique.txt";
		HashSet<String> set = new HashSet<String>();
		int count =0;
		
		BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(dictFileName), "UTF-8"));
		while(br1.ready()){
			String line = br1.readLine();
			set.add(line);
			count++;
		}
		Sid.log(count);
		Sid.log(set.size());
		br1.close();
		
		/*br1 = new BufferedReader(new InputStreamReader(new FileInputStream("dict/DrugDictionary.txt"), "UTF-8"));
		while(br1.ready()){
			String line = br1.readLine();
			set.add(line);
			count++;
		}
		Sid.log(count);
		Sid.log(set.size());
		br1.close();*/
		
		PrintWriter pwr = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dictChangedName), "UTF-8")));
		for (String string : set) {
			pwr.println(string);
		}
		pwr.close();
		
	}

}
