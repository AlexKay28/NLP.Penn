
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
 * all features including the offset conjuctions
 * @author sjonnalagadda
 *
 */
public class FeaturePrinter_incl_OffsetConj {
	
	//options
	public static boolean useDictFeature=true;
	public static boolean useNearNeighFeature=true;
	public static boolean useSVMFeature=true;
	public static boolean useClusteringFeature=true;
	
	public static boolean useW = true;
	public static boolean useNC = true;
	public static boolean useBNC = true;
	public static boolean useDimensionFeature=false;
	public static boolean ultimateBasicFeatures=false;
	public static boolean ultimateBasicFeaturesPlusSimFind=false;
	
	//

	public EngLemmatiser lemmatiser;
	public HeppleTagger tagger;
	public SidChunker chunker;
	public HashSet<String> corpusTokens;
	public HashMap<String, String> simFindStoreLocal ;
	public HashMap<String, String> simFindStoreClinicalTrails ;
	public HashMap<String, String> simFindStoreOther ;
	public HashMap<String, String> svmPredictions = new HashMap<String, String>();
	public HashMap<String, String> clusterings = new HashMap<String, String>();
	public HashMap<String, String> permtermMap = new HashMap<String, String>();
	
	public String sent;
	public Sentence sentence;
	public ArrayList<Token> tokens;
	public String[] stringTokens;
	public String[] posTags;
	public String[] phraseChunks;
	
	public ArrayList<String> labels;
	public ArrayList<String> dictLabels;
	public ArrayList<String> featureSets;
	public String section;
	
	public Pattern conPattern;
	
	public FeaturePrinter_incl_OffsetConj() {
		new FeaturePrinter_incl_OffsetConj("models/simFindStores_i2b2.txt", "models/Thesarus_clinicalTrials_h2000_5_6_20.txt", "");
	}
	
	
	/**
	 * 
	 */
	public FeaturePrinter_incl_OffsetConj(String i2b2Thesaurus, String clinicalThesaurus, String otherThesaurus) {
		lemmatiser=new EngLemmatiser("models/lemmatiser", false, true);
		this.tagger = new HeppleTagger("models/tagger");
		chunker= new SidChunker();
		
		section="";
		conPattern = Pattern.compile("c=\"(.+)\"\\s+(\\d+):(\\d+)\\s+\\d+:(\\d+)\\|\\|t=\"(.+)\"");
		
		simFindStoreLocal = new HashMap<String, String>();
		simFindStoreClinicalTrails = new HashMap<String, String>();
		simFindStoreOther = new HashMap<String, String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(i2b2Thesaurus));
			while(br.ready()){
				String line = br.readLine();
				if(line.split("\\s").length>4)
					simFindStoreLocal.put(line.split("\\s")[0].trim(), line);
			}
			br.close();
			
			br = new BufferedReader(new FileReader(clinicalThesaurus));
			while(br.ready()){
				String line = br.readLine();
				if(line.split("\\s").length>4)
					simFindStoreClinicalTrails.put(line.split("\\s")[0].trim(), line);
			}
			br.close();
			
			if(otherThesaurus.length()>0){
				br = new BufferedReader(new FileReader(otherThesaurus));
				while(br.ready()){
					String line = br.readLine();
					if(line.split("\\s").length>4)
						simFindStoreOther.put(line.split("\\s")[0].trim(), line);
				}
				br.close();
			}
			
			if(useDimensionFeature){			
				BufferedReader br_permtermvectors = new BufferedReader(new FileReader("models/i2b2_permtermvectors.txt"));
				br_permtermvectors.readLine();
				while(br_permtermvectors.ready()){
					String line = br_permtermvectors.readLine();
					String token = line.substring(0, line.indexOf("|"));
					if(token.matches("[0-9`~!@#$%^&*()-=_+\\[\\]\\\\{}|;\':\\\",\\./<>?]+"))
						continue;
					String rest = line.substring(line.indexOf("|")+1).trim();
					String[] values = rest.split("\\|");
					String distVecFeature = "";
					for (int i = 0; i < values.length; i++) {
						String value= new DecimalFormat("0.#").format(Double.parseDouble(values[i]));
						if(!value.equals("0")&&!value.equals("-0")){
							distVecFeature+="DSV"+i+value+"\t";
						}
					}
					//Sid.log(distVecFeature);
					permtermMap.put(token, distVecFeature.trim());
				}
				br_permtermvectors.close();
			}
			
