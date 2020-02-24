package subtasks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import util.Sid;

public class ReformatSnippets {
	
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("dict/relSnippetsModified_v2_tab.txt"));
		PrintWriter pwr = new PrintWriter(new FileWriter("dict/relSnippetsModified_v2.txt"));
		while(br.ready()){
			String text=br.readLine();
			
			Sid.log("\t"+text);
			String[] tokens=text.split("\t");
			for(int i=0; i<tokens.length; i++){
				if(tokens[i].startsWith("\""))
					tokens[i]=tokens[i].substring(1);
				if(tokens[i].endsWith("\""))
					tokens[i]=tokens[i].substring(0,tokens[i].length()-1);
			}
			Sid.log(tokens[0]+"||"+tokens[1]+"||"+tokens[2]);
			pwr.println(tokens[0]+"||"+tokens[1]+"||"+tokens[2]);
		}
		br.close();
		pwr.close();
	}
	
}
