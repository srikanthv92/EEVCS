/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 JP iNFOTeCH
 */
package anon.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This class loads resources from the file system. It allows to specify resource paths
 * like "dir1/dir2/resource" or "dir1/dir2/" relative to the classpath or the current directory.
 * It allows also for going up directories like "dir1/dir2/../resource" that would be translated
 * to "dir1/resource".
 * @author Rolf Wendolsky
 */
public final class ResourceLoader
{
	private static final String SYSTEM_RESOURCE_TYPE_ZIP = "ZIP";
	private static final String SYSTEM_RESOURCE_TYPE_JAR = "JAR";
	private static final String SYSTEM_RESOURCE_TYPE_FILE = "FILE";
	private static final String SYSTEM_RESOURCE = "systemresource:/";
	private static final String SYSTEM_RESOURCE_ENDSIGN = "/+/";
	private static final String DIR_UP = "../";
//	private static final String DIR_CURRENT = "./";
	private static final int READ_BUFFER = 2000;

	private static final String RESOURCE_NO_CLASSES_FOUND = "";

	/// holds references to all files in the class path as File objects for caching purposes
	private static Vector ms_classpathFiles;
	/// holds absolute URLs to the resources as Strings; relative paths may be attached
	private static Vector ms_classpathResourceURLs = new Vector();
	/// the resource types: either SYSTEM_RESOURCE_TYPE_ZIP or SYSTEM_RESOURCE_TYPE_FILE
	private static Vector ms_classpathResourceTypes;
	/// stores the parent directory of jar file that holds this class for caching purposes
	private static File ms_parentResourceFile;
	private static String ms_parentResourceFileResourceURL;
	private static String ms_parentResourceFileResourceType;
	/// the class path at the last state it was read
	private static String ms_classpath;

	{
		try
		{
			ms_parentResourceFile =
				new File(ClassUtil.getClassDirectory(
						ResourceLoader.class).getAbsolutePath());
		}
		catch (Exception a_e)
		{
			// the parent resource file could not be loaded
			a_e.printStackTrace(System.err);
			ms_parentResourceFile = null;
		}
		
	}
	
	private ResourceLoader()
	{
	}

	/**
	 * Reads a java.io.InputStream into a byte array and closes the stream.
	 * If the stream blocks before the first byte is read this method will block, too, until
	 * there are any bytes available for reading.
	 * @param a_iStream an InputStream
	 * @throws IOException if an I/O error occurs
	 * @return the InputStream as bytes
	 */
	public static byte[] getStreamAsBytes(InputStream a_iStream) throws IOException
	{
		byte[] data;
		byte[] buffer;
		int readDataLength = 1;

		data = new byte[0];
		// if readDataLength >= 0 there are more bytes available to read
		while(readDataLength >= 0)
		{
			// initialize the buffer
			if (a_iStream.available() > 0)
			{
				buffer = new byte[a_iStream.available()];
			}
			else
			{
				buffer = new byte[READ_BUFFER];
			}

			// read all available data into buffer until the buffer is filled
			readDataLength = a_iStream.read(buffer);
			data = trimByteArray(buffer, readDataLength, data);
		}
		a_iStream.close();

		return data;
	}

	/**
	 * Gets the absolute URL to a requested resource if the resource is found in the class path or
	 * in the local directory. Loads a single resource only, therefore directory specifications like
	 * "home/dir/" are not allowed.
	 * @param a_strRelativeResourcePath the relative path to a resource
	 * @return the absolute URL to the requested resource
	 */
	public static URL getResourceURL(String a_strRelativeResourcePath)
	{
		Vector parentResourceFile;
		Vector parentResourceFileURL;
		Vector parentResourceFileType;
		File localFile;
		URL resourceURL = null;

		if ((a_strRelativeResourcePath = formatResourcePath(a_strRelativeResourcePath)) == null ||
			a_strRelativeResourcePath.endsWith("/"))
		{
			return null;
		}

		// this is the standard method for getting a resource URL from the class path
		resourceURL = ResourceLoader.class.getResource("/" + a_strRelativeResourcePath);

		if (resourceURL == null)
		{
			// try to find the resource in the local directory
			localFile = new File(a_strRelativeResourcePath);
			if (localFile.exists() && localFile.canRead())
			{
				try
				{
					resourceURL = new java.net.URL("file:" + localFile.getAbsolutePath());
				}
				catch (MalformedURLException a_e)
				{
				}
			}
		}

		if (resourceURL == null && ms_parentResourceFile != null &&
			!readFilesFromClasspath().contains(ms_parentResourceFile))
		{
			/**
			 * The parent resource file is not contained in the class path.
			 * Try to load the requested resource directly from the local file.
			 */
			parentResourceFile = new Vector();
			parentResourceFileURL = new Vector();
			parentResourceFileType = new Vector();
			parentResourceFile.addElement(ms_parentResourceFile);
			parentResourceFileURL.addElement(ms_parentResourceFileResourceURL);
			parentResourceFileType.addElement(ms_parentResourceFileResourceType);

			resourceURL = getResourceURL(a_strRelativeResourcePath, parentResourceFile,
										 parentResourceFileURL, parentResourceFileType);
			ms_parentResourceFileResourceURL = (String) parentResourceFileURL.firstElement();
			ms_parentResourceFileResourceType = (String) parentResourceFileType.firstElement();
		}

		/**
		 * This is an other (tricky) implementation that tries to find the resource in the
		 * classpath. It may be used if class.getResource(..) does not work properly.
		 * Please do not remove as this could be important for testing purposes.

		if (resourceURL == null)
		{
			// classPathFiles and classPathResources must be synchronized!
			synchronized (ms_classpathResourceURLs)
			{
				resourceURL = getResourceURL(a_strRelativeResourcePath, readFilesFromClasspath(),
											 ms_classpathResourceURLs, ms_classpathResourceTypes);
			}
		}*/

		return resourceURL;
	}

