/**
 * Copyright of Lnx Research, LLC.
 * email: sid.kgp@gmail.com
 */
package concepts.subtasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Sid;
import dragon.nlp.Sentence;
import dragon.nlp.Token;
import dragon.nlp.Word;
import dragon.nlp.tool.HeppleTagger;
import dragon.nlp.tool.lemmatiser.EngLemmatiser;

/**
 * basic features
 * @author sjonnalagadda
 *
 */
public class FeaturePrinter {

	EngLemmatiser lemmatiser;
	HeppleTagger tagger;
	//SimFind sFind;
	HashMap<String, String> simFindStore ;
	
	String sent;
	Sentence sentence;
	ArrayList<Token> tokens;
	ArrayList<String> labels;
	ArrayList<String> dictLabels;
	ArrayList<String> featureSets;
	String section;
	
	Pattern conPattern;
	
	/**
	 * 
	 */
	public FeaturePrinter() {
		lemmatiser=new EngLemmatiser("models/lemmatiser", false, true);
		tagger = new HeppleTagger("models/tagger");
		//sFind = new SimFind();
		section="";
		conPattern = Pattern.compile("c=\"(.+)\"\\s+(\\d+):(\\d+)\\s+\\d+:(\\d+)\\|\\|t=\"(.+)\"");
		
		simFindStore = new HashMap<String, String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("models/simFindStores.txt"));
			while(br.ready()){
				String line = br.readLine();
				if(line.split("\\s").length>4)
					simFindStore.put(line.split("\\s")[0].trim(), line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		
		FeaturePrinter fp = new FeaturePrinter();
		PrintWriter pwr = new PrintWriter(new FileWriter("models/trainingCorpusI2B2version1.txt"));
		
		File dir_txt=new File("../trn_txt");
		String[] txtFiles = dir_txt.list();
		for (String txtFile : txtFiles) {
			fp.section = "";
			Sid.log(txtFile);
			
			int line=0;
			BufferedReader br_txt = new BufferedReader(new FileReader("../trn_txt/"+txtFile));
			while(br_txt.ready()){
				
				fp.sent=br_txt.readLine();
				
				if(fp.sent.matches(".+\\s:"))
					fp.section=fp.sent;
				
				fp.doTokenization();
				fp.doPOSSentence();
				
				fp.labels = new ArrayList<String>();
				fp.dictLabels = new ArrayList<String>();
				for (int i=0; i<fp.tokens.size(); i++) {
					fp.labels.add("O");
					fp.dictLabels.add("O");
				}
				
				BufferedReader br_con = new BufferedReader(new FileReader("../trn_con/"+txtFile.substring(0,txtFile.length()-3)+"con"));
				while(br_con.ready()){
					String conLine = br_con.readLine();
					Matcher mCon = fp.conPattern.matcher(conLine);
					if(mCon.find()){
						int lineNum = Integer.parseInt(mCon.group(2))-1;
						if(lineNum!=line)
							continue;
						int init = Integer.parseInt(mCon.group(3));
						int end = Integer.parseInt(mCon.group(4));
						String label = mCon.group(5);
						for (int i=init; i<=end; i++) {
							fp.labels.set(i,"I"+label);
						}
						/*System.out.println(conLine);
						System.out.println("lineNum:\t"+mCon.group(2));
						System.out.println("init:\t"+mCon.group(3));
						System.out.println("end:\t"+mCon.group(4));
						System.out.println("label:\t"+mCon.group(5));*/
					}
				}
				br_con.close();
				
				BufferedReader br_dict = new BufferedReader(new FileReader("../dictCon_txt/"+txtFile.substring(0,txtFile.length()-3)+"dic"));
				while(br_dict.ready()){
					String conLine = br_dict.readLine();
					Matcher mCon = fp.conPattern.matcher(conLine);
					if(mCon.find()){
						int lineNum = Integer.parseInt(mCon.group(2))-1;
						if(lineNum!=line)
							continue;
						int init = Integer.parseInt(mCon.group(3));
						int end = Integer.parseInt(mCon.group(4));
						String label = mCon.group(5);
						for (int i=init; i<=end; i++) {
							fp.dictLabels.set(i,label);
						}
						/*System.out.println(conLine);
						System.out.println("dictlineNum:\t"+mCon.group(2));
						System.out.println("dictinit:\t"+mCon.group(3));
						System.out.println("dictend:\t"+mCon.group(4));
						System.out.println("dictlabel:\t"+mCon.group(5));*/
					}
				}
				br_dict.close();
				
				
				for (int index = 0; index < fp.tokens.size(); index++) {
					String featureSet = (fp.getW(index)
							+fp.getLW(index)
							+fp.getNC(index)
							+fp.getBNC(index)
							+fp.getWC(index)
							+fp.getBWC(index)
							
							
							+fp.getPREFIX(2,index)
							+fp.getPREFIX(3,index)
							+fp.getPREFIX(4,index)
							+fp.getSUFFIX(2,index)
							+fp.getSUFFIX(3,index)
							+fp.getSUFFIX(4,index)
							
							+fp.getCHARNGRAM(index)
							+fp.getPOS(index)
							
							/**
							 * domain-specific
							 */
							+fp.getPRETAG(index)
							
							+fp.getRegexMatch(index,"ALPHA", Pattern.compile("^[A-Za-z]+$"))
							+fp.getRegexMatch(index,"INITCAPS", Pattern.compile("^[A-Z].*$"))
							+fp.getRegexMatch(index,"UPPER-LOWER", Pattern.compile("^[A-Z][a-z].*$"))
							+fp.getRegexMatch(index,"LOWER-UPPER", Pattern.compile("^[a-z]+[A-Z]+.*$"))
							+fp.getRegexMatch(index,"ALLCAPS", Pattern.compile("^[A-Z]+$"))
							+fp.getRegexMatch(index,"MIXEDCAPS", Pattern.compile("^[A-Z][a-z]+[A-Z][A-Za-z]*$"))
							+fp.getRegexMatch(index,"SINGLECHAR", Pattern.compile("^[A-Za-z]$"))
							+fp.getRegexMatch(index,"SINGLEDIGIT", Pattern.compile("^[0-9]$"))
							+fp.getRegexMatch(index,"NUMBER", Pattern.compile("^[0-9,]+$"))
							+fp.getRegexMatch(index,"HASDIGIT", Pattern.compile("^.*[0-9].*$"))
							+fp.getRegexMatch(index,"ALPHANUMERIC", Pattern.compile("^.*[0-9].*[A-Za-z].*$"))
							+fp.getRegexMatch(index,"ALPHANUMERIC", Pattern.compile("^.*[A-Za-z].*[0-9].*$"))
							+fp.getRegexMatch(index,"NUMBERS_LETTERS", Pattern.compile("^[0-9]+[A-Za-z]+$"))
							+fp.getRegexMatch(index,"LETTERS_NUMBERS", Pattern.compile("^[A-Za-z]+[0-9]+$"))
							+fp.getRegexMatch(index,"ROMAN", Pattern.compile("^[IVXDLCM]+$",Pattern.CASE_INSENSITIVE))
							+fp.getRegexMatch(index,"GREEK", Pattern.compile("^(alpha|beta|gamma|delta|epsilon|zeta|eta|theta|iota|kappa|lambda|mu|nu|xi|omicron|pi|rho|sigma|tau|upsilon|phi|chi|psi|omega)$"))
							+fp.getRegexMatch(index,"ISPUNCT", Pattern.compile("^[`~!@#$%^&*()-=_+\\[\\]\\\\{}|;\':\\\",./<>?]+$"))
							
							+fp.getLCHAR(index)
							+fp.getRCHAR(index)
							
							+fp.getSIMFIND(index)
							
							+fp.getSECTION()
							
							
							
					);	
					fp.featureSets.add(featureSet);
					pwr.println(featureSet+fp.labels.get(index));
					
				}
				pwr.println();
				line++;
			}
			br_txt.close();
			pwr.flush();
		}
		
		pwr.close();
				
	}
	
	
	
	
	/**
	 * @return
	 */
	private String getSECTION() {
		return "SECTION="+this.section+"\t";
	}

	/**
	 * @param index
	 * @return
	 */
	private String getSIMFIND(int index) {
		//ArrayList<String> ret = this.sFind.getSimWords(sentence.getWord(index).getContent().toLowerCase(), 5);
		String text="";
		if(!this.simFindStore.containsKey(sentence.getWord(index).getContent().toLowerCase()))
			return text;
		String[] ret = this.simFindStore.get(sentence.getWord(index).getContent().toLowerCase()).split("\\s");
		
		for(int i=0; i<5 ; i++){
			text+="SIMFIND="+ret[i]+"\t";
		}
		return text;
	}

	/**
	 * @param index
	 * @return
	 */
	private String getRCHAR(int index) {
		if(index<sentence.getWordNum()-1)
			return "RCHAR="+sentence.getWord(index+1).getContent().substring(0,1)+"\t";
		return "";
	}

	/**
	 * @param index
	 * @return
	 */
	private String getLCHAR(int index) {
		if(index>0)
			return "LCHAR="+sentence.getWord(index-1).getContent().substring(sentence.getWord(index-1).getContent().length()-1)+"\t";
		return "";
	}

	/**
	 * @param index
	 * @param string
	 * @param compile
	 * @return
	 */
	private String getRegexMatch(int index, String feature, Pattern pattern) {
		Matcher m = pattern.matcher(sentence.getWord(index).getContent());
		if(m.find())
			return feature+"\t";
		return "";
	}


	/**
	 * TODO n
	 * @param index
	 * @return
	 */
	private String getPRETAG(int index) {
		return "PRETAG="+this.dictLabels.get(index)+"\t";
	}

	/**
	 * @param index
	 * @return
	 */
	private String getPOS(int index) {
		return "POS="+sentence.getWord(index).getPOSIndex()+"\t"; 
	}

	/**
	 * @param index
	 * @return
	 */
	private String getCHARNGRAM(int index) {
		String s = sentence.getWord(index).getContent();
		String out="";
		int slen = s.length();
		for (int k = 0; k < (slen - 2)+1; k++)
			out+="CHARNGRAM="+ s.substring (k, k+2).intern()+"\t";
		for (int k = 0; k < (slen - 3)+1; k++)
			out+="CHARNGRAM="+ s.substring (k, k+3).intern()+"\t";
		return out;
	}

	/**
	 * @param i
	 * @param index
	 * @return
	 */
	private String getSUFFIX(int n, int index) {
		// TODO Auto-generated method stub
		return n+"SUFFIX="+sentence.getWord(index).getContent().substring(Math.max(0, sentence.getWord(index).getContent().length()-n))+"\t";
	}

	/**
	 * @param index
	 * @return
	 */
	private String getPREFIX(int n, int index) {
		return n+"PREFIX="+sentence.getWord(index).getContent().substring(0, Math.min(n, sentence.getWord(index).getContent().length()))+"\t";
	}

	/**
	 * @param index
	 * @return
	 */
	private String getBWC(int index) {
		return "BWC="+sentence.getWord(index).getContent().replaceAll("[0-9]+", "0").replaceAll("[a-z]+", "a").replaceAll("[A-Z]+", "A").replaceAll("[^0-9a-zA-Z]+", "x")+"\t";
	}

	/**
	 * @param index
	 * @return
	 */
	private String getWC(int index) {
		return "WC="+sentence.getWord(index).getContent().replaceAll("[0-9]", "0").replaceAll("[a-z]", "a").replaceAll("[A-Z]", "A").replaceAll("[^0-9a-zA-Z]", "x")+"\t";
	}

	/**
	 * @param index
	 * @return
	 */
	private String getBNC(int index) {
		return "BNC="+sentence.getWord(index).getContent().replaceAll("[0-9]+", "0")+"\t";
	}

	/**
	 * @param i
	 * @return
	 */
	private String getNC(int index) {
		return "NC="+sentence.getWord(index).getContent().replaceAll("[0-9]", "0")+"\t";
	}

	/**
	 * returns the text of the token in lower case
	 * @param token
	 * @return
	 */
	public String getW(int index){
		return "W="+sentence.getWord(index).getContent().toLowerCase()+"\t";
	}
	
	public String getLW(int index){
		String lemma = lemmatiser.lemmatize(sentence.getWord(index).getContent(), sentence.getWord(index).getPOSIndex());
		return "LW="+lemma+"\t"; 
	}
	
	private void doTokenization(){
		String[] stringTokens = sent.split("\\s+");
		tokens = new ArrayList<Token>();
		for (String string : stringTokens) {
			tokens.add(new Token(string));
		}
	}
	
	private void doPOSSentence()
    {
        sentence = new dragon.nlp.Sentence();
        for (Token token : tokens) {
        	sentence.addWord(new Word(token.toString()));
		}
        tagger.tag(sentence);
    }

}
