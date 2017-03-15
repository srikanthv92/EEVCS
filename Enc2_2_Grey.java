/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;

import java.util.Vector;

/** Encryptor - Object for the VisualCryptography with greyscales<br/>
 * The implemented scheme can express 4 different greyscales.<br/>
 * For each pixel there is decided which luminance it has and
 * then a special matrix is assigned to.
 * @author Boßle Johannes

 */
public class Enc2_2_Grey extends Encryptor {
	/** description of this VCMode*/
	final String DESCRIPTION = "creates two transparencies for the visual " +
			"cryptography with greyscales. The original picture is " +
			"taken and for each pixel is decided which lumincance " +
			"it has. With this scheme you can achieve 4 different " +
			"greyscales (white, light grey, dark grey, black).";
	/** Grey-Matrices*/
	IntMatrix m_initMatrixG0,m_initMatrixG1,m_initMatrixG2,m_initMatrixG3;
	/** Array of grey Matrices*/
	IntMatrix[] m_Grey0, m_Grey1, m_Grey2, m_Grey3;
	/** creates a new encryptor object for the grey VC<br/>
	 * mainly it sets up the init-matrices for this scheme
	 * 
	 * @param height - height of the source pic
	 * @param width - width of the source pic
	 * @param n - number of foils
	 */
	public Enc2_2_Grey(int height, int width, int n) {
		super(height, width, n);
		m_initMatrixG0 = new IntMatrix(m_maxFoil, m_maxSubpixel);
		m_initMatrixG1 = new IntMatrix(m_maxFoil, m_maxSubpixel);
		m_initMatrixG2 = new IntMatrix(m_maxFoil, m_maxSubpixel);
		m_initMatrixG3 = new IntMatrix(m_maxFoil, m_maxSubpixel);
		
		//init G0
		m_initMatrixG0.setElement(0,0,BLACKPIXEL);
		m_initMatrixG0.setElement(0,1,BLACKPIXEL);
		m_initMatrixG0.setElement(0,2,BLACKPIXEL);
		m_initMatrixG0.setElement(0,3,WHITEPIXEL);
		m_initMatrixG0.setElement(0,4,WHITEPIXEL);
		m_initMatrixG0.setElement(0,5,WHITEPIXEL);
		m_initMatrixG0.setElement(1,0,BLACKPIXEL);
		m_initMatrixG0.setElement(1,1,BLACKPIXEL);
		m_initMatrixG0.setElement(1,2,BLACKPIXEL);
		m_initMatrixG0.setElement(1,3,WHITEPIXEL);
		m_initMatrixG0.setElement(1,4,WHITEPIXEL);
		m_initMatrixG0.setElement(1,5,WHITEPIXEL);
		
		//initG1
		m_initMatrixG1.setElement(0,0,BLACKPIXEL);
		m_initMatrixG1.setElement(0,1,BLACKPIXEL);
		m_initMatrixG1.setElement(0,2,BLACKPIXEL);
		m_initMatrixG1.setElement(0,3,WHITEPIXEL);
		m_initMatrixG1.setElement(0,4,WHITEPIXEL);
		m_initMatrixG1.setElement(0,5,WHITEPIXEL);
		m_initMatrixG1.setElement(1,0,WHITEPIXEL);
		m_initMatrixG1.setElement(1,1,BLACKPIXEL);
		m_initMatrixG1.setElement(1,2,BLACKPIXEL);
		m_initMatrixG1.setElement(1,3,BLACKPIXEL);
		m_initMatrixG1.setElement(1,4,WHITEPIXEL);
		m_initMatrixG1.setElement(1,5,WHITEPIXEL);
		
		//initG2
		m_initMatrixG2.setElement(0,0,BLACKPIXEL);
		m_initMatrixG2.setElement(0,1,BLACKPIXEL);
		m_initMatrixG2.setElement(0,2,BLACKPIXEL);
		m_initMatrixG2.setElement(0,3,WHITEPIXEL);
		m_initMatrixG2.setElement(0,4,WHITEPIXEL);
		m_initMatrixG2.setElement(0,5,WHITEPIXEL);
		m_initMatrixG2.setElement(1,0,WHITEPIXEL);
		m_initMatrixG2.setElement(1,1,WHITEPIXEL);
		m_initMatrixG2.setElement(1,2,BLACKPIXEL);
		m_initMatrixG2.setElement(1,3,BLACKPIXEL);
		m_initMatrixG2.setElement(1,4,BLACKPIXEL);
		m_initMatrixG2.setElement(1,5,WHITEPIXEL);
		
		//initG3
		m_initMatrixG3.setElement(0,0,BLACKPIXEL);
		m_initMatrixG3.setElement(0,1,BLACKPIXEL);
		m_initMatrixG3.setElement(0,2,BLACKPIXEL);
		m_initMatrixG3.setElement(0,3,WHITEPIXEL);
		m_initMatrixG3.setElement(0,4,WHITEPIXEL);
		m_initMatrixG3.setElement(0,5,WHITEPIXEL);
		m_initMatrixG3.setElement(1,0,WHITEPIXEL);
		m_initMatrixG3.setElement(1,1,WHITEPIXEL);
		m_initMatrixG3.setElement(1,2,WHITEPIXEL);
		m_initMatrixG3.setElement(1,3,BLACKPIXEL);
		m_initMatrixG3.setElement(1,4,BLACKPIXEL);
		m_initMatrixG3.setElement(1,5,BLACKPIXEL);
		
	}	
	