	/**
	 * Loads a resource from the classpath or the current directory.
	 * The resource may be contained in an archive (ZIP,JAR) or a directory structure.
	 * If the resource could not be found in the classpath, it is loaded from the current
	 * directory. Loads a single resource only, therefore directory specifications like
	 * "home/dir/" are not allowed.
	 * @param a_strRelativeResourcePath a relative filename for the resource
	 * @return the contents of the resource or null if resource could not be loaded
	 */
	public static byte[] loadResource(String a_strRelativeResourcePath)
	{
		InputStream in;

		if ((a_strRelativeResourcePath = formatResourcePath(a_strRelativeResourcePath)) == null ||
			a_strRelativeResourcePath.endsWith("/"))
		{
			return null;
		}

		// load images from the local classpath
		in = ResourceLoader.class.getResourceAsStream("/" + a_strRelativeResourcePath);

		try
		{
			if (in == null && ms_parentResourceFile != null &&
				!readFilesFromClasspath().contains(ms_parentResourceFile))
			{
				/**
				 * The parent resource file is not contained in the class path. Try to load the
				 * resource directly from the parent resource file.
				 */
				in = new FileInputStream(
					new File(ms_parentResourceFile,
							 replaceFileSeparatorsSystemSpecific(a_strRelativeResourcePath)));
			}
		}
		catch (IOException a_e)
		{
		}

		try
		{
			if (in == null)
			{
				// load resource from the current directory
				in = new FileInputStream(a_strRelativeResourcePath);
			}
			return getStreamAsBytes(in);
		}
		catch (IOException a_e)
		{
			return null;
		}
	}

	/**
	 * Loads resources from the classpath or the current directory and instantiates them as
	 * byte arrays. Resources with the same path name
	 * are only loaded from the first source that contains the resource.
	 * If the resource search path is a directory, the method returns a Hashtable with all
	 * instanciated resources. Otherwise, the Hashtable contains only one element or is empty if
	 * no resource was found or no resource could not be instantiated.
	 * The resources may be contained in an archive (JAR) or a directory structure. If no resources
	 * could be found in the classpath or they could not be instanciated by the given
	 * resource instantiator, they are loaded from the current directory.
	 * The resource search path is relative to the given directory and may either specify a single
	 * resource or, if it ends with a slash "/", a (virtual) directory in the given directory.
	 * Therefore a resource search path "/" loads all resources in the specified directory.
	 * A resource search path "certificates/" loads all resources in the (virtual) directory
	 * <i> certificates </i>.
	 * @param a_strResourceSearchPath a relative filename for the resource
	 * @param a_bRecursive true if (virtual) subdirectories should be searched for resources;
	 *                     false otherwise (has an effect only for resource paths ending with "/")
	 * @return the contents of the resources as byte arrays or an empty Hashtable if no resource
	 *         could be loaded
	 */
	public static Hashtable loadResources(String a_strResourceSearchPath, boolean a_bRecursive)
	{
		return loadResources(a_strResourceSearchPath,
							 (new ResourceLoader()).createByteArrayInstantiator(),
							 a_bRecursive);
	}

