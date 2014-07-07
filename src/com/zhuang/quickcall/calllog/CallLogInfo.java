package com.zhuang.quickcall.calllog;

public class CallLogInfo {

	public long _id;
	public String name;
	public String number_label;
	public String number_type;
	public String number;
	
	public int type;
	public long duration;
	public long date;
	
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("_id = ").append(_id)
		.append(", name = ").append(name)
		.append(", number_label = ").append(number_label)
		.append(", number_type = ").append(number_type)
		.append(", number = ").append(number)
		.append(", type = ").append(type)
		.append(", duration = ").append(duration)
		.append(", date = ").append(date);
		
		return buffer.toString();
	}
}
