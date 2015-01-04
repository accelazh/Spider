package org.accela.spider.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class EnforcedRandomAccessFile extends RandomAccessFile
{

	public EnforcedRandomAccessFile(File file, String mode)
			throws FileNotFoundException
	{
		super(file, mode);
	}

	public EnforcedRandomAccessFile(String name, String mode)
			throws FileNotFoundException
	{
		super(name, mode);
	}

	public String readCharLine() throws IOException
	{
		StringBuffer buf = new StringBuffer();
		boolean eof=false;

		while (true)
		{
			char ch = 0;
			try
			{
				ch = this.readChar();
			}
			catch (EOFException ex)
			{
				eof=true;
				break;
			}

			if ('\n' == ch || '\r' == ch)
			{
				break;
			}
			
			buf.append(ch);
		}
		
		while(true)
		{
			char ch=0;
			long lastPos=0;
			
			lastPos=this.getFilePointer();
			
			try
			{
				ch=this.readChar();
			}
			catch (EOFException ex)
			{
				eof=true;
				break;
			}
			
			if(ch!='\n'&&ch!='\r')
			{
				this.seek(lastPos);
				break;
			}
		}
		
		if(eof&&buf.length()<=0)
		{
			return null;
		}
		else
		{
			return buf.toString();
		}
		
	}

}
