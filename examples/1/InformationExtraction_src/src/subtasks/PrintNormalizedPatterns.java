package subtasks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import relations.Normalizer;

public class PrintNormalizedPatterns {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("../team_wang/justPatterns.txt"));
		PrintWriter pwr = new PrintWriter(new FileWriter("../team_wang/NormalizedPatterns.txt"));
		while(br.ready()){
			String line = br.readLine();
			pwr.println(Normalizer.normalizeSentence(line));
		}
		pwr.close();
		br.close();
	}

}
