package subtasks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import util.Sid;

public class ExtractSnippets {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		PrintWriter pwr = new PrintWriter(new FileWriter("dict/relSnippets.txt"));
		
		BufferedReader br = new BufferedReader(new FileReader("dict/relPatternsWTags.txt"));
		while(br.ready()){
			String line = (br.readLine());
			String[] tokens = line.split("\\s");
			int start=-1,end=-1;
			int length=0;
			for (String token : tokens) {
				if(token.equals("PROBLEM_")||token.equals("TREATMENT_")||token.equals("TEST_")){
					if(start==-1)
						start=length;
					else{
						end=length;
						String snippet="";
						for(int count=start;count<=end;count++){
							snippet+=tokens[count]+" ";
						}
						snippet=snippet.trim();
						Sid.log(line+"||"+snippet);
						pwr.println(line+"||"+snippet);
						continue;
					}
				}
					
				length++;
			}
			
		}
		
		pwr.close();
		
	}

}
