package proteinTagging.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import util.Sid;


public class RePrinter {

	
	/**
	 * reprints from the XML file. This more perfect.
	 * @throws IOException 
	 * 
	 */
	public static void reprint() throws IOException{
		ArrayList<String> allSentencesNoLabels = new ArrayList<String>();
		ArrayList<String> allSentencesProtLabels = new ArrayList<String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("dict/GENIAcorpus3.02.xml"));
			while(br.ready()){
				String line;
				line = br.readLine();
				if(line.contains("<sentence>")){
					String string = line.substring(line.indexOf("<sentence>"));
					allSentencesNoLabels.add(XMLGeneral.removeAllTags(string));
					allSentencesProtLabels.add(XMLGeneral.removeAllTagsExcept_prot(string));
				}
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();	
		}
		Sid.log(allSentencesNoLabels.get(0));Sid.log(allSentencesProtLabels.get(0));
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		printHalfFile();
	}

	
	/**
	 * returns the bufferedreaders for the training file and testing file in that order 
	 * @param bufferedReader
	 * @return
	 * @throws IOException
	 */
	private static void printHalfFile() throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader("dict/bc2geneMention/train/train.in"));
		ArrayList<String> lines = new ArrayList<String>();
		while(bufferedReader.ready()){
			String line = bufferedReader.readLine();
			lines.add(line);
		}
		bufferedReader.close();
		Collections.shuffle(lines);
		int size = lines.size();
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		String fileName = "corpus_half_"+ dateFormat.format(date);
		fileName="corpus";
		PrintWriter pwr_training = new PrintWriter(new FileWriter("dict/bc2geneMention/train/train_7500_1.in"));
		PrintWriter pwr_testing = new PrintWriter(new FileWriter("dict/bc2geneMention/train/train_7500_2.in"));
		ArrayList<String> trainSentenceLabels= new  ArrayList<String>();
		ArrayList<String> testSentenceLabels= new  ArrayList<String>();
		for (int count=0; count<size/2; count++) {
			pwr_training.println(lines.get(count));
			trainSentenceLabels.add(lines.get(count).split("\\s")[0]);
		}
		pwr_training.close();
		
		for (int count=size/2; count<size;count++) {
			pwr_testing.println(lines.get(count));
			testSentenceLabels.add(lines.get(count).split("\\s")[0]);
		}
		pwr_testing.close();
		
		bufferedReader = new BufferedReader(new FileReader("dict/bc2geneMention/train/GENE.eval"));
		pwr_training = new PrintWriter(new FileWriter("dict/bc2geneMention/train/GENE1.eval"));
		pwr_testing = new PrintWriter(new FileWriter("dict/bc2geneMention/train/GENE2.eval"));
		while(bufferedReader.ready()){
			String line = bufferedReader.readLine();
			if(trainSentenceLabels.contains(line.split("\\|")[0]))
				pwr_training.println(line);
			else if(testSentenceLabels.contains(line.split("\\|")[0]))
				pwr_testing.println(line);
			else
				System.err.println("error");
		}
		bufferedReader.close();pwr_testing.close();pwr_training.close();
		
		bufferedReader = new BufferedReader(new FileReader("dict/bc2geneMention/train/ALTGENE.eval"));
		pwr_training = new PrintWriter(new FileWriter("dict/bc2geneMention/train/ALTGENE1.eval"));
		pwr_testing = new PrintWriter(new FileWriter("dict/bc2geneMention/train/ALTGENE2.eval"));
		while(bufferedReader.ready()){
			String line = bufferedReader.readLine();
			if(trainSentenceLabels.contains(line.split("\\|")[0]))
				pwr_training.println(line);
			else if(testSentenceLabels.contains(line.split("\\|")[0]))
				pwr_testing.println(line);
			else
				System.err.println("error");
		}
		bufferedReader.close();pwr_testing.close();pwr_training.close();
		
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(fileName+"_training.txt");
		ret.add(fileName+"_testing.txt");
		
	}

	
}
