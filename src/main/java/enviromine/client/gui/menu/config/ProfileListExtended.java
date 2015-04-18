package enviromine.client.gui.menu.config;

import java.io.File;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EnviroMine;
import enviromine.world.EM_WorldData;

public class ProfileListExtended extends GuiListExtended{

    private final Minecraft mc;
	private final List<profileEntry> profileList = Lists.newArrayList();
    protected static String createNew = "Create New Profile";
    private GuiScreen profileMenu;
    private boolean reloadFlag = false;
    
    public ProfileListExtended(Minecraft mc, int x, int y, int p_i45010_4_, int p_i45010_5_, int p_i45010_6_, GuiScreen parentGuiScreen)
	{
		super(mc, x, y, p_i45010_4_, p_i45010_5_, p_i45010_6_);
		
		profileMenu = parentGuiScreen;
		this.mc = mc;
		
		File[] profileDir = new File(EM_ConfigHandler.profilePath).listFiles();
		
		
		
		
		int id=500;
		
		profileList.add(new ProfileListExtended.profileEntry(createNew));
		for(File entry: profileDir)
		{
			
			if(entry.isDirectory()) 
			{  
				String profileName = entry.toString().substring(EM_ConfigHandler.profilePath.length()).toUpperCase();
				profileList.add(new ProfileListExtended.profileEntry(profileName));
				id++;
			}
		}
		
	}

	
	@Override
	public GuiListExtended.IGuiListEntry getListEntry(int p_148180_1_) 
	{
		// TODO Auto-generated method stub
		return this.profileList.get(p_148180_1_);
	}

	@Override
	protected int getSize() 
	{
		// TODO Auto-generated method stub
		return this.profileList.size();
	}
	
	@Override
	protected int getContentHeight()
	{
		
		return this.getSize() * 30 + this.headerPadding;
	}

	@SideOnly(Side.CLIENT)
	public class profileEntry implements GuiListExtended.IGuiListEntry
	{

        private final GuiButton bntProfile;
        private String profileName;

		
		public profileEntry(String profileName)
		{
			this.profileName = profileName;
			this.bntProfile = new GuiButtonExt(0, 0, 0, 120, 20, profileName);
		}
		

		
		@Override
		public void drawEntry(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_)
		{
	
			this.bntProfile.xPosition = (ProfileListExtended.this.width /2) - (this.bntProfile.width/2);
            this.bntProfile.yPosition = p_148279_3_;
            this.bntProfile.displayString = profileName;
    
            this.bntProfile.drawButton(ProfileListExtended.this.mc, p_148279_7_, p_148279_8_);

			//mc.fontRenderer.drawString(textTypeText(type, line), 32, p_148279_3_, textTypeColor(type));
		}
		
		@Override
		public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
		{

		       if (this.bntProfile.mousePressed(ProfileListExtended.this.mc, p_148278_2_, p_148278_3_))
	            {
		    	   
		    	   if(profileName == ProfileListExtended.createNew)
		    	   {
		    		   Minecraft.getMinecraft().displayGuiScreen(new NameProfile(profileMenu));
		    		   return false;
		    	   }
		   			EnviroMine.theWorldEM.setProfile(profileName);

		   			if(EM_ConfigHandler.ReloadConfig())
		   			{

		   			}
	                return true;
	            }
		       else return false;
		}
		
		@Override
		public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_)
		{
		}
		
	}
	
}
