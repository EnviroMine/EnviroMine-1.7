package enviromine;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
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
		if(this.damageType == "suffocate")
		{
			return new ChatComponentText(new StringBuilder().append(par1EntityLivingBase.getCommandSenderName()).append(" suffocated to death").toString());
		} else if(this.damageType == "frostbite")
		{
			return new ChatComponentText(new StringBuilder().append(par1EntityLivingBase.getCommandSenderName()).append(" froze to death").toString());
		} else if(this.damageType == "dehydrate")
		{
			return new ChatComponentText(new StringBuilder().append(par1EntityLivingBase.getCommandSenderName()).append(" died of thirst").toString());
		} else if(this.damageType == "landslide")
		{
			return new ChatComponentText(new StringBuilder().append(par1EntityLivingBase.getCommandSenderName()).append(" was crushed in a landslide").toString());
		} else if(this.damageType == "organfailure")
		{
			return new ChatComponentText(new StringBuilder().append(par1EntityLivingBase.getCommandSenderName()).append(" died of organ failure").toString());
		} else if(this.damageType == "heatstroke")
		{
			return new ChatComponentText(new StringBuilder().append(par1EntityLivingBase.getCommandSenderName()).append("'s brain was cooked by heatstroke").toString());
		} else if(this.damageType == "bleedout")
		{
			return new ChatComponentText(new StringBuilder().append(par1EntityLivingBase.getCommandSenderName()).append(" blead out").toString());
		} else if(this.damageType == "gasfire")
		{
			return new ChatComponentText(new StringBuilder().append(par1EntityLivingBase.getCommandSenderName()).append(" died in a gas fire").toString());
		} else if(this.damageType == "thething")
		{
			return new ChatComponentText("");
		} else
		{
			return new ChatComponentText(new StringBuilder().append(par1EntityLivingBase.getCommandSenderName()).append(" died from enviromental causes").toString());
		}
	}
}
