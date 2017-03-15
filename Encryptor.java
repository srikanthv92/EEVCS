/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/** abstract class for doing encryption with visual cryptography and
 * for getting the result of the foils
 * @author Boßle Johannes
 *  
 */
public abstract class Encryptor {
	/**Pixel colors - represented as byte alpha | byte red | byte green | byte blue*/
	final int WHITEPIXEL = (255 << 24) | (255 << 16) | (255 << 8) | 255;
	final int BLACKPIXEL = (255 << 24);
	/** the threshold of this scheme
	 * used to decide the pixel color*/
	final int THRESHOLD = 128;
	
	/** Instance of SecureRandom()*/
	Random m_rnd = new SecureRandom();

	/** height source pic*/
	int m_hSrc;
	/** width of the source pic*/
	int m_wSrc;
	/** height of the encrypted and result pic*/
	int m_hEnc; 
	/** width of the encrypted and result pic*/
	int m_wEnc;

	/** Pixel[][] to store the pixels of a pic. Each Pixel 
	 * contains several Subpixel on several Foils
	 * represented by an IntMatrix
	 */
	Pixel m_pixels[][];

	/** Vector m_foils to store the encrypted Pics*/
	Vector m_foils;
	/** store the Foil with the source*/
	Foil m_sourceFoil;
	/** store the Foil with the result*/
	Foil m_resultFoil;
	/** ArrayList to store the sequence of the random*/
	ArrayList m_sequence;
	
	/** the number of foils used for this scheme
	 * it is initialized by the constructor
	 */
	int m_maxFoil;
	/** the number of subpixel used for a concrete scheme
	 * it is initialized by the abstract method
	 * @link{Encryptor#getMaxSubpixel()}
	 */
	int m_maxSubpixel;
	/** the number of the permutations which are possible
	 * it is initialized by the constructor in dependeny of 
	 * the instance of permutation
	 */
	int m_maxPerm;
	/** the instance of the permutation*/
	Permutation m_permutation;
	
	/** store the init-matrices*/
	IntMatrix m_initMatrixC0, m_initMatrixC1;
	/** store the permuted init-matrices as an Array for black and white*/ 
	IntMatrix m_Cblack[], m_Cwhite[]; 
	
	/**
	 * Constructs an ImageEncrypter with given dimensions and
	 * the number of foils
	 * 
	 * @param height - height of image
	 * @param width - width of image
	 * @param maxFoil - number of foils
	 */
	public Encryptor(int height, int width, int maxFoil) {
//		System.out.println("   Encryptor: init with " + maxFoil);
		m_maxFoil = maxFoil;
		//set dimensions
		m_hSrc = height;
		m_wSrc = width;
		m_hEnc = this.getFactorHeight() * m_hSrc;
		m_wEnc = this.getFactorWidth() * m_wSrc;
		//init the Vector and the ArrayList
		m_foils = new Vector();
		m_sequence = new ArrayList();
		m_maxSubpixel = this.getMaxSubpixel();
		m_permutation = this.getPermutationInstance();
		m_maxPerm = m_permutation.getTotal();
	}
	/** returns a Description of this VisualCryptography
	 * mode
	 * @return - description of this mode
	 */
	public abstract String getDescription();
	/** returns the number of the foils used for this scheme
	 * 
	 * @return number of the foils
	 */
	public abstract int getMaxSubpixel();
	/** returns an instance of Permutation
	 * 
	 * @return a instance of Permutation
	 */
	public abstract Permutation getPermutationInstance();
	/** returns a factor, alternatively how many subpixel 
	 * there are in a row
	 * 
	 * @return number of subpixel per row
	 */
	public abstract int getFactorWidth();
	/** returns a factor, alternatively how many subpixel
	 * there are in a column
	 * @return number of subpixel in a column
	 */
	public abstract int getFactorHeight();
	
	/** extracts a encrypted pic from the Pixel[] and 
	 * wraps it in a Foil
	 * @param numberOfFoil which foil to extract
	 * @return a foil with an encrypted pic
	 */
	public abstract Foil getFoil(int numberOfFoil);
	
