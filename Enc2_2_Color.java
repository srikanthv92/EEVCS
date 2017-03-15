/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;

import java.util.Vector;

/** Encryptor - Object for the colored VisualCryptography<br/>
 * The implemented scheme was introduced by Yang and Laih 2000<br/>
 * It works by defining some colors and deleting some colors through the
 * overlaying process, whenever a black pixel is shown.<br/>
 * for example: red and black = black; red and red = red;
 * @author Boßle Johannes
 *  
 */
public class Enc2_2_Color extends Encryptor {
	/** color definition as int for a red pixel*/
	final static int REDPIXEL = (255 << 24) | (255 << 16);
	/** color definition as int for a green pixel*/
	final static int GREENPIXEL = (255 << 24) |(255 << 8);
	/** color definition as int for a blue pixel*/
	final static int BLUEPIXEL = (255 << 24) | 255;
//	/** color definition as int for a white pixel*/
//	final static int WHITEPIXEL = (255 << 24) | (255 << 16) | (255 << 8) | 255;
//	/** color definition as int for a black pixel*/
//	final static int BLACKPIXEL = (255 << 24);
	/** description of this VCMode*/
	final String DESCRIPTION = "creates two transparencies for the visual " +
			"cryptography with colors. The original picture is " +
			"taken and for each pixel is decided which elementary " +
			"color (red, green, blue or black) it has. Each pixel " +
			"contains 1 red, 1 green, 1 blue and 3 black subpixel. " +
			"By overlaying the transparencies the black color dominates " +
			"all others and is shown. For displaying a concrete color " +
			"(for example red) all subpixel except the red one are " +
			"overlayed with an black subpixel. The red pixel is overlayed " +
			"with a red subpixel. So you achieve 5 black " +
			"and a red subpixel, and for the human visual system " +
			"it appears as a (dark)red pixel.";
	/** the threshold of this scheme
	 * used to decide the pixel color*/
	final int THRESHOLD = 64;
	
	/** Color-Matrices*/
	IntMatrix m_initMatrixRed,m_initMatrixGreen,m_initMatrixBlue,m_initMatrixWhite,m_initMatrixBlack;
	/** Array of colored Matrices*/
	IntMatrix[] m_Red, m_Green, m_Blue, m_White, m_Black;
	/** creates a new encryptor object for the colored VC<br/>
	 *  mainly it sets up the init-matrices for this scheme
	 * 
	 * @param height - height of the source pic
	 * @param width - width of the source pic
	 * @param n - number of foils
	 */
	public Enc2_2_Color(int height, int width, int n){
		super(height, width, 2);
		m_initMatrixRed = new IntMatrix(m_maxFoil, m_maxSubpixel);
		m_initMatrixGreen = new IntMatrix(m_maxFoil, m_maxSubpixel);
		m_initMatrixBlue = new IntMatrix(m_maxFoil, m_maxSubpixel);
		m_initMatrixWhite = new IntMatrix(m_maxFoil, m_maxSubpixel);
		m_initMatrixBlack = new IntMatrix(m_maxFoil, m_maxSubpixel);
		
		//init red
		m_initMatrixRed.setElement(0,0,REDPIXEL);
		m_initMatrixRed.setElement(0,1,GREENPIXEL);
		m_initMatrixRed.setElement(0,2,BLUEPIXEL);
		m_initMatrixRed.setElement(0,3,BLACKPIXEL);
		m_initMatrixRed.setElement(0,4,BLACKPIXEL);
		m_initMatrixRed.setElement(0,5,BLACKPIXEL);
		m_initMatrixRed.setElement(1,0,REDPIXEL);
		m_initMatrixRed.setElement(1,1,BLACKPIXEL);
		m_initMatrixRed.setElement(1,2,BLACKPIXEL);
		m_initMatrixRed.setElement(1,3,BLACKPIXEL);
		m_initMatrixRed.setElement(1,4,GREENPIXEL);
		m_initMatrixRed.setElement(1,5,BLUEPIXEL);
		//init green
		m_initMatrixGreen.setElement(0,0,REDPIXEL);
		m_initMatrixGreen.setElement(0,1,GREENPIXEL);
		m_initMatrixGreen.setElement(0,2,BLUEPIXEL);
		m_initMatrixGreen.setElement(0,3,BLACKPIXEL);
		m_initMatrixGreen.setElement(0,4,BLACKPIXEL);
		m_initMatrixGreen.setElement(0,5,BLACKPIXEL);
		m_initMatrixGreen.setElement(1,0,BLACKPIXEL);
		m_initMatrixGreen.setElement(1,1,GREENPIXEL);
		m_initMatrixGreen.setElement(1,2,BLACKPIXEL);
		m_initMatrixGreen.setElement(1,3,BLACKPIXEL);
		m_initMatrixGreen.setElement(1,4,REDPIXEL);
		m_initMatrixGreen.setElement(1,5,BLUEPIXEL);
		//init blue
		m_initMatrixBlue.setElement(0,0,REDPIXEL);
		m_initMatrixBlue.setElement(0,1,GREENPIXEL);
		m_initMatrixBlue.setElement(0,2,BLUEPIXEL);
		m_initMatrixBlue.setElement(0,3,BLACKPIXEL);
		m_initMatrixBlue.setElement(0,4,BLACKPIXEL);
		m_initMatrixBlue.setElement(0,5,BLACKPIXEL);
		m_initMatrixBlue.setElement(1,0,BLACKPIXEL);
		m_initMatrixBlue.setElement(1,1,BLACKPIXEL);
		m_initMatrixBlue.setElement(1,2,BLUEPIXEL);
		m_initMatrixBlue.setElement(1,3,BLACKPIXEL);
		m_initMatrixBlue.setElement(1,4,REDPIXEL);
		m_initMatrixBlue.setElement(1,5,GREENPIXEL);		
		
		//init white
		m_initMatrixWhite.setElement(0,0,REDPIXEL);
		m_initMatrixWhite.setElement(0,1,GREENPIXEL);
		m_initMatrixWhite.setElement(0,2,BLUEPIXEL);
		m_initMatrixWhite.setElement(0,3,BLACKPIXEL);
		m_initMatrixWhite.setElement(0,4,BLACKPIXEL);
		m_initMatrixWhite.setElement(0,5,BLACKPIXEL);
		m_initMatrixWhite.setElement(1,0,BLACKPIXEL);
		m_initMatrixWhite.setElement(1,1,BLACKPIXEL);
		m_initMatrixWhite.setElement(1,2,BLACKPIXEL);
		m_initMatrixWhite.setElement(1,3,REDPIXEL);
		m_initMatrixWhite.setElement(1,4,GREENPIXEL);
		m_initMatrixWhite.setElement(1,5,BLUEPIXEL);
		
		//init black
		m_initMatrixBlack.setElement(0,0,REDPIXEL);
		m_initMatrixBlack.setElement(0,1,GREENPIXEL);
		m_initMatrixBlack.setElement(0,2,BLUEPIXEL);
		m_initMatrixBlack.setElement(0,3,BLACKPIXEL);
		m_initMatrixBlack.setElement(0,4,BLACKPIXEL);
		m_initMatrixBlack.setElement(0,5,BLACKPIXEL);
		m_initMatrixBlack.setElement(1,0,REDPIXEL);
		m_initMatrixBlack.setElement(1,1,GREENPIXEL);
		m_initMatrixBlack.setElement(1,2,BLUEPIXEL);
		m_initMatrixBlack.setElement(1,3,BLACKPIXEL);
		m_initMatrixBlack.setElement(1,4,BLACKPIXEL);
		m_initMatrixBlack.setElement(1,5,BLACKPIXEL);
	}

