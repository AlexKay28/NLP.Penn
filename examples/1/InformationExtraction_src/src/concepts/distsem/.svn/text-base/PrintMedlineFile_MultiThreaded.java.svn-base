package concepts.distsem;

import util.Sid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.WebPageFetcher;


class MyThread implements Runnable{

	long pmid;
	String outputDir = "../medlineClinicalTrials/";
	Pattern titlePattern = Pattern.compile("<ArticleTitle>(.+)</ArticleTitle>");
	Pattern abstractPattern = Pattern.compile("<AbstractText>(.+)</AbstractText>");
	
	// assign name to thread
    public MyThread(long pmid2 )
    {
    	this.pmid = pmid2;
    }
	
	@Override
	public void run() {
		try {
			Sid.log(pmid);
			WebPageFetcher ft = new WebPageFetcher("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&retmode=xml&id="+pmid);
			String text = ft.getPageContent();
			Matcher titleMatcher = titlePattern.matcher(text);
			
			PrintWriter pwr = new PrintWriter(new File(outputDir+pmid+".txt"), "UTF-8");
			
			if(titleMatcher.find()){
				Sid.log("title:"+titleMatcher.group(1));
				pwr.println(titleMatcher.group(1));
			}
			Matcher abstractMatcher = abstractPattern.matcher(text);
			if(abstractMatcher.find()){
				Sid.log("abstract:"+abstractMatcher.group(1));
				pwr.println(abstractMatcher.group(1));
			}
			
			pwr.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
}



public class PrintMedlineFile_MultiThreaded {

	
	static String outputDir = "../medlineClinicalTrials/";
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws MalformedURLException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws FileNotFoundException, MalformedURLException, UnsupportedEncodingException {
		Scanner sc = new Scanner(new File("inputs/pubmed_clinincalTrials.txt"), "UTF-8");
		
		
		new File(outputDir).mkdir();
		
		
		while(sc.hasNextLong()){
			long pmid = sc.nextLong();
			MyThread threadn = new MyThread(pmid);
			threadn.run();
		}

	}

}
