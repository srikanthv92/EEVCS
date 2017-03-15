/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;

import java.awt.Image;

/** implements the behaviour of a 2 out of n - scheme with general
 * access
 * @author Boßle Johannes
 */
public class EncGA extends Encryptor {
	/** description of this VCMode*/
	final String DESCRIPTION = "With this scheme you can generate " +
			"3 transparencies, whereas you can specify by the checkboxes " +
			"(beside the original image) which group(s) " +
			"of participants are allowed to decode. For a practical " +
			"application you can consider the example of a president " +
			"and two generals. To fire a missile you need always the " +
			"president and one of the generals. So you would mark [1&nbsp;and&nbsp;2] " +
			"and [1&nbsp;and&nbsp;3] whrerby 1 is the president, 2 and 3 are the generals. " +
			"<br>If all checkboxes are marked, then you get the same result as " +
			"if you did an 2 out of 3 scheme. If no checkbox was selected then " +
			"you get a 3 out of 3 scheme, since 2 participants are not privileged " +
			"to decode.";
	/** store the encryptor to get some of the init-matrices*/
	private Encryptor m_enc = null;
	/** store the access structure for this scheme*/
	private boolean[] m_structure; 
	/** creates an new encryptor object, which has implemented
	 * a general access structure.
	 * @param ga - the access structure as 1and2, 1and3, 2and3
	 * @param height - height of the source pic
	 * @param width - width of the source pic
	 * @param n - number of foils
	 */
	public EncGA(boolean[] ga, int height, int width, int n){
		super(height, width, n);
		System.out.println("Encryptor with GeneralAccess");
		if(ga[0]&&ga[1]&&ga[2]){
			m_enc = new Enc2_n(width, height,3);
			m_initMatrixC0 = m_enc.getInitMatrixC0();
			m_initMatrixC1 = m_enc.getInitMatrixC1();
		} else if((!ga[0])&&(!ga[1])&&(!ga[2])){
			m_enc = new Enc3_3(width, height,3);
			m_initMatrixC0 = m_enc.getInitMatrixC0();
			m_initMatrixC1 = m_enc.getInitMatrixC1();
		} else {
			m_enc = new Enc2_2(width, height,2);
			m_structure = ga;
			m_initMatrixC0 = new IntMatrix(3,4);
			m_initMatrixC1 = new IntMatrix(3,4);
			
			this.setupStartMatrix(this.getCAMatrix());
		}
	}
	/** sets up the encryptor object with a new access structure
	 * 
	 * @param ga - the access structure as 1and2, 1and3, 2and3
	 * @param newImage - the image to load
	 * @return - the new Encryptor-Object with the new matrices for
	 * this access structure
	 */
	public EncGA getEncGAWithNewAccessStructure(boolean [] ga, Image newImage){
//		System.out.println(""+ga[0]+ga[1]+ga[2]);
		EncGA instance = new EncGA(ga,m_wSrc, m_hSrc, 3);
		instance.initEncrypt(newImage);
		instance.encrypt();
		return instance;
	}
	/** generates the init-matrices by the given CAMatrix
	 * 
	 * @param ca - the CAMatrix for this scheme
	 */
	public void setupStartMatrix(int[][] ca){
		int[][] matrixWhite = new int[3][4];
		int[][] matrixBlack = new int[3][4];
		if(ca[0].length==1){
			//only a 3x1-CA-Matrix
			for(int i=0; i<ca.length; i++){
				for(int j=0; j<ca[i].length; j++){
					if(ca[i][j]==1){
						matrixWhite[i] = m_enc.m_initMatrixC0.getRow(j);
						matrixBlack[i] = m_enc.m_initMatrixC1.getRow(j);
						break;
					}else{
						matrixWhite[i] = m_enc.m_initMatrixC0.getRow(j);
						matrixBlack[i] = m_enc.m_initMatrixC1.getRow(j+1);
					}
				}
			}//end for i
		}else{
			int[] noInfo = {WHITEPIXEL,BLACKPIXEL,BLACKPIXEL,WHITEPIXEL};
			for(int i=0; i<ca.length; i++){
				for(int j=0; j<ca[i].length; j++){
					if(ca[i][j]==1){
						//enc.m_initMatrixC0.getRow(j)
						matrixWhite[i] = m_enc.m_initMatrixC0.getRow(j);
						matrixBlack[i] = m_enc.m_initMatrixC1.getRow(j);
						break;//stop when the first 1 was found
					}else{
						matrixWhite[i] = noInfo;
						matrixBlack[i] = noInfo;
					}
				}//for j
			}//for i
		}//end else
		
		m_initMatrixC0.setElements(matrixWhite);
		m_initMatrixC1.setElements(matrixBlack);
	}

