package subtasks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;



public class SVM_Dictionary {

	public static void main(String[] args) throws IOException{
		BufferedReader br1 = new BufferedReader(new FileReader("../i2b2OUT_4svmV2.out"));
		BufferedReader br2 = new BufferedReader(new FileReader("../i2b2OUT_4svmV2.terms"));
		br1.readLine();br1.readLine();br1.readLine();br1.readLine();br1.readLine();
		System.setOut(new PrintStream("../i2b2SVM.dict"));
		while(br1.ready()&&br2.ready()){
			//System.out.println(br2.readLine()+"\t"+ br1.readLine());
			System.out.println(br2.readLine().split("\t")[1]+"\t"+ br1.readLine().split("\\s+")[3].split(":")[1]);
		}
	}
	
}
