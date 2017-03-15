/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;

/**implements the behaviour for a 3 out of 3 scheme
 * @author Boßle Johannes
 *  
 */
public class Enc3_3 extends Encryptor {
	/** description of this VCMode*/
	final String DESCRIPTION = "With this scheme you can generate 3 " +
			"transparencies, whereas always all 3 participants are " +
			"needed to decode.";

	/** creates a new encryptor object for the VC with 3 out of 3<br/>
	 * mainly it sets up the init-matrices for this scheme
	 * 
	 * @param height - height of the source pic
	 * @param width - width of the source pic
	 * @param n - number of foils
	 */
	public Enc3_3(int height, int width, int n){
		super(height, width, 3);

		// init matrices
		m_initMatrixC0 = new IntMatrix(m_maxFoil, m_maxSubpixel);
		m_initMatrixC1 = new IntMatrix(m_maxFoil, m_maxSubpixel);
		
		m_initMatrixC0.setElement(0, 0, WHITEPIXEL);
		m_initMatrixC0.setElement(0, 1, WHITEPIXEL);
		m_initMatrixC0.setElement(0, 2, BLACKPIXEL);
		m_initMatrixC0.setElement(0, 3, BLACKPIXEL);
		m_initMatrixC0.setElement(1, 0, WHITEPIXEL);
		m_initMatrixC0.setElement(1, 1, BLACKPIXEL);
		m_initMatrixC0.setElement(1, 2, WHITEPIXEL);
		m_initMatrixC0.setElement(1, 3, BLACKPIXEL);
		m_initMatrixC0.setElement(2, 0, WHITEPIXEL);
		m_initMatrixC0.setElement(2, 1, BLACKPIXEL);
		m_initMatrixC0.setElement(2, 2, BLACKPIXEL);
		m_initMatrixC0.setElement(2, 3, WHITEPIXEL);

		m_initMatrixC1.setElement(0, 0, BLACKPIXEL);
		m_initMatrixC1.setElement(0, 1, BLACKPIXEL);
		m_initMatrixC1.setElement(0, 2, WHITEPIXEL);
		m_initMatrixC1.setElement(0, 3, WHITEPIXEL);
		m_initMatrixC1.setElement(1, 0, BLACKPIXEL);
		m_initMatrixC1.setElement(1, 1, WHITEPIXEL);
		m_initMatrixC1.setElement(1, 2, BLACKPIXEL);
		m_initMatrixC1.setElement(1, 3, WHITEPIXEL);
		m_initMatrixC1.setElement(2, 0, BLACKPIXEL);
		m_initMatrixC1.setElement(2, 1, WHITEPIXEL);
		m_initMatrixC1.setElement(2, 2, WHITEPIXEL);
		m_initMatrixC1.setElement(2, 3, BLACKPIXEL);

	}
	/** extracts the pixels for one foil
	 *  @param foilNr the number of foil which shall be extracted
	 *  @return Foil with the pixels for the foil
	 */
	public Foil getFoil(int foilNr){
		int index;
		int tempPix[] = new int[m_hEnc * m_wEnc]; // array for grabbing pic
		
		// copy encrypted pic to array
		for (int y = 0; y < m_hSrc; y++) {
			for (int x = 0; x < m_wSrc; x++) {
				index = 2 * (x + y * m_hEnc);

				tempPix[index] = m_pixels[x][y].getSubpixel(foilNr, 0);
				tempPix[index + 1] = m_pixels[x][y].getSubpixel(foilNr, 1);
				tempPix[index + m_hEnc] = m_pixels[x][y].getSubpixel(foilNr, 2);
				tempPix[index + m_hEnc + 1] = m_pixels[x][y].getSubpixel(foilNr, 3);
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
//		// copy encrypted pic to array
//		for (int y = 0; y < m_hSrc; y++) {
//			for (int x = 0; x < m_wSrc; x++) {
//				index = 2 * (x + y * m_hEnc);
//
//				tempPix[index] = m_pixels[x][y].getOverlaySubpixel(0);
//				tempPix[index + 1] = m_pixels[x][y].getOverlaySubpixel(1);
//				tempPix[index + m_hEnc] = m_pixels[x][y].getOverlaySubpixel(2);
//				tempPix[index + m_hEnc + 1] = m_pixels[x][y].getOverlaySubpixel(3);
//			}
//		}
//	
//		return new Foil(tempPix, m_wEnc, m_hEnc);
//	}
	/**
	 * @see jb.Encryptor#getMaxSubpixel()
	 */
	public int getMaxSubpixel() {
		return 4;
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
		return 2;
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

