/**
 * @author siddhartha
 */
package concepts.distsem;

//import general.Sid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

import util.Sid;



/**
 * prints the similar words kernel
 * @author sjonnalagadda
 *
 */
public class SimStorePrinterMT implements Runnable{

	public static int numThreads=10;
	public static HashMap<String,String> allAnswers=new HashMap<String,String>();
	public static int countProcessed=0;
	public SimFind sFind;
	int numResults;
	PrintWriter pwr;
	public String token; 
	public SimStorePrinterMT(String vectorStore, String indexLoc, int numResults, PrintWriter printWriter, String token) {
		this.sFind = new SimFind(vectorStore,indexLoc);
		this.numResults = numResults;
		this.token=token;
		this.pwr=printWriter;
	}

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
		
		SimStorePrinterMT simStore0 = new SimStorePrinterMT(vectorStore,indexLoc,numResults,new PrintWriter(new FileWriter(output,true)),"");	
		SimStorePrinterMT simStore1 = new SimStorePrinterMT(vectorStore,indexLoc,numResults,new PrintWriter(new FileWriter(output,true)),"");	
		SimStorePrinterMT simStore2 = new SimStorePrinterMT(vectorStore,indexLoc,numResults,new PrintWriter(new FileWriter(output,true)),"");	
		SimStorePrinterMT simStore3 = new SimStorePrinterMT(vectorStore,indexLoc,numResults,new PrintWriter(new FileWriter(output,true)),"");	
		SimStorePrinterMT simStore4 = new SimStorePrinterMT(vectorStore,indexLoc,numResults,new PrintWriter(new FileWriter(output,true)),"");	
		SimStorePrinterMT simStore5 = new SimStorePrinterMT(vectorStore,indexLoc,numResults,new PrintWriter(new FileWriter(output,true)),"");	
		SimStorePrinterMT simStore6 = new SimStorePrinterMT(vectorStore,indexLoc,numResults,new PrintWriter(new FileWriter(output,true)),"");	
		SimStorePrinterMT simStore7 = new SimStorePrinterMT(vectorStore,indexLoc,numResults,new PrintWriter(new FileWriter(output,true)),"");	
		SimStorePrinterMT simStore8 = new SimStorePrinterMT(vectorStore,indexLoc,numResults,new PrintWriter(new FileWriter(output,true)),"");	
		SimStorePrinterMT simStore9 = new SimStorePrinterMT(vectorStore,indexLoc,numResults,new PrintWriter(new FileWriter(output,true)),"");	
		
		IndexReader indexReader = IndexReader.open(FSDirectory.open(new File(corpusIndexLoc)));
		TermEnum tEnum = indexReader.terms();
		int count=0;
		while(tEnum.next()){
			String token = tEnum.term().text();
			if(token.matches("[0-9`~!@#$%^&*()-=_+\\[\\]\\\\{}|;\':\\\",\\./<>?]+"))
				continue;
			System.out.println(token);
			
			while(Thread.activeCount()>SimStorePrinterMT.numThreads){
				//Sid.log("more thread:\t"+Thread.activeCount()+"\tcount:\t"+count);
				Thread.yield();
			}
			
			if(count%10==0){
				while(simStore0.token!="")
					Thread.yield();
				simStore0.token = token;
				Thread thread = new Thread(simStore0);
				thread.start();
			}
			else if(count%10==1){
				while(simStore1.token!="")
					Thread.yield();simStore1.token = token;
				Thread thread = new Thread(simStore1);
				thread.start();
			}
			else if(count%10==2){
				while(simStore2.token!="")
					Thread.yield();
				simStore2.token = token;
				Thread thread = new Thread(simStore2);
				thread.start();
			}
			else if(count%10==3){
				while(simStore3.token!="")
					Thread.yield();
				simStore3.token = token;
				Thread thread = new Thread(simStore3);
				thread.start();
			}
			else if(count%10==4){
				while(simStore4.token!="")
					Thread.yield();
				simStore4.token = token;
				Thread thread = new Thread(simStore4);
				thread.start();
			}
			else if(count%10==5){
				while(simStore5.token!="")
					Thread.yield();
				simStore5.token = token;
				Thread thread = new Thread(simStore5);
				thread.start();
			}
			else if(count%10==6){
				while(simStore6.token!="")
					Thread.yield();
				simStore6.token = token;
				Thread thread = new Thread(simStore6);
				thread.start();
			}
			else if(count%10==7){
				while(simStore7.token!="")
					Thread.yield();
				simStore7.token = token;
				Thread thread = new Thread(simStore7);
				thread.start();
			}
			else if(count%10==8){
				while(simStore8.token!="")
					Thread.yield();
				simStore8.token = token;
				Thread thread = new Thread(simStore8);
				thread.start();
			}
			else if(count%10==9){
				while(simStore9.token!="")
					Thread.yield();
				simStore9.token = token;
				Thread thread = new Thread(simStore9);
				thread.start();
			}	
			count++;
			
		}
		
		System.out.print("\n\n\n\n\n"+count);
		simStore0.pwr.close();
	}

	@Override
	public void run() {
		ArrayList<String> ret = sFind.getSimWords(token, numResults);
		if(ret.size()==0)
			return;
		String toAdd=token+"\t";
		for (String simString : ret) {
			toAdd+=(simString+"\t");
		}
		synchronized (toAdd) {
			allAnswers.put(token, toAdd);
			pwr.println(toAdd);
			pwr.flush();
		}
		token="";
	}

}
