package org.accela.common;

public class Assertion
{
	private static int count = 0;

	/**
	 * 非常郁闷地发现，如果你在交给线程池的任务里断言错误，Java什么都不会做，不会
	 * 输出错误信息，甚至连线程池中的那个线程都不会结束，而是继续运行，若无其事地... 另外，还发现抛出异常也是同样的情况....
	 * 
	 * 为了让上述情况下，发生的断言错误也能够被发现，你可以这样写断言：
	 * 
	 * assert(exp) : Assertion.declare();
	 * 
	 * 这样在发生断也的时候，你会看到错误信息的输出。然后你可以通过给Assertion.declare()
	 * 方法中的语句设置断点，来定位是哪里发生了断言错误。
	 * 
	 * @return 没用，这是为了适应Java断言表达式的格式
	 */
	public static String declare()
	{
		System.err
				.println("****************************************************"
						+ "***************************************************"
						+ "************");

		System.err.println("Asserion Error "
				+ count
				+ " ! Set break point for me "
				+ Assertion.class.getName()
				+ " and see who the hell crushed your work!");
		System.err
				.println("****************************************************"
						+ "***************************************************"
						+ "************");

		count++;
		return "";
	}

	public static int getCount()
	{
		return count;
	}
}
