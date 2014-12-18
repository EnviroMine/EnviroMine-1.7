package enviromine.client.gui.menu.update;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.gui.UpdateNotification;
import enviromine.client.gui.menu.update.UpdatePage.WordPressPost;
import enviromine.core.EM_Settings;
import enviromine.utils.RenderAssist;

public class PostGuiList extends GuiListExtended{
	
    private final List LineList = Lists.newArrayList();
	private WordPressPost lastPost;
	private WordPressPost curPost;
	
	public PostGuiList(Minecraft mc, int x, int y,	int p_i45010_4_, int p_i45010_5_, int p_i45010_6_, int tab) 
	{
		super(mc, x, y, p_i45010_4_, p_i45010_5_,	Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
	
		if(NewsPage.tabSelection == 150) DisplayWordPressNews(mc);
		else if (NewsPage.tabSelection == 151) EnviromineVersions(mc);
		else if (NewsPage.tabSelection == 152) DisplayChangeLog(mc);
		
	}
	
	
	public void EnviromineVersions(Minecraft mc)
	{
		int verStat = UpdateNotification.compareVersions(EM_Settings.Version, UpdateNotification.version);
		
		if(verStat == -1)
		{
			LineList.add(new PostGuiList.Row("Your current version is "+ EM_Settings.Version, null));
			LineList.add(new PostGuiList.Row(" ",null));
			LineList.add(new PostGuiList.Row(StatCollector.translateToLocalFormatted("updatemsg.enviromine.avalible",UpdateNotification.version), null));
			LineList.add(new PostGuiList.Row(" ",null));
			LineList.add(new PostGuiList.Row("Check changlog for whats new.", null));
			LineList.add(new PostGuiList.Row(" ",null));
			LineList.add(new PostGuiList.Row("Download new version on our wiki!", null));
			LineList.add(new PostGuiList.Row(" ",null));
			LineList.add(new PostGuiList.Row("https://github.com/Funwayguy/EnviroMine/wiki/Downloads", null));
			LineList.add(new PostGuiList.Row(" ",null));
			LineList.add(new PostGuiList.Row("https://enviromine.wordpress.com/", null));
			
			

		} else if(verStat == 0)
		{
			LineList.add(new PostGuiList.Row("EnviroMine " + EM_Settings.Version + " is up to date", null));
		} else if(verStat == 1)
		{
			LineList.add(new PostGuiList.Row("EnviroMine " + EM_Settings.Version + " is a debug version", null));
		} else if(verStat == -2)
		{
			LineList.add(new PostGuiList.Row("An error occured while parsing EnviroMine's version file!", null));
		}		

	}
	
	public void DisplayChangeLog(Minecraft mc)
	{
		List wordWrap = mc.fontRenderer.listFormattedStringToWidth(UpdatePage.changeLog,300);
		
		addWordWrap(wordWrap);
	}
	
	public void DisplayWordPressNews(Minecraft mc)
	{
		String allPostLines = ""; 
		
		for(WordPressPost post : UpdatePage.Posts)
		{ 
			LineList.add(new PostGuiList.Row(EnumChatFormatting.BOLD.UNDERLINE +  post.getTitle(), "title"));
			LineList.add(new PostGuiList.Row(" ",null));
			LineList.add(new PostGuiList.Row("Posted: "+ EnumChatFormatting.ITALIC + post.getPubDate(), "date"));
			LineList.add(new PostGuiList.Row(" ",null));			
			
			//String test = .replaceAll("&nbsp;", "");
			
			List wordWrap = mc.fontRenderer.listFormattedStringToWidth(post.getDescription(),300);
			
			addWordWrap(wordWrap);
			
			LineList.add(new PostGuiList.Row(" ",null));
			
			LineList.add(new PostGuiList.Row(EnumChatFormatting.ITALIC + "Posted by: "+ post.getCreator(), "creator"));
			
			LineList.add(new PostGuiList.Row(" ",null));
			LineList.add(new PostGuiList.Row(" ",null));
			LineList.add(new PostGuiList.Row(" ",null));
			LineList.add(new PostGuiList.Row(" ",null));
			LineList.add(new PostGuiList.Row(" ",null));
			
		}
		
	}

	
	private void addWordWrap(List wordWrap)
	{
		Iterator wrapped = wordWrap.iterator();
		while (wrapped.hasNext())
		{
			Object line = wrapped.next();
		

			LineList.add(new PostGuiList.Row(line.toString(),null));
		}
	}
	
	@Override
	public IGuiListEntry getListEntry(int p_148180_1_) {

		return (PostGuiList.Row)this.LineList.get(p_148180_1_);
	}

	@Override
	protected int getSize() 
	{
		
		return this.LineList.size();
	}

	@Override
	  protected int getContentHeight()
	  {
		
		 return this.getSize() * Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + this.headerPadding;
	  }
	   /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return 400;
    }

   
    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 32;
    }

	 @SideOnly(Side.CLIENT)
	    public static class Row implements GuiListExtended.IGuiListEntry
	    {
			private WordPressPost post;
			private WordPressPost lastPost;
			Minecraft mc = Minecraft.getMinecraft();
			public static int LastYpos = 0;	
			private String line;
			private String type;

		 	public Row(String line, String type)
		 	{
		 		
		 		this.line = line;
		 		this.type = type;
		 	}
	
		 	
	
		 	@Override
		 	public void drawEntry(int p_148279_1_, int p_148279_2_,	int p_148279_3_, int p_148279_4_, int p_148279_5_,	Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_,	boolean p_148279_9_) 
		 	{
		 		
		 		
		 		
		 		
		 		if(type == "title")
		 		{
		 			mc.fontRenderer.drawString(line, 140, p_148279_3_, RenderAssist.getColorFromRGBA(246, 255, 0, 255) );
		 		}
		 		else if (type == "creator")
		 		{
		 			mc.fontRenderer.drawString(line, 140, p_148279_3_, RenderAssist.getColorFromRGBA(53, 219, 161, 255) );
		 		}
		 		else if (type == "date")
		 		{
		 			mc.fontRenderer.drawString(line, 140, p_148279_3_, RenderAssist.getColorFromRGBA(71, 134, 186, 255) );
		 		}
		 		else
		 		{
		 			mc.fontRenderer.drawString(line, 140, p_148279_3_,16777215 );
		 		}
		 		
		 	}

		 	@Override
		 	public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) 
		 	{
		 		// TODO Auto-generated method stub
		 		return false;
		 	}

		 	@Override
		 	public void mouseReleased(int p_148277_1_, int p_148277_2_, 	int p_148277_3_, int p_148277_4_, int p_148277_5_,	int p_148277_6_) 
		 	{
		 		// 	TODO Auto-generated method stub
			
		 	}
	   }
	   
}
	