	/**
	 * Loads resources from the classpath or the current directory and instantiates them as
	 * objects of a type defined by the resource instantiator. Resources with the same path name
	 * are only loaded from the first source that contains the resource.
	 * If the resource search path is a directory, the method returns a Hashtable with all
	 * instanciated resources. Otherwise, the Hashtable contains only one element or is empty if
	 * no resource was found or no resource could not be instantiated.
	 * The resources may be contained in an archive (JAR) or a directory structure. If no resources
	 * could be found in the classpath or they could not be instanciated by the given
	 * resource instantiator, they are loaded from the current directory.
	 * The resource search path is relative to the given directory and may either specify a single
	 * resource or, if it ends with a slash "/", a (virtual) directory in the given directory.
	 * Therefore a resource search path "/" loads all resources in the specified directory.
	 * A resource search path "certificates/" loads all resources in the (virtual) directory
	 * <i> certificates </i>.
	 * @param a_strResourceSearchPath a relative filename for the resource
	 * @param a_instantiator an object that instantiates the loaded resource
	 * @param a_bRecursive true if (virtual) subdirectories should be searched for resources;
	 *                     false otherwise (has an effect only for resource paths ending with "/")
	 * @return the contents of the resource or an empty Hashtable if no resource
	 *         could be loaded
	 */
	public static Hashtable loadResources(String a_strResourceSearchPath,
										  ResourceInstantiator a_instantiator,
										  boolean a_bRecursive)
	{
		Hashtable resources = new Hashtable();
		Enumeration classPathFiles = readFilesFromClasspath().elements();

		while (classPathFiles.hasMoreElements())
		{
			loadResources(a_strResourceSearchPath, (File)classPathFiles.nextElement(),
						  a_instantiator, a_bRecursive, false, resources);
		}
		loadResources(a_strResourceSearchPath, new File(System.getProperty("user.dir")),
					  a_instantiator, a_bRecursive, false, resources);

		return resources;
	}

	/**
	 * Loads resources from a directory and instantiates them as byte arrays.
	 * If the resource search path is a directory, the method returns a Hashtable with all
	 * instanciated resources. Otherwise, the Hashtable contains only one element or is empty if
	 * no resource was found or no resource could not be instantiated.
	 * The resources may be contained in an archive (JAR) or a directory structure.
	 * The resource search path is relative to the given directory and may either specify a single
	 * resource or, if it ends with a slash "/", a (virtual) directory in the given directory.
	 * Therefore a resource search path "/" loads all resources in the specified directory.
	 * A resource search path "certificates/" loads all resources in the (virtual) directory
	 * <i> certificates </i>.
	 * @param a_strResourceSearchPath a relative filename for the resource
	 * @param a_directory a simple file, directory or zip file
	 * @param a_bRecursive true if (virtual) subdirectories should be searched for resources;
	 *                     false otherwise (has an effect only for resource paths ending with "/")
	 * @return the contents of the resource or an empty Hashtable if no resource
	 *         could be loaded
	 */
	public static Hashtable loadResources(String a_strResourceSearchPath,
										  File a_directory,
										  boolean a_bRecursive)
	{
		Hashtable resources = new Hashtable();
		loadResources(a_strResourceSearchPath, a_directory,
					  (new ResourceLoader()).createByteArrayInstantiator(),
					  a_bRecursive, false, resources);
		return resources;
	}

	/**
	 * Loads resources from a directory and instantiates them as
	 * objects of a type defined by the resource instantiator.
	 * If the resource search path is a directory, the method returns a Hashtable with all
	 * instanciated resources. Otherwise, the Hashtable contains only one element or is empty if
	 * no resource was found or no resource could not be instantiated.
	 * The resources may be contained in an archive (JAR) or a directory structure.
	 * The resource search path is relative to the given directory and may either specify a single
	 * resource or, if it ends with a slash "/", a (virtual) directory in the given directory.
	 * Therefore a resource search path "/" loads all resources in the specified directory.
	 * A resource search path "certificates/" loads all resources in the (virtual) directory
	 * <i> certificates </i>.
	 * @param a_strResourceSearchPath a relative filename for the resource
	 * @param a_directory a simple file, directory or zip file
	 * @param a_instantiator an object that instantiates the loaded resource
	 * @param a_bRecursive true if (virtual) subdirectories should be searched for resources;
	 *                     false otherwise (has an effect only for resource paths ending with "/")
	 * @return the contents of the resource or an empty Hashtable if no resource
	 *         could be loaded
	 */
	public static Hashtable loadResources(String a_strResourceSearchPath,
										  File a_directory,
										  ResourceInstantiator a_instantiator,
										  boolean a_bRecursive)
	{
		Hashtable resources = new Hashtable();
		loadResources(a_strResourceSearchPath, a_directory,
					  a_instantiator, a_bRecursive, false, resources);
		return resources;
	}

	/**
	 * Interprets a String as a filename and converts its file separators to
	 * system specific file separators.
	 * @param a_filename a generic file name
	 * @return a system specific file name
	 */
	public static String replaceFileSeparatorsSystemSpecific(String a_filename)
	{
		if (a_filename == null)
		{
			return null;
		}
		a_filename = a_filename.replace('/', File.separatorChar);
		a_filename = a_filename.replace('\\', File.separatorChar);

		return a_filename;
	}

