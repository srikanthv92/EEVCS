/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;


public class Enc2_2 extends Encryptor {
	/** description of this VCMode*/
	final String DESCRIPTION = "This is the basic mode for the visual" +
			" cryptography introduced by Naor and Shamir in 1994. This " +
			"cryptographic scheme is perfectly secure and can be " +
			"encoded without any computations directly by the human " +
			"visual system. It conceals a picture by generating two " +
			"transparencies which look like random noise. But " +
			"overlaying of these transparencies leads to a \"clear\" " +
			"image, which contains the original information. For more " +
			"possible schemes with more features look at the mode menu." +
			"<br><b>How is it done?</b>" +
			"<br>Each pixel is divided into 4 subpixel, which can be black or white. " +
			"There are always 2 black and 2 white subpixel. <br>" +
			"For displaying a white pixel, the sequence of the subpixel " +
			"on the first and on the second transparency are identically. " +
			"Overlaying leads to 2 black and 2 white subpixel.<br>" +
			"For displaying a black subpixel the sequences of the subpixel " +
			"are different, so that you get 4 black subpixel by overlaying.";
	/** creates a new encryptor object for the VC with 2 out of 2<br/>
	 * mainly it sets up the init-matrices for this scheme
	 * 
	 * @param height - height of the source pic
	 * @param width - width of the source pic
	 * @param n - number of foils
	 */
	public Enc2_2(int height, int width, int n){
		super(height, width, 2);
		// init matrices
		m_initMatrixC0 = new IntMatrix(m_maxFoil, m_maxSubpixel);
		m_initMatrixC1 = new IntMatrix(m_maxFoil, m_maxSubpixel);
		m_initMatrixC0.setElement(0, 0, WHITEPIXEL);
		m_initMatrixC0.setElement(0, 1, WHITEPIXEL);
		m_initMatrixC0.setElement(0, 2, BLACKPIXEL);
		m_initMatrixC0.setElement(0, 3, BLACKPIXEL);
		m_initMatrixC0.setElement(1, 0, WHITEPIXEL);
		m_initMatrixC0.setElement(1, 1, WHITEPIXEL);
		m_initMatrixC0.setElement(1, 2, BLACKPIXEL);
		m_initMatrixC0.setElement(1, 3, BLACKPIXEL);

		m_initMatrixC1.setElement(0, 0, WHITEPIXEL);
		m_initMatrixC1.setElement(0, 1, WHITEPIXEL);
		m_initMatrixC1.setElement(0, 2, BLACKPIXEL);
		m_initMatrixC1.setElement(0, 3, BLACKPIXEL);
		m_initMatrixC1.setElement(1, 0, BLACKPIXEL);
		m_initMatrixC1.setElement(1, 1, BLACKPIXEL);
		m_initMatrixC1.setElement(1, 2, WHITEPIXEL);
		m_initMatrixC1.setElement(1, 3, WHITEPIXEL);		
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
