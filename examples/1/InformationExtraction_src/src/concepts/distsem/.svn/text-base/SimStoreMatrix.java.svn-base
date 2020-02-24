package concepts.distsem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

import pitt.search.semanticvectors.VectorStoreRAM;
import pitt.search.semanticvectors.VectorUtils;
//import util.Sid;

public class SimStoreMatrix {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String vectorStore="models/i2b2_permtermvectors.bin";
		//String indexLoc = "models/i2b2_positional_index";
		String corpusIndexLoc = "models/i2b2_positional_index";
		String output="models/simStoreMatrix_dummy.txt";
		//int numResults = 10;
		
		//if(args.length!=0){			
		if(true){
			if(args.length!=3){
				System.err.println("Fewer arguments.\n" +
						"usage: java concepts.distsem.SimStoreMatrix <vectorStore> <corpusIndexLoc> <simStoreMatrix>");
				return;
			}
			
			vectorStore=args[0];
			corpusIndexLoc=args[1];
			output=args[2];
		}
		
		//SimFind sFind = new SimFind(vectorStore,indexLoc);
		
		VectorStoreRAM aVectorStoreRAM = new VectorStoreRAM();
		aVectorStoreRAM.InitFromFile(vectorStore);
		PrintWriter pwr = new PrintWriter(new FileWriter(output));
		
		ArrayList<String> tokens2process = new ArrayList<String>();
		IndexReader indexReader = IndexReader.open(FSDirectory.open(new File(corpusIndexLoc)));
		TermEnum tEnum = indexReader.terms();
		while(tEnum.next()){
			String token = tEnum.term().text();
			if(token.matches("[0-9`~!@#$%^&*()-=_+\\[\\]\\\\{}|;\':\\\",\\./<>?]+")||!tEnum.term().field().equals("contents"))
				continue;
			tokens2process.add(token);
			pwr.println(token);
		}
		
		System.out.println(tokens2process.size());
		pwr.println("Matrix begins");
		
		//DefaultSparseFloatMatrix simStoreMatrix = new DefaultSparseFloatMatrix((long)tokens2process.size(),(long)tokens2process.size());
		//Float[][] simStoreMap = new Float[tokens2process.size()][tokens2process.size()];
		for(int i=0; i<tokens2process.size();i++){
			System.out.println(i);
			for(int j=0; j<tokens2process.size();j++){
				String str1 = tokens2process.get(i);
				String str2 = tokens2process.get(j);
				float [] str1Value=null, str2Value=null;
				if((str1Value=aVectorStoreRAM.getVector(str1))!=null
						&& (str2Value=aVectorStoreRAM.getVector(str2))!=null){
					float cosine=VectorUtils.scalarProduct(str1Value, str2Value);
					if(cosine!=0)
						pwr.println(i+"\t"+j+"\t"+cosine);
				}
			}
		}
		
		pwr.close();
		//Sid.log(simStoreMatrix.getSize());
		
	}

}
