/**
 * @author siddhartha
 */
package subtasks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * not necessary
 * @author sjonnalagadda
 *
 */
public class CreateUnannotedDocuments {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String fileName = "../allUnannotated/deid_surrogate_test_all_groundtruth_version2.xml";
		String outputDir= "../allUnannotated/docs/";
		/*File dir = new File(fileName.substring(0, fileName.lastIndexOf('.')));
		dir.mkdir();*/
		
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String text="";
		while(br.ready()){
			String line= br.readLine();
			text+=line+"\n";
			if(line.matches("<RECORD ID=\"(\\d+)\">"))
				System.out.println(line);
		}
		br.close();
		
		Pattern recP = Pattern.compile("<RECORD ID=\"(\\d+)\">((.(?!</RECORD>))+).</RECORD>",Pattern.DOTALL);
		Matcher recM = recP.matcher(text);
		while(recM.find()){
			System.out.println(recM.group(2).replaceAll("</?[A-Z\\s=\"]+>", "")+"\n\n\n\n\n\n");
			PrintWriter pwr = new PrintWriter(new FileWriter(outputDir+fileName.substring(fileName.lastIndexOf('/'), fileName.lastIndexOf('.'))+recM.group(1)+".txt"));
			pwr.print(recM.group(2).replaceAll("</?[A-Z\\s=\"]+>", "").trim());
			pwr.close();
		}
		
		
	}
	
	

}