	/**
	 * Returns a given a requested system resource as a file. A system resource is either
	 * a zip file or a directory that is specified in the classpath
	 * (Property <Code> java.class.path </Code>). The resource must be specified with by
	 * the following protocol syntax:
	 * <DL>
	 * <DT> ZipFile </DT>
	 * <DD> :systemresource:/ZIP[id]/+/ </DD>
	 * <DT> File </DT>
	 * <DD> :systemresource:/FILE[id]/+/ </DD>
	 * </DL>
	 * [id] may be an integer specifying the resource's position int the classpath
	 * (beginning with 0) or an absolute path containing the requested resource. The end sign
	 * '/+/' is optional and marks the end of the [id].
	 * The system resource protocol is only used in old JDKs < 1.2.
	 *
	 * @param a_systemResource a system resource a String
	 * @return The requested system resource as a file or null if the resource could not be found
	 */
	protected static File getSystemResource(String a_systemResource)
	{
		int endIndex;

		if (a_systemResource.indexOf(SYSTEM_RESOURCE) != 0)
		{
			return null;
		}

		// find the beginning of the [id] string
		a_systemResource =
			a_systemResource.substring(SYSTEM_RESOURCE.length(), a_systemResource.length());
		if (a_systemResource.toUpperCase().startsWith(SYSTEM_RESOURCE_TYPE_ZIP))
		{
			a_systemResource = a_systemResource.substring(
				 SYSTEM_RESOURCE_TYPE_ZIP.length(), a_systemResource.length());
		}
		else if (a_systemResource.toUpperCase().startsWith(SYSTEM_RESOURCE_TYPE_JAR))
		{
			a_systemResource = a_systemResource.substring(
				 SYSTEM_RESOURCE_TYPE_JAR.length(), a_systemResource.length());
		}
		else if (a_systemResource.toUpperCase().startsWith(SYSTEM_RESOURCE_TYPE_FILE))
		{
			a_systemResource = a_systemResource.substring(
				 SYSTEM_RESOURCE_TYPE_FILE.length(), a_systemResource.length());
		}

		// now find the end of the [id] string and extract the [id]
		endIndex = a_systemResource.indexOf(SYSTEM_RESOURCE_ENDSIGN);
		if (endIndex >= 0)
		{
			a_systemResource = a_systemResource.substring(0, endIndex);
		}

	   // try to interpret the [id] as an integer number
		try
		{
			return (File)readFilesFromClasspath().elementAt(Integer.parseInt(a_systemResource));
		}
		catch (Exception a_e)
		{
			// the [id] seems to be a file path
			return new File(a_systemResource);
		}
	}

	/**
	 * Loads resources from a given directory (simple file, directory or zip file) that are found
	 * in the given resource search path and instantiates them as objects of a type defined by the
	 * resource instantiator. Resources with the same path name
	 * are only loaded once according to the given Hashtable.
	 * The resource search path is relative to the given directory and may either specify a single
	 * resource or, if it ends with a slash "/", a (virtual) directory in the given directory.
	 * Therefore a resource search path "/" loads all resources in the specified directory.
	 * A resource search path "certificates/" loads all resources in the (virtual) directory
	 * <i> certificates </i>.
	 * @param a_strResourceSearchPath a (virtual) path in the directory to load resources from
	 * @param a_Directory a simple file, directory or zip file
	 * @param a_instantiator a ResourceInstantiator that is used to instantiate the loaded resources
	 * @param a_bRecursive true if (virtual) subdirectories should be searched for resources;
	 *                     false otherwise (has an effect only for resource paths ending with "/")
	 * @param a_bStopAtFirstResource true if the search should stop with the first loaded resource;
	 *                               false otherwise
	 * @param a_loadedResources a Hashtable where the loaded and instantiated resources are stored
	 */
	protected static void loadResources(String a_strResourceSearchPath,
										File a_Directory,
										ResourceInstantiator a_instantiator,
										boolean a_bRecursive,
										boolean a_bStopAtFirstResource,
										Hashtable a_loadedResources)
	{
		Enumeration entries;

		if ((a_strResourceSearchPath = formatResourcePath(a_strResourceSearchPath)) == null ||
			a_loadedResources == null || a_Directory == null || a_instantiator == null ||
			!a_Directory.exists() || !a_Directory.canRead())
		{
			return;
		}

		try
		{
			// try to fetch the objects as the file was a zip file
			if (a_Directory.isDirectory())
			{
				throw new IOException("This is a directory.");
			}

			Object object;
			ZipFile zipfile;
			ZipEntry zipentry;
			String strCurrentResourcePath;

			zipfile = new ZipFile(a_Directory);
			// if the search path is a single file, try to load it directly
			if (!a_strResourceSearchPath.endsWith("/"))
			{
				zipentry = zipfile.getEntry(a_strResourceSearchPath);
				if (zipentry == null)
				{
					throw new IOException ("Requested entry not found.");
				}
				// we have found the requested entry
				Vector temp = new Vector();
				temp.addElement(zipentry);
				entries = temp.elements();
			}
			else
			{
				// search all entries for the requested ones
				entries = zipfile.entries();
			}

			while (entries.hasMoreElements())
			{
				zipentry = (ZipEntry) entries.nextElement();
				if (zipentry.isDirectory() ||
					!isResourceInSearchPath(zipentry.toString(), a_strResourceSearchPath,
											a_bRecursive))
				{
					continue;
				}

				object = null;
				try
				{
					object = a_instantiator.getInstance(zipentry, zipfile);

				}
				catch (ResourceInstantiator.ResourceInstantiationException a_e)
				{
					return;
				}
				catch (Exception a_e)
				{
				}
				if (object != null)
				{
					strCurrentResourcePath = getCurrentResourcePath(zipentry);
					if (!a_loadedResources.containsKey(strCurrentResourcePath))
					{
						a_loadedResources.put(strCurrentResourcePath, object);
						if (!a_strResourceSearchPath.endsWith("/") || a_bStopAtFirstResource)
						{
							// the requested resource has been found
							return;
						}
					}
				}
			}
		}
		catch (Exception a_e)
		{
			// this seems to be no valid zip file; treat it as a simple file or directory
			try
			{
				loadResourcesFromFile(a_strResourceSearchPath, a_Directory, a_Directory,
									  a_instantiator, a_loadedResources,
									  a_bRecursive, a_bStopAtFirstResource);
			}
			catch (ResourceInstantiator.ResourceInstantiationException a_ex)
			{
				return;
			}
		}
	}

