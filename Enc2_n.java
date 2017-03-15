/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jb;

/** implements the behaviour for a 2 out of n (n<=5) scheme
 * @author Boßle Johannes
 *  
 */
public class Enc2_n extends Encryptor {
	/** description of this VCMode*/
	final String DESCRIPTION = "With this scheme you can generate " +
			"a various number (minimum 3) of transparencies, " +
			"whereas always any 2 participants can decode.";

	/** store whether there are 5 foils or not*/
	boolean m_foils5;

	/** creates a new encryptor object for the VC with 2 out of n (n
	 * >2 and n <=5)<br/>
	 * mainly it sets up the init-matrices for this scheme
	 * 
	 * @param height - height of the source pic
	 * @param width - width of the source pic
	 * @param n - number of foils
	 */
	public Enc2_n(int height, int width, int n){
		super(height, width, n);
		m_foils5 = (m_maxFoil==5);
		
		// init matrices
		m_initMatrixC0 = new IntMatrix(m_maxFoil, m_maxSubpixel);
		m_initMatrixC1 = new IntMatrix(m_maxFoil, m_maxSubpixel);
		
		if(m_maxFoil==3){
			//scheme 2 out of 3
			m_initMatrixC0.setElement(0, 0, WHITEPIXEL);
			m_initMatrixC0.setElement(0, 1, BLACKPIXEL);
			m_initMatrixC0.setElement(0, 2, WHITEPIXEL);
			m_initMatrixC0.setElement(0, 3, BLACKPIXEL);
			m_initMatrixC0.setElement(1, 0, WHITEPIXEL);
			m_initMatrixC0.setElement(1, 1, BLACKPIXEL);
			m_initMatrixC0.setElement(1, 2, WHITEPIXEL);
			m_initMatrixC0.setElement(1, 3, BLACKPIXEL);
			m_initMatrixC0.setElement(2, 0, WHITEPIXEL);
			m_initMatrixC0.setElement(2, 1, BLACKPIXEL);
			m_initMatrixC0.setElement(2, 2, WHITEPIXEL);
			m_initMatrixC0.setElement(2, 3, BLACKPIXEL);
						
			m_initMatrixC1.setElement(0, 0, BLACKPIXEL);
			m_initMatrixC1.setElement(0, 1, WHITEPIXEL);
			m_initMatrixC1.setElement(0, 2, WHITEPIXEL);
			m_initMatrixC1.setElement(0, 3, BLACKPIXEL);
			m_initMatrixC1.setElement(1, 0, WHITEPIXEL);
			m_initMatrixC1.setElement(1, 1, BLACKPIXEL);
			m_initMatrixC1.setElement(1, 2, WHITEPIXEL);
			m_initMatrixC1.setElement(1, 3, BLACKPIXEL);
			m_initMatrixC1.setElement(2, 0, WHITEPIXEL);
			m_initMatrixC1.setElement(2, 1, WHITEPIXEL);
			m_initMatrixC1.setElement(2, 2, BLACKPIXEL);
			m_initMatrixC1.setElement(2, 3, BLACKPIXEL);
		
		} else if(m_maxFoil==4){
			//scheme 2 out of 4
			m_initMatrixC0.setElement(0, 0, WHITEPIXEL);
			m_initMatrixC0.setElement(0, 1, BLACKPIXEL);
			m_initMatrixC0.setElement(0, 2, WHITEPIXEL);
			m_initMatrixC0.setElement(0, 3, WHITEPIXEL);
			m_initMatrixC0.setElement(1, 0, WHITEPIXEL);
			m_initMatrixC0.setElement(1, 1, BLACKPIXEL);
			m_initMatrixC0.setElement(1, 2, WHITEPIXEL);
			m_initMatrixC0.setElement(1, 3, WHITEPIXEL);
			m_initMatrixC0.setElement(2, 0, WHITEPIXEL);
			m_initMatrixC0.setElement(2, 1, BLACKPIXEL);
			m_initMatrixC0.setElement(2, 2, WHITEPIXEL);
			m_initMatrixC0.setElement(2, 3, WHITEPIXEL);
			m_initMatrixC0.setElement(3, 0, WHITEPIXEL);
			m_initMatrixC0.setElement(3, 1, BLACKPIXEL);
			m_initMatrixC0.setElement(3, 2, WHITEPIXEL);
			m_initMatrixC0.setElement(3, 3, WHITEPIXEL);
			
			m_initMatrixC1.setElement(0, 0, BLACKPIXEL);
			m_initMatrixC1.setElement(0, 1, WHITEPIXEL);
			m_initMatrixC1.setElement(0, 2, WHITEPIXEL);
			m_initMatrixC1.setElement(0, 3, WHITEPIXEL);
			m_initMatrixC1.setElement(1, 0, WHITEPIXEL);
			m_initMatrixC1.setElement(1, 1, BLACKPIXEL);
			m_initMatrixC1.setElement(1, 2, WHITEPIXEL);
			m_initMatrixC1.setElement(1, 3, WHITEPIXEL);
			m_initMatrixC1.setElement(2, 0, WHITEPIXEL);
			m_initMatrixC1.setElement(2, 1, WHITEPIXEL);
			m_initMatrixC1.setElement(2, 2, BLACKPIXEL);
			m_initMatrixC1.setElement(2, 3, WHITEPIXEL);
			m_initMatrixC1.setElement(3, 0, WHITEPIXEL);
			m_initMatrixC1.setElement(3, 1, WHITEPIXEL);
			m_initMatrixC1.setElement(3, 2, WHITEPIXEL);
			m_initMatrixC1.setElement(3, 3, BLACKPIXEL);
		} else if(m_foils5){
			//scheme 2 out of 5
			m_initMatrixC0.setElement(0, 0, WHITEPIXEL);
			m_initMatrixC0.setElement(0, 1, BLACKPIXEL);
			m_initMatrixC0.setElement(0, 2, WHITEPIXEL);
			m_initMatrixC0.setElement(0, 3, WHITEPIXEL);
			m_initMatrixC0.setElement(0, 4, WHITEPIXEL);
			m_initMatrixC0.setElement(0, 5, BLACKPIXEL);
			m_initMatrixC0.setElement(1, 0, WHITEPIXEL);
			m_initMatrixC0.setElement(1, 1, BLACKPIXEL);
			m_initMatrixC0.setElement(1, 2, WHITEPIXEL);
			m_initMatrixC0.setElement(1, 3, WHITEPIXEL);
			m_initMatrixC0.setElement(1, 4, WHITEPIXEL);
			m_initMatrixC0.setElement(1, 5, BLACKPIXEL);
			m_initMatrixC0.setElement(2, 0, WHITEPIXEL);
			m_initMatrixC0.setElement(2, 1, BLACKPIXEL);
			m_initMatrixC0.setElement(2, 2, WHITEPIXEL);
			m_initMatrixC0.setElement(2, 3, WHITEPIXEL);
			m_initMatrixC0.setElement(2, 4, WHITEPIXEL);
			m_initMatrixC0.setElement(2, 5, BLACKPIXEL);
			m_initMatrixC0.setElement(3, 0, WHITEPIXEL);
			m_initMatrixC0.setElement(3, 1, BLACKPIXEL);
			m_initMatrixC0.setElement(3, 2, WHITEPIXEL);
			m_initMatrixC0.setElement(3, 3, WHITEPIXEL);
			m_initMatrixC0.setElement(3, 4, WHITEPIXEL);
			m_initMatrixC0.setElement(3, 5, BLACKPIXEL);			
			m_initMatrixC0.setElement(4, 0, WHITEPIXEL);
			m_initMatrixC0.setElement(4, 1, BLACKPIXEL);
			m_initMatrixC0.setElement(4, 2, WHITEPIXEL);
			m_initMatrixC0.setElement(4, 3, WHITEPIXEL);
			m_initMatrixC0.setElement(4, 4, WHITEPIXEL);
			m_initMatrixC0.setElement(4, 5, BLACKPIXEL);
			
			//init m_initMatrixC1
			m_initMatrixC1.setElement(0, 0, BLACKPIXEL);
			m_initMatrixC1.setElement(0, 1, WHITEPIXEL);
			m_initMatrixC1.setElement(0, 2, WHITEPIXEL);
			m_initMatrixC1.setElement(0, 3, WHITEPIXEL);
			m_initMatrixC1.setElement(0, 4, WHITEPIXEL);
			m_initMatrixC1.setElement(0, 5, BLACKPIXEL);			
			m_initMatrixC1.setElement(1, 0, WHITEPIXEL);
			m_initMatrixC1.setElement(1, 1, BLACKPIXEL);
			m_initMatrixC1.setElement(1, 2, WHITEPIXEL);
			m_initMatrixC1.setElement(1, 3, WHITEPIXEL);
			m_initMatrixC1.setElement(1, 4, WHITEPIXEL);
			m_initMatrixC1.setElement(1, 5, BLACKPIXEL);			
			m_initMatrixC1.setElement(2, 0, WHITEPIXEL);
			m_initMatrixC1.setElement(2, 1, WHITEPIXEL);
			m_initMatrixC1.setElement(2, 2, BLACKPIXEL);
			m_initMatrixC1.setElement(2, 3, WHITEPIXEL);
			m_initMatrixC1.setElement(2, 4, WHITEPIXEL);
			m_initMatrixC1.setElement(2, 5, BLACKPIXEL);			
			m_initMatrixC1.setElement(3, 0, WHITEPIXEL);
			m_initMatrixC1.setElement(3, 1, WHITEPIXEL);
			m_initMatrixC1.setElement(3, 2, WHITEPIXEL);
			m_initMatrixC1.setElement(3, 3, BLACKPIXEL);
			m_initMatrixC1.setElement(3, 4, WHITEPIXEL);
			m_initMatrixC1.setElement(3, 5, BLACKPIXEL);			
			m_initMatrixC1.setElement(4, 0, WHITEPIXEL);
			m_initMatrixC1.setElement(4, 1, WHITEPIXEL);
			m_initMatrixC1.setElement(4, 2, WHITEPIXEL);
			m_initMatrixC1.setElement(4, 3, WHITEPIXEL);
			m_initMatrixC1.setElement(4, 4, BLACKPIXEL);
			m_initMatrixC1.setElement(4, 5, BLACKPIXEL);
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

				if(m_foils5){
					tempPix[index] = m_pixels[x][y].getSubpixel(foilNr, 0);
					tempPix[index + 1] = m_pixels[x][y].getSubpixel(foilNr, 1);
					tempPix[index + 2] = m_pixels[x][y].getSubpixel(foilNr, 2);
					tempPix[index + m_wEnc] = m_pixels[x][y].getSubpixel(foilNr, 3);
					tempPix[index + m_wEnc+1] = m_pixels[x][y].getSubpixel(foilNr, 4);
					tempPix[index + m_wEnc+2] = m_pixels[x][y].getSubpixel(foilNr, 5);
				}else{
					tempPix[index] = m_pixels[x][y].getSubpixel(foilNr, 0);
					tempPix[index + 1] = m_pixels[x][y].getSubpixel(foilNr, 1);
					tempPix[index + m_wEnc] = m_pixels[x][y].getSubpixel(foilNr, 2);
					tempPix[index + m_wEnc+1] = m_pixels[x][y].getSubpixel(foilNr, 3);

				}
			}
		}

