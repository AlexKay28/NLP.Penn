package subtasks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;



public class Clustering_Dictionary {

	public static void main(String[] args) throws IOException{
		BufferedReader br1 = new BufferedReader(new FileReader("../IE_data/i2b2_4clusteringAll.arff"));
		BufferedReader br2 = new BufferedReader(new FileReader("../IE_data/i2b2_200_clusters.txt"));
		
		//br1.readLine();br1.readLine();br1.readLine();br1.readLine();br1.readLine();
		System.setOut(new PrintStream("dict/i2b2_200_clustering.dict"));
		int count=0;
		while(br1.ready()&&br2.ready()){
			String line1=br1.readLine();
			if(line1.startsWith("%\t")){
				String term = line1.split("\t")[1];
				String line2 = br2.readLine();
				String clusterNum = line2.split("\\s+")[1];
				System.out.println(term+"\t"+clusterNum);
			}
			//System.out.println(br2.readLine()+"\t"+ br1.readLine());
			//System.out.println(br2.readLine().split("\t")[1]+"\t"+ br1.readLine().split("\\s+")[3].split(":")[1]);
		}
	}
	
}
