package enviromine.client.gui.menu.config;

import java.io.File;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StatCollector;
import net.minecraft.world.storage.ISaveFormat;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;

@SideOnly(Side.CLIENT)
public class NameProfile extends GuiScreen
{
	private GuiScreen parentGuiScreen;
	private ProfileListExtended profileList;
    private GuiTextField textFieldList;
    private GuiCheckBox genModConfigs;
    private GuiCheckBox genBasicConfigs;
	
	public NameProfile(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
	      Keyboard.enableRepeatEvents(true);
	        this.buttonList.clear();
	        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("editor.enviromine.createNew", new Object[0])));
	        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel", new Object[0])));
	        
        this.textFieldList = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
        this.textFieldList.setFocused(true);
        this.textFieldList.setText("Something");
        
        this.genModConfigs = new GuiCheckBox(2, this.width / 2 - 100, 90, "config.enviromine.genModConfigs", false);
        this.genBasicConfigs =  new GuiCheckBox(3, this.width / 2 - 100, 110, "config.enviromine.genBasicConfigs", false);
        
        this.buttonList.add(this.genModConfigs);
        this.buttonList.add(this.genBasicConfigs);
	}
	
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.textFieldList.updateCursorCounter();
    }
    
    
    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
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
	            if (par1GuiButton.id == 1)
	            {
	                this.mc.displayGuiScreen(this.parentGuiScreen);
	            }
	            else if (par1GuiButton.id == 0)
	            {
	            	String newProfile = this.textFieldList.getText().trim();
	            	
	            	if(this.textFieldList.getText().trim().length() > 0)
	            	{
	            		File profileFile = new File(EM_ConfigHandler.profilePath + newProfile);

	            		EM_ConfigHandler.CheckDir(profileFile);
	            		
	            		if(this.genModConfigs.isChecked()) EM_Settings.genConfigs = true;
	            		if(this.genBasicConfigs.isChecked()) EM_Settings.genDefaults = true;
	            		
			   			EnviroMine.theWorldEM.setProfile(newProfile);

			   			if(EM_ConfigHandler.ReloadConfig())
			   			{

			   			}
	            	}
	
	                this.mc.displayGuiScreen(this.parentGuiScreen);
	            }
	            else if(par1GuiButton.id == 2)
	            {
	            	
	            	
	            	this.genBasicConfigs.setIsChecked(false);
	            	this.genBasicConfigs.enabled = !this.genModConfigs.isChecked();
	            }
	            else if(par1GuiButton.id == 3)
	            {
	            	this.genModConfigs.setIsChecked(false);
	            	this.genModConfigs.enabled = !this.genBasicConfigs.isChecked();
	            }
	        }
	  
	}
	
    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char p_73869_1_, int p_73869_2_)
    {
        this.textFieldList.textboxKeyTyped(p_73869_1_, p_73869_2_);
        ((GuiButton)this.buttonList.get(0)).enabled = this.textFieldList.getText().trim().length() > 0;

        if (p_73869_2_ == 28 || p_73869_2_ == 156)
        {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }
    
	  /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
    {

            this.textFieldList.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
            
            super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);

    }
    
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj,  StatCollector.translateToLocal("ProfileMenu"), this.width / 2, 15, 16777215);
		

	        this.textFieldList.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
}

