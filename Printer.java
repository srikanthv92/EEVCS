/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;

import javax.swing.JFrame;

/** class to print out a kind of label and an image
 * 
 * 
 */
public class Printer extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5540757637900552093L;
	Image m_imageSrc;
	int m_width, m_height;
	String m_label;
	/** constructor of the class Printer to print out an image and a 
	 * kind of Label
	 * @param curFoil - the current number of the transparency for the label
	 * @param maxFoil - the maximum number of transparencies for the label
	 * @param imageProducer - the imageProducer of the image to print
	 * @param width - the width of the image
	 * @param height - the height of the image
	 */
	public Printer (int curFoil, int maxFoil, ImageProducer imageProducer, int width, int height){
		super("PRINT");
		m_label ="transparency "+curFoil+" of "+maxFoil+" from VCApplet";
		
		m_width = width;
		m_height = height;
		m_imageSrc = this.createImage(imageProducer);
		Toolkit tk = Toolkit.getDefaultToolkit();
	    PrintJob pj = tk.getPrintJob(this,"", null);
		if (pj != null) {
			Graphics g = pj.getGraphics();
			this.paint(g);
			g.dispose();
			pj.end();
	    }
	}
	/** provides painting on this frame and is also called
	 * from the constructor to paint on the printer 
	 */
	public void paint(Graphics g){
		g.drawString(m_label,50,30);
		g.drawImage(m_imageSrc, 50, 80, m_width, m_height, this);
		g.drawRect(50,80,m_width,m_height);
	}
}
