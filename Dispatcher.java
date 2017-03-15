/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jb;

import gui.ImageIconLoader;

import java.awt.Image;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/** Dispatcher takes over the control of the VCApplet
 * @author Boßle Johannes
 *  
 */
public class Dispatcher implements Runnable{
	/** the Applet which has initiciated this Dispatcher*/
	VCApplet m_applet;
	/** the Thread which is needed while waiting*/
	Thread m_thread;
	/** timout set for waiting for a picture*/
	int m_timeout = 1000;
	/** the image to load*/
	Image m_loadImage, m_loadImage2;
	/** the Encryptor, which does the encryption*/
	Encryptor m_encryptor;
	/** Constant for the type recognition of the EncryptionType
	 * Here it is a 2 out of 2 Scheme
	 */	
	final static int VCTYPE_2_2 = 0;
	/** Constant for the type recognition of the EncryptionType
	 * Here it is a 3 out of 3 Scheme
	 */	
	final static int VCTYPE_3_3 = 1;
	/** Constant for the type recognition of the EncryptionType
	 * Here it is a 2 out of n Scheme, with n<=5
	 */	
	final static int VCTYPE_2_N = 2;
	/** Constant for the type recognition of the EncryptionType
	 * Here it is a 3 out of n Scheme, with n<=5
	 */	
	final static int VCTYPE_3_N = 3;
	/**the width of the pic that is used*/
	final int WIDTH = 100;
	/**the height of the pic that is used*/
	final int HEIGHT = 100;
	/**the strings of the pics which are used if someone not wants to 
	 * use own pictures
	 */
	final String PIC = "smile.gif";
	final String GREYPIC ="monalisa.jpg";
	final String COLORPIC ="italy.gif";
	/** the second picture to load if someone wants to 
	 * see the same random key effect
	 */
	final String PIC2 = "question.gif";
	
	/** which type of vc to use*/
	int m_vctype;
	/** set the maximum Foils*/
	int m_nrOfFoils;
	/** the number of the current shown encrypted pic
	 * 
	 */
	int m_currentFoil;
	/** wasColorOrGrey signalizes, if there has been another example picture
	 * 
	 */
	boolean wasColorOrGrey = false;
	/** no option specified*/
	final static int NOOPTION = 0;
	/** grey option specified*/
	final static int GREY = 1;
	/** colored VC option specified*/
	final static int COLOR = 2;
	/** same random key option specified*/
	final static int DOUBLEDKEY = 3;
	/** VC with General Access option specified*/
	final static int GENERALACCESS = 4;
	/** hold the specified option*/
	int m_option = NOOPTION;
	
	/** some view elements */
	ImagePanel m_srcImagePanel = new ImagePanel(WIDTH,HEIGHT);
	ImagePanel m_srcImagePanel2 = new ImagePanel(WIDTH,HEIGHT);
	ImagePanel m_encImagePanel = new ImagePanel(WIDTH,HEIGHT,this);
	ImagePanel m_encImagePanel2 = new ImagePanel(WIDTH,HEIGHT,this);
	ImagePanel m_ovlImagePanel = new ImagePanel(WIDTH,HEIGHT,this);

