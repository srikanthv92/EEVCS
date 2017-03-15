/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;

import anon.util.ResourceLoader;

/** AppletListener for the VCApplet to listen on Actions and ItemState-Eventes
 * 
 */
public class VCAppletListener implements ActionListener, ItemListener, HyperlinkListener {

	/** the applet which called this VCAppletListener */
	VCApplet m_applet;
	/*VCApplet m_applet* flag to see if there are loaded encrypted transparencies*/
	boolean m_LoadEncFoil = false;
		
	/** creates a new VCAppletListenerObject
	 * 
	 * @param applet - the applet to listen to
	 */
	public VCAppletListener(VCApplet applet){
		m_applet = applet;
	}
	/** creates a filechooser to select images to load
	 * 
	 * @param desc - description to display
	 * @param loadEnc - true if encrypted foils are loaded, false otherwise
	 * @return - JFileChooserObject with filter, descritption,...
	 */
	private JFileChooser getFileChooser(String desc, boolean loadEnc){
		JFileChooser jfc = new JFileChooser(desc);
		jfc.setFileFilter(new FileFilter(){
			public boolean accept(File f){
				boolean ret = false;
				if (f.isDirectory()) {
					return true;
				}
				if(ImagePanel.extension(f)!= null){
					ret = true;
				}
				return ret;
			}
			public String getDescription(){
				return "*.gif, *.png";
			}
		});
		jfc.setAccessory(new ImagePreview(jfc));

		return jfc;
	}
	
