package enviromine.client.gui.menu.update;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import enviromine.client.gui.UpdatePage;
import enviromine.client.gui.UpdatePage.WordPressPost;

public class NewsPage  extends GuiScreen{
	private GuiScreen parentGuiScreen;
	private GuiTextField field_146302_g;
	private PostGuiList newsPostList;
	public final int xSizeOfTexture = 176;
	public final int ySizeOfTexture = 88;
	
	public NewsPage(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
	}
	
	@Override
	public void initGui()
	{
		
		this.buttonList.add(new GuiButton(150, 30, 50, 80, 20, "News Page"));
		
		this.buttonList.add(new GuiButton(151, 30, 70, 80, 20, "Version Page"));
		
		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 40, StatCollector.translateToLocal("gui.back")));

		this.newsPostList = new PostGuiList(this.mc, this.width, this.height, 32, this.height - 32, 40, UpdatePage.Posts);
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
			if(par1GuiButton.id == 150)
			{
				
			}
			else if (par1GuiButton.id == 200)
			{
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}
		}
	}
	
 
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj,  StatCollector.translateToLocal("options.enviromine.updatepage.title"), this.width / 2, 15, 16777215);
		
        this.newsPostList.drawScreen(par1, par2, par3);
		//drawNewsPost();
		
		super.drawScreen(par1, par2, par3);
	}
	
	int nextYpos = 0;
	
	private void drawNewsPost()
	{
		int cnt = 0;
		nextYpos = 0;
		//ArrayList<WordPressPost> post = UpdatePage.Posts;
		
		for(WordPressPost post : UpdatePage.Posts)
		{
			List test = this.fontRendererObj.listFormattedStringToWidth(post.getDescription(),300);
		
			this.drawString(fontRendererObj, post.getTitle(), 140, 50 + nextYpos ,16777215 );
			
			this.fontRendererObj.drawSplitString(post.getDescription(),145, 50 + fontRendererObj.FONT_HEIGHT + nextYpos, 300, 16777215);
			
			nextYpos += (test.size() * fontRendererObj.FONT_HEIGHT) + (fontRendererObj.FONT_HEIGHT * 2);
			
			System.out.println(nextYpos);
			cnt++;
		}
		
		

	}

}
