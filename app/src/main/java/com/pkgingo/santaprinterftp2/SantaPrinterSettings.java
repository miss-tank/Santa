package com.pkgingo.santaprinterftp2;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class SantaPrinterSettings {

	private boolean addAFooter=true;
	private int footerHeight=200;
	private ARGBSettings footerARGB = new ARGBSettings();
	
	private ARGBSettings msgFontARGB = new ARGBSettings();
	private int msgFontSize=100;
	private String msgString="Happy Holidays from the CTA!";
	
	private ARGBSettings copyrightFontARGB = new ARGBSettings();
	private int copyrightFontSize=30;
	private String copyrightPrefix="© Chicago Transit Authority  ";
	
	SimpleDateFormat s = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
	
	Bitmap leftImage, rightImage;

	
	
	public SantaPrinterSettings()
	{
		addAFooter=false;
		msgFontSize=100;
		copyrightFontSize=30;
		footerHeight=200;
		copyrightPrefix="� Chicago Transit Authority  ";
		msgString="Happy Holidays from the CTA!";
		footerARGB.setARGB(0xff, 0xff, 0xff, 0xff); //white
		msgFontARGB.setARGB(0xff, 0xd3, 0x0c, 0x43); //cta red
		copyrightFontARGB.setARGB(0xff, 0x00, 0x79, 0xc2); //cta blue
		
	}
	
	public void addFooter()
	{
		addAFooter=true;
	}
	
	public void removeFooter()
	{
		addAFooter=false;
	}
	
	public boolean hasAFooter()
	{
		return addAFooter;
	}
	
	public void setFooterHeight(int h)
	{
		footerHeight=h;
	}
	
	public void setFooterHeight(String h)
	{
		if (h!=null)
		{
			footerHeight=Integer.parseInt(h);
		}
		else
		{
			removeFooter();
		}
	}
	
	public int getFooterHeight()
	{
		return footerHeight;
	}
	
	
	public ARGBSettings msgFontColor()
	{
		return msgFontARGB;
	}
	
	public ARGBSettings copyrightFontColor()
	{
		return copyrightFontARGB;
	}
	
	public ARGBSettings footerColor()
	{
		return footerARGB;
	}
	
	public String getCopyrightText()
	{
		if (copyrightPrefix.equalsIgnoreCase("")) return "";
		return copyrightPrefix + s.format(new Date());
	}
	
	public String getMessageText()
	{
		return msgString;
	}
	
	public void setMessageText(String s)
	{
		if (s==null)
		{
			s="";
		}
		msgString=s;
	}
	
	public void setCopyrightPrefixText(String s)
	{
		if (s==null)
		{
			s="";
		}
		copyrightPrefix=s;
	}
	
	public void setCopyrightFontSize(int s)
	{
		copyrightFontSize=s;
	}
	
	public void setMessageTextFontSize(int s)
	{
		msgFontSize=s;
	}
	
	public void setCopyrightFontSize(String s)
	{
		if (s!=null)
		{
			copyrightFontSize=Integer.parseInt(s);
		}
	}
	
	public void setMessageTextFontSize(String s)
	{
		if (s!=null)
		{
			msgFontSize=Integer.parseInt(s);
		}
	}
	
	public int getMessageTextFontSize()
	{
		return msgFontSize;
	}
	
	public int getCopyrightFontSize()
	{
		return copyrightFontSize;
	}
	
	public void setLeftImage(Bitmap b)
	{
		leftImage=b;
	}
	
	public void setRightImage(Bitmap b)
	{
		rightImage=b;
	}
	
	public Bitmap getLeftImage()
	{
		return leftImage;
	}
	
	public Bitmap getRightImage()
	{
		return rightImage;
	}
}