	/** generate the the cumulative array matrix
	 * 
	 * @return - the CAMatrix
	 */
	public int[][] getCAMatrix(){
		int[][] ca = null;
		
		if(m_structure[0]){
			if(m_structure[1]){
				if(m_structure[2]){
					//true true true
					//do nothing with ca
				}else{
					//true true false
					ca = new int[3][1];
					for(int i=0; i<ca[0].length; i++){
						ca[i][0] = 0;
					}
					ca[0][0] = 1;
				}
			}else{
				if(m_structure[2]){
					//true false true
					ca = new int[3][1];
					for(int i=0; i<ca[0].length; i++){
						ca[i][0] = 0;
					}
					ca[1][0] = 1;
				}else{
					//true false false
					ca = new int[3][2];
					for(int i=0; i<ca.length; i++)
						for(int j=0; j<ca[i].length; j++){
							ca[i][j] = 0;
						}
					ca[1][0] = 1;
					ca[0][1] = 1;
				}
			}
		} else {
			if(m_structure[1]){
				if(m_structure[2]){
					//false true true
					ca = new int[3][1];
					for(int i=0; i<ca[0].length; i++){
						ca[i][0] = 0;
					}
					ca[2][0] = 1;
				}else{
					//false true false
					ca = new int[3][2];
					for(int i=0; i<ca.length; i++)
						for(int j=0; j<ca[i].length; j++){
							ca[i][j] = 0;
						}
					ca[0][1] = 1;
					ca[2][0] = 1;
				}
			}else{
				if(m_structure[2]){
					//false false true
					ca = new int[3][2];
					for(int i=0; i<ca.length; i++)
						for(int j=0; j<ca[i].length; j++){
							ca[i][j] = 0;
						}
					ca[1][1] = 1;
					ca[2][0] = 1;
				}else{
					//false false false
					//nothing to do
				}
			}
		}//end if/else m_structure[0]
		
		return ca;
	}

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
	 * @see jb.Encryptor#getFoil(int)
	 */
	public Foil getFoil(int numberOfFoil) {
		int index;
		int tempPix[] = new int[m_hEnc * m_wEnc]; // array for grabbing pic
		
		// copy encrypted pic to array
		for (int y = 0; y < m_hSrc; y++) {
			for (int x = 0; x < m_wSrc; x++) {
				index = 2 * (x + y * m_hEnc);

				tempPix[index] = m_pixels[x][y].getSubpixel(numberOfFoil, 0);
				tempPix[index + 1] = m_pixels[x][y].getSubpixel(numberOfFoil, 1);
				tempPix[index + m_hEnc] = m_pixels[x][y].getSubpixel(numberOfFoil, 2);
				tempPix[index + m_hEnc + 1] = m_pixels[x][y].getSubpixel(numberOfFoil, 3);
			}
		}//end for
		return new Foil(tempPix,m_wEnc, m_hEnc);
	}

//	/**
//	 * @see jb.Encryptor#getOverlayedPic()
//	 */
//	public Foil getOverlayedPic() {
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
//		return new Foil(tempPix, m_wEnc, m_hEnc);
//	}
	
	/**
	 * @see jb.Encryptor#getDescription()
	 */
	public String getDescription() {
		return DESCRIPTION;
	}
}
