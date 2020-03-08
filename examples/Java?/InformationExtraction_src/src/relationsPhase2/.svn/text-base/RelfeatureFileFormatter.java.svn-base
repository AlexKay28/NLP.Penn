package relationsPhase2;

import util.Sid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RelfeatureFileFormatter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//BufferedReader br = new BufferedReader(new FileReader("models/relInstanceModel_v2.txt"));
		BufferedReader br = new BufferedReader(new FileReader("models/sentimentModel.txt"));
		
		String format = "SVM_light";
		//format = "SVM_string";
		//format = "REGULAR";
		//format = "DUMMY_REGULAR";
		//format = "JustLabel";
		//format = "SVM_adjusted";	
		//format = "CRF_adjusted";
		//format = "CRF_training";
		//format="CRF";
		
		//PrintWriter pwr = new PrintWriter(new FileWriter("models/relInstanceModel_v2_SVM_light.txt"));
		PrintWriter pwr = new PrintWriter(new FileWriter("models/sentimentModel_SVM_light.txt"));
		
		while(br.ready()){
			String line = br.readLine();
			
			String ret=null;
			if (format.equals("SVM_light")) {
				line = line.substring(line.indexOf(' ')).trim();
				if(!line.contains(" "))
					continue;
				String target = line.substring(line.lastIndexOf(' ')).trim();
				line = line.substring(0, line.lastIndexOf(' ')).trim();
				String[] tokens = line.split("\\s+");
				ret = target + " ";
				for (String token : tokens) {
					if(token.startsWith("DistCon")||token.startsWith("LineLength"))
						ret+=token.replaceFirst("_", ":") + " ";
					else
						ret += token.replaceAll(":", "_colon_").replaceAll("#","_hash_") + ":1 ";
				}
			}
			else if (format.equals("CRF")) {
				line = line.substring(line.indexOf(' ')).trim();
				ret = line.substring(0, line.lastIndexOf(' ')).trim();
			}
			else if (format.equals("CRF_training")) {
				ret = line.substring(line.indexOf(' ')).trim();
				
			}
			else if (format.equals("CRF_adjusted")) {
				line = line.substring(line.indexOf(' ')).trim();
				line = line.substring(0, line.lastIndexOf(' ')).trim();
				String[] tokens = line.split("\\s+");
				ret = "";
				for (String token : tokens) {
					if(token.startsWith("Pattern_")||token.startsWith("LineLength_"))
						continue;
					else
						ret += token + " ";
				}
			}
			else if (format.equals("SVM_adjusted")) {
				line = line.substring(line.indexOf(' ')).trim();
				String target = line.substring(line.lastIndexOf(' ')).trim();
				line = line.substring(0, line.lastIndexOf(' ')).trim();
				String[] tokens = line.split("\\s+");
				ret = target + " ";
				for (String token : tokens) {
					if(token.startsWith("Pattern_")||token.startsWith("LineLength_"))
						continue;
					if(token.startsWith("DistCon")||token.startsWith("LineLength"))
						ret+=token.replaceFirst("_", ":") + " ";
					else
						ret += token.replaceAll(":", "_colon_").replaceAll("#","_hash_") + ":1 ";
				}
			}
			else if(format.equals("SVM_string")){
				line = line.substring(line.indexOf(' ')).trim();
				String target = line.substring(line.lastIndexOf(' ')).trim();
				line = line.substring(0, line.lastIndexOf(' ')).trim();
				String[] tokens = line.split("\\s");
				ret = target + " ";
				for (String token : tokens) {
					ret += token + " ";
				}
			}
			else if(format.equals("REGULAR")){
				String instanceId = line.substring(0,line.indexOf(' ')).trim();
				line = line.substring(line.indexOf(' ')).trim();
				String target = line.substring(line.lastIndexOf(' ')).trim();
				line = line.substring(0, line.lastIndexOf(' ')).trim();
				ret = instanceId+" "+target + " " + line;
				
			}
			else if(format.equals("DUMMY_REGULAR")){
				String instanceId = line.substring(0,line.indexOf(' ')).trim();
				line = line.substring(line.indexOf(' ')).trim();
				String target = line.substring(line.lastIndexOf(' ')).trim();
				line = line.substring(0, line.lastIndexOf(' ')).trim();
				ret = instanceId+" "+target + " " + line.split("\\s")[0];
				
			}
			else if (format.equals("JustLabel")) {
				line = line.substring(line.indexOf(' ')).trim();
				String target = line.substring(line.lastIndexOf(' ')).trim();
				ret = target ;
			}
			
			if(format.startsWith("CRF"))
				ret=ret.trim()+"\n";
			else
				ret = ret.trim();
			Sid.log(ret);
			pwr.println(ret);
			
		}
		pwr.close();
		
		
		
		br.close();
	}

}
