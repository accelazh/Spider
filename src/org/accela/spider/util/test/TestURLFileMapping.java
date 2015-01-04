package org.accela.spider.util.test;

import java.io.File;
import java.net.MalformedURLException;

import org.accela.spider.util.URL;
import org.accela.spider.util.URLFileMapping;

import junit.framework.TestCase;

public class TestURLFileMapping extends TestCase
{
	public void testURLToFile() throws MalformedURLException
	{
		URLFileMapping mapping = new URLFileMapping();

		// group1: varying from root file
		assert (mapping
				.urlToFile(new File(""),
						new URL(
								"http://www.sina.com.cn/hello/nice.idx?id=100&search=1000#fragment"))
				.getPath()
				.equals("http\\sina.com.cn\\80\\hello\\nice.idx#id=100&search=1000#fragment"));
		assert (mapping
				.urlToFile(new File("   "),
						new URL(
								"http://www.sina.com.cn/hello/nice.idx?id=100&search=1000#fragment"))
				.getPath()
				.equals("http\\sina.com.cn\\80\\hello\\nice.idx#id=100&search=1000#fragment"));

		assert (mapping
				.urlToFile(new File("/"),
						new URL(
								"http://www.sina.com.cn/hello/nice.idx?id=100&search=1000#fragment"))
				.getPath()
				.equals("\\http\\sina.com.cn\\80\\hello\\nice.idx#id=100&search=1000#fragment"));
		assert (mapping
				.urlToFile(new File(" /  "),
						new URL(
								"http://www.sina.com.cn/hello/nice.idx?id=100&search=1000#fragment"))
				.getPath()
				.equals("\\http\\sina.com.cn\\80\\hello\\nice.idx#id=100&search=1000#fragment"));

		assert (mapping
				.urlToFile(new File("dir1"),
						new URL(
								"http://www.sina.com.cn/hello/nice.idx?id=100&search=1000#fragment"))
				.getPath()
				.equals("dir1\\http\\sina.com.cn\\80\\hello\\nice.idx#id=100&search=1000#fragment"));
		assert (mapping
				.urlToFile(new File("  dir1 "),
						new URL(
								"http://www.sina.com.cn/hello/nice.idx?id=100&search=1000#fragment"))
				.getPath()
				.equals("dir1\\http\\sina.com.cn\\80\\hello\\nice.idx#id=100&search=1000#fragment"));

		assert (mapping
				.urlToFile(new File("/dir1"),
						new URL(
								"http://www.sina.com.cn/hello/nice.idx?id=100&search=1000#fragment"))
				.getPath()
				.equals("\\dir1\\http\\sina.com.cn\\80\\hello\\nice.idx#id=100&search=1000#fragment"));
		assert (mapping
				.urlToFile(new File(" /dir1  "),
						new URL(
								"http://www.sina.com.cn/hello/nice.idx?id=100&search=1000#fragment"))
				.getPath()
				.equals("\\dir1\\http\\sina.com.cn\\80\\hello\\nice.idx#id=100&search=1000#fragment"));

		assert (mapping
				.urlToFile(new File("dir1/dir2"),
						new URL(
								"http://www.sina.com.cn/hello/nice.idx?id=100&search=1000#fragment"))
				.getPath()
				.equals("dir1\\dir2\\http\\sina.com.cn\\80\\hello\\nice.idx#id=100&search=1000#fragment"));
		assert (mapping
				.urlToFile(new File("  dir1/dir2 "),
						new URL(
								"http://www.sina.com.cn/hello/nice.idx?id=100&search=1000#fragment"))
				.getPath()
				.equals("dir1\\dir2\\http\\sina.com.cn\\80\\hello\\nice.idx#id=100&search=1000#fragment"));

		assert (mapping
				.urlToFile(new File("/dir1/dir2"),
						new URL(
								"http://www.sina.com.cn/hello/nice.idx?id=100&search=1000#fragment"))
				.getPath()
				.equals("\\dir1\\dir2\\http\\sina.com.cn\\80\\hello\\nice.idx#id=100&search=1000#fragment"));
		assert (mapping
				.urlToFile(new File(" /dir1/dir2  "),
						new URL(
								"http://www.sina.com.cn/hello/nice.idx?id=100&search=1000#fragment"))
				.getPath()
				.equals("\\dir1\\dir2\\http\\sina.com.cn\\80\\hello\\nice.idx#id=100&search=1000#fragment"));

		// group2: empty query and fragment from group1
		assert (mapping.urlToFile(new File(""),
				new URL("http://www.sina.com.cn/hello/nice.idx?#")).getPath()
				.equals("http\\sina.com.cn\\80\\hello\\nice.idx"));
		assert (mapping.urlToFile(new File("   "),
				new URL("http://www.sina.com.cn/hello/nice.idx?#")).getPath()
				.equals("http\\sina.com.cn\\80\\hello\\nice.idx"));

		assert (mapping.urlToFile(new File("/"),
				new URL("http://www.sina.com.cn/hello/nice.idx?#")).getPath()
				.equals("\\http\\sina.com.cn\\80\\hello\\nice.idx"));
		assert (mapping.urlToFile(new File(" /  "),
				new URL("http://www.sina.com.cn/hello/nice.idx?#")).getPath()
				.equals("\\http\\sina.com.cn\\80\\hello\\nice.idx"));

		assert (mapping.urlToFile(new File("dir1"),
				new URL("http://www.sina.com.cn/hello/nice.idx?#")).getPath()
				.equals("dir1\\http\\sina.com.cn\\80\\hello\\nice.idx"));
		assert (mapping.urlToFile(new File("  dir1 "),
				new URL("http://www.sina.com.cn/hello/nice.idx?#")).getPath()
				.equals("dir1\\http\\sina.com.cn\\80\\hello\\nice.idx"));

		assert (mapping.urlToFile(new File("/dir1"),
				new URL("http://www.sina.com.cn/hello/nice.idx?#")).getPath()
				.equals("\\dir1\\http\\sina.com.cn\\80\\hello\\nice.idx"));
		assert (mapping.urlToFile(new File(" /dir1  "),
				new URL("http://www.sina.com.cn/hello/nice.idx?#")).getPath()
				.equals("\\dir1\\http\\sina.com.cn\\80\\hello\\nice.idx"));

		assert (mapping.urlToFile(new File("dir1/dir2"),
				new URL("http://www.sina.com.cn/hello/nice.idx?#")).getPath()
				.equals("dir1\\dir2\\http\\sina.com.cn\\80\\hello\\nice.idx"));
		assert (mapping.urlToFile(new File("  dir1/dir2 "),
				new URL("http://www.sina.com.cn/hello/nice.idx?#")).getPath()
				.equals("dir1\\dir2\\http\\sina.com.cn\\80\\hello\\nice.idx"));

		assert (mapping.urlToFile(new File("/dir1/dir2"),
				new URL("http://www.sina.com.cn/hello/nice.idx?#")).getPath()
				.equals("\\dir1\\dir2\\http\\sina.com.cn\\80\\hello\\nice.idx"));
		assert (mapping.urlToFile(new File(" /dir1/dir2  "),
				new URL("http://www.sina.com.cn/hello/nice.idx?#")).getPath()
				.equals("\\dir1\\dir2\\http\\sina.com.cn\\80\\hello\\nice.idx"));

		// group2: removing empty query and fragment from group2
		assert (mapping.urlToFile(new File(""),
				new URL("http://www.sina.com.cn/hello/nice.idx")).getPath()
				.equals("http\\sina.com.cn\\80\\hello\\nice.idx"));
		assert (mapping.urlToFile(new File("   "),
				new URL("http://www.sina.com.cn/hello/nice.idx")).getPath()
				.equals("http\\sina.com.cn\\80\\hello\\nice.idx"));

		assert (mapping.urlToFile(new File("/"),
				new URL("http://www.sina.com.cn/hello/nice.idx")).getPath()
				.equals("\\http\\sina.com.cn\\80\\hello\\nice.idx"));
		assert (mapping.urlToFile(new File(" /  "),
				new URL("http://www.sina.com.cn/hello/nice.idx")).getPath()
				.equals("\\http\\sina.com.cn\\80\\hello\\nice.idx"));

		assert (mapping.urlToFile(new File("dir1"),
				new URL("http://www.sina.com.cn/hello/nice.idx")).getPath()
				.equals("dir1\\http\\sina.com.cn\\80\\hello\\nice.idx"));
		assert (mapping.urlToFile(new File("  dir1 "),
				new URL("http://www.sina.com.cn/hello/nice.idx")).getPath()
				.equals("dir1\\http\\sina.com.cn\\80\\hello\\nice.idx"));

		assert (mapping.urlToFile(new File("/dir1"),
				new URL("http://www.sina.com.cn/hello/nice.idx")).getPath()
				.equals("\\dir1\\http\\sina.com.cn\\80\\hello\\nice.idx"));
		assert (mapping.urlToFile(new File(" /dir1  "),
				new URL("http://www.sina.com.cn/hello/nice.idx")).getPath()
				.equals("\\dir1\\http\\sina.com.cn\\80\\hello\\nice.idx"));

		assert (mapping.urlToFile(new File("dir1/dir2"),
				new URL("http://www.sina.com.cn/hello/nice.idx")).getPath()
				.equals("dir1\\dir2\\http\\sina.com.cn\\80\\hello\\nice.idx"));
		assert (mapping.urlToFile(new File("  dir1/dir2 "),
				new URL("http://www.sina.com.cn/hello/nice.idx")).getPath()
				.equals("dir1\\dir2\\http\\sina.com.cn\\80\\hello\\nice.idx"));

		assert (mapping.urlToFile(new File("/dir1/dir2"),
				new URL("http://www.sina.com.cn/hello/nice.idx")).getPath()
				.equals("\\dir1\\dir2\\http\\sina.com.cn\\80\\hello\\nice.idx"));
		assert (mapping.urlToFile(new File(" /dir1/dir2  "),
				new URL("http://www.sina.com.cn/hello/nice.idx")).getPath()
				.equals("\\dir1\\dir2\\http\\sina.com.cn\\80\\hello\\nice.idx"));

	}
}
