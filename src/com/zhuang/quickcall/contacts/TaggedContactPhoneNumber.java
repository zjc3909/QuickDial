/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.contacts;

public class TaggedContactPhoneNumber {

	public long id;

    public String displayName;
    public String originalNumber;
    public String numberTag;

    /**
     * Defines if the contact number is primary / default.
     */
    public boolean isDefault;
    
    public long contact_id;
    public String lookup_key;
    public long phone_id;
    public long photo_id;
    
}
