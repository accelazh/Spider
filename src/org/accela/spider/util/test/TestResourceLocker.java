package org.accela.spider.util.test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.accela.spider.util.ResourceLocker;

import junit.framework.TestCase;

public class TestResourceLocker extends TestCase
{
	public void testReentrantAndLock() throws InterruptedException
	{
		final ResourceLocker<String> lock = new ResourceLocker<String>();

		for (int i = 0; i < 100; i++)
		{
			lock.lock("123");
		}

		final AtomicBoolean failed = new AtomicBoolean(false);
		final AtomicInteger finish = new AtomicInteger(0);
		final AtomicInteger lockCount = new AtomicInteger(0);

		class Locker implements Runnable
		{
			private String value;

			public Locker(String value)
			{
				this.value = value;
			}

			@Override
			public void run()
			{
				lock.lock(value);
				if (lockCount.get() != 0)
				{
					failed.set(true);
					assert (false);
				}
				lockCount.set(1);

				try
				{
					Thread.sleep(10);
				}
				catch (InterruptedException ex)
				{
					ex.printStackTrace();
				}

				lockCount.set(0);
				lock.unlock(value);

				finish.incrementAndGet();
			}
		}

		for (int i = 0; i < 100; i++)
		{
			new Thread(new Locker("123")).start();
		}
		for (int i = 0; i < 100; i++)
		{
			new Thread(new Locker("456")).start();
		}

		Thread.sleep(2000);

		assert (finish.get() == 100) : finish.get();

		lock.unlock("123");

		Thread.sleep(2000);

		assert (finish.get() == 100) : finish.get();

		for (int i = 0; i < 99; i++)
		{
			lock.unlock("123");
		}

		Thread.sleep(2000);

		assert (finish.get() == 200) : finish.get();

		assert (!failed.get());
	}

