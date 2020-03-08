package apps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import util.SimpleCommandLineParser;


/**
 * 
 * @author sjonnalagadda
 *
 */

public class WordSpaceThesarusEvaluator {


	
	public WordSpaceThesarusEvaluator(){
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SimpleCommandLineParser parser = new SimpleCommandLineParser(args);
		String input=parser.getValue("input", "in");;
		doSingleWordTesting(input);
		
	}

	private static void doSingleWordTesting(String input) throws IOException {
		//BufferedReader br = new BufferedReader(new FileReader("_clinicalTrials_2000d_permutation_thesarusMatch_weighted.txt"));
		BufferedReader br = new BufferedReader(new FileReader(input));
		int prpr=0, prte=0, prtr=0, prot=0, prpr_maj=0, prte_maj=0, prtr_maj=0, prot_maj=0;
		int tepr=0, tete=0, tetr=0, teot=0, tepr_maj=0, tete_maj=0, tetr_maj=0, teot_maj=0;
		int trpr=0, trte=0, trtr=0, trot=0, trpr_maj=0, trte_maj=0, trtr_maj=0, trot_maj=0;
		int otpr=0, otte=0, ottr=0, otot=0, otpr_maj=0, otte_maj=0, ottr_maj=0, otot_maj=0;
		
		while(br.ready()){
			String line = br.readLine();
			String[] tokens = line.split("\t");
			HashMap<String, Double> typeCount=new HashMap<String, Double>();
			typeCount.put("pr", 0.0);typeCount.put("te", 0.0);typeCount.put("tr", 0.0);typeCount.put("ot", 0.0);
			if(true)//balance for the same term being present
				typeCount.put(tokens[1], -1.0);
			double max=0; String maxKey=null;
			for(int i=2; i<tokens.length; i++){
				typeCount.put(tokens[i].split("_")[0], 
						typeCount.get(tokens[i].split("_")[0])+Double.parseDouble(tokens[i].split("_")[1]));
				if(max<=typeCount.get(tokens[i].split("_")[0])){
					max=typeCount.get(tokens[i].split("_")[0]);
					maxKey=tokens[i].split("_")[0];
				}
			}
			
			if(tokens[1].equals("pr")){
				prpr+=typeCount.get("pr");
				prte+=typeCount.get("te");
				prtr+=typeCount.get("tr");
				prot+=typeCount.get("ot");
				if(maxKey.equals("pr"))	prpr_maj++;
				else if(maxKey.equals("te"))	prte_maj++;
				else if(maxKey.equals("tr"))	prtr_maj++;
				else if(maxKey.equals("ot"))	prot_maj++;
			}
			else if(tokens[1].equals("te")){
				tepr+=typeCount.get("pr");
				tete+=typeCount.get("te");
				tetr+=typeCount.get("tr");
				teot+=typeCount.get("ot");
				if(maxKey.equals("pr"))	tepr_maj++;
				else if(maxKey.equals("te"))	tete_maj++;
				else if(maxKey.equals("tr"))	tetr_maj++;
				else if(maxKey.equals("ot"))	teot_maj++;
			}
			else if(tokens[1].equals("tr")){
				trpr+=typeCount.get("pr");
				trte+=typeCount.get("te");
				trtr+=typeCount.get("tr");
				trot+=typeCount.get("ot");
				if(maxKey.equals("pr"))	trpr_maj++;
				else if(maxKey.equals("te"))	trte_maj++;
				else if(maxKey.equals("tr"))	trtr_maj++;
				else if(maxKey.equals("ot"))	trot_maj++;
			}
			else if(tokens[1].equals("ot")){
				otpr+=typeCount.get("pr");
				otte+=typeCount.get("te");
				ottr+=typeCount.get("tr");
				otot+=typeCount.get("ot");
				if(maxKey.equals("pr"))	
					otpr_maj++;
				else if(maxKey.equals("te"))	otte_maj++;
				else if(maxKey.equals("tr"))	ottr_maj++;
				else if(maxKey.equals("ot"))	otot_maj++;
			}
		}
		
		System.out.println("prpr="+prpr+"\tprte="+prte+"\tprtr="+prtr+"\tprpr_maj="+prpr_maj+"\tprte_maj="+prte_maj+"\tprtr_maj="+prtr_maj+
				"\ntepr="+tepr+"\ttete="+tete+"\ttetr="+tetr+"\ttepr_maj="+tepr_maj+"\ttete_maj="+tete_maj+"\ttetr_maj="+tetr_maj+
				"\ntrpr="+trpr+"\ttrte="+trte+"\ttrtr="+trtr+"\ttrpr_maj="+trpr_maj+"\ttrte_maj="+trte_maj+"\ttrtr_maj="+trtr_maj
				);
		
		/*System.out.println("\n\npr_recall="+1.0*prpr/(prpr+prte+prtr)+
				"\nte_recall="+1.0*tete/(tepr+tete+tetr)+
				"\ntr_recall="+1.0*trtr/(trpr+trte+trtr)
				);
		
		System.out.println("\n\npr_precision="+1.0*prpr/(prpr+tepr+trpr)+
				"\nte_precision="+1.0*tete/(prte+tete+trte)+
				"\ntr_precision="+1.0*trtr/(prtr+tetr+trtr)
				);
		
		System.out.println("\n\nstrict accuracy="+1.0*(prpr+tete+trtr)/(prpr+tepr+trpr+prte+tete+trte+prtr+tetr+trtr)
				);*/
		
		System.out.println("\n\nprMajor_recall="+1.0*prpr_maj/(prpr_maj+prte_maj+prtr_maj+prot_maj)+
				"\nteMajor_recall="+1.0*tete_maj/(tepr_maj+tete_maj+tetr_maj+teot_maj)+
				"\ntrMajor_recall="+1.0*trtr_maj/(trpr_maj+trte_maj+trtr_maj+trot_maj)
				);
		
		System.out.println("\n\nprMajor_precision="+1.0*prpr_maj/(prpr_maj+tepr_maj+trpr_maj+otpr_maj)+
				"\nteMajor_precision="+1.0*tete_maj/(prte_maj+tete_maj+trte_maj+otte_maj)+
				"\ntrMajor_precision="+1.0*trtr_maj/(prtr_maj+tetr_maj+trtr_maj+otte_maj)
				);
		
		double MicroR = 1.0*(prpr_maj+tete_maj+trtr_maj)/(prpr_maj+tepr_maj+trpr_maj+
				prte_maj+tete_maj+trte_maj+prtr_maj+tetr_maj+trtr_maj+prot_maj+teot_maj+trot_maj);
		
		double MicroP = 1.0*(prpr_maj+tete_maj+trtr_maj)/(prpr_maj+tepr_maj+trpr_maj+otpr_maj+
				prte_maj+tete_maj+trte_maj+otte_maj+prtr_maj+tetr_maj+trtr_maj+ottr_maj);

		System.out.println("\n\nMicroR="+MicroR+"\nMicroP="+MicroP+"\nMicroF="+2*MicroP*MicroR/(MicroP+MicroR));
		
		System.out.println("\n\naccuracyMajor="+1.0*(prpr_maj+tete_maj+trtr_maj+otot_maj)/(prpr_maj+tepr_maj+trpr_maj+otpr_maj+
				prte_maj+tete_maj+trte_maj+otte_maj+prtr_maj+tetr_maj+trtr_maj+ottr_maj+prot_maj+teot_maj+trot_maj+otot_maj)
		);
		
		
		
	}

}
