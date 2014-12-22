package enviromine.client.gui.menu.update;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.Level;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.org.apache.xml.internal.resolver.readers.SAXParserHandler;

import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;

public class WordPressPost {
	
	public static ArrayList<WordPressPost> Posts = new ArrayList<WordPressPost>();
	public static String changeLog;
	

		 String title;
		 String description;
		 String link;
		 String date;
		 String creator;
		
		
		
		public WordPressPost(String title, String description,	String link, String date, String creator)
		{
			this.title = title;
			this.description = description;
			this.link = link;
			this.creator = creator;
			
			date = date.replaceAll("\\+\\d+", "");
			System.out.print(date);
			DateFormat format = new SimpleDateFormat("E, d MMM y hh:mm:ss");
			
			try {
				Date newDate =  format.parse(date.trim());
				
				DateFormat newformat = new SimpleDateFormat("MMMM 'the' d',' y h:mma"); 
				
				String newDateString = newformat.format(newDate);
				
				this.date = newDateString;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				System.out.println("Error Parsing Date");
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
