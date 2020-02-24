package util.parser;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import util.Sid;
import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.MemoryTreebank;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeNormalizer;
import edu.stanford.nlp.trees.Treebank;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.Filters;
import edu.stanford.nlp.util.StringUtils;

/**
 * This class implements the <code>TreeReader</code> interface to read Penn Treebank-style 
 * files. The reader is implemented as a pushdown automaton (PDA) that parses the Lisp-style 
 * format in which the trees are stored. This reader is compatible with both PTB
 * and PATB trees. 
 *
 * @author Christopher Manning
 * @author Roger Levy
 * @author Spence Green
 */
public class MyPennTreeReader  {

	/**
	 * Loads treebank data from first argument and prints it.
	 *
	 * @param args Array of command-line arguments: specifies a filename
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Treebank tb = new MemoryTreebank(new TreeNormalizer());
		tb.loadPath(args[0]);
		
		PrintWriter pwr = new PrintWriter(new FileWriter(args[1],false));
		pwr.close();
		
		Collection<GrammaticalStructure> gsBank = new TreeBankGrammaticalStructureWrapper(tb, false);
		for (GrammaticalStructure gs : gsBank) {
			pwr = new PrintWriter(new FileWriter(args[1],true));
			
			Tree t;
			if (gsBank instanceof TreeBankGrammaticalStructureWrapper) {
				t = ((TreeBankGrammaticalStructureWrapper)gsBank).getOriginalTree(gs);
			} else {
				t = gs.root(); // recover tree
			}
			Sid.log("\n\n"+t.getLeaves());
			t.pennPrint();
			ArraySetList<Tree> simpTrees = simplify(t);
			Sid.log(simpTrees);
			
			pwr.println(simpTrees.value(t));
			for (Tree tree : simpTrees) {
				pwr.println("\t"+simpTrees.value(tree));
			}
			//printDependencies(gs.typedDependenciesCCprocessed(true), t, false);
			pwr.close();
		}
		
	}

	public static ArraySetList<Tree> simplify(Tree tree) {
		
		ArraySetList<Tree> simpTrees = new ArraySetList<Tree>();
		simpTrees.addN(tree);simpTrees.remove(tree);
		
		
		Tree repT = copy(tree);
		//necessary changes
		for (Tree subTree : repT.subTreeList()) {
			
			//rule 9: S* ~ {S*}
			if(subTree.nodeString().equals("S")
					&& subTree.children().length>=2
					&& subTree.children()[0].nodeString().equals("NP")
					&& containsPhrase(subTree,"VP")
					&& subTree.getLeaves().size()<repT.getLeaves().size()
			){
				Tree ct1 = copy(subTree);
				Sid.log(ct1.getLeaves()); 
				simpTrees.addN(ct1);
			}

			/*//rule 16: NP|[NP1 , NP2 ,?] ~ [NPmin] {NP1 can be NP2}
			if(subTree.nodeString().equals("NP") 
					&& subTree.children().length>=3
					&& subTree.children()[0].nodeString().equals("NP")
					&& subTree.children()[1].nodeString().equals(",")
					&& subTree.children()[2].nodeString().equals("NP")
			){
				
				String np1Penn = subTree.firstChild().pennString();
				String np2Penn = subTree.children()[2].pennString();
				Tree ct1 = addModal(np1Penn,np2Penn);
				//Sid.log(ct1.getLeaves()); 
				simpTrees.addN(ct1);
				
				int max = 0;
				if(subTree.children()[2].numChildren()>subTree.children()[0].numChildren())
					max=1;
				subTree.removeChild(max);subTree.removeChild(max); 
				if(subTree.children().length>1 && subTree.children()[1].nodeString().equals(","))
					subTree.removeChild(1); 
				
			}*/

			//rule 19: NP|[NP VP+] ~ [NP] {NP "can be" VP+}
			//VP+ starts with a VBG not VBD - VBD is past tense. found an error like that 
			if(subTree.nodeString().equals("NP") 
					&& subTree.children().length>=2
					&& subTree.children()[0].nodeString().equals("NP")
					&& subTree.children()[1].nodeString().equals("VP")
					&& (subTree.children()[1].firstChild().nodeString().equals("VBG") || subTree.children()[1].firstChild().nodeString().equals("VBN"))
			){
				Tree ct1 = addModal(subTree);
				//Sid.log(ct1.getLeaves()); 
				simpTrees.addN(ct1);
				
				subTree.removeChild(1);
			}
			
			//rule 19': NP|[NP , VP] ~ [NP] {NP "can be" VP}
			if(subTree.nodeString().equals("NP") 
					&& subTree.children().length>=3
					&& subTree.children()[0].nodeString().equals("NP")
					&& subTree.children()[1].nodeString().equals(",")
					&& subTree.children()[2].nodeString().equals("VP")
			){
				String npPenn = subTree.firstChild().pennString();
				String vpPenn = subTree.children()[2].pennString();
				Tree ct1 = addModal(npPenn,vpPenn);
				simpTrees.addN(ct1);
				
				if(subTree.children().length>=4&&subTree.children()[3].nodeString().equals(","))
					subTree.removeChild(3);//remove comma at the end
				subTree.removeChild(1); subTree.removeChild(1);
			}
			
