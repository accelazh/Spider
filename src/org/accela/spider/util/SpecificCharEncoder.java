package org.accela.spider.util;

import org.accela.common.Assertion;

//TODO 功能局限：如果需要encode超过10个字符，那么'\'的数量将会超过1024
public class SpecificCharEncoder
{
	private char meta = 0;

	public SpecificCharEncoder()
	{
		this('\\');
	}

	public SpecificCharEncoder(char meta)
	{
		this.meta = meta;
	}

	public char getMeta()
	{
		return this.meta;
	}

	public String encode(String str, char orign, char encoded)
	{
		if (null == str)
		{
			throw new IllegalArgumentException("str should not be null");
		}
		if (orign == meta)
		{
			throw new IllegalArgumentException(
					"orign should not be equal to meta: " + orign);
		}
		if (encoded == meta)
		{
			throw new IllegalArgumentException(
					"encoded should not be equal to meta: " + encoded);
		}
		if (orign == encoded)
		{
			throw new IllegalArgumentException(
					"orign should not be equal to encoded: " + orign);
		}

		String encodedStr = hideSpecified(hideMeta(str), orign, encoded);
		assert(checkEncodingValid(encodedStr, orign, encoded, str)):Assertion.declare();
		return encodedStr;
	}
	
	private boolean checkEncodingValid(String str, char orign, char encoded, String orignStr)
	{
		if(str.contains(""+orign))
		{
			return false;
		}
		int idx=0;
		while(idx<str.length())
		{
			if(str.charAt(idx)!=meta)
			{
				idx++;
				continue;
			}
			if(idx+1>=str.length())
			{
				idx++;
				return false;
			}
			
			char next=str.charAt(idx+1);
			if(next==meta)
			{
				idx+=2;
				continue;
			}
			if(next==encoded)
			{
				idx+=2;
				continue;
			}
			
			return false;
		}
		
		if(!decode(str, encoded, orign).equals(orignStr))
		{
			assert(false);
			return false;
		}
		
		return true;
	}

	private String hideMeta(String str)
	{
		return str.replace("" + meta, "" + meta + "" + meta);
	}

	private String hideSpecified(String str, char orign, char encoded)
	{
		assert (orign != meta) : Assertion.declare();
		assert (encoded != meta) : Assertion.declare();
		assert (orign != encoded) : Assertion.declare();

		return str.replace("" + orign, "" + meta + "" + encoded);
	}

	public String decode(String str, char encoded, char orign)
	{
		if (null == str)
		{
			throw new IllegalArgumentException("str should not be null");
		}
		if (orign == meta)
		{
			throw new IllegalArgumentException(
					"orign should not be equal to meta: " + orign);
		}
		if (encoded == meta)
		{
			throw new IllegalArgumentException(
					"encoded should not be equal to meta: " + encoded);
		}
		if (orign == encoded)
		{
			throw new IllegalArgumentException(
					"orign should not be equal to encoded: " + orign);
		}

		return unhideMeta(unhideSpecified(str, encoded, orign));
	}

	private String unhideSpecified(String str, char encoded, char orign)
	{
		assert (orign != meta) : Assertion.declare();
		assert (encoded != meta) : Assertion.declare();
		assert (orign != encoded) : Assertion.declare();

		StringBuffer buf = new StringBuffer(str);
		int idx = 0;
		while (idx < buf.length())
		{
			if (buf.charAt(idx) != meta)
			{
				idx++;
				continue;
			}
			if (idx + 1 >= buf.length())
			{
				idx++;
				System.out.println("error encoding");
				continue;
			}

			char next = buf.charAt(idx + 1);
			if (next == meta)
			{
				idx += 2;
				continue;
			}
			if (next != encoded)
			{
				System.out.println("error encoding");
				idx += 2;
				continue;
			}

			buf.replace(idx, idx + 2, "" + orign);
			idx++;
		}

		return buf.toString();
	}

	private String unhideMeta(String str)
	{
		StringBuffer buf = new StringBuffer(str);
		int idx = 0;
		while (idx < buf.length())
		{
			if (buf.charAt(idx) != meta)
			{
				idx++;
				continue;
			}
			if (idx + 1 >= buf.length())
			{
				System.out.println("error encoding");
				idx++;
				continue;
			}

			char next = buf.charAt(idx + 1);
			if (next != meta)
			{
				System.out.println("error encoding");
				idx += 2;
				continue;
			}

			buf.replace(idx, idx + 2, "" + meta);
			idx++;
		}

		return buf.toString();
	}

}
