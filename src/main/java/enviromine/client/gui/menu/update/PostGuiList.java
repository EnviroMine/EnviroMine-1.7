package enviromine.client.gui.menu.update;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.gui.UpdateNotification;
import enviromine.core.EM_Settings;
import enviromine.utils.EnviroUtils;
import enviromine.utils.RenderAssist;

@SideOnly(Side.CLIENT)
public class PostGuiList extends GuiListExtended
{
	
	private final List<Row> LineList = Lists.newArrayList();
	//private WordPressPost lastPost;
	//private WordPressPost curPost;
	/** The buttonID of the button used to scroll up */
	private int scrollUpButtonID;
	/** The buttonID of the button used to scroll down */
	private int scrollDownButtonID;
	Minecraft mc;
	
	public PostGuiList(Minecraft mc, int x, int y, int p_i45010_4_, int p_i45010_5_, int p_i45010_6_, int tab)
	{
		super(mc, x, y, p_i45010_4_, p_i45010_5_, p_i45010_6_);
		
		this.mc = mc;
		
		if(NewsPage.tabSelection == 150 && WordPressPost.Posts != null)
			DisplayWordPressNews(mc);
		else if(NewsPage.tabSelection == 151)
			EnviromineVersions(mc);
		else if(NewsPage.tabSelection == 152 && WordPressPost.changeLog != null)
			DisplayChangeLog(mc);
		
	}
	
	/**
	 * Registers the IDs that can be used for the scrollbar's up/down buttons.
	 */
	@Override
	public void registerScrollButtons(int p_148134_1_, int p_148134_2_)
	{
		this.scrollUpButtonID = p_148134_1_;
		this.scrollDownButtonID = p_148134_2_;
		super.registerScrollButtons(p_148134_1_, p_148134_2_);
	}
	
	@SuppressWarnings("unused")
	public void EnviromineVersions(Minecraft mc)
	{
		if(EM_Settings.Version == "FWG_" + "EM" + "_VER")
		{
			addLine(EnumChatFormatting.RED + "THIS COPY OF ENIVROMINE IS NOT FOR PUBLIC USE!");
			return;
		}
		
		int verStat = EnviroUtils.compareVersions(EM_Settings.Version, UpdateNotification.version);
		
		if(verStat == -1)
		{
			addLine(StatCollector.translateToLocalFormatted("news.enviromine.version.current", EM_Settings.Version));
			addBlankLines(1);
			addLine(StatCollector.translateToLocalFormatted("updatemsg.enviromine.available", UpdateNotification.version));
			addBlankLines(1);
			addLine(StatCollector.translateToLocalFormatted("news.enviromine.news.changelog"));
			addBlankLines(1);
			addLine(StatCollector.translateToLocalFormatted("news.enviromine.news.wiki"));
			addBlankLines(1);
			addLine("https://github.com/Funwayguy/EnviroMine/wiki/Downloads");
			addBlankLines(1);
			addLine("https://enviromine.wordpress.com/");
		} else if(verStat == 0)
		{
			addLine(StatCollector.translateToLocalFormatted("updatemsg.enviromine.uptodate", EM_Settings.Version));
		} else if(verStat == 1)
		{
			addLine(StatCollector.translateToLocalFormatted("updatemsg.enviromine.debug", EM_Settings.Version));
		} else if(verStat == -2)
		{
			addLine(StatCollector.translateToLocalFormatted("updatemsg.enviromine.error"));
		}
		
	}
	
	public void DisplayChangeLog(Minecraft mc)
	{
		String[] changlog = WordPressPost.changeLog.split("\n");
		
		List<String> lineBreaks = Lists.newArrayList();
		List<String> wordWrap = Lists.newArrayList();
		
		for(String line : changlog)
		{
			@SuppressWarnings("unchecked")
			List<String> lines = mc.fontRenderer.listFormattedStringToWidth(line, this.width - 64);
			
			wordWrap.addAll(lines);
			
			for(int i = 1; i <= lines.size(); i++)
			{
				lineBreaks.add(i + "," + lines.size());
			}
		}
		
		addWordWrap(wordWrap, lineBreaks);
	}
	
	public void DisplayWordPressNews(Minecraft mc)
	{
		for(WordPressPost post : WordPressPost.Posts)
		{
			addLine(EnumChatFormatting.BOLD + "" + EnumChatFormatting.UNDERLINE + post.getTitle(), textType.TITLE);
			addBlankLines(1);
			
			addLine("Posted: " + EnumChatFormatting.ITALIC + post.getPubDate(), textType.DATE);
			addBlankLines(1);
			
			@SuppressWarnings("unchecked")
			List<String> wordWrap = mc.fontRenderer.listFormattedStringToWidth(post.getDescription(), this.width - 64);
			
			addWordWrap(wordWrap);
			
			addBlankLines(1);
			
			addLine(EnumChatFormatting.ITALIC + "Posted by: " + post.getCreator(), textType.CREATOR);
			addBlankLines(2);
			addLine("", textType.HR);
			addBlankLines(2);
		}
		
	}
	
	
	private void addWordWrap(List<String> wordWrap)
	{
		textType type;
		Iterator<String> wrapped = wordWrap.iterator();
		while(wrapped.hasNext())
		{
			Object line = wrapped.next();
			type = textType.DEFAULT;
			LineList.add(new PostGuiList.Row(line.toString(), type));
		}

	}
	/**
	 * Pass String and will wordwrap it to screen and add to list to be drawn
	 * @param wordWrap
	 */
	private void addWordWrap(List<String> wordWrap, List<String> LineBreaks)
	{
		textType type;
		textType lasttype =  textType.DEFAULT;
		
		Iterator<String> wrapped = wordWrap.iterator();
		
		Iterator<String> breaks = LineBreaks.iterator();
		while(wrapped.hasNext())
		{
			Object line = wrapped.next();
			Object linenum = breaks.next();

			type = textType.DEFAULT;
			
			if(NewsPage.tabSelection == 152)
			{
				String[] count = linenum.toString().split(",");
				
				if(Integer.parseInt(count[0]) == 1)
				{
					type = parseChangelog(line.toString());
					lasttype = type;
				}
				else if (Integer.parseInt(count[0]) <= Integer.parseInt(count[1]))
				{
					type = lasttype;
					
					if (Integer.parseInt(count[0]) == Integer.parseInt(count[1])) lasttype = textType.DEFAULT;
				}
				//type = parseChangelog(line.toString());
			}
			
			LineList.add(new PostGuiList.Row(line.toString(), type));
		}
	}
	
