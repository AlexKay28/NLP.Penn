package concepts.distsem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class ThesarusPrinter {

	public static void main(String[] args) throws FileNotFoundException{
		String simStoreMatrix = "models/simStoreMatrix.txt";
		int numSims =10;
		String output = "Thesarus_i2b2.txt";
		float simThreshold = -1;
		
		/*if(args.length<3||args.length>4){
			System.err.println("Fewer/More arguments.\n" +
			"usage: java concepts.distsem.ThesarusPrinter <simStoreMatrix> <numSims> <ThesarusOutput>");
			return;
		}*/
		
		if(args.length==3){
			simStoreMatrix = args[0];
			numSims = Integer.parseInt(args[1]);
			output = args[2];
		}
		
		if(args.length==4){
			simStoreMatrix = args[0];
			numSims = Integer.parseInt(args[1]);
			simThreshold = Float.parseFloat(args[2]);
			output = args[3];
		}
		
		Scanner sc = new Scanner(new File(simStoreMatrix));
		PrintWriter pwr = new PrintWriter(new File(output));
		
		//store tokens
		ArrayList<String> tokens = new ArrayList<String>();
		String line=null;
		while(sc.hasNextLine()){
			line=sc.nextLine();
			if(line.split("\\s").length>1||line.endsWith(".txt"))
				break; 
			tokens.add(line);
		}		
		while(line.split("\\s").length<3&&sc.hasNextLine())
			line=sc.nextLine();
		
		//print sims
		int tokenNum=-1;
		HashMap<Integer, Float> sims = null;
		HashMap<Integer, Float> maxs = null;
		while(sc.hasNextLine()){
			int token1Num = Integer.parseInt(line.split("\t")[0]);
			if(token1Num!=tokenNum){
				if(maxs!=null){
					Set<Integer> maxKeySet = maxs.keySet();
					String print=tokens.get(tokenNum);
					for (Integer key : maxKeySet) {
						if(key.equals(tokenNum))
							continue;
						print+="\t"+tokens.get(key);					
					}
					pwr.println(print.trim());
					//Sid.log(print.trim());
				}
								
				sims = new HashMap<Integer, Float>();
				maxs = new HashMap<Integer, Float>();
				tokenNum = token1Num;								
			}
			int token2Num = Integer.parseInt(line.split("\t")[1]);
			float sim = Float.parseFloat(line.split("\t")[2]);
			sims.put(token2Num, sim);
			maxs = addMax(maxs,token2Num,sim,numSims,simThreshold);
			line=sc.nextLine();				
		}
		
		pwr.close();
		
	}

	/**
	 * insertion sort type for getting numSims maximum entries
	 * @param maxs
	 * @param token2Num
	 * @param sim
	 * @param numSims
	 * @param simThreshold 
	 * @return
	 */
	private static HashMap<Integer, Float> addMax(HashMap<Integer, Float> maxs,
			int token2Num, float sim, int numSims, float simThreshold) {
		if(maxs.size()<numSims && sim>simThreshold)
			maxs.put(token2Num, sim);
		//TODO: bug fixed; need to run all the experiments again :(
		else{
			float min = Integer.MAX_VALUE;int minKey=Integer.MAX_VALUE;
			Set<Integer> maxKeySet = maxs.keySet();
			for (Integer key : maxKeySet) {
				if(min>maxs.get(key)){
					min=maxs.get(key);
					minKey=key;
				}					
			}
			if(sim>min){
				maxs.remove(minKey);
				maxs.put(token2Num, sim);
			}
		}
		return maxs;
	}
	
	
}