			//rule 21: NP|[NP ADJP] ~ [NP], {NP can be ADJP}
			if(subTree.nodeString().equals("NP") 
					&& subTree.children().length>=2
					&& subTree.children()[0].nodeString().equals("NP")
					&& subTree.children()[1].nodeString().equals("ADJP")
					&& subTree.children()[1].firstChild().nodeString().equals("JJ")
			){
				String npPenn = subTree.firstChild().pennString();
				String adjPenn = subTree.children()[1].pennString();
				Tree ct1 = addModal(npPenn,adjPenn);
				//Sid.log(ct1.getLeaves()); 
				simpTrees.addN(ct1);
				
				subTree.removeChild(1);
			}
			
			//rule 21': NP|[NP , ADJP] ~ [NP], {NP can be ADJP}
			if(subTree.nodeString().equals("NP") 
					&& subTree.children().length>=3
					&& subTree.children()[0].nodeString().equals("NP")
					&& subTree.children()[1].nodeString().equals(",")
					&& subTree.children()[2].nodeString().equals("ADJP")
					//&& children[1].firstChild().nodeString().equals("JJ"):Pierre, Vinken, ,, 61, years, old, ,,
			){
				String npPenn = subTree.firstChild().pennString();
				String adjPenn = subTree.children()[2].pennString();
				Tree ct1 = addModal(npPenn,adjPenn);
				//Sid.log(ct1.getLeaves()); 
				simpTrees.addN(ct1);
				
				if(subTree.children().length>=4&&subTree.children()[3].nodeString().equals(","))
					subTree.removeChild(3);//remove comma at the end
				subTree.removeChild(1);subTree.removeChild(1);
			}
			
			//rule 29: NP|[* PRN] ~ [PRN - LRB - RRB]
			if(subTree.nodeString().equals("NP") 
					&& subTree.children().length>=2
					//&& subTree.children()[0].nodeString().equals("NP")
					&& subTree.children()[1].nodeString().equals("PRN")
			){
				subTree.removeChild(1); 
			}
			
