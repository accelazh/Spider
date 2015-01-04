package org.accela.spider.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

//identical with concurrent hash map, except that periodically invoke clear() method
public class PeriodicallyClearConcurrentHashMap<K, V> implements
		ConcurrentMap<K, V>
{
	private ConcurrentHashMap<K, V> map = null;

	private long nanoPeriod = 0;

	private AtomicLong last = null;

	private static final long ONE_MILLION = 1000000;

	public PeriodicallyClearConcurrentHashMap(long period)
	{
		if (period < 0)
		{
			throw new IllegalArgumentException("period should not be negative");
		}

		this.map = new ConcurrentHashMap<K, V>();
		this.nanoPeriod = period * ONE_MILLION;
		this.last = new AtomicLong(System.nanoTime()); // 内部实现使用nano时间，提高精度至1ms也能够分辨
	}

	private void tryPeriodicallyClear()
	{
		if (System.nanoTime() - last.get() >= nanoPeriod)
		{
			//use double-checked lock to ensure synchronization
			synchronized (this)
			{
				long current = System.nanoTime();
				if (current - last.get() >= nanoPeriod)
				{
					this.clear();
					last.set(current);
				}
			}
		}
	}

	public void clear()
	{
		map.clear();
	}

	public boolean contains(Object value)
	{
		tryPeriodicallyClear();

		return map.contains(value);
	}

	public boolean containsKey(Object key)
	{
		tryPeriodicallyClear();

		return map.containsKey(key);
	}

	public boolean containsValue(Object value)
	{
		tryPeriodicallyClear();

		return map.containsValue(value);
	}

	public Enumeration<V> elements()
	{
		tryPeriodicallyClear();

		return map.elements();
	}

	public Set<java.util.Map.Entry<K, V>> entrySet()
	{
		tryPeriodicallyClear();

		return map.entrySet();
	}

	public boolean equals(Object o)
	{
		tryPeriodicallyClear();

		return map.equals(o);
	}

	public V get(Object key)
	{
		tryPeriodicallyClear();

		return map.get(key);
	}

	public int hashCode()
	{
		tryPeriodicallyClear();

		return map.hashCode();
	}

	public boolean isEmpty()
	{
		tryPeriodicallyClear();

		return map.isEmpty();
	}

	public Enumeration<K> keys()
	{
		tryPeriodicallyClear();

		return map.keys();
	}

	public Set<K> keySet()
	{
		tryPeriodicallyClear();

		return map.keySet();
	}

	public V put(K key, V value)
	{
		tryPeriodicallyClear();

		return map.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> m)
	{
		tryPeriodicallyClear();

		map.putAll(m);
	}

	public V putIfAbsent(K key, V value)
	{
		tryPeriodicallyClear();

		return map.putIfAbsent(key, value);
	}

	public boolean remove(Object key, Object value)
	{
		tryPeriodicallyClear();

		return map.remove(key, value);
	}

	public V remove(Object key)
	{
		tryPeriodicallyClear();

		return map.remove(key);
	}

	public boolean replace(K key, V oldValue, V newValue)
	{
		tryPeriodicallyClear();

		return map.replace(key, oldValue, newValue);
	}

	public V replace(K key, V value)
	{
		tryPeriodicallyClear();

		return map.replace(key, value);
	}

	public int size()
	{
		tryPeriodicallyClear();

		return map.size();
	}

	public String toString()
	{
		tryPeriodicallyClear();

		return map.toString();
	}

	public Collection<V> values()
	{
		tryPeriodicallyClear();

		return map.values();
	}

	public long getPeriod()
	{
		return nanoPeriod / ONE_MILLION;
	}

	public long getLastClearTime()
	{
		return last.get() / ONE_MILLION;
	}

	public long getNextClearTime()
	{
		return (last.get() + nanoPeriod) / ONE_MILLION;
	}

	public long getTimeToNextClear()
	{
		return Math.max(0, (nanoPeriod - (System.nanoTime() - last.get()))
				/ ONE_MILLION);
	}

	public static void main(String[] args)
	{
		PeriodicallyClearConcurrentHashMap<Integer, String> m = new PeriodicallyClearConcurrentHashMap<Integer, String>(
				0);

		long startTime = System.nanoTime();
		for (int i = 0; i < 100; i++)
		{
			m.put((int) (Math.random() * Integer.MAX_VALUE), "Hello World");
		}
		System.out.println(System.nanoTime() - startTime);

		startTime = System.nanoTime();
		for (int i = 0; i < 100; i++)
		{
			m.get((int) (Math.random() * Integer.MAX_VALUE));
		}
		System.out.println(System.nanoTime() - startTime);
	}

}
