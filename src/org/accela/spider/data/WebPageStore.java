package org.accela.spider.data;

import org.accela.spider.util.URL;

//Note that WebPageStore must be THREAD-SAFE!
public interface WebPageStore
{
	public void put(WebPage page) throws WebPageStoreException;

	//����Ҳ���urlָ�������򷵻�-1
	public long getStamp(URL url);

	public boolean contains(URL url);
}
