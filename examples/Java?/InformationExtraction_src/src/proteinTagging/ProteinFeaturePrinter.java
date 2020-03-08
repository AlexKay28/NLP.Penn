/**
 * Copyright of Lnx Research, LLC.
 * email: sid.kgp@gmail.com
 */
package proteinTagging;

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
import util.chunk.SidChunker;
import util.tokenizer.BobTokenizer;
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
public class ProteinFeaturePrinter {

	//options
	private static final boolean useDimensionFeature = false;
	public static final boolean useSimFind=true;	

	//

	public EngLemmatiser lemmatiser;
	public HeppleTagger tagger;
	public SidChunker chunker;
	public HashSet<String> corpusTokens;
	/*public HashMap<String, String> simFindStoreLocal ;
	public HashMap<String, String> simFindStoreClinicalTrails ;*/
	public HashMap<String, String> simFindStoreProteins ;
	public HashMap<String, String> permtermMap = new HashMap<String, String>();

	HashSet<String> singleProts;

	public String sentId;
	public String sent;
	public Sentence sentence;
	public ArrayList<Token> tokens;
	public String[] stringTokens;
	public String[] posTags;
	public String[] phraseChunks;

	public ArrayList<String> labels;
	public ArrayList<String> featureSets;

	public Pattern conPattern;