		//System.out.println("foilNr: "+foilNr);
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
//				if(m_foils5){
//					tempPix[index] = m_pixels[x][y].getOverlaySubpixel(0);
//					tempPix[index + 1] = m_pixels[x][y].getOverlaySubpixel(1);
//					tempPix[index + 2] = m_pixels[x][y].getOverlaySubpixel(2);
//					tempPix[index + m_wEnc] = m_pixels[x][y].getOverlaySubpixel(3);
//					tempPix[index + m_wEnc+1] = m_pixels[x][y].getOverlaySubpixel(4);
//					tempPix[index + m_wEnc+2] = m_pixels[x][y].getOverlaySubpixel(5);
//					
//				}else {
//					tempPix[index] = m_pixels[x][y].getOverlaySubpixel(0);
//					tempPix[index + 1] = m_pixels[x][y].getOverlaySubpixel(1);
//					tempPix[index + m_wEnc] = m_pixels[x][y].getOverlaySubpixel(2);
//					tempPix[index + m_wEnc+1] = m_pixels[x][y].getOverlaySubpixel(3);
//				}
//			}
//		}
//	
//		return new Foil(tempPix, m_wEnc, m_hEnc);
//	}

	/**
	 * @see jb.Encryptor#getMaxSubpixel()
	 */
	public int getMaxSubpixel() {
		if(m_maxFoil==5) return 6;
		else return 4;
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
		if(m_maxFoil==5) return 3;
		else return 2;
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


