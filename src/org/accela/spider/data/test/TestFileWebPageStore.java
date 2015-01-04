package org.accela.spider.data.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;

import org.accela.spider.data.WebPage;
import org.accela.spider.data.WebPageStoreException;
import org.accela.spider.data.impl.EmptyAnalysis;
import org.accela.spider.data.impl.FileWebPageStore;
import org.accela.spider.data.impl.TextContent;
import org.accela.spider.strategy.impl.URLOnlyHyperlink;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestFileWebPageStore extends TestCase
{
	@Override
	protected void setUp() throws Exception
	{
		clearDir(new File("useless"));
		
		ensureWorkingDirEmpty();
	}

	private void clearDir(File file)
	{
		if (!file.isDirectory())
		{
			return;
		}

		for (File f : file.listFiles())
		{
			deleteFile(f);
		}
	}

	private void deleteFile(File file)
	{
		if (file.isDirectory())
		{
			for (File child : file.listFiles())
			{
				deleteFile(child);
			}
		}
		file.delete();
	}

	private void ensureWorkingDirEmpty()
	{
		File dir = new File("useless");
		if (!dir.isDirectory())
		{
			throw new IllegalStateException("working dir not created");
		}
		if (dir.listFiles().length > 0)
		{
			throw new IllegalStateException("working dir not cleaned");
		}
	}

	public void testDirectoryPathMapper() throws MalformedURLException
	{
		// group 1
		FileWebPageStore.DirectoryPathMapper mapper = new FileWebPageStore.DirectoryPathMapper(
				new File(""));
		assert (mapper.map(new URL("http://www.sina.com.cn")).getPath()
				.equals("http\\sina.com.cn\\80\\index.html"));
		assert (mapper.map(new URL("http://www.sina.com.cn:81/")).getPath()
				.equals("http\\sina.com.cn\\81\\index.html"));
		assert (mapper.map(new URL("http://www.sina.com.cn/hello")).getPath()
				.equals("http\\sina.com.cn\\80\\hello\\index.html"));
		assert (mapper.map(new URL("http://www.sina.com.cn/hello.htm"))
				.getPath().equals("http\\sina.com.cn\\80\\hello.htm"));
		assert (mapper.map(new URL("http://www.sina.com.cn:82/hello.htm/nice"))
				.getPath()
				.equals("http\\sina.com.cn\\82\\hello.htm\\nice\\index.html"));
		assert (mapper
				.map(new URL("http://www.sina.com.cn/hello.htm/nice.asp"))
				.getPath().equals("http\\sina.com.cn\\80\\hello.htm\\nice.asp"));
		assert (mapper
				.map(new URL("http://www.sina.com.cn/hello.htm/nice.asp/"))
				.getPath()
				.equals("http\\sina.com.cn\\80\\hello.htm\\nice.asp"));
		assert (mapper
				.map(new URL("http://www.sina.com.cn:99/hello.htm/default.html/"))
				.getPath()
				.equals("http\\sina.com.cn\\99\\hello.htm\\default.html"));

		// group 2
		mapper = new FileWebPageStore.DirectoryPathMapper(
				new File("/"));
		assert (mapper.map(new URL("http://www.sina.com.cn")).getPath()
				.equals("\\http\\sina.com.cn\\80\\index.html"));
		assert (mapper.map(new URL("http://www.sina.com.cn:81/")).getPath()
				.equals("\\http\\sina.com.cn\\81\\index.html"));
		assert (mapper.map(new URL("http://www.sina.com.cn/hello")).getPath()
				.equals("\\http\\sina.com.cn\\80\\hello\\index.html"));
		assert (mapper.map(new URL("http://www.sina.com.cn/hello.htm"))
				.getPath().equals("\\http\\sina.com.cn\\80\\hello.htm"));
		assert (mapper.map(new URL("http://www.sina.com.cn:82/hello.htm/nice"))
				.getPath()
				.equals("\\http\\sina.com.cn\\82\\hello.htm\\nice\\index.html"));
		assert (mapper
				.map(new URL("http://www.sina.com.cn/hello.htm/nice.asp"))
				.getPath().equals("\\http\\sina.com.cn\\80\\hello.htm\\nice.asp"));
		assert (mapper
				.map(new URL("http://www.sina.com.cn/hello.htm/nice.asp/"))
				.getPath()
				.equals("\\http\\sina.com.cn\\80\\hello.htm\\nice.asp"));
		assert (mapper
				.map(new URL("http://www.sina.com.cn:99/hello.htm/default.html/"))
				.getPath()
				.equals("\\http\\sina.com.cn\\99\\hello.htm\\default.html"));

		// group 3
		mapper = new FileWebPageStore.DirectoryPathMapper(
				new File("dir"));
		assert (mapper.map(new URL("http://www.sina.com.cn")).getPath()
				.equals("dir\\http\\sina.com.cn\\80\\index.html"));
		assert (mapper.map(new URL("http://www.sina.com.cn:81/")).getPath()
				.equals("dir\\http\\sina.com.cn\\81\\index.html"));
		assert (mapper.map(new URL("http://www.sina.com.cn/hello")).getPath()
				.equals("dir\\http\\sina.com.cn\\80\\hello\\index.html"));
		assert (mapper.map(new URL("http://www.sina.com.cn/hello.htm"))
				.getPath().equals("dir\\http\\sina.com.cn\\80\\hello.htm"));
		assert (mapper.map(new URL("http://www.sina.com.cn:82/hello.htm/nice"))
				.getPath()
				.equals("dir\\http\\sina.com.cn\\82\\hello.htm\\nice\\index.html"));
		assert (mapper
				.map(new URL("http://www.sina.com.cn/hello.htm/nice.asp"))
				.getPath().equals("dir\\http\\sina.com.cn\\80\\hello.htm\\nice.asp"));
		assert (mapper
				.map(new URL("http://www.sina.com.cn/hello.htm/nice.asp/"))
				.getPath()
				.equals("dir\\http\\sina.com.cn\\80\\hello.htm\\nice.asp"));
		assert (mapper
				.map(new URL("http://www.sina.com.cn:99/hello.htm/default.html/"))
				.getPath()
				.equals("dir\\http\\sina.com.cn\\99\\hello.htm\\default.html"));

		// group 4
		mapper = new FileWebPageStore.DirectoryPathMapper(
				new File("/dir"));
		assert (mapper.map(new URL("ftp://www.sina.com.cn")).getPath()
				.equals("\\dir\\ftp\\sina.com.cn\\21\\index.html"));
		assert (mapper.map(new URL("ftp://www.sina.com.cn:81/")).getPath()
				.equals("\\dir\\ftp\\sina.com.cn\\81\\index.html"));
		assert (mapper.map(new URL("ftp://www.sina.com.cn/hello")).getPath()
				.equals("\\dir\\ftp\\sina.com.cn\\21\\hello\\index.html"));
		assert (mapper.map(new URL("ftp://www.sina.com.cn/hello.htm"))
				.getPath().equals("\\dir\\ftp\\sina.com.cn\\21\\hello.htm"));
		assert (mapper.map(new URL("ftp://www.sina.com.cn:82/hello.htm/nice"))
				.getPath()
				.equals("\\dir\\ftp\\sina.com.cn\\82\\hello.htm\\nice\\index.html"));
		assert (mapper
				.map(new URL("ftp://www.sina.com.cn/hello.htm/nice.asp"))
				.getPath().equals("\\dir\\ftp\\sina.com.cn\\21\\hello.htm\\nice.asp"));
		assert (mapper
				.map(new URL("ftp://www.sina.com.cn/hello.htm/nice.asp/"))
				.getPath()
				.equals("\\dir\\ftp\\sina.com.cn\\21\\hello.htm\\nice.asp"));
		assert (mapper
				.map(new URL("ftp://www.sina.com.cn:99/hello.htm/default.html/"))
				.getPath()
				.equals("\\dir\\ftp\\sina.com.cn\\99\\hello.htm\\default.html"));

	}

	public void testSeparatedBySuffixWriter() throws IOException
	{
		FileWebPageStore.SeparatedBySuffixWriter writer = new FileWebPageStore.SeparatedBySuffixWriter();

		WebPage page = new WebPage(new URL("http://www.sina.com.cn"));
		page.setContent(new TextContent("this is content"));
		page.setAnalysis(new EmptyAnalysis());
		page.setRecursion(19);
		page.setStamp(1234);
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello1")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello2")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello3")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello4")));

		writer.write(new File("useless/nice.hhh"), page);

		assert (new File("useless/nice.hhh").exists());
		assert (new File("useless/nice.hhh.links").exists());
		assert (new File("useless/nice.hhh.analysis").exists());
		assert (new File("useless/nice.hhh.url").exists());
		assert (new File("useless/nice.hhh.recursion").exists());
		assert (new File("useless/nice.hhh.stamp").exists());

		BufferedReader in = new BufferedReader(new FileReader(
				"useless/nice.hhh"));
		assert (in.readLine().equals("this is content"));
		assert (in.readLine() == null);
		in.close();

		in = new BufferedReader(new FileReader("useless/nice.hhh.links"));
		assert (in.readLine()
				.equals(page.getLinks().get(0).getURL().toString()));
		assert (in.readLine()
				.equals(page.getLinks().get(1).getURL().toString()));
		assert (in.readLine()
				.equals(page.getLinks().get(2).getURL().toString()));
		assert (in.readLine()
				.equals(page.getLinks().get(3).getURL().toString()));
		assert (in.readLine() == null);
		in.close();

		in = new BufferedReader(new FileReader("useless/nice.hhh.analysis"));
		assert (in.readLine() == null);
		in.close();

		in = new BufferedReader(new FileReader("useless/nice.hhh.url"));
		assert (in.readLine().equals(page.getURL().toString()));
		assert (in.readLine() == null);
		in.close();

		in = new BufferedReader(new FileReader("useless/nice.hhh.recursion"));
		assert (in.readLine().equals("19"));
		assert (in.readLine() == null);
		in.close();

		in = new BufferedReader(new FileReader("useless/nice.hhh.stamp"));
		assert (in.readLine().equals("1234"));
		assert (in.readLine() == null);
		in.close();
	}

	public void testSimple() throws WebPageStoreException,
			InterruptedException, IOException
	{
		FileWebPageStore store = new FileWebPageStore(new File("useless"));

		WebPage page = new WebPage(new URL("http://www.sina.com.cn"));
		page.setContent(new TextContent("this is content"));
		page.setAnalysis(new EmptyAnalysis());
		page.setRecursion(19);
		page.setStamp(1234);
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello1")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello2")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello3")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello4")));

		assert (!store.contains(page.getURL()));
		assert (store.getStamp(page.getURL()) == -1);

		store.put(page);

		assert (store.contains(page.getURL()));
		assert (Math.abs(System.currentTimeMillis()
				- store.getStamp(page.getURL())) < 2000);

		assert (new File("useless/http/sina.com.cn/80/index.html").exists());
		assert (new File("useless/http/sina.com.cn/80/index.html.links").exists());
		assert (new File("useless/http/sina.com.cn/80/index.html.analysis")
				.exists());
		assert (new File("useless/http/sina.com.cn/80/index.html.url").exists());
		assert (new File("useless/http/sina.com.cn/80/index.html.recursion")
				.exists());
		assert (new File("useless/http/sina.com.cn/80/index.html.stamp").exists());

		Thread.sleep(10000);

		page.setContent(new TextContent("this is the content2"));
		store.put(page);

		assert (Math.abs(System.currentTimeMillis()
				- store.getStamp(page.getURL())) < 2000);

		BufferedReader in = new BufferedReader(new FileReader(
				"useless/http/sina.com.cn/80/index.html"));
		assert (in.readLine().equals("this is the content2"));
		assert (in.readLine() == null);
		in.close();

		in = new BufferedReader(new FileReader(
				"useless/http/sina.com.cn/80/index.html.links"));
		assert (in.readLine()
				.equals(page.getLinks().get(0).getURL().toString()));
		assert (in.readLine()
				.equals(page.getLinks().get(1).getURL().toString()));
		assert (in.readLine()
				.equals(page.getLinks().get(2).getURL().toString()));
		assert (in.readLine()
				.equals(page.getLinks().get(3).getURL().toString()));
		assert (in.readLine() == null);
		in.close();

		in = new BufferedReader(new FileReader(
				"useless/http/sina.com.cn/80/index.html.analysis"));
		assert (in.readLine() == null);
		in.close();

		in = new BufferedReader(new FileReader(
				"useless/http/sina.com.cn/80/index.html.url"));
		assert (in.readLine().equals(page.getURL().toString()));
		assert (in.readLine() == null);
		in.close();

		in = new BufferedReader(new FileReader(
				"useless/http/sina.com.cn/80/index.html.recursion"));
		assert (in.readLine().equals("19"));
		assert (in.readLine() == null);
		in.close();

		in = new BufferedReader(new FileReader(
				"useless/http/sina.com.cn/80/index.html.stamp"));
		assert (in.readLine().equals("1234"));
		assert (in.readLine() == null);
		in.close();

		// =================

		page = new WebPage(new URL(
				"http://www.sina.com.cn/A1/A2/A3/B1/B2/B3/hello"));
		page.setContent(new TextContent("this is content"));
		page.setAnalysis(new EmptyAnalysis());
		page.setRecursion(19);
		page.setStamp(1234);
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello1")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello2")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello3")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello4")));

		store.put(page);

		assert (new File(
				"useless/http/sina.com.cn/80/A1/A2/A3/B1/B2/B3/hello/index.html")
				.exists());
		assert (new File(
				"useless/http/sina.com.cn/80/A1/A2/A3/B1/B2/B3/hello/index.html.links")
				.exists());
		assert (new File(
				"useless/http/sina.com.cn/80/A1/A2/A3/B1/B2/B3/hello/index.html.analysis")
				.exists());
		assert (new File(
				"useless/http/sina.com.cn/80/A1/A2/A3/B1/B2/B3/hello/index.html.url")
				.exists());
		assert (new File(
				"useless/http/sina.com.cn/80/A1/A2/A3/B1/B2/B3/hello/index.html.recursion")
				.exists());
		assert (new File(
				"useless/http/sina.com.cn/80/A1/A2/A3/B1/B2/B3/hello/index.html.stamp")
				.exists());

		// ==================

		page = new WebPage(new URL(
				"http://www.sina.com.cn/A1/A2/A3/B1/B2/B3/hello.htm"));
		page.setContent(new TextContent("this is content"));
		page.setAnalysis(new EmptyAnalysis());
		page.setRecursion(19);
		page.setStamp(1234);
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello1")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello2")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello3")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello4")));

		store.put(page);

		assert (new File("useless/http/sina.com.cn/80/A1/A2/A3/B1/B2/B3/hello.htm")
				.exists());
		assert (new File(
				"useless/http/sina.com.cn/80/A1/A2/A3/B1/B2/B3/hello.htm.links")
				.exists());
		assert (new File(
				"useless/http/sina.com.cn/80/A1/A2/A3/B1/B2/B3/hello.htm.analysis")
				.exists());
		assert (new File(
				"useless/http/sina.com.cn/80/A1/A2/A3/B1/B2/B3/hello.htm.url")
				.exists());
		assert (new File(
				"useless/http/sina.com.cn/80/A1/A2/A3/B1/B2/B3/hello.htm.recursion")
				.exists());
		assert (new File(
				"useless/http/sina.com.cn/80/A1/A2/A3/B1/B2/B3/hello.htm.stamp")
				.exists());
	}

	// need to be tested manually
	public void testTestSeparatedBySuffixWriterLock() throws IOException,
			InterruptedException
	{
		final FileWebPageStore.SeparatedBySuffixWriter writer = new FileWebPageStore.SeparatedBySuffixWriter();

		final WebPage page = new WebPage(new URL("http://www.sina.com.cn"));
		page.setContent(new TextContent("this is content"));
		page.setAnalysis(new EmptyAnalysis());
		page.setRecursion(19);
		page.setStamp(1234);
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello1")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello2")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello3")));
		page.getLinks().add(new URLOnlyHyperlink(new URL(
				"http://www.sina.com.cn/hello4")));

		class Runner implements Runnable
		{
			private String path = null;

			public Runner(String path)
			{
				this.path = path;
			}

			@Override
			public void run()
			{
				try
				{
					writer.write(new File(path), page);
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}

		new Thread(new Runner("useless/hello.hhh")).start();
		new Thread(new Runner("useless/hello.hhh")).start();
		new Thread(new Runner("useless/hello2.hhh")).start();
	}

}
