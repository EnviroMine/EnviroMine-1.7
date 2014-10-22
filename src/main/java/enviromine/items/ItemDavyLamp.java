package enviromine.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDavyLamp extends ItemBlock
{
	public String[] lampNames = new String[]{"off", "lit", "gas"};
	
	public ItemDavyLamp(Block block)
	{
		super(block);
		this.setHasSubtypes(true);
		this.setUnlocalizedName("enviromine.davy_lamp");
	}
	
	@Override
	public int getMetadata(int damageValue)
	{
		return damageValue%3;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return getUnlocalizedName() + "." + lampNames[itemstack.getItemDamage()%3];
	}

    /**
     * Gets an icon index based on an item's damage value
     */
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        return this.field_150939_a.getIcon(1, damage);
    }
}
