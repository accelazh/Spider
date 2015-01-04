package org.accela.spider.util.test;

import java.io.File;
import java.io.IOException;

import org.accela.spider.util.EnforcedRandomAccessFile;

import junit.framework.TestCase;

public class TestEnforcedRandomAccessFile extends TestCase
{
	public void testSimple() throws IOException
	{
		new File("testRaf.txt").delete();
		EnforcedRandomAccessFile raf=new EnforcedRandomAccessFile("testRaf.txt", "rw");
		raf.writeChars("\n\rhello world");
		raf.writeChars("hello world");
		raf.writeChars("\nhello world");
		raf.writeChars("\rhello world");
		raf.writeChars("\n\rhello world"); 
		raf.writeChars("\r\nhello world");
		raf.writeChars("\n\r\nhello world");
		raf.writeChars("\r\n\rhello world");
		raf.writeChars("\n\r\n\n\r");
		raf.writeByte(5);
		
		raf.seek(0);
		
		assert(raf.readCharLine().equals(""));
		assert(raf.getFilePointer()==2*2):raf.getFilePointer();
		
		assert(raf.readCharLine().equals("hello worldhello world"));
		assert(raf.getFilePointer()==2*2+23*2):raf.getFilePointer();
		
		assert(raf.readCharLine().equals("hello world"));
		assert(raf.getFilePointer()==2*2+23*2+12*2):raf.getFilePointer();
		
		assert(raf.readCharLine().equals("hello world"));
		assert(raf.getFilePointer()==2*2+23*2+12*2+13*2):raf.getFilePointer();
		
		assert(raf.readCharLine().equals("hello world"));
		assert(raf.getFilePointer()==2*2+23*2+12*2+13*2+13*2):raf.getFilePointer();
		
		assert(raf.readCharLine().equals("hello world"));
		assert(raf.getFilePointer()==2*2+23*2+12*2+13*2+13*2+14*2):raf.getFilePointer();
		
		assert(raf.readCharLine().equals("hello world"));
		assert(raf.getFilePointer()==2*2+23*2+12*2+13*2+13*2+14*2+14*2):raf.getFilePointer();
		
		assert(raf.readCharLine().equals("hello world"));
		assert(raf.getFilePointer()==2*2+23*2+12*2+13*2+13*2+14*2+14*2+16*2+1):raf.getFilePointer();
		
		assert(raf.readCharLine()==null);
		assert(raf.getFilePointer()==2*2+23*2+12*2+13*2+13*2+14*2+14*2+16*2+1):raf.getFilePointer();
		
		raf.seek(raf.getFilePointer()-3*2-1);
		assert(raf.readCharLine()==null);
		assert(raf.getFilePointer()==2*2+23*2+12*2+13*2+13*2+14*2+14*2+16*2+1):raf.getFilePointer();
		
	}
}