			if (useSVMFeature) {
				BufferedReader br_svmPredictions = new BufferedReader(new FileReader("dict/i2b2SVM.dict"));
				while(br_svmPredictions.ready()){
					String[] line=br_svmPredictions.readLine().split("\\s+");
					svmPredictions.put(line[0], line[1]);
				}
				br_svmPredictions.close();
			}
			
			if(useClusteringFeature){
				BufferedReader br_clusterings = new BufferedReader(new FileReader("dict/i2b2_200_clustering.dict"));
				while(br_clusterings.ready()){
					String[] line=br_clusterings.readLine().split("\\s+");
					clusterings.put(line[0], line[1]);
				}
				br_clusterings.close();
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
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args);
		String i2b2Thesaurus = "models/simFindStores_i2b2.txt";
		String clinicalThesaurus = "models/Thesarus_clinicalTrials_h2000_5_6_20.txt";
		String otherThesaurus = parser.getValue("thesaurus", "th");
		String outputFile = parser.getValue("out");
		
		//FeaturePrinter_incl_OffsetConj fp = new FeaturePrinter_incl_OffsetConj(i2b2Thesaurus, clinicalThesaurus, otherThesaurus);
		FeaturePrinter_incl_OffsetConj fp = new FeaturePrinter_incl_OffsetConj();
		PrintWriter pwr = new PrintWriter(new FileWriter(outputFile));
				//"outputs/trainingCorpusI2B2_SimFind_close_local_10_clinical_h2000_5_6_20.txt"));
		
