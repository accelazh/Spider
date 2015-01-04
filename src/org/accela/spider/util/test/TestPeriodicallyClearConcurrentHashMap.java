package org.accela.spider.util.test;

import org.accela.spider.util.PeriodicallyClearConcurrentHashMap;

import junit.framework.TestCase;

public class TestPeriodicallyClearConcurrentHashMap extends TestCase
{
	public void testClear()
	{
		clearTest(1000);
		clearTest(100);
		clearTest(10);
		clearTest(1);
		clearTest(0);
	}

	private void clearTest(long interval)
	{
		PeriodicallyClearConcurrentHashMap<String, String> map = new PeriodicallyClearConcurrentHashMap<String, String>(
				interval);

		for (int i = 0; i < 10; i++)
		{
			map.put("hello", "world");
			if (interval != 0)
			{
				assert (map.get("hello") != null) : "interval: "
						+ interval
						+ ", i: "
						+ i;
				assert (map.get("hello").equals("world")) : "interval: "
						+ interval
						+ ", i: "
						+ i;
			}
			else
			{
				assert (map.get("hello") == null) : "interval: "
						+ interval
						+ ", i: "
						+ i;
				assert (map.size() == 0) : "interval: "
						+ interval
						+ ", i: "
						+ i;
			}

			try
			{
				Thread.sleep(interval + ((interval == 0) ? 0 : 1));
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
				assert (false);
			}

			assert (map.get("hello") == null) : "interval: "
					+ interval
					+ ", i: "
					+ i;
			assert (map.size() == 0) : "interval: " + interval + ", i: " + i;
		}

	}

}
