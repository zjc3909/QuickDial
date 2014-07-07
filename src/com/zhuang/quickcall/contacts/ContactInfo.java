/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.contacts;

/**
 * ContactInfo
 * @author gavin.zhuang 
 */
public class ContactInfo {

	public String displayName;
	public String companyName;
	public String jobTitle;
	public String department;
	public String firstName;
	public String middleName;
	public String lastName;
	public String suffix;
	public String lookupKey;

	public long contactId;
	public long photoId;
	public int hasPhone;
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("displayName=").append(displayName)
		.append(", companyName=").append(companyName)
		.append(", jobTitle=").append(jobTitle)
		.append(", department=").append(department)
		.append(", lookupKey=").append(lookupKey)
		.append(", contactId=").append(contactId)
		.append(", hasPhone=").append(hasPhone);
		
		return builder.toString();
	}
}