		File dir_txt=new File("C:/Users/siddhartha/Desktop/jcarafe-0.9.7RC4-sid/trn_txt");
		String[] txtFiles = dir_txt.list();
		for (String txtFile : txtFiles) {
			fp.section = "";
			fp.stringTokens = null;
			fp.posTags = null;
			fp.phraseChunks = null;
			Sid.log(txtFile);
			
			int line=0;
			BufferedReader br_txt = new BufferedReader(new FileReader("C:/Users/siddhartha/Desktop/jcarafe-0.9.7RC4-sid/trn_txt/"+txtFile));
			while(br_txt.ready()){
				
				fp.sent=br_txt.readLine();
				
				if(fp.sent.matches(".+\\s:"))
					fp.section=fp.sent;
				
				fp.doTokenization();
				fp.doPOSSentence();
				fp.doChunking();
				
				fp.featureSets = new ArrayList<String>();
				fp.labels = new ArrayList<String>();
				fp.dictLabels = new ArrayList<String>();
				for (int i=0; i<fp.tokens.size(); i++) {
					fp.labels.add("O");
					fp.dictLabels.add("O");
				}
				
				BufferedReader br_con = new BufferedReader(new FileReader("C:/Users/siddhartha/Desktop/jcarafe-0.9.7RC4-sid/trn_con/"+txtFile.substring(0,txtFile.length()-3)+"con"));
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
				
				BufferedReader br_dict = new BufferedReader(new FileReader("C:/Users/siddhartha/Desktop/jcarafe-0.9.7RC4-sid/dictCon_txt/"+txtFile.substring(0,txtFile.length()-3)+"dic"));
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
					pwr.println(finalFeature.replace(' ', '_').replace('\t', ' ').trim()+" "+fp.labels.get(index));
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
		if(ultimateBasicFeatures&&!ultimateBasicFeaturesPlusSimFind){
			return this.getW(index);
		}
		
		if(ultimateBasicFeatures&&ultimateBasicFeaturesPlusSimFind){
			return this.getW(index)+this.getSIMFIND(index);
		}
		
		String featureSet =  (this.getW(index)
				+this.getLW(index)
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
				+this.getPOS(index)
				+this.getLINGPOS(index)
				+this.getCHUNK(index)
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
				
				+this.getSIMFIND(index)
				
				
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
	public String getSIMFIND(int index) {
		//ArrayList<String> ret = this.sFind.getSimWords(sentence.getWord(index).getContent().toLowerCase(), 5);
		String text="";
		if (useNearNeighFeature) {
			if (this.simFindStoreLocal.containsKey(sentence.getWord(index)
					.getContent().toLowerCase())) {
				String[] ret = this.simFindStoreLocal.get(
						sentence.getWord(index).getContent().toLowerCase())
						.split("\\s");
				for (int i = 0; i < ret.length; i++) {
					text += "SIMLocal=" + ret[i] + "\t";
				}
			}
			if (this.simFindStoreClinicalTrails.containsKey(sentence
					.getWord(index).getContent().toLowerCase())) {
				String[] ret = this.simFindStoreClinicalTrails.get(
						sentence.getWord(index).getContent().toLowerCase())
						.split("\\s");
				for (int i = 0; i < ret.length; i++) {
					text += "SIMCTs=" + ret[i] + "\t";
				}
			}
			if (this.simFindStoreOther.containsKey(sentence
					.getWord(index).getContent().toLowerCase())) {
				String[] ret = this.simFindStoreOther.get(
						sentence.getWord(index).getContent().toLowerCase())
						.split("\\s");
				for (int i = 0; i < ret.length; i++) {
					text += "SIMOther=" + ret[i] + "\t";
				}
			}
		}
		
		if(useDimensionFeature){
			text+=permtermMap.get(sentence.getWord(index).getContent().toLowerCase())+"\t";
		}
		
		if(useSVMFeature){
			String add="";
			if(!svmPredictions.containsKey(sentence.getWord(index).getContent().toLowerCase()))
				add="SVM=ot\t";
			else
				add="SVM="+svmPredictions.get(sentence.getWord(index).getContent().toLowerCase())+"\t";
			text+=add;
		}
		
		if(useClusteringFeature){
			String add="";
			if(!clusterings.containsKey(sentence.getWord(index).getContent().toLowerCase()))
				add="CL=ot\t";
			else
				add="CL="+clusterings.get(sentence.getWord(index).getContent().toLowerCase())+"\t";
			text+=add;
		}
		
		
		return text;
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
	public String getLINGPOS(int index) {
		return "LINGPOS="+this.posTags[index]+"\t";
	}

	/**
	 * @param index
	 * @return
	 */
	public String getCHUNK(int index) {
		return "CHUNK="+this.phraseChunks[index]+"\t";
	}

	
	/**
	 * @param index
	 * @return
	 */
	public String getPOS(int index) {
		return "POS="+sentence.getWord(index).getPOSIndex()+"\t"; 
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
	
	public String getLW(int index){
		String lemma = lemmatiser.lemmatize(sentence.getWord(index).getContent(), sentence.getWord(index).getPOSIndex());
		return "LW="+lemma+"\t"; 
	}
	
	void doTokenization(){
		stringTokens = sent.split("\\s+");
		tokens = new ArrayList<Token>();
		for (String string : stringTokens) {
			tokens.add(new Token(string));
		}
	}
	
	void doPOSSentence()
    {
        sentence = new dragon.nlp.Sentence();
        for (Token token : tokens) {
        	sentence.addWord(new Word(token.toString()));
		}
        this.tagger.tag(sentence);
    }
	

	
	/**
	 * 
	 */
	void doChunking() {
		posTags = this.chunker.mPosTagger.tag(Arrays.asList(stringTokens)).tags().toArray(new String[0]);
		phraseChunks = this.chunker.mChunker.chunk(stringTokens,posTags);
		
	}

}
