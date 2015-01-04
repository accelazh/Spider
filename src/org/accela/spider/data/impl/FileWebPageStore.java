package org.accela.spider.data.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.accela.common.Assertion;
import org.accela.spider.data.WebPage;
import org.accela.spider.data.WebPageStore;
import org.accela.spider.data.WebPageStoreException;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.util.ResourceLocker;
import org.accela.spider.util.URL;
import org.accela.spider.util.URLFileMapping;
import org.accela.spider.util.URLPath;

public class FileWebPageStore implements WebPageStore
{
	private Mapper mapper = null;

	private Writer writer = null;

	public FileWebPageStore(File parentDir)
	{
		this(new DirectoryPathMapper(parentDir), new SeparatedBySuffixWriter());
	}

	public FileWebPageStore(Mapper mapper, Writer writer)
	{
		if (null == mapper)
		{
			throw new IllegalArgumentException("mapper should not be null");
		}
		if (null == writer)
		{
			throw new IllegalArgumentException("writer should not be null");
		}

		this.mapper = mapper;
		this.writer = writer;
	}

	@Override
	public boolean contains(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		return mapper.map(url).exists();
	}

	@Override
	public long getStamp(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		File file = mapper.map(url);
		if (file.exists())
		{
			return file.lastModified();
		}

		return -1;
	}

	@Override
	public void put(WebPage page) throws WebPageStoreException
	{
		if (null == page)
		{
			throw new IllegalArgumentException("page should not be null");
		}
		if (null == page.getURL())
		{
			throw new IllegalArgumentException(
					"page.getURL() should not be null");
		}
		if (null == page.getContent())
		{
			throw new IllegalArgumentException(
					"page.getContent() should not be null");
		}
		if (null == page.getLinks())
		{
			throw new IllegalArgumentException(
					"page.getLinks() should not be null");
		}
		if (null == page.getAnalysis())
		{
			throw new IllegalArgumentException(
					"page.getAnalysis() should not be null");
		}

		File file = mapper.map(page.getURL());

		File parentFile = file.getParentFile();
		assert (parentFile != null) : Assertion.declare();
		parentFile.mkdirs();
		if (!parentFile.isDirectory())
		{
			throw new WebPageStoreException(
					"failed to create parent directory for file: " + file);
		}

		try
		{
			writer.write(file, page);
		}
		catch (IOException ex)
		{
			throw new WebPageStoreException(ex);
		}
	}

	public static interface Mapper
	{
		public File map(URL url);
	}

	public static class DirectoryPathMapper implements Mapper
	{
		private URLFileMapping mapping = new URLFileMapping();

		private File parentDir = null;

		public DirectoryPathMapper(File parentDir)
		{
			if (null == parentDir)
			{
				throw new IllegalArgumentException(
						"parentDir should not be null");
			}

			this.parentDir = parentDir;
		}

		@Override
		public File map(URL url)
		{
			if (null == url)
			{
				throw new IllegalArgumentException("url should not be null");
			}

			return urlToFile(url);
		}

		private File urlToFile(URL url)
		{
			if (null == url)
			{
				throw new IllegalArgumentException("url should not be null");
			}

			URLPath urlPath = new URLPath(url.getPath());
			assert (!urlPath.getName().equals("..")) : Assertion.declare();
			assert (!urlPath.getName().equals(".")) : Assertion.declare();

			String fileName = urlPath.getName();
			int idxOfDot = fileName.indexOf('.');
			if (idxOfDot <= 0 || idxOfDot >= fileName.length() - 1)
			{
				List<String> urlTokens = urlPath.getTokens();
				urlTokens.add("index.html");
				urlPath = new URLPath(urlTokens);
			}

			try
			{
				url = new URL(url.getProtocol(), url.getHost(), url.getPort(),
						urlPath.getPath(), url.getQuery(), url.getRef());
			}
			catch (MalformedURLException ex)
			{
				ex.printStackTrace();
				assert (false) : Assertion.declare();
			}

			assert (checkURLIsFileNotDir(url)) : Assertion.declare();
			File ret = mapping.urlToFile(parentDir, url);
			assert (ret != null) : Assertion.declare();

			return ret;
		}

		private boolean checkURLIsFileNotDir(URL url)
		{
			if (null == url)
			{
				throw new IllegalArgumentException("url should not be null");
			}

			URLPath path = new URLPath(url.getPath());
			if (path.countToken() <= 0)
			{
				return false;
			}
			if (path.getName().equals(".") || path.getName().equals(".."))
			{
				return false;
			}
			if (path.getName().length() <= 0)
			{
				return false;
			}

			if (path.getName().indexOf('.') <= 0
					|| path.getName().indexOf('.') >= path.getName().length() - 1)
			{
				return false;
			}

			return true;
		}

	}

	public static interface Writer
	{
		public void write(File file, WebPage page) throws IOException;
	}

	public static class SeparatedBySuffixWriter implements Writer
	{
		private ResourceLocker<File> lock = new ResourceLocker<File>();

		@Override
		public void write(File file, WebPage page) throws IOException
		{
			if (null == file)
			{
				throw new IllegalArgumentException("file should not be null");
			}
			if (file.getPath().endsWith("/") || file.getPath().endsWith("\\"))
			{
				throw new IllegalArgumentException(
						"file represents a directory");
			}
			if (null == page)
			{
				throw new IllegalArgumentException("page should not be null");
			}
			if (null == page.getURL())
			{
				throw new IllegalArgumentException(
						"page.getURL() should not be null");
			}
			if (null == page.getContent())
			{
				throw new IllegalArgumentException(
						"page.getContent() should not be null");
			}
			if (null == page.getLinks())
			{
				throw new IllegalArgumentException(
						"page.getLinks() should not be null");
			}
			if (null == page.getAnalysis())
			{
				throw new IllegalArgumentException(
						"page.getAnalysis() should not be null");
			}

			lock.lock(file);
			try
			{
				writeToFile(addSuffix(file, "url"), page.getURL().toString());

				writeToFile(file, page.getContent().getText());

				StringBuffer linksStr = new StringBuffer();
				for (Hyperlink link : page.getLinks())
				{
					linksStr.append(link.getURL().toString());
					linksStr.append('\n');
				}
				writeToFile(addSuffix(file, "links"), linksStr.toString());

				writeToFile(addSuffix(file, "analysis"), "");

				writeToFile(addSuffix(file, "stamp"), "" + page.getStamp());

				writeToFile(addSuffix(file, "recursion"), ""
						+ page.getRecursion());
			}
			finally
			{
				lock.unlock(file);
			}

		}

		private File addSuffix(File file, String suffix)
		{
			if (null == file)
			{
				throw new IllegalArgumentException("file should not be null");
			}
			if (file.getPath().endsWith("/") || file.getPath().endsWith("\\"))
			{
				throw new IllegalArgumentException(
						"file represents a directory");
			}
			if (null == suffix)
			{
				throw new IllegalArgumentException("suffix should not be null");
			}
			if (suffix.length() <= 0)
			{
				throw new IllegalArgumentException("suffix should not be empty");
			}
			for (int i = 0; i < suffix.length(); i++)
			{
				if (!Character.isLetterOrDigit(suffix.charAt(i)))
				{
					throw new IllegalArgumentException(
							"suffix should not contain a character neither letter or digit");
				}
			}

			return new File(file.getPath() + "." + suffix);
		}

		private void writeToFile(File file, String content) throws IOException
		{
			if (null == file)
			{
				throw new IllegalArgumentException("file should not be null");
			}

			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			try
			{
				out.write(content);
			}
			finally
			{
				out.close();
			}
		}
	}

}
