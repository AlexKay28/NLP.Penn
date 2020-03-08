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

public class PrintMedlineFile {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws MalformedURLException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws FileNotFoundException, MalformedURLException, UnsupportedEncodingException {
		Scanner sc = new Scanner(new File("inputs/pubmed_clinincalTrials.txt"), "UTF-8");
		
		String outputDir = "../medlineClinicalTrials/";
		new File(outputDir).mkdir();
		
		Pattern titlePattern = Pattern.compile("<ArticleTitle>(.+)</ArticleTitle>");
		Pattern abstractPattern = Pattern.compile("<AbstractText>(.+)</AbstractText>");
		
		while(sc.hasNextLong()){
			long pmid = sc.nextLong();
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
		}

	}

}
