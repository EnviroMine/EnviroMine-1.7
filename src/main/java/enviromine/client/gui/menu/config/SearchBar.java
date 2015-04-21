package enviromine.client.gui.menu.config;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;

public class SearchBar extends GuiListExtended 
{

	public SearchBar(Minecraft p_i45010_1_, int p_i45010_2_, int p_i45010_3_, int p_i45010_4_, int p_i45010_5_, int p_i45010_6_) 
	{
		super(p_i45010_1_, p_i45010_2_, p_i45010_3_, p_i45010_4_, p_i45010_5_,	p_i45010_6_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IGuiListEntry getListEntry(int p_148180_1_) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@SideOnly(Side.CLIENT)
	public class searchEntry implements GuiListExtended.IGuiListEntry
	{

		@Override
		public void drawEntry(int p_148279_1_, int p_148279_2_,
				int p_148279_3_, int p_148279_4_, int p_148279_5_,
				Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_,
				boolean p_148279_9_) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean mousePressed(int p_148278_1_, int p_148278_2_,
				int p_148278_3_, int p_148278_4_, int p_148278_5_,
				int p_148278_6_) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void mouseReleased(int p_148277_1_, int p_148277_2_,
				int p_148277_3_, int p_148277_4_, int p_148277_5_,
				int p_148277_6_) {
			// TODO Auto-generated method stub
			
		}
	}
}
