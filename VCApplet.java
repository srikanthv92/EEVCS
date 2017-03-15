/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
//import java.net.MalformedURLException;
//import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
//import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import gui.ImageIconLoader;


public class VCApplet extends JApplet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8958882345610556850L;

	/** provide large amount of information about this applet*/
	final String INFO = "<b>Embedded Extended Visual Cryptography Schemes</b> (Version 1.1.4)<br>" +
	"Author: <b></b>" +
	 "Goal of this Applet is to demonstrate "+
	"the wide range and mightiness of the Embedded Extended Visual Cryptography Schemes." +
	"</p>";
	/** path to the help file*/
	final String HELPFILE = "Help.html";

	/** the Dispatcher associated with this Applet. It takes over
	 * the control
	 */
	Dispatcher m_dispatcher = new Dispatcher(this);
	/** color to show, that a transparency is selected*/
	final Color SELECTEDCOLOR = new Color(0xEE,0xDD,0x82);
	/** the menu gui-elements*/
	JMenuBar mainMenu = new JMenuBar();
	
	JMenu fileMenu = new JMenu("File");
	JMenuItem loadItem = new JMenuItem("Load image");
	JMenuItem loadEncItem = new JMenuItem("Load encrypted transparencies");
	JMenuItem saveFoilItem = new JMenuItem("Save current transparency");
	JMenuItem saveAllItem = new JMenuItem("Save all transparencies");
	JMenuItem printItem = new JMenuItem("Print");
	JMenuItem exitItem = new JMenuItem("Exit");
	
	JMenu vcmodeMenu = new JMenu("Mode");
	ButtonGroup modeGroup = new ButtonGroup();
	JMenu basic2_n_menu = new JMenu("Basic 2 out of n");
	JRadioButtonMenuItem enc2_2RbItem = new JRadioButtonMenuItem("2 out of 2",true);//default
	JRadioButtonMenuItem enc2_3RbItem = new JRadioButtonMenuItem("2 out of 3");
	JRadioButtonMenuItem enc2_4RbItem = new JRadioButtonMenuItem("2 out of 4");
	JRadioButtonMenuItem enc2_5RbItem = new JRadioButtonMenuItem("2 out of 5");
	JMenu basic3_n_menu = new JMenu("Basic 3 out of n");
	JRadioButtonMenuItem enc3_3RbItem = new JRadioButtonMenuItem("3 out of 3");
	JRadioButtonMenuItem enc3_4RbItem = new JRadioButtonMenuItem("3 out of 4");
	JRadioButtonMenuItem enc3_5RbItem = new JRadioButtonMenuItem("3 out of 5");
	JMenu extendedModeMenu = new JMenu("Extended modes");
	JRadioButtonMenuItem enc2_2GreyRbItem = new JRadioButtonMenuItem("2 out of 2 - grey");
	JRadioButtonMenuItem enc2_2ColorRbItem = new JRadioButtonMenuItem("2 out of 2 - coloured");
	JRadioButtonMenuItem enc2_2DoubledKeyRbItem = new JRadioButtonMenuItem("2 out of 2 - with same random/key");
	JRadioButtonMenuItem enc3_3GARbItem = new JRadioButtonMenuItem("2 out of 3 - with general access");

	JMenu vcMenu = new JMenu("VisualCryptography");
	JMenuItem generateEncItem = new JMenuItem("Generate encrypted transparencies");
