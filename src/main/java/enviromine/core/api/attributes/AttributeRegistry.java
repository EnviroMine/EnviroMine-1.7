package enviromine.core.api.attributes;

import java.util.ArrayList;

public class AttributeRegistry
{
	static ArrayList<AttributeManager> blockManagers = new ArrayList<AttributeManager>();
	static ArrayList<AttributeManager> entityManagers = new ArrayList<AttributeManager>();
	static ArrayList<AttributeManager> itemManagers = new ArrayList<AttributeManager>();
	
	public static void registerManager(AttributeManager manager, Type type)
	{
		ArrayList<AttributeManager> list = getManagerList(type);
		if(!list.contains(manager))
		{
			list.add(manager);
		}
	}
	
	public static ArrayList<AttributeManager> getManagerList(Type type)
	{
		switch(type)
		{
			case BLOCK:
				return blockManagers;
			case ENTITY:
				return entityManagers;
			case ITEM:
				return itemManagers;
			default:
				return new ArrayList<AttributeManager>();
		}
	}
	
	public static enum Type
	{
		BLOCK,
		ENTITY,
		ITEM
	}
}
