package enviromine.client.gui;

import enviromine.core.EM_Settings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

public class EM_GuiAuthWarn extends GuiScreen
{
	public static boolean shouldWarn = false;
	GuiScreen parentScreen;
	
	public EM_GuiAuthWarn(GuiScreen parent)
	{
		parentScreen = parent;
	}
	
	@SuppressWarnings("unchecked")
	public void initGui()
	{
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done", new Object[0])));
	}

    protected void actionPerformed(GuiButton p_146284_1_)
    {
        if (p_146284_1_.enabled)
        {
            if (p_146284_1_.id == 200)
            {
            	shouldWarn = false;
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.drawBackground(0);
        this.drawCenteredString(this.fontRendererObj, I18n.format("auth.enviromine.1", EM_Settings.Version), this.width / 2, this.height / 2 - 50, 16777215);
        this.drawCenteredString(this.fontRendererObj, I18n.format("auth.enviromine.2"), this.width / 2, this.height / 2 - 40, 16777215);
        
        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.RED + I18n.format("auth.enviromine.3"), this.width / 2, this.height / 2 - 20, 16777215);
        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.RED + I18n.format("auth.enviromine.4"), this.width / 2, this.height / 2 - 10, 16777215);
        
        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GREEN + I18n.format("auth.enviromine.5", "#StopModReposts"), this.width / 2, this.height / 2 + 20, 16777215);
        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GREEN + "http://stopmodreposts.org", this.width / 2, this.height / 2 + 30, 16777215);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
