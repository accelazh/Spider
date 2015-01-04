package org.accela.spider.strategy.impl;

import org.accela.spider.util.URL;

import org.accela.spider.strategy.URLFilter;
import org.accela.spider.util.PeriodicallyClearConcurrentHashMap;

//�ظ���URL����������Ҫ���˵ġ���ʱ���ڣ�����ظ���URL��Ҫ���ˣ����ݿ��У��ո�
//�����¹�URL��һ��ʱ����Ҳ��Ӧ���ٱ��ظ����¡����ǹ����ظ�������������棬ǰ��
//�޶����ظ�ʱ��϶̣�����1���ӣ������޶���ʱ��ϳ�������1�졣ǰ�߽��������ڴ�
//�Ϳ���������������Ҫ���ݿ⡣�������ֻ�жԺ��ߵĹ��ˣ���ô������������⡣��
//һ�֣�ÿ�μ���ظ�����Ҫ�������ݿ⣬�����ϴ󡣵ڶ��֣�������ݿ��л�û��ĳһ
//��URL�ļ�¼����ô���Ծͻ���������URLͨ��������Spider���������ݣ�Ȼ�������ݿ���
//�������¼��������������ݿ��з����Ӧ��¼֮ǰ������URL�����ظ����������ط���
//��spider����ôÿһ��URL����ͨ�����ԣ�spider�ͻ�ΪÿһURL�����ظ������ء�������
//���ⲻ����Ƶ���վ�Ϳ����ƻ�spider����һ�ֹ��˵����趨���޶�ʱ�䣬����1���ӣ�
//���ʱ��������URL�����ؽ���������ݿ⣬�Ӷ��������������⡣���ǵ�һ�ֹ��˵��޶�
//ʱ��Ҳ��Ӧ��̫�������Ҳ�Ӧ�ó����ڶ��ֹ��˵��޶�ʱ�䡣���򣬱���6:00���µ���ҳ��
//����24Сʱ��ڶ���6:00Ӧ���ٸ��£��������5:00һ�����������ҳURL�����͸�
//spider����Ȼ������󲻻�ͨ�������ǽ�������������5:00ǰ�������ҳ�����ܱ������ˡ�
//
//�����ĵ�һ�ֹ��˹�����RepeatedURLFilterʵ�֣�����PrefilterStage�У��ڶ��ֹ���
//������DateURLFilterʵ�֣�����FilterStage�С�
public class RepeatedURLFilter implements URLFilter
{
	private PeriodicallyClearConcurrentHashMap<URL, URL> records=null;
	
	public RepeatedURLFilter(long repetitionInterval)
	{
		if(repetitionInterval<0)
		{
			throw new IllegalArgumentException("repetitionInterval should not be negative");
		}
		
		this.records=new PeriodicallyClearConcurrentHashMap<URL, URL>(repetitionInterval);
	}
	
	@Override
	public boolean accept(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}
		
		if(records.containsKey(url))
		{
			return false;
		}
		else
		{
			records.put(url, url);
			return true;
		}
	}

	public long getRepetitionInterval()
	{
		return records.getPeriod();
	}

}
