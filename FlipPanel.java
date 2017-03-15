/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;

import gui.ImageIconLoader;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//import java.net.MalformedURLException;
//import java.net.URL;

import javax.swing.BorderFactory;
//import javax.swing.Icon;
//import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/** provides a flipping fuctionality
 * @author Boßle Johannes
 */
public class FlipPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7361358164251757356L;

	/** the state of the flip
	 * true means that it is flipped, false that it is shown completely
	 */
	private boolean m_state = false;
	
	/** variable to store the content of the panel
	 * 
	 */
	private JPanel m_content;
	/** variable to store the parent of the panel
	 * 
	 */
	private Container m_parent;
	/** some view elements*/
	GridBagConstraints c = new GridBagConstraints();
	private final String rightIcon = "Right16.gif";
	private final String downIcon = "DownFull16.gif";
	
	private JButton m_button;
	private JPanel smallPanel = new JPanel();
	
	/** creates a new FlipPanel with the specified content as content to show
	 * initially the big panel is shown
	 * @param content
	 */
	public FlipPanel(JPanel content, JApplet parent, String name){
		m_content = content;
		m_parent = parent.getContentPane();
		
		TitledBorder tb = BorderFactory.createTitledBorder(name);
		this.setBorder(tb);
		
		GridBagLayout gbl = new GridBagLayout();
		
		setLayout(gbl);
		m_button = new JButton();
//		try {
//			rightIcon = new ImageIcon(new URL(parent.getCodeBase()+"Right16.gif"));
//			downIcon = new ImageIcon(new URL(parent.getCodeBase()+"DownFull16.gif"));
//		} catch (MalformedURLException e1) {
//			System.out.println("Sorry, couldn't get the icon");
//		}
//		m_button.setIcon(downIcon);
		m_button.setIcon(ImageIconLoader.loadImageIcon(downIcon));
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(0, 1, 0, 0);
		c.anchor = GridBagConstraints.NORTHWEST;
		m_button.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
					flip(!m_state);
			}
		});
		this.add(m_button,c);

		c.gridx = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 10, 0, 0);
		this.add(content,c);

		m_parent.add(this);
		
		smallPanel.setPreferredSize(content.getSize());
	}
	
	/** flip between the big and the small panel
	 * 
	 * @param flip - true for the small panel, false for the big one
	 */
	public void flip(boolean flip){
		if(flip){
			//show the small version
			m_state = true;
			this.remove(m_content);
			this.add(smallPanel,c);
			m_button.setIcon(ImageIconLoader.loadImageIcon(rightIcon));
		}else{
			//show the big version
			m_state = false;
			this.remove(smallPanel);
			this.add(m_content,c);
			m_button.setIcon(ImageIconLoader.loadImageIcon(downIcon));
		}
		m_parent.validate();
	}
	/** returns the state of this FlipPanel
	 * 
	 * @return boolean m_state - true if the panel is small,
	 * false otherwise
	 */
	public boolean isFlipped(){
		return m_state;
	}

}
