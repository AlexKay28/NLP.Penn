package util.parser;
import java.util.ArrayList;

import edu.stanford.nlp.trees.Tree;

import util.Sid;



public class ArraySetList<E> extends ArrayList<E> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean addN(E item){
		if(super.contains(item))
			return false;
		for (E tree : this) {
			if(value((Tree) tree).equals(value((Tree)item)))
				return false;
		}
		Sid.log(value((Tree) item));
		return this.add(item);
		
	}

	public  String value(Tree t) {
		String ret = "";
		for (Tree leaf : t.getLeaves()) {
			if(leaf.nodeString().equals("-LRB-"))
				ret+= "("+" ";
			else if(leaf.nodeString().equals("-RRB-"))
				ret+= ")"+" ";
			else if(leaf.nodeString().equals("-LSB-"))
				ret+= "["+" ";
			else if(leaf.nodeString().equals("-RSB-"))
				ret+= "]"+" ";
			else
				ret+=leaf.nodeString()+" ";
		}
		ret=ret.trim();
		/*if(ret.endsWith(" ."))
			ret= ret.substring(0,ret.length()-1);
		ret=ret.trim();
		*/ret=ret.substring(0,1).toUpperCase()+ret.substring(1);
		if(ret.charAt(ret.length()-1)==',')
			ret=ret.substring(0, ret.length()-1)+".";
		else if(ret.charAt(ret.length()-1)!='.')
			ret+=" .";
		return ret;
	}
}
