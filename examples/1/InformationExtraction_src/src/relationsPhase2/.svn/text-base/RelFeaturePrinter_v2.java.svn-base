package relationsPhase2;

import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import concepts.main.FeaturePrinter_incl_OffsetConj;
import dragon.nlp.Sentence;
import dragon.nlp.Token;
import dragon.nlp.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.MemoryTreebank;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeNormalizer;
import edu.stanford.nlp.trees.Treebank;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.Filters;

import relations.MatchInteractions;
import relations.Normalizer;
import subtasks.SidSmithWaterman;
import util.Sid;


public class RelFeaturePrinter_v2 {

	HashMap<String,String> linePatterns = new HashMap<String, String>();
	HashMap<String,String> patternTags = new HashMap<String, String>();
	ArrayList<String> patterns = new ArrayList<String>();
	LinkedHashMap<Integer, String> conceptDictionary;
	ArrayList<String> sortedConcepts;
	FeaturePrinter_incl_OffsetConj fp;
	LexicalizedParser lp;
	
	String sent;
	Sentence sentence;
	ArrayList<Token> tokens;
	String[] stringTokens;
	String[] posTags;
	String[] phraseChunks;
	List<TypedDependency> deps;
	String section;
	//public static ArrayList<String> stopList = new ArrayList<String>();

	public RelFeaturePrinter_v2() throws IOException{
		fp = new FeaturePrinter_incl_OffsetConj();
		lp = new LexicalizedParser("models/englishPCFG.ser.gz");
		
		MatchInteractions.loadStopWords();

		//store all interaction pairs and their tags
		BufferedReader br = new BufferedReader(new FileReader("dict/relSnippetsModified.txt"));
		while(br.ready()){
			String text=br.readLine();
			if(text.split("\\|\\|").length==3){
				String pattern=Normalizer.normalizeSentence(text.split("\\|\\|")[2]).trim();
				patternTags.put(pattern, text.split("\\|\\|")[1]);
				linePatterns.put(text.split("\\|\\|")[0],pattern);
				patterns.add(pattern);
			}
		}
		br.close();
		
		section = "";
	}
	