	/**
	 * 
	 */
	public ProteinFeaturePrinter() {
		lemmatiser=new EngLemmatiser("models/lemmatiser", false, true);
		tagger = new HeppleTagger("models/tagger");
		chunker= new SidChunker();

		conPattern = Pattern.compile("c=\"(.+)\"\\s+(\\d+):(\\d+)\\s+\\d+:(\\d+)\\|\\|t=\"(.+)\"");

		/*		simFindStoreLocal = new HashMap<String, String>();
		simFindStoreClinicalTrails = new HashMap<String, String>();*/
		simFindStoreProteins = new HashMap<String, String>();

		try {
			singleProts = new HashSet<String>();
			BufferedReader br = new BufferedReader(new FileReader("dict/singleProtDictionary.txt"));
			while(br.ready())
				singleProts.add(br.readLine().trim().toUpperCase());
			br.close();

			if (useSimFind) {
				/*br = new BufferedReader(new FileReader(
				"models/simFindStores_i2b2.txt"));
				while (br.ready()) {
					String line = br.readLine();
					if (line.split("\\s").length > 4)
						simFindStoreLocal
						.put(line.split("\\s")[0].trim(), line);
				}
				br.close();
				br = new BufferedReader(new FileReader(
				"models/Thesarus_clinicalTrials_2000d_20_0.7.txt"));
				while (br.ready()) {
					String line = br.readLine();
					if (line.split("\\s").length > 4)
						simFindStoreClinicalTrails.put(
								line.split("\\s")[0].trim(), line);
				}
				br.close();*/
				br = new BufferedReader(new FileReader(
				"dict/bc2geneMention/Thesarus_proteins_2000d_5.txt"));
				//		"models/Thesarus_clinicalTrials_2000d_20.txt"));
				while (br.ready()) {
					String line = br.readLine();
					if (line.split("\\s").length > 4)
						simFindStoreProteins
						.put(line.split("\\s")[0].trim(), line);
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
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		boolean training = false;
		String outputFileName="outputs/bc2geneMention/testingBC2_half_proteins_2000d_5.txt";
		String inputFileName="dict/bc2geneMention/train/train_7500_1.in";
		String labelFileName="dict/bc2geneMention/train/GENE2.eval";
		ArrayList<String> labelLines = null;
		if (training) {
			labelLines = new ArrayList<String>();
			BufferedReader br_labelFile = new BufferedReader(new FileReader(
					labelFileName));
			while (br_labelFile.ready()) {
				labelLines.add(br_labelFile.readLine());
			}
			br_labelFile.close();
		}
		ProteinFeaturePrinter fp = new ProteinFeaturePrinter();
		PrintWriter pwr = new PrintWriter(new FileWriter(outputFileName));


		int line=0;
		BufferedReader br_txt = new BufferedReader(new FileReader(inputFileName));
		while(br_txt.ready()){
			Sid.log(line);

			fp.stringTokens = null;
			fp.posTags = null;
			fp.phraseChunks = null;

			String lineString = br_txt.readLine();
			fp.sent=lineString.substring(lineString.indexOf(" ")+1);
			fp.sentId=lineString.substring(0,lineString.indexOf(" "));

			fp.doTokenization();
			fp.doPOSSentence();
			fp.doChunking();

			fp.featureSets = new ArrayList<String>();

			if (training) {
				fp.labels = new ArrayList<String>(Arrays.asList(fp.getLabels(lineString, fp.stringTokens, labelLines)));
			}

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
				if(training)
					pwr.println(finalFeature.replace(' ', '_').replace('\t', ' ').trim()+" "+fp.labels.get(index));
				else
					pwr.println(finalFeature.replace(' ', '_').replace('\t', ' ').trim());
			}

			//Sid.log(""); 
			pwr.println();
			line++;
		}
		br_txt.close();


		pwr.close();

	}



	String getFeatureSet(int index) {
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



		);	
		return featureSet;
	}


	/**
	 * @param index
	 * @return
	 */
	public String getSIMFIND(int index) {
		if(!useSimFind)
			return "";

		String text="";
		/*if(this.simFindStoreLocal.containsKey(sentence.getWord(index).getContent().toLowerCase())){
			String[] ret = this.simFindStoreLocal.get(sentence.getWord(index).getContent().toLowerCase()).split("\\s");			
			for(int i=0; i<ret.length ; i++){
				text+="SIMLocal="+ret[i]+"\t";
			}
		}
		if(this.simFindStoreClinicalTrails.containsKey(sentence.getWord(index).getContent().toLowerCase())){
			String[] ret = this.simFindStoreClinicalTrails.get(sentence.getWord(index).getContent().toLowerCase()).split("\\s");			
			for(int i=0; i<ret.length ; i++){
				text+="SIMCTs="+ret[i]+"\t";
			}
		}
		
		*/
		if(this.simFindStoreProteins.containsKey(sentence.getWord(index).getContent().toLowerCase())){
			String[] ret = this.simFindStoreProteins.get(sentence.getWord(index).getContent().toLowerCase()).split("\\s");			
			for(int i=0; i<ret.length ; i++){
				text+="SIMProt="+ret[i]+"\t";
			}
		}
		
		if(useDimensionFeature){
			text+=permtermMap.get(sentence.getWord(index).getContent().toLowerCase())+"\t";
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
		String s = sentence.getWord(index).getContent();
		String ret="";

		if(singleProts.contains(s.trim().toUpperCase()))
			ret+="PRETAG=PROT\t";

		/*if(firstNames.contains(s.trim().toUpperCase()))
			ret+="PRETAG=FPER\t";
		if(lastNames.contains(s.trim().toUpperCase()))
			ret+="PRETAG=LPER\t";

		for(int start =0; start<=index; start++){
			for(int end=index; end<stringTokens.length; end++){
				String text="";
				for(int count=start;count<=end;count++)
					text+=sentence.getWord(count).getContent().toUpperCase()+" ";
				text=text.trim();
				if(!ret.contains("PRETAG=ORG\t") && orgs.contains(text))
					ret+="PRETAG=ORG\t";
				if(!ret.contains("PRETAG=LOC\t") && locs.contains(text))
					ret+="PRETAG=LOC\t";
			}
		}*/

		return ret;
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
		return "BNC="+sentence.getWord(index).getContent().replaceAll("[0-9]+", "0")+"\t";
	}

	/**
	 * @param i
	 * @return
	 */
	public String getNC(int index) {
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

	void doTokenization(){
		stringTokens = new BobTokenizer().getTokens(sent).toArray(new String[0]);
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
		tagger.tag(sentence);
	}



	/**
	 * 
	 */
	void doChunking() {
		posTags = this.chunker.mPosTagger.tag(Arrays.asList(stringTokens)).tags().toArray(new String[0]);
		phraseChunks = this.chunker.mChunker.chunk(stringTokens,posTags);

	}

	private String[] getLabels(String line, String[] tokens,
			ArrayList<String> labelLines) throws IOException {
		String[] labels = new String[tokens.length];
		int[] labelStarts=new int[tokens.length];int noSpaceCharCount=0;
		for (int i = 0; i < labels.length; i++) {
			labels[i]="O";
			labelStarts[i]=noSpaceCharCount;
			noSpaceCharCount+=tokens[i].length();
		}

		for (String conLine: labelLines) {
			String lineId = conLine.split("\\|")[0];
			if(!line.startsWith(lineId))
				continue;
			int entStart = Integer.parseInt(conLine.split("\\|")[1].split("\\s")[0]);
			int entEnd = Integer.parseInt(conLine.split("\\|")[1].split("\\s")[1]);
			for (int i = 0; i < labels.length; i++) {
				if(labelStarts[i]>=entStart&&labelStarts[i]<=entEnd)
					labels[i]="I";
			}
		}

		return labels;
	}

}
