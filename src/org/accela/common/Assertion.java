package org.accela.common;

public class Assertion
{
	private static int count = 0;

	/**
	 * �ǳ����Ƶط��֣�������ڽ����̳߳ص���������Դ���Javaʲô��������������
	 * ���������Ϣ���������̳߳��е��Ǹ��̶߳�������������Ǽ������У��������µ�... ���⣬�������׳��쳣Ҳ��ͬ�������....
	 * 
	 * Ϊ������������£������Ķ��Դ���Ҳ�ܹ������֣����������д���ԣ�
	 * 
	 * assert(exp) : Assertion.declare();
	 * 
	 * �����ڷ�����Ҳ��ʱ����ῴ��������Ϣ�������Ȼ�������ͨ����Assertion.declare()
	 * �����е�������öϵ㣬����λ�����﷢���˶��Դ���
	 * 
	 * @return û�ã�����Ϊ����ӦJava���Ա��ʽ�ĸ�ʽ
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
