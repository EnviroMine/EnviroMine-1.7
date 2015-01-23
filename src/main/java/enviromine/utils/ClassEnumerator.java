package enviromine.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassEnumerator
{
	public static Class<?> loadClass(String className)
	{
		try
		{
			return Class.forName(className);
		} catch(ClassNotFoundException e)
		{
			throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
		}
	}
	
	public static void processDirectory(File directory, String pkgname, ArrayList<Class<?>> classes)
	{
		// Get the list of the files contained in the package
		String[] files = directory.list();
		for(int i = 0; i < files.length; i++)
		{
			String fileName = files[i];
			String className = null;
			// we are only interested in .class files
			if(fileName.endsWith(".class"))
			{
				// removes the .class extension
				className = pkgname + '.' + fileName.substring(0, fileName.length() - 6);
			}
			if(className != null)
			{
				classes.add(loadClass(className));
			}
			File subdir = new File(directory, fileName);
			if(subdir.isDirectory())
			{
				processDirectory(subdir, pkgname + '.' + fileName, classes);
			}
		}
	}
	
	public static void processJarfile(URL resource, String pkgname, ArrayList<Class<?>> classes)
	{
		String relPath = pkgname.replace('.', '/');
		String resPath = resource.getPath();
		String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
		JarFile jarFile;
		try
		{
			jarFile = new JarFile(jarPath);
		} catch(IOException e)
		{
			throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
		}
		Enumeration<JarEntry> entries = jarFile.entries();
		while(entries.hasMoreElements())
		{
			JarEntry entry = entries.nextElement();
			String entryName = entry.getName();
			String className = null;
			if(entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length()))
			{
				className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
			}
			if(className != null)
			{
				classes.add(loadClass(className));
			}
		}
		
		try
		{
			jarFile.close();
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static ArrayList<Class<?>> getClassesForPackage(Package pkg)
	{
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		
		String pkgname = pkg.getName();
		String relPath = pkgname.replace('.', '/');
		
		// Get a File object for the package
		URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
		if(resource == null)
		{
			throw new RuntimeException("Unexpected problem: No resource for " + relPath);
		}
		
		if(resource.toString().startsWith("jar:"))
		{
			processJarfile(resource, pkgname, classes);
		} else
		{
			try
			{
				processDirectory(new File(URLDecoder.decode(resource.getPath(), "UTF-8")), pkgname, classes);
			} catch(Exception e)
			{
				throw new RuntimeException("Unexpected problem: Unable to get path from " + resource.getPath());
			}
		}
		
		return classes;
	}
}
