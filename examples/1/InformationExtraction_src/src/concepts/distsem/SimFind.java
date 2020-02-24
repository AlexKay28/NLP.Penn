/**
 * @author siddhartha
 */
package concepts.distsem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import pitt.search.semanticvectors.CloseableVectorStore;
import pitt.search.semanticvectors.Flags;
import pitt.search.semanticvectors.LuceneUtils;
import pitt.search.semanticvectors.ObjectVector;
import pitt.search.semanticvectors.SearchResult;
import pitt.search.semanticvectors.VectorSearcher;
import pitt.search.semanticvectors.VectorStoreReader;
import pitt.search.semanticvectors.ZeroVectorException;



/**
 * find similar words
 * @author sjonnalagadda
 *
 */
public class SimFind {
	
	CloseableVectorStore queryVecReader;
    CloseableVectorStore searchVecReader;
    LuceneUtils luceneUtils;
	
	public SimFind(String permtermvectorsbin, String positionalIndex){
		try {
			//queryVecReader = VectorStoreReader.openVectorStore("models/permtermvectors_i2b2Unannotated.bin");
			queryVecReader = VectorStoreReader.openVectorStore(permtermvectorsbin);
			searchVecReader = queryVecReader;
		    //luceneUtils = new LuceneUtils("models/positional_index_i2b2Unannotated");
			luceneUtils = new LuceneUtils(positionalIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	}

	public ArrayList<String> getSimWords(String token,int numResults){
		String[] arguments = {"-queryvectorfile", "", "-searchvectorfile", "", token};
		 // Stage i. Assemble command line options.
	    String[] args = Flags.parseCommandLineFlags(arguments);

	    // This takes the slice of args from argc to end.
	    if (!Flags.matchcase) {
	      for (int i = 0; i < args.length; ++i) {
	        args[i] = args[i].toLowerCase();
	      }
	    }

	    
	    VectorSearcher vecSearcher;
	    LinkedList<SearchResult> results = new LinkedList<SearchResult>();
	   
	    // Stage iii. Perform search according to cosine sum.
	    if (Flags.searchtype.equals("sum")) {
	      // Create VectorSearcher and search for nearest neighbors.
	      try {
	        vecSearcher =
	            new VectorSearcher.VectorSearcherCosine(this.queryVecReader,
	            										this.searchVecReader,
	                                                    this.luceneUtils,
	                                                    args);
	        results = vecSearcher.getNearestNeighbors(numResults);
	      } catch (ZeroVectorException zve) {
	        System.err.println(zve.getMessage());
	        results = new LinkedList<SearchResult>();
	      }
	    } 
	    else {
	        // This shouldn't happen: unrecognized options shouldn't have got past the Flags parsing.
	        System.err.println("Search type unrecognized ...");
	        results = new LinkedList<SearchResult>();
	      }

	    
	    /*
	    // If Flags.searchvectorfile wasn't set, it defaults to Flags.queryvectorfile.
	    if (Flags.searchvectorfile.equals("")) {
	      Flags.searchvectorfile = Flags.queryvectorfile;
	    }

	    LinkedList<SearchResult> results = Search.RunSearch(arguments, 10);
*/		ArrayList<String> ret = new ArrayList<String>();
		if (results.size() > 0) {
			for (int i=ret.size();i<results.size();i++) {
				SearchResult result = results.get(i);
				String relatedToken = ((ObjectVector)result.getObject()).getObject().toString();
				if(relatedToken.equals(token))
					continue;//don't want to repeat
				//float score = result.getScore() ;
				ret.add(relatedToken);
				//System.out.println(score + "\t" + relatedToken);
			}
		}
		
		return ret;
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String token="c5";
		SimFind sFind = new SimFind("models/i2b2_permtermvectors.bin","models/i2b2_positional_index");
		
		
		long start = System.currentTimeMillis();
		
		ArrayList<String> ret = sFind.getSimWords(token, 10);
		for (String string : ret) {
			System.out.println(string);
		}
		
		/*String[] arguments = {"-queryvectorfile", "models/permtermvectors_i2b2Unannotated.bin", "-searchvectorfile", "models/permtermvectors_i2b2Unannotated.bin", token};
		 // Stage i. Assemble command line options.
	    args = Flags.parseCommandLineFlags(arguments);

	    int numResults=10;
	    if (Flags.numsearchresults > 0) 
	    	numResults = Flags.numsearchresults;

	    CloseableVectorStore queryVecReader = VectorStoreReader.openVectorStore(Flags.queryvectorfile);
	    CloseableVectorStore searchVecReader = queryVecReader;
	    LuceneUtils luceneUtils = new LuceneUtils("models/positional_index_i2b2Unannotated");
	    
	    // This takes the slice of args from argc to end.
	    if (!Flags.matchcase) {
	      for (int i = 0; i < args.length; ++i) {
	        args[i] = args[i].toLowerCase();
	      }
	    }

	    
	    VectorSearcher vecSearcher;
	    LinkedList<SearchResult> results = new LinkedList<SearchResult>();
	   
	    // Stage iii. Perform search according to cosine sum.
	    if (Flags.searchtype.equals("sum")) {
	      // Create VectorSearcher and search for nearest neighbors.
	      try {
	        vecSearcher =
	            new VectorSearcher.VectorSearcherCosine(sFind.queryVecReader,
	            										sFind.searchVecReader,
	                                                    sFind.luceneUtils,
	                                                    args);
	        results = vecSearcher.getNearestNeighbors(numResults);
	      } catch (ZeroVectorException zve) {
	        System.err.println(zve.getMessage());
	        results = new LinkedList<SearchResult>();
	      }
	    } 
	    else {
	        // This shouldn't happen: unrecognized options shouldn't have got past the Flags parsing.
	        System.err.println("Search type unrecognized ...");
	        results = new LinkedList<SearchResult>();
	      }

	    //return results;
	    
	    // If Flags.searchvectorfile wasn't set, it defaults to Flags.queryvectorfile.
	    if (Flags.searchvectorfile.equals("")) {
	      Flags.searchvectorfile = Flags.queryvectorfile;
	    }

	    LinkedList<SearchResult> results = Search.RunSearch(arguments, 10);
		ArrayList<String> ret = new ArrayList<String>();
		if (results.size() > 0) {
			for (int i=ret.size();i<results.size();i++) {
				SearchResult result = results.get(i);
				String relatedToken = ((ObjectVector)result.getObject()).getObject().toString();
				if(relatedToken.equals(token))
					continue;//don't want to repeat
				float score = result.getScore() ;
				ret.add(score + "|" + relatedToken);
				System.out.println(score + "\t" + relatedToken);
			}
		}
		*/
		System.out.println("Elapsed time: " + (System.currentTimeMillis() - start));

	}

}
