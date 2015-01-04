package org.accela.spider;

public enum SpiderStageName
{
	Constructing, Normalization, BeginningListener, Prefilter, Scheduling,
	Filter, FetchingContent, ExtractingHyperlink, Analyzing, Stamping, Recursion,
	EndingListener, Delivering, All
}
