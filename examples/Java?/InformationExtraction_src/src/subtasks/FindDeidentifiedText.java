package subtasks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import util.Sid;

public class FindDeidentifiedText {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("dict/relPatternsWTags_withdetails.txt"));
		HashSet<String> deIds = new HashSet<String>();
		while(br.ready()){
			String line=br.readLine();
			String[] tokens = line.split("[\\s\\[]");
			for (String token : tokens) {
				if(token.contains("**")){
					deIds.add(token);
				}
			}
		}
		for (String deId : deIds) {
			Sid.log(deId);
		}
	}

}
