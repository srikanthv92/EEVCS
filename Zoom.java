/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageProducer;

import javax.swing.JFrame;

/**opens a new JFrame and displays the given image
 * 
 */
public class Zoom extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3187274312890781985L;
	int m_width;
	int m_height;
	Image m_image;
	/** creates a new Zoom-Object
	 * 
	 * @param description - the description to display in the title of the Frame
	 * @param imageProducer - the imageProducer for the picture
	 * @param width - the width of the pic
	 * @param height - the height of the pic
	 */
	public Zoom(String description, ImageProducer imageProducer, int width, int height){
		super("zoomed transparency "+description);
		m_width = width*2;
		m_height = height*2;
		m_image = this.createImage(imageProducer);
		this.setSize(m_width+60, m_height+100);
		this.setVisible(true);
	}
	/** provides painting of the picture on this frame
	 */
	public void paint(Graphics g){
		g.drawImage(m_image, 30, 50, m_width, m_height, this);
	}

}