	/**creates a new Dispatcher-Object*/
	public Dispatcher(VCApplet a){
		m_applet = a;
	}
	/**loads an given image
	 * 
	 *
	 */
	public void loadImage(File f){
		// load image
		try{
			if(f==null){
//				change the example pictures for grey and color
				if(m_option==COLOR){
					m_loadImage = ImageIconLoader.loadImageIcon(COLORPIC).getImage();					
				}else if(m_option==GREY){
					m_loadImage = ImageIconLoader.loadImageIcon(GREYPIC).getImage();
				}else{
					m_loadImage = ImageIconLoader.loadImageIcon(PIC).getImage();
//					m_loadImage = ImageIO.read(new URL(m_applet.getCodeBase()+PIC)).getSubimage(0,0,WIDTH,HEIGHT);
				}
				if(m_loadImage==null){
					throw new Exception();
				}
				m_loadImage = m_loadImage.getScaledInstance(WIDTH,HEIGHT,Image.SCALE_SMOOTH);
			} else {
				//try to read out the given file
				m_loadImage = ImageIO.read(f).getScaledInstance(WIDTH,HEIGHT,Image.SCALE_SMOOTH);
//				getSubimage(0,0,WIDTH,HEIGHT);
			} 
		}catch (Exception e) {
			e.printStackTrace(System.out);
			JOptionPane.showMessageDialog(m_applet,"Loading of the image failed.\n" +
					"Please be sure to have specified the right path to the images.\n" +
					"The image will be scaled to 100*100 pixel.","Alert",JOptionPane.ERROR_MESSAGE);
		}
		// wait until image loaded
		System.out.print("Dispatcher: loading image");
		m_thread = java.lang.Thread.currentThread();
		do {
			Thread t = new Thread (this);
			t.run();
		} while (m_loadImage.getHeight(m_applet) < 0 & m_timeout > 0);
		if (m_timeout > 0) {
			this.initNewMode();
		} else {
			System.out.println("Dispatcher: timeout while loading image");
		}
	}
	/** inits a new Mode when a new mode is choosen
	 * chooses the right encryptor class
	 *
	 */
	public void initNewMode(){
		System.out.println("Dispatcher: loading image complete");
		
		if(m_option==GENERALACCESS){
			m_applet.checkBoxPanel.setVisible(true);
		}else{
			m_applet.checkBoxPanel.setVisible(false);
		}
		m_srcImagePanel.setImage(m_loadImage,WIDTH,HEIGHT);
		m_applet.inputPanelLeft.add(m_srcImagePanel2);
//		m_applet.inputPanel.add(srcImagePanel2);
		if(m_option==DOUBLEDKEY){
			m_srcImagePanel2.setVisible(true);
			try{
//				m_loadImage2 = ImageIO.read(new URL(m_applet.getCodeBase()+PIC2)).getSubimage(0,0,WIDTH,HEIGHT);
				m_loadImage2 = ImageIconLoader.loadImageIcon(PIC2).getImage();
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
			m_srcImagePanel2.setImage(m_loadImage2, WIDTH, HEIGHT);
		}
		m_encImagePanel.setImage(null,WIDTH,HEIGHT);
		m_encImagePanel.setVisible(false);
		m_applet.encPanelRight.add(m_encImagePanel2,0);
		m_encImagePanel2.setImage(null,WIDTH,HEIGHT);
		m_encImagePanel2.setVisible(false);				

		m_applet.encPanel.doLayout();

		//this.encPanel.doLayout();
		m_ovlImagePanel.setImage(null,WIDTH,HEIGHT);
		m_ovlImagePanel.setVisible(false);
		m_applet.resultPanel.doLayout();
		
		if (m_loadImage != null) {// encrypt
			System.out.println("Dispatcher: choose Encryptor");
			m_encryptor = null;
			wasColorOrGrey = false;
			switch(m_vctype){
			case VCTYPE_2_2 : 
				if(m_option==COLOR){
					wasColorOrGrey = true;
					m_encryptor = new Enc2_2_Color(WIDTH, HEIGHT,2);
				}else if(m_option==DOUBLEDKEY){
					m_encryptor = new EncDoubledKey(m_loadImage, m_loadImage2, WIDTH, HEIGHT,2);
//					((EncDoubledKey)m_encryptor).initEncrypt();
				}else if(m_option==GREY){
					wasColorOrGrey = true;
					m_encryptor = new Enc2_2_Grey(WIDTH, HEIGHT,2);
				}else{
					m_encryptor = new Enc2_2(WIDTH, HEIGHT,2);
				}
				break;
			case VCTYPE_3_3 : 
				if(m_option==GENERALACCESS){
					m_encryptor = new EncGA(this.getAccessStructure(),WIDTH, HEIGHT,3);
				}else{
					m_encryptor = new Enc3_3(WIDTH, HEIGHT,3);
				}
				break;
			case VCTYPE_2_N : m_encryptor = new Enc2_n(WIDTH, HEIGHT,m_nrOfFoils);
				break;
			case VCTYPE_3_N : m_encryptor = new Enc3_n(WIDTH, HEIGHT,m_nrOfFoils);
				break;
			}
			if(m_option!=DOUBLEDKEY){
				m_encryptor.initEncrypt(m_loadImage);
			}
			
		}
	}
	/** sends the encrypted images to the applet
	 *
	 */
	public void putEncImagesToApplet(){

		//send the images to the applet be sure to have choosen the right images
		m_encryptor.encrypt();
//		System.out.println("Dispatcher: setting new images");
		m_srcImagePanel.setImage(m_applet.createImage(m_encryptor.getImageProducerSource()),WIDTH,HEIGHT);
		m_srcImagePanel.setVisible(false);
		m_srcImagePanel.setVisible(true);
		m_srcImagePanel.validate();
		if(m_option==DOUBLEDKEY){
			m_srcImagePanel2.setImage(m_applet.createImage(((EncDoubledKey)m_encryptor).getImageProducerSource(true)),WIDTH,HEIGHT);
			m_srcImagePanel2.setVisible(false);
			m_srcImagePanel2.setVisible(true);
			m_srcImagePanel2.validate();
		}
		if(m_option==GENERALACCESS){
			m_encryptor = ((EncGA)m_encryptor).getEncGAWithNewAccessStructure(this.getAccessStructure(),m_loadImage);
		}
		if(this.m_nrOfFoils<3 && m_option!=DOUBLEDKEY){
			m_applet.prevNextFoilPanel.setVisible(false);
			m_encImagePanel2.setVisible(true);
			m_encImagePanel2.setImage(m_applet.createImage(m_encryptor.getImageProducerEncrypted(1)),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			m_encImagePanel2.setVisible(false);
			m_encImagePanel2.setVisible(true);
		}else{
			m_applet.prevNextFoilPanel.setVisible(true);
		}
		m_encImagePanel.setImage(m_applet.createImage(m_encryptor.getImageProducerEncrypted(this.getCurrentFoil())),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
		m_encImagePanel.setVisible(false);
		m_encImagePanel.setVisible(true);
		m_applet.inputPanel.validate();
		m_applet.encPanel.validate();
		
		//try to update resultPanel
		this.overlay(m_applet.getSetForAccess());
		m_applet.resultPanel.setVisible(false);
		m_applet.resultPanel.setVisible(true);
	}

	/** causes the Encryptor to overlay the foils and sends it to
	 * the overlayed pic back to the applet
	 * 
	 *
	 */
	public void overlay(boolean[] setForAccess){
		if (m_encryptor != null) {
//			System.out.println("Dispatcher: overlay foils");
			m_encryptor.overlayFoils(setForAccess);
			m_ovlImagePanel.setVisible(true);
			Image img = m_applet.createImage(m_encryptor.getImageProducerOverlay());
			m_ovlImagePanel.setImage(img ,m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			m_ovlImagePanel.setVisible(false);
			m_ovlImagePanel.setVisible(true);
			m_applet.resultPanel.validate();
			m_applet.resultPanel.setVisible(true);
		}
	}
	/** return the SrcCanvas, the Canvas where the sourceFoil
	 * picture can be displayed
	 * 
	 * @return the SourceCanvas
	 */
	public ImagePanel getSrcCanvas(){
		return m_srcImagePanel;
	}
	/** return the encCanvas, the Canvas where the encryptedFoil
	 * picture can be displayed
	 * 
	 * @return the encryptedCanvas
	 */
	public ImagePanel getEncCanvas(){
		return m_encImagePanel;
	}
	/**returns the foil with the specified number as an ImagePanel
	 * 
	 * @param nr - the number of the encrypted picture
	 * @return the ImagePanel with the encrypted pic of foil number nr
	 */
	public ImagePanel getEncCanvas(int nr){
		if(m_option==DOUBLEDKEY){
			m_encImagePanel.setImage(m_applet.createImage(m_encryptor.getImageProducerEncrypted(nr)),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
		}else{
			m_encImagePanel.setImage(m_applet.createImage(m_encryptor.getImageProducerEncryptedFromVector(nr)),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
		}
		m_currentFoil = nr;
		return m_encImagePanel;
	}
	/** return the ResultCanvas, the Canvas where the result
	 * picture can be displayed
	 * 
	 * @return the ResultCanvas
	 */
	public ImagePanel getResultCanvas(){
		return m_ovlImagePanel;
	}
	/** some kind of initialization for entering an new VC_TYPE
	 * for the parameters @see Dispatcher
	 * @param nrFoils - number of Foils used in this scheme
	 * @param option - whether to set an option or not
	 * @param type - the number of the scheme
	 */
	public void newMode(int nrFoils, int option, int type){
		m_applet.inputFlip.flip(false);
		this.reset();
		this.setNrOfFoils(nrFoils);
		this.setOptions(option);
		this.setVctype(type);
		m_applet.enableFunctionsAfterEncrypt(false);
		if(m_option==COLOR || m_option==GREY || wasColorOrGrey){
			this.loadImage(m_applet.m_fileImage);
			//this.initNewMode is called in loadImage
		}else{
			this.initNewMode();
		}
		
	}
	/** sets some values back to their standard value
	 * 
	 *
	 */
	public void reset(){
		System.out.println("Dispatcher: reset");
		if(m_encImagePanel2.isVisible()){
			m_encImagePanel2.setVisible(false);
		}
		if(m_srcImagePanel2.isVisible()){
			m_srcImagePanel2.setVisible(false);
		}
		this.setCurrentFoil(0);
	}
	/** wraps the encryptor.getDescription()
	 * 
	 * @return - the description of the encryptor-object
	 */
	public String getDescription(){
		if(m_encryptor==null){
			System.out.println("encryptor = null");
			return new Enc2_2(WIDTH,HEIGHT,2).getDescription();
		}
		return m_encryptor.getDescription();
	}

	/** Causes a Thread to wait for 100ms while a picture is loaded
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			Thread.sleep(100);
			System.out.print(".");
			m_timeout--;
		} catch (InterruptedException e) {
			// ready
		}
	}
	
	/**
	 * @return Returns the m_nrOfFoils.
	 */
	public int getNrOfFoils() {
		return m_nrOfFoils;
	}
	/**
	 * @param numberoffoils The numberoffoils to set.
	 */
	public void setNrOfFoils(int numberoffoils) {
		m_nrOfFoils = numberoffoils;
	}

	/**
	 * @return Returns the vctype.
	 */
	public int getVctype() {
		return m_vctype;
	}
	/**
	 * @param type the vctype to set. for possible values @see{applet#getParameterinfo()}
	 */
	public void setVctype(int type) {
		m_vctype = type;
		System.out.println("dispatcher: setVCType=" + type);
	}

	/** sets an option to this dispatcher-Object
	 * 
	 * @param option - for possible values @see Dispatcher
	 */
	public void setOptions(int option){
		this.m_option = option;
	}

	/** sends back the specified AccessStructure for this scheme 
	 * only wraps the VCApplet#getAccessStructure()
	 * @return the boolean[] with the access structure
	 */
	public boolean[] getAccessStructure(){
		return m_applet.getAccessStructure();
	}
	/**
	 * @return Returns the m_currentFoil.
	 */
	public int getCurrentFoil() {
		return m_currentFoil;
	}
	/**
	 * @param foil The m_currentFoil to set.
	 */
	public void setCurrentFoil(int foil) {
		m_currentFoil = foil;
	}
	/** loads the encrypted foils
	 * 
	 * @param files - the encrypted foils to load
	 * @return - true if successful, false otherwise
	 */
	public boolean loadAsEncFoil(File[] files){
		boolean success = false;
		for(int i=0; i<files.length; i++){
			if(files[i]==null)return false;
		}
		try{
			int width = m_encryptor.getWidthEnc();
			int height = m_encryptor.getHeightEnc();
			Vector v = m_encryptor.getM_Foils();
			v.clear();
			for(int i=0; i<files.length; i++){
				Image image = ImageIO.read(files[i]).getSubimage(0,0,width,height);
				int[] tempPix = new int[width * height];
				m_encryptor.grabImage(image,tempPix,width,height);
				Foil f = new Foil(tempPix,width,height);
				v.add(i,f);
			}
			//set the images to the Applet
			if(this.m_nrOfFoils<3){
				m_applet.prevNextFoilPanel.setVisible(false);
				m_encImagePanel2.setVisible(true);
				Image im = m_applet.createImage(m_encryptor.getImageProducerEncryptedFromVector(1));
				m_encImagePanel2.setImage(im,m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			}else{
				m_applet.prevNextFoilPanel.setVisible(true);
			}
			Image img = m_applet.createImage(m_encryptor.getImageProducerEncryptedFromVector(0));
			m_encImagePanel.setImage(img,m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			m_applet.inputPanel.validate();
			m_applet.encFlip.flip(true);
			m_applet.encFlip.flip(false);
			
			m_applet.encPanel.validate();
			success = true;
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		return success;
	}
	
	/** method to save the current foil as a picture
	 * 
	 *
	 */
	public void saveCurrent(){
		if(m_ovlImagePanel.isMarked()){
			m_ovlImagePanel.save();
			return;
		}
		if(this.getNrOfFoils()==2 && m_option!=DOUBLEDKEY){
			//which foil is marked
			if(m_encImagePanel.isMarked() && !m_encImagePanel2.isMarked()){
				m_encImagePanel.save();
			}else if(!m_encImagePanel.isMarked() && m_encImagePanel2.isMarked()){
				m_encImagePanel2.save();
			}else{
				JOptionPane.showMessageDialog(m_applet,"Please select one transparency by left click with the mouse" +
						"\nor do a right click on the transparency to save directly.","Warning",JOptionPane.WARNING_MESSAGE);
			}
			
		}else{
			m_encImagePanel.save();
		}
		m_applet.markCurrentShowButton(m_applet.SELECTEDCOLOR);
	}
	/** method to save all foils as pictures
	 * 
	 *
	 */
	public void saveAll(){
		int nr = this.getNrOfFoils();
		if(m_option == Dispatcher.DOUBLEDKEY){
			nr++;
			for(int i=0; i<nr; i++){
				m_encImagePanel.setImage(m_applet.createImage(m_encryptor.getImageProducerEncrypted(i)),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
				m_encImagePanel.save();
			}
			m_encImagePanel.setImage(m_applet.createImage(m_encryptor.getImageProducerEncrypted(this.getCurrentFoil())),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			m_applet.markCurrentShowButton(m_applet.SELECTEDCOLOR);
			return;
		}
		for(int i=0; i<nr; i++){
			m_encImagePanel.setImage(m_applet.createImage(m_encryptor.getImageProducerEncryptedFromVector(i)),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			m_encImagePanel.save();
		}
		m_encImagePanel.setImage(m_applet.createImage(m_encryptor.getImageProducerEncryptedFromVector(this.getCurrentFoil())),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
		m_applet.markCurrentShowButton(m_applet.SELECTEDCOLOR);
	}
	/** call a new Printer-Object and let it do the necessary
	 * work to get a printed paper.
	 *
	 */
	public void print(){
		if(m_ovlImagePanel.isMarked()){
			new Printer(0,0,m_encryptor.getImageProducerOverlay(),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			return;
		}
		if(this.getNrOfFoils()==2 && m_option!=DOUBLEDKEY){
			if(m_encImagePanel.isMarked() && !m_encImagePanel2.isMarked()){
				new Printer(1,2,m_encryptor.getImageProducerEncryptedFromVector(0),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			}else if(!m_encImagePanel.isMarked() && m_encImagePanel2.isMarked()){
				new Printer(2,2,m_encryptor.getImageProducerEncryptedFromVector(1),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			}else{
				JOptionPane.showMessageDialog(m_applet,"Please select one transparency by left click with the mouse.","Warning",JOptionPane.WARNING_MESSAGE);
			}
		}else{
			if(m_option != DOUBLEDKEY){
				new Printer((this.getCurrentFoil()+1),this.getNrOfFoils(),m_encryptor.getImageProducerEncryptedFromVector(this.getCurrentFoil()),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			}else{
				new Printer((this.getCurrentFoil()+1),this.getNrOfFoils(),m_encryptor.getImageProducerEncrypted(this.getCurrentFoil()),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			}
			
		}
		m_applet.markCurrentShowButton(m_applet.SELECTEDCOLOR);
	}

	/** creates a new JFrame and shows the selected Transparency
	 * in a bigger way
	 *
	 */
	public void zoom(){
		if(m_ovlImagePanel.isMarked()){
			new Zoom("resulting",m_encryptor.getImageProducerOverlay(),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			return;
		}
		if(this.getNrOfFoils()==2 && m_option!=DOUBLEDKEY){
			if(m_encImagePanel.isMarked() && !m_encImagePanel2.isMarked()){
				new Zoom("#1",m_encryptor.getImageProducerEncryptedFromVector(0),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			}else if(!m_encImagePanel.isMarked() && m_encImagePanel2.isMarked()){
				new Zoom("#2",m_encryptor.getImageProducerEncryptedFromVector(1),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			}else{
				JOptionPane.showMessageDialog(m_applet,"Please select one transparency by left click with the mouse.","Warning",JOptionPane.WARNING_MESSAGE);
			}
		}else{
			int nr = this.getCurrentFoil();
			if(m_option != DOUBLEDKEY){
				new Zoom("#"+(nr+1), m_encryptor.getImageProducerEncryptedFromVector(nr),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			}else{
				new Zoom("#"+(nr+1), m_encryptor.getImageProducerEncrypted(nr),m_encryptor.getWidthEnc(),m_encryptor.getHeightEnc());
			}
		}
		m_applet.markCurrentShowButton(m_applet.SELECTEDCOLOR);
	}
	/** deselects a marked ImagePanel if another one is marked
	 * 
	 * @param ip - the imagePanel with the newer mark
	 */
	public void setOtherImagePanelUnmarked(ImagePanel ip){
		boolean enc = m_encImagePanel.isMarked();
		boolean enc1 = m_encImagePanel2.isMarked();
		boolean ovl = m_ovlImagePanel.isMarked();
		if((this.getNrOfFoils()==2) &&((enc&&enc1)||(enc&&ovl)||(enc1&&ovl))){
			//if two transparancies and two of them are marked
			if(m_encImagePanel==ip){
				//the newer mark is in encImagePanel
				m_encImagePanel2.setMarked(false);
				m_ovlImagePanel.setMarked(false);
			}else if(m_encImagePanel2==ip){
				m_encImagePanel.setMarked(false);
				m_ovlImagePanel.setMarked(false);
			}else{
				m_encImagePanel.setMarked(false);
				m_encImagePanel2.setMarked(false);
			}
		}else if((enc&&ovl)){
			if(m_encImagePanel==ip){
				m_ovlImagePanel.setMarked(false);
			}else{
				m_encImagePanel.setMarked(false);
			}
		}
	}
}


