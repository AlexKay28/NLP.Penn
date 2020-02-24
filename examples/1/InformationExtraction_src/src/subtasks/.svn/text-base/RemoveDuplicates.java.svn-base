package subtasks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

public class RemoveDuplicates {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String inputFile="dict/sent_i2b2UnannotatedDocs_1653.txt";;
		String outputFile="dict/sent_i2b2UnannotatedDocs_1653_unique.txt";;
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		HashSet<String> sentSet = new HashSet<String>();
		while(br.ready())
			sentSet.add(br.readLine().trim().toLowerCase());
		br.close();
		PrintWriter pwr = new PrintWriter(new FileWriter(outputFile));
		for (String string : sentSet) {
			pwr.println(string);
		}
		pwr.close();

	}

}
