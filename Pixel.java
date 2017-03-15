/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;
/**
 * Pixel: subclass for ImageEncryptor
 * 
 * @author Holger Schmid
 * @author Boßle Johannes
 * @version 08.11.99
 */

class Pixel {
	/** Number of maximum Foils*/
	int m_maxFoil;
	/** Number of maximum Subpixel*/
	int m_maxSubpixel;
	/** Colour of this pixel in the original pic*/
	int m_color; 
	/** Matrix used to encrypt this pixel*/
	IntMatrix m_cryptMatrix; 
	/**color of subpixels*/
	int m_subPixel[][]; 
	/** resulting subpixels of overlayed foils*/
//	int m_overlaySubPixel[]; 

	/** creates a new Pixel Object with the specified Paramater
	 * 
	 * @param maxFoils - the number of Foils used for encryption
	 * @param maxSubpix - the number of Subpixel used for encryption
	 */
	public Pixel(int maxFoils, int maxSubpix) {
		m_maxFoil = maxFoils;
		m_maxSubpixel = maxSubpix;
		
		m_subPixel = new int[m_maxFoil][m_maxSubpixel];
//		m_overlaySubPixel = new int[m_maxSubpixel];
//		for(int i=0; i< m_maxSubpixel; i++){
//			m_overlaySubPixel[i]=0;
//		}
	}
	/** sets the color of the original Pixel
	 */
	public void setColor(int setColor) {
		m_color = setColor;
		return;
	}
	/** returns the color of the original Pixel
	 * 
	 * @return the color
	 */
	public int getColor() {
		return m_color;
	}

	/**Matrix used to encrypt this pixel
	 * 
	 * @param newMatrix - the given (permutated) Matrix for Encryption
	 */
	public void setMatrix(IntMatrix newMatrix) {
		m_cryptMatrix = newMatrix;
		return;
	}

	/** computes Subpixel from the given Matrix
	 * and stores it in subPixel[][]
	 */
	public void computeSubpixels() {
		// foils
		for (int i = 0; i < m_maxFoil; i++) {// subpixel per foil
			for (int j = 0; j < m_maxSubpixel; j++) {
				m_subPixel[i][j] = m_cryptMatrix.getElement(i, j);
			}
		}
		return;
	}

	/** returns the subPixel at the specified Position
	 * 
	 * @param foilNr - the number of the foil
	 * @param pixelNr - the number of the pixel
	 */
	public int getSubpixel(int foilNr, int pixelNr) {
		return m_subPixel[foilNr][pixelNr];
	}

//	/** returns the Pixel, which was computed by the 
//	 * computeOverlay()
//	 * @param pixelNr
//	 * @return the overlayed subpixel
//	 */
//	public int getOverlaySubpixel(int pixelNr) {
//		return m_overlaySubPixel[pixelNr];
//	}
//	/** computes the overlay of only a set of given foils
//	 * 
//	 * @param set - the number of foils used to compute the overlay
//	 */
//	public void computeOverlay(boolean[] set) {
//		for(int i=0; i<m_overlaySubPixel.length; i++){
//			m_overlaySubPixel[i] = 0;
//		}
//		for(int j=0; j<m_maxSubpixel; j++){
//			for(int i=0; i<m_maxFoil; i++){
//				if(set[i]){
////					negation is important since -1 | -16777276 was -1
////					in other words white or black --> white
//					m_overlaySubPixel[j] |= (~m_subPixel[i][j]);
//				}
//				else{
//					m_overlaySubPixel[j]=m_overlaySubPixel[j];
//				}
//				
//			}//end for 2nd
//			m_overlaySubPixel[j] = ~m_overlaySubPixel[j];
//		}//end for 1st
//	}

	/** helping method to look, whether a element is in an array or not
	 * 
	 * @param set the array where to look in
	 * @param element
	 * @return true if the element was found in the array, false otherwise
	 */
	public boolean isInSet(int[] set, int element){
		for(int i=0; i<set.length; i++){
			if(set[i]==element)return true;
		}
		return false;
	}
}// class Pixel

