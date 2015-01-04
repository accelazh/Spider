package org.accela.spider.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.accela.common.Assertion;

public class ResourceLocker<T>
{
	private Map<T, Record> lockMap = new HashMap<T, Record>();

	private ReentrantLock lock = new ReentrantLock();

	private Condition condition = lock.newCondition();

	public ResourceLocker()
	{
		// do nothing
	}

	public void lock(T resource)
	{
		if (null == resource)
		{
			throw new IllegalArgumentException("resource should not be null");
		}

		lock.lock();
		try
		{
			while (!tryLock(resource))
			{
				condition.awaitUninterruptibly();
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	public void lockInterruptibly(T resource) throws InterruptedException
	{
		if (null == resource)
		{
			throw new IllegalArgumentException("resource should not be null");
		}

		lock.lock();
		try
		{
			while (!tryLock(resource))
			{
				condition.await();
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	public boolean tryLock(T resource, long timeout, TimeUnit unit)
			throws InterruptedException
	{
		if (null == resource)
		{
			throw new IllegalArgumentException("resource should not be null");
		}
		if (null == unit)
		{
			throw new IllegalArgumentException("unit should not be null");
		}

		lock.lock();
		try
		{
			long duration = 0;
			long nanoTimeOut = unit.toNanos(timeout);
			while (!tryLock(resource))
			{
				long startTime = System.nanoTime();
				boolean ret = condition.await(Math.max(0,
						(nanoTimeOut - duration)), TimeUnit.NANOSECONDS);
				duration += System.nanoTime() - startTime;
				if (!ret)
				{
					return false;
				}
			}
			
			return true;
		}
		finally
		{
			lock.unlock();
		}
	}

	public boolean tryLock(T resource)
	{
		lock.lock();
		try
		{
			Record holder = lockMap.get(resource);
			if (null == holder)
			{
				// pass
				lockMap.put(resource, holder = new Record(Thread
						.currentThread()));
				holder.increment();

				return true;
			}
			else if (holder.getThread().equals(Thread.currentThread()))
			{
				// pass
				assert (holder.getCount() >= 1) : Assertion.declare();
				holder.increment();

				return true;
			}
			else
			{
				return false;
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	public void unlock(T resource)
	{
		lock.lock();
		try
		{
			Record holder = lockMap.get(resource);
			if (null == holder)
			{
				throw new IllegalMonitorStateException("unlock before lock");
			}
			else if (!holder.getThread().equals(Thread.currentThread()))
			{
				throw new IllegalMonitorStateException(
						"unlock another thread's lock");
			}
			else
			{
				assert (holder.getCount() >= 1):Assertion.declare();
				holder.decrement();

				if (holder.getCount() <= 0)
				{
					lockMap.remove(resource);
					condition.signalAll();
				}
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	private static class Record
	{
		private Thread thread = null;
		private int count = 0;

		public Record(Thread thread)
		{
			if (null == thread)
			{
				throw new IllegalArgumentException("thread should not be null");
			}

			this.thread = thread;
			this.count = 0;
		}

		public Thread getThread()
		{
			return this.thread;
		}

		public int getCount()
		{
			return this.count;
		}

		public void increment()
		{
			count++;
		}

		public void decrement()
		{
			if (count <= 0)
			{
				throw new IllegalStateException("count already <=0: " + count);
			}
			count--;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (null == obj || !(obj instanceof Record))
			{
				return false;
			}

			Record other = (Record) obj;
			return this.thread.equals(other.thread)
					&& this.count == other.count;
		}

		@Override
		public int hashCode()
		{
			return thread.hashCode() + 31 * count;
		}

	}
}
