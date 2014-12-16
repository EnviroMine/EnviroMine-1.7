package enviromine.client.gui;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.Level;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.org.apache.xml.internal.resolver.readers.SAXParserHandler;

import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;

public class UpdatePage {
	
	public static ArrayList<WordPressPost> Posts = new ArrayList<WordPressPost>();
	
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
			this.date = date;
			this.creator = creator;
			
			System.out.println( this.title  +":"+ this.link  +":"+ this.creator  +":"+  this.date  +":"+ this.description);
		}
		
		public String getTitle()
		{
			return this.title;
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
			String[] page = getNotification("https://enviromine.wordpress.com/news/feed/", true);
		
			for(String line : page)
			{
				
				//System.out.println(line);
				
			}
		}catch(IOException e)
		{
			if(EM_Settings.updateCheck)
			{
				EnviroMine.logger.log(Level.WARN, "Failed to get versions file!");
			}
		}
	}

	private static String[] getNotification(String link, boolean doRedirect) throws IOException
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
		
		try {
			MySAXApp.main(page);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ReadXMLFile.main(page);
		
		
		/*
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {

		    //InputStream    xmlInput  = new FileInputStream("theFile.xml");
		    SAXParser      saxParser = factory.newSAXParser();

		    DefaultHandler handler   = new SAXParserHandler();
		    saxParser.parse(page, handler);
		    
		    System.out.println(saxParser.toString());

		} catch (Throwable err) {
		    err.printStackTrace ();
		}*/
		
		
		
		
		
		String test = page.replace("\n", "");
		String content = null;
		
		//System.out.println(test);
		
		Pattern pattern = Pattern.compile("(<div id=\"content\">)(.+?)(<!-- #content -->)");
		Matcher matcher = pattern.matcher(test);

		System.out.println("cnt:" +matcher.groupCount());
		if(matcher.find())
		{
//			System.out.print("Start:"+ matcher.start());
	//		System.out.print("End:"+ matcher.end());
		//	System.out.println("Group:"+ matcher.group());
			
			content = matcher.group(2);
			
			pattern = Pattern.compile("<div class=\"post-\\d+.+?>.+?<h2 class=\"post-title\">.+?<a href.+?>(.+?)</a>.+?class=\"day\".+?>(.+?)</a>.+?<a.+?>(.+?)</a>.+?<p>(.+?)</p>");
			matcher = pattern.matcher(content);
			
			System.out.println("cnt:" +matcher.groupCount());
			
			if(matcher.find())
			{
				
				System.out.print("Start:"+ matcher.start());
				System.out.print("End:"+ matcher.end());
				System.out.println("Group:"+ matcher.group());
				
				content = matcher.group(2);
				
				//String[] postList = content.split("<!-- .post-wrapper -->");
				
				//for (String postit : postList)
				//{
					Pattern pattern1 = Pattern.compile("post-(\\d+?) .+?<a.+?>(.+?)<\\/a>.+?<a.+?>(.+?)<\\/a>.+?<a.+?>(.+?)<\\/a>.+?<p>(.+?)<\\/p>");
					Matcher matcher1 = pattern.matcher(content);
					
					System.out.println("Count "+ matcher1.groupCount() + matcher1.find());
					
					int lastMatchPos = 0;
					
						while (matcher1.find()) 
						{
							   System.out.println(matcher1.group(1));
							   System.out.println(matcher1.group(2));
							   System.out.println(matcher1.group(3));
							   System.out.println(matcher1.group(4));
							   //System.out.println(matcher1.group(5));
							   lastMatchPos = matcher1.end();
						}
						
				
				//}
				
				
				
			}
			
		}
		
		if(content != null)
		{
			//pattern = Pattern.compile(regex);
			//Matcher matcher = pattern.matcher(content);
		}
		
		// This splits it to only show the Divs containing new post.
		/*
		String[] pageSplit = page.split("<!-- #header-->");
		pageSplit = pageSplit[1].split("<!-- #main -->");
		pageSplit = pageSplit[0].split("<!-- .post-meta -->");
		pageSplit = pageSplit[0].split("<!-- .post-wrapper -->");
		
		System.out.println(pageSplit[0]);
		System.out.println("---------------------------------------");
		System.out.println(pageSplit[1]);
		*/
		
		
		
			/*
		for(String post : pageSplit)
		{
			System.out.println("----------------------------");
			Pattern pattern = Pattern.compile("(<div.*?>)(.*?)(</div>)");
			
			Matcher matcher = pattern.matcher(post);
			
			while (matcher.find())
			{
			      System.out.print("Start index: " + matcher.start());
			      System.out.print(" End index: " + matcher.end() + " ");
			      System.out.println(matcher.group());	
			}
					
					
			
		}*/
		
		//pageSplit = pageSplit[0].split("<!-- .post-meta -->");
		// end of the split... now split divs up per post
		
		//Temp split up per line
		//pageSplit = pageSplit[0].split("\\n");
		String[] pageSplit = page.split("<!-- #header-->");
		
		return pageSplit;
	//	return pageSplit;
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