	/** prepares the encryption by calling @link{#setImage(Image)}
	 * and performs @link{#encrypt()}
	 * 
	 * @param newImage the image to encrypt
	 */
	public boolean initEncrypt(Image newImage){
		this.doPermutation();
		this.setImage(newImage);
		return true;
	}
	/** method to create all possible permutations of a
	 * init matrix
	 *
	 */
	public void doPermutation(){
		m_Cwhite = new IntMatrix[m_maxPerm];
		m_Cblack = new IntMatrix[m_maxPerm];
		
		int[][] orderArray = m_permutation.getPermArray();
		
		Vector c0 = m_initMatrixC0.getPermMatrixVector(orderArray);
		Vector c1 = m_initMatrixC1.getPermMatrixVector(orderArray);
		
		for(int i=0; i<m_maxPerm; i++){
			m_Cwhite[i] = (IntMatrix)c0.get(i);
			m_Cblack[i] = (IntMatrix)c1.get(i);
		}
	}

	/** prepares an given image to be encrypted
	 * it calls a PixelGrabber and grabs the pixel of
	 * the image. There are done some further steps
	 * to encrypt a picture.
	 * 
	 * @param newImage - the image to encrypt
	 */
	public void setImage(Image newImage){
		int[] tempPix = new int[m_hSrc * m_wSrc]; // array for grabbing pic
		this.grabImage(newImage, tempPix,m_wSrc,m_hSrc);
		//init the pixels
		m_pixels = new Pixel[m_hSrc][m_wSrc];
		for (int y = 0; y < m_hSrc; y++) {
			for (int x = 0; x < m_wSrc; x++) {
				m_pixels[x][y] = new Pixel(m_maxFoil, m_maxSubpixel);
			}
		}
		m_sourceFoil = new Foil(tempPix,m_wSrc,m_hSrc);
	}
	
	/** Grabs the pixel of an given image to the given int[]
	 * 
	 * @param newImage - the image to grab
	 * @param tempPix - the int[] to store the rastered pic
	 * @return tempPix - with the rastered pic
	 */
	public void grabImage(Image newImage, int[] tempPix, int width, int height) {
		// read picture and copy to array
		System.out.println("   Encryptor: grabbing image");
		PixelGrabber pixelGrabber = new PixelGrabber(newImage, 0, 0, width, height,
				tempPix, 0, width);
		try {
			//System.out.println(pixelGrabber.getColorModel()+"");
			pixelGrabber.grabPixels();
		} catch (InterruptedException e) {
			System.err.println("   Error: interrupted waiting for pixels");
		}
		if ((pixelGrabber.getStatus() & ImageObserver.ABORT) != 0) {
			System.err.println("   Error: image fetch aborted or errored");
		}
	}
	
	/** sets up the pixels by giving a matrix and the original colour  
	 * 
	 * @param tempPix
	 */
	public void setMatrixToPixel(int[] tempPix){
		// store grabbed image for encryption
//		System.out.println("   Encryptor: setting matrix to each Pixel");
		for (int y = 0; y < m_hSrc; y++) {
			for (int x = 0; x < m_wSrc; x++) {
				int pixel = tempPix[x + y * m_hSrc];
				m_pixels[x][y].setColor(pixel);
				
				int red   = (pixel >> 16) & 0xff;
		        int green = (pixel >>  8) & 0xff;
		        int blue  = (pixel      ) & 0xff;
		        int factor = (int) (red * 0.299 + green * 0.587 + blue * 0.114);
				
				if (factor >THRESHOLD) {
					m_pixels[x][y].setMatrix(m_Cwhite[this.getRandom()]);
				} else {
					m_pixels[x][y].setMatrix(m_Cblack[this.getRandom()]);
				}
			}
		}//end for - store
	}//end prepareMatrix

