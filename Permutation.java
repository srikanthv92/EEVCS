/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;

/** Creates permutations, edited and changed by jb<br/>
 * implements the Rosenalgorithm
 * @author  Michael Gilleland, Merriam Park Software
 * @author Johannes Boßle
 */
public class Permutation {
	  private int[] a;
	  private int numLeft;
	  private int total;

	  /** creates an Object to create permutations
	   * 
	   * @param n - number of elements to permutate
	   */
	  public Permutation (int n) {
	    if (n < 1 || n>9) {
	      throw new IllegalArgumentException ("Min 1, max 9");
	    }
	    a = new int[n];
	    total = getFactorial (n);
	    reset ();
	  }

	  /**creates a int[][] with all possible permutations, saved as int[].
	   * int[] is the internal represantation for a permutation
	   * @return vector with all possible permutations
	   */
	  public int[][] getPermArray(){
	  	this.reset();
	  	int[][] result = new int[this.getTotal()][a.length];
	  	for(int i=0; i<result.length; i++){
	  		int[] temp = this.getNext();
	  		for(int j=0; j<a.length; j++){
	  			result[i][j]=temp[j];
	  		}
	  	}
	  	return result;
	  }
	  /** does a reset, sets all values to the init-values
	   */
	  public void reset () {
	    for (int i = 0; i < a.length; i++) {
	      a[i] = i;
	    }
	    numLeft = total;//new BigInteger (total.toString ());
	  }
	  /**returns the number of left permutations
	   * 
	   * @return number of left permutations
	   */
	  public int getNumLeft () {
	    return numLeft;
	  }

	  /** returns the number how many permutations are possible (n!)
	   * @return number of permutations
	   */
	  public int getTotal () {
	    return total;
	  }

	  /** looks whether ther are more permutations possible
	   * 
	   * @return true if there are more permutations
	   */
	  public boolean hasMore () {
	    return (numLeft!=0);
	  }

	  /** computes n-faculty
	   * 
	   * @param n
	   * @return n*(n-1)*(n-2)...*2*1
	   */
	  private static int getFactorial (int n) {
	    int fact = 1;
	    for (int i = n; i > 1; i--) {
	      fact = fact*(i);
	    }
	    return fact;
	  }

	  /** generates the next Permutation, which is interpreted as number
	   * bigger than the previous one. this is done by an algorithm
	   * of Kenneth H. Rosen
	   * @return Integer[] with the order of the next Permutation
	   */
	  public int[] getNext () {

	    if (numLeft==total) {
	      numLeft = numLeft-1;
	      return a;
	    }
	    int temp;

	    // Find largest index j with a[j] < a[j+1]
	    int j = a.length - 2;
	    //while (a[j].intValue() > a[j+1].intValue()){
	    while (a[j] > a[j+1]) {
	      j--;
	    }

	    // Find index k such that a[k] is smallest integer
	    // greater than a[j] to the right of a[j]
	    int k = a.length - 1;
	    while (a[j] > a[k]) {
	      k--;
	    }

	    // Interchange a[j] and a[k]
	    temp = a[k];
	    a[k] = a[j];
	    a[j] = temp;

	    // Put tail end of permutation after jth position in increasing order
	    int r = a.length - 1;
	    int s = j + 1;

	    while (r > s) {
	      temp = a[s];
	      a[s] = a[r];
	      a[r] = temp;
	      r--;
	      s++;
	    }

	    numLeft = numLeft-1;
	    return a;
	  }
}