	/** method to create all possible permutations of a
	 * init matrix
	 *
	 */
	public void doPermutation(){
		m_Grey0 = new IntMatrix[m_maxPerm];
		m_Grey1 = new IntMatrix[m_maxPerm];
		m_Grey2 = new IntMatrix[m_maxPerm];
		m_Grey3 = new IntMatrix[m_maxPerm];

		int[][] orderArray = m_permutation.getPermArray();
		
		Vector c0 = m_initMatrixG0.getPermMatrixVector(orderArray);
		Vector c1 = m_initMatrixG1.getPermMatrixVector(orderArray);
		Vector c2 = m_initMatrixG2.getPermMatrixVector(orderArray);
		Vector c3 = m_initMatrixG3.getPermMatrixVector(orderArray);
		
		for(int i=0; i<m_maxPerm; i++){
			m_Grey0[i] = (IntMatrix)c0.get(i);
			m_Grey1[i] = (IntMatrix)c1.get(i);
			m_Grey2[i] = (IntMatrix)c2.get(i);
			m_Grey3[i] = (IntMatrix)c3.get(i);
		}
	}
	/** sets up the pixels by giving a matrix and the original colour  
	 * 
	 * @param tempPix
	 */
	public void setMatrixToPixel(int[] tempPix){
		// store grabbed image for encryption
//		System.out.println("   encryptor: setting matrix to each Pixel");
		int black = 256/4;
		int darkGrey = 256/2;
		int leightGrey = 256/4*2;

		for (int y = 0; y < m_hSrc; y++) {
			for (int x = 0; x < m_wSrc; x++) {
				int index = tempPix[x + y * m_hSrc];
				
				int red   = (index >> 16) & 0xff;
		        int green = (index >>  8) & 0xff;
		        int blue  = (index      ) & 0xff;
		        int alpha = (int) (red * 0.299 + green * 0.587 + blue * 0.114);
		        
				m_pixels[x][y].setColor(index);

				if(alpha <= black){
					m_pixels[x][y].setMatrix(m_Grey3[this.getRandom()]);
				}else if(alpha <= darkGrey){
					m_pixels[x][y].setMatrix(m_Grey2[this.getRandom()]);
				}else if(alpha <= leightGrey){
					m_pixels[x][y].setMatrix(m_Grey1[this.getRandom()]);
				}else{
					m_pixels[x][y].setMatrix(m_Grey0[this.getRandom()]);
				}
			}
		}//end for - store
	}//end prepareMatrix

	/**
	 * @see jb.Encryptor#getMaxSubpixel()
	 */
	public int getMaxSubpixel() {
		return 6;
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
		return 3;
	}

	/**
	 * @see jb.Encryptor#getFactorHeight()
	 */
	public int getFactorHeight() {
		return 2;
	}

	/**
	 * @see jb.Encryptor#getFoil(int)
	 */
	public Foil getFoil(int foilNr) {
		int index;
		int factor = this.getFactorWidth();
		int tempPix[] = new int[m_hEnc * m_wEnc]; // array for grabbing pic

		// copy encrypted pic to array
		for (int y = 0; y < m_hSrc; y++) {
			for (int x = 0; x < m_wSrc; x++) {
				index = factor * (x + y * m_hEnc);

				tempPix[index] = m_pixels[x][y].getSubpixel(foilNr, 0);
				tempPix[index + 1] = m_pixels[x][y].getSubpixel(foilNr, 1);
				tempPix[index + 2] = m_pixels[x][y].getSubpixel(foilNr, 2);
				tempPix[index + m_wEnc] = m_pixels[x][y].getSubpixel(foilNr, 3);
				tempPix[index + m_wEnc + 1] = m_pixels[x][y].getSubpixel(foilNr, 4);
				tempPix[index + m_wEnc + 2] = m_pixels[x][y].getSubpixel(foilNr, 5);

			}
		}
		return new Foil(tempPix, m_wEnc, m_hEnc);
	}

//	/**
//	 * @see jb.Encryptor#getOverlayedPic()
//	 */
//	public Foil getOverlayedPic() {
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
//				tempPix[index + m_wEnc] = m_pixels[x][y].getOverlaySubpixel(3);
//				tempPix[index + m_wEnc+1] = m_pixels[x][y].getOverlaySubpixel(4);
//				tempPix[index + m_wEnc+2] = m_pixels[x][y].getOverlaySubpixel(5);
//			}
//		}
//		return new Foil(tempPix, m_wEnc, m_hEnc);
//	}

	/**
	 * @see jb.Encryptor#getDescription()
	 */
	public String getDescription() {
		return DESCRIPTION;
	}
}