	/** sets up the pixels by giving a matrix and the original colour  
	 * 
	 * @param tempPix
	 */
	public void setMatrixToPixel(int[] tempPix){
		// store grabbed image for encryption
		System.out.println("   encryptor special color: setting matrix to each Pixel");
		for (int y = 0; y < m_hSrc; y++) {
			for (int x = 0; x < m_wSrc; x++) {
				int index = tempPix[x + y * m_hSrc];
				m_pixels[x][y].setColor(index);
				int decision = this.decideColor(index);
				switch(decision){
					/*case WHITEPIXEL:
						m_pixels[x][y].setMatrix(m_White[this.getRandom()]);
						break;*/
					case REDPIXEL: 
						m_pixels[x][y].setMatrix(m_Red[this.getRandom()]);
						break;
					case GREENPIXEL: 
						m_pixels[x][y].setMatrix(m_Green[this.getRandom()]);
						break;
					case BLUEPIXEL: 
						m_pixels[x][y].setMatrix(m_Blue[this.getRandom()]);
						break;
					/*case BLACKPIXEL: 
						m_pixels[x][y].setMatrix(m_Black[this.getRandom()]);
						break;*/
				}
			}
		}//end for - store
	}//end prepareMatrix
	
	/** decide which elementary color this pixel has
	 * 
	 * @param pixel - the original pixel color
	 * @return - the elementary color which can be encoded
	 */
	public int decideColor(int pixel){
		int result=0;
		int red   = (pixel >> 16) & 0xff;
        int green = (pixel >>  8) & 0xff;
        int blue  = (pixel      ) & 0xff;
        
        if((red<THRESHOLD)&&(green<THRESHOLD)&&(blue<THRESHOLD)){
        	result = WHITEPIXEL;
        }else if((red>3*THRESHOLD) && (green>3*THRESHOLD) && (blue>3*THRESHOLD)){
        	result = BLACKPIXEL;
        }else if((red > 2*THRESHOLD) && (red> green) && (red>blue)){
        	result = REDPIXEL;
        }else if((green > 2*THRESHOLD) && (green>red) && (green>blue)){
        	result = GREENPIXEL;
        }else if((blue > 2*THRESHOLD) && (blue>green) && (blue>red)){
        	result = BLUEPIXEL;
        }else{
        	result = WHITEPIXEL;
        }
		return result;
	}
	/** method to create all possible permutations of a
	 * init matrix
	 *
	 */
	public void doPermutation(){
		m_White = new IntMatrix[m_maxPerm];
		m_Black = new IntMatrix[m_maxPerm];
		m_Red = new IntMatrix[m_maxPerm];
		m_Green = new IntMatrix[m_maxPerm];
		m_Blue = new IntMatrix[m_maxPerm];

		int[][] orderArray = m_permutation.getPermArray();
		
		Vector c0 = m_initMatrixWhite.getPermMatrixVector(orderArray);
		Vector c1 = m_initMatrixBlack.getPermMatrixVector(orderArray);
		Vector c2 = m_initMatrixRed.getPermMatrixVector(orderArray);
		Vector c3 = m_initMatrixGreen.getPermMatrixVector(orderArray);
		Vector c4 = m_initMatrixBlue.getPermMatrixVector(orderArray);
		
		for(int i=0; i<m_maxPerm; i++){
			m_White[i] = (IntMatrix)c0.get(i);
			m_Black[i] = (IntMatrix)c1.get(i);
			m_Red[i] = (IntMatrix)c2.get(i);
			m_Green[i] = (IntMatrix)c3.get(i);
			m_Blue[i] = (IntMatrix)c4.get(i);
			
		}
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
	 * @see jb.Encryptor#getDescription()
	 */
	public String getDescription() {
		return DESCRIPTION;
	}
}
