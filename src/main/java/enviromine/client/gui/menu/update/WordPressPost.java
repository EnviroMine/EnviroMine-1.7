package enviromine.client.gui.menu.update;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.logging.log4j.Level;
import enviromine.core.EnviroMine;

public class WordPressPost
{
	public static ArrayList<WordPressPost> Posts = new ArrayList<WordPressPost>();
	public static String changeLog;
	
	String title = "NULL";
	String description = "NULL";
	String link = "NULL";
	String date = "NULL";
	String creator = "NULL";
	
	public WordPressPost(String title, String description, String link, String date, String creator)
	{
		this.title = title;
		this.description = description;
		this.link = link;
		this.creator = creator;
		
		date = date.replaceAll("\\+\\d+", "");
		//System.out.print(date);
		DateFormat format = new SimpleDateFormat("E, d MMM y hh:mm:ss");
		
		try
		{
			Date newDate = format.parse(date.trim());
			
			DateFormat newformat = new SimpleDateFormat("MMMM 'the' d',' y h:mma");
			
			String newDateString = newformat.format(newDate);
			
			this.date = newDateString;
		} catch(ParseException e)
		{
			// TODO Auto-generated catch block
			EnviroMine.logger.log(Level.ERROR, "Error Parsing Date");
			this.date = date;
		}
		
		//System.out.println( this.title  +":"+ this.link  +":"+ this.creator  +":"+  this.date  +":"+ this.description);
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public String getCreator()
	{
		return this.creator;
	}
	
	public String getLink()
	{
		return this.link;
	}
	
	public String getPubDate()
	{
		return this.date;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}
