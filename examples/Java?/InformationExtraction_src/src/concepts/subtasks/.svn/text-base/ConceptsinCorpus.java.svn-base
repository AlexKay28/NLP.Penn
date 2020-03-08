package concepts.subtasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Sid;

/**
 * print concepts from corpus
 * @author sjonnalagadda
 *
 */
public class ConceptsinCorpus {

	public static void main(String[] args) throws NumberFormatException, IOException{
		PrintWriter pwrProblems = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("I2B2problems.txt"), "UTF-8")));
		PrintWriter pwrTests = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("I2B2tests.txt"), "UTF-8")));
		PrintWriter pwrTreatments = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("I2B2treatments.txt"), "UTF-8")));
		
		File dir_txt=new File("../trn_txt");
		String[] txtFiles = dir_txt.list();
		for (String txtFile : txtFiles) {
			BufferedReader br1 = new BufferedReader(new FileReader("../trn_con/"+txtFile.substring(0,txtFile.length()-3)+"con"));
			while(br1.ready()){
				String concept = br1.readLine();
				Pattern conPattern = Pattern.compile("c=\"(.+)\" \\d+:\\d+ \\d+:\\d+\\|\\|t=\"(.+)\"");
				Matcher conMatcher = conPattern.matcher(concept);
				if(conMatcher.find()){
					Sid.log(conMatcher.group(1)+"\t"+conMatcher.group(2));
					if(conMatcher.group(2).equals("problem"))
						pwrProblems.println(txtFile+"\t"+conMatcher.group(1));
					else if(conMatcher.group(2).equals("test"))
						pwrTests.println(txtFile+"\t"+conMatcher.group(1));
					else if(conMatcher.group(2).equals("treatment"))
						pwrTreatments.println(txtFile+"\t"+conMatcher.group(1));
					
				}
			}
			br1.close();
		}
		
		pwrProblems.close();pwrTests.close();pwrTreatments.close();
	}

}
