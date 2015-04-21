package enviromine.handlers.Legacy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class LegacyHandler 
{
	protected static List LegacyList = new ArrayList();
	
	public LegacyHandler()
	{

	}
	
	public static void init()
	{
		
		RegisterLegacy(new ConfigLegacy());
		
		
		
		Iterator it = LegacyList.iterator();
		
		while(it.hasNext())
		{
			LegacyHandler legacyFile = (LegacyHandler) it.next();
			
			if(legacyFile.initCheck())
			{
				legacyFile.runLegacy();
			}
			
		}	
	}
	
	public static void RegisterLegacy(LegacyHandler legacyFile)
	{
		if(!LegacyList.contains(legacyFile))
		{
			LegacyList.add(legacyFile);
		}
	}
	
	public abstract boolean initCheck();
	
	public abstract void runLegacy();
	
	
}