	/**
	 * Will add "num" Blank lines to be drawn
	 * @param num
	 */
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
	
	/**
	 * Pass a line from Changelog and returns Crude code coloring.. does not
	 * parse multi lines cause by word wrap.
	 * @param line
	 * @return
	 */
	
	private textType parseChangelog(String line)
	{
		line = line.toLowerCase();
		Pattern versionNum = Pattern.compile("\\[.+\\]");
		Pattern change = Pattern.compile(".*(fixed|\\*|fix|fixes|bug|changed).*");
		Pattern add = Pattern.compile(".*(added|\\+|new|adding).*");
		Pattern removed = Pattern.compile(".*(removed|deleted|revert).*");
		Pattern header = Pattern.compile(".*full enviromine changelog.*");
		
		if(versionNum.matcher(line).matches())
		{
			//System.out.println(line + versionNum.matcher(line).matches());
			return textType.VERSION;
		} else if(change.matcher(line).matches())
		{
			//System.out.println(line + change.matcher(line).matches());
			return textType.CHANGED;
		} else if(header.matcher(line).matches())
		{
			//System.out.println(line + header.matcher(line).matches());
			return textType.HEADER;
		} else if(removed.matcher(line).matches())
		{
			//System.out.println(line + removed.matcher(line).matches());
			return textType.REMOVED;
		} else if(add.matcher(line).matches())
		{
			//System.out.println(line + add.matcher(line).matches());
			return textType.ADD;
		}
		
		else
			return textType.DEFAULT;
	}
	
    /**
     * Breaks a string into a list of pieces that will fit a specified width.
     */

	
	
	@Override
	public IGuiListEntry getListEntry(int p_148180_1_)
	{
		
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
		return this.width - 64;
	}
	
	protected int getScrollBarX()
	{
		return this.width - 8;
	}
	
	private final float scrollSpeed = 0.025F;
	
	public void scrollByMultiplied(float p_148145_1_)
	{
		super.scrollBy(MathHelper.ceiling_float_int(p_148145_1_ * scrollSpeed));
	}
	
	@Override
	public void actionPerformed(GuiButton p_148147_1_)
	{
		if(p_148147_1_.enabled)
		{
			if(p_148147_1_.id == this.scrollUpButtonID)
			{
				this.scrollByMultiplied(-this.slotHeight * 2F / 3F);
			} else if(p_148147_1_.id == this.scrollDownButtonID)
			{
				this.scrollByMultiplied(this.slotHeight * 2F / 3F);
			}
		}
	}
	
	@Override
	public void drawScreen(int p_148128_1_, int p_148128_2_, float p_148128_3_)
	{
		if(p_148128_1_ > this.left && p_148128_1_ < this.right && p_148128_2_ > this.top && p_148128_2_ < this.bottom && !(Mouse.isButtonDown(0) && this.func_148125_i()))
		{
            for (; !this.mc.gameSettings.touchscreen && Mouse.next(); this.mc.currentScreen.handleMouseInput())
            {
                float j1 = Mouse.getEventDWheel();

                if (j1 != 0)
                {
                	j1 *= -1F;

                    this.scrollByMultiplied(j1 * (float)this.slotHeight / 2F);
                }
            }
		}
		
		super.drawScreen(p_148128_1_, p_148128_2_, p_148128_3_);
	}
	
	@SideOnly(Side.CLIENT)
	public static class Row implements GuiListExtended.IGuiListEntry
	{
		//private WordPressPost post;
		//private WordPressPost lastPost;
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
		public void drawEntry(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_)
		{
			mc.fontRenderer.drawString(textTypeText(type, line), 32, p_148279_3_, textTypeColor(type));
		}
		
		@Override
		public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
		{
			return false;
		}
		
		@Override
		public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_)
		{
		}
		
	}
	
	/**
	 * These Enums are used for Parsing colors or chaning text when drawing to screen
	 * @author GenDeathrow
	 *
	 */
	enum textType
	{
		TITLE, DATE, CREATOR, HR, DEFAULT,
		
		VERSION, HEADER, ADD, REMOVED, CHANGED
		
	}
	
	public static int textTypeColor(textType type)
	{
		switch(type)
		{
			case TITLE:
				return RenderAssist.getColorFromRGBA(21, 153, 21, 255);
				
			case DATE:
				return RenderAssist.getColorFromRGBA(71, 134, 186, 255);
				
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
				return 16777215;
				
		}
	}
	
	public static String textTypeText(textType type, String line)
	{
		switch(type)
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