	/** actionPerformed
	 * sets the action whenever a button is pressed
	 */
	public void actionPerformed(ActionEvent evt) {
		m_applet.setEncTFText();
		String actionCommand = evt.getActionCommand();
		try{
			//File
			if (actionCommand == (m_applet.loadItem.getText()) || actionCommand == m_applet.loadButton.getActionCommand()) {
				JFileChooser jfc = this.getFileChooser("load a new original image",false);
				if(jfc.showOpenDialog(m_applet)==JFileChooser.APPROVE_OPTION){
					m_applet.m_fileImage = jfc.getSelectedFile();
					m_applet.m_dispatcher.loadImage(m_applet.m_fileImage);
					m_applet.inputPanel.setVisible(false);
					m_applet.inputPanel.setVisible(true);
					m_applet.updateMenu();
					m_applet.enableFunctionsAfterEncrypt(false);
					m_applet.inputPanel.validate();
				}
			}else if(actionCommand==(m_applet.loadEncItem.getText()) || actionCommand == m_applet.loadEncButton.getActionCommand()){
				m_LoadEncFoil = true;
				
				int nrOfFoils = m_applet.m_dispatcher.getNrOfFoils();
				File[] files = new File[nrOfFoils];
				for(int i=0; i<nrOfFoils; i++){
					JFileChooser jfc = this.getFileChooser("load transparency #"+(i+1),true);
					int temp = jfc.showOpenDialog(m_applet);
					if(temp==JFileChooser.APPROVE_OPTION){
						files[i] = jfc.getSelectedFile();
					}else if(temp==JFileChooser.CANCEL_OPTION){
						m_LoadEncFoil = false;
						return;
					}else{
						m_LoadEncFoil = false;
						m_applet.enableFunctionsAfterEncrypt(false);
						return;
					}
				}
				m_applet.enableFunctionsAfterEncrypt(true);
				m_applet.inputFlip.flip(true);
				m_applet.encFlip.flip(false);
				m_applet.resultFlip.flip(false);
				if(!m_applet.m_dispatcher.loadAsEncFoil(files)){
					m_LoadEncFoil = false;
					JOptionPane.showMessageDialog(m_applet,"Loading of the transparencies failed.\nPlease be sure to have selected the right mode and transparencies.","Alert",JOptionPane.ERROR_MESSAGE);
					m_applet.enableFunctionsAfterEncrypt(false);
				}else{
					m_applet.m_dispatcher.overlay(m_applet.m_setForAccess);
				}
			}else if(actionCommand == m_applet.saveFoilItem.getText() || actionCommand == m_applet.saveButton.getActionCommand()){
				m_applet.m_dispatcher.saveCurrent();
			}else if(actionCommand == m_applet.saveAllItem.getText() || actionCommand == m_applet.saveAllButton.getActionCommand()){
				m_applet.m_dispatcher.saveAll();
			}else if(actionCommand == m_applet.printItem.getText()  || actionCommand == m_applet.printButton.getActionCommand()){
				m_applet.m_dispatcher.print();
			}	
			else if(actionCommand == m_applet.exitItem.getText()){
				System.out.println("Exit");
				System.exit(0);
			} 
			//MODE
			else if(actionCommand == m_applet.enc2_2RbItem.getText()){
				m_applet.m_dispatcher.newMode(2,Dispatcher.NOOPTION,Dispatcher.VCTYPE_2_2);
				m_applet.updateMenu();
			}else if(actionCommand == m_applet.enc2_3RbItem.getText()){
				m_applet.m_dispatcher.newMode(3,Dispatcher.NOOPTION,Dispatcher.VCTYPE_2_N);
				m_applet.updateMenu();
			}else if(actionCommand == m_applet.enc2_4RbItem.getText()){
				m_applet.m_dispatcher.newMode(4,Dispatcher.NOOPTION,Dispatcher.VCTYPE_2_N);
				m_applet.updateMenu();
			}else if(actionCommand == m_applet.enc2_5RbItem.getText()){
				m_applet.m_dispatcher.newMode(5,Dispatcher.NOOPTION,Dispatcher.VCTYPE_2_N);
				m_applet.updateMenu();
			}else if(actionCommand == m_applet.enc2_2GreyRbItem.getText()){
				m_applet.m_dispatcher.newMode(2,Dispatcher.GREY,Dispatcher.VCTYPE_2_2);
				m_applet.updateMenu();
			}else if(actionCommand == m_applet.enc2_2ColorRbItem.getText()){
				m_applet.m_dispatcher.newMode(2,Dispatcher.COLOR,Dispatcher.VCTYPE_2_2);
				m_applet.updateMenu();
			}else if(actionCommand == m_applet.enc2_2DoubledKeyRbItem.getText()){
				m_applet.m_dispatcher.newMode(2,Dispatcher.DOUBLEDKEY,Dispatcher.VCTYPE_2_2);
				m_applet.updateMenu();
				m_applet.m_dispatcher.loadImage(m_applet.m_fileImage);
			}else if(actionCommand == m_applet.enc3_3RbItem.getText()){
				m_applet.m_dispatcher.newMode(3,Dispatcher.NOOPTION,Dispatcher.VCTYPE_3_3);
				m_applet.updateMenu();
			}else if(actionCommand == m_applet.enc3_3GARbItem.getText()){
				m_applet.m_dispatcher.newMode(3,Dispatcher.GENERALACCESS,Dispatcher.VCTYPE_3_3);
				m_applet.updateMenu();
			}else if(actionCommand == m_applet.enc3_4RbItem.getText()){
				m_applet.m_dispatcher.newMode(4,Dispatcher.NOOPTION,Dispatcher.VCTYPE_3_N);
				m_applet.updateMenu();
			}else if(actionCommand == m_applet.enc3_5RbItem.getText()){
				m_applet.m_dispatcher.newMode(5,Dispatcher.NOOPTION,Dispatcher.VCTYPE_3_N);
				m_applet.updateMenu();
			}
			//VC -- encrypt
			else if (actionCommand == m_applet.generateEncItem.getText()  || actionCommand == m_applet.vcButton.getActionCommand()) {
				m_LoadEncFoil = false;
				m_applet.enableFunctionsAfterEncrypt(true);
				m_applet.inputFlip.flip(false);
				m_applet.encFlip.flip(false);
				m_applet.m_dispatcher.putEncImagesToApplet();
				m_applet.markCurrentShowButton(m_applet.SELECTEDCOLOR);
				m_applet.resultFlip.flip(false);
			}
			//Show
			else if(actionCommand == m_applet.zoomButton.getActionCommand()){
				m_applet.m_dispatcher.zoom();
			}else if(actionCommand == m_applet.show1Item.getText() || actionCommand == m_applet.show1Button.getActionCommand()){
				m_applet.m_dispatcher.getEncCanvas(0);
				m_applet.setEncTFText();
				m_applet.show1Button.setBackground(m_applet.SELECTEDCOLOR);
			}else if(actionCommand == m_applet.show2Item.getText() || actionCommand == m_applet.show2Button.getActionCommand()){
				m_applet.m_dispatcher.getEncCanvas(1);
				m_applet.setEncTFText();
				m_applet.show2Button.setBackground(m_applet.SELECTEDCOLOR);
			}else if(actionCommand == m_applet.show3Item.getText() || actionCommand == m_applet.show3Button.getActionCommand()){
				m_applet.m_dispatcher.getEncCanvas(2);
				m_applet.setEncTFText();
				m_applet.show3Button.setBackground(m_applet.SELECTEDCOLOR);
			}else if(actionCommand == m_applet.show4Item.getText() || actionCommand == m_applet.show4Button.getActionCommand()){
				m_applet.m_dispatcher.getEncCanvas(3);
				m_applet.setEncTFText();
				m_applet.show4Button.setBackground(m_applet.SELECTEDCOLOR);
			}else if(actionCommand == m_applet.show5Item.getText() || actionCommand == m_applet.show5Button.getActionCommand()){
				m_applet.m_dispatcher.getEncCanvas(4);
				m_applet.setEncTFText();
				m_applet.show5Button.setBackground(m_applet.SELECTEDCOLOR);
			}else if(actionCommand == m_applet.nextFoil.getText()){
				m_applet.m_dispatcher.getEncCanvas(m_applet.m_dispatcher.getCurrentFoil()+1);
				m_applet.setEncTFText();
				m_applet.markCurrentShowButton(m_applet.SELECTEDCOLOR);
			}else if(actionCommand == m_applet.prevFoil.getText()){
				m_applet.m_dispatcher.getEncCanvas(m_applet.m_dispatcher.getCurrentFoil()-1);
				m_applet.setEncTFText();
				m_applet.markCurrentShowButton(m_applet.SELECTEDCOLOR);
			}else if(actionCommand == m_applet.infoItem.getText()|| actionCommand == m_applet.infoButton.getActionCommand()){
				JEditorPane ep = new JEditorPane();
				ep.setPreferredSize(new Dimension(350,200));
				ep.setEditable(false);
				ep.setBackground(m_applet.BACKGROUNDCOLOR);
				ep.setContentType("text/html");
				ep.setText("<html><title>About</title><body><div style=\"text-align:justify;\">"+m_applet.INFO+"</div></body></html>");
	//			JOptionPane.showMessageDialog(m_applet, m_applet.INFO);
				JOptionPane.showMessageDialog(m_applet, ep, "About", JOptionPane.INFORMATION_MESSAGE);
			}else if(actionCommand == m_applet.helpItem.getText()|| actionCommand == m_applet.helpButton.getActionCommand()){
				JFrame frame = new JFrame("Information");
				JEditorPane ep = new JEditorPane();
				ep.setPreferredSize(new Dimension(450,450));
				ep.setEditable(false);
				try{
					URL helpURL = ResourceLoader.getResourceURL(m_applet.HELPFILE);
//					URL helpURL = new URL(m_applet.getCodeBase()+ m_applet.HELPFILE);
					if (helpURL != null) {
							ep.setPage(helpURL);
					}
				} catch (IOException e) {
					System.err.println("Couldn't find help file");
				}
				
				ep.addHyperlinkListener(this);
				JScrollPane jsp = new JScrollPane(ep);
				jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				frame.getContentPane().add(jsp);
				frame.pack();
				frame.setVisible(true);
				

			}
		}catch(SecurityException se){
			JOptionPane.showMessageDialog(m_applet,"You are not privileged to do this operation. \nPlease check your security policy.","Alert",JOptionPane.ERROR_MESSAGE);
		}
	}
	/**
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		Object o = e.getSource();
		if (o == m_applet.overlay1Item || o == m_applet.ovl1Item) {
			if(((AbstractButton) o).isSelected()){
				m_applet.m_setForAccess[0] = true;
				m_applet.overlay1Item.setSelected(true);
				m_applet.ovl1Item.setSelected(true);
			}else{
				m_applet.m_setForAccess[0] = false;
				m_applet.overlay1Item.setSelected(false);
				m_applet.ovl1Item.setSelected(false);
			}
		}else if (o == m_applet.overlay2Item|| o == m_applet.ovl2Item) {
			if(((AbstractButton) o).isSelected()){
				m_applet.m_setForAccess[1] = true;
				m_applet.overlay2Item.setSelected(true);
				m_applet.ovl2Item.setSelected(true);
			}else{
				m_applet.m_setForAccess[1] = false;
				m_applet.overlay2Item.setSelected(false);
				m_applet.ovl2Item.setSelected(false);
			}
		}else if (o == m_applet.overlay3Item|| o == m_applet.ovl3Item) {
			if(((AbstractButton) o).isSelected()){
				m_applet.m_setForAccess[2] = true;
				m_applet.overlay3Item.setSelected(true);
				m_applet.ovl3Item.setSelected(true);
			}else{
				m_applet.m_setForAccess[2] = false;
				m_applet.overlay3Item.setSelected(false);
				m_applet.ovl3Item.setSelected(false);
			}
		}else if (o == m_applet.overlay4Item|| o == m_applet.ovl4Item) {
			if(((AbstractButton) o).isSelected()){
				m_applet.m_setForAccess[3] = true;
				m_applet.overlay4Item.setSelected(true);
				m_applet.ovl4Item.setSelected(true);
			}else{
				m_applet.m_setForAccess[3] = false;
				m_applet.overlay4Item.setSelected(false);
				m_applet.ovl4Item.setSelected(false);
			}
		}else if (o == m_applet.overlay5Item|| o == m_applet.ovl5Item) {
			if(((AbstractButton) o).isSelected()){
				m_applet.m_setForAccess[4] = true;
				m_applet.overlay5Item.setSelected(true);
				m_applet.ovl5Item.setSelected(true);
			}else{
				m_applet.m_setForAccess[4] = false;
				m_applet.overlay5Item.setSelected(false);
				m_applet.ovl5Item.setSelected(false);
			}
		}
		m_applet.resultFlip.flip(false);
		m_applet.m_dispatcher.overlay(m_applet.m_setForAccess);
		m_applet.resultPanelLeft.doLayout();
//		m_applet.resultPanel.doLayout();
		m_applet.resultPanel.setVisible(false);
		m_applet.resultPanel.setVisible(true);
//		m_applet.validateTree();
		
	}
	/**
	 * @return Returns the m_LoadEncFoil.
	 */
	public boolean isLoadEncFoil() {
		return m_LoadEncFoil;
	}
	/**
	 * @param loadEncFoil The loadEncFoil to set.
	 */
	public void setLoadEncFoil(boolean loadEncFoil) {
		m_LoadEncFoil = loadEncFoil;
	}
	/** used to realize hyperlinks in the help pages
	 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
	 */
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		      JEditorPane pane = (JEditorPane) e.getSource();
		      try {
	          	pane.setPage(e.getURL());
	          } catch (Throwable t) {
	          	t.printStackTrace();
	          }
          }
	}
}