	/**
	 * Returns all resources in a directory and the directory itself as Objects
	 * if they are resources of the type specified by the ResourceInstantiator.
	 * @param a_strResourceSearchPath the (virtual) path in the file to load resources from
	 * @param a_file a resource file or directory
	 * @param a_topDirectory the directory where all other files and directories reside
	 * @param a_instantiator an object that instantiates the loaded resource
	 * @param a_loadedResources a Vector where the loaded and instantiated resources are stored
	 * @param a_bRecursive true if subdirectories should be visited; false otherwise
	 * @param a_bStopAtFirstResource true if the search should stop with the first loaded resource;
	 *                               false otherwise
	 * @throws ResourceInstantiator.ResourceInstantiationException if the ResourceInstantiator
	 *         has become invalid because of too many errors
	 */
	private static void loadResourcesFromFile(String a_strResourceSearchPath,
											  File a_file, File a_topDirectory,
											  ResourceInstantiator a_instantiator,
											  Hashtable a_loadedResources,
											  boolean a_bRecursive,
											  boolean a_bStopAtFirstResource)
		throws ResourceInstantiator.ResourceInstantiationException
	{
		String[] filesArray;
		String strCurrentResourcePath;

		if ((!a_strResourceSearchPath.endsWith("/") || a_bStopAtFirstResource)
			&& a_loadedResources.size() > 0)
		{
			// the requested resource has already been found
			return;
		}

		if (a_file != null && a_file.exists())
		{
			strCurrentResourcePath = getCurrentResourcePath(a_file, a_topDirectory);

			// jump to the search path if the current file is not contained in the search path
			if (strCurrentResourcePath.indexOf(a_strResourceSearchPath) != 0 &&
				!a_strResourceSearchPath.equals("/"))
			{
				a_file = new File(a_topDirectory,
								  replaceFileSeparatorsSystemSpecific(a_strResourceSearchPath));
				loadResourcesFromFile(a_strResourceSearchPath, a_file, a_topDirectory,
									  a_instantiator, a_loadedResources,
									  a_bRecursive, a_bStopAtFirstResource);
				return;
			}

			if (a_file.isFile() && isResourceInSearchPath(
				 strCurrentResourcePath, a_strResourceSearchPath, a_bRecursive))
			{
				Object object = null;

				if (a_loadedResources.containsKey(strCurrentResourcePath))
				{
					// this file has already been loaded
					return;
				}

				try
				{
					object = a_instantiator.getInstance(a_file, a_topDirectory);

				}
				catch (ResourceInstantiator.ResourceInstantiationException a_e)
				{
					throw a_e;
				}
				catch (Exception a_e)
				{
				}

				if (object != null)
				{
					a_loadedResources.put(strCurrentResourcePath, object);
					if (!a_strResourceSearchPath.endsWith("/") || a_bStopAtFirstResource)
					{
						// the requested resource has been found
						return;
					}
				}
			}
			else if (a_file.isDirectory() && isResourceInSearchPath(
			 strCurrentResourcePath, a_strResourceSearchPath, a_bRecursive)) {

				filesArray = a_file.list();
				for (int i = 0; i < filesArray.length; i++)
				{
					// JDK 1.1.8 adds a separator char to the absolute directory path; remove it
					String separatorChar = "" + File.separatorChar;
					if (a_file.getAbsolutePath().endsWith(separatorChar))
					{
						separatorChar = ""; // JDK 1.1.8
					}

					loadResourcesFromFile(
					   a_strResourceSearchPath,
					   new File(a_file.getAbsolutePath() + separatorChar + filesArray[i]),
					   a_topDirectory, a_instantiator, a_loadedResources, a_bRecursive,
					   a_bStopAtFirstResource);
				}
			}
		}
	}

