package concepts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import util.Sid;
import util.chunk.SidChunker;

/**
 * matches using dictionaries on noun phrases
 * @author sjonnalagadda
 *
 */

public class DictionaryMatcher {

	SidChunker chunker;
	HashSet<String> problemsDict;
	HashSet<String> treatmentsDict;
	HashSet<String> testsDict;
	
	public DictionaryMatcher() throws IOException{
		this.chunker= new SidChunker();
		
		problemsDict = new HashSet<String>();
		BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream("dict/UMLSproblemsUnique.txt"), "UTF-8"));
		while(br1.ready()){
			String line = br1.readLine();
			problemsDict.add(line);
		}
		br1.close();
		
		treatmentsDict = new HashSet<String>();
		br1 = new BufferedReader(new InputStreamReader(new FileInputStream("dict/UMLStreatmentsUnique.txt"), "UTF-8"));
		while(br1.ready()){
			String line = br1.readLine();
			treatmentsDict.add(line);
		}
		br1.close();
		
		br1 = new BufferedReader(new InputStreamReader(new FileInputStream("dict/drugs_full list_v4.txt"), "UTF-8"));
		while(br1.ready()){
			String line = br1.readLine();
			treatmentsDict.add(line);
		}
		br1.close();
		
		
		testsDict = new HashSet<String>();
		br1 = new BufferedReader(new InputStreamReader(new FileInputStream("dict/UMLStestsUnique.txt"), "UTF-8"));
		while(br1.ready()){
			String line = br1.readLine();
			testsDict.add(line);
		}
		br1.close();
		
	}
	
	public static void main(String[] args) throws IOException{
		
		DictionaryMatcher dMatcher = new DictionaryMatcher();
		
		File dir_txt=new File("../competition_txt");
		String[] txtFiles = dir_txt.list();
		for (String txtFile : txtFiles) {
			ArrayList<String> dictConcepts = new ArrayList<String>();
			BufferedReader br1 = new BufferedReader(new FileReader("../competition_txt/"+txtFile));
			int lineNumber=0;
			
			while(br1.ready()){
				lineNumber++;
				String sentence = br1.readLine();
				dictConcepts.addAll(dMatcher.processSentenceChunks(sentence,lineNumber));
			}
			br1.close();
			
			PrintWriter pwr = new PrintWriter(new FileWriter("../competition_dictCon_txt/"+txtFile.substring(0,txtFile.length()-3)+"dic"));
			for (String dictCon : dictConcepts) {
				//Sid.log(dictCon);
				pwr.println(dictCon);
			}
			pwr.close();
		}
		
		/*String sentence = "This report was created by KOTEKOLL , CH 10/17/2001 07:08 PM";
		dMatcher.processSentence(sentence);*/
		
	}
	
	 /**
	 * @param sentence
	 * @param lineNumber 
	 */
	private ArrayList<String> processSentenceChunks(String sentence, int lineNumber) {
		ArrayList<String> ret = new ArrayList<String>();
		String[] tokens = sentence.split("\\s+");
		for(int start=0; start<tokens.length; start++){
			for(int end=start; end<tokens.length; end++){
				String chunk="";
				for(int i=start;i<=end;i++){
					chunk+=tokens[i]+" ";
				}
				chunk=chunk.trim();
				String tag = this.processChunk(chunk);
				if(!tag.equals("O"))
					ret.add("c=\""+chunk+"\" "+lineNumber+":"+start+" "+lineNumber+":"+end+"||t=\""+tag+"\"");
			}
		}
		return ret;
	}

	public  ArrayList<String> processSentence(String line) throws IOException{
	    	String[] tokens 
			= this.chunker.mTokenizerFactory
			.tokenizer(line.toCharArray(),0,line.length())
			.tokenize();
			String[] tags = this.chunker.mPosTagger.tag(Arrays.asList(tokens)).tags().toArray(new String[0]);
			
			String[] chunks = this.chunker.mChunker.chunk(tokens,tags);
			
			ArrayList<String> nounPhrases = new ArrayList<String>();
			String chunk = "";
			int init = -1;
			for(int count=0; count<chunks.length; count++){
				if(chunks[count].equals("B-NP"))
					init=count;
				
				if((chunks[count].equals("B-NP")&& chunk.equals(""))||chunks[count].equals("I-NP"))
					chunk += tokens[count]+" ";
				else if(chunks[count].equals("B-NP")&& !chunk.equals("")){
					nounPhrases.add(chunk.trim()); Sid.log("start="+init+"\t end="+(count-1)+"\tNP="+chunk.trim());init=-1;processNP(chunk.trim());
					chunk = tokens[count]+" ";
				}
				else if(!chunk.equals("")){
					nounPhrases.add(chunk.trim());Sid.log("start="+init+"\t end="+(count-1)+"\tNP="+chunk.trim());init=-1;processNP(chunk.trim());
					chunk = "";
				}
			}
			if(!chunk.equals("")){
				nounPhrases.add(chunk.trim());Sid.log("start="+init+"\t end="+(chunks.length-1)+"\tNP="+chunk.trim());init=-1;processNP(chunk.trim());
				chunk = "";
			}
			return nounPhrases;
	    }

	/**
	 * @param trim
	 */
	private void processNP(String nounPhrase) {
		String tag="O";
		if(problemsDict.contains(nounPhrase))
			tag="dict_Pr";
		else if(treatmentsDict.contains(nounPhrase))
			tag="dict_Tr";
		else if(testsDict.contains(nounPhrase))
			tag="dict_Te";
		Sid.log(tag);
	}

	private String processChunk(String nounPhrase) {
		String tag="O";
		if(problemsDict.contains(nounPhrase))
			tag="dict_Pr";
		else if(treatmentsDict.contains(nounPhrase))
			tag="dict_Tr";
		else if(testsDict.contains(nounPhrase))
			tag="dict_Te";
		/*Sid.log(tag);
		*/
		return tag;
	}
}
