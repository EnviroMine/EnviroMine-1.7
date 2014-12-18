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

public class UpdatePage {
	
	public static ArrayList<WordPressPost> Posts = new ArrayList<WordPressPost>();
	public static String changeLog;
	
	public class WordPressPost
	{
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
	
	
	static boolean hasChecked = false;

	
	public static void display()
	{
		if(!EnviroMine.proxy.isClient() || hasChecked)
		{
			return;
		}

		hasChecked = true;
		try
		{
			String page = getNotification("https://enviromine.wordpress.com/news/feed/", true);
		
			try {
				WordPressParser.main(page);
			} catch (Exception e) {
				EnviroMine.logger.log(Level.WARN, "Failed to parse WordPress News Page");
			}
			
			
		}catch(IOException e)
		{
			if(EM_Settings.updateCheck)
			{
				EnviroMine.logger.log(Level.WARN, "Failed to get WordPress News Page!");
			}
		}
		
		try {
			changeLog = getNotification("https://drone.io/github.com/Funwayguy/EnviroMine-1.7/files/build/libs/full_changelog.txt", true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			EnviroMine.logger.log(Level.WARN, "Failed to get ChangeLog file!");
		}

		
	}

	private static String getNotification(String link, boolean doRedirect) throws IOException
	{
		URL url = new URL(link);
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setDoOutput(false);
		con.setReadTimeout(20000);
		con.setRequestProperty("Connection", "keep-alive");
		
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:16.0) Gecko/20100101 Firefox/16.0");
		((HttpURLConnection)con).setRequestMethod("GET");
		con.setConnectTimeout(5000);
		BufferedInputStream in = new BufferedInputStream(con.getInputStream());
		int responseCode = con.getResponseCode();
		if(responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_MOVED_PERM)
		{
			EnviroMine.logger.log(Level.WARN, "Update request returned response code: " + responseCode + " " + con.getResponseMessage());
		} else if(responseCode == HttpURLConnection.HTTP_MOVED_PERM)
		{
			if(doRedirect)
			{
				try
				{
					return getNotification(con.getHeaderField("location"), false);
				} catch(IOException e)
				{
					throw e;
				}
			} else
			{
				throw new IOException();
			}
		}
		StringBuffer buffer = new StringBuffer();
		int chars_read;
		//	int total = 0;
		while((chars_read = in.read()) != -1)
		{
			char g = (char)chars_read;
			buffer.append(g);
		}
		final String page = buffer.toString();
		
		return page;
	}
	
	
	public static void xml()
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {

		    InputStream    xmlInput  = new FileInputStream("theFile.xml");
		    SAXParser      saxParser = factory.newSAXParser();

		    DefaultHandler handler   = new SAXParserHandler();
		    saxParser.parse(xmlInput, handler);

		} catch (Throwable err) {
		    err.printStackTrace ();
		}
	}
	


}
