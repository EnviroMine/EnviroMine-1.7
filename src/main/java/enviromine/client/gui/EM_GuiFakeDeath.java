package enviromine.client.gui;

import net.minecraft.client.gui.GuiGameOver;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class EM_GuiFakeDeath extends GuiGameOver
{
	private int ticksOpen = 0;

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char keyChar, int p_73869_2_)
    {
    	if(p_73869_2_ == Keyboard.KEY_ESCAPE)
    	{
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
    	}
    }
    
    /**
     * Called from the main game loop to update the screen.
     */
	@Override
    public void updateScreen()
    {
        //super.updateScreen();
        ++this.ticksOpen;

        if (this.ticksOpen >= 15)
        {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        }
    }
}
