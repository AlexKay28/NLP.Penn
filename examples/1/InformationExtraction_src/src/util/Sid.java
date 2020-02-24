package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.SvmLight2FeatureVectorAndLabel;
import cc.mallet.pipe.iterator.SelectiveFileLineIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.Labeling;

import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;



public class Sid {

	/**
	 * Prints <code>obj</code> in new line
	 * @param obj
	 * @throws IOException 
	 */
	public static void log(Object obj){
		/*PrintWriter pwr = new PrintWriter(new FileWriter("Spain_sample_output.txt",true));
		pwr.println(obj);
		pwr.close();*/
		System.out.println(obj);
	}

	public static String removeDoubleQuote(String string) {
		Pattern pattern =
			Pattern.compile("\"+([^\"]*)\"+");
		Matcher matcher = 
			pattern.matcher(string);

		if(matcher.find()){
			return matcher.group(1);
		}

		return string;
	}

	public static String secondWord(String line){
		if(!line.contains("\\s"))
			return null;
		line=line.substring(line.indexOf("\\s")).trim();
		line=line.substring(0, line.indexOf("\\s"));
		return line;
	}


	/**
	 * The method breaks any phrase into simple tokens.
	 * Uses the normal tokenizer
	 * @param line - any phrase
	 * @return an array of individual Strings
	 */
	public static String[] generalTokenize(String line)
	{   
		TokenizerFactory TOKENIZER_FACTORY = new RegExTokenizerFactory("(-|'|\\d|\\p{L})+|\\S");
		char[] cs = line.toCharArray();
		Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(cs,0,cs.length);
		String[] tokens = tokenizer.tokenize();
		return tokens;
	}

	//meant to clean the extracts from google news
	//Source: http://www.trans4mind.com/personal_development/HTMLGuide/specialCharacters.htm
	public static String cleanForNews(String line){
		//dangerous for common use
		//line=line.replaceAll("<[^\\s>]+>", "");

		line=line.replaceAll("&hellip;", "");
		line=line.replaceAll("&#133;", "");

		line=line.replaceAll("&hellip;", "");
		line=line.replaceAll("&#133;", "");

		line=line.replaceAll("&gt;", ">");
		line=line.replaceAll("&#062;", ">");

		line=line.replaceAll("&lt;", "<");
		line=line.replaceAll("&#060;", "<");

		line=line.replaceAll("&amp;", "&");
		line=line.replaceAll("&#038;", "&");

		//long dash
		line=line.replaceAll("&mdash;", "--");
		line=line.replaceAll("&#151;", "--");
		line=line.replaceAll("&#8212;", "--");

		line=line.replaceAll("&#8730;", "");
		line=line.replaceAll("&radic;", "");

		line=line.replaceAll("&#8734;", "");
		line=line.replaceAll("&infin;", "");

		line=line.replaceAll("&nbsp;", " ");
		line=line.replaceAll("&#160;", " ");

		line=line.replaceAll("&#8747;", "");
		line=line.replaceAll("&infin;", "");

		line=line.replaceAll("&amp#8592;", "");
		line=line.replaceAll("&larr;", "");

		line=line.replaceAll("&#8594;", "");
		line=line.replaceAll("&rarr;", "");

		line=line.replaceAll("&#8595;", "?");
		line=line.replaceAll("&darr;", "?");

		line=line.replaceAll("&#169;", "");
		line=line.replaceAll("&copy;", "");

		line=line.replaceAll("&#174;", "");
		line=line.replaceAll("&reg;", "");

		line=line.replaceAll("&#247;", "/");
		line=line.replaceAll("&divide;", "/");

		line=line.replaceAll("&#33;", "!");
		line=line.replaceAll("&#34;", "\"");
		line=line.replaceAll("&#35;", "#");
		line=line.replaceAll("&#36;", "$");
		line=line.replaceAll("&#37;", "%");
		line=line.replaceAll("&#38;", "&");
		line=line.replaceAll("&#39;", "'");
		line=line.replaceAll("&#40;", "(");
		line=line.replaceAll("&#41;", ")");
		line=line.replaceAll("&#42;", "*");
		line=line.replaceAll("&#43;", "+");
		line=line.replaceAll("&#44;", ",");
		line=line.replaceAll("&#45;", "-");
		line=line.replaceAll("&#46;", ".");
		line=line.replaceAll("&#47;", "/");
		line=line.replaceAll("&#48;", "+");

		line=line.replaceAll("&#58;", ":");
		line=line.replaceAll("&#59;", ";");
		line=line.replaceAll("&#61;", "=");
		line=line.replaceAll("&#63;", "?");
		line=line.replaceAll("&#64;", "@");

		line=line.replaceAll("&#91;", "[");
		line=line.replaceAll("&#92;", "\\");
		line=line.replaceAll("&#93;", "]");
		line=line.replaceAll("&#94;", "^");
		line=line.replaceAll("&#95;", "_");
		line=line.replaceAll("&#96;", "`");

		line=line.replaceAll("&#123;", "{");
		line=line.replaceAll("&#124;", "|");
		line=line.replaceAll("&#125;", "}");
		line=line.replaceAll("&#126;", "~");

		//last resort
		line=line.replaceAll("&\\S+;", "");

		//�??
		line=line.replaceAll("�\\?\\?", "");
		return line;

	}


