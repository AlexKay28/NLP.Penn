package concepts.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;


public class BootStrapResamplingRunner {

	static PrintStream sysBackUp = System.out;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		int numRuns=10000;
		String reference = "/home/siddhartha/currentProjects/IE/competition_groundTruth/"; String[] referenceFiles = new File(reference).list();
		//String system1 = "/home/siddhartha/currentProjects/IE/IE_java/outputs/competition_con_baseline/"; 
		String system1 = "/home/siddhartha/currentProjects/IE/IE_java/outputs/competition_con_close_clinical_10/";
		//String system2 = "/home/siddhartha/currentProjects/IE/IE_java/outputs/competition_con_close_local_10_clinical_20_2000d_thesarus/";
		String system2 = "/home/siddhartha/currentProjects/IE/IE_java/outputs/competition_con_close_local_clinical_10/";
		int numReferenceFiles = new File(reference).list().length;
		int system2Wins=0, system2Loses=0;
		
		Runtime rt = Runtime.getRuntime();
		
		String bsr_reference = "bsr_refernce/";
		String bsr_system1 = "bsr_system1/";
		String bsr_system2 = "bsr_system2/";
		deleteDirectory(new File(bsr_reference));deleteDirectory(new File(bsr_system1));deleteDirectory(new File(bsr_system2));
		
		for(int count=0; count<numRuns; count++){
			log("\n\nIteration:\t\t"+count);
			
			float acc_1=-1;float acc_2=-1;
			
			new File(bsr_reference).mkdirs(); new File(bsr_system1).mkdirs(); new File(bsr_system2).mkdirs();
			
			for(int fileCount=0; fileCount< numReferenceFiles; fileCount++){
				int randomFileNum = (int)(Math.random()*numReferenceFiles);
				//log(randomFileNum);
				copyFile(new File(reference+referenceFiles[randomFileNum]), new File(bsr_reference+fileCount+"_"+referenceFiles[randomFileNum]));
				copyFile(new File(system1+referenceFiles[randomFileNum]), new File(bsr_system1+fileCount+"_"+referenceFiles[randomFileNum]));
				copyFile(new File(system2+referenceFiles[randomFileNum]), new File(bsr_system2+fileCount+"_"+referenceFiles[randomFileNum]));
			}
			
			Process pr = rt.exec("java -jar lib/i2b2va-eval.jar -rcp "+bsr_reference+" -scp "+bsr_system1+" -ft con");
	        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	        String line=null;
	        while((line=input.readLine()) != null) {
	        	if(line.startsWith("Class Exact Span")){
					String[] tokens=line.split("\t");
					acc_1=Float.parseFloat(tokens[tokens.length-1]);
					log("accuracy1=\t"+acc_1);
					break;					
				}
	        }
	        int exitVal = pr.waitFor();input.close();
	        //System.out.println("Exited with error code "+exitVal);

	        pr = rt.exec("java -jar lib/i2b2va-eval.jar -rcp "+bsr_reference+" -scp "+bsr_system2+" -ft con");
	        input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	        line=null;
	        while((line=input.readLine()) != null) {
	        	if(line.startsWith("Class Exact Span")){
					String[] tokens=line.split("\t");
					acc_2=Float.parseFloat(tokens[tokens.length-1]);
					log("accuracy2=\t"+acc_2);
					break;					
				}
	        }
	        exitVal = pr.waitFor(); input.close();
	        //System.out.println("Exited with error code "+exitVal);

	     
			if(acc_2>acc_1)
				system2Wins++;
			else
				system2Loses++;
			log("system2Wins="+system2Wins+"\tsystem2Loses="+system2Loses);
			deleteDirectory(new File(bsr_reference));deleteDirectory(new File(bsr_system1));deleteDirectory(new File(bsr_system2));
			
		}

		log("system2Wins="+system2Wins+"\tsystem2Loses="+system2Loses);
		
	}


	private static void log(Object obj) {
		PrintStream backup = System.out;
		System.setOut(sysBackUp);
		System.out.println(obj);
		System.setOut(backup);
	}

	public static void copyFile(File in, File out) throws Exception {
		FileInputStream fis  = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} 
		catch (Exception e) {
			throw e;
		}
		finally {
			if (fis != null) fis.close();
			if (fos != null) fos.close();
		}
	}

	public static boolean deleteDirectory(File path) {
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0;  i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return( path.delete() );
	}

}