			//rule 30: NP|[NP : S*] ~ [S*]
			if(subTree.nodeString().equals("NP") 
					&& subTree.children().length>=3
					&& subTree.children()[0].nodeString().equals("NP")
					&& subTree.children()[1].nodeString().equals(":")
					&& subTree.children()[2].nodeString().equals("S")
					&& containsPhrase(subTree.children()[2], "NP") && containsPhrase(subTree.children()[2], "VP")
			){
				subTree.removeChild(0); subTree.removeChild(0);  
			}
		}
		simpTrees.addN(repT);
		repT = copy(repT);//important - not replicating
		
		//tackle abbreviations
		for (Tree subTree : repT.subTreeList()) {
			//rule 29: NP|[NP1 PRN] ~ [NP1]
			if(subTree.nodeString().equals("NP") 
					&& subTree.children().length>=2
					//&& subTree.children()[0].nodeString().equals("NP")
					&& subTree.children()[1].nodeString().equals("PRN")
			){
				subTree.removeChild(0); 
				subTree.children()[0].removeChild(0); subTree.children()[0].removeChild(subTree.children()[0].numChildren()-1);
			}
		}
		
		for (Tree subTree : repT.subTreeList()) {
			Tree[] children = subTree.children();
			
			//rule 4: NP|[NP PP] ~ [NP]
			//Not so important - just increases the number of rules
			if(subTree.nodeNumber(repT)!=-1 && subTree.nodeString().equals("NP") 
					&& children.length>=2
					&& children[0].nodeString().equals("NP")
					&& children[1].nodeString().equals("PP")
			){
				int nodeNum = subTree.nodeNumber(repT);
				repT.getNodeNumber(nodeNum).removeChild(1);
			}
			
			
			
			//rule 2: S|[PP , S*1] ~ [S*1]
			//moved over to replacement section
			if(subTree.nodeNumber(repT)!=-1 && (subTree.nodeString().equals("S")||subTree.nodeString().equals("S1")) 
					&& children.length>=3
					&& children[0].nodeString().equals("PP")
					&& children[1].nodeString().equals(",")
					&& children[2].nodeString().equals("S")
					&& containsPhrase(children[2],"NP") && containsPhrase(children[2],"VP")
			){
				int nodeNum = subTree.nodeNumber(repT);
				repT.getNodeNumber(nodeNum).removeChild(0); repT.getNodeNumber(nodeNum).removeChild(0);
			}
			
			//rule 5: S|[PP|ADVP , NP VP .?] ~ [NP VP .?]
			//moved up
			if(subTree.nodeNumber(repT)!=-1 && (subTree.nodeString().equals("S") || subTree.nodeString().equals("S1")) 
					&& children.length>=4
					&& (children[0].nodeString().equals("PP") || children[0].nodeString().equals("ADVP"))
					&& children[1].nodeString().equals(",")
					&& children[2].nodeString().equals("NP")
					&& children[3].nodeString().equals("VP")
			){
				int nodeNum = subTree.nodeNumber(repT);
				repT.getNodeNumber(nodeNum).removeChild(0); repT.getNodeNumber(nodeNum).removeChild(0);
			}
			
			//rule 5': S|[PP|ADVP NP VP .?] ~ [NP VP .?]
			if(subTree.nodeNumber(repT)!=-1 && (subTree.nodeString().equals("S") || subTree.nodeString().equals("S1")) 
					&& children.length>=3
					&& (children[0].nodeString().equals("PP") || children[0].nodeString().equals("ADVP"))
					&& children[1].nodeString().equals("NP")
					&& children[2].nodeString().equals("VP")
			){
				int nodeNum = subTree.nodeNumber(repT);
				repT.getNodeNumber(nodeNum).removeChild(0); 
			}
			
			//rule 11: S|[NP ADVP VP] ~ [NP VP]
			//moved up
			if(subTree.nodeNumber(repT)!=-1 && subTree.nodeString().equals("S") 
					&& children.length>=3
					&& children[0].nodeString().equals("NP")
					&& (children[1].nodeString().equals("ADVP") || children[1].nodeString().equals("ADJP"))
					&& children[2].nodeString().equals("VP")
			){
				int nodeNum = subTree.nodeNumber(repT);
				repT.getNodeNumber(nodeNum).removeChild(1);
			}

			//rule 12: VP|[VBN PP] ~ [VBN]
			//not so important
			if(subTree.nodeNumber(repT)!=-1 && subTree.nodeString().equals("VP") 
					&& children.length>=2
					&& children[0].nodeString().equals("VBN")
					&& children[1].nodeString().equals("PP")
			){
				int nodeNum = subTree.nodeNumber(repT);
				repT.getNodeNumber(nodeNum).removeChild(1);
			}
			
			//rule 14: VP|[MD VP , S+] ~ [MD VP]
			//move up
			if(subTree.nodeNumber(repT)!=-1 && subTree.nodeString().equals("VP") 
					&& children.length>=4
					&& children[0].nodeString().equals("MD")
					&& children[1].nodeString().equals("VP")
					&& children[2].nodeString().equals(",")
					&& children[3].nodeString().equals("S")
					&& containsPhrase(children[3],"VP") && !containsPhrase(children[3],"NP")
			){
				int nodeNum = subTree.nodeNumber(repT);
				repT.getNodeNumber(nodeNum).removeChild(2); repT.getNodeNumber(nodeNum).removeChild(2);
			}
			
			//rule 15: VP|[VBP|VBZ NP , S+|PP] ~ [VBP VP]
			//move up
			if(subTree.nodeNumber(repT)!=-1 && subTree.nodeString().equals("VP") 
					&& children.length>=4
					&& (children[0].nodeString().equals("VBP") || children[0].nodeString().equals("VBZ"))
					&& children[1].nodeString().equals("NP")
					&& children[2].nodeString().equals(",")
					&& ((children[3].nodeString().equals("S")&& !(containsPhrase(children[3],"VP") && containsPhrase(children[3],"NP"))) 
							|| children[3].nodeString().equals("PP"))
			){
				int nodeNum = subTree.nodeNumber(repT);
				repT.getNodeNumber(nodeNum).removeChild(2); repT.getNodeNumber(nodeNum).removeChild(2);
			}
			
			
			//rule 17: NP|[PRP VBN NN] ~ [PRP NN]
			//move up
			if(subTree.nodeNumber(repT)!=-1 && subTree.nodeString().equals("NP") 
					&& children.length>=3
					&& children[0].nodeString().startsWith("PRP") //note the point
					&& children[1].nodeString().equals("VBN")
					&& children[2].nodeString().equals("NN")
			){
				int nodeNum = subTree.nodeNumber(repT);
				repT.getNodeNumber(nodeNum).removeChild(1);
			}
			
			//rule 18: VP|[VBN|VBZ|VBD ADVP ...] ~ [VBN ...]
			//move up
			if(subTree.nodeNumber(repT)!=-1 && subTree.nodeString().equals("VP") 
					&& children.length>=3
					&& (children[0].nodeString().equals("VBN") || children[0].nodeString().equals("VBZ") || children[0].nodeString().equals("VBD"))
					&& children[1].nodeString().equals("ADVP") 
					
			){
				int nodeNum = subTree.nodeNumber(repT);
				repT.getNodeNumber(nodeNum).removeChild(1);
			}
			
			//rule 20: PP|[ADVP IN ...] ~ [IN ...]
			//move up
			if(subTree.nodeNumber(repT)!=-1 && subTree.nodeString().equals("PP") 
					&& children.length>=3
					&& children[0].nodeString().equals("ADVP")
					&& children[1].nodeString().equals("IN")
			){
				int nodeNum = subTree.nodeNumber(repT);
				repT.getNodeNumber(nodeNum).removeChild(0);
			}
			
			//rule 23: NP|[ADJP ...] ~ [...]
			//move up
			if(subTree.nodeNumber(repT)!=-1 && subTree.nodeString().equals("NP") 
					&& children.length>=2
					&& children[0].nodeString().equals("ADJP")
			){
				int nodeNum = subTree.nodeNumber(repT);
				repT.getNodeNumber(nodeNum).removeChild(0);
			}
		}
		simpTrees.addN(repT);
		
		for(int count=0; count<simpTrees.size(); count++){
			Tree t = simpTrees.get(count);
			if(!t.nodeString().equals("S")&&!t.nodeString().equals("S1"))
				continue;
			for (Tree subTree : t.subTreeList()) {
				

				//rule 30: NP|[NP : S*] ~ [S*]
				//only replace rule
				if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=3
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals(":")
						&& subTree.children()[2].nodeString().equals("S")
						&& (containsPhrase(subTree.children()[2], "NP") || containsPhrase(subTree.children()[2], "VP"))
				){
					subTree.removeChild(0); subTree.removeChild(0);  
				}
				
				
				
				//Tree[] children = subTree.children();
				
				//rule 1: S|[S*1 , CC S*2] ~ [S1] [S2]
				//anyways  S1 and S2 will automatically get separated
				/*if((subTree.nodeString().equals("S")||subTree.nodeString().equals("S1")) 
						&& children.length>=4
						&& children[0].nodeString().equals("S")
						&& containsPhrase(children[0],"NP") && containsPhrase(children[0],"VP")
						&& children[1].nodeString().equals(",")
						&& children[2].nodeString().equals("CC")
						&& children[3].nodeString().equals("S")
						&& containsPhrase(children[3],"NP") && containsPhrase(children[3],"VP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1); ct.getNodeNumber(nodeNum).removeChild(1); ct.getNodeNumber(nodeNum).removeChild(1);
					Sid.log(ct.getLeaves());
					simpTrees.add(ct);
					ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0);
					//Sid.log(ct.getLeaves());
					simpTrees.add(ct);
				}*/
				
				/*//rule 2: S|[PP , S*1] ~ [S*1]
				//moved over to replacement section
				if((subTree.nodeString().equals("S")||subTree.nodeString().equals("S1")) 
						&& children.length>=3
						&& children[0].nodeString().equals("PP")
						&& children[1].nodeString().equals(",")
						&& children[2].nodeString().equals("S")
						&& containsPhrase(children[2],"NP") && containsPhrase(children[2],"VP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0);
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}
				*/
				
				//rule 3: S|[S+1 , NP VP] ~ [NP VP]
				if((subTree.nodeString().equals("S")||subTree.nodeString().equals("S1")) 
						&& subTree.children().length>=4
						&& subTree.children()[0].nodeString().equals("S")
						&& !(containsPhrase(subTree.children()[0],"NP") && containsPhrase(subTree.children()[0],"VP"))
						&& subTree.children()[1].nodeString().equals(",")
						&& subTree.children()[2].nodeString().equals("NP")
						&& subTree.children()[3].nodeString().equals("VP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0); 
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);				
				}
				
				//rule 4: NP|[NP PP] ~ [NP]
				//Not so important - just increases the number of rules
				/*if((subTree.nodeString().equals("NP")) 
						&& children.length>=2
						&& children[0].nodeString().equals("NP")
						&& children[1].nodeString().equals("PP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}*/
				
				
				//rule 4: NP|[NP , PP] ~ [NP]
				//can be important at times
				if((subTree.nodeString().equals("NP")) 
						&& subTree.children().length>=3
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals(",")
						&& subTree.children()[2].nodeString().equals("PP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1);ct.getNodeNumber(nodeNum).removeChild(1);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}
				
				/*//rule 5: S|[PP|ADVP , NP VP .?] ~ [NP VP .?]
				//moved up
				if((subTree.nodeString().equals("S") || subTree.nodeString().equals("S1")) 
						&& children.length>=4
						&& (children[0].nodeString().equals("PP") || children[0].nodeString().equals("ADVP"))
						&& children[1].nodeString().equals(",")
						&& children[2].nodeString().equals("NP")
						&& children[3].nodeString().equals("VP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}
				
				//rule 5': S|[PP|ADVP NP VP .?] ~ [NP VP .?]
				if((subTree.nodeString().equals("S") || subTree.nodeString().equals("S1")) 
						&& children.length>=3
						&& (children[0].nodeString().equals("PP") || children[0].nodeString().equals("ADVP"))
						&& children[1].nodeString().equals("NP")
						&& children[2].nodeString().equals("VP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); 
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}
				*/
				//rule 6: NP|[NP  S*] ~ {S*}
				if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=2
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals("S")
						&& containsPhrase(subTree.children()[1],"NP") && containsPhrase(subTree.children()[1],"VP")
				){
					/*int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0);*/
					Tree ct = copy(subTree);
					ct.removeChild(0);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}
				
				//rule 7: SBAR|[IN S*] ~ {S*}
				/*if(subTree.nodeString().equals("SBAR") 
						&& children.length>=2
						&& children[0].nodeString().equals("IN")
						&& children[1].nodeString().equals("S")
						&& containsPhrase(children[1],"NP") && containsPhrase(children[1],"VP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0);
					Tree ct = copy(subTree);
					ct.removeChild(0);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}*/
				
				//rule 8: NP|[NP SBAR] ~ [NP], {SBAR -WHNP + NP}
				if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=2
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals("SBAR")
						&& containsPhrase(subTree.children()[1],"WHNP") && containsPhrase(subTree.children()[1],"VP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);Tree ct1 = copy(subTree);
					ct.getNodeNumber(nodeNum).removeChild(1);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
					ct1.lastChild().removeChild(0);
					//Sid.log(ct1.getLeaves()); 
					simpTrees.addN(ct1);
				}
				
				//rule 8': NP|[NP SBAR] ~ [NP]
				else if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=2
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals("SBAR")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}
				
				//rule 8'': NP|[NP , SBAR] ~ [NP], {SBAR -WHNP + NP}
				if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=3
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals(",")
						&& subTree.children()[2].nodeString().equals("SBAR")
						&& containsPhrase(subTree.children()[2],"WHNP") && containsPhrase(subTree.children()[2],"VP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);Tree ct1 = copy(subTree);
					ct.getNodeNumber(nodeNum).removeChild(1); ct.getNodeNumber(nodeNum).removeChild(1);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
					ct1.children()[2].removeChild(0);
					ct1.removeChild(1);
					//Sid.log(ct1.getLeaves()); 
					simpTrees.addN(ct1);
				}
				
				//rule 8'': NP|[NP , SBAR] ~ [NP]
				else if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=3
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals(",")
						&& subTree.children()[2].nodeString().equals("SBAR")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1); ct.getNodeNumber(nodeNum).removeChild(1);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}
				
				
				//rule 9: S* ~ {S*}
				if(subTree.nodeString().equals("S")
						&& subTree.children().length>=2
						&& subTree.children()[0].nodeString().equals("NP")
						&& containsPhrase(subTree,"VP")
						&& subTree.getLeaves().size()<t.getLeaves().size()
				){
					Tree ct1 = copy(subTree);
					//Sid.log(ct1.getLeaves()); 
					simpTrees.addN(ct1);
				}
				
				//rule 10: VP|[VBP NP SBAR] ~ [VBP NP]
				// SBAR starts 
				if(subTree.nodeString().equals("VP") 
						&& subTree.children().length>=3
						&& subTree.children()[0].nodeString().equals("VBP")
						&& subTree.children()[1].nodeString().equals("NP")
						&& subTree.children()[2].nodeString().equals("SBAR")
						&& containsPhrase(subTree.children()[2],"WHADVP") && !containsPhrase(subTree.children()[2],"NP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(2);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}
				
				//rule 10': VP(, SBAR) ~ }, SBAR{
				if(subTree.nodeString().equals("VP") 
						&& subTree.children().length>=3
						&& containsPhrase(subTree,",") && containsPhrase(subTree,"SBAR")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					int childNum=0;
					while(childNum!=subTree.children().length-1){
						if(subTree.children()[childNum].nodeString().equals(",")
								&& subTree.children()[childNum+1].nodeString().equals("SBAR")){
							ct.getNodeNumber(nodeNum).removeChild(childNum); ct.getNodeNumber(nodeNum).removeChild(childNum);
						}
						childNum++;
					}
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}
				
				/*//rule 11: S|[NP ADVP VP] ~ [NP VP]
				//moved up
				if(subTree.nodeString().equals("S") 
						&& children.length>=3
						&& children[0].nodeString().equals("NP")
						&& (children[1].nodeString().equals("ADVP") || children[1].nodeString().equals("ADJP"))
						&& children[2].nodeString().equals("VP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}*/
				
				/*//rule 12: VP|[VBN PP] ~ [VBN]
				//not so important
				if(subTree.nodeString().equals("VP") 
						&& children.length>=2
						&& children[0].nodeString().equals("VBN")
						&& children[1].nodeString().equals("PP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}*/
				
				//rule 13: VP|[VP1 , CC VP2] ~ [VP1] [VP2]
				if(subTree.nodeString().equals("VP") 
						&& subTree.children().length>=4
						&& subTree.children()[0].nodeString().equals("VP")
						&& subTree.children()[1].nodeString().equals(",")
						&& subTree.children()[2].nodeString().equals("CC")
						&& subTree.children()[3].nodeString().equals("VP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1); ct.getNodeNumber(nodeNum).removeChild(1); ct.getNodeNumber(nodeNum).removeChild(1);
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
					ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0);
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}
				
				//rule 13': VP|[VP1 CC VP2] ~ [VP1] [VP2]
				if(subTree.nodeString().equals("VP") 
						&& subTree.children().length>=3
						&& subTree.children()[0].nodeString().equals("VP")
						&& subTree.children()[1].nodeString().equals("CC")
						&& subTree.children()[2].nodeString().equals("VP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1); ct.getNodeNumber(nodeNum).removeChild(1);   
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
					ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0);  
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}
				
			
				/*//rule 14: VP|[MD VP , S+] ~ [MD VP]
				//move up
				if(subTree.nodeString().equals("VP") 
						&& children.length>=4
						&& children[0].nodeString().equals("MD")
						&& children[1].nodeString().equals("VP")
						&& children[2].nodeString().equals(",")
						&& children[3].nodeString().equals("S")
						&& containsPhrase(children[3],"VP") && !containsPhrase(children[3],"NP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(2); ct.getNodeNumber(nodeNum).removeChild(2);
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}
				
				//rule 15: VP|[VBP|VBZ NP , S+|PP] ~ [VBP VP]
				//move up
				if(subTree.nodeString().equals("VP") 
						&& children.length>=4
						&& (children[0].nodeString().equals("VBP") || children[0].nodeString().equals("VBZ"))
						&& children[1].nodeString().equals("NP")
						&& children[2].nodeString().equals(",")
						&& ((children[3].nodeString().equals("S")&& !(containsPhrase(children[3],"VP") && containsPhrase(children[3],"NP"))) 
								|| children[3].nodeString().equals("PP"))
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(2); ct.getNodeNumber(nodeNum).removeChild(2);
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}
				*/
				//bad rule
				/*//rule 16: NP|[NP1 , NP2 ,?] ~ [NP1] [NP2] {NP1 can be NP2}
				if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=3
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals(",")
						&& subTree.children()[2].nodeString().equals("NP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1); ct.getNodeNumber(nodeNum).removeChild(1); 
					if(ct.getNodeNumber(nodeNum).children().length>1 && ct.getNodeNumber(nodeNum).children()[1].nodeString().equals(","))
						ct.getNodeNumber(nodeNum).removeChild(1); 
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
					ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0);
					if(ct.getNodeNumber(nodeNum).children().length>1 &&ct.getNodeNumber(nodeNum).children()[1].nodeString().equals(","))
						ct.getNodeNumber(nodeNum).removeChild(1); 
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
					String np1Penn = subTree.firstChild().pennString();
					String np2Penn = subTree.children()[2].pennString();
					Tree ct1 = addModal(np1Penn,np2Penn);
					//Sid.log(ct1.getLeaves()); 
					simpTrees.addN(ct1);
				}*/
				
				//rule 16': NP|[NP1 NP2] ~ [NP1] [NP2]
				if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=2
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals("NP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1);  
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
					ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); 
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}
				
				
				//rule 16'': NP|[DT NP1 CC NP2 ***] ~ [NP1] [NP2]
				//Bad rules because it separates things like "between, RFX5, and, CIITA" 
				//But I don't care because I am storing all possible trees and it can help in
				//cases like with, the, yeast, two-hybrid, and, far-Western, blot, assays, . 
				if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=4
						&& subTree.children()[0].nodeString().equals("DT")
						&& subTree.children()[1].nodeString().equals("NP")
						&& subTree.children()[2].nodeString().equals("CC")
						&& subTree.children()[3].nodeString().equals("NP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1);  ct.getNodeNumber(nodeNum).removeChild(1);  
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
					ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(2); ct.getNodeNumber(nodeNum).removeChild(2);   
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}
				
				/*//rule 17: NP|[PRP VBN NN] ~ [PRP NN]
				//move up
				if(subTree.nodeString().equals("NP") 
						&& children.length>=3
						&& children[0].nodeString().startsWith("PRP") //note the point
						&& children[1].nodeString().equals("VBN")
						&& children[2].nodeString().equals("NN")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1);  
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}
				
				//rule 18: VP|[VBN|VBZ|VBD ADVP ...] ~ [VBN ...]
				//move up
				if(subTree.nodeString().equals("VP") 
						&& children.length>=3
						&& (children[0].nodeString().equals("VBN") || children[0].nodeString().equals("VBZ") || children[0].nodeString().equals("VBD"))
						&& children[1].nodeString().equals("ADVP") 
						
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1);  
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}*/
				
				//rule 19: NP|[NP VP+] ~ [NP] {NP "can be" VP+}
				//VP+ starts with a VBG not VBD - VBD is past tense. found an error like that 
				if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=2
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals("VP")
						&& (subTree.children()[1].firstChild().nodeString().equals("VBG") || subTree.children()[1].firstChild().nodeString().equals("VBN"))
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1);  
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
					Tree ct1 = addModal(subTree);
					//Sid.log(ct1.getLeaves()); 
					simpTrees.addN(ct1);
				}
				
				//rule 19': NP|[NP , VP] ~ [NP] {NP "can be" VP}
				if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=3
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals(",")
						&& subTree.children()[2].nodeString().equals("VP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					if(ct.getNodeNumber(nodeNum).children().length>=4&&ct.getNodeNumber(nodeNum).children()[3].nodeString().equals(","))
						ct.getNodeNumber(nodeNum).removeChild(3);//remove comma at the end
					ct.getNodeNumber(nodeNum).removeChild(1); ct.getNodeNumber(nodeNum).removeChild(1);  
					simpTrees.addN(ct);
					String npPenn = subTree.firstChild().pennString();
					String vpPenn = subTree.children()[2].pennString();
					Tree ct1 = addModal(npPenn,vpPenn);
					simpTrees.addN(ct1);
				}
				
				/*//rule 20: PP|[ADVP IN ...] ~ [IN ...]
				//move up
				if(subTree.nodeString().equals("PP") 
						&& children.length>=3
						&& children[0].nodeString().equals("ADVP")
						&& children[1].nodeString().equals("IN")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0);  
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}*/
				
				//rule 21: NP|[NP ADJP] ~ [NP], {NP can be ADJP}
				if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=2
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals("ADJP")
						&& subTree.children()[1].firstChild().nodeString().equals("JJ")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
					String npPenn = subTree.firstChild().pennString();
					String adjPenn = subTree.children()[1].pennString();
					Tree ct1 = addModal(npPenn,adjPenn);
					//Sid.log(ct1.getLeaves()); 
					simpTrees.addN(ct1);
				}
				
				//rule 21': NP|[NP , ADJP] ~ [NP], {NP can be ADJP}
				if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=3
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals(",")
						&& subTree.children()[2].nodeString().equals("ADJP")
						//&& children[1].firstChild().nodeString().equals("JJ"):Pierre, Vinken, ,, 61, years, old, ,,
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					if(ct.getNodeNumber(nodeNum).children().length>=4&&ct.getNodeNumber(nodeNum).children()[3].nodeString().equals(","))
						ct.getNodeNumber(nodeNum).removeChild(3);//remove comma at the end
					ct.getNodeNumber(nodeNum).removeChild(1);ct.getNodeNumber(nodeNum).removeChild(1);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
					String npPenn = subTree.firstChild().pennString();
					String adjPenn = subTree.children()[2].pennString();
					Tree ct1 = addModal(npPenn,adjPenn);
					//Sid.log(ct1.getLeaves()); 
					simpTrees.addN(ct1);
				}
				
				
				//rule 22: S[SBAR+ , ...] ~ }SBAR+ ,{
				//SBAR+ contians IN
				if(subTree.nodeString().equals("S") 
						&& subTree.children().length>=3
						&& subTree.children()[0].nodeString().equals("SBAR")
						&& subTree.children()[1].nodeString().equals(",")
						&& containsPhrase(subTree.children()[0],"IN") 
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}

				//rule 24: PP|[PP1 CC PP2] ~ [PP1] [PP2]
				if(subTree.nodeString().equals("PP") 
						&& subTree.children().length>=3
						&& subTree.children()[0].nodeString().equals("PP")
						&& subTree.children()[1].nodeString().equals("CC")
						&& subTree.children()[2].nodeString().equals("PP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1); ct.getNodeNumber(nodeNum).removeChild(1);   
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
					ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0);  
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}
				
				//rule 25: VP|[... , PP] ~ }, PP{
				if(subTree.nodeString().equals("VP") 
						&& subTree.children().length>=3
						&& subTree.children()[subTree.children().length-1].nodeString().equals("PP")
						&& subTree.children()[subTree.children().length-2].nodeString().equals(",")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(subTree.children().length-2); ct.getNodeNumber(nodeNum).removeChild(subTree.children().length-2);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}
				
				//rule 26: S*[ADVP , ...] ~ }ADVP ,{
				if(subTree.nodeString().equals("S") 
						&& subTree.children().length>=3
						&& subTree.children()[0].nodeString().equals("ADVP")
						&& subTree.children()[1].nodeString().equals(",")
						&& containsPhrase(subTree,"NP") && containsPhrase(subTree,"VP") 
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).removeChild(0);
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}
				
				//rule 27: VP[... , ADVP PP , ...] ~ }, ADVP ,{
				if(subTree.nodeString().equals("VP") 
						&& subTree.children().length>=6
						&& containsPhrase(subTree,"ADVP") && containsPhrase(subTree,"PP") && containsPhrase(subTree,",") 
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					int childNum=0;
					while(childNum!=subTree.children().length-3){
						if(subTree.children()[childNum].nodeString().equals(",")
								&& subTree.children()[childNum+1].nodeString().equals("ADVP")
								&& subTree.children()[childNum+2].nodeString().equals("PP")
								&& subTree.children()[childNum+3].nodeString().equals(",")){
							ct.getNodeNumber(nodeNum).removeChild(childNum); ct.getNodeNumber(nodeNum).removeChild(childNum); ct.getNodeNumber(nodeNum).removeChild(childNum); ct.getNodeNumber(nodeNum).removeChild(childNum);
						}
						childNum++;
					}
					//Sid.log(ct.getLeaves()); 
					simpTrees.addN(ct);
				}
				
				
				//rule 28: VP|[VBZ ADJP1 , CC ADJP2] ~ [ADJP1] [ADJP2]
				if(subTree.nodeString().equals("VP") 
						&& subTree.children().length>=5
						&& subTree.children()[1].nodeString().equals("ADJP")
						&& subTree.children()[2].nodeString().equals(",")
						&& subTree.children()[3].nodeString().equals("CC")
						&& subTree.children()[4].nodeString().equals("ADJP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1); ct.getNodeNumber(nodeNum).removeChild(1); ct.getNodeNumber(nodeNum).removeChild(1);
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
					ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(2); ct.getNodeNumber(nodeNum).removeChild(2); ct.getNodeNumber(nodeNum).removeChild(2);
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}
				
				//rule 28': VP|[VBZ ADJP1 CC ADJP2] ~ [ADJP1] [ADJP2]
				if(subTree.nodeString().equals("VP") 
						&& subTree.children().length>=4
						&& subTree.children()[1].nodeString().equals("ADJP")
						&& subTree.children()[2].nodeString().equals("CC")
						&& subTree.children()[3].nodeString().equals("ADJP")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1); ct.getNodeNumber(nodeNum).removeChild(1);   
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
					ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(2); ct.getNodeNumber(nodeNum).removeChild(2);  
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}
				
				//rule 29: NP|[NP1 PRN] ~ [NP1] [PRN - LRB - RRB]
				if(subTree.nodeString().equals("NP") 
						&& subTree.children().length>=2
						&& subTree.children()[0].nodeString().equals("NP")
						&& subTree.children()[1].nodeString().equals("PRN")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(1); 
					simpTrees.addN(ct);
					ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(0); ct.getNodeNumber(nodeNum).children()[0].removeChild(0); ct.getNodeNumber(nodeNum).children()[0].removeChild(1);  
					//Sid.log(ct.getLeaves());
					simpTrees.addN(ct);
				}
				
				//rule 30: VP|[* NP1 , S] ~ [* NP1]
				if(subTree.nodeString().equals("VP") 
						&& subTree.children().length>=4
						&& subTree.children()[1].nodeString().equals("NP")
						&& subTree.children()[2].nodeString().equals(",")
						&& subTree.children()[3].nodeString().equals("S")
				){
					int nodeNum = subTree.nodeNumber(t);
					Tree ct = copy(t);
					ct.getNodeNumber(nodeNum).removeChild(2); ct.getNodeNumber(nodeNum).removeChild(2); 
					simpTrees.addN(ct);
				}
			}
		}
		return simpTrees;
		
	}

	private static boolean containsPhrase(Tree tree, String string) {
		Tree lt = tree;
		for (Tree child : lt.getChildrenAsList()) {
			if(child.nodeString().equals(string))
				return true;
		}
		for (Tree child : lt.getChildrenAsList()) {
			if(child.nodeString().equals("S")&&containsPhrase(child, string))
				return true;
		}
		return false;
	}

	private static Tree addModal(String firstP, String secondP){
		PrintWriter pwr;
		try {
			pwr = new PrintWriter(new FileWriter("dummy.txt"));
			String modPenn = "(S"+firstP+"(VP(MD can)(VP (VB be)"+secondP+"))(. .))";
			pwr.print(modPenn);
			pwr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Treebank tb = new MemoryTreebank(new TreeNormalizer());
		tb.loadPath("dummy.txt");
		Collection<GrammaticalStructure> gsBank = new TreeBankGrammaticalStructureWrapper(tb, false);
		Tree tc = ((TreeBankGrammaticalStructureWrapper)gsBank).getOriginalTree((GrammaticalStructure) gsBank.toArray()[0]);
		return tc; 
	}
	
	private static Tree addModal(Tree t){
		PrintWriter pwr;
		try {
			pwr = new PrintWriter(new FileWriter("dummy.txt"));
			String npPenn = t.firstChild().pennString();
			String vpPenn = t.children()[1].pennString();
			String modPenn = "(S"+npPenn+"(VP(MD can)(VP (VB be)"+vpPenn+"))(. .))";
			pwr.print(modPenn);
			pwr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Treebank tb = new MemoryTreebank(new TreeNormalizer());
		tb.loadPath("dummy.txt");
		Collection<GrammaticalStructure> gsBank = new TreeBankGrammaticalStructureWrapper(tb, false);
		Tree tc = ((TreeBankGrammaticalStructureWrapper)gsBank).getOriginalTree((GrammaticalStructure) gsBank.toArray()[0]);
		return tc; 
	}
	
	private static Tree copy(Tree t) {
		PrintWriter pwr;
		try {
			pwr = new PrintWriter(new FileWriter("dummy.txt"));
			pwr.print(t.pennString());
			pwr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Treebank tb = new MemoryTreebank(new TreeNormalizer());
		tb.loadPath("dummy.txt");
		Collection<GrammaticalStructure> gsBank = new TreeBankGrammaticalStructureWrapper(tb, false);
		Tree tc = ((TreeBankGrammaticalStructureWrapper)gsBank).getOriginalTree((GrammaticalStructure) gsBank.toArray()[0]);
		return tc;
	}

	public static String ptb2dot(Tree t, Tree root){
		if(t.isLeaf())
			return "";
		String ret = "";
		for (Tree child : t.children()) {
			ret += t.nodeString()+"_"+t.nodeNumber(root) + " -- " + child.nodeString()+"_"+child.nodeNumber(root) + ";\n";
			// ret += ptb2dot(child);
		}
		for (Tree child : t.children()) {
			//  ret += t.nodeString() + " -- " + child.nodeString()+";\n";
			ret += ptb2dot(child,root);
		}
		return ret;
	}


	/**
	 * Print typed dependencies in either the Stanford dependency representation or in the
	 * conllx format.
	 *
	 * @param deps Typed dependencies to print
	 * @param tree Tree corresponding to typed dependencies (only necessary if conllx == true)
	 * @param conllx If true use conllx format, otherwise use Stanford representation
	 */
	static void printDependencies(Collection<TypedDependency> deps, Tree tree, boolean conllx) {
		if (conllx) {
			List<Tree> leaves = tree.getLeaves();
			List<String> words = new ArrayList<String>(leaves.size());
			List<String> pos = new ArrayList<String>(leaves.size());
			String[] relns = new String[leaves.size()];
			int[] govs = new int[leaves.size()];

			for (Tree leaf : leaves) {
				words.add(leaf.value());
				pos.add(leaf.parent(tree).value()); // use slow, but safe, parent look up
			}

			for (TypedDependency dep : deps) {
				int depIdx = dep.dep().index()-1;
				govs[depIdx] = dep.gov().index();
				relns[depIdx] = dep.reln().toString();
			}

			for (int i = 0; i < relns.length; i++) {
				System.out.printf("%d\t%s\t_\t%s\t%s\t_\t%d\t%s\t_\t_\n", i+1, words.get(i), pos.get(i), pos.get(i),govs[i],relns[i]);
			}
			System.out.println();
		} else {
			System.out.println(StringUtils.join(deps, "\n"));
			System.out.println();
		}
	}



}


/**
 * Allow a collection of trees, that is a Treebank, appear to be a collection
 * of GrammaticalStructures.
 *
 * @author danielcer
 *
 */
class TreeBankGrammaticalStructureWrapper extends AbstractCollection<GrammaticalStructure>{
	final public Treebank treebank;
	final public boolean keepPunct;
	private Map<GrammaticalStructure, Tree> origTrees = new WeakHashMap<GrammaticalStructure, Tree>();


	public TreeBankGrammaticalStructureWrapper(Treebank wrappedTreeBank) {
		treebank = wrappedTreeBank;
		keepPunct = false;
	}

	public TreeBankGrammaticalStructureWrapper(Treebank wrappedTreeBank, boolean keepPunct) {
		treebank = wrappedTreeBank;
		this.keepPunct = keepPunct;
	}

	@Override
	public Iterator<GrammaticalStructure> iterator() {
		return new gsIterator();
	}


	public Tree getOriginalTree(GrammaticalStructure gs) {
		return origTrees.get(gs);
	}

	private class gsIterator implements Iterator<GrammaticalStructure> {
		Iterator<Tree> tbIterator = treebank.iterator();

		public boolean hasNext() {
			return tbIterator.hasNext();
		}

		public GrammaticalStructure next() {
			Tree t = tbIterator.next();


			GrammaticalStructure gs = (keepPunct ?
					new EnglishGrammaticalStructure(t, Filters.<String>acceptFilter()) :
						new EnglishGrammaticalStructure(t));

			origTrees.put(gs, t);
			return gs;
		}


		public void remove() {
			tbIterator.remove();
		}

	}

	@Override
	public int size() {
		return treebank.size();
	}
}
