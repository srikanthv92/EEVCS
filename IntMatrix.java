/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package jb;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This class implements a nxm matrix. The matrix elements are 
 * integer values.<br/> edited and some functions added
 * 
 * @author Holger Schmid
 * @author Boßle Johannes
 */
public class IntMatrix {
	/** number of rows and columns in matrix*/
	int m_n, m_m;
	/** store the matrix values*/
	int m_matrix[][]; // matrix values

	/**
	 * Constructs an empty nxm matrix.
	 * 
	 * @param rows
	 *            number of rows
	 * @param columns
	 *            number of columns
	 */
	public IntMatrix(int rows, int columns) {
		m_n = rows;
		m_m = columns;
		m_matrix = new int[m_n][m_m];
	}

	/**
	 * Constructs a new matrix by copying an other one /*
	 * 
	 * @param mat
	 *            the matrix to be copyied
	 */
	public IntMatrix(IntMatrix mat) {
		int i, j;
		m_n = mat.getRows();
		m_m = mat.getColumns();
		m_matrix = new int[m_n][m_m];

		for (i = 0; i < m_n; i++){
			for (j = 0; j < m_m; j++){
				m_matrix[i][j] = mat.getElement(i, j);
			}
		}
	}
//	/** constructs a Matrix from int[][], be carefull to transpose
//	 * the matrix sometimes before, first the rows, then the columns
//	 * returns IntMatrix(rows, clumns)
//	 * @param matrix (rows, columns)
//	 */
//	private IntMatrix(int[][] matrix){
//		this(matrix.length,matrix[0].length);
//		for(int i=0; i<m_n; i++){
//			for(int j=0; j<m_m; j++){
//				m_matrix[i][j] = matrix[i][j];
//			}
//		}
//		
//	}

	/**
	 * Returns the number of rows of the matrix
	 * 
	 * @return number of rows
	 */
	public int getRows() {
		return m_n;
	}

	/**
	 * Returns the number of columns of the matrix
	 * 
	 * @return number of columns
	 */
	public int getColumns() {
		return m_m;
	}

	/**
	 * Returns the value of a matrix element.
	 * 
	 * @param row
	 *            row of element
	 * @param column
	 *            column of element
	 */
	public int getElement(int row, int column) {
		return m_matrix[row][column];
	}

	/**
	 * Sets the value of a matrix element.
	 * 
	 * @param row
	 *            row of element
	 * @param column
	 *            column of element
	 * @param value
	 *            value to be set
	 */
	public void setElement(int row, int column, int value) {
		m_matrix[row][column] = value;
		return;
	}

	/** set the elements of a Matrix to the given int[][]
	 * 
	 * @param src - matrix with the elements to set
	 */
	public void setElements(int[][] src){
		for(int i=0; i<src.length; i++){
			for(int j=0; j<src[i].length; j++){
				this.setElement(i,j,src[i][j]);
			}
		}
	}
	/**
	 * Returns a string represantation of the matrix. The string consists of the
	 * matrix element values, seperated by tabs. The rows of the matrix are
	 * seperated by newlines.
	 * 
	 * @return a string with the values of the matrix
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		int i, j;
		for (i = 0; i < m_n; i++) {//rows
			for (j = 0; j < m_m; j++) {//columns
				result.append((m_matrix[i][j] + "\t"));
			}
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * Exchanges two rows of the matrix.
	 * 
	 * @param row1
	 *            the one row to be exchanged
	 * @param row2
	 *            the other row to be exchanged
	 */
	public void exchangeRows(int row1, int row2) {
		int i;
		int temp;
		for (i = 0; i < m_m; i++) {
			temp = m_matrix[row1][i];
			m_matrix[row1][i] = m_matrix[row2][i];
			m_matrix[row2][i] = temp;
		}
		return;
	}
	/**
	 * Exchanges two columns of the matrix.
	 * 
	 * @param col1
	 *            the one col to be exchanged
	 * @param col2
	 *            the other col to be exchanged
	 */
	public void exchangeColumns(int col1, int col2) {
		int i;
		int temp;
		for (i = 0; i < m_n; i++) {
			temp = m_matrix[i][col1];
			m_matrix[i][col1] = m_matrix[i][col2];
			m_matrix[i][col2] = temp;
		}
	}
	/** returns a Column of the Matrix
	 * 
	 * @param nrColumn
	 * @return int[] with Elements of the Column nrColumn
	 */
	public int[] getColumn (int nrColumn){
		int[] result = new int[this.getRows()];
		for(int i=0; i<result.length; i++){
			result[i]= this.getElement(i,nrColumn);
		}
		return result;
	}
	/** returns a row of the Matrix
	 * 
	 * @param row
	 * @return int[] with Elements of the Column nrColumn
	 */
	public int[] getRow (int row){
		return m_matrix[row];
	}
	/** creates a new IntMatrix with the specified column-order
	 * uses #IntMatrix(int[][]) to create the new Matrix
	 * @param order - the order of the columns
	 * @return IntMatrix with the specified order of the column
	 */
	public IntMatrix reorderColumns(int[] order){
		int[][] result = new int[order.length][];
		for(int i=0; i<order.length; i++){
			result[i] = this.getColumn(order[i]); 
		}
		result = this.transposeMatrix(result);
		
		IntMatrix temp = new IntMatrix(result.length, result[0].length);
		temp.setElements(result);
		return temp;
	}
	/** transpose a int[][] with column, rows to a int[][] 
	 * with rows, columns
	 * @param src the int[][] to transpose
	 * @return the transposed int[][]
	 */
	public int[][] transposeMatrix(int[][] src){
		int[][] res = new int[src[0].length][src.length];
		for(int i=0; i<src.length; i++){
			for(int j=0; j<src[i].length; j++){
				res[j][i]=src[i][j];
			}
		}
		return res;
	}
	/** gets a Vector with all permutated Matrix
	 * 
	 * @param v - the vector, where the order of the 
	 * permutations are stored, see @link{Permutation#getPermVector()}
	 * @return a Vector with all permutated Matrix
	 */
	public Vector getPermMatrixVector(Vector v){
		Vector result = new Vector();
		Enumeration en = v.elements();
		while(en.hasMoreElements()){
			int[] order = (int[])en.nextElement();
			result.add(this.reorderColumns(order));
		}
		return result;
	}
	/** gets a Vector with all permutated Matrix
	 * 
	 * @param orderArray - the vector, where the order of the 
	 * permutations are stored, see @link{Permutation#getPermVector()}
	 * @return a Vector with all permutated Matrix
	 */
	public Vector getPermMatrixVector(int[][] orderArray){
		Vector result = new Vector();
		//get the int[] for permutation
		for(int i=0; i<orderArray.length; i++){
				result.add(this.reorderColumns(orderArray[i]));
		}
		
		return result;
	}
}// class IntMatrix
