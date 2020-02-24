package util.tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokens output by this tokenizer consist of a contiguous block of alphanumeric characters or a single punctuation mark. Note, therefore, that any
 * construction which contains a punctuation mark (such as a contraction or a real number) will necessarily span over at least three tokens.
 * 
 * @author sjonnalagadda
 */
public class BobTokenizer {

	private static boolean isPunctuation(char ch)
    {
        return ("`~!@#$%^&*()-–=_+[]\\{}|;':\",./<>?".indexOf(ch) != -1);
    }
	
	 public List<String> getTokens(String text)
	    {
	        int start = 0;
	        List<String> tokens = new ArrayList<String>();
	        for (int i = 1; i - 1 < text.length(); i++)
	        {
	            char current = text.charAt(i - 1);
	            char next = 0;
	            if (i < text.length())
	                next = text.charAt(i);
	            if (Character.isSpaceChar(current))
	            {
	                start = i;
	            }
	            else if (Character.isLetter(current) || Character.isDigit(current))
	            {
	                if (!Character.isLetter(next) && !Character.isDigit(next))
	                {
	                    tokens.add(text.substring(start, i));
	                    start = i;
	                }
	            }
	            else if (isPunctuation(current))
	            {
	                tokens.add(text.substring(start, i));
	                start = i;
	            }
	        }
	        if (start < text.length())
	            tokens.add(text.substring(start, text.length()));
	        return tokens;
	    }
	 
		public static void main(String[] args){
	    	String text = "var szone=\"edm/news/story\"; var smode=\"\"; var sadpg=\"edm/news/story\";   var sarena=\"arena=_UNDEFINED_VALUE_;\";  var sMAXPOP=1;";
	    	List<String> tokens = new BobTokenizer().getTokens(text);
	    	for (String token : tokens) {
				System.out.println(token);
			}
	    }
	
}