	public void testTryLock() throws InterruptedException
	{
		final ResourceLocker<String> lock = new ResourceLocker<String>();
		final AtomicBoolean failed = new AtomicBoolean(false);
		final AtomicInteger finish = new AtomicInteger(0);

		for (int i = 0; i < 100; i++)
		{
			boolean ret = lock.tryLock("123");
			assert (ret);
		}

		for (int i = 0; i < 100; i++)
		{
			new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					boolean ret = lock.tryLock("123");
					if (ret)
					{
						failed.set(true);
						assert (false);
					}

					finish.incrementAndGet();
				}

			}).start();
		}

		Thread.sleep(100);
		assert (finish.get() == 100);

		for (int i = 0; i < 100; i++)
		{
			lock.unlock("123");
		}

		lock.lock("456");

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				boolean ret = lock.tryLock("123");
				if (!ret)
				{
					failed.set(true);
					assert (false);
				}

				finish.incrementAndGet();
			}

		}).start();

		Thread.sleep(100);
		
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				boolean ret = lock.tryLock("456");
				if (ret)
				{
					failed.set(true);
					assert (false);
				}

				finish.incrementAndGet();
			}

		}).start();

		Thread.sleep(100);

		assert (!failed.get());
		assert (finish.get() == 102);
	}
	public void testTimeOut() throws InterruptedException
	{
		final ResourceLocker<String> lock = new ResourceLocker<String>();
		final AtomicBoolean failed = new AtomicBoolean(false);
		final AtomicInteger finish = new AtomicInteger(0);

		long startTime = System.currentTimeMillis();
		boolean ret = false;
		try
		{
			ret = lock.tryLock("123", 100, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
			assert (false);
		}
		assert (ret);
		assert (System.currentTimeMillis() - startTime < 10);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				boolean ret = true;
				try
				{
					ret = lock.tryLock("123", 1, TimeUnit.SECONDS);
				}
				catch (InterruptedException ex)
				{
					ex.printStackTrace();
					failed.set(true);
					assert (false);
				}
				if (ret)
				{
					failed.set(true);
				}

				finish.incrementAndGet();
			}
		}).start();
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				boolean ret = true;
				try
				{
					ret = lock.tryLock("123", 1000, TimeUnit.MILLISECONDS);
				}
				catch (InterruptedException ex)
				{
					ex.printStackTrace();
					failed.set(true);
					assert (false);
				}
				if (ret)
				{
					failed.set(true);
				}

				finish.incrementAndGet();
			}
		}).start();
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				boolean ret = true;
				try
				{
					ret = lock.tryLock("123", 1000000, TimeUnit.MICROSECONDS);
				}
				catch (InterruptedException ex)
				{
					ex.printStackTrace();
					failed.set(true);
					assert (false);
				}
				if (ret)
				{
					failed.set(true);
				}

				finish.incrementAndGet();
			}
		}).start();
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				boolean ret = true;
				try
				{
					ret = lock.tryLock("123", 1000000000, TimeUnit.NANOSECONDS);
				}
				catch (InterruptedException ex)
				{
					ex.printStackTrace();
					failed.set(true);
					assert (false);
				}
				if (ret)
				{
					failed.set(true);
				}

				finish.incrementAndGet();
			}
		}).start();

		Thread.sleep(800);
		assert (finish.get() == 0);

		Thread.sleep(300);
		assert (finish.get() == 4);
		assert (!failed.get());
	}

	public void testTimeOutRewait() throws InterruptedException
	{
		final ResourceLocker<String> lock = new ResourceLocker<String>();
		final AtomicBoolean failed = new AtomicBoolean(false);
		final AtomicInteger finish = new AtomicInteger(0);
		final AtomicInteger acquireLockCount=new AtomicInteger(0);
		final AtomicInteger notAcquireLockCount=new AtomicInteger(0);

		long startTime = System.currentTimeMillis();
		boolean ret = false;
		try
		{
			ret = lock.tryLock("123", 100, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
			assert (false);
		}
		assert (ret);
		assert (System.currentTimeMillis() - startTime < 10);

		for (int i = 0; i < 100; i++)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					boolean ret = true;
					try
					{
						ret = lock.tryLock("123", 1, TimeUnit.SECONDS);
					}
					catch (InterruptedException ex)
					{
						ex.printStackTrace();
						failed.set(true);
						assert (false);
					}

					if (ret)
					{
						acquireLockCount.incrementAndGet();
						try
						{
							Thread.sleep(100);
						}
						catch (InterruptedException ex)
						{
							ex.printStackTrace();
						}
						lock.unlock("123");
					}
					else
					{
						notAcquireLockCount.incrementAndGet();
					}

					finish.incrementAndGet();
				}
			}).start();
		}

		Thread.sleep(100);
		lock.unlock("123");

		Thread.sleep(500);
		assert (finish.get() >= 3);
		assert (finish.get() <= 5);

		Thread.sleep(500);
		assert (finish.get() == 100);
		assert(acquireLockCount.get()+notAcquireLockCount.get()==100);
		assert(acquireLockCount.get()>=8);
		assert(acquireLockCount.get()<=10);

		assert (!failed.get());
	}

	public void testLockInterruption() throws InterruptedException
	{
		final ResourceLocker<String> lock = new ResourceLocker<String>();
		final AtomicBoolean failed = new AtomicBoolean(false);
		final AtomicInteger finishCount = new AtomicInteger(0);

		lock.lock("123");

		Thread t1 = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				lock.lock("123");
				failed.set(true);

				finishCount.incrementAndGet();
			}

		});
		Thread t2 = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					lock.lockInterruptibly("123");
					failed.set(true);
				}
				catch (InterruptedException e)
				{

				}

				finishCount.incrementAndGet();
			}

		});
		Thread t3 = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					lock.tryLock("123", 100, TimeUnit.SECONDS);
					failed.set(true);
				}
				catch (InterruptedException e)
				{

				}

				finishCount.incrementAndGet();
			}

		});

		t1.start();
		t2.start();
		t3.start();

		Thread.sleep(100);

		assert (finishCount.get() == 0);

		t1.interrupt();
		t2.interrupt();
		t3.interrupt();

		Thread.sleep(100);

		assert (finishCount.get() == 2);
		assert (!failed.get());
	}

	public void testErrorUnlock() throws InterruptedException
	{
		final ResourceLocker<String> lock = new ResourceLocker<String>();
		final AtomicInteger finish = new AtomicInteger(0);

		lock.lock("123");
		lock.unlock("123");

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 100; i++)
		{
			if (i % 4 == 0)
			{
				lock.lock("123");
			}
			else if (i % 4 == 1)
			{
				lock.lockInterruptibly("123");
			}
			else if (i % 4 == 2)
			{
				boolean ret=lock.tryLock("123");
				assert(ret);
			}
			else
			{
				boolean ret=lock.tryLock("123", 100, TimeUnit.SECONDS);
				assert(ret);
			}
		}
		for (int i = 0; i < 100; i++)
		{
			lock.unlock("123");
		}
		assert (System.currentTimeMillis() - startTime < 10);

		try
		{
			lock.unlock("123");
			assert (false);
		}
		catch (Exception ex)
		{
			assert (ex instanceof IllegalMonitorStateException);
		}
		try
		{
			lock.unlock("456");
			assert (false);
		}
		catch (Exception ex)
		{
			assert (ex instanceof IllegalMonitorStateException);
		}

		// try to unlock other thread's lock
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				lock.lock("456");
				finish.incrementAndGet();
			}
		}).start();

		Thread.sleep(100);
		assert (finish.get() == 1) : finish.get();

		try
		{
			lock.unlock("456");
			assert (false);
		}
		catch (Exception ex)
		{
			assert (ex instanceof IllegalMonitorStateException);
		}

		// try to unlock other thread's lock
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					lock.lockInterruptibly("789");
				}
				catch (InterruptedException ex)
				{
					ex.printStackTrace();
					assert (false);
				}
				finish.incrementAndGet();
			}
		}).start();

		Thread.sleep(100);
		assert (finish.get() == 2) : finish.get();

		try
		{
			lock.unlock("789");
			assert (false);
		}
		catch (Exception ex)
		{
			assert (ex instanceof IllegalMonitorStateException);
		}

		// try to unlock other thread's lock
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				lock.tryLock("101112");
				finish.incrementAndGet();
			}
		}).start();

		Thread.sleep(100);
		assert (finish.get() == 3) : finish.get();

		try
		{
			lock.unlock("101112");
			assert (false);
		}
		catch (Exception ex)
		{
			assert (ex instanceof IllegalMonitorStateException);
		}

		// try to unlock other thread's lock
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					lock.tryLock("131415", 1000, TimeUnit.SECONDS);
				}
				catch (InterruptedException ex)
				{
					ex.printStackTrace();
					assert (false);
				}
				finish.incrementAndGet();
			}
		}).start();

		Thread.sleep(100);
		assert (finish.get() == 4) : finish.get();

		try
		{
			lock.unlock("131415");
			assert (false);
		}
		catch (Exception ex)
		{
			assert (ex instanceof IllegalMonitorStateException);
		}
	}
}
