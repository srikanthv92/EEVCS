/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;

/** holds the data of the shown transparencies
 * @author Boßle Johannes
 */
public class Foil {
	/** the m_imageData buffer where the int[] of a picture is stored*/
	int[] m_imageData;
	/**width of the pcture*/
	int m_width;
	/**height of the pcture*/
	int m_height;

	/** creates a new Foil with the given parameters
	 * 
	 * @param tempPix - the m_imageData-data
	 * @param w - width of the pic
	 * @param h - height of the pic
	 */
	Foil(int[] tempPix, int w, int h){
		m_width = w;
		m_height = h;
		m_imageData = tempPix;
	}
	/** returns the m_imageData-data
	 * 
	 * @return - the raw grabbed m_imageData data, like from a PixelGrabber
	 */
	public int[] getGrabbedImage(){
		return m_imageData;
	}
	/** returns an ImageProducer for a pic with the
	 * parameters associated to this foil
	 * @return the ImageProducer for this foil
	 */
	public ImageProducer getImage(){
		return new MemoryImageSource(m_width, m_height, m_imageData, 0, m_width);
	}
	/** computes the overlay of two foils
	 * 
	 * @param foil - the foil for which to compute the overlay with this
	 * foil
	 * @return - the new overlayed foil
	 */
	public Foil computeOverlayOfTwoFoils(Foil foil){
		int[] pic1 = foil.getGrabbedImage();
		int[] pic2 = this.getGrabbedImage();
		int[] result = new int[this.m_imageData.length];
		for(int i=0; i<pic1.length; i++){
			result[i] = (~pic1[i])|(~pic2[i]);
			result[i] = (~result[i]);
		}	
		return new Foil(result, this.getWidth(), this.getHeight());
	}
	/** computes the overlay of two foils
	 * 
	 * @param foil - the foil for which to compute the overlay with this
	 * foil
	 * @param set - the structure for overlaying
	 * @return - the new overlayed foil
	 */
	public Foil computeOverlayOfFoils(Foil foil, boolean[] set){
		int[] pic1 = foil.getGrabbedImage();
		int[] pic2 = this.getGrabbedImage();
		int[] result = new int[this.m_imageData.length];
		
		if(set[0]&&set[1]){
			for(int i=0; i<pic1.length; i++){
				result[i] = (~pic1[i])|(~pic2[i]);
				result[i] = (~result[i]);
			}	
		} else if(set[0] && !set[1]){
			result = pic1;
		} else if(!set[0] && set[1]){
			result = pic2;
		}

		return new Foil(result, this.getWidth(), this.getHeight());
	}

	/**
	 * @return Returns the height.
	 */
	public int getHeight() {
		return m_height;
	}
	/**
	 * @return Returns the width.
	 */
	public int getWidth() {
		return m_width;
	}
	/** possibilty to get a represantation of this foil as a String
	 * gives the back the raw-data of the saved picture
	 * @return the String-Representation
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		for(int i=0; i<m_imageData.length; i++){
			sb.append(m_imageData[i]+" ");
		}
		
		return sb.toString();
	}
}
