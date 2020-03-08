package concepts.distsem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

import pitt.search.semanticvectors.VectorStoreRAM;
import pitt.search.semanticvectors.VectorUtils;
//import util.Sid;
import util.tokenizer.BobTokenizer;

public class SentSimStoreMatrix {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String vectorStore="models/i2b2_permtermvectors.bin";
		String sentFileName = "dict/sent_i2b2UnannotatedDocs_1653.txt";
		//String corpusIndexLoc = "models/i2b2_positional_index";
		String output="models/sentSimStoreMatrix_local.txt";
		//int numResults = 10;
		
		if(args.length!=0){
			
			if(args.length!=3){
				System.err.println("fewer/more arguments");
				return;
			}
			
			vectorStore=args[0];
			sentFileName=args[1];
			output=args[2];
			
		}
		//SimFind sFind = new SimFind(vectorStore,indexLoc);
		
		VectorStoreRAM aVectorStoreRAM = new VectorStoreRAM();
		aVectorStoreRAM.InitFromFile(vectorStore);
		PrintWriter pwr = new PrintWriter(new FileWriter(output));
		
		ArrayList<String> sents2process = new ArrayList<String>();
		BufferedReader br  = new BufferedReader(new FileReader(sentFileName));
		while(br.ready()){
			String sent = br.readLine().trim().toLowerCase();
			sents2process.add(sent);
			pwr.println(sent);
		}
		
		System.out.println(sents2process.size());
		pwr.println("Matrix begins");
		
		//DefaultSparseFloatMatrix simStoreMatrix = new DefaultSparseFloatMatrix((long)tokens2process.size(),(long)tokens2process.size());
		//Float[][] simStoreMap = new Float[tokens2process.size()][tokens2process.size()];
		for(int i=0; i<sents2process.size();i++){
			System.out.println(i);
			String sent1 = sents2process.get(i);
			List<String> tokens1 = new BobTokenizer().getTokens(sent1);
			if(tokens1.size()<2)
				continue;
			float[] sum1 =  VectorUtils.createZeroVector();
			for (String token : tokens1) {
				float[] tokenVector=aVectorStoreRAM.getVector(token);
				if(tokenVector!=null)
					sum1 = VectorUtils.addVectors(sum1, tokenVector, 1);
			}
			if(VectorUtils.isZeroVector(sum1))
				continue;
			sum1 = VectorUtils.getNormalizedVector(sum1);
							
			for(int j=0; j<sents2process.size();j++){
				String sent2 = sents2process.get(j);
				List<String> tokens2 = new BobTokenizer().getTokens(sent2);
				if(tokens2.size()<2)
					continue;
				float[] sum2 = VectorUtils.createZeroVector();
				for (String token : tokens2) {
					float[] tokenVector=aVectorStoreRAM.getVector(token);
					if(tokenVector!=null)
						sum2 = VectorUtils.addVectors(sum2, tokenVector, 1);
				}
				
				sum2 = VectorUtils.getNormalizedVector(sum2);
				float cosine=VectorUtils.scalarProduct(sum1, sum2);
				if(cosine!=0)
					pwr.println(i+"\t"+j+"\t"+cosine);				
			}
		}
		
		pwr.close();
		//Sid.log(simStoreMatrix.getSize());
		
	}

}
