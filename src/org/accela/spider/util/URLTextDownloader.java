package org.accela.spider.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.StringTokenizer;

// TODO 能够根据URLConnection的contentType自动转换编码，
// 		但是如果contentType中没有写明，即使是HTML中meta标签中注明charset，
//		也不能自动转换编码。默认将使用系统编码。
public class URLTextDownloader
{
	public String download(URL url) throws IOException
	{
		return download(url, null);
	}

	public String download(URL url, String preferedContentType)
			throws IOException
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		URLConnection conn = url.openConnection();
		InputStream inputStream = conn.getInputStream(); // 将打开输入流放在contentType验证前面，可以分辨出UnknownHostException，而此时contentType返回的是null
		String contentType = conn.getContentType();

		testPreferedContentType(preferedContentType, contentType);
		String charset = getCharset(contentType);
		assert (charset != null);

		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(inputStream,
					charset));
		}
		catch (UnsupportedEncodingException ex)
		{
			reader = new BufferedReader(new InputStreamReader(inputStream));
		}
		StringBuffer content = new StringBuffer();

		char[] buffer = new char[8096];
		int numRead = 0;
		try
		{
			while (((numRead = reader.read(buffer)) != -1)) // 为了避免读掉换行符，不使用readLine方法
			{
				if (Thread.interrupted())
				{
					throw new InterruptedIOException();
				}

				content.append(buffer, 0, numRead);
			}
		}
		finally
		{
			reader.close();
		}

		return content.toString();
	}

	private void testPreferedContentType(String preferedContentType,
			String contentType) throws ContentUnpreferedException
	{
		if (preferedContentType != null)
		{
			if (null == contentType
					|| !contentType.contains(preferedContentType))
			{
				throw new ContentUnpreferedException(
						"not prefered content type format: "
								+ "prefered: "
								+ preferedContentType
								+ ", current: "
								+ contentType);
			}
		}
	}

	private String getCharset(String contentType)
	{
		if (null == contentType)
		{
			return "";
		}

		String charsetExp = extractCharsetExpression(contentType);
		if (null == charsetExp)
		{
			return "";
		}
		String charsetVal = extractCharsetValue(charsetExp);
		if (null == charsetVal)
		{
			return "";
		}
		String cleanCharsetVal = clearCharsetValue(charsetVal);
		assert (cleanCharsetVal != null);

		return cleanCharsetVal;
	}

	private String extractCharsetExpression(String contentType)
	{
		StringTokenizer tokens = new StringTokenizer(contentType, "; ");
		while (tokens.hasMoreTokens())
		{
			String token = tokens.nextToken().toLowerCase();
			if (token.contains("charset")
					|| token.contains("encoding")
					|| token.contains("encode"))
			{
				return token;
			}
		}

		return null;
	}

	private String extractCharsetValue(String charsetExp)
	{
		StringTokenizer tokens = new StringTokenizer(charsetExp, "=");
		while (tokens.hasMoreTokens())
		{
			String token = tokens.nextToken().trim().toLowerCase();
			if (token.contains("charset")
					|| token.contains("encoding")
					|| token.contains("encode"))
			{
				continue;
			}

			return token;
		}

		return null;
	}

	private String clearCharsetValue(String charsetVal)
	{
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < charsetVal.length(); i++)
		{
			char ch = charsetVal.charAt(i);
			if (Character.isLetterOrDigit(ch) || ('-' == ch) || ('_' == ch))
			{
				buf.append(ch);
			}
		}

		return buf.toString();
	}

	private void testGetCharset()
	{
		assert (getCharset("text/html; charset=utf-8").equals("utf-8"));
		assert (getCharset("text/html; encoding=utf-8").equals("utf-8"));
		assert (getCharset("text/html; encode=utf-8").equals("utf-8"));
		assert (getCharset("text/plain; charset=gb2312-8").equals("gb2312-8"));
		assert (getCharset("text/plain; charset=\'gb2312\'").equals("gb2312"));
		assert (getCharset("text/plain; charset=\"gb2312\"").equals("gb2312"));
		assert (getCharset("text/plain;;charset=\"gb2312\"").equals("gb2312"));
		assert (getCharset("text/html; encode=\"utf-8\"").equals("utf-8"));
		assert (getCharset("text/htm charset=\"utf-8\"").equals("utf-8"));
		assert (getCharset("text/html; charset=").equals(""));
		assert (getCharset("text/html; charset=\'\'").equals(""));
		assert (getCharset("text/html; charset=\"\"").equals(""));
		assert (getCharset("text/html; charset").equals(""));
		assert (getCharset("text/html").equals(""));
		assert (getCharset("").equals(""));
		assert (getCharset(null).equals(""));
	}

	public static void main(String[] args) throws MalformedURLException,
			IOException
	{
		System.setOut(new PrintStream("temp.txt"));

		URLTextDownloader d = new URLTextDownloader();
		System.out.println(d.download(new URL("http://www.chafanhou.com")));

		System.out.println();
		d.testGetCharset();
	}

}
