package com.zhuang.quickcall.quickcall;

public class QuickCallInfo {

	public long _id;
	public String name;
	public String number;
	public long photoId;
	public String trace;
	
	public long createTime;

	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("_id = ").append(_id)
		.append(", name = ").append(name)
		.append(", number = ").append(number)
		.append(", photoId = ").append(photoId)
		.append(", trace = ").append(trace);
		
		return buffer.toString();
	}
}
