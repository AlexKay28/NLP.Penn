package util;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;



/**
 * gets everything we need from WWW
 * @author siddhartha
 *
 */
public final class WebPageFetcher {

	// PRIVATE //
	private URL fURL;
	private long DEF_SLEEP_TIME = 1000; //1 seconds sleep time

	private static final String HTTP = "http";
	private static final String END_OF_INPUT = "\\Z";
	private static final String NEWLINE = System.getProperty("line.separator");


	/**
	 * Constructor taking the URL as the input
	 * @param aURL
	 */
	public WebPageFetcher( URL aURL ){
		if ( ! HTTP.equals(aURL.getProtocol())  ) {
			//throw new IllegalArgumentException("URL is not for HTTP Protocol: " + aURL);
		}
		fURL = aURL;
	}

	/**
	 * Constructor taking the String URL as the input
	 * @param aUrlName
	 * @throws MalformedURLException
	 */
	public WebPageFetcher( String aUrlName ) throws MalformedURLException {
		this ( new URL(aUrlName) );
	}



	/**
	 * Just for testing purposes
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException{
		/*try { // Construct data 
			String data = URLEncoder.encode("In our study, the frequency of struvite, calcium oxalate, cystine, urates, mixed and compounds stones are in agreement with papers that report on dog populations in America and Europe, but a higher frequency of silica uroliths was observed in Mexico City dogs.", "UTF-8");
			
			// Send data 
			//URL url = new URL("http://www.ebi.ac.uk:80/Rebholz-srv/MeshUP/GetMeSHAnnotation"); 
			URL url = new URL("http://www.ebi.ac.uk/Rebholz-srv/MeshUP/");
			URLConnection conn = url.openConnection(); 
			conn.setDoOutput(true); 
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
			wr.write(data); 
			wr.flush(); 
			// Get the response 
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
			String line; 

			while ((line = rd.readLine()) != null) { // Process line... 
				Sid.log(line);
			} 
			wr.close(); 
			rd.close(); 
			
		}
		catch (Exception e) { 
			Sid.log("error");
			e.printStackTrace();
		} 

*/
		System.setProperty("http.proxyHost", "easydown.info");
		System.setProperty("http.proxyPort", "8080");
		WebPageFetcher fetcher = new  WebPageFetcher("http://www.public.asu.edu/~sjonnal3/proxieTest.html");
		String result = fetcher.getPageContent(0, 10,"UTF-8");
		Sid.log(result);
	}


	/**
	 * Fetches the HTML content of the page as simple text.	
	 * @param attempt
	 * @param maxAttempts - time between each attempt is 1 sec
	 * @return
	 */
	public String getPageContent(int attempt, int maxAttempts,  String charsetName) {
		String result = null;
		URLConnection connection = null;
		try {
			connection =  fURL.openConnection();
			Scanner scanner = new Scanner(connection.getInputStream(),charsetName);
			scanner.useDelimiter(END_OF_INPUT);
			result = scanner.next();
		}
		catch ( Exception ex ) {
			if(attempt<maxAttempts){
				try {
					Thread.sleep(DEF_SLEEP_TIME);
				} catch (InterruptedException e) {
					;
				}
				System.err.println("attempt#"+ ++attempt);
				return getPageContent(attempt,maxAttempts,charsetName);

			}				
			log("Cannot open connection to " + fURL.toString());
			return "";
		}
		return result;
	}


	/**
	 * Fetches the HTML content of the page as simple text.
	 */
	public String getPageContent() {
		String result = null;
		URLConnection connection = null;
		try {
			connection =  fURL.openConnection();
			Scanner scanner = new Scanner(connection.getInputStream(),"ISO-8859-15");
			scanner.useDelimiter(END_OF_INPUT);
			result = scanner.next();
		}
		catch ( IOException ex ) {
			log("Cannot open connection to " + fURL.toString());
		}
		return result;
	}


	/**
	 * Fetches HTML headers as simple text.
	 */
	public String getPageHeader(){
		StringBuilder result = new StringBuilder();

		URLConnection connection = null;
		try {
			connection = fURL.openConnection();
		}
		catch (IOException ex) {
			log("Cannot open connection to URL: " + fURL);
		}

		//not all headers come in key-value pairs - sometimes the key is
		//null or an empty String
		int headerIdx = 0;
		String headerKey = null;
		String headerValue = null;
		while ( (headerValue = connection.getHeaderField(headerIdx)) != null ) {
			headerKey = connection.getHeaderFieldKey(headerIdx);
			if ( headerKey != null && headerKey.length()>0 ) {
				result.append( headerKey );
				result.append(" : ");
			}
			result.append( headerValue );
			result.append(NEWLINE);
			headerIdx++;
		}
		return result.toString();
	}


	/**
	 * returns if the string is a last name<p>
	 * "askWhitePagesLastName.txt" is our database which we search before 
	 * sending in www<p>
	 * @param search
	 * @return True/False
	 * @throws MalformedURLException 
	 */
	public static boolean askWhitePagesLastName(String search) throws MalformedURLException{
		
		try {
			BufferedReader br1 = new BufferedReader(new FileReader("dict/askWhitePagesLastName.txt"));
			while(br1.ready())
				if(br1.readLine().trim().equalsIgnoreCase(search.trim()))
					return true;
			br1.close();
		} catch (FileNotFoundException e1) {
			
			e1.printStackTrace();
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		
		WebPageFetcher fetcher = new  WebPageFetcher("http://www.ancestry.com/facts/"+search+"-name-meaning.ashx");
		String result = fetcher.getPageContent();
		
		if(result==null)
			return false;
		
		PrintWriter askWhitePagesLastName = null;
		try {
			askWhitePagesLastName = new PrintWriter(new FileWriter("dict/askWhitePagesLastName.txt",true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!result.contains("Sorry, we couldn't find"))
			askWhitePagesLastName.println(search);
		askWhitePagesLastName.close();
		
		return !result.contains("Sorry, we couldn't find");
	}
	

	private static void log(Object aObject){
		System.out.println(aObject);
	}
} 


