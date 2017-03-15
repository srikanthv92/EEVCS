/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/*
 JP iNFOTeCH
 */
package gui;

import java.util.Hashtable;
import java.awt.Image;
import java.awt.MediaTracker;
import javax.swing.ImageIcon;

import anon.util.ResourceLoader;
//import logging.LogHolder;
//import logging.LogLevel;
//import logging.LogType;

/**
 * This class loads resources from the file system.
 */
final public class ImageIconLoader
{
	// all loaded icons are stored in the cache and do not need to be reloaded from file
	private static Hashtable ms_iconCache = new Hashtable();

	private ImageIconLoader()
	{
	}

	/**
	 * Loads an ImageIcon from the classpath or the current directory.
	 * The icon may be contained in an archive (JAR) or a directory structure. If the icon could
	 * not be found in the classpath, it is loaded from the current directory.
	 * Once an icon is loaded, it is stored in a memory cache, so that further calls of this method
	 * do not load the icon from the file system, but from the cache.
	 * @param a_strRelativeImagePath the relative resource path or filename of the Image
	 * @return the loaded ImageIcon or null if the icon could not be loaded
	 *         (getImageLoadStatus() == java.awt.MediaTracker.ERRORED)
	 */
	public static ImageIcon loadImageIcon(String a_strRelativeImagePath)
	{
		return loadImageIcon(a_strRelativeImagePath, true);
	}

	/**
	 * Loads an ImageIcon from the classpath or the current directory.
	 * The icon may be contained in an archive (JAR) or a directory structure. If the icon could
	 * not be found in the classpath, it is loaded from the current directory.
	 * Once an icon is loaded, it is stored in a memory cache, so that further calls of this method
	 * do not load the icon from the file system, but from the cache.
	 * The image may be loaded synchronously so that the method only returns when the image has been
	 * loaded completely (or an error occured), or asynchronously so that the method returns even if
	 * the image has not been loaded yet.
	 * @param a_strRelativeImagePath the relative resource path or filename of the Image
	 * @param a_bSync true if the image is loaded synchronously; false otherwise
	 * @return the loaded ImageIcon or null if the icon could not be loaded
	 *         (getImageLoadStatus() == java.awt.MediaTracker.ERRORED)
	 */
	public static ImageIcon loadImageIcon(String a_strRelativeImagePath, boolean a_bSync)
	{
		ImageIcon img;
		int statusBits;

		// try to load the image from the cache
		if (ms_iconCache.containsKey(a_strRelativeImagePath))
		{
			return new ImageIcon((Image)ms_iconCache.get(a_strRelativeImagePath));
		}

		// load image from the local classpath or the local directory
		try
		{
			img = new ImageIcon(ResourceLoader.getResourceURL(a_strRelativeImagePath));
		}
		catch (NullPointerException a_e)
		{
			img = null;
		}

		if (img != null)
		{
			if (a_bSync)
			{
				statusBits = MediaTracker.ABORTED | MediaTracker.ERRORED | MediaTracker.COMPLETE;
				while ((img.getImageLoadStatus() & statusBits) == 0)
				{
					Thread.yield();
				}
			}

			// write the image to the cache
			ms_iconCache.put(a_strRelativeImagePath, img.getImage());
		}

		statusBits = MediaTracker.ABORTED | MediaTracker.ERRORED;
//		if (img == null || (img.getImageLoadStatus() & statusBits) != 0)
//		{
//			LogHolder.log(LogLevel.INFO, LogType.GUI,
//						  "Could not load requested image '" + a_strRelativeImagePath + "'!");
//		}

		return img;
	}
}
