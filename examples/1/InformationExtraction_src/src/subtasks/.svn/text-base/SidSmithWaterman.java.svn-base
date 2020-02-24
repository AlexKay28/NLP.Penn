package subtasks;

import util.Sid;

import Jama.Matrix;

/**
 * Smith, Temple F.; and Waterman, Michael S. (1981). "Identification of Common Molecular Subsequences". Journal of Molecular Biology  147: 195–197. doi:10.1016/0022-2836(81)90087-5
 * @author sjonnal3
 *
 */
public class SidSmithWaterman {

	public static void main(String[] args){
		//standard examples
		/*String A="A A U G C C A U U G A C G G";
		String B="C A G C C U C G C U U A G";
		*/
		
		String A="A A U G C C A U U G A C G G";
		String B="C A G C C U C G C U U A G";
		
		double score = getSidSWscore(A,B);
		
		Sid.log(score);
		
	}

	public static double getSidSWscore(String A, String B) {

		double score = 0;
		
		String[] As=A.split("\\s");
		String[] Bs=B.split("\\s");
		Matrix H=new Matrix(As.length+1,Bs.length+1);
		
		for(int k=0;k<H.getRowDimension();k++){
			H.set(k, 0, 0);
		}
		for(int l=0;l<H.getColumnDimension();l++){
			H.set(0, l, 0);
		}	
		
		//equation 1
		for(int i=1;i<H.getRowDimension();i++){
			for(int j=1; j<H.getColumnDimension();j++){
				
				/*if(i==2&j==3)
					H.print(4, 1);
				*/
				double par1=H.get(i-1, j-1);
				if(As[i-1].equals(Bs[j-1])){//i-1 and j-1 because our seqs. start with 0
					par1+=1;
				}
				else
					par1-=0.3333;
				
				double par2=Integer.MIN_VALUE;
				for(int k=1;k<=i;k++){
					double dummy=H.get(i-k, j)-(1+0.3333*k);
					if(dummy>par2)
						par2=dummy;
				}
				
				double par3=Integer.MIN_VALUE;
				for(int l=1;l<=j;l++){
					double dummy=H.get(i, j-l)-(1+0.3333*l);
					if(dummy>par3)
						par3=dummy;
				}
				
				double par4=0;
				
				double entry=Math.max(Math.max(par1, par2), Math.max(par3, par4));
				H.set(i, j, entry);
				if(entry>score)
					score=entry;
			}			
		}
		
		//print H
		//H.print(4, 1);
		return score;
	}
	
}