	/**
	 * Gets the absolute URL to a requested resource if the resource is found in the
	 * given resource files. In addition to the resource files, the absolute URLs and file types
	 * of the resource files must be given. If the correct values are unknown, the must be
	 * replaced by "null".
	 * @param a_strRelativeResourcePath the relative path to a resource
	 * @param a_resourceFiles the resource files
	 * @param a_resourceURLs the absolute URLs to the resource files
	 * @param a_resourceTypes the file types of the resource files, either SYSTEM_RESOURCE_TYPE_ZIP
	 *                        or SYSTEM_RESOURCE_TYPE_FILE
	 * @return URL
	 */
	private static URL getResourceURL(String a_strRelativeResourcePath,
									  Vector a_resourceFiles,
									  Vector a_resourceURLs,
									  Vector a_resourceTypes)
	{
		File classPathFile;
		String strRelativeResourcePath;
		String classPathResourceURL;
		Enumeration resourceFiles = a_resourceFiles.elements();
		Class firstClassFound;
		FileTypeInstantiator instantiator = (new ResourceLoader()).createFileTypeInstantiator();
		Hashtable resourceType;

		for (int i = 0; resourceFiles.hasMoreElements(); i++)
		{
			classPathFile = (File) resourceFiles.nextElement();
			classPathResourceURL = (String) a_resourceURLs.elementAt(i);
			if (classPathResourceURL == null)
			{
				// this resource file has not been searched for classes before
				firstClassFound = ClassUtil.getFirstClassFound(classPathFile);
				if (firstClassFound == null)
				{
					// no classes have been found in this resource file;
					a_resourceURLs.setElementAt(RESOURCE_NO_CLASSES_FOUND, i);
					continue;
				}
				strRelativeResourcePath = ClassUtil.toRelativeResourcePath(firstClassFound);

				// get the resource type
				resourceType = new Hashtable();
				loadResources(strRelativeResourcePath, classPathFile, instantiator,
							  false, true, resourceType);
				a_resourceTypes.setElementAt(resourceType.elements().nextElement(), i);

				// extract the URL to the resource directory from the parent class directory
				strRelativeResourcePath = "/" + strRelativeResourcePath;
				classPathResourceURL =
					firstClassFound.getResource(strRelativeResourcePath).toString();
				if (!classPathResourceURL.endsWith(strRelativeResourcePath))
				{
					// this should never happen...
					continue;
				}
				classPathResourceURL = classPathResourceURL.substring(
					0, classPathResourceURL.lastIndexOf(strRelativeResourcePath));
				a_resourceURLs.setElementAt(classPathResourceURL, i);
			}
			else if (classPathResourceURL.trim().equals(RESOURCE_NO_CLASSES_FOUND))
			{
				// no classes have been found in this resource file
				continue;
			}

			if (a_resourceTypes.elementAt(i).equals(SYSTEM_RESOURCE_TYPE_FILE))
			{
				// this resource file is a directory
				File testfile = new File(classPathFile, replaceFileSeparatorsSystemSpecific(
					a_strRelativeResourcePath));

				// test if the file exists in this directory
				if (!testfile.exists())
				{
					// the requested resource does not exist in this directory
					continue;
				}
			}
			else
			{
				// this resource file is a zip file; test if the resource exists in this file
				try
				{
					if (new ZipFile(classPathFile).getEntry(a_strRelativeResourcePath)
						!= null)
					{
						/**
						 * The requested resource has been found. This contruction might look
						 * a bit complicated, but it is needed for JView under Windows.
						 * If we test "==null" followed by "continue", the virtual machine stops
						 * without any messages.
						 */
					}
					else
					{
						// the requested resource does not exist in this zip file
						continue;
					}
				}
				catch (Exception a_e)
				{
					continue;
				}
			}

			// prepare the relative resource path
			if (!a_strRelativeResourcePath.startsWith("/"))
			{
				a_strRelativeResourcePath = "/" + a_strRelativeResourcePath;
			}

			// construct the URL for the file
			try
			{
				return new URL(classPathResourceURL + a_strRelativeResourcePath);
			}
			catch (MalformedURLException a_e)
			{
				// should never happen
			}
		}

		return null;
	}

	/**
	 * Gets the relative resource path for the currently parsed file.
	 * @param a_currentFile the currently parsed file
	 * @param a_topDirectory the top directory in that this file resides; the resource path is
	 *                       given relative to this directory
	 * @return the relative resource path
	 */
	private static String getCurrentResourcePath(File a_currentFile, File a_topDirectory)
	{

		if(	a_currentFile.toString().equals(a_topDirectory.toString()))
		{
			return "/";
		}

		String strCurrentFile;
		int separator = 1;

		// JDK 1.1.8 adds a separator after the directory
		if (a_topDirectory.toString().endsWith(File.separator))
		{
			separator = 0; // JDK 1.1.8
		}

		strCurrentFile = a_currentFile.toString().substring(
				  a_topDirectory.toString().length() + separator, a_currentFile.toString().length());
		strCurrentFile = strCurrentFile.replace('\\','/');

		if (a_currentFile.isDirectory() && !strCurrentFile.endsWith("/"))
		{
			strCurrentFile = strCurrentFile + "/";
		}

		return strCurrentFile;
	}

