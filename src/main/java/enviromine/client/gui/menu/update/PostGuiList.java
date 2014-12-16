package enviromine.client.gui.menu.update;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.gui.UpdatePage;
import enviromine.client.gui.UpdatePage.WordPressPost;

public class PostGuiList extends GuiListExtended{
	
    private final List LineList = Lists.newArrayList();
	private WordPressPost lastPost;
	private WordPressPost curPost;
	
	public PostGuiList(Minecraft mc, int x, int y,	int p_i45010_4_, int p_i45010_5_, int p_i45010_6_, ArrayList<WordPressPost> Post) 
	{
		super(mc, x, y, p_i45010_4_, p_i45010_5_,	p_i45010_6_);
	
		for(WordPressPost post : UpdatePage.Posts)
		{
			curPost = post;
			
			
			LineList.add(new PostGuiList.Row(curPost, lastPost));
			lastPost = curPost;
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
		int Total = 0;
		for(WordPressPost post : UpdatePage.Posts)
		{
			List test = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(post.getDescription(),300);
   			Total += (test.size() * Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT) + 40;
		}
		return Total;
		  //return this.getSize() * this.slotHeight + this.headerPadding;
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

		 	public Row(WordPressPost post, WordPressPost lastPost)
		 	{
		 		
		 		this.post = post;
		 		this.lastPost = lastPost;
		 	}
		 	
		 	@Override
		 	public void drawEntry(int p_148279_1_, int p_148279_2_,	int p_148279_3_, int p_148279_4_, int p_148279_5_,	Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_,	boolean p_148279_9_) 
		 	{
				int cnt = 0;
				
				
	               if (this.post != null)
	                {
	            	  // int nextYpos = p_148279_3_;
	            	   
	            	   if(this.lastPost != null)
	            	   {
	       					List lastPostString = mc.fontRenderer.listFormattedStringToWidth(lastPost.getDescription(),300);
	       					
	       					LastYpos = lastPostString.size() * mc.fontRenderer.FONT_HEIGHT; 
	            	   }
	       				
	       			
	       			
	       				mc.fontRenderer.drawString(post.getTitle(), 140, p_148279_3_ + LastYpos ,16777215 );
	    			
	       				mc.fontRenderer.drawSplitString(post.getDescription(),145, p_148279_3_ + mc.fontRenderer.FONT_HEIGHT + LastYpos, 300, 16777215);
	    			
	       				//nextYpos += (test.size() * mc.fontRenderer.FONT_HEIGHT) + (mc.fontRenderer.FONT_HEIGHT * 2);
	    	
	       				
	       				//LastYpos = ((test.size() * mc.fontRenderer.FONT_HEIGHT) + (mc.fontRenderer.FONT_HEIGHT * 2))*2;
	       				
	       				System.out.println(post.getTitle() +":"+ p_148279_3_ +":"+ LastYpos);
	    
	       				cnt++;         
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
	