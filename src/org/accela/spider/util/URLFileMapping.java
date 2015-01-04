package org.accela.spider.util;

import java.io.File;

import org.accela.common.Assertion;

//TODO BUG: 将Query和Ref映射成为文件名，可能导致同一文件夹下有大量文件，超过系统限制，或者文件路径过长
public class URLFileMapping
{
	// http://www.sina.com.cn/hello/index.html?search=10&id=100#fragment
	// will be mapped to
	// rootFile/http/www.sina.com.cn/80/hello/index.html#id=10&search=100#fragment
	public File urlToFile(File root, URL url)
	{
		if (null == root)
		{
			throw new IllegalArgumentException("root should not be null");
		}
		root = new File(root.getPath().trim());
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		String pathStr = "/"
				+ url.getProtocol()
				+ "/"
				+ url.getHost()
				+ "/"
				+ url.getPort()
				+ url.getPath()
				+ normalizeFileNameSupplement(url.getQuery())
				+ normalizeFileNameSupplement(url.getRef());

		String rootStr = root.getPath();

		String retStr = "";
		if (rootStr.length() > 0)
		{
			if (rootStr.endsWith("/") || rootStr.endsWith("\\"))
			{
				rootStr = rootStr.substring(0, rootStr.length() - 1);
			}
			retStr = rootStr + pathStr;
		}
		else
		{
			retStr = rootStr + pathStr.substring(1);
		}

		File ret = new File(retStr);
		assert (ret.getPath().trim().equals(ret.getPath())) : Assertion
				.declare();
		assert (checkFileIsParent(root, ret)) : Assertion.declare();
		return ret;
	}

	private String normalizeFileNameSupplement(String str)
	{
		if (null == str)
		{
			return "";
		}

		str = str.trim();
		str = str.replace("/", "%2F").replace("\\", "%5C").replace("?", "%3F")
				.replace(":", "%3A").replace("<", "%3C").replace(">", "%3E")
				.replace("|", "%7C").replace("*", "%2A").replace("\"", "%22");

		return "#" + str;
	}

	private boolean checkFileIsParent(File root, File file)
	{
		if (null == root)
		{
			throw new IllegalArgumentException("root should not be null");
		}
		if (null == file)
		{
			throw new IllegalArgumentException("file should not be null");
		}

		if (root.getPath().length() <= 0)
		{
			String filePath = file.getPath();

			return !filePath.startsWith("/")
					&& !filePath.startsWith("\\")
					&& filePath.length() > 0;
		}

		File fileParent = file.getParentFile();
		while (fileParent != null)
		{
			if (fileParent.equals(root))
			{
				return true;
			}

			fileParent = fileParent.getParentFile();
		}

		return false;
	}

}
