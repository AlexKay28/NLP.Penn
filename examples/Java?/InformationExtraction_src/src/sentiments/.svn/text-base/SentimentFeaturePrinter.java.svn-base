package sentiments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import relations.MatchInteractions;
import relationsPhase2.Dijkstra;
import relationsPhase2.Edge;
import relationsPhase2.Vertex;
import util.Sid;
import util.tokenizer.BobTokenizer;
import concepts.main.FeaturePrinter_incl_OffsetConj;
import concepts.main.FeaturePrinter_incl_OffsetConj_Testing;
import dragon.nlp.Sentence;
import dragon.nlp.Token;
import dragon.nlp.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.Treebank;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.Filters;


public class SentimentFeaturePrinter {

	FeaturePrinter_incl_OffsetConj fp;
	LexicalizedParser lp;

	String sent;
	Sentence sentence;
	ArrayList<Token> tokens;
	String[] stringTokens;
	ArrayList<String> lemmas;
	String[] posTags;
	String[] phraseChunks;
	List<TypedDependency> deps;
	//String section;
	//public static ArrayList<String> stopList = new ArrayList<String>();
	public static final int MAX_words =100;
	
	public SentimentFeaturePrinter() throws IOException{
		fp = new FeaturePrinter_incl_OffsetConj();
		lp = new LexicalizedParser("models/englishPCFG.ser.gz");
	}


