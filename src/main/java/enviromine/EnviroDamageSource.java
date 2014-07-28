package enviromine;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.DamageSource;

public class EnviroDamageSource extends DamageSource
{
	public static EnviroDamageSource heatstroke = (EnviroDamageSource)(new EnviroDamageSource("heatstroke")).setDamageBypassesArmor();
	public static EnviroDamageSource organfailure = (EnviroDamageSource)(new EnviroDamageSource("organfailure")).setDamageBypassesArmor();
	public static EnviroDamageSource bleedout = (EnviroDamageSource)(new EnviroDamageSource("bleedout")).setDamageBypassesArmor();
	public static EnviroDamageSource suffocate = (EnviroDamageSource)(new EnviroDamageSource("suffocate")).setDamageBypassesArmor();
	public static EnviroDamageSource frostbite = (EnviroDamageSource)(new EnviroDamageSource("frostbite")).setDamageBypassesArmor();
	public static EnviroDamageSource dehydrate = (EnviroDamageSource)(new EnviroDamageSource("dehydrate")).setDamageBypassesArmor();
	public static EnviroDamageSource landslide = (EnviroDamageSource)(new EnviroDamageSource("landslide"));
	
	protected EnviroDamageSource(String par1Str)
	{
		super(par1Str);
	}
	
	@Override
	public ChatMessageComponent getDeathMessage(EntityLivingBase par1EntityLivingBase)
	{
		if(this.damageType == "suffocate")
		{
			return ChatMessageComponent.createFromText(new StringBuilder().append(par1EntityLivingBase.getTranslatedEntityName()).append(" suffocated to death").toString());
		} else if(this.damageType == "frostbite")
		{
			return ChatMessageComponent.createFromText(new StringBuilder().append(par1EntityLivingBase.getTranslatedEntityName()).append(" froze to death").toString());
		} else if(this.damageType == "dehydrate")
		{
			return ChatMessageComponent.createFromText(new StringBuilder().append(par1EntityLivingBase.getTranslatedEntityName()).append(" died of thirst").toString());
		} else if(this.damageType == "landslide")
		{
			return ChatMessageComponent.createFromText(new StringBuilder().append(par1EntityLivingBase.getTranslatedEntityName()).append(" was crushed in a landslide").toString());
		} else if(this.damageType == "organfailure")
		{
			return ChatMessageComponent.createFromText(new StringBuilder().append(par1EntityLivingBase.getTranslatedEntityName()).append(" died of organ failure").toString());
		} else if(this.damageType == "heatstroke")
		{
			return ChatMessageComponent.createFromText(new StringBuilder().append(par1EntityLivingBase.getTranslatedEntityName()).append("'s brain was cooked by heatstroke").toString());
		} else if(this.damageType == "bleedout")
		{
			return ChatMessageComponent.createFromText(new StringBuilder().append(par1EntityLivingBase.getTranslatedEntityName()).append(" blead out").toString());
		} else
		{
			return ChatMessageComponent.createFromText(new StringBuilder().append(par1EntityLivingBase.getTranslatedEntityName()).append(" died from enviromental causes").toString());
		}
	}
}
