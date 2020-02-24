/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

package malletExtensions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import cc.mallet.classify.Classifier;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.SvmLight2FeatureVectorAndLabel;
import cc.mallet.pipe.iterator.SelectiveFileLineIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.Labeling;
import cc.mallet.util.CommandOption;
import cc.mallet.util.MalletLogger;

/**
 * Command line tool for classifying a sequence of  
 *  instances directly from text input, without
 *  creating an instance list.
 *  <p>
 * 
 *  @author David Mimno
 *  @author Gregory Druck
 */

public class SvmLight2Classify {

	@SuppressWarnings("unused")
	private static Logger logger = MalletLogger.getLogger(SvmLight2Classify.class.getName());

	static CommandOption.File inputFile =	new CommandOption.File
		(SvmLight2Classify.class, "input", "FILE", true, null,
		 "The file containing data to be classified, one instance per line", null);

	static CommandOption.File outputFile = new CommandOption.File
		(SvmLight2Classify.class, "output", "FILE", true, new File("text.vectors"),
		 "Write the instance list to this file; Using - indicates stdout.", null);

	static CommandOption.String lineRegex = new CommandOption.String
		(SvmLight2Classify.class, "line-regex", "REGEX", true, "^(\\S*)[\\s,]*(.*)$",
		 "Regular expression containing regex-groups for label, name and data.", null);
	
	static CommandOption.Integer nameOption = new CommandOption.Integer
		(SvmLight2Classify.class, "name", "INTEGER", true, 1,
		 "The index of the group containing the instance name.\n" +
         "   Use 0 to indicate that the name field is not used.", null);

	static CommandOption.Integer dataOption = new CommandOption.Integer
		(SvmLight2Classify.class, "data", "INTEGER", true, 2,
		 "The index of the group containing the data.", null);
	
	static CommandOption.File classifierFile = new CommandOption.File
		(SvmLight2Classify.class, "classifier", "FILE", true, new File("classifier"),
		 "Use the pipe and alphabets from a previously created vectors file.\n" +
		 "   Allows the creation, for example, of a test set of vectors that are\n" +
		 "   compatible with a previously created set of training vectors", null);

	static CommandOption.String encoding = new CommandOption.String
		(SvmLight2Classify.class, "encoding", "STRING", true, Charset.defaultCharset().displayName(),
		 "Character encoding for input file", null);

	public static void main (String[] args) throws FileNotFoundException, IOException {

		// Process the command-line options
		CommandOption.setSummary (SvmLight2Classify.class,
								  "A tool for classifying a stream of unlabeled instances");
		CommandOption.process (SvmLight2Classify.class, args);
		
		// Print some helpful messages for error cases
		if (args.length == 0) {
			CommandOption.getList(SvmLight2Classify.class).printUsage(false);
			System.exit (-1);
		}
		if (inputFile == null) {
			throw new IllegalArgumentException ("You must include `--input FILE ...' in order to specify a"+
								"file containing the instances, one per line.");
		}
		
	  // Read classifier from file
		Classifier classifier = null;
		try {
			ObjectInputStream ois =
				new ObjectInputStream (new BufferedInputStream(new FileInputStream (classifierFile.value)));
			
			classifier = (Classifier) ois.readObject();
			ois.close();
		} catch (Exception e) {
			throw new IllegalArgumentException("Problem loading classifier from file " + classifierFile.value +
							   ": " + e.getMessage());
		}
		
		// Read instances from the file
		/*Reader fileReader;
		if (inputFile.value.toString().equals ("-")) {
		    fileReader = new InputStreamReader (System.in);
		}
		else {
			fileReader = new InputStreamReader(new FileInputStream(inputFile.value), encoding.value);
		}
		Iterator<Instance> csvIterator = 
			new CsvIterator (fileReader, Pattern.compile(lineRegex.value),
							 dataOption.value, 0, nameOption.value);
		Iterator<Instance> iterator = 
			classifier.getInstancePipe().newIteratorFrom(csvIterator);*/
        Pipe instancePipe;
        
        // Build a new pipe
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
        pipeList.add(new SvmLight2FeatureVectorAndLabel());
        instancePipe = new SerialPipes(pipeList);

        InstanceList instances = new InstanceList (instancePipe);
        Reader fileReader;
        if (inputFile.equals ("-")) {

            fileReader = new InputStreamReader (System.in);
        }
        else {
            fileReader = new InputStreamReader(new FileInputStream(inputFile.value), encoding.value);

        }
       
        // Read instances from the file
        instances.addThruPipe (new SelectiveFileLineIterator (fileReader, "^\\s*#.+"));
       
        Iterator<Instance> iterator = instances.iterator();
		
	
		// Write classifications to the output file
		PrintStream out = null;

		if (outputFile.value.toString().equals ("-")) {
			out = System.out;
		}
		else {
			out = new PrintStream(outputFile.value, encoding.value);
		}
		
		//System.out.println(classifier.getInstancePipe().getDataAlphabet().size());
		
		// gdruck@cs.umass.edu
		// Stop growth on the alphabets. If this is not done and new
		// features are added, the feature and classifier parameter
		// indices will not match.  
		classifier.getInstancePipe().getDataAlphabet().stopGrowth();
		classifier.getInstancePipe().getTargetAlphabet().stopGrowth();
		
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
		
		if (! outputFile.value.toString().equals ("-")) {
			out.close();
		}
	}
}

    