	public static void main(String[] args) {

		boolean training = true;
		//training=false;

		try {

			PrintWriter pwr = new PrintWriter(new FileWriter("models/sentimentModel.txt"));
			SentimentFeaturePrinter rp = new SentimentFeaturePrinter();
			
			String[] corpusFiles = {"/home/siddhartha/currentProjects/sentimentAnalysis/dict/positive_andersen.txt","/home/siddhartha/currentProjects/sentimentAnalysis/dict/negative_andersen.txt"};
			String[] corpusCatgories = {"pos","neg"};
			
			for(int i=0; i<corpusCatgories.length; i++){
				String corpusFile = corpusFiles[i];
				String category = corpusCatgories[i];
				BufferedReader br1 = new BufferedReader(new FileReader(corpusFile));
				int lineNumber=0;
				while(br1.ready()){
					lineNumber++;
					String line = br1.readLine();
					Sid.log("Line:"+line);
					
					if(line.split("\\s+").length>MAX_words)
						continue;
					
					rp.getData(line);
					String instanceName = category+lineNumber;
					//Sid.log("\tInstance Name:"+instanceName);
					String features = rp.getSentimentFeatures(line,instanceName).trim();
					//Sid.log("\tFeature Set:"+features);
					if(training){
						Sid.log(instanceName+" "+features.trim()+" "+category);
						pwr.println(instanceName+" "+features.trim()+" "+category);
						Sid.log("");
					}
					else{
						String label = "None";
						Sid.log(instanceName+" "+features.trim()+" "+label);
						pwr.println(instanceName+" "+features.trim()+" "+label);
						Sid.log("");
					}
				}
				br1.close();
			}
			pwr.flush();
			pwr.close();			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@SuppressWarnings("unused")
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

		//tokenization and lemmatization
		sent=line;
		stringTokens = new BobTokenizer().getTokens(line).toArray(new String[0]);
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
		
		lemmas=new ArrayList<String>();
		for (int count=0; count<stringTokens.length; count++) {
			String lemma = fp.lemmatiser.lemmatize(sentence.getWord(count).getContent(), sentence.getWord(count).getPOSIndex());
			lemmas.add(lemma);
		}
		

		//LingPipe tagging
		posTags = fp.chunker.mPosTagger.tag(Arrays.asList(stringTokens)).tags().toArray(new String[0]);

		//Open-NLP chunking
		phraseChunks = fp.chunker.mChunker.chunk(stringTokens,posTags);


		//parse -- will add later
		/*lp.parse(wordList);
		Tree parseTree = lp.getBestPCFGParse();
		Treebank tb = new MemoryTreebank(new TreeNormalizer());
		tb.add(parseTree);
		Collection<GrammaticalStructure> gsBank = new TreeBankGrammaticalStructureWrapper(tb, false);
		deps =null;
		for (GrammaticalStructure gs : gsBank) {
			deps = gs.typedDependenciesCCprocessed(true);
		}*/
	}


	/**
	 * @param line
	 * @param instanceName
	 * @return
	 */
	private String getSentimentFeatures(String line, String instanceName) {

		////Surface features

		
		////Lexical features

		//all-tokens
		String feature_tok=""; String feature_tok_pos="";int count=0;
		for (String tok : stringTokens) {
			//tok to lower case
			tok=tok.toLowerCase();
			feature_tok+="Tok_"+tok+" ";
			String pos="";
			if(count<stringTokens.length/3.0)
				pos="BEG";
			else if(count>2*stringTokens.length/3.0)
				pos="END";
			else
				pos="MID";
			feature_tok_pos+="TokPos"+pos+"_"+tok+" ";
			count++;
		}
		feature_tok=feature_tok.trim();

		//lemmas
		String feature_lemma="";
		for(int i=0; i<stringTokens.length; i++){
			feature_lemma+="Lemma_"+lemmas.get(i).toLowerCase()+" ";
		}
		
		//word bigrams
		String feature_bigram="";
		for(int i=0; i<stringTokens.length-1; i++)
			feature_bigram+="Bigram_"+stringTokens[i]+"_"+stringTokens[i+1]+" ";
		
		//word trigrams
		String feature_trigram="";
		for(int i=0; i<stringTokens.length-2; i++)
			feature_trigram+="Trigram_"+stringTokens[i]+"_"+stringTokens[i+1]+"_"+stringTokens[i+2]+" ";
		
				
		////syntactic features
		
		//Adjective words
		String featureAdjWords="";
		for(int i=0; i<stringTokens.length; i++ ){
			if(posTags[i].startsWith("JJ"))
					featureAdjWords+="Adj_"+lemmas.get(i)+" ";
		}
		
		//Adverb words
		String featureAdvWords="";
		for(int i=0; i<stringTokens.length; i++ ){
			if(posTags[i].startsWith("RB"))
					featureAdvWords+="Adv_"+lemmas.get(i)+" ";
		}
				
		//verb phrases
		String featureVerbPhrases="";
		for(int i=0; i<stringTokens.length; i++ ){
			if(phraseChunks[i].endsWith("VP"))
				featureVerbPhrases+="VP_"+stringTokens[i]+" ";
		}

		
		//syntactic connections --- need to think of this
/*		String feature_synBigram="";
		for (TypedDependency dep : deps) {
			String reln = dep.reln().toString();
			String dep1 = dep.dep().toString();int dep1Num=Integer.parseInt(dep1.substring(dep1.lastIndexOf('-')+1));
			String dep2 = dep.gov().toString();int dep2Num=Integer.parseInt(dep2.substring(dep2.lastIndexOf('-')+1));
			if((dep1Num>=ent1Start+1&&dep1Num<=ent1End+1&&!(dep2Num>=ent1Start+1&&dep2Num<=ent1End+1))||(dep1Num>=ent2Start+1&&dep1Num<=ent2End+1&&!(dep2Num>=ent2Start+1&&dep2Num<=ent2End+1)))
				feature_synBigram+="SynBigram_"+reln+"_"+dep2.substring(0, dep2.lastIndexOf('-'))+" ";
			else if((dep2Num>=ent1Start+1&&dep2Num<=ent1End+1&&!(dep1Num>=ent1Start+1&&dep1Num<=ent1End+1))||(dep2Num>=ent2Start+1&&dep2Num<=ent2End+1&&!(dep1Num>=ent2Start+1&&dep1Num<=ent2End+1)))
				feature_synBigram+="SynBigram_"+reln+"_"+dep1.substring(0, dep1.lastIndexOf('-'))+" ";
		}

*/		
		
		
		String ret = 
			feature_tok.trim()+" "+
			feature_lemma.trim()+" "+
			feature_bigram.trim()+" "+
			feature_trigram.trim()+" "+
			featureAdjWords.trim()+" "+
			featureAdvWords.trim()+" "+
			featureVerbPhrases.trim()+" "+
			featureVerbPhrases.trim()+" ";


		return ret;
	}




	@SuppressWarnings("unused")
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
