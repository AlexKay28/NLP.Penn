package relations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import subtasks.SidSmithWaterman;
import util.Sid;

public class LeaveOneOutRuns {

	public static HashMap<String,String> linePatterns = new HashMap<String, String>();
	public static HashMap<String,String> patternTags = new HashMap<String, String>();
	public static ArrayList<String> patterns = new ArrayList<String>();
	public static LinkedHashMap<Integer, String> conceptDictionary;
	public static ArrayList<String> sortedConcepts;
	//public static ArrayList<String> stopList = new ArrayList<String>();


	public static void main(String[] args) {
		try {
			File dir_txt=new File("../competition_txt");
			String[] txtFiles = dir_txt.list();

			MatchInteractions.loadStopWords();



			//store all interaction pairs and their tags
			BufferedReader br = new BufferedReader(new FileReader("dict/relSnippetsModified.txt"));
			while(br.ready()){
				String text=br.readLine();
				if(text.split("\\|\\|").length==3){
					String pattern=Normalizer.normalizeSentence(text.split("\\|\\|")[2]).trim();
					patternTags.put(pattern, text.split("\\|\\|")[1]);
					linePatterns.put(text.split("\\|\\|")[0],pattern);
					patterns.add(pattern);
				}
			}
			br.close();

			for (String txtFile : txtFiles) {
				BufferedReader br1 = new BufferedReader(new FileReader("../competition_txt/"+txtFile));
				PrintWriter pwr = new PrintWriter(new FileWriter("outputs/competition2_rel/"+txtFile.substring(0,txtFile.length()-3)+"rel"));
				
				int lineNumber=0;
				while(br1.ready()){
					lineNumber++;
					String line = br1.readLine();

					conceptDictionary = new LinkedHashMap<Integer, String>();				
					String[] replacedTokens = replaceWithCon(line,lineNumber,txtFile);
					ArrayList<String> contestants = findContestants(replacedTokens);
					if(contestants.size()==0)
						continue;
					Sid.log(line);
					
					for (String contestant : contestants) {
						String tag=null;
						//ArrayList<String> dummy=new ArrayList<String>();dummy.add(linePatterns.get(contestant));dummy.add(linePatterns.get(contestant));
						Sid.log(linePatterns.get(contestant));
						//patterns.remove(linePatterns.get(contestant));
						if(linePatterns.containsKey(contestant)&&!patterns.contains(linePatterns.get(contestant))){
							tag=patternTags.remove(linePatterns.get(contestant));
						}
						String interaction = findInteraction(contestant);
						Sid.log(interaction+"\n");
						if(interaction!=null){
							pwr.println(interaction.split("\\|\\|")[2]+"||"+interaction.split("\\|\\|")[3]+"||"+interaction.split("\\|\\|")[4]);
						}
						if(linePatterns.containsKey(contestant)&&!patterns.contains(linePatterns.get(contestant))){
							patternTags.put(linePatterns.get(contestant),tag);
						}
						//patterns.add(linePatterns.get(contestant));
					}

				}
				
				pwr.close();
				br1.close();
				Sid.log("");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	public static void loadStopWords() {
		//stopWord
		BufferedReader brStops;
		try {
			brStops = new BufferedReader(new FileReader("dict/stopFileFox_reduced.txt"));
			while(brStops.ready()){
				MatchInteractions.stopList.add(brStops.readLine().trim().toLowerCase());
			}
			brStops.close();

			brStops = new BufferedReader(new FileReader("dict/auxillaryVerbs.txt"));
			while(brStops.ready()){
				String word=brStops.readLine().trim().toLowerCase();
				if(!MatchInteractions.stopList.contains(word))
					MatchInteractions.stopList.add(word);
			}
			brStops.close();
			brStops = new BufferedReader(new FileReader("dict/conjunctions.txt"));
			while(brStops.ready()){
				String word=brStops.readLine().trim().toLowerCase();
				if(!MatchInteractions.stopList.contains(word))
					MatchInteractions.stopList.add(word);
			}
			brStops.close();
			brStops = new BufferedReader(new FileReader("dict/determiners.txt"));
			while(brStops.ready()){
				String word=brStops.readLine().trim().toLowerCase();
				if(!MatchInteractions.stopList.contains(word))
					MatchInteractions.stopList.add(word);
			}
			brStops.close();
			
			/*brStops = new BufferedReader(new FileReader("dict/prepositions-particles.txt"));
			while(brStops.ready()){
				String word=brStops.readLine().trim().toLowerCase();
				stopList.remove(word);
			}
			brStops.close();
			brStops = new BufferedReader(new FileReader("dict/pronouns.txt"));
			while(brStops.ready()){
				String word=brStops.readLine().trim().toLowerCase();
				stopList.remove(word);
			}
			brStops.close();*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}




	private static void sortConcepts() {
		Iterator<Integer> it=conceptDictionary.keySet().iterator();
		ArrayList<Integer> keys=new ArrayList<Integer>();
		while(it.hasNext())
			keys.add(it.next());
		Collections.sort(keys);
		sortedConcepts = new ArrayList<String>();
		for (Integer key : keys) {
			sortedConcepts.add(conceptDictionary.get(key).split("\\|\\|")[0]);
		}
		conceptDictionary.clear();
	}




	private static String findInteraction(String contestant) {
		int ent1=-1,ent2=-1;
		String[] tokens=contestant.split("\\s");
		int count=0;
		for (String token : tokens) {
			if((token.equals("PROBLEM_")||token.equals("TEST_")||token.equals("TREATMENT_"))&&ent1==-1){
				ent1=count;
			}
			else if((token.equals("PROBLEM_")||token.equals("TEST_")||token.equals("TREATMENT_"))&&ent2==-1){
				ent2=count;
				break;
			}
			if(token.contains("PROBLEM")||token.contains("TEST")||token.contains("TREATMENT")){
				count++;						
			}


		}
		//Sid.log("\t"+contestant);
		contestant=Normalizer.normalizeSentence(contestant).trim();
		Sid.log("\t"+contestant);
		
		/*contestant=contestant.replaceAll("PROBLEM_", "PROBLEM__________________________________________________");
		contestant=contestant.replaceAll("TEST_", "TEST__________________________________________________");
		contestant=contestant.replaceAll("TREATMENT_", "TREATMENT__________________________________________________");
		*/
		Iterator<String> keys= patternTags.keySet().iterator();

		LinkedHashMap<String, Double> results = new LinkedHashMap<String, Double>();
		ArrayList<String> posLabels=new ArrayList<String>();
		if(contestant.contains("TEST_")){
			posLabels.add("TeRP");posLabels.add("TeCP");
		}
		else if(contestant.contains("TREATMENT_")){
			posLabels.add("TrIP");posLabels.add("TrWP");posLabels.add("TrCP");posLabels.add("TrAP");posLabels.add("TrNAP");
		}
		else{
			posLabels.add("PIP");
		}
			

		while(keys.hasNext()){
			String key = keys.next();
			if(!posLabels.contains(patternTags.get(key)))
				continue;
			
			/*key=key.replaceAll("PROBLEM_", "PROBLEM__________________________________________________");
			key=key.replaceAll("TEST_", "TEST__________________________________________________");
			key=key.replaceAll("TREATMENT_", "TREATMENT__________________________________________________");
			*/
			//double score = NeoBio.getNormalizedSWScore(key, contestant);
			double score = SidSmithWaterman.getSidSWscore(key, contestant)/key.split("\\s").length;
			//threshhold
			if(score>=0.85){
				/*key=key.replaceAll("_+", "_");
				contestant=contestant.replaceAll("_+", "_");*/
				/*Sid.log("\tKey Found:"+key);
				Sid.log("\tScore="+score);
				Sid.log("\tClassification="+patternTags.get(key));
				*/
				//Sid.log("\t"+sortedConcepts.get(ent1)+"||r=\""+patternTags.get(key)+"\"||"+sortedConcepts.get(ent2));
				results.put(score+"||"+key+"||"+sortedConcepts.get(ent1)+"||r=\""+patternTags.get(key)+"\"||"+sortedConcepts.get(ent2), score);
				//return sortedConcepts.get(ent1)+"||r=\""+patternTags.get(key)+"\"||"+sortedConcepts.get(ent2);
			}
		}

		if(results.size()>0){
			ArrayList<Entry<String,Double>> as = new ArrayList<Entry<String,Double>>( results.entrySet() );  
			
			Collections.sort( as , new Comparator<Object>() {  
				@SuppressWarnings("rawtypes")
				public int compare( Object o1 , Object o2 )  
				{  
					Map.Entry e1 = (Map.Entry)o1 ;  
					Map.Entry e2 = (Map.Entry)o2 ;  
					Double first = (Double)e1.getValue();  
					Double second = (Double)e2.getValue();  
					return second.compareTo( first );  
				}  
			});  
			
			if(posLabels.contains("PIP"))
				return as.get(0).getKey();
			else if(posLabels.contains("TrIP")){
				if(as.get(0).getValue()==1)
					return as.get(0).getKey();
				double countI=0,countW=0,countC=0,countA=0,countNA=0;
				for (Entry<String, Double> entry : as) {
					if(entry.getKey().contains("||r=\"TrIP\"||"))
						countI++;
					else if(entry.getKey().contains("||r=\"TrWP\"||"))
						countW++;
					else if(entry.getKey().contains("||r=\"TrCP\"||"))
						countC++;
					else if(entry.getKey().contains("||r=\"TrAP\"||"))
						countA++;
					else if(entry.getKey().contains("||r=\"TrIP\"||"))
						countNA++;
				}
				double max = Math.max(Math.max(countI, countW), Math.max(Math.max(countC, countA), countNA));
				if(max>0&&countI==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TrIP"+"\"||"+sortedConcepts.get(ent2);
				if(max>0&&countW==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TrWP"+"\"||"+sortedConcepts.get(ent2);
				if(max>0&&countC==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TrCP"+"\"||"+sortedConcepts.get(ent2);
				if(max>0&&countA==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TrAP"+"\"||"+sortedConcepts.get(ent2);
				if(max>0&&countNA==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TrNAP"+"\"||"+sortedConcepts.get(ent2);

			}
			else if(posLabels.contains("TeRP")){
				if(as.get(0).getValue()==1)
					return as.get(0).getKey();
				double countR=0,countC=0;
				for (Entry<String, Double> entry : as) {
					if(entry.getKey().contains("||r=\"TeRP\"||"))
						countR++;
					else if(entry.getKey().contains("||r=\"TeCP\"||"))
						countC++;
				}
				double max = Math.max(countR, countC);
				if(max>0&&countR==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TeRP"+"\"||"+sortedConcepts.get(ent2);
				if(max>0&&countC==max)
					return max+"||"+"averaged"+"||"+sortedConcepts.get(ent1)+"||r=\""+"TeCP"+"\"||"+sortedConcepts.get(ent2);
			}
		}

		/*Sid.log("\tNO Key Found");
		Sid.log("\tScore="+0);
		Sid.log("\tClassification="+"none");*/
		return null;
	}




	//check back if you need nested entities
	private static ArrayList<String> findContestants(String[] replacedTokens) {
		ArrayList<Integer> problems = new ArrayList<Integer>();
		ArrayList<Integer> tests = new ArrayList<Integer>();
		ArrayList<Integer> treatments = new ArrayList<Integer>();
		ArrayList<Integer> all = new ArrayList<Integer>();

		ArrayList<String> ret = new ArrayList<String>(); 


		//track the numbers
		for(int count=0; count<replacedTokens.length; count++){
			if(!replacedTokens[count].contains("_"))
				continue;
			else if(replacedTokens[count].equals("PROBLEM_")){
				replacedTokens[count]="PROBLEM";
				problems.add(count);
			}
			else if(replacedTokens[count].equals("TEST_")){
				replacedTokens[count]="TEST";
				tests.add(count);
			}
			else if(replacedTokens[count].equals("TREATMENT_")){
				replacedTokens[count]="TREATMENT";
				treatments.add(count);
			}
		}
		all.addAll(problems);all.addAll(tests);all.addAll(treatments);

		if(all.size()<2)
			return ret;

		//for each first entity
		for(int ent1=0; ent1<all.size()-1; ent1++){
			//for every other entity
			for(int ent2=ent1+1; ent2<all.size(); ent2++){
				if(!problems.contains(all.get(ent1))&&!problems.contains(all.get(ent2)))
					continue;
				String[] contestant=replacedTokens.clone();
				contestant[all.get(ent1)]+="_";
				contestant[all.get(ent2)]+="_";

				String contString = "";
				for (String string : contestant) {
					if(string.length()>0)
						contString+=string+" ";
				}
				ret.add(contString.trim());
			}

		}

		return ret;
	}


	private static String[] replaceWithCon(String line, int lineNumber, String txtFile) throws IOException {
		BufferedReader br1 = new BufferedReader(new FileReader("../competition_groundTruth/"+txtFile.substring(0,txtFile.length()-3)+"con"));
		String[] tokens = line.split("\\s");
		while(br1.ready()){
			String concept = br1.readLine();
			Pattern conPattern = Pattern.compile("\\d+:(\\d+) \\d+:(\\d+)");
			Matcher conMatcher = conPattern.matcher(concept);
			int conLineNumber=-1;
			if(conMatcher.find()){
				conLineNumber=Integer.parseInt(conMatcher.group().split(":")[0]);
				if(conLineNumber==lineNumber){
					String classification = (concept.split("\\|\\|t=\"")[1].split("\"")[0].toUpperCase());
					int start=Integer.parseInt(conMatcher.group(1));
					int end=Integer.parseInt(conMatcher.group(2));
					conceptDictionary.put(start, concept);
					tokens[start]=classification+"_";
					for(int i=end; i>start; i--){
						tokens[i]="";
					}
				}
			}
		}
		br1.close();
		String modLine = "";
		for (String token : tokens) {
			if(token.length()>0){
				modLine +=token+" ";
			}
		}
		modLine=modLine.trim();

		if(conceptDictionary.size()>0)
			sortConcepts();

		//Sid.log(modLine);
		return tokens;
	}

}