	/**
	 * Encrypts the image and saves the encrypted images
	 * in a vector named m_foils
	 *  
	 */
	public void encrypt() {
//		System.out.println("   Encryptor: encrypting");
		int[] tempPix = m_sourceFoil.getGrabbedImage();
		this.setMatrixToPixel(tempPix);
		this.computeSubpixel();
		m_foils.clear();
		m_foils = getEncryptedFoils();
	}
	
	/** stores all foils with encrypted pics to
	 * a vector and returns it
	 * @return a vector with the encrypted pic foils
	 */
	private Vector getEncryptedFoils(){
		Vector v = new Vector();
		for(int i = 0; i<m_maxFoil; i++){
			v.add(i,this.getFoil(i));
		}
		return v;
	}
	/** computes the subpixels, calls for each subpixel
	 * the @link{Pixel#computeSubpixel()}
	 *
	 */ 
	public void computeSubpixel(){
		for (int y = 0; y < m_hSrc; y++) {
			for (int x = 0; x < m_wSrc; x++) {
				m_pixels[x][y].computeSubpixels();
			}
		}
	}

	/**
	 * Returns a random number, which is a valid number of a permutation.
	 * This means that, it is 0 <= rnd < m_maxPerm
	 */
	public int getRandom() {
		int random = java.lang.Math.abs(m_rnd.nextInt()) % m_maxPerm;
		m_sequence.add(new Integer(random));
		return random;
	}

	/** generates the foil by directly computing the
	 * overlay of the foils
	 * @param set - the number of foils which shall be overlayed
	 */
	public void overlayFoils(boolean[] set){
		Foil result = null;
		if(set != null){
			for(int i=0; i<set.length && i<m_maxFoil; i++){
				if(set[i]){
					Foil foil = (Foil)m_foils.get(i);
					if(result==null){
						result = foil;
					}else{
						result = result.computeOverlayOfTwoFoils(foil);
					}
				}
			}
		}
		if(result==null){
			result = new Foil(new int[m_wEnc*m_hEnc], m_wEnc, m_hEnc);
		}
		m_resultFoil = result;
	}
	/** Returns the rastered original image.
	 * 
	 * @return ImageProducer for the original image
	 */
	public ImageProducer getImageProducerSource(){
		return m_sourceFoil.getImage();
	}

	/** Returns the image of the encrypted foils.
	 * 
	 * @param foilNr - number of foil to return
	 * @return ImageProducer for the foil image
	 */
	public ImageProducer getImageProducerEncrypted(int foilNr) {
//		Foil f = (Foil)m_foils.get(foilNr);
		Foil f = this.getFoil(foilNr);
		return f.getImage();
	}
	
	/** Returns the image of the encrypted foils.
	 * 
	 * @param foilNr - number of foil to return
	 * @return ImageProducer for the foil image
	 */
	public ImageProducer getImageProducerEncryptedFromVector(int foilNr) {
		Foil f = (Foil)m_foils.get(foilNr);
		return f.getImage();
	}
	
	/** Returns the image of the overlayed foils.
	 * 
	 * @return ImageProducer for the overlayed foils image
	 */	
	public ImageProducer getImageProducerOverlay(){
		return m_resultFoil.getImage();
	}
	
	/**
	 * @return Returns the height of the encrypted Pic
	 */
	public int getHeightEnc() {
		return m_hEnc;
	}
	/**
	 * @return Returns the width of the encrypted Pic
	 */
	public int getWidthEnc() {
		return m_wEnc;
	}

	/**
	 * @return Returns the m_initMatrixC0.
	 */
	public IntMatrix getInitMatrixC0() {
		return m_initMatrixC0;
	}
	/**
	 * @return Returns the m_initMatrixC1.
	 */
	public IntMatrix getInitMatrixC1() {
		return m_initMatrixC1;
	}
	/** sets the given vector to the vector which is used
	 * to save the foils for decryption
	 * @param foils - the new loaded Foils
	 */
	public void setM_Foils(Vector foils){
		m_foils = foils;
	}
	/** 
	 * @return m_foils - the vector with the encrypted foils
	 */
	public Vector getM_Foils(){
		return m_foils;
	}
}
