/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
/** ImagePanel provides functionality to display
 * the generated images. Furthermore there is a MouseListener
 * to listen for clicks at the pictures for selecting and saving
 * 
 * @author Boßle Johannes
 */
class ImagePanel extends JPanel implements MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3829172084390009671L;
	/** the imageSource*/
	Image m_imgSource;
	/** the imageBuffer*/
	Image m_imgBuffer;
	/** width of this image*/
	int m_width;
	/** height of this image*/
	int m_height;
	/** the dispatcher object which has called*/
	Dispatcher m_dispatcher;
	/** store whether this ImagePanel was selected*/
	boolean m_marked = false;

	/** creates a new blank ImagePanel without an image
	 * and without any listeners
	 * 
	 * @param width - width of the image
	 * @param height - height of the image
	 */
	public ImagePanel(int width, int height) {
		this.setPreferredSize(new Dimension(width,height));
		this.setVisible(true);
		return;
	}
	/** creates a new blank ImagePanel with listeners
	 * without an concrete image, but with listeners
	 * @param width - width of the image
	 * @param height - height of the image
	 * @param disp - the dispatcher-object which called
	 */ 
	public ImagePanel(int width, int height, Dispatcher disp) {
		m_dispatcher = disp;
		this.addMouseListener(this);
		this.setPreferredSize(new Dimension(width,height));
		this.setVisible(true);
		return;
	}

	/** sets the image for this canvas and draws it
	 * 
	 * @param img - the image to display
	 * @param width - width of the image
	 * @param height - height of the image
	 */
	public void setImage(Image img, int width, int height) {
		m_imgSource = img;
		this.setVisible(true);
		m_width = width;
		m_height = height;
		m_imgBuffer = createImage(width, height);
		if (m_imgSource != null && (m_imgBuffer!= null) ) {
			Graphics g = m_imgBuffer.getGraphics();
			g.drawImage(m_imgSource, 0, 0, width, height, this);
			g.dispose();
		}
		this.setPreferredSize(new Dimension(width,height));
	}
	/** sets the image for this canvas and draws it
	 * 
	 * @param img - the image to display
	 */
	public void setImage(Image img) {
		m_imgSource = img;
		this.setVisible(true);
		m_imgBuffer = createImage(m_width, m_height);
		if (m_imgSource != null ) {
			Graphics g = m_imgBuffer.getGraphics();
			g.drawImage(m_imgSource, 0, 0, m_width, m_height, this);
			g.dispose();
		}
		this.setPreferredSize(new Dimension(m_width, m_height));
	}
	/** draws an image of the imageBuffer
	 * @param g - the Graphics g parameter
	 */
	public void paint(Graphics g) {
		if (m_imgBuffer != null) {
			g.drawImage(m_imgBuffer, 0, 0, m_width, m_height, this);
			if(m_marked){
				g.setColor(Color.blue);
			}
			g.drawRect(0,0,m_width-1, m_height-1);
		} else {// error message
			//g.drawString("Image not available", 10, 100);
		}
	}
	
	/** not implemented
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {}

	/** not implemented
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {	}

	/** not implemented
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {}

	/** get the MouseEvent, do the new layout and paint it new
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {		
		if ( !e.isMetaDown() ) {
			//left click
            m_marked = !m_marked;
            m_dispatcher.setOtherImagePanelUnmarked(this);
	    	this.update(this.getGraphics());		
	     } else {
	     	//rightClick
	     	try{
	     		this.save();
	     	}catch(SecurityException se){
	     		JOptionPane.showMessageDialog(this,"You are not privileged to do this operation. \nPlease check your security policy.","Alert",JOptionPane.ERROR_MESSAGE);
	     	}
	     }
	}
	
	/** not implemented
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {}
	/** save the current image as *.gif or *.jpg\n
	 * calls a JFileChooser-Object and checks up the security-
	 * constraints and writes it to the persistent storage or 
	 * displays an error message.
	 */
	public void save(){
		JFileChooser jfc = new JFileChooser();
		jfc.setFileFilter(new FileFilter(){
			public boolean accept(File f){
				boolean ret = false;
				if(f.isDirectory())return true;
				if(ImagePanel.extension(f)!= null){
					ret = true;
				}
				return ret;
			}
			public String getDescription(){
				return "*.gif, *.png";
			}
		});
		JTextArea ta = new JTextArea(" please specify the format \n in the filename \n and make sure \n that privileges are \n set. ");
		ta.setEditable(false);
		ta.setBackground(jfc.getBackground());
		ta.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		jfc.setAccessory(ta);
		if(jfc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
			File f = jfc.getSelectedFile();
			if(!(!f.canWrite() && f.exists())){
				//!(cannot write to this file and it exists)
				try {
					String ext = extension(f);
					if(ext!=null){
						//saving as gif
						if(ext.equals("gif")){
							System.out.println("saving image as gif");
							GIFEncoder encode = new GIFEncoder(m_imgBuffer);
							OutputStream output = new BufferedOutputStream(new FileOutputStream(f));
							encode.Write(output);
						}else if(ext.equals("png")){
							System.out.println("saving image as png");
							ImageIO.write((RenderedImage)m_imgBuffer, ext, f);
						}
					}else{
						JOptionPane.showMessageDialog(jfc,"please specify whether *.gif or *.png","",JOptionPane.ERROR_MESSAGE);
					}
					
				} catch (FileNotFoundException e) {
					System.out.println("file not found");
					e.printStackTrace();
				} catch (Exception e) {
					System.out.println("saving failed");
					e.printStackTrace();
				}
			}else{
				JOptionPane.showMessageDialog(jfc,"not privileged to write a file","",JOptionPane.ERROR_MESSAGE);
				System.out.println("not privileged to write to a file");
			}
		}else{
			//user pressed CANCEL
			return;
		}
	}//end save()
	/** get the extension of a given file
	 * 
	 * @param f - the file to give back the extension
	 * @return - the extension of this file
	 */
	public static String extension(File f){
		String name = f.getName();
		String ext = "";
		int pos = name.lastIndexOf(".");
		if(pos>0 && pos<name.length()-1){
			ext = name.substring(pos+1).toLowerCase();
		}
		if(ext.equals("gif")||ext.equals("png")){
			return ext;
		}
		return null;
	}
	/** is this imagePanel selected?
	 * 
	 * @return true if selected/marked, false otherswise
	 */
	public boolean isMarked(){
		return m_marked;
	}
	/** sets the imagePanel to the given state
	 * 
	 * @param b - true: set it marked, false: set it unmarked
	 */
	public void setMarked(boolean b){
		if(b)m_marked=true;
		else m_marked=false;
		try{
    		this.update(this.getGraphics());		
    	}catch(Exception exc){
    		System.out.println("FlipPanel with this Object was not opened so it was not updated immediately");
    	}
	}
}