	public static void removeBlankLines(String fileName, String charset){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charset));
			String text="";
			int count=0;
			while(br.ready()){
				Sid.log(++count);
				String line = br.readLine();
				if(!line.equals(""))
					text+=line+"\n";
			}
			text=text.trim();
			Sid.log("replacing now\n\n\n");
			br.close();
			PrintWriter pwr = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8")));
			pwr.print(text);
			pwr.close();			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		//Sid.log(Sid.class);
		//Sid.removeBlankLines("C:/Documents and Settings/sjonnalagadda/Desktop/pubmed_result-4.txt", "UTF-8");
		//Sid.log(cleanForNews("(PressMediaWire) New Haven, Conn.  �?? In response to a Lancet Journal letter suggesting that obese people are significantly  contributing to world oil demands and global food scarcity, Kelly Brownell, director  of the  Rudd Center for Food Policy and Obesity  at Yale University, cautioned  that the data are interesting, but how they are framed will make a big difference."));


		try {
			Pipe trainInstancePipe;
			// Build a new pipe
			ArrayList<Pipe> trainPipeList = new ArrayList<Pipe>();
			trainPipeList.add(new SvmLight2FeatureVectorAndLabel());
			trainInstancePipe = new SerialPipes(trainPipeList);
			InstanceList trainInstances = new InstanceList (trainInstancePipe);
			Reader trainFileReader = new InputStreamReader(new FileInputStream("../../mallet_hg3/sentimentAnalysis/sentimentModel_SVM_light.txt"), "UTF-8");
			
			// Read instances from the file
			trainInstances.addThruPipe (new SelectiveFileLineIterator (trainFileReader, "^\\s*#.+"));
			// Save instances to output file
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("../../mallet_hg3/sentimentAnalysis/sentimentModel_SVM_light_eclipse.mallet"));
			oos.writeObject(trainInstances);
			oos.close();
			
			//train-classifier
			ClassifierTrainer<?> trainer = new MaxEntTrainer();
			Classifier classifier = trainer.train(trainInstances);
			oos = new ObjectOutputStream(new FileOutputStream("../../mallet_hg3/sentimentAnalysis/sentimentModel_MaxEnt.model"));
			oos.writeObject(classifier);
			oos.close();
			
			//run-classifier
			// Write classifications to the output file
			PrintStream out = System.out;
			
			
			//testing begins
		
			
			// Stop growth on the alphabets.
			classifier.getInstancePipe().getDataAlphabet().stopGrowth();
			classifier.getInstancePipe().getTargetAlphabet().stopGrowth();
			
			Pipe testInstancePipe;
			ArrayList<Pipe> testPipeList = new ArrayList<Pipe>();
			testPipeList.add(new SvmLight2FeatureVectorAndLabel());
			testInstancePipe = new SerialPipes(testPipeList);
			InstanceList testInstances = new InstanceList (testInstancePipe);
			Reader testFileReader = new InputStreamReader(new FileInputStream("../../mallet_hg3/sentimentAnalysis/sentimentModel_SVM_light.txt"), "UTF-8");
			
			// Read instances from the file
			testInstances.addThruPipe (new SelectiveFileLineIterator (testFileReader, "^\\s*#.+"));
			Iterator<Instance> iterator = testInstances.iterator();
			while (iterator.hasNext()) {
				Instance instance = iterator.next();
				
				Labeling labeling = 
					classifier.classify(instance).getLabeling();

				StringBuilder output = new StringBuilder();
				
				//output.append(instance.getName()+"\t);

				double max=-1; Label label=null;
				for (int location = 0; location < labeling.numLocations(); location++) {
					if(max<labeling.valueAtLocation(location)){
						max=labeling.valueAtLocation(location);
						label=labeling.labelAtLocation(location);
					}
				}
				output.append(label);
				//output.append("\t" + max);
				out.println(output);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
