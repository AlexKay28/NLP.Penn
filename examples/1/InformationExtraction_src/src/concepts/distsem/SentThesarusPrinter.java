package concepts.distsem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import pitt.search.semanticvectors.VectorStoreRAM;
import pitt.search.semanticvectors.VectorUtils;
import util.tokenizer.BobTokenizer;

public class SentThesarusPrinter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		String vectorStore="models/i2b2_permtermvectors.bin";
		String sentFileName = "dict/sent_i2b2UnannotatedDocs_1653_unique.txt";
		int numSims =20;
		String output="models/sentThesarus_local.txt";
				
		if(args.length!=0){
			
			if(args.length!=4){
				System.err.println("fewer/more arguments");
				return;
			}
			
			vectorStore=args[0];
			sentFileName=args[1];
			numSims = Integer.parseInt(args[2]);
			output=args[3];
			
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
			//pwr.println(sent);
		}
		
		System.out.println(sents2process.size());
		//pwr.println("Matrix begins");
		
		for(int i=0; i<sents2process.size();i++){
			System.out.println(i);
			HashMap<Integer, Float> maxs = new HashMap<Integer, Float>();
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
				//System.out.println("\t"+j);
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
				if(cosine!=0){
					if(maxs.size()<numSims)
						maxs.put(j, cosine);
					else{
						float min = Integer.MAX_VALUE;int minKey=Integer.MIN_VALUE;
						Set<Integer> maxKeySet = maxs.keySet();
						for (Integer key : maxKeySet) {
							if(min>maxs.get(key)){
								min=maxs.get(key);
								minKey=key;
							}					
						}
						if(cosine>min){
							maxs.remove(minKey);
							maxs.put(j, cosine);
						}
					}
						
					//pwr.println(i+"\t"+j+"\t"+cosine);
				}
			}
			
			Set<Integer> maxKeySet = maxs.keySet();
			String print=i+"";
			for (Integer key : maxKeySet) {
				if(key.equals(i))
					continue;
				print+="\t"+key;					
			}
			pwr.println(print.trim());
			
		}
		
		pwr.close();
		//Sid.log(simStoreMatrix.getSize());
		
	}

}
