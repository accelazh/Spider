package org.accela.spider.util.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.accela.spider.util.SpecificCharEncoder;

import junit.framework.TestCase;

public class TestSpecificCharEncoder extends TestCase
{
	public void testSimple()
	{
		SpecificCharEncoder encoder = new SpecificCharEncoder();
		encoder.encode("today is nice", ' ', 'w').equals("today\\wis\\wnice");
		encoder.encode("today\nis\nfine", '\n', 'n')
				.equals("today\\nis\\nfine");
		encoder.encode("\\today\nis\\\nfi\\\\nne\\", '\n', 'n')
				.equals("\\\\today\\nis\\\\nfi\\\\\\\\ne\\\\");
	}

	public void testRandom()
	{
		for (int i = 0; i < 500; i++)
		{
			randomTest();
		}
	}

	private void randomTest()
	{
		SpecificCharEncoder encoder = new SpecificCharEncoder();
		Random rand = new Random();

		int iteration = rand.nextInt(10) + 1;

		// generate characters
		List<Character> origns = new ArrayList<Character>();
		List<Character> encodeds = new ArrayList<Character>();

		double select=rand.nextDouble();
		if(select<0.33)
		{
			generateCharsRandom(iteration, origns, encodeds, encoder.getMeta());
			//System.out.println("random");
		}
		else if(select<0.66)
		{
			generateCharsSwap(iteration, origns, encodeds, encoder.getMeta());
			//System.out.println("swap");
		}
		else
		{
			generateCharsCircular(iteration, origns, encodeds, encoder.getMeta());
			//System.out.println("circular");
		}
		//System.out.println("origns: " + origns);
		//System.out.println("encodeds: " + encodeds);

		// generate string
		StringBuffer strBuf = new StringBuffer();
		String str = null;
		int strLength = rand.nextInt(100) + 1;

		for (int i = 0; i < strLength; i++)
		{
			select = rand.nextDouble();
			char c = 0;

			if (select < 0.2)
			{
				c = origns.get(rand.nextInt(origns.size()));
			}
			else if (select < 0.4)
			{
				c = encodeds.get(rand.nextInt(encodeds.size()));
			}
			else if (select < 0.6)
			{
				c = encoder.getMeta();
			}
			else
			{
				c = (char) rand.nextInt(128);
			}

			strBuf.append(c);
		}
		str = strBuf.toString();
		// System.out.println("string: " + str);

		// encoding
		String encoded = str;
		for (int i = 0; i < iteration; i++)
		{
			encoded = encoder.encode(encoded, origns.get(i), encodeds.get(i));

			for (int j = 0; j < encoded.length(); j++)
			{
				assert (encoded.charAt(j) != origns.get(i));
			}
		}
		// System.out.println("encoding: " + encoded);

		// decoding
		String decoded = encoded;
		for (int i = iteration - 1; i >= 0; i--)
		{
			decoded = encoder.decode(decoded, encodeds.get(i), origns.get(i));
		}
		// System.out.println("decoded: " + decoded);

		// test
		assert (decoded.equals(str));
	}

	private void generateCharsRandom(int iteration,
			List<Character> origns,
			List<Character> encodeds,
			char meta)
	{
		Random rand = new Random();
		for (int i = 0; i < iteration; i++)
		{
			char orign = 0;
			do
			{
				orign = (char) rand.nextInt(128);
			}
			while (orign == meta);

			char encoded = 0;
			do
			{
				encoded = (char) rand.nextInt(128);
			}
			while (encoded == orign || encoded == meta);

			origns.add(orign);
			encodeds.add(encoded);
		}
	}

	private void generateCharsSwap(int iteration,
			List<Character> origns,
			List<Character> encodeds,
			char meta)
	{
		Random rand = new Random();

		char orign = 0;
		do
		{
			orign = (char) rand.nextInt(128);
		}
		while (orign == meta);

		char encoded = 0;
		do
		{
			encoded = (char) rand.nextInt(128);
		}
		while (encoded == orign || encoded == meta);

		origns.add(orign);
		encodeds.add(encoded);

		for (int i = 0; i < iteration - 1; i++)
		{
			orign = encodeds.get(encodeds.size() - 1);
			encoded = origns.get(origns.size() - 1);

			origns.add(orign);
			encodeds.add(encoded);
		}
	}

	private void generateCharsCircular(int iteration,
			List<Character> origns,
			List<Character> encodeds,
			char meta)
	{
		Random rand = new Random();

		char orign = 0;
		do
		{
			orign = (char) rand.nextInt(128);
		}
		while (orign == meta);

		char encoded = 0;
		do
		{
			encoded = (char) rand.nextInt(128);
		}
		while (encoded == orign || encoded == meta);

		origns.add(orign);
		encodeds.add(encoded);
		
		for (int i = 0; i < iteration - 1; i++)
		{
			orign = encodeds.get(encodeds.size() - 1);
			
			encoded = 0;
			do
			{
				encoded = (char) rand.nextInt(128);
			}
			while (encoded == orign || encoded == meta);

			origns.add(orign);
			encodeds.add(encoded);
		}

	}

}
