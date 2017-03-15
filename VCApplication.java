/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jb;

import javax.swing.JApplet;
import javax.swing.JFrame;
/** run the VCApplet as an application
 *
 */
public class VCApplication{
	static final JApplet applet = new VCApplet();
	/**starts the applet in a frame
	 * 
	 * @param argv - the normal argv[] - here it is empty
	 */
	static public void main (String argv[]) {
//		if(argv.length==1){
//		    JFrame frame = new JFrame ("Embedded Extended Visual Cryptography Schemes");
//		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		    frame.getContentPane().add ("Center", applet);
////		    applet.setStub(new VCAppletStub (applet, argv));
//		    frame.setSize(700,700);
//		    frame.setVisible(true);
//		    applet.init();
//		    applet.start();
////		    frame.pack();
//		}else{
//			//print out the usage term
//			System.out.println("usage: java VCApplication host=<path>");
//			System.out.println("<path> = path to the pictures to load");
//			System.out.println("for windows for example:\n host=\"C:\\Dokumente und Einstellungen\\Boßle Johannes\\Eigene Dateien\\sec\\vc_work\\vc\"");
//			System.out.println("or \"\" if nothing was changed (vc.jar and the pics are in the same directory)");
//			System.out.println("trying to continue anyway...");
//			String[] arg = {"host=\"\""};
//			VCApplication.main(arg);
//		}
	    JFrame frame = new JFrame ("Embedded Extended Visual Cryptography Schemes");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().add ("Center", applet);
//	    applet.setStub(new VCAppletStub (applet, argv));
	    frame.setSize(700,740);
	    frame.setVisible(true);
	    applet.init();
	    applet.start();		
	}
}