	/**
	 * Gets the relative resource path for the currently parsed zip entry.
	 * @param a_currentEntry the currently parsed zip entry
	 * @return the relative resource path
	 */
	private static String getCurrentResourcePath(ZipEntry a_currentEntry)
	{
		if (a_currentEntry.isDirectory() && !a_currentEntry.toString().endsWith("/"))
		{
			return a_currentEntry.toString() + "/";
		}
		return a_currentEntry.toString();
	}

	/**
	 * Tests if the currently parsed resource is in the search path.
	 * @param a_strCurrentResourcePath the currently parsed resource
	 * @param a_strResourceSearchPath the resource search path
	 * @param a_bRecursive true if subdirectories should be visited; false otherwise
	 * @return true if the current resource path is in the search path; false otherwise
	 */
	private static boolean isResourceInSearchPath(String a_strCurrentResourcePath,
												  String a_strResourceSearchPath,
												  boolean a_bRecursive)
	{
		if (a_strCurrentResourcePath.equals(a_strResourceSearchPath) ||
			a_strCurrentResourcePath.equals("/"))
		{
			// the entry was found or this is the directory itself
			return true;
		}
		if (a_strResourceSearchPath.equals("/"))
		{
			if (a_bRecursive)
			{
				// all resources in the file are loaded
				return true;
			}
			if (a_strCurrentResourcePath.indexOf("/") >= 0)
			{
				// the search is not recursive and this is a directory or a file in a subdirectory
				return false;
			}
		}
		if (a_strCurrentResourcePath.length() <= a_strResourceSearchPath.length())
		{
			// this cannot be the wanted entry (don't remove, important for zip files)
			return false;
		}

		if (a_strCurrentResourcePath.startsWith(a_strResourceSearchPath))
		{

			// OK, the entry in the search path; now test if it is in a subdirectory
			if (a_strResourceSearchPath.endsWith("/"))
			{
				if (a_bRecursive)
				{
					return true;
				}
				if (a_strCurrentResourcePath.substring(
								a_strResourceSearchPath.length()).indexOf("/") < 0)
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Reformats a given resource name in a way it can be easily interpreted by the resource
	 * loader methods. The resulting paths are either null or of the form
	 * "dir1/dir2/resource", "dir1/dir2/" or "/". All other cases, including going up directory
	 * trees by inserting "../", are handled by this method and transformed to one of the four
	 * defined forms.
	 * @param a_strRelativeResourcePath the relative path to a resource
	 * @return the formatted file name or null if the file name is illegal
	 */
	private static String formatResourcePath(String a_strRelativeResourcePath)
	{
		int index, tempIndex;
		String temp;

		if (a_strRelativeResourcePath == null)
		{
			return null;
		}
		// trim leading and ending white spaces and replace file separators as needed
		a_strRelativeResourcePath = a_strRelativeResourcePath.trim().replace('\\', '/');

		if (a_strRelativeResourcePath.equals("/"))
		{
			// this path specifies all resources contained in it
			return a_strRelativeResourcePath;
		}

		if (a_strRelativeResourcePath.length() == 0 || a_strRelativeResourcePath.startsWith("/"))
		{
			// invalid relative path
			return null;
		}

		// interpret all "/../" as going up the tree
		while ((index = a_strRelativeResourcePath.indexOf("/" + DIR_UP)) >= 0)
		{
			if (a_strRelativeResourcePath.startsWith(DIR_UP))
			{
				// invalid relative path
				return null;
			}

			temp = a_strRelativeResourcePath.substring(0, index);
			if ((tempIndex = temp.lastIndexOf("/")) >= 0)
			{
				temp = temp.substring(0, tempIndex + 1);
			}
			else
			{
				temp = "/";
			}
			temp += a_strRelativeResourcePath.substring(index + ("/" + DIR_UP).length(),
				a_strRelativeResourcePath.length());

			a_strRelativeResourcePath = temp;
			while (a_strRelativeResourcePath.startsWith("/"))
			{
				if (a_strRelativeResourcePath.equals("/"))
				{
					break;
				}
				a_strRelativeResourcePath = a_strRelativeResourcePath.substring(
								1, a_strRelativeResourcePath.length());
			}
		}
		if (a_strRelativeResourcePath.startsWith(DIR_UP))
		{
			return null;
		}

		/*
		while ((index = a_strRelativeResourcePath.lastIndexOf(DIR_CURRENT)) >= 0)
		{

			if (a_strRelativeResourcePath.equals(DIR_CURRENT))
			{
				a_strRelativeResourcePath = "";
			}
			else if (index == 0)
			{
				a_strRelativeResourcePath =
					a_strRelativeResourcePath.substring(index + DIR_CURRENT.length(),
					a_strRelativeResourcePath.length());
			}
			else if (a_strRelativeResourcePath.charAt(index - 1) == '/')
			{
				temp = a_strRelativeResourcePath.substring(0, index) +
					a_strRelativeResourcePath.substring(index + DIR_CURRENT.length(),
					a_strRelativeResourcePath.length());
				a_strRelativeResourcePath = temp;
			}
		}*/

		return a_strRelativeResourcePath;
	}

	/**
	 * Trims a byte array in a way that all bytes after the given length <code> a_maxLength </code>
	 * are cut off.
	 * Afterwards, a new byte array is constructed with the bytes from the given
	 * <code> a_arrayToAppendTo </code> and the trimmed array. If <code> a_maxLength </code> is
	 * smaller than or equal to zero a reference to <code> a_arrayToAppendTo </code> is returned.
	 * @param a_trimmedArray the byte array to trim
	 * @param a_maxLength the maximum length of the trimmed byte array
	 * @param a_arrayToAppendTo the array to append the trimmed byte array to
	 * @return the concatenated array
	 */
	private static byte[] trimByteArray(byte[] a_trimmedArray, int a_maxLength,
										byte[] a_arrayToAppendTo)
	{
		byte[] temp;

		if  (a_maxLength <= 0)
		{
			temp = a_arrayToAppendTo;
		}
		else
		{
			int trimmedLength;
			if (a_trimmedArray.length > a_maxLength)
			{
				trimmedLength = a_maxLength;
			}
			else
			{
				trimmedLength = a_trimmedArray.length;
			}

			temp = new byte[a_arrayToAppendTo.length + trimmedLength];

			System.arraycopy(a_arrayToAppendTo, 0, temp, 0, a_arrayToAppendTo.length);
			System.arraycopy(a_trimmedArray, 0, temp, a_arrayToAppendTo.length, trimmedLength);
		}

		return temp;
	}

	/**
	 * Reads all resources from the classpath and stores them as files.
	 * The method does nothing if the classpath has not changed since the last call.
	 * @return all resources from the classpath as files
	 */
	private static Vector readFilesFromClasspath()
	{
		String classpath = System.getProperty("java.class.path");

		if (ms_classpath == null || !ms_classpath.equals(classpath))
		{
			synchronized (ms_classpathResourceURLs)
			{
				StringTokenizer tokenizer;

				ms_classpath = classpath;
				ms_classpathFiles = new Vector();
				ms_classpathResourceURLs = new Vector();
				ms_classpathResourceTypes = new Vector();

				tokenizer = new StringTokenizer(ms_classpath, System.getProperty("path.separator"));
				while (tokenizer.hasMoreTokens())
				{
					ms_classpathFiles.addElement(
					   new File(new File(tokenizer.nextToken()).getAbsolutePath()));
					ms_classpathResourceURLs.addElement((Class)null);
					ms_classpathResourceTypes.addElement((String)null);
				}
			}
		}
		return ms_classpathFiles;
	}

	/**
	 * Returns a new ByteArrayInstantiator.
	 * This method is needed due to static centext restrictions.
	 * @return a new ByteArrayInstantiator
	 */
	private ByteArrayInstantiator createByteArrayInstantiator()
	{
		return new ByteArrayInstantiator();
	}

	/**
	 * Returns a new FileTypeInstantiator.
	 * This method is needed due to static centext restrictions.
	 * @return a new FileTypeInstantiator
	 */
	private FileTypeInstantiator createFileTypeInstantiator()
	{
		return new FileTypeInstantiator();
	}

	/**
	 * This class is used to get resources as byte arrays.
	 */
	private final class ByteArrayInstantiator implements ResourceInstantiator
	{
		public Object getInstance(File a_file, File a_topDirectory)
			throws Exception
		{
				return getStreamAsBytes(new FileInputStream(a_file));
		}

		public Object getInstance(ZipEntry a_entry, ZipFile a_file)
			throws Exception
		{
			return getStreamAsBytes(a_file.getInputStream(a_entry));
		}
	}

	/**
	 * Does not load or instantiate resources but returns the file type of resources. The file
	 * type may either be SYSTEM_RESOURCE_TYPE_ZIP or SYSTEM_RESOURCE_TYPE_FILE.
	 */
	private final class FileTypeInstantiator implements ResourceInstantiator
	{
		public Object getInstance(File a_file, File a_topDirectory)
		{
			return SYSTEM_RESOURCE_TYPE_FILE;
		}

		public Object getInstance(ZipEntry a_entry, ZipFile a_file)
		{
			return SYSTEM_RESOURCE_TYPE_ZIP;
		}
	}
}
