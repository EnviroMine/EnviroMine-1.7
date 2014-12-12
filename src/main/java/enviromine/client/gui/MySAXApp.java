package enviromine.client.gui;
import java.io.FileReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class MySAXApp extends DefaultHandler
{

    public MySAXApp ()
    {
    	super();
    }

    public static void main (String page)	throws Exception
    {
    	XMLReader xr = XMLReaderFactory.createXMLReader();
    	MySAXApp handler = new MySAXApp();
    	xr.setContentHandler(handler);
    	xr.setErrorHandler(handler);

    				// Parse each file provided on the
    				// command line.

    	    //FileReader r = new FileReader(page);
    	    xr.parse(new InputSource(new StringReader(page)));
    	
    	
    	
    }
    boolean item = false;
    boolean bTitle = false;
    boolean bLink = false;
    boolean bCreator = false;
    boolean bPubDate = false;
    boolean bDescription = false;
    
    public void startElement (String uri, String name,  String qName, Attributes atts)
    {
    	if(qName.equalsIgnoreCase("item"))
    	{
    		item = true;
    	}
    	
    	if(qName.equalsIgnoreCase("title") && item)
    	{
    		System.out.print(qName +":");
    		bTitle = true;
    	}  
    	else if(qName.equalsIgnoreCase("link") && item)
    	{
    		System.out.print(qName +":");
    		bLink = true;
    	}
    	else if(qName.equalsIgnoreCase("dc:creator") && item)
    	{
    		System.out.print(qName +":");
    		bCreator = true;
    	}    	
    	else if(qName.equalsIgnoreCase("pubDate") && item)
    	{
    		System.out.print(qName +":");
    		bPubDate = true;
    	}
    	else if(qName.equalsIgnoreCase("description") && item)
    	{
    		System.out.print(qName +":");
    		bDescription = true;
    	}
    	/*
    	if ("".equals (uri))
    		System.out.println("Start element: " + qName);
    	else
    		System.out.println("Start element: {" + uri + "}" + name);
    		*/
    }

    public void endElement (String uri, String name, String qName)
    {
    	if(qName.equalsIgnoreCase("item"))
    	{
    		item = false;
    	}
    	
    	/*
    	if ("".equals (uri))
    		System.out.println("End element: " + qName);
    	else
    		ystem.out.println("End element:   {" + uri + "}" + name);
    		*/
    }
    
    public void characters (char ch[], int start, int length)
    {
    	
    	if(bTitle)
    	{
    		String vart = new String(ch, start, length);
    		System.out.println(vart);
    		bTitle = false;
    	}
    	else if(bLink)
    	{
    		String vart = new String(ch, start, length);
    		System.out.println(vart);
    		bLink = false;
    	}
    	else if (bCreator)
    	{
    		String vart = new String(ch, start, length);
    		System.out.println(vart);
    		bCreator = false;
    	}
    	else if(bPubDate)
    	{
    		String vart = new String(ch, start, length);
    		System.out.println(vart);
    		bPubDate = false;
    	}
    	else if(bDescription)
    	{
    		String vart = new String(ch, start, length);
    	     String nohtml = vart.toString().replaceAll("\\<.*?>","");
    	     System.out.println(StringEscapeUtils.unescapeHtml4(nohtml));
    		//System.out.println(nohtml);
    		bDescription = false;
    	}
    	/*
	System.out.print("Characters:    \"");
	for (int i = start; i < start + length; i++) {
	    switch (ch[i]) {
	    case '\\':
		System.out.print("\\\\");
		break;
	    case '"':
		System.out.print("\\\"");
		break;
	    case '\n':
		System.out.print("\\n");
		break;
	    case '\r':
		System.out.print("\\r");
		break;
	    case '\t':
		System.out.print("\\t");
		break;
	    default:
		System.out.print(ch[i]);
		break;
	    }
	}
	System.out.print("\"\n");*/
    }
}