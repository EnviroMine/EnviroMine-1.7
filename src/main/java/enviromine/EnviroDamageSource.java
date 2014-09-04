package enviromine;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;

public class EnviroDamageSource extends DamageSource
{
	public static EnviroDamageSource heatstroke = (EnviroDamageSource)(new EnviroDamageSource("heatstroke")).setDamageBypassesArmor();
	public static EnviroDamageSource organfailure = (EnviroDamageSource)(new EnviroDamageSource("organfailure")).setDamageBypassesArmor();
	public static EnviroDamageSource bleedout = (EnviroDamageSource)(new EnviroDamageSource("bleedout")).setDamageBypassesArmor();
	public static EnviroDamageSource suffocate = (EnviroDamageSource)(new EnviroDamageSource("suffocate")).setDamageBypassesArmor();
	public static EnviroDamageSource frostbite = (EnviroDamageSource)(new EnviroDamageSource("frostbite")).setDamageBypassesArmor();
	public static EnviroDamageSource dehydrate = (EnviroDamageSource)(new EnviroDamageSource("dehydrate")).setDamageBypassesArmor();
	public static EnviroDamageSource landslide = (EnviroDamageSource)(new EnviroDamageSource("landslide"));
	public static EnviroDamageSource gasfire = (EnviroDamageSource)(new EnviroDamageSource("gasfire"));
	public static EnviroDamageSource thething = (EnviroDamageSource)(new EnviroDamageSource("thething")).setDamageBypassesArmor();
	
	protected EnviroDamageSource(String par1Str)
	{
		super(par1Str);
	}
	
	@Override
	public IChatComponent func_151519_b(EntityLivingBase par1EntityLivingBase)
	{
		if (!this.damageType.equals("thething"))
		{
			return new ChatComponentTranslation("deathmessage.enviromine."+this.damageType, new Object[]{par1EntityLivingBase.getCommandSenderName()});
		}
		return new ChatComponentText("");
	}
}
