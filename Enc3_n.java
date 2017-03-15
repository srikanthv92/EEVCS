/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;

/**implements the behaviour for a 3 out of n (n<=5) scheme
 * @author Boßle Johannes
 *  
 */
public class Enc3_n extends Encryptor {

	/** store whether there are 5 foils or not*/
	boolean foils5;
	/** description of this VCMode*/
	final String DESCRIPTION = "With this scheme you can generate " +
			"a various number of transparencies (minimum 4), " +
			"whereas always any 3 participants can decode.";
	/** creates a new encryptor object for the VC with 3 out of n (n
	 * >3 and n <=5)<br/>
	 * mainly it sets up the init-matrices for this scheme
	 * 
	 * @param height - height of the source pic
	 * @param width - width of the source pic
	 * @param n - number of foils
	 */
	public Enc3_n(int height, int width, int n){
		super(height, width, n);
		foils5 = (m_maxFoil==5);

		// init matrices
		m_initMatrixC0 = new IntMatrix(m_maxFoil, m_maxSubpixel);
		m_initMatrixC1 = new IntMatrix(m_maxFoil, m_maxSubpixel);
				
		//init m_initMatrixC0
		m_initMatrixC0.setElement(0, 0, WHITEPIXEL);
		m_initMatrixC0.setElement(0, 1, WHITEPIXEL);
		m_initMatrixC0.setElement(0, 2, WHITEPIXEL);
		m_initMatrixC0.setElement(0, 3, WHITEPIXEL);
		m_initMatrixC0.setElement(0, 4, BLACKPIXEL);
		m_initMatrixC0.setElement(0, 5, BLACKPIXEL);
		m_initMatrixC0.setElement(0, 6, BLACKPIXEL);
		if(foils5){
			m_initMatrixC0.setElement(0,7,BLACKPIXEL);
		}else{
			m_initMatrixC0.setElement(0, 7, BLACKPIXEL);
		}
		
		m_initMatrixC0.setElement(1, 0, WHITEPIXEL);
		m_initMatrixC0.setElement(1, 1, WHITEPIXEL);
		m_initMatrixC0.setElement(1, 2, WHITEPIXEL);
		m_initMatrixC0.setElement(1, 3, BLACKPIXEL);
		m_initMatrixC0.setElement(1, 4, WHITEPIXEL);
		m_initMatrixC0.setElement(1, 5, BLACKPIXEL);
		m_initMatrixC0.setElement(1, 6, BLACKPIXEL);
		if(foils5){
			m_initMatrixC0.setElement(1,7,BLACKPIXEL);
		}else{
			m_initMatrixC0.setElement(1, 7, BLACKPIXEL);
		}
		
		m_initMatrixC0.setElement(2, 0, WHITEPIXEL);
		m_initMatrixC0.setElement(2, 1, WHITEPIXEL);
		m_initMatrixC0.setElement(2, 2, WHITEPIXEL);
		m_initMatrixC0.setElement(2, 3, BLACKPIXEL);
		m_initMatrixC0.setElement(2, 4, BLACKPIXEL);
		m_initMatrixC0.setElement(2, 5, WHITEPIXEL);
		m_initMatrixC0.setElement(2, 6, BLACKPIXEL);
		if(foils5){
			m_initMatrixC0.setElement(2,7,BLACKPIXEL);
		}else{
			m_initMatrixC0.setElement(2, 7, BLACKPIXEL);
		}
		
		m_initMatrixC0.setElement(3, 0, WHITEPIXEL);
		m_initMatrixC0.setElement(3, 1, WHITEPIXEL);
		m_initMatrixC0.setElement(3, 2, WHITEPIXEL);
		m_initMatrixC0.setElement(3, 3, BLACKPIXEL);
		m_initMatrixC0.setElement(3, 4, BLACKPIXEL);
		m_initMatrixC0.setElement(3, 5, BLACKPIXEL);
		m_initMatrixC0.setElement(3, 6, WHITEPIXEL);
		if(foils5){
			m_initMatrixC0.setElement(3,7,BLACKPIXEL);
			
			m_initMatrixC0.setElement(4, 0, WHITEPIXEL);
			m_initMatrixC0.setElement(4, 1, WHITEPIXEL);
			m_initMatrixC0.setElement(4, 2, WHITEPIXEL);
			m_initMatrixC0.setElement(4, 3, BLACKPIXEL);
			m_initMatrixC0.setElement(4, 4, BLACKPIXEL);
			m_initMatrixC0.setElement(4, 5, BLACKPIXEL);
			m_initMatrixC0.setElement(4, 6, BLACKPIXEL);
			m_initMatrixC0.setElement(4, 7, WHITEPIXEL);
		}else{
			m_initMatrixC0.setElement(3, 7, BLACKPIXEL);
		}
		
		//init m_initMatrixC1
		m_initMatrixC1.setElement(0, 0, BLACKPIXEL);
		m_initMatrixC1.setElement(0, 1, BLACKPIXEL);
		m_initMatrixC1.setElement(0, 2, BLACKPIXEL);
		m_initMatrixC1.setElement(0, 3, BLACKPIXEL);
		m_initMatrixC1.setElement(0, 4, WHITEPIXEL);
		m_initMatrixC1.setElement(0, 5, WHITEPIXEL);
		m_initMatrixC1.setElement(0, 6, WHITEPIXEL);
		if(foils5){
			m_initMatrixC1.setElement(0,7,WHITEPIXEL);
		}else{
			m_initMatrixC1.setElement(0, 7, WHITEPIXEL);
		}
		
		m_initMatrixC1.setElement(1, 0, BLACKPIXEL);
		m_initMatrixC1.setElement(1, 1, BLACKPIXEL);
		m_initMatrixC1.setElement(1, 2, BLACKPIXEL);
		m_initMatrixC1.setElement(1, 3, WHITEPIXEL);
		m_initMatrixC1.setElement(1, 4, BLACKPIXEL);
		m_initMatrixC1.setElement(1, 5, WHITEPIXEL);
		m_initMatrixC1.setElement(1, 6, WHITEPIXEL);
		if(foils5){
			m_initMatrixC1.setElement(1,7,WHITEPIXEL);
		}else{
			m_initMatrixC1.setElement(1, 7, WHITEPIXEL);		
		}
		
		m_initMatrixC1.setElement(2, 0, BLACKPIXEL);
		m_initMatrixC1.setElement(2, 1, BLACKPIXEL);
		m_initMatrixC1.setElement(2, 2, BLACKPIXEL);
		m_initMatrixC1.setElement(2, 3, WHITEPIXEL);
		m_initMatrixC1.setElement(2, 4, WHITEPIXEL);
		m_initMatrixC1.setElement(2, 5, BLACKPIXEL);
		m_initMatrixC1.setElement(2, 6, WHITEPIXEL);
		if(foils5){
			m_initMatrixC1.setElement(2,7,WHITEPIXEL);
		}else{
			m_initMatrixC1.setElement(2, 7, WHITEPIXEL);
		}
		
		m_initMatrixC1.setElement(3, 0, BLACKPIXEL);
		m_initMatrixC1.setElement(3, 1, BLACKPIXEL);
		m_initMatrixC1.setElement(3, 2, BLACKPIXEL);
		m_initMatrixC1.setElement(3, 3, WHITEPIXEL);
		m_initMatrixC1.setElement(3, 4, WHITEPIXEL);
		m_initMatrixC1.setElement(3, 5, WHITEPIXEL);
		m_initMatrixC1.setElement(3, 6, BLACKPIXEL);
		if(foils5){
			m_initMatrixC1.setElement(3,7,WHITEPIXEL);
			
			m_initMatrixC1.setElement(4, 0, BLACKPIXEL);
			m_initMatrixC1.setElement(4, 1, BLACKPIXEL);
			m_initMatrixC1.setElement(4, 2, BLACKPIXEL);
			m_initMatrixC1.setElement(4, 3, WHITEPIXEL);
			m_initMatrixC1.setElement(4, 4, WHITEPIXEL);
			m_initMatrixC1.setElement(4, 5, WHITEPIXEL);
			m_initMatrixC1.setElement(4, 6, WHITEPIXEL);
			m_initMatrixC1.setElement(4, 7, BLACKPIXEL);
		}else{
			m_initMatrixC1.setElement(3, 7, WHITEPIXEL);
		}

		
	}
	/** extracts the pixels for one foil
	 *  @param foilNr the number of foil which shall be extracted
	 *  @return Foil with the pixels for the foil
	 */
	public Foil getFoil(int foilNr){
		int index;
		int factor = this.getFactorWidth();
		int tempPix[] = new int[m_hEnc * m_wEnc]; // array for grabbing pic
		
		// copy encrypted pic to array
		for (int y = 0; y < m_hSrc; y++) {
			for (int x = 0; x < m_wSrc; x++) {
				index = factor*(x + y * m_hEnc);

				tempPix[index] = m_pixels[x][y].getSubpixel(foilNr, 0);
				tempPix[index + 1] = m_pixels[x][y].getSubpixel(foilNr, 1);
				tempPix[index + 2] = m_pixels[x][y].getSubpixel(foilNr, 2);
				tempPix[index + 3] = m_pixels[x][y].getSubpixel(foilNr, 3);
				tempPix[index + m_wEnc] = m_pixels[x][y].getSubpixel(foilNr, 4);
				tempPix[index + m_wEnc + 1] = m_pixels[x][y].getSubpixel(foilNr, 5);
				tempPix[index + m_wEnc + 2] = m_pixels[x][y].getSubpixel(foilNr, 6);
				tempPix[index + m_wEnc + 3] = m_pixels[x][y].getSubpixel(foilNr, 7);
			}
		}
		return new Foil(tempPix,m_wEnc, m_hEnc);
	}
//	/**	extracts the resulting pic for the resultFoil 
//	 * @return the foil/pic getting by overlaying some encrypted
//	 * foils
//	 */
//	public Foil getOverlayedPic(){
//		int tempPix[] = new int[m_hEnc * m_wEnc]; // array for grabbing pic
//		int col, index;
//		int factor = this.getFactorWidth();
//		// copy encrypted pic to array
//		for (int y = 0; y < m_hSrc; y++) {
//			for (int x = 0; x < m_wSrc; x++) {
//				index = factor * (x + y * m_hEnc);
//
//				tempPix[index] = m_pixels[x][y].getOverlaySubpixel(0);
//				tempPix[index + 1] = m_pixels[x][y].getOverlaySubpixel(1);
//				tempPix[index + 2] = m_pixels[x][y].getOverlaySubpixel(2);
//				tempPix[index + 3] = m_pixels[x][y].getOverlaySubpixel(3);
//				tempPix[index + m_wEnc] = m_pixels[x][y].getOverlaySubpixel(4);
//				tempPix[index + m_wEnc + 1] = m_pixels[x][y].getOverlaySubpixel(5);
//				tempPix[index + m_wEnc + 2] = m_pixels[x][y].getOverlaySubpixel(6);
//				tempPix[index + m_wEnc + 3] = m_pixels[x][y].getOverlaySubpixel(7);
//			}
//		}
//	
//		return new Foil(tempPix, m_wEnc, m_hEnc);
//	}
	/**
	 * @see jb.Encryptor#getMaxSubpixel()
	 */
	public int getMaxSubpixel() {
		return 8;
	}

	/**
	 * @see jb.Encryptor#getPermutationInstance()
	 */
	public Permutation getPermutationInstance() {
		return new Permutation(m_maxSubpixel);
	}

	/**
	 * @see jb.Encryptor#getFactorWidth()
	 */
	public int getFactorWidth() {
		return 4;
	}

	/**
	 * @see jb.Encryptor#getFactorHeight()
	 */
	public int getFactorHeight() {
		return 2;
	}
	
	/**
	 * @see jb.Encryptor#getDescription()
	 */
	public String getDescription() {
		return DESCRIPTION;
	}
}


