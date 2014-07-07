/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.contacts;

import com.zhuang.quickcall.LogLevel;

/**
 * ContactBinding
 * @author gavin.zhuang 
 */
public class ContactBinding {
	
    public String originalNumber;
    public String displayName;
    public String phoneNumber;
    public String phoneNumberTag;

    public boolean isValid;
    public boolean hasContact;
    public boolean isPersonalContact;

    public long phoneId;
    public long photoId;
    public long contact_id;
    
    public String toString() {
        try {
            if (LogLevel.DEV) {
                StringBuffer sb = new StringBuffer("ContactBinding(" + originalNumber + ") ");

                if (isValid) {
                    if (hasContact) {
                        sb.append("; number=");
                        sb.append(originalNumber);
                        sb.append("company=");
                        sb.append(!isPersonalContact);
                        sb.append("; bindId=");
                        sb.append(phoneId);
                        sb.append("; bindName=");
                        sb.append(displayName);
                        sb.append("; bindNumber=");
                        sb.append(phoneNumber);
                        sb.append(";");
                    } else {
                        sb.append("no contacts");
                    }
                } else {
                    sb.append("not valid");
                }
                return sb.toString();
            }
        } catch (Exception ex) {
        }
        return originalNumber;
    }
}
