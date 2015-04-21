package enviromine.client.gui.menu.config;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.gui.menu.update.PostGuiList;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class ProfileMenu extends GuiScreen
{
	private GuiScreen parentGuiScreen;
	private ProfileListExtended profileList;
    private GuiTextField textFieldList;
	
	public ProfileMenu(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		int doneWidth = Math.max(mc.fontRenderer.getStringWidth(I18n.format("gui.back")) + 20, 100);
		int createWidth = Math.max(mc.fontRenderer.getStringWidth(I18n.format("editor.enviromine.createNewProfile")) + 20, 100);
	    this.buttonList.add(new GuiButtonExt(200, 20, this.height - 29, doneWidth, 20, I18n.format("gui.back")));
	    this.buttonList.add(new GuiButtonExt(201, this.width - createWidth - 20, this.height - 29, createWidth, 20, I18n.format("editor.enviromine.createNewProfile")));
		this.profileList = new ProfileListExtended(this.mc, this.width, this.height, 32, this.height - 32, 30, this.parentGuiScreen);

	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}
	
	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	public void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.enabled)
		{
			if(par1GuiButton.id == 201)
			{
	    		   Minecraft.getMinecraft().displayGuiScreen(new NameProfile(this.parentGuiScreen));
			}
			else if (par1GuiButton.id == 200)
			{
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}
		}
	}
	
	  /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
    {
            if (this.profileList.func_148179_a(p_73864_1_, p_73864_2_, p_73864_3_))
            {
            	EM_ConfigHandler.ReloadConfig();
   				this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            
            super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);

    }
    
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();		
		   this.profileList.drawScreen(par1, par2, par3);
			this.drawCenteredString(this.fontRendererObj,  I18n.format("editor.enviromine.selectProfile"), this.width / 2, 15, 16777215);
			
		super.drawScreen(par1, par2, par3);
	}
	
}

