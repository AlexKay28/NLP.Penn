
package concepts.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Sid;
import util.SimpleCommandLineParser;
import util.chunk.SidChunker;
import dragon.nlp.Sentence;
import dragon.nlp.Token;
import dragon.nlp.Word;
import dragon.nlp.tool.HeppleTagger;
import dragon.nlp.tool.lemmatiser.EngLemmatiser;

/**
 * features including the offset conjuctions
 * @author sjonnalagadda
 *
 */
public class FeaturePrinter_simple {
	
	//options
	public static boolean useDictFeature=true;
	
	public static boolean useW = true;
	public static boolean useNC = true;
	public static boolean useBNC = true;
	
	//

	public HashSet<String> corpusTokens;
	
	public String sent;
	public Sentence sentence;
	public ArrayList<Token> tokens;
	public String[] stringTokens;
	
	public ArrayList<String> labels;
	public ArrayList<String> dictLabels;
	public ArrayList<String> featureSets;
	public String section;
	
	public Pattern conPattern;
	
	public FeaturePrinter_simple() {
		section="";
		conPattern = Pattern.compile("c=\"(.+)\"\\s+(\\d+):(\\d+)\\s+\\d+:(\\d+)\\|\\|t=\"(.+)\"");
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args);
		String outputFile = parser.getValue("out");
		
		//FeaturePrinter_incl_OffsetConj fp = new FeaturePrinter_incl_OffsetConj(i2b2Thesaurus, clinicalThesaurus, otherThesaurus);
		FeaturePrinter_simple fp = new FeaturePrinter_simple();
		PrintWriter pwr = new PrintWriter(new FileWriter(outputFile));
				//"outputs/trainingCorpusI2B2_SimFind_close_local_10_clinical_h2000_5_6_20.txt"));
		