//	JMenuItem generateRandomItem = new JMenuItem("Generate random");
//	JMenuItem saveRandomItem = new JMenuItem("Save random");
//	JMenuItem loadRandomItem = new JMenuItem("Load random");
	
	JMenu showMenu = new JMenu("Show");
	JMenuItem show1Item = new JMenuItem("Transparency #1");
	JMenuItem show2Item = new JMenuItem("Transparency #2");
	JMenuItem show3Item = new JMenuItem("Transparency #3");
	JMenuItem show4Item = new JMenuItem("Transparency #4");
	JMenuItem show5Item = new JMenuItem("Transparency #5");
	
	JMenu overlayMenu = new JMenu("Result");
	JCheckBoxMenuItem overlay1Item = new JCheckBoxMenuItem("Overlay transparency #1");
	JCheckBoxMenuItem overlay2Item = new JCheckBoxMenuItem("Overlay transparency #2");
	JCheckBoxMenuItem overlay3Item = new JCheckBoxMenuItem("Overlay transparency #3");
	JCheckBoxMenuItem overlay4Item = new JCheckBoxMenuItem("Overlay transparency #4");
	JCheckBoxMenuItem overlay5Item = new JCheckBoxMenuItem("Overlay transparency #5");
	
	JCheckBox ovl1Item = new JCheckBox("Overlay transparency #1");
	JCheckBox ovl2Item = new JCheckBox("Overlay transparency #2");
	JCheckBox ovl3Item = new JCheckBox("Overlay transparency #3");
	JCheckBox ovl4Item = new JCheckBox("Overlay transparency #4");
	JCheckBox ovl5Item = new JCheckBox("Overlay transparency #5");
	
	JMenu infoMenu = new JMenu("Info");
	JMenuItem infoItem = new JMenuItem("About");
	JMenuItem helpItem = new JMenuItem("Help");
	
	/** the VCAppletListener for this Applet*/
	VCAppletListener m_vcAppletListener = new VCAppletListener(this);

	/** the toolbar gui-elements*/
	JToolBar toolbar;
	JButton loadButton, loadEncButton, saveButton, saveAllButton, printButton;
	JButton vcButton, show1Button, show2Button, show3Button, show4Button, show5Button;
	JButton infoButton, helpButton, zoomButton;
	/** other gui-elements*/
	JButton nextFoil = new JButton(">");
	JButton prevFoil = new JButton("<");
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	JPanel prevNextFoilPanel = new JPanel(gbl);
	
	JEditorPane descInputEP = new JEditorPane();
	JScrollPane jsp = new JScrollPane(descInputEP);
	JTextField descEncTF = new JTextField();
	JPanel overlayPanel = new JPanel(new GridLayout(5,1));
	FlipPanel inputFlip;
	FlipPanel encFlip;
	FlipPanel resultFlip;
	
	JPanel inputPanelLeft = new JPanel();
	JPanel inputPanelRight = new JPanel();
	JPanel encPanelLeft = new JPanel();
	JPanel encPanelRight = new JPanel();
	JPanel resultPanelLeft = new JPanel();
	JPanel resultPanelRight = new JPanel();
	
	JPanel inputPanel = new JPanel();
	JPanel encPanel = new JPanel();
	JPanel resultPanel = new JPanel();
	
	JPanel checkBoxPanel = new JPanel(new GridLayout(3,1));
	JCheckBox check1and2Box = new JCheckBox("1 and 2");
	JCheckBox check1and3Box = new JCheckBox("1 and 3");
	JCheckBox check2and3Box = new JCheckBox("2 and 3");
	
	/** the boolean[] to store whether transparency
	 * should be overlayed or not
	 */
	boolean[] m_setForAccess = new boolean[5];
	/** store the selected file by the FileChooser called at
	 * the VCAppletListener at the loading-process
	 */
	File m_fileImage = null;
	/** the BackgroundColor*/
	Color BACKGROUNDCOLOR = inputPanel.getBackground();

	/** method called at the beginning of the Applet 
	 * to do the init process, loads all the GUI-Elements and
	 * sets Listeners to them, where needed
	 */
	public void init() {
		System.out.println("Applet: init");
		this.createToolbarElements();
		this.makeMenuStructure();
		this.setListener();
		this.createGUI();
		//call first the loadImage-method of dispatcher and then
		//prepare for the default mode
		m_dispatcher.loadImage(m_fileImage);
		m_dispatcher.newMode(2,Dispatcher.NOOPTION,Dispatcher.VCTYPE_2_2);
		this.updateMenu();
		//set the focus in some area for directly reacting on F1-keyStroke
		inputPanel.requestFocus();
		inputFlip.flip(true);
		inputFlip.flip(false);
		
		System.out.println("Applet: waiting for action");
	}
	/** initialize the toolbar elements with its icons
	 * 
	 *
	 */
	private void createToolbarElements(){
		toolbar = new JToolBar("Visual Cryptography Control",JToolBar.HORIZONTAL);
		loadButton = this.makeButton("Open16","load","load an image","load");
		loadEncButton = this.makeButton("Add16","loadEnc","load encrypted transparencies","loadEnc");
		saveButton = this.makeButton("Save16","save","save a transparency","save");
		saveAllButton = this.makeButton("SaveAll16","saveAll","save all transparencies","save all");
		printButton = this.makeButton("Print16","print","print current transparency","print");
		
		vcButton = this.makeButton("","vc","generate encrypted transparencies","encrypt");
		show1Button = this.makeButton("","show1","show transparency #1","1");
		show2Button = this.makeButton("","show2","show transparency #2","2");
		show3Button = this.makeButton("","show3","show transparency #3","3");
		show4Button = this.makeButton("","show4","show transparency #4","4");
		show5Button = this.makeButton("","show5","show transparency #5","5");
		infoButton = this.makeButton("About16","info","show information about this applet","info");
		helpButton = this.makeButton("Help16","help","show helping information","help");
		zoomButton = this.makeButton("ZoomIn16","zoom","zoom in current transparency","zoom");
	}
	/** creates the menu
	 * 
	 *
	 */
	private void makeMenuStructure(){
		//add the menu gui
		fileMenu.add(loadItem);
		fileMenu.add(loadEncItem);
		fileMenu.add(saveFoilItem);
		fileMenu.add(saveAllItem);
		fileMenu.add(printItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		mainMenu.add(fileMenu);
		
		modeGroup.add(enc2_2RbItem);
		modeGroup.add(enc2_3RbItem);
		modeGroup.add(enc2_4RbItem);
		modeGroup.add(enc2_5RbItem);
		modeGroup.add(enc2_2GreyRbItem);
		modeGroup.add(enc2_2ColorRbItem);
		modeGroup.add(enc2_2DoubledKeyRbItem);
		modeGroup.add(enc3_3RbItem);
		modeGroup.add(enc3_3GARbItem);
		modeGroup.add(enc3_4RbItem);
		modeGroup.add(enc3_5RbItem);
		basic2_n_menu.add(enc2_2RbItem);
		basic2_n_menu.add(enc2_3RbItem);
		basic2_n_menu.add(enc2_4RbItem);
		basic2_n_menu.add(enc2_5RbItem);
		vcmodeMenu.add(basic2_n_menu);
		basic3_n_menu.add(enc3_3RbItem);
		basic3_n_menu.add(enc3_4RbItem);
		basic3_n_menu.add(enc3_5RbItem);
		vcmodeMenu.add(basic3_n_menu);
		extendedModeMenu.add(enc2_2GreyRbItem);
		extendedModeMenu.add(enc2_2ColorRbItem);
		extendedModeMenu.add(enc2_2DoubledKeyRbItem);
		extendedModeMenu.add(enc3_3GARbItem);
		vcmodeMenu.add(extendedModeMenu);
		mainMenu.add(vcmodeMenu);
		
		vcMenu.add(generateEncItem);
//		vcMenu.addSeparator();
//		vcMenu.add(generateRandomItem);
//		vcMenu.add(saveRandomItem);
//		vcMenu.add(loadRandomItem);
		mainMenu.add(vcMenu);
		
		showMenu.add(show1Item);
		showMenu.add(show2Item);
		showMenu.add(show3Item);
		showMenu.add(show4Item);
		showMenu.add(show5Item);
		mainMenu.add(showMenu);
		
		overlayMenu.add(overlay1Item);
		overlayMenu.add(overlay2Item);
		overlayMenu.add(overlay3Item);
		overlayMenu.add(overlay4Item);
		overlayMenu.add(overlay5Item);
		mainMenu.add(overlayMenu);
		
		infoMenu.add(helpItem);
		infoMenu.add(infoItem);
		mainMenu.add(infoMenu);
		this.setJMenuBar(mainMenu);
		//toolbar
		toolbar.setFloatable(false);
		toolbar.add(loadButton);
		toolbar.add(loadEncButton);
		toolbar.add(saveButton);
		toolbar.add(saveAllButton);
		toolbar.add(printButton);
		toolbar.addSeparator();
		toolbar.add(vcButton);
		toolbar.addSeparator();
		toolbar.add(zoomButton);
		toolbar.addSeparator();
		toolbar.add(show1Button);
		toolbar.add(show2Button);
		toolbar.add(show3Button);
		toolbar.add(show4Button);
		toolbar.add(show5Button);
		toolbar.addSeparator();
		toolbar.add(helpButton);
		toolbar.add(infoButton);
		
		helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));
				
		this.enableFunctionsAfterEncrypt(false);
		//add a new panel to the toolbar to get alignment to the left
		toolbar.add(new JPanel());
		
		this.getContentPane().add(toolbar);
	}
	/** sets the actionListener and the itemStateListener to the menu
	 * 
	 *
	 */
	private void setListener(){
		//set the action listeners to the menu
		loadItem.addActionListener(m_vcAppletListener);
		loadEncItem.addActionListener(m_vcAppletListener);
		saveFoilItem.addActionListener(m_vcAppletListener);
		saveAllItem.addActionListener(m_vcAppletListener);
		printItem.addActionListener(m_vcAppletListener);
		exitItem.addActionListener(m_vcAppletListener);
		
		generateEncItem.addActionListener(m_vcAppletListener);
		
		enc2_2RbItem.addActionListener(m_vcAppletListener);
		enc2_3RbItem.addActionListener(m_vcAppletListener);
		enc2_4RbItem.addActionListener(m_vcAppletListener);
		enc2_5RbItem.addActionListener(m_vcAppletListener);
		enc2_2GreyRbItem.addActionListener(m_vcAppletListener);
		enc2_2ColorRbItem.addActionListener(m_vcAppletListener);
		enc2_2DoubledKeyRbItem.addActionListener(m_vcAppletListener);
		enc3_3GARbItem.addActionListener(m_vcAppletListener);
		enc3_3RbItem.addActionListener(m_vcAppletListener);
		enc3_4RbItem.addActionListener(m_vcAppletListener);
		enc3_5RbItem.addActionListener(m_vcAppletListener);

		show1Item.addActionListener(m_vcAppletListener);
		show2Item.addActionListener(m_vcAppletListener);
		show3Item.addActionListener(m_vcAppletListener);
		show4Item.addActionListener(m_vcAppletListener);
		show5Item.addActionListener(m_vcAppletListener);
		
		overlay1Item.addItemListener(m_vcAppletListener);
		overlay2Item.addItemListener(m_vcAppletListener);
		overlay3Item.addItemListener(m_vcAppletListener);
		overlay4Item.addItemListener(m_vcAppletListener);
		overlay5Item.addItemListener(m_vcAppletListener);
		
		nextFoil.addActionListener(m_vcAppletListener);
		prevFoil.addActionListener(m_vcAppletListener);
		
		ovl1Item.addItemListener(m_vcAppletListener);
		ovl2Item.addItemListener(m_vcAppletListener);
		ovl3Item.addItemListener(m_vcAppletListener);
		ovl4Item.addItemListener(m_vcAppletListener);
		ovl5Item.addItemListener(m_vcAppletListener);
		
		infoItem.addActionListener(m_vcAppletListener);
		helpItem.addActionListener(m_vcAppletListener);
	}

	/** places the several GUI-Elements
	 * 
	 *
	 */
	private void createGUI(){
		// add GUI components and layouts
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
	
		inputPanel.setLayout(new BoxLayout(inputPanel,BoxLayout.X_AXIS));		
		encPanel.setLayout(new BoxLayout(encPanel,BoxLayout.X_AXIS));
		resultPanel.setLayout(new BoxLayout(resultPanel,BoxLayout.X_AXIS));
		
		inputFlip = new FlipPanel(inputPanel,this, "Original");
		encFlip = new FlipPanel(encPanel,this,"Encrypted Transparencies");
		resultFlip = new FlipPanel(resultPanel,this,"Overlay");
		
		checkBoxPanel.add(check1and2Box);
		checkBoxPanel.add(check1and3Box);
		checkBoxPanel.add(check2and3Box);
		checkBoxPanel.setVisible(false);
		
		inputPanel.add(inputPanelLeft);
		inputPanel.add(inputPanelRight);
		encPanel.add(encPanelLeft);
		encPanel.add(encPanelRight);
		resultPanel.add(resultPanelLeft);
		resultPanel.add(resultPanelRight);
		
		inputPanel.add(m_dispatcher.getSrcCanvas());
		inputPanel.add(checkBoxPanel);
		inputPanel.add(jsp);
		encPanel.add(m_dispatcher.getEncCanvas());
		encPanel.add(prevNextFoilPanel);
		resultPanel.add(m_dispatcher.getResultCanvas());
		resultPanel.add(overlayPanel);
		
		
		inputPanelLeft.add(m_dispatcher.getSrcCanvas());
		inputPanelLeft.add(checkBoxPanel);
		descInputEP.setEditable(false);
		descInputEP.setBackground(BACKGROUNDCOLOR);
		descInputEP.setContentType("text/html");
		jsp.setPreferredSize(new Dimension(390,100));
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		Border eb = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		jsp.setBorder(eb);
		inputPanelRight.add(jsp);
		inputPanel.validate();
		
		encPanelLeft.add(m_dispatcher.getEncCanvas());
		descEncTF.setEditable(false);
		descEncTF.setBorder(null);
		descEncTF.setBackground(BACKGROUNDCOLOR);
		gbc.insets = new Insets(0,10,0,0);
		prevNextFoilPanel.add(prevFoil,gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		prevNextFoilPanel.add(nextFoil,gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(7,10,0,0);
		prevNextFoilPanel.add(descEncTF,gbc);
		
		prevNextFoilPanel.setVisible(false);
		encPanelRight.add(prevNextFoilPanel);
		encFlip.flip(true);
		resultPanelLeft.add(m_dispatcher.getResultCanvas());
		overlayPanel.add(ovl1Item);
		overlayPanel.add(ovl2Item);
		overlayPanel.add(ovl3Item);
		overlayPanel.add(ovl4Item);
		overlayPanel.add(ovl5Item);
		resultPanelRight.add(overlayPanel);
		resultFlip.flip(true);
		doLayout();
		validate();
	}
	/** enables some functions, which are only available after
	 * encrypting. Also can disable these functions;
	 * @param b - true if the functions should be enabled, false otherwise
	 */
	public void enableFunctionsAfterEncrypt(boolean b){
		if(b){
			//set functions enabled
			saveButton.setEnabled(true);
			saveAllButton.setEnabled(true);
			printButton.setEnabled(true);
			saveFoilItem.setEnabled(true);
			saveAllItem.setEnabled(true);
			printItem.setEnabled(true);
			overlay1Item.setEnabled(true);
			overlay2Item.setEnabled(true);
			zoomButton.setEnabled(true);
			
			resultPanelLeft.removeAll();
			resultPanelLeft.add(m_dispatcher.getResultCanvas());
			
			switch(m_dispatcher.getNrOfFoils()){
			case 5: show5Item.setEnabled(true);
					show5Button.setEnabled(true);
					overlay5Item.setEnabled(true);
					ovl5Item.setEnabled(true);
			case 4: show4Item.setEnabled(true);
					show4Button.setEnabled(true);
					overlay4Item.setEnabled(true);
					ovl4Item.setEnabled(true);
			case 3: show3Item.setEnabled(true);
					show3Button.setEnabled(true);
					overlay3Item.setEnabled(true);
					ovl3Item.setEnabled(true);
					show2Item.setEnabled(true);
					show2Button.setEnabled(true);
					show1Item.setEnabled(true);
					show1Button.setEnabled(true);
			case 2: overlay1Item.setEnabled(true);
					ovl1Item.setEnabled(true);
					overlay2Item.setEnabled(true);
					ovl2Item.setEnabled(true);	
			}
			//special case for DOUBLEDKEY
			if(m_dispatcher.m_option == Dispatcher.DOUBLEDKEY){
				show1Item.setEnabled(true);
				show2Item.setEnabled(true);
				show3Item.setEnabled(true);
				show1Button.setEnabled(true);
				show2Button.setEnabled(true);
				show3Button.setEnabled(true);
				overlay3Item.setEnabled(true);
				ovl3Item.setEnabled(true);
			}
			
		}else{
			//boolean b = false
			saveButton.setEnabled(false);
			saveAllButton.setEnabled(false);
			printButton.setEnabled(false);
			saveFoilItem.setEnabled(false);
			saveAllItem.setEnabled(false);
			printItem.setEnabled(false);
			overlay1Item.setEnabled(false);
			overlay2Item.setEnabled(false);
			zoomButton.setEnabled(false);
			
			show1Item.setEnabled(false);
			show2Item.setEnabled(false);
			show3Item.setEnabled(false);
			show4Item.setEnabled(false);
			show5Item.setEnabled(false);
			show1Button.setEnabled(false);
			show2Button.setEnabled(false);
			show3Button.setEnabled(false);
			show4Button.setEnabled(false);
			show5Button.setEnabled(false);
			
			overlay1Item.setEnabled(false);
			ovl1Item.setEnabled(false);
			overlay2Item.setEnabled(false);
			ovl2Item.setEnabled(false);
			overlay3Item.setEnabled(false);
			ovl3Item.setEnabled(false);
			overlay4Item.setEnabled(false);
			ovl4Item.setEnabled(false);
			overlay5Item.setEnabled(false);
			ovl5Item.setEnabled(false);
		}
		loadEncButton.setEnabled(true);
		loadEncItem.setEnabled(true);
		if(m_dispatcher.m_option == Dispatcher.DOUBLEDKEY){
			loadEncButton.setEnabled(false);
			loadEncItem.setEnabled(false);
		}
	}
	
	/** get information about this Applet
	 * @return a info about this Applet
	 */
	public String getAppletInfo(){
		return INFO;
	}
	
	/** overrides the Applet#getParameterInfo() and returns
	 * a String that no parameters are needed
	 * @return parameterinfo as a String[][]
	 */
	public String[][] getParameterInfo(){
		String[][] parameterinfo = new String[1][3];
		parameterinfo[0][0] = "No";
		parameterinfo[0][1] = "parameter";
		parameterinfo[0][2] = "needed";
		return parameterinfo;
	}
	/** returns the current SetForAccess if there is some information
	 * in it. If all boolean are false or null, then null is returned
	 * @return setForAccess or null if no information is in it
	 */
	public boolean[] getSetForAccess(){
		for(int i=0; i<m_setForAccess.length; i++){
			if(m_setForAccess[i]==true)
				return m_setForAccess;
		}
		return null;
	}

	/** reads out the ordered AccessStructure from the inputPanel
	 * and stores it in an boolean[]
	 * @return boolean[] with true if the pair is allowed to decrypt
	 * and false otherwise
	 */
	public boolean[] getAccessStructure(){
		boolean[] structure = new boolean[3];
		if(check1and2Box.isSelected()){
			structure[0]=true;
		}
		if(check1and3Box.isSelected()){
			structure[1]=true;
		}
		if(check2and3Box.isSelected()){
			structure[2]=true;
		}
		return structure;
	}
	/** generates the current status for showing the number of Foil
	 * 
	 *
	 */
	public void setEncTFText(){
		int curFoil = m_dispatcher.getCurrentFoil();
		int maxFoil = m_dispatcher.getNrOfFoils();
		boolean flipStatus = encFlip.isFlipped();
		if(flipStatus){
			encFlip.flip(false);
		}
		Color c = vcButton.getBackground();
		show1Button.setBackground(c);
		show2Button.setBackground(c);
		show3Button.setBackground(c);
		show4Button.setBackground(c);
		show5Button.setBackground(c);
		if(m_dispatcher.m_option != Dispatcher.DOUBLEDKEY){
			descEncTF.setText("Transparency "+(curFoil+1)+" of "+maxFoil);
			if(curFoil==0){
				prevFoil.setEnabled(false);
				nextFoil.setEnabled(true);
			}else if(curFoil == maxFoil-1){
				nextFoil.setEnabled(false);
				prevFoil.setEnabled(true);
			}else{
				prevFoil.setEnabled(true);
				nextFoil.setEnabled(true);
			}
		}else{
			if(curFoil==0){
				descEncTF.setText("#1: KEY - Transparency");
				prevFoil.setEnabled(false);
				nextFoil.setEnabled(true);
			}else if(curFoil==1){
				descEncTF.setText("#2: Transparency  created with KEY and left image");
				prevFoil.setEnabled(true);
				nextFoil.setEnabled(true);
			}else if(curFoil==2){
				descEncTF.setText("#3: Transparency  created with KEY and right image");
				nextFoil.setEnabled(false);
				prevFoil.setEnabled(true);
			}
		}
		encPanel.setVisible(false);
		encPanel.setVisible(true);
		if(flipStatus){
			encFlip.flip(true);
		}
	}
	public void markCurrentShowButton(Color c){
		if(m_dispatcher.getNrOfFoils()>2 || m_dispatcher.m_option == Dispatcher.DOUBLEDKEY){
			int curFoil = m_dispatcher.getCurrentFoil()+1;
			switch(curFoil){
				case 1: show1Button.setBackground(c);break;
				case 2: show2Button.setBackground(c);break;
				case 3: show3Button.setBackground(c);break;
				case 4: show4Button.setBackground(c);break;
				case 5: show5Button.setBackground(c);break;
			}
		}
	}
	
	/** method to do an update to the menu, sets only the possible 
	 * options for this scenario
	 *
	 */
	public void updateMenu(){
		descInputEP.setText("<html><body><div style=\"vertical-align:top\">"+m_dispatcher.getDescription()+"</div></body></html>");

//		disable/enable some buttons

		m_setForAccess = new boolean[5];
		overlay1Item.setSelected(false);
		overlay2Item.setSelected(false);
		overlay3Item.setSelected(false);
		overlay4Item.setSelected(false);
		overlay5Item.setSelected(false);
		ovl1Item.setSelected(false);
		ovl2Item.setSelected(false);
		ovl3Item.setSelected(false);
		ovl4Item.setSelected(false);
		ovl5Item.setSelected(false);
		
		resultFlip.flip(true);
		encFlip.flip(true);
	}
	
	/** creates the buttons with icons for the toolbar
	 * 
	 * @param imageName - name of the image for the icon without .gif
	 * @param actionCommand - the command to listen
	 * @param toolTipText - the tooltip to display
	 * @param altText - the alternate text if no image
	 * @return JButton with the setted Listeners and Icons
	 */
	protected JButton makeButton(String imageName,
			String actionCommand,
			String toolTipText,
			String altText) {
		//Look for the image.
		String imgLocation = imageName + ".gif";
//		Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(m_vcAppletListener);
		if(!imageName.equalsIgnoreCase("") && imageName != null){
			button.setIcon(ImageIconLoader.loadImageIcon(imgLocation));
		}else{
			button.setText(altText);
		}
//		try {
//			URL iconURL = new URL(this.getCodeBase()+imgLocation);	
//			if(!imageName.equalsIgnoreCase("") && imageName != null){
//				button.setIcon(new ImageIcon(iconURL, altText));
//			}else{
//				button.setText(altText);
//			}
//		} catch (MalformedURLException e) {
//			System.err.println("Sorry, not able to get the icon");
//			button.setText(altText);
//		}	
		return button;
	}
}
