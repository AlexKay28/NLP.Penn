package util.parser;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import util.Sid;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.MemoryTreebank;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeNormalizer;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.Treebank;

public class MyStanfordParser {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Treebank tb = new MemoryTreebank(new TreeNormalizer());
	    Collection<GrammaticalStructure> gsBank = null;
	    String parserFile = "models/englishPCFG.ser.gz";
	    LexicalizedParser lp = new LexicalizedParser(parserFile);
	    BufferedReader isr = new BufferedReader(new InputStreamReader(System.in));
	    while(true){
	    	long start = System.currentTimeMillis();
			
	    	String line = isr.readLine();
	    	PTBTokenizer<Word> ptb = PTBTokenizer.newPTBTokenizer(new StringReader(line));
	        List<Word> words = ptb.tokenize();
	        lp.parse(words);
	        //Tree parseTree = lp.getBestParse();
	        Tree parseTree = lp.getBestPCFGParse();
	        tb.add(parseTree);
	        gsBank = new TreeBankGrammaticalStructureWrapper(tb, false);
	        for (GrammaticalStructure gs : gsBank) {

				Tree t;
				if (gsBank instanceof TreeBankGrammaticalStructureWrapper) {
					t = ((TreeBankGrammaticalStructureWrapper)gsBank).getOriginalTree(gs);
				} else {
					t = gs.root(); // recover tree
				}
				TreePrint tp = new TreePrint("penn");
			    PrintWriter pwr = new PrintWriter(new FileWriter("dummy.txt"));
				tp.printTree(t);
			    tp.printTree(t,pwr);
				pwr.close();
				BufferedReader br = new BufferedReader(new FileReader("dummy.txt"));
				String text = br.readLine().replace("ROOT", "S1");
				while(br.ready()){
					text+=br.readLine();
				}
				
				Sid.log(text);
				MyPennTreeReader.printDependencies(gs.typedDependenciesCCprocessed(true), t, false);

				System.out.println("Elapsed time: " + (System.currentTimeMillis() - start));
				System.out.println("Elapsed time: " + (System.currentTimeMillis() - start));
			}
	    }
	}

}
