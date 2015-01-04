package org.accela.spider.strategy;

import org.accela.spider.util.URL;

//further URL extraction may require to extract URL, anchor text, font, 
//capitalization information and so on. URL extraction result varies so 
//that an interface is needed
public interface Hyperlink
{
	public URL getURL();
}
