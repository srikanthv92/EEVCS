/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;

import java.awt.Image;
import java.awt.image.ImageProducer;

/** class to do the encryption to demonstrate the effect
 * of using the same random key
 * @author Boßle Johannes
 */
public class EncDoubledKey extends Enc2_2 {
	/** description of this VCMode*/
	final String DESCRIPTION = "Shows the effect whenever a key is " +
			"used two times. It generates a random transparency " +
			"serving as a key and then encrypts the right and " +
			"the left image with a simple 2 out of 2 scheme. " +
			"By overlaying the transparencies without the key " +
			"you get an symmetrical difference and can achieve a " +
			"lot of information about the two encrypted pictures. " +
			"<br><b>So what to do?</b><br>" +
			"Only use a random key once (similar to a one-time-pad)";
	/** the encryptor-objects*/
	private Enc2_2 m_enc2;
	/** the images for the vc*/
	private Image m_loadImage1,m_loadImage2;
	/** creates a new DoubledKey-Object
	 * 
	 * @param newImage1 - left image
	 * @param newImage2 - right image
	 * @param height - height of the source pic
	 * @param width - width of the source pic
	 * @param n - number of foils to use
	 */
	public EncDoubledKey(Image newImage1, Image newImage2, int height, int width, int n){
		super(height,width,n);
		m_enc2 = new Enc2_2(height, width, n);
		m_loadImage1 = newImage1;
		m_loadImage2 = newImage2;
		this.initEncrypt();
	}

	/** prepares the encryption by calling @link{#setImage(Image)}
	 * and performs @link{#encrypt()}, overrides #initEncrypt()
	 */
	public boolean initEncrypt(){
		m_enc2.doPermutation();
		m_enc2.setImage(m_loadImage2);
		m_enc2.encrypt();
		
		//this.doPermutation();
		m_Cblack = m_enc2.m_Cblack;
		m_Cwhite = m_enc2.m_Cwhite;
		this.setImage(m_loadImage1);
		this.encrypt();
		
		return true;
	}	
	/** Returns the second rastered original image.
	 * 
	 * @return ImageProducer for the second original image
	 */
	public ImageProducer getImageProducerSource(boolean second){
		return m_enc2.m_sourceFoil.getImage();
	}
	
	/** generates the foil with the result by overlaying some
	 * foils. the foil is stored in m_resultFoil.
	 * @param set - the number of foils, which shall be overlayed
	 * beginning with 0, stored as boolean[] 
	 */
	public void overlayFoils(boolean[] set) {
//		System.out.println("   encryptor doubledKey: overlay some foils");
		if(set!=null){
			if(set[0]&&set[1]&&set[2]){
				//compute the special overlay for two foils with tow encrypted pics
				m_resultFoil = this.getFoil(2).computeOverlayOfTwoFoils(this.getFoil(1));
				m_resultFoil = m_resultFoil.computeOverlayOfTwoFoils(this.getFoil(0));
			}else if(set[1]&&set[2]){
				m_resultFoil = this.getFoil(2).computeOverlayOfTwoFoils(this.getFoil(1));
			}
			else if(set[0]&&set[1]){
				m_resultFoil = this.getFoil(0).computeOverlayOfTwoFoils(this.getFoil(1));
			}else if(set[0]&&set[2]){
				m_resultFoil = this.getFoil(0).computeOverlayOfTwoFoils(this.getFoil(2));
			}
			else{
				if(set[0]){
					m_resultFoil = this.getFoil(0);
				}else if(set[1]){
					m_resultFoil = this.getFoil(1);
				}else if(set[2]){
					m_resultFoil = this.getFoil(2);
				}else{
					boolean[] b = new boolean[2];
					b[0]=false;b[1]=false;
					m_resultFoil = this.getFoil(0).computeOverlayOfFoils(this.getFoil(0),b);
				}
			}
		}//set!=null
		if(m_resultFoil==null){
			m_resultFoil = new Foil(new int[m_wEnc*m_hEnc], m_wEnc, m_hEnc);
		}
	}
	
	/**
	 * @see jb.Encryptor#getFoil(int)
	 */
	public Foil getFoil(int foilNr) {
		if (foilNr==1){
			return super.getFoil(1);
		}else if(foilNr==2){
			return m_enc2.getFoil(1);
		}else{
			return super.getFoil(0);
		}
	}

	/** sets up the pixels by giving a matrix and the original colour  
	 * 
	 * @param tempPix
	 */
	public void setMatrixToPixel(int[] tempPix){
		// store grabbed image for encryption
//		System.out.println("   encryptor doubledKey: setting matrix to each Pixel");
		int index=0;
		
		for (int y = 0; y < m_hSrc; y++) {
			for (int x = 0; x < m_wSrc; x++) {
				m_pixels[x][y].setColor(tempPix[x + y * m_hSrc]);
				int sequence = ((Integer)(m_enc2.m_sequence.get(index++))).intValue();
				//System.out.println(sequence + " " + index);
				if (tempPix[x + y * m_hSrc] == WHITEPIXEL) {
					m_pixels[x][y].setMatrix(m_Cwhite[sequence]);
				} else {
					m_pixels[x][y].setMatrix(m_Cblack[sequence]);
				}
			}
		}//end for - store
	}//end prepareMatrix
	/**
	 * @see jb.Encryptor#getDescription()
	 */
	public String getDescription() {
		return DESCRIPTION;
	}
}