		File dir_txt=new File("C:/Users/siddhartha/Desktop/jcarafe-sid/trn_txt");
		String[] txtFiles = dir_txt.list();
		for (String txtFile : txtFiles) {
			fp.section = "";
			fp.stringTokens = null;
			Sid.log(txtFile);
			
			int line=0;
			BufferedReader br_txt = new BufferedReader(new FileReader("C:/Users/siddhartha/Desktop/jcarafe-sid/trn_txt/"+txtFile));
			while(br_txt.ready()){
				
				fp.sent=br_txt.readLine();
				
				if(fp.sent.matches(".+\\s:"))
					fp.section=fp.sent.replaceAll(":", "_");
				
				fp.doTokenization();
				fp.doSentence();
				
				fp.featureSets = new ArrayList<String>();
				fp.labels = new ArrayList<String>();
				fp.dictLabels = new ArrayList<String>();
				for (int i=0; i<fp.tokens.size(); i++) {
					fp.labels.add("O");
					fp.dictLabels.add("O");
				}
				
				BufferedReader br_con = new BufferedReader(new FileReader("C:/Users/siddhartha/Desktop/jcarafe-sid/trn_con/"+txtFile.substring(0,txtFile.length()-3)+"con"));
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
				
				BufferedReader br_dict = new BufferedReader(new FileReader("C:/Users/siddhartha/Desktop/jcarafe-sid/dictCon_txt/"+txtFile.substring(0,txtFile.length()-3)+"dic"));
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
					String featureSet = fp.getFeatureSet(index);
					fp.featureSets.add(featureSet);
					//pwr.println(featureSet+fp.labels.get(index));
					
				}
				
				//offset conjunction
				for (int index = 0; index < fp.tokens.size(); index++) {
					String finalFeature="";
					if(index>1)
						finalFeature+=fp.featureSets.get(index-2).replaceAll("\t", "_2\t");
					if(index>0)
						finalFeature+=fp.featureSets.get(index-1).replaceAll("\t", "_1\t");
					finalFeature+=fp.featureSets.get(index);
					if(index<fp.tokens.size()-1)
						finalFeature+=fp.featureSets.get(index+1).replaceAll("\t", "+1\t");
					if(index<fp.tokens.size()-2)
						finalFeature+=fp.featureSets.get(index+2).replaceAll("\t", "+2\t");
					//Sid.log(finalFeature); 
					pwr.println(fp.labels.get(index)+"\t"+finalFeature.replaceAll("=", ":").trim());
				}
				
				//Sid.log(""); 
				pwr.println();
				line++;
			}
			br_txt.close();
			pwr.flush();
		}
		
		pwr.close();
				
	}
	
	

	String getFeatureSet(int index) {
		
		String featureSet =  (this.getW(index)
				+this.getNC(index)
				+this.getBNC(index)
				+this.getWC(index)
				+this.getBWC(index)
				
				
				+this.getPREFIX(2,index)
				+this.getPREFIX(3,index)
				+this.getPREFIX(4,index)
				+this.getSUFFIX(2,index)
				+this.getSUFFIX(3,index)
				+this.getSUFFIX(4,index)
				
				+this.getCHARNGRAM(index)
				
				/**
				 * domain-specific
				 */
				+this.getPRETAG(index)
				
				+this.getRegexMatch(index,"ALPHA", Pattern.compile("^[A-Za-z]+$"))
				+this.getRegexMatch(index,"INITCAPS", Pattern.compile("^[A-Z].*$"))
				+this.getRegexMatch(index,"UPPER-LOWER", Pattern.compile("^[A-Z][a-z].*$"))
				+this.getRegexMatch(index,"LOWER-UPPER", Pattern.compile("^[a-z]+[A-Z]+.*$"))
				+this.getRegexMatch(index,"ALLCAPS", Pattern.compile("^[A-Z]+$"))
				+this.getRegexMatch(index,"MIXEDCAPS", Pattern.compile("^[A-Z][a-z]+[A-Z][A-Za-z]*$"))
				+this.getRegexMatch(index,"SINGLECHAR", Pattern.compile("^[A-Za-z]$"))
				+this.getRegexMatch(index,"SINGLEDIGIT", Pattern.compile("^[0-9]$"))
				+this.getRegexMatch(index,"NUMBER", Pattern.compile("^[0-9,]+$"))
				+this.getRegexMatch(index,"HASDIGIT", Pattern.compile("^.*[0-9].*$"))
				+this.getRegexMatch(index,"ALPHANUMERIC", Pattern.compile("^.*[0-9].*[A-Za-z].*$"))
				+this.getRegexMatch(index,"ALPHANUMERIC", Pattern.compile("^.*[A-Za-z].*[0-9].*$"))
				+this.getRegexMatch(index,"NUMBERS_LETTERS", Pattern.compile("^[0-9]+[A-Za-z]+$"))
				+this.getRegexMatch(index,"LETTERS_NUMBERS", Pattern.compile("^[A-Za-z]+[0-9]+$"))
				+this.getRegexMatch(index,"ROMAN", Pattern.compile("^[IVXDLCM]+$",Pattern.CASE_INSENSITIVE))
				+this.getRegexMatch(index,"GREEK", Pattern.compile("^(alpha|beta|gamma|delta|epsilon|zeta|eta|theta|iota|kappa|lambda|mu|nu|xi|omicron|pi|rho|sigma|tau|upsilon|phi|chi|psi|omega)$"))
				+this.getRegexMatch(index,"ISPUNCT", Pattern.compile("^[`~!@#$%^&*()-=_+\\[\\]\\\\{}|;\':\\\",./<>?]+$"))
				
				+this.getLCHAR(index)
				+this.getRCHAR(index)
				
				
				+this.getSECTION()
				
				
				
		);	
		return featureSet;
	}

	/**
	 * @return
	 */
	public String getSECTION() {
		return "SECTION="+this.section+"\t";
	}

	/**
	 * @param index
	 * @return
	 */
	public String getRCHAR(int index) {
		if(index<sentence.getWordNum()-1)
			return "RCHAR="+sentence.getWord(index+1).getContent().substring(0,1)+"\t";
		return "";
	}

	/**
	 * @param index
	 * @return
	 */
	public String getLCHAR(int index) {
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
	public String getRegexMatch(int index, String feature, Pattern pattern) {
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
	public String getPRETAG(int index) {
		if(useDictFeature)
			return "PRETAG="+this.dictLabels.get(index)+"\t";
		return "";
	}

	
	/**
	 * @param index
	 * @return
	 */
	public String getCHARNGRAM(int index) {
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
	public String getSUFFIX(int n, int index) {
		// TODO Auto-generated method stub
		return n+"SUFFIX="+sentence.getWord(index).getContent().substring(Math.max(0, sentence.getWord(index).getContent().length()-n))+"\t";
	}

	/**
	 * @param index
	 * @return
	 */
	public String getPREFIX(int n, int index) {
		return n+"PREFIX="+sentence.getWord(index).getContent().substring(0, Math.min(n, sentence.getWord(index).getContent().length()))+"\t";
	}

	/**
	 * @param index
	 * @return
	 */
	public String getBWC(int index) {
		return "BWC="+sentence.getWord(index).getContent().replaceAll("[0-9]+", "0").replaceAll("[a-z]+", "a").replaceAll("[A-Z]+", "A").replaceAll("[^0-9a-zA-Z]+", "x")+"\t";
	}

	/**
	 * @param index
	 * @return
	 */
	public String getWC(int index) {
		return "WC="+sentence.getWord(index).getContent().replaceAll("[0-9]", "0").replaceAll("[a-z]", "a").replaceAll("[A-Z]", "A").replaceAll("[^0-9a-zA-Z]", "x")+"\t";
	}

	/**
	 * @param index
	 * @return
	 */
	public String getBNC(int index) {
		if(useBNC)
			return "BNC="+sentence.getWord(index).getContent().replaceAll("[0-9]+", "0")+"\t";
		else
			return "";
	}

	/**
	 * @param i
	 * @return
	 */
	public String getNC(int index) {
		if(useNC)
			return "NC="+sentence.getWord(index).getContent().replaceAll("[0-9]", "0")+"\t";
		else
			return "";
	}

	/**
	 * returns the text of the token in lower case
	 * @param token
	 * @return
	 */
	public String getW(int index){
		if(useW)
			return "W="+sentence.getWord(index).getContent().toLowerCase()+"\t";
		else
			return ""; 
	}
	
	void doTokenization(){
		stringTokens = sent.split("\\s+");
		tokens = new ArrayList<Token>();
		for (String string : stringTokens) {
			tokens.add(new Token(string.replaceAll("=", "_").replaceAll(":", "_")));
		}
	}
	
	void doSentence()
    {
        sentence = new dragon.nlp.Sentence();
        for (Token token : tokens) {
        	sentence.addWord(new Word(token.toString()));
		}
    }


}
