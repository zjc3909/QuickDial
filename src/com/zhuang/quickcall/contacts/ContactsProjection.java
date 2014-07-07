/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.contacts;

import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class ContactsProjection {

	private static final String CONTACTS_SORT_KEY = "sort_key";
	private static final String CONTACTS_DISPLAY_NAME = "display_name";

	public static final class PersonalContacts {
		private PersonalContacts() {
		};
		
		public static final String[] PERSONAL_CONTACTS_SUMMARY_PROJECTION = new String[] {
				ContactsContract.Contacts._ID, // 0
				ContactsContract.Contacts.DISPLAY_NAME, // 1
				ContactsContract.Contacts.STARRED, // 2
				ContactsContract.Contacts.HAS_PHONE_NUMBER, // 3
				ContactsContract.Contacts.PHOTO_ID, // 4
				ContactsContract.Contacts.LOOKUP_KEY, // 5
				CONTACTS_SORT_KEY
		};

		public static final int ID_COLUMN_INDEX = 0;
		public static final int DISPLAY_NAME_COLUMN_INDEX = 1;
		public static final int STARRED_COLUMN_INDEX = 2;
		public static final int HAS_PHONE_NUMBER_COLUMN_INDEX = 3;
		public static final int PHOTO_ID_COLUMN_INDEX = 4;
		public static final int LOOKUP_KEY_COLUMN_INDEX = 5;
		public static final int SORT_KEY_INDEX = 6;
	}
	
	public static final String[] CONTACTS_PROJECTION = new String[]{
		ContactsContract.CommonDataKinds.Phone._ID,			// 0
		ContactsContract.CommonDataKinds.Phone.CONTACT_ID,			// 0
		ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,		// 1
		ContactsContract.CommonDataKinds.Phone.STARRED,				// 2
		ContactsContract.CommonDataKinds.Phone.PHOTO_ID,			// 3
		ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,			// 4
	};
	
	public static final int CONTACTS_ID_INDEX = 0;
	public static final int CONTACTS_CONTACTS_ID_INDEX = 1;
	public static final int CONTACTS_DISPLAY_NAME_INDEX = 2;
	public static final int CONTACTS_STARRED_INDEX = 3;
	public static final int CONTACTS_PHOTO_ID_INDEX = 4;
	public static final int CONTACTS_LOOKUP_KEY_INDEX = 5;
	
	public static final class PhoneContacts {
		private PhoneContacts() {
		};

		static final String[] PHONE_CONTACTS_SUMMARY_PROJECTION = new String[] {
			ContactsContract.CommonDataKinds.Phone.CONTACT_ID,			// 0
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,		// 1
			ContactsContract.CommonDataKinds.Phone.STARRED,				// 2
			ContactsContract.CommonDataKinds.Phone.PHOTO_ID,			// 3
			ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,			// 4
			ContactsContract.CommonDataKinds.Phone._ID					// 5
		};

		static final int CONTACT_ID_COLUMN_INDEX 	= 0;
		static final int DISPLAY_NAME_COLUMN_INDEX 	= 1;
		static final int STARRED_COLUMN_INDEX 		= 2;
		static final int PHOTO_ID_COLUMN_INDEX 		= 3;
		static final int LOOKUP_KEY_COLUMN_INDEX 	= 4;
		static final int PHONE_ID_COLUMN_INDEX 		= 5;
	}
	
	public static final String[] MATRIX_COLUMN_NAMES = new String[] {
		ContactsContract.Contacts._ID,				// 0
		ContactsContract.Contacts.DISPLAY_NAME,		// 1
		ContactsContract.Contacts.STARRED,			// 2
		ContactsContract.Contacts.HAS_PHONE_NUMBER,	// 3
		ContactsContract.Contacts.PHOTO_ID,			// 4
		ContactsContract.Contacts.LOOKUP_KEY,		// 5
		ContactsContract.CommonDataKinds.Phone._ID	// 6
	};
	
	// CONTACT PHONE
	public static final String[] CONTACTS_PHONE_PROJECTION = new String[]{
		ContactsContract.CommonDataKinds.Phone._ID,
		ContactsContract.CommonDataKinds.Phone.NUMBER,
		ContactsContract.CommonDataKinds.Phone.TYPE,       
		ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,	
		ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
		CONTACTS_SORT_KEY
		
	};
	
	public static final int CONTACTS_PHONE_ID_INDEX = 0;
	public static final int CONTACTS_PHONE_NUMBER_INDEX = 1;
	public static final int CONTACTS_PHONE_TYPE_INDEX = 2;
	public static final int CONTACTS_PHONE_DISPLAY_NAME_INDEX = 3;
	public static final int CONTACTS_PHONE_PHOTO_ID_INDEX = 4;
	public static final int CONTACTS_PHONE_SORT_KEY_INDEX = 5;
	
	public static final String CONTACT_PHONE_QUERY_SELECTION = Phone.DISPLAY_NAME + " LIKE ? || '%'" + " OR " +
    		"REPLACE(REPLACE(REPLACE(" + Phone.NUMBER + ", \"-\", \"\"), \" \", \"\"), \"+\", \"\")" + " LIKE ? || '%'" + " OR " +
    		"REPLACE(REPLACE(REPLACE(" + Phone.NUMBER + ", \"-\", \"\"), \" \", \"\"), \"+\", \"\")" + " LIKE ? || '%'" + " OR " +
    		Phone.NUMBER + " LIKE ? || '%'" + " OR " +
			Phone.NUMBER + " LIKE ? || '%'";
	
	// CONTACT EMAIL
	public static final String[] CONTACTS_EMAIL_PROJECTION = new String[]{
		ContactsContract.CommonDataKinds.Email._ID,
		ContactsContract.CommonDataKinds.Email.DATA,
		ContactsContract.CommonDataKinds.Email.TYPE, 
		CONTACTS_DISPLAY_NAME,
		ContactsContract.CommonDataKinds.Email.PHOTO_ID,
		ContactsContract.CommonDataKinds.Email.LABEL,
		CONTACTS_SORT_KEY
		
	};
	
	public static final int CONTACTS_EMAIL_ID_INDEX = 0;
	public static final int CONTACTS_EMAIL_ADDRESS_INDEX = 1;
	public static final int CONTACTS_EMAIL_TYPE_INDEX = 2;
	public static final int CONTACTS_EMAIL_DISPLAY_NAME_INDEX = 3;
	public static final int CONTACTS_EMAIL_PHOTO_ID_INDEX = 4;
	public static final int CONTACTS_EMAIL_LABEL_INDEX = 5;
	public static final int CONTACTS_EMAIL_SORT_KEY_INDEX = 6;
	
	public static final String CONTACT_EMAIL_QUERY_SELECTION = Email.DISPLAY_NAME + " LIKE ? || '%'" + " OR " +
    		Email.DATA + " LIKE ? || '%'" + " OR " +
    		Email.DATA + " LIKE ? || '%'";
}
