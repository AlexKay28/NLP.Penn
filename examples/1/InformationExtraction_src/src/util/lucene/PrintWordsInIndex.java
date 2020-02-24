package util.lucene;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

public class PrintWordsInIndex {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public static void main(String[] args) throws CorruptIndexException, IOException {
		IndexReader reader = IndexReader.open(FSDirectory.open(new File("dict/bc2geneMention/bc2gm_positional_index")));
		TermEnum tEnum = reader.terms();
		
		PrintWriter pwr = new PrintWriter("dict/bc2geneMention/bc2gm.words");
		while(tEnum.next()){
			pwr.println(tEnum.term().text());
		}
		pwr.close();reader.close();
	}

}
