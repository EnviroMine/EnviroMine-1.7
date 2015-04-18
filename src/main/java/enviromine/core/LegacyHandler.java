package enviromine.core;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class LegacyHandler 
{
	static String CONFIG_VERSION = "1.0.0";
	
	static String LOADED_VERSION = "1.0.0";
	
	
	public static void initCheck()
	{

		File source = new File(EM_ConfigHandler.configPath);
		File dest = new File(EM_ConfigHandler.defaultProfile);
		try {
		    if(!dest.exists()) FileUtils.copyDirectory(source, dest);
		}catch (IOException e) 
		{
		    e.printStackTrace();
		}
		
	}

}
