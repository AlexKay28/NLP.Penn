package util.chunk;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import opennlp.tools.lang.english.TreebankChunker;

import util.Sid;
import util.tokenizer.SidIndoEuropeanTokenizerFactory;

import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmDecoder;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.FastCache;


public class SidChunker {
	
	public HmmDecoder mPosTagger;
    public TokenizerFactory mTokenizerFactory;
    public TreebankChunker mChunker;

   

    public SidChunker()  {
    	File hmmFile = new File("models/pos-en-bio-genia.HiddenMarkovModel");
		int cacheSize = Integer.valueOf("20");
		FastCache<String,double[]> cache = new FastCache<String,double[]>(cacheSize);

		// read HMM for pos tagging
		HiddenMarkovModel posHmm;
		try {
			posHmm
			= (HiddenMarkovModel)
			AbstractExternalizable.readObject(hmmFile);
		} catch (IOException e) {
			System.out.println("Exception reading model=" + e);
			e.printStackTrace(System.out);
			return;
		} catch (ClassNotFoundException e) {
			System.out.println("Exception reading model=" + e);
			e.printStackTrace(System.out);
			return;
		}		
		// construct chunker
		this.mPosTagger  = new HmmDecoder(posHmm,null,cache);
		this.mTokenizerFactory = SidIndoEuropeanTokenizerFactory.INSTANCE;
		try {
			this.mChunker = new TreebankChunker("models/EnglishChunk.bin.gz");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /**
     * get all the chunks in the line
     * @param line
     * @return
     * @throws IOException
     */
    public  ArrayList<String> getNPChunks(String line) throws IOException{
    	// apply chunker & pos tagger
		String[] tokens 
		= this.mTokenizerFactory
		.tokenizer(line.toCharArray(),0,line.length())
		.tokenize();
		String[] tags = this.mPosTagger.tag(Arrays.asList(tokens)).tags().toArray(new String[0]);
		
		String[] chunks = this.mChunker.chunk(tokens,tags);
		
		ArrayList<String> nounPhrases = new ArrayList<String>();
		String chunk = "";
		for(int count=0; count<chunks.length; count++){
			if((chunks[count].equals("B-NP")&& chunk.equals(""))||chunks[count].equals("I-NP"))
				chunk += tokens[count]+" ";
			else if(chunks[count].equals("B-NP")&& !chunk.equals("")){
				nounPhrases.add(chunk.trim());
				chunk = tokens[count]+" ";
			}
			else if(!chunk.equals("")){
				nounPhrases.add(chunk.trim());
				chunk = "";
			}
		}
		if(!chunk.equals("")){
			nounPhrases.add(chunk.trim());
			chunk = "";
		}
		//Sid.log(System.currentTimeMillis()-start);start=System.currentTimeMillis();
		return nounPhrases;
    }
	

    /**
     * get all the chunks with their tags
     * @param line
     * @return
     * @throws IOException
     */
    public  String getNPReplacement(String line){
    	// apply chunker & pos tagger
		String[] tokens_
		= this.mTokenizerFactory
		.tokenizer(line.toCharArray(),0,line.length())
		.tokenize();
		String[] tags = this.mPosTagger.tag(Arrays.asList(tokens_)).tags().toArray(new String[0]);
		String[] chunks = this.mChunker.chunk(tokens_,tags);
		
		String[] finalTokens = tokens_;
		for(int count=0; count<chunks.length; count++){
			if(!tags[count].equals("DT") && !tags[count].equals("CD") && count!=chunks.length-1 && chunks[count+1].equals("I-NP") )
				finalTokens[count] = "";
		}
		
		String ret = "";
		for (String token : finalTokens) {
			if(token.equals(""))
				continue;
			
			if(token.matches("\\p{Punct}")&&line.contains(" "+token)&&line.contains(token+" "))
				ret = ret + token + " ";
			else if(token.matches("\\p{Punct}")&&line.contains(" "+token))
				ret = ret+token;
			else if(token.matches("\\p{Punct}")&&line.contains(token+" "))
				ret = ret.trim()+token+" ";
			else if(token.matches("\\p{Punct}"))
				ret = ret.trim()+token;
			else
				ret = ret + token + " ";
			/*
			else if(token.matches("[-]")) 
				ret = ret.trim()+token;
			else if(token.matches("[\\(\\[\\{]"))
				ret = ret+token;
			else if(token.matches("\\p{Punct}")) 
				ret = ret.trim()+token+" ";
			else
				ret = ret + token + " ";*/
		}
		
		return ret;
		
		/*ArrayList<String> nounPhrases = new ArrayList<String>();
		String chunk = "";
		for(int count=0; count<chunks.length; count++){
			if((chunks[count].equals("B-NP")&& chunk.equals(""))||chunks[count].equals("I-NP")){
				chunk += tokens_[count]+"_"+tags[count]+" ";
				if(!tags[count].equals("DT") && count!=chunks.length-1 && chunks[count+1].equals("I-NP") )
					finalTokens[count] = "";
			}
			else if(chunks[count].equals("B-NP")&& !chunk.equals("")){
				nounPhrases.add(chunk.trim());
				chunk = tokens_[count]+"_"+tags[count]+" ";
			}
			else if(!chunk.equals("")){
				nounPhrases.add(chunk.trim());
				chunk = "";
			}
		}
		if(!chunk.equals("")){
			nounPhrases.add(chunk.trim());
			chunk = "";
		}
		//Sid.log(System.currentTimeMillis()-start);start=System.currentTimeMillis();
		return nounPhrases;*/
    }

    
    
    public static void main(String[] args) throws IOException {

		SidChunker sc = new SidChunker();
		//Sid.log(sc.getNPReplacement("The findings are being announced at the European Association for the Study of Diabetes meeting in Rome. Exenatide improves blood sugar control and gives progressive weight reductions in patients with type 2 diabetes. In this randomised trial, 259 patients completed a 30-week course of either a long-acting release formulation of exenatide 2mg administered once weekly (129 patients), or 10?g exenatide twice a day (130 patients)."));
		//Sid.log(sc.getNPReplacement("a and b went to sita."));
		Sid.log(sc.getNPChunks("The findings are being announced at the European Association for the Study of Diabetes meeting in Rome. Exenatide improves blood sugar control and gives progressive weight reductions in patients with type 2 diabetes. In this randomised trial, 259 patients completed a 30-week course of either a long-acting release formulation of exenatide 2mg administered once weekly (129 patients), or 10?g exenatide twice a day (130 patients)."));
      
		
	}
}