	public static void main(String[] args) {
		
		boolean training = true;
		training=false;
		
		try {
			
			PrintWriter pwr = new PrintWriter(new FileWriter("models/relInstanceModel.txt"));
			
			RelFeaturePrinter_v2 rp = new RelFeaturePrinter_v2();
			
			File dir_txt=new File("../competition_txt");
			String[] txtFiles = dir_txt.list();
			

			for (String txtFile : txtFiles) {
				BufferedReader br1 = new BufferedReader(new FileReader("../competition_txt/"+txtFile));
				rp.section = "";
				
				int lineNumber=0;
				while(br1.ready()){
					lineNumber++;
					String line = br1.readLine();
					
					
					rp.conceptDictionary = new LinkedHashMap<Integer, String>();				
					String[] replacedTokens = rp.replaceWithCon(line,lineNumber,txtFile);
					ArrayList<String> contestants = rp.findContestants(replacedTokens);
					if(contestants.size()==0||line.split("\\s+").length>150)
						continue;
					
					
					Sid.log("Line:"+line);
					
					rp.getData(line);
					for (String contestant : contestants) {
						int ent1=-1,ent2=-1;
						String[] tokens=contestant.split("\\s");
						int count=0;
						for (String token : tokens) {
							if((token.equals("PROBLEM_")||token.equals("TEST_")||token.equals("TREATMENT_"))&&ent1==-1){
								ent1=count;
							}
							else if((token.equals("PROBLEM_")||token.equals("TEST_")||token.equals("TREATMENT_"))&&ent2==-1){
								ent2=count;
								break;
							}
							if(token.contains("PROBLEM")||token.contains("TEST")||token.contains("TREATMENT")){
								count++;						
							}
						}
						String instanceName = (txtFile+" "+rp.sortedConcepts.get(ent1)+"||"+rp.sortedConcepts.get(ent2)).replaceAll("\\s+", "_");
						Sid.log("\tInstance Name:"+instanceName);
						String features = rp.getBasicFeatures(line,instanceName);
						features=features.trim()+" "+rp.getAdvancedFeatures(line,instanceName, contestant);
						Sid.log("\tFeature Set:"+features);
						
						if(training){
							String label = getLabel(instanceName);
							Sid.log(instanceName+" "+features.trim()+" "+label);
							pwr.println(instanceName+" "+features.trim()+" "+label);
							Sid.log("");
						}
						else{
							String label = "None";
							Sid.log(instanceName+" "+features.trim()+" "+label);
							pwr.println(instanceName+" "+features.trim()+" "+label);
							Sid.log("");
						}
					}
				}
				
				pwr.flush();
				br1.close();
			}
			pwr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




	private String getAdvancedFeatures(String line, String instanceName, String contestant) {
		/*Pattern conPattern = Pattern.compile("\\d+:(\\d+)_\\d+:(\\d+).+\\d+:(\\d+)_\\d+:(\\d+)");
		Matcher conMatcher = conPattern.matcher(instanceName);
		int ent1Start=-1; int ent1End=-1; int ent2Start=-1; int ent2End=-1;
		if(conMatcher.find()){
			ent1Start=Integer.parseInt(conMatcher.group(1));ent1End=Integer.parseInt(conMatcher.group(2));ent2Start=Integer.parseInt(conMatcher.group(3));ent2End=Integer.parseInt(conMatcher.group(4));
		}*/
		
		String ret="";
		
		contestant=Normalizer.normalizeSentence(contestant).trim();
		
		Iterator<String> keys= patternTags.keySet().iterator();

		LinkedHashMap<String, Double> results = new LinkedHashMap<String, Double>();
		ArrayList<String> posLabels=new ArrayList<String>();
		if(contestant.contains("TEST_")){
			posLabels.add("TeRP");posLabels.add("TeCP");
		}
		else if(contestant.contains("TREATMENT_")){
			posLabels.add("TrIP");posLabels.add("TrWP");posLabels.add("TrCP");posLabels.add("TrAP");posLabels.add("TrNAP");
		}
		else{
			posLabels.add("PIP");
		}
			

		while(keys.hasNext()){
			String key = keys.next();
			if(!posLabels.contains(patternTags.get(key)))
				continue;
			
			/*key=key.replaceAll("PROBLEM_", "PROBLEM__________________________________________________");
			key=key.replaceAll("TEST_", "TEST__________________________________________________");
			key=key.replaceAll("TREATMENT_", "TREATMENT__________________________________________________");
			*/
			//double score = NeoBio.getNormalizedSWScore(key, contestant);
			double score = SidSmithWaterman.getSidSWscore(key, contestant)/key.split("\\s").length;
			//threshhold
			if(score>=0.85){
				/*key=key.replaceAll("_+", "_");
				contestant=contestant.replaceAll("_+", "_");*/
				/*Sid.log("\tKey Found:"+key);
				Sid.log("\tScore="+score);
				Sid.log("\tClassification="+patternTags.get(key));
				*/
				//Sid.log("\t"+sortedConcepts.get(ent1)+"||r=\""+patternTags.get(key)+"\"||"+sortedConcepts.get(ent2));
				results.put(score+"||"+key, score);
				ret+="Pattern_"+key.replace(' ', '_')+" ";
				//return sortedConcepts.get(ent1)+"||r=\""+patternTags.get(key)+"\"||"+sortedConcepts.get(ent2);
			}
		}

		ret+="LineLength_"+line.split("\\s+").length +" ";
		
		
		return ret;
	}


	private static String getLabel(String instanceName) throws IOException {
		Pattern instPattern = Pattern.compile("^(.+\\.)txt.+_(\\d+:\\d+_\\d+:\\d+).+_(\\d+:\\d+_\\d+:\\d+)$");
		Matcher instMatcher = instPattern.matcher(instanceName);
		String label="None";
		if(instMatcher.find()){
			String fileName="../trn_rel/"+instMatcher.group(1)+"rel";
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			while(br.ready()){
				String text=br.readLine();
				if(text.contains(instMatcher.group(2).replace('_', ' '))&&text.contains(instMatcher.group(3).replace('_', ' '))){
					//example: c="po pain medications" 47:7 47:9||r="TrIP"||c="her pain" 47:0 47:1
					Pattern interactionPattern = Pattern.compile("\\d+:\\d+ \\d+:\\d+\\|\\|r=\"([a-zA-Z]+)\".+\\d+:\\d+ \\d+:\\d+");
					Matcher interactionMatcher = interactionPattern.matcher(text);
					if(interactionMatcher.find()){
						label=interactionMatcher.group(1);
						return label;
					}
				}	
			}
		}
		else{
			System.err.println("error in instance");
		}
		return label;
	}


	private void getData(String line) {
		
		//line = "Pentamidine 300 mg IV q. 36 hours , Pentamidine nasal wash 60 mg per 6 ml of sterile water q.d. , voriconazole 200 mg p.o. b.i.d. , acyclovir 400 mg p.o. b.i.d. , cyclosporine 50 mg p.o. b.i.d. , prednisone 60 mg p.o. q.d. , GCSF 480 mcg IV q.d. , Epogen 40,000 units subcu q. week , Protonix 40 mg q.d. , Simethicone 80 mg p.o. q. 8 , nitroglycerin paste 1 &quot; q. 4 h. p.r.n. , flunisolide nasal inhaler , 2 puffs q. 8 , OxyCodone 10-15 mg p.o. q. 6 p.r.n. , Sudafed 30 mg q. 6 p.o. p.r.n. , Fluconazole 2% cream b.i.d. to erythematous skin lesions , Ditropan 5 mg p.o. b.i.d. , Tylenol 650 mg p.o. q. 4 h. p.r.n. , Ambien 5-10 mg p.o. q. h.s. p.r.n. , Neurontin 100 mg q. a.m. , 200 mg q. p.m. , Aquaphor cream b.i.d. p.r.n. , Lotrimin 1% cream b.i.d. to feet , Dulcolax 5-10 mg p.o. q.d. p.r.n. , Phoslo 667 mg p.o. t.i.d. , Peridex 0.12% , 15 ml p.o. b.i.d. mouthwash , Benadryl 25-50 mg q. 4-6 h. p.r.n. pruritus , Sarna cream q.d. p.r.n. pruritus , Nystatin 5 ml p.o. q.i.d. swish and !";
		
		stringTokens = null;
		posTags = null;
		phraseChunks = null;
		
		//tokenization
		sent=line;
		if(sent.matches(".+\\s:"))
			section=sent.replaceAll("\\s+", "_");
		stringTokens = sent.split("\\s+");
		tokens = new ArrayList<Token>();
		List<edu.stanford.nlp.ling.Word> wordList = new ArrayList<edu.stanford.nlp.ling.Word>();
		for (String string : stringTokens) {
			tokens.add(new Token(string));
			wordList.add(new edu.stanford.nlp.ling.Word(string));
		}
		
		
		
		//POS-hepple
		sentence = new dragon.nlp.Sentence();
        for (Token token : tokens) {
        	sentence.addWord(new Word(token.toString()));
		}
        fp.tagger.tag(sentence);
        
        //LingPipe tagging
        posTags = fp.chunker.mPosTagger.tag(Arrays.asList(stringTokens)).tags().toArray(new String[0]);

        //Open-NLP chunking
        phraseChunks = fp.chunker.mChunker.chunk(stringTokens,posTags);
		
        
      //parse
		lp.parse(wordList);
        Tree parseTree = lp.getBestPCFGParse();
        Treebank tb = new MemoryTreebank(new TreeNormalizer());
        tb.add(parseTree);
        Collection<GrammaticalStructure> gsBank = new TreeBankGrammaticalStructureWrapper(tb, false);
        deps =null;
        for (GrammaticalStructure gs : gsBank) {
        	deps = gs.typedDependenciesCCprocessed(true);
        }
	}


	/**
	 * @param line
	 * @param instanceName
	 * @return
	 */
	private String getBasicFeatures(String line, String instanceName) {
		
		Pattern conPattern = Pattern.compile("\\d+:(\\d+)_\\d+:(\\d+).+\\d+:(\\d+)_\\d+:(\\d+)");
		Matcher conMatcher = conPattern.matcher(instanceName);
		int ent1Start=-1; int ent1End=-1; int ent2Start=-1; int ent2End=-1;
		if(conMatcher.find()){
			ent1Start=Integer.parseInt(conMatcher.group(1));ent1End=Integer.parseInt(conMatcher.group(2));ent2Start=Integer.parseInt(conMatcher.group(3));ent2End=Integer.parseInt(conMatcher.group(4));
		}
		
		String ent1Type = conceptDictionary.get(ent1Start).substring(conceptDictionary.get(ent1Start).indexOf("||t=\"")+5,conceptDictionary.get(ent1Start).lastIndexOf("\"")); 
		String ent2Type = conceptDictionary.get(ent2Start).substring(conceptDictionary.get(ent2Start).indexOf("||t=\"")+5,conceptDictionary.get(ent2Start).lastIndexOf("\"")); 
		
		//Surface features
		
		//relative ordering of the candidate concepts
		String feature_RelativeOrdering = "RelOrd_"+ent1Type+"_"+ent2Type;
		
		//distance between the candidate concepts
		String feature_distCon = "DistCon_"+(ent2Start - ent1End);
		
		//Presence of intervening concepts
		boolean present=false;
		for(int i=ent1End+1;i<ent2Start;i++)
			if(conceptDictionary.containsKey(i))
				present=true;
		String feature_intrCon = "IntrCon_"+present;
		
		//Lexical features
		
		//tokens-in-concepts
		String feature_tokCon="";
		HashSet<String> toks = new HashSet<String>();
		for(int i=ent1Start;i<=ent1End;i++)
			toks.add(stringTokens[i]);
		for(int i=ent2Start;i<=ent2End;i++)
			toks.add(stringTokens[i]);
		for (String tok : toks) {
			feature_tokCon+="TokCon_"+tok+" ";
		}
		feature_tokCon=feature_tokCon.trim();
		
		//concepts "NEW"
		String con1="";
		for(int i=ent1Start;i<=ent1End;i++)
			con1+=stringTokens[i]+"_";
		String con2="";
		for(int i=ent2Start;i<=ent2End;i++)
			con2+=stringTokens[i]+"_";
		String feature_con="Con_"+con1+" "+"Con_"+con2;
		feature_con=feature_con.trim();
		
		//lexical trigrams
		String feature_con1_1="Con1_1_";
		String feature_con1_2="Con1_2_";
		String feature_con1_3="Con1_3_";
		String feature_con11="Con11_";
		String feature_con12="Con12_";
		String feature_con13="Con13_";
		
		String feature_con2_1="Con2_1_";
		String feature_con2_2="Con2_2_";
		String feature_con2_3="Con2_3_";
		String feature_con21="Con21_";
		String feature_con22="Con22_";
		String feature_con23="Con23_";

		String feature_con1l="Con1l_";
		String feature_con1r="Con1r_";
		String feature_con2l="Con2l_";
		String feature_con2r="Con2r_";
		
		
		if(ent1Start-3>=0){
			feature_con1_3+=stringTokens[ent1Start-3];feature_con1l+=stringTokens[ent1Start-3];
		}
		if(ent1Start-2>=0){
			feature_con1_2+=stringTokens[ent1Start-2];feature_con1l+=stringTokens[ent1Start-2];
		}
		if(ent1Start-1>=0){
			feature_con1_1+=stringTokens[ent1Start-1];feature_con1l+=stringTokens[ent1Start-1];
		}

		if(ent1End+1<stringTokens.length){
			feature_con11+=stringTokens[ent1End+1];feature_con1r+=stringTokens[ent1End+1];
		}
		if(ent1End+2<stringTokens.length){
			feature_con12+=stringTokens[ent1End+2];feature_con1r+=stringTokens[ent1End+2];
		}
		if(ent1End+3<stringTokens.length){
			feature_con13+=stringTokens[ent1End+3];feature_con1r+=stringTokens[ent1End+3];
		}
		
		if(ent2Start-3>=0){
			feature_con2_3+=stringTokens[ent2Start-3];feature_con2l+=stringTokens[ent2Start-3];
		}
		if(ent2Start-2>=0){
			feature_con2_2+=stringTokens[ent2Start-2];feature_con2l+=stringTokens[ent2Start-2];
		}
		if(ent2Start-1>=0){
			feature_con2_1+=stringTokens[ent2Start-1];feature_con2l+=stringTokens[ent2Start-1];
		}

		if(ent2End+1<stringTokens.length){
			feature_con21+=stringTokens[ent2End+1];feature_con2r+=stringTokens[ent2End+1];
		}
		if(ent2End+2<stringTokens.length){
			feature_con22+=stringTokens[ent2End+2];feature_con2r+=stringTokens[ent2End+2];
		}
		if(ent2End+3<stringTokens.length){
			feature_con23+=stringTokens[ent2End+3];feature_con2r+=stringTokens[ent2End+3];
		}
		
		//Inter-concept tokens
		String feature_interToks="";
		for(int i=ent1End+1;i<ent2Start;i++){
			feature_interToks+="InterTok_"+stringTokens[i]+" ";
		}
		feature_interToks=feature_interToks.trim();
		
		//syntactic features
		
		//verb phrases
		String featureVerbPhrases="";
		for(int i=ent1End+1; i<ent2Start; i++ ){
			if(phraseChunks[i].endsWith("VP"))
				featureVerbPhrases+="VPbetween_"+stringTokens[i]+" ";
		}
		
		int count=0;
		for(int i=ent1Start-1; i>=0; i--){
			if(phraseChunks[i].endsWith("VP")&&count<2){
				featureVerbPhrases+="VPbefore_"+stringTokens[i]+" ";
				count++;
			}
		}
		
		count=0;
		for(int i=ent2End+1; i<stringTokens.length; i++){
			if(phraseChunks[i].endsWith("VP")&&count<2){
				featureVerbPhrases+="VPafter_"+stringTokens[i]+" ";
				count++;
			}
		}
		
		//headword of the concepts
		String feature_headWord="";
		int headWord1=ent1End;
		for(int i=ent1End;i>=ent1Start;i--){
			if(phraseChunks[i].contains("NP")){
				feature_headWord+="HeadWord_"+stringTokens[i]+" ";
				headWord1=i;
				break;
			}
				
		}
		int headWord2=ent2End;
		for(int i=ent2End;i>=ent2Start;i--){
			if(phraseChunks[i].contains("NP")){
				feature_headWord+="HeadWord_"+stringTokens[i]+" ";
				headWord2=i;
				break;
			}
				
		}
		
		//syntactic connections
		String feature_synBigram="";
		for (TypedDependency dep : deps) {
			String reln = dep.reln().toString();
			String dep1 = dep.dep().toString();int dep1Num=Integer.parseInt(dep1.substring(dep1.lastIndexOf('-')+1));
			String dep2 = dep.gov().toString();int dep2Num=Integer.parseInt(dep2.substring(dep2.lastIndexOf('-')+1));
			if((dep1Num>=ent1Start+1&&dep1Num<=ent1End+1&&!(dep2Num>=ent1Start+1&&dep2Num<=ent1End+1))||(dep1Num>=ent2Start+1&&dep1Num<=ent2End+1&&!(dep2Num>=ent2Start+1&&dep2Num<=ent2End+1)))
				feature_synBigram+="SynBigram_"+reln+"_"+dep2.substring(0, dep2.lastIndexOf('-'))+" ";
			else if((dep2Num>=ent1Start+1&&dep2Num<=ent1End+1&&!(dep1Num>=ent1Start+1&&dep1Num<=ent1End+1))||(dep2Num>=ent2Start+1&&dep2Num<=ent2End+1&&!(dep1Num>=ent2Start+1&&dep1Num<=ent2End+1)))
				feature_synBigram+="SynBigram_"+reln+"_"+dep1.substring(0, dep1.lastIndexOf('-'))+" ";
		}
		
		//link path 
		String feature_linkPath = getLinkPathFeatures(headWord1,headWord2);
		
		//section
		String feature_section="Section_"+section;
		
		String ret = 
			feature_RelativeOrdering.trim()+" "+
			feature_distCon.trim()+" "+
			feature_intrCon.trim()+" "+
			feature_tokCon.trim()+" "+
			feature_con.trim()+" "+
			feature_con11.trim()+" "+
			feature_con12.trim()+" "+
			feature_con13.trim()+" "+
			feature_con1_1.trim()+" "+
			feature_con1_2.trim()+" "+
			feature_con1_3.trim()+" "+
			feature_con1l.trim()+" "+
			feature_con1r.trim()+" "+
			feature_con21.trim()+" "+
			feature_con22.trim()+" "+
			feature_con23.trim()+" "+
			feature_con2_1.trim()+" "+
			feature_con2_2.trim()+" "+
			feature_con2_3.trim()+" "+
			feature_con2l.trim()+" "+
			feature_con2r.trim()+" "+
			feature_interToks.trim()+" "+
			featureVerbPhrases.trim()+" "+
			feature_headWord.trim()+" "+
			feature_synBigram.trim()+" "+
			feature_linkPath.trim()+" "+
			feature_section.trim()+" ";
		
		
		return ret;
	}




	private String getLinkPathFeatures(int headWord1, int headWord2) {
		String ret="";
		
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		for (int i=0; i<stringTokens.length;  i++) {
			vertices.add(new Vertex(""+i));
		}
		for (int i=0; i<stringTokens.length;  i++) {
			ArrayList<Edge> adjVertices  = new ArrayList<Edge>();
			for (TypedDependency dep : deps) {
				String dep1 = dep.dep().toString();int dep1Num=Integer.parseInt(dep1.substring(dep1.lastIndexOf('-')+1))-1;
				String dep2 = dep.gov().toString();int dep2Num=Integer.parseInt(dep2.substring(dep2.lastIndexOf('-')+1))-1;
				if(dep1Num==i)
					adjVertices.add(new Edge(vertices.get(dep2Num),1));
				if(dep2Num==i)
					adjVertices.add(new Edge(vertices.get(dep1Num),1));
			}
			vertices.get(i).adjacencies=adjVertices.toArray(new Edge[0]);
		}
		
		Dijkstra d = new Dijkstra();
		d.computePaths(vertices.get(headWord1));
		
		//link tokens
		List<Vertex> path = d.getShortestPathTo(vertices.get(headWord2));
		for (Vertex vertex : path) {
			ret+="LinkToken_"+stringTokens[Integer.parseInt(vertex.name)]+" ";
		}
		
		//string of tokens in the path
		ret+="LinkPattern_";
		for(int i=1; i<path.size()-1;i++){
			ret+=stringTokens[Integer.parseInt(path.get(i).name)]+"_";
		}
		ret+=" ";
		
		//link path
		ret+="LPath_";
		for(int i=0; i<path.size()-1;i++){
			int node1 = Integer.parseInt(path.get(i).name);
			int node2 = Integer.parseInt(path.get(i+1).name);
			for (TypedDependency dep : deps) {
				String reln = dep.reln().toString();
				String dep1 = dep.dep().toString();int dep1Num=Integer.parseInt(dep1.substring(dep1.lastIndexOf('-')+1))-1;
				String dep2 = dep.gov().toString();int dep2Num=Integer.parseInt(dep2.substring(dep2.lastIndexOf('-')+1))-1;
				if((dep1Num==node1&&dep2Num==node2)||(dep1Num==node2&&dep2Num==node1))
					ret+=reln+"_";
			}
		}
		ret+=" ";
		
		return ret;
	}


	public void loadStopWords() {
		//stopWord
		BufferedReader brStops;
		try {
			brStops = new BufferedReader(new FileReader("dict/stopFileFox_reduced.txt"));
			while(brStops.ready()){
				MatchInteractions.stopList.add(brStops.readLine().trim().toLowerCase());
			}
			brStops.close();

			brStops = new BufferedReader(new FileReader("dict/auxillaryVerbs.txt"));
			while(brStops.ready()){
				String word=brStops.readLine().trim().toLowerCase();
				if(!MatchInteractions.stopList.contains(word))
					MatchInteractions.stopList.add(word);
			}
			brStops.close();
			brStops = new BufferedReader(new FileReader("dict/conjunctions.txt"));
			while(brStops.ready()){
				String word=brStops.readLine().trim().toLowerCase();
				if(!MatchInteractions.stopList.contains(word))
					MatchInteractions.stopList.add(word);
			}
			brStops.close();
			brStops = new BufferedReader(new FileReader("dict/determiners.txt"));
			while(brStops.ready()){
				String word=brStops.readLine().trim().toLowerCase();
				if(!MatchInteractions.stopList.contains(word))
					MatchInteractions.stopList.add(word);
			}
			brStops.close();
			
			/*brStops = new BufferedReader(new FileReader("dict/prepositions-particles.txt"));
			while(brStops.ready()){
				String word=brStops.readLine().trim().toLowerCase();
				stopList.remove(word);
			}
			brStops.close();
			brStops = new BufferedReader(new FileReader("dict/pronouns.txt"));
			while(brStops.ready()){
				String word=brStops.readLine().trim().toLowerCase();
				stopList.remove(word);
			}
			brStops.close();*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}




	private void sortConcepts() {
		Iterator<Integer> it=conceptDictionary.keySet().iterator();
		ArrayList<Integer> keys=new ArrayList<Integer>();
		while(it.hasNext())
			keys.add(it.next());
		Collections.sort(keys);
		sortedConcepts = new ArrayList<String>();
		for (Integer key : keys) {
			sortedConcepts.add(conceptDictionary.get(key).split("\\|\\|")[0]);
		}
		
	}




	@SuppressWarnings({ "unused" })
	private String findInteraction(String contestant) {
		int ent1=-1,ent2=-1;
		String[] tokens=contestant.split("\\s");
		int count=0;
		for (String token : tokens) {
			if((token.equals("PROBLEM_")||token.equals("TEST_")||token.equals("TREATMENT_"))&&ent1==-1){
				ent1=count;
			}
			else if((token.equals("PROBLEM_")||token.equals("TEST_")||token.equals("TREATMENT_"))&&ent2==-1){
				ent2=count;
				break;
			}
			if(token.contains("PROBLEM")||token.contains("TEST")||token.contains("TREATMENT")){
				count++;						
			}


		}
		//Sid.log("\t"+contestant);
		contestant=Normalizer.normalizeSentence(contestant).trim();
		
		Iterator<String> keys= patternTags.keySet().iterator();

		LinkedHashMap<String, Double> results = new LinkedHashMap<String, Double>();
		ArrayList<String> posLabels=new ArrayList<String>();
		if(contestant.contains("TEST_")){
			posLabels.add("TeRP");posLabels.add("TeCP");
		}
		else if(contestant.contains("TREATMENT_")){
			posLabels.add("TrIP");posLabels.add("TrWP");posLabels.add("TrCP");posLabels.add("TrAP");posLabels.add("TrNAP");
		}
		else{
			posLabels.add("PIP");
		}
			

		while(keys.hasNext()){
			String key = keys.next();
			if(!posLabels.contains(patternTags.get(key)))
				continue;
			
			double score = SidSmithWaterman.getSidSWscore(key, contestant)/key.split("\\s").length;
			//threshhold
			if(score>=0.85){
				results.put(score+"||"+key+"||"+sortedConcepts.get(ent1)+"||r=\""+patternTags.get(key)+"\"||"+sortedConcepts.get(ent2), score);
			}
		}

		if(results.size()>0){
			ArrayList<Entry<String,Double>> as = new ArrayList<Entry<String,Double>>( results.entrySet() );  
			
			Collections.sort( as , new Comparator<Object>() {  
				@SuppressWarnings("rawtypes")
				public int compare( Object o1 , Object o2 )  
				{  
					Map.Entry e1 = (Map.Entry)o1 ;  
					Map.Entry e2 = (Map.Entry)o2 ;  
					Double first = (Double)e1.getValue();  
					Double second = (Double)e2.getValue();  
					return second.compareTo( first );  
				}  
			});  
			
			if(posLabels.contains("PIP"))
				return as.get(0).getKey();
			else if(posLabels.contains("TrIP")){
				if(as.get(0).getValue()==1)
					return as.get(0).getKey();
				double countI=0,countW=0,countC=0,countA=0,countNA=0;
				for (Entry<String, Double> entry : as) {
					if(entry.getKey().contains("||r=\"TrIP\"||"))
						countI++;
					else if(entry.getKey().contains("||r=\"TrWP\"||"))
						countW++;
					else if(entry.getKey().contains("||r=\"TrCP\"||"))
						countC++;
					else if(entry.getKey().contains("||r=\"TrAP\"||"))
						countA++;
					else if(entry.getKey().contains("||r=\"TrIP\"||"))
						countNA++;
				}
				double max = Math.max(Math.max(countI, countW), Math.max(Math.max(countC, countA), countNA));
				if(max>0&&countI==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TrIP"+"\"||"+sortedConcepts.get(ent2);
				if(max>0&&countW==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TrWP"+"\"||"+sortedConcepts.get(ent2);
				if(max>0&&countC==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TrCP"+"\"||"+sortedConcepts.get(ent2);
				if(max>0&&countA==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TrAP"+"\"||"+sortedConcepts.get(ent2);
				if(max>0&&countNA==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TrNAP"+"\"||"+sortedConcepts.get(ent2);

			}
			else if(posLabels.contains("TeRP")){
				if(as.get(0).getValue()==1)
					return as.get(0).getKey();
				double countR=0,countC=0;
				for (Entry<String, Double> entry : as) {
					if(entry.getKey().contains("||r=\"TeRP\"||"))
						countR++;
					else if(entry.getKey().contains("||r=\"TeCP\"||"))
						countC++;
				}
				double max = Math.max(countR, countC);
				if(max>0&&countR==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TeRP"+"\"||"+sortedConcepts.get(ent2);
				if(max>0&&countC==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TeCP"+"\"||"+sortedConcepts.get(ent2);
			}
		}

		return null;
	}




	private ArrayList<String> findContestants(String[] replacedTokens) {
		ArrayList<Integer> problems = new ArrayList<Integer>();
		ArrayList<Integer> tests = new ArrayList<Integer>();
		ArrayList<Integer> treatments = new ArrayList<Integer>();
		ArrayList<Integer> all = new ArrayList<Integer>();

		ArrayList<String> ret = new ArrayList<String>(); 


		//track the numbers
		for(int count=0; count<replacedTokens.length; count++){
			if(!replacedTokens[count].contains("_"))
				continue;
			else if(replacedTokens[count].equals("PROBLEM_")){
				replacedTokens[count]="PROBLEM";
				problems.add(count);
			}
			else if(replacedTokens[count].equals("TEST_")){
				replacedTokens[count]="TEST";
				tests.add(count);
			}
			else if(replacedTokens[count].equals("TREATMENT_")){
				replacedTokens[count]="TREATMENT";
				treatments.add(count);
			}
		}
		all.addAll(problems);all.addAll(tests);all.addAll(treatments);

		if(all.size()<2)
			return ret;

		//for each first entity
		for(int ent1=0; ent1<all.size()-1; ent1++){
			//for every other entity
			for(int ent2=ent1+1; ent2<all.size(); ent2++){
				if(!problems.contains(all.get(ent1))&&!problems.contains(all.get(ent2)))
					continue;
				String[] contestant=replacedTokens.clone();
				contestant[all.get(ent1)]+="_";
				contestant[all.get(ent2)]+="_";

				String contString = "";
				for (String string : contestant) {
					if(string.length()>0)
						contString+=string+" ";
				}
				ret.add(contString.trim());
			}

		}

		return ret;
	}


	private String[] replaceWithCon(String line, int lineNumber, String txtFile) throws IOException {
		BufferedReader br1 = new BufferedReader(new FileReader("../competition_groundTruth/"+txtFile.substring(0,txtFile.length()-3)+"con"));
		String[] tokens = line.split("\\s");
		while(br1.ready()){
			String concept = br1.readLine();
			Pattern conPattern = Pattern.compile("\\d+:(\\d+) \\d+:(\\d+)");
			Matcher conMatcher = conPattern.matcher(concept);
			int conLineNumber=-1;
			if(conMatcher.find()){
				conLineNumber=Integer.parseInt(conMatcher.group().split(":")[0]);
				if(conLineNumber==lineNumber){
					String classification = (concept.split("\\|\\|t=\"")[1].split("\"")[0].toUpperCase());
					int start=Integer.parseInt(conMatcher.group(1));
					int end=Integer.parseInt(conMatcher.group(2));
					conceptDictionary.put(start, concept);
					tokens[start]=classification+"_";
					for(int i=end; i>start; i--){
						tokens[i]="";
					}
				}
			}
		}
		br1.close();
		String modLine = "";
		for (String token : tokens) {
			if(token.length()>0){
				modLine +=token+" ";
			}
		}
		modLine=modLine.trim();

		if(conceptDictionary.size()>0)
			sortConcepts();

		//Sid.log(modLine);
		return tokens;
	}

}


/**
 * Allow a collection of trees, that is a Treebank, appear to be a collection
 * of GrammaticalStructures.
 *
 * @author danielcer
 *
 */
class TreeBankGrammaticalStructureWrapper extends AbstractCollection<GrammaticalStructure>{
	final public Treebank treebank;
	final public boolean keepPunct;
	private Map<GrammaticalStructure, Tree> origTrees = new WeakHashMap<GrammaticalStructure, Tree>();


	public TreeBankGrammaticalStructureWrapper(Treebank wrappedTreeBank) {
		treebank = wrappedTreeBank;
		keepPunct = false;
	}

	public TreeBankGrammaticalStructureWrapper(Treebank wrappedTreeBank, boolean keepPunct) {
		treebank = wrappedTreeBank;
		this.keepPunct = keepPunct;
	}

	@Override
	public Iterator<GrammaticalStructure> iterator() {
		return new gsIterator();
	}


	public Tree getOriginalTree(GrammaticalStructure gs) {
		return origTrees.get(gs);
	}

	private class gsIterator implements Iterator<GrammaticalStructure> {
		Iterator<Tree> tbIterator = treebank.iterator();

		public boolean hasNext() {
			return tbIterator.hasNext();
		}

		public GrammaticalStructure next() {
			Tree t = tbIterator.next();


			GrammaticalStructure gs = (keepPunct ?
					new EnglishGrammaticalStructure(t, Filters.<String>acceptFilter()) :
						new EnglishGrammaticalStructure(t));

			origTrees.put(gs, t);
			return gs;
		}


		public void remove() {
			tbIterator.remove();
		}

	}

	@Override
	public int size() {
		return treebank.size();
	}
}
