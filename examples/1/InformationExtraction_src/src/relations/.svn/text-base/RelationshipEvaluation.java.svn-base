package relations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import util.Sid;

public class RelationshipEvaluation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			File dir_rel = new File("../trn_rel");
			String[] relFiles = dir_rel.list();
			int tp=0,fp=0,fn=0;
			int tpTrIP=0,fpTrIP=0,fnTrIP=0;
			int tpTrWP=0,fpTrWP=0,fnTrWP=0;
			int tpTrCP=0,fpTrCP=0,fnTrCP=0;
			int tpTrAP=0,fpTrAP=0,fnTrAP=0;
			int tpTrNAP=0,fpTrNAP=0,fnTrNAP=0;
			int tpPIP=0,fpPIP=0,fnPIP=0;
			int tpTeRP=0,fpTeRP=0,fnTeRP=0;
			int tpTeCP=0,fpTeCP=0,fnTeCP=0;
			
			int tpTrP=0,fpTrP=0,fnTrP=0;
			int tpTeP=0,fpTeP=0,fnTeP=0;
			for (String relFile : relFiles) {
				ArrayList<String> goldStandard = new ArrayList<String>();
				ArrayList<String> outputSentences = new ArrayList<String>();

				//Sid.log("\t" + relFile);
				BufferedReader br = new BufferedReader(new FileReader(
						"../trn_rel/" + relFile));
				while (br.ready()) {
					String line = br.readLine().trim();
					goldStandard.add(line);
					//Sid.log(line);
				}
				br.close();
				
				br = new BufferedReader(new FileReader(
						"../RESULTS/" + relFile.substring(0,relFile.length()-3)+"res"));
				while (br.ready()) {
					String line = br.readLine().trim();
					outputSentences.add(line);
					//Sid.log(line);
				}
				br.close();
				
				int localTP=0;
				for (String output : outputSentences) {
					String[] outputTokens = output.split("\\|\\|");
					for (String gold : goldStandard) {
						String[] goldTokens = gold.split("\\|\\|");
						if(output.equalsIgnoreCase(gold)||
								(outputTokens[0].equalsIgnoreCase(goldTokens[2])&&
										outputTokens[1].equalsIgnoreCase(goldTokens[1])&&
										outputTokens[2].equalsIgnoreCase(goldTokens[0]))){
							localTP++;
						}
					}
				}
				
				tp+=localTP;
				fp+=outputSentences.size()-localTP;
				fn+=goldStandard.size()-localTP;
				
				
				ArrayList<String> goldStandardTrIP = new ArrayList<String>();
				ArrayList<String> outputSentencesTrIP = new ArrayList<String>();
				for (String string : outputSentences) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TrIP"))
						outputSentencesTrIP.add(string);
				}
				for (String string : goldStandard) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TrIP"))
						goldStandardTrIP.add(string);
				}
				int localTPTrIP=0;
				for (String output : outputSentencesTrIP) {
					String[] outputTokens = output.split("\\|\\|");
					for (String gold : goldStandardTrIP) {
						String[] goldTokens = gold.split("\\|\\|");
						if(output.equalsIgnoreCase(gold)||
								(outputTokens[0].equalsIgnoreCase(goldTokens[2])&&
										outputTokens[1].equalsIgnoreCase(goldTokens[1])&&
										outputTokens[2].equalsIgnoreCase(goldTokens[0]))){
							localTPTrIP++;
						}
					}
				}
				
				tpTrIP+=localTPTrIP;
				fpTrIP+=outputSentencesTrIP.size()-localTPTrIP;
				fnTrIP+=goldStandardTrIP.size()-localTPTrIP;
				
				//TrWP
				ArrayList<String> goldStandardTrWP = new ArrayList<String>();
				ArrayList<String> outputSentencesTrWP = new ArrayList<String>();
				for (String string : outputSentences) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TrWP"))
						outputSentencesTrWP.add(string);
				}
				for (String string : goldStandard) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TrWP"))
						goldStandardTrWP.add(string);
				}
				int localTPTrWP=0;
				for (String output : outputSentencesTrWP) {
					String[] outputTokens = output.split("\\|\\|");
					for (String gold : goldStandardTrWP) {
						String[] goldTokens = gold.split("\\|\\|");
						if(output.equalsIgnoreCase(gold)||
								(outputTokens[0].equalsIgnoreCase(goldTokens[2])&&
										outputTokens[1].equalsIgnoreCase(goldTokens[1])&&
										outputTokens[2].equalsIgnoreCase(goldTokens[0]))){
							localTPTrWP++;
						}
					}
				}
				
				tpTrWP+=localTPTrWP;
				fpTrWP+=outputSentencesTrWP.size()-localTPTrWP;
				fnTrWP+=goldStandardTrWP.size()-localTPTrWP;
				
				
				//TrCP
				ArrayList<String> goldStandardTrCP = new ArrayList<String>();
				ArrayList<String> outputSentencesTrCP = new ArrayList<String>();
				for (String string : outputSentences) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TrCP"))
						outputSentencesTrCP.add(string);
				}
				for (String string : goldStandard) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TrCP"))
						goldStandardTrCP.add(string);
				}
				int localTPTrCP=0;
				for (String output : outputSentencesTrCP) {
					String[] outputTokens = output.split("\\|\\|");
					for (String gold : goldStandardTrCP) {
						String[] goldTokens = gold.split("\\|\\|");
						if(output.equalsIgnoreCase(gold)||
								(outputTokens[0].equalsIgnoreCase(goldTokens[2])&&
										outputTokens[1].equalsIgnoreCase(goldTokens[1])&&
										outputTokens[2].equalsIgnoreCase(goldTokens[0]))){
							localTPTrCP++;
						}
					}
				}
				
				tpTrCP+=localTPTrCP;
				fpTrCP+=outputSentencesTrCP.size()-localTPTrCP;
				fnTrCP+=goldStandardTrCP.size()-localTPTrCP;
				
				//TrAP
				ArrayList<String> goldStandardTrAP = new ArrayList<String>();
				ArrayList<String> outputSentencesTrAP = new ArrayList<String>();
				for (String string : outputSentences) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TrAP"))
						outputSentencesTrAP.add(string);
				}
				for (String string : goldStandard) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TrAP"))
						goldStandardTrAP.add(string);
				}
				int localTPTrAP=0;
				for (String output : outputSentencesTrAP) {
					String[] outputTokens = output.split("\\|\\|");
					for (String gold : goldStandardTrAP) {
						String[] goldTokens = gold.split("\\|\\|");
						if(output.equalsIgnoreCase(gold)||
								(outputTokens[0].equalsIgnoreCase(goldTokens[2])&&
										outputTokens[1].equalsIgnoreCase(goldTokens[1])&&
										outputTokens[2].equalsIgnoreCase(goldTokens[0]))){
							localTPTrAP++;
						}
					}
				}
				
				tpTrAP+=localTPTrAP;
				fpTrAP+=outputSentencesTrAP.size()-localTPTrAP;
				fnTrAP+=goldStandardTrAP.size()-localTPTrAP;
				
				//TrNAP
				ArrayList<String> goldStandardTrNAP = new ArrayList<String>();
				ArrayList<String> outputSentencesTrNAP = new ArrayList<String>();
				for (String string : outputSentences) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TrNAP"))
						outputSentencesTrNAP.add(string);
				}
				for (String string : goldStandard) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TrNAP"))
						goldStandardTrNAP.add(string);
				}
				int localTPTrNAP=0;
				for (String output : outputSentencesTrNAP) {
					String[] outputTokens = output.split("\\|\\|");
					for (String gold : goldStandardTrNAP) {
						String[] goldTokens = gold.split("\\|\\|");
						if(output.equalsIgnoreCase(gold)||
								(outputTokens[0].equalsIgnoreCase(goldTokens[2])&&
										outputTokens[1].equalsIgnoreCase(goldTokens[1])&&
										outputTokens[2].equalsIgnoreCase(goldTokens[0]))){
							localTPTrNAP++;
						}
					}
				}
				
				tpTrNAP+=localTPTrNAP;
				fpTrNAP+=outputSentencesTrNAP.size()-localTPTrNAP;
				fnTrNAP+=goldStandardTrNAP.size()-localTPTrNAP;
				
				
				//PIP
				ArrayList<String> goldStandardPIP = new ArrayList<String>();
				ArrayList<String> outputSentencesPIP = new ArrayList<String>();
				for (String string : outputSentences) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("PIP"))
						outputSentencesPIP.add(string);
				}
				for (String string : goldStandard) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("PIP"))
						goldStandardPIP.add(string);
				}
				int localTPPIP=0;
				for (String output : outputSentencesPIP) {
					String[] outputTokens = output.split("\\|\\|");
					for (String gold : goldStandardPIP) {
						String[] goldTokens = gold.split("\\|\\|");
						if(output.equalsIgnoreCase(gold)||
								(outputTokens[0].equalsIgnoreCase(goldTokens[2])&&
										outputTokens[1].equalsIgnoreCase(goldTokens[1])&&
										outputTokens[2].equalsIgnoreCase(goldTokens[0]))){
							localTPPIP++;
						}
					}
				}
				
				tpPIP+=localTPPIP;
				fpPIP+=outputSentencesPIP.size()-localTPPIP;
				fnPIP+=goldStandardPIP.size()-localTPPIP;
				
				//TeRP
				ArrayList<String> goldStandardTeRP = new ArrayList<String>();
				ArrayList<String> outputSentencesTeRP = new ArrayList<String>();
				for (String string : outputSentences) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TeRP"))
						outputSentencesTeRP.add(string);
				}
				for (String string : goldStandard) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TeRP"))
						goldStandardTeRP.add(string);
				}
				int localTPTeRP=0;
				for (String output : outputSentencesTeRP) {
					String[] outputTokens = output.split("\\|\\|");
					for (String gold : goldStandardTeRP) {
						String[] goldTokens = gold.split("\\|\\|");
						if(output.equalsIgnoreCase(gold)||
								(outputTokens[0].equalsIgnoreCase(goldTokens[2])&&
										outputTokens[1].equalsIgnoreCase(goldTokens[1])&&
										outputTokens[2].equalsIgnoreCase(goldTokens[0]))){
							localTPTeRP++;
						}
					}
				}
				
				tpTeRP+=localTPTeRP;
				fpTeRP+=outputSentencesTeRP.size()-localTPTeRP;
				fnTeRP+=goldStandardTeRP.size()-localTPTeRP;
				
				//TeCP
				ArrayList<String> goldStandardTeCP = new ArrayList<String>();
				ArrayList<String> outputSentencesTeCP = new ArrayList<String>();
				for (String string : outputSentences) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TeCP"))
						outputSentencesTeCP.add(string);
				}
				for (String string : goldStandard) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TeCP"))
						goldStandardTeCP.add(string);
				}
				int localTPTeCP=0;
				for (String output : outputSentencesTeCP) {
					String[] outputTokens = output.split("\\|\\|");
					for (String gold : goldStandardTeCP) {
						String[] goldTokens = gold.split("\\|\\|");
						if(output.equalsIgnoreCase(gold)||
								(outputTokens[0].equalsIgnoreCase(goldTokens[2])&&
										outputTokens[1].equalsIgnoreCase(goldTokens[1])&&
										outputTokens[2].equalsIgnoreCase(goldTokens[0]))){
							localTPTeCP++;
						}
					}
				}
				
				tpTeCP+=localTPTeCP;
				fpTeCP+=outputSentencesTeCP.size()-localTPTeCP;
				fnTeCP+=goldStandardTeCP.size()-localTPTeCP;
				
				//TeP
				ArrayList<String> goldStandardTeP = new ArrayList<String>();
				ArrayList<String> outputSentencesTeP = new ArrayList<String>();
				for (String string : outputSentences) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TeCP")||stringTokens[1].contains("TeRP"))
						outputSentencesTeP.add(string);
				}
				for (String string : goldStandard) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TeCP")||stringTokens[1].contains("TeRP"))
						goldStandardTeP.add(string);
				}
				int localTPTeP=0;
				for (String output : outputSentencesTeP) {
					String[] outputTokens = output.split("\\|\\|");
					for (String gold : goldStandardTeP) {
						String[] goldTokens = gold.split("\\|\\|");
						if((outputTokens[0].equalsIgnoreCase(goldTokens[0])&&outputTokens[2].equalsIgnoreCase(goldTokens[2]))||
								(outputTokens[0].equalsIgnoreCase(goldTokens[2])&&outputTokens[2].equalsIgnoreCase(goldTokens[0]))){
							localTPTeP++;
						}
					}
				}
				
				tpTeP+=localTPTeP;
				fpTeP+=outputSentencesTeP.size()-localTPTeP;
				fnTeP+=goldStandardTeP.size()-localTPTeP;
				
				
				//TrP
				ArrayList<String> goldStandardTrP = new ArrayList<String>();
				ArrayList<String> outputSentencesTrP = new ArrayList<String>();
				for (String string : outputSentences) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TrIP")||stringTokens[1].contains("TrWP")||stringTokens[1].contains("TrCP")||stringTokens[1].contains("TrAP")||
							stringTokens[1].contains("TrNAP"))
						outputSentencesTrP.add(string);
				}
				for (String string : goldStandard) {
					String[] stringTokens = string.split("\\|\\|");
					if(stringTokens[1].contains("TrIP")||stringTokens[1].contains("TrWP")||stringTokens[1].contains("TrCP")||stringTokens[1].contains("TrAP")||
							stringTokens[1].contains("TrNAP"))
						goldStandardTrP.add(string);
				}
				int localTPTrP=0;
				for (String output : outputSentencesTrP) {
					String[] outputTokens = output.split("\\|\\|");
					for (String gold : goldStandardTrP) {
						String[] goldTokens = gold.split("\\|\\|");
						if((outputTokens[0].equalsIgnoreCase(goldTokens[0])&&outputTokens[2].equalsIgnoreCase(goldTokens[2]))||
								(outputTokens[0].equalsIgnoreCase(goldTokens[2])&&outputTokens[2].equalsIgnoreCase(goldTokens[0]))){
							localTPTrP++;
						}
					}
				}
				/*if(goldStandardTrP.size()>localTPTrP)
					Sid.log("Error in:\t"+relFile) ;
				*/
				tpTrP+=localTPTrP;
				fpTrP+=outputSentencesTrP.size()-localTPTrP;
				fnTrP+=goldStandardTrP.size()-localTPTrP;
			}
			
			double precision=(tp*1.0)/(tp+fp);
			double recall=(tp*1.0)/(tp+fn);
			double fScore=2*precision*recall/(precision+recall);
			Sid.log("\n");
			Sid.log("TOTAL\ntp:\t"+tp+"\nfp:\t"+fp+"\nfn:\t"+fn+"\nprecision:\t"+precision+"\nrecall:\t"+recall+"\nfScore:\t"+fScore);
			
			
			precision=(tpTrIP*1.0)/(tpTrIP+fpTrIP);
			recall=(tpTrIP*1.0)/(tpTrIP+fnTrIP);
			fScore=2*precision*recall/(precision+recall);
			Sid.log("\n");
			Sid.log("TrIP\ntp:\t"+tpTrIP+"\nfp:\t"+fpTrIP+"\nfn:\t"+fnTrIP+"\nprecision:\t"+precision+"\nrecall:\t"+recall+"\nfScore:\t"+fScore);
			
			precision=(tpTrWP*1.0)/(tpTrWP+fpTrWP);
			recall=(tpTrWP*1.0)/(tpTrWP+fnTrWP);
			fScore=2*precision*recall/(precision+recall);
			Sid.log("\n");
			Sid.log("TrWP\ntp:\t"+tpTrWP+"\nfp:\t"+fpTrWP+"\nfn:\t"+fnTrWP+"\nprecision:\t"+precision+"\nrecall:\t"+recall+"\nfScore:\t"+fScore);
			
			precision=(tpTrCP*1.0)/(tpTrCP+fpTrCP);
			recall=(tpTrCP*1.0)/(tpTrCP+fnTrCP);
			fScore=2*precision*recall/(precision+recall);
			Sid.log("\n");
			Sid.log("TrCP\ntp:\t"+tpTrCP+"\nfp:\t"+fpTrCP+"\nfn:\t"+fnTrCP+"\nprecision:\t"+precision+"\nrecall:\t"+recall+"\nfScore:\t"+fScore);
			
			precision=(tpTrAP*1.0)/(tpTrAP+fpTrAP);
			recall=(tpTrAP*1.0)/(tpTrAP+fnTrAP);
			fScore=2*precision*recall/(precision+recall);
			Sid.log("\n");
			Sid.log("TrAP\ntp:\t"+tpTrAP+"\nfp:\t"+fpTrAP+"\nfn:\t"+fnTrAP+"\nprecision:\t"+precision+"\nrecall:\t"+recall+"\nfScore:\t"+fScore);
			
			precision=(tpTrNAP*1.0)/(tpTrNAP+fpTrNAP);
			recall=(tpTrNAP*1.0)/(tpTrNAP+fnTrNAP);
			fScore=2*precision*recall/(precision+recall);
			Sid.log("\n");
			Sid.log("TrNAP\ntp:\t"+tpTrNAP+"\nfp:\t"+fpTrNAP+"\nfn:\t"+fnTrNAP+"\nprecision:\t"+precision+"\nrecall:\t"+recall+"\nfScore:\t"+fScore);
			
			precision=(tpPIP*1.0)/(tpPIP+fpPIP);
			recall=(tpPIP*1.0)/(tpPIP+fnPIP);
			fScore=2*precision*recall/(precision+recall);
			Sid.log("\n");
			Sid.log("PIP\ntp:\t"+tpPIP+"\nfp:\t"+fpPIP+"\nfn:\t"+fnPIP+"\nprecision:\t"+precision+"\nrecall:\t"+recall+"\nfScore:\t"+fScore);
			
			precision=(tpTeRP*1.0)/(tpTeRP+fpTeRP);
			recall=(tpTeRP*1.0)/(tpTeRP+fnTeRP);
			fScore=2*precision*recall/(precision+recall);
			Sid.log("\n");
			Sid.log("TeRP\ntp:\t"+tpTeRP+"\nfp:\t"+fpTeRP+"\nfn:\t"+fnTeRP+"\nprecision:\t"+precision+"\nrecall:\t"+recall+"\nfScore:\t"+fScore);
			
			precision=(tpTeCP*1.0)/(tpTeCP+fpTeCP);
			recall=(tpTeCP*1.0)/(tpTeCP+fnTeCP);
			fScore=2*precision*recall/(precision+recall);
			Sid.log("\n");
			Sid.log("TeCP\ntp:\t"+tpTeCP+"\nfp:\t"+fpTeCP+"\nfn:\t"+fnTeCP+"\nprecision:\t"+precision+"\nrecall:\t"+recall+"\nfScore:\t"+fScore);
			
			
			
			precision=(tpTeP*1.0)/(tpTeP+fpTeP);
			recall=(tpTeP*1.0)/(tpTeP+fnTeP);
			fScore=2*precision*recall/(precision+recall);
			Sid.log("\n");
			Sid.log("TeP\ntp:\t"+tpTeP+"\nfp:\t"+fpTeP+"\nfn:\t"+fnTeP+"\nprecision:\t"+precision+"\nrecall:\t"+recall+"\nfScore:\t"+fScore);
			
			precision=(tpTrP*1.0)/(tpTrP+fpTrP);
			recall=(tpTrP*1.0)/(tpTrP+fnTrP);
			fScore=2*precision*recall/(precision+recall);
			Sid.log("\n");
			Sid.log("TrP\ntp:\t"+tpTrP+"\nfp:\t"+fpTrP+"\nfn:\t"+fnTrP+"\nprecision:\t"+precision+"\nrecall:\t"+recall+"\nfScore:\t"+fScore);
			
			
			
		} catch (IOException e) {
			// TODO: handle exception
		}
		
	}

}
