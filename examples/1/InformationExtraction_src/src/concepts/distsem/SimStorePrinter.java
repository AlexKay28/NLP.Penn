/**
 * @author siddhartha
 */
package concepts.distsem;

//import general.Sid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

import util.Sid;
import util.WebPageFetcher;




/**
 * prints the similar words kernel
 * @author sjonnalagadda
 *
 */
public class SimStorePrinter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		String vectorStore="models/i2b2_permtermvectors.bin";
		String indexLoc = "models/i2b2_positional_index";
		String output="models/simFindStores_i2b2_dummy.txt";
		String corpusIndexLoc = "models/i2b2_positional_index";
		int numResults = 10;
		
		if(args.length!=0){
			
			if(args.length!=5){
				System.err.println("fewer/more arguments");
				return;
			}
			
			vectorStore=args[0];
			indexLoc=args[1];
			output=args[2];
			corpusIndexLoc=args[3];
			numResults = Integer.parseInt(args[4]);
		}
		SimFind sFind = new SimFind(vectorStore,indexLoc);
		
		
		
		
		PrintWriter pwr = new PrintWriter(new FileWriter(output));
		
		IndexReader indexReader = IndexReader.open(FSDirectory.open(new File(corpusIndexLoc)));
		TermEnum tEnum = indexReader.terms();
		int count=0;
		while(tEnum.next()){
			count++;
			String token = tEnum.term().text();
			if(token.matches("[0-9`~!@#$%^&*()-=_+\\[\\]\\\\{}|;\':\\\",\\./<>?]+"))
				continue;
			System.out.println(token);
			
			
			ArrayList<String> ret = sFind.getSimWords(token, numResults);
			if(ret.size()==0)
				continue;
			pwr.print(token+"\t");
			for (String simString : ret) {
				pwr.print(simString+"\t");
			}
			pwr.println();pwr.flush();
		}
		
		pwr.close();
		System.out.print("\n\n\n\n\n"+count);
	}

}
