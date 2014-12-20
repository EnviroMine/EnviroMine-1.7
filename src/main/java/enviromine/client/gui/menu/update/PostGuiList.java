package enviromine.client.gui.menu.update;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

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
		if(EM_Settings.Version == "FWG_" + "EM" + "_VER")
		{
			addLine("Your current version is "+ EM_Settings.Version);
			return;
		}
		
		
		@SuppressWarnings("unused")
		int verStat = UpdateNotification.compareVersions(EM_Settings.Version, UpdateNotification.version);
		
		if(verStat == -1)
		{
			addLine("Your current version is "+ EM_Settings.Version);
			addBlankLines(1);
			addLine(StatCollector.translateToLocalFormatted("updatemsg.enviromine.avalible",UpdateNotification.version));
			addBlankLines(1);
			addLine("Check changlog for whats new.");
			addBlankLines(1);
			addLine("Download new version on our wiki!");
			addBlankLines(1);
			addLine("https://github.com/Funwayguy/EnviroMine/wiki/Downloads");
			addBlankLines(1);
			addLine("https://enviromine.wordpress.com/");

		} else if(verStat == 0)
		{
			addLine("EnviroMine " + EM_Settings.Version + " is up to date");
		} else if(verStat == 1)
		{
			addLine("EnviroMine " + EM_Settings.Version + " is a debug version");
		} else if(verStat == -2)
		{
			addLine("An error occured while parsing EnviroMine's version file!");
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
			addLine(EnumChatFormatting.BOLD.UNDERLINE +  post.getTitle(), textType.TITLE);
			addBlankLines(1);

			addLine("Posted: "+ EnumChatFormatting.ITALIC + post.getPubDate(), textType.DATE);
			addBlankLines(1);			
			
			List wordWrap = mc.fontRenderer.listFormattedStringToWidth(post.getDescription(),300);
			
			addWordWrap(wordWrap);
			
			addBlankLines(1);	
			
			addLine(EnumChatFormatting.ITALIC + "Posted by: "+ post.getCreator(), textType.CREATOR);
			addBlankLines(2);
			addLine("", textType.HR);
			addBlankLines(2);
		}
		
	}

	
	private void addWordWrap(List wordWrap)
	{
		textType type;
		Iterator wrapped = wordWrap.iterator();
		while (wrapped.hasNext())
		{
			Object line = wrapped.next();
			type = textType.DEFAULT;
			if (NewsPage.tabSelection == 152)	type = parseChangelog(line.toString());

			LineList.add(new PostGuiList.Row(line.toString(),type));
		}
	}
	
	private void addBlankLines(int num)
	{
		for(int i = 0; i < num; i++)
		{
			addLine("");
		}
		
	}
	
	private void addLine(String text)
	{
		addLine(text, textType.DEFAULT);
	}
	
	private void addLine(String text, textType hr)
	{
		LineList.add(new PostGuiList.Row(text, hr));
	}
	
	private textType parseChangelog(String line)
	{
		Pattern versionNum = Pattern.compile("\\[.+\\]");
		Pattern change = Pattern.compile("(Fixed)");
		Pattern add = Pattern.compile("Added");
		Pattern removed = Pattern.compile("Removed");
		Pattern header = Pattern.compile("Full EnviroMine Changelog");
		
		
		System.out.println(line + change.matcher(line).matches());
		if(versionNum.matcher(line).matches())
		{
			return textType.VERSION;
		}
		else if (change.matcher(line).matches())
		{
			return textType.CHANGED;
		}
		else if (header.matcher(line).matches())
		{
			return textType.HEADER;
		}
		else if (add.matcher(line).matches())
		{
			return textType.ADD;
		}
		else if (removed.matcher(line).matches())
		{
			return textType.REMOVED;
		}
		else return textType.DEFAULT;
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
			private textType type;

		 	public Row(String line, textType type)
		 	{
		 		
		 		this.line = line;
		 		this.type = type;
		 	}
	
		 	int color;
	
		 	@Override
		 	public void drawEntry(int p_148279_1_, int p_148279_2_,	int p_148279_3_, int p_148279_4_, int p_148279_5_,	Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_,	boolean p_148279_9_) 
		 	{
		 		mc.fontRenderer.drawString(textTypeText(type, line), 130, p_148279_3_ , textTypeColor(type));
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
	   
	 enum textType
	 {
		 TITLE,
		 DATE,
		 CREATOR,
		 HR,
		 DEFAULT,
		 
		 VERSION,
		 HEADER,
		 ADD,
		 REMOVED,
		 CHANGED
		 
		 
	 }
	 
	 public static int textTypeColor(textType type)
	 {
		 switch (type)
		 {
			case TITLE:
				return RenderAssist.getColorFromRGBA(21, 153, 21, 255);
			 
			case DATE:
				return  RenderAssist.getColorFromRGBA(71, 134, 186, 255);
				 
			case CREATOR:
				return RenderAssist.getColorFromRGBA(53, 219, 161, 255);
				 
			case HR:
				return RenderAssist.getColorFromRGBA(71, 134, 186, 255);
			 
			case VERSION:
				return RenderAssist.getColorFromRGBA(255, 251, 0, 255);
				
			case HEADER:
				return RenderAssist.getColorFromRGBA(110, 129, 255, 255);
				
			case ADD:
				return RenderAssist.getColorFromRGBA(0, 255, 0, 255);
				
			case REMOVED:
				return RenderAssist.getColorFromRGBA(255, 0, 0, 255);
				
			case CHANGED:
				return RenderAssist.getColorFromRGBA(255, 98, 0, 255);
				
			 default:
				return  16777215;
				
		 }
	 }
	 
	 public static String textTypeText(textType type, String line)
	 {
		 switch (type)
		 {
			case TITLE:
				line = line.toUpperCase();
				break;

			case HR:
	 			String hr = "---------------------------------------------------------------------------------------";
	 			line = Minecraft.getMinecraft().fontRenderer.trimStringToWidth(hr, 300);
	 			break;

			default:
				break;
				
		 }
		 
		 return line;
	 }
}
	