package enviromine.core.utils;

import java.util.Set;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class NBTMatcher
{
	@SuppressWarnings("unchecked")
	public boolean ContainsNBT(NBTTagCompound nbtBase, NBTTagCompound nbtPart)
	{
		if(nbtBase.equals(nbtPart)) // Quick shortcut if they are already equal
		{
			return true;
		}
		
		for(String key : (Set<String>)nbtPart.func_150296_c())
		{
			if(!nbtBase.hasKey(key)) // Base tag is missing the partial tag!
			{
				return false;
			}
			
			NBTBase baseTag = nbtBase.getTag(key);
			NBTBase partTag = nbtPart.getTag(key);
			
			if(partTag instanceof NBTTagCompound && baseTag instanceof NBTTagCompound)
			{
				if(!ContainsNBT((NBTTagCompound)baseTag, (NBTTagCompound)partTag))
				{
					return false;
				}
			} else
			{
				if(!partTag.equals(nbtBase.getTag(key)))
				{
					return false;
				}
			}
		}
		
		return true;
	}
}
