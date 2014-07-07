/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.contacts;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.R;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhuang.quickcall.utils.NumberHelper;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

public class ContactsUtils {
	
	private static String TAG = "[ZHUANG]ContactsUtils";
	
	public static Uri Uri_People = Uri.parse("content://com.android.contacts/contacts");	
	
	public static final String PERSONAL_QUERY_SELECTION =
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME   + " LIKE ? || '%'" + " OR " + 
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME   + " LIKE '%' || ' ' || ? || '%'" + " OR " + 
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME   + " LIKE '+' || ? || '%'" + " OR " + 
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME   + " LIKE '(' || ? || '%'";
	
	public static Uri getPeopleUri() {
        return ContactsContract.Contacts.CONTENT_URI;
    }
	
	public static Uri getContactPhoneUri(){
		return ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	}
	
	public static Uri getContactEmailUri(){
		return ContactsContract.CommonDataKinds.Email.CONTENT_URI;
	}
	
	public static Intent getCreateNewContactIntent(Context context, String contactName, String contactNumber) {
        Intent i = new Intent(Intent.ACTION_INSERT);
        i.setType(ContactsContract.Contacts.CONTENT_TYPE);
        i.putExtra(ContactsContract.Intents.Insert.NAME, contactName);
        i.putExtra(ContactsContract.Intents.Insert.PHONE, contactNumber);
        return i;
    }
	
	public static Intent getAddToContactIntent(Context context, String contactNumber) {
        Intent i = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        i.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        i.putExtra(ContactsContract.Intents.Insert.PHONE, contactNumber);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        return i;
    }
	
	/**
     * Bind synchronization.
     */
    public static class BindSync {
        public static enum State {
            INVALID_RECORD, NOT_CHANGED, UPDATED, BIND, REBIND, GONE
        }

        public State syncState = State.INVALID_RECORD;
        public ContactBinding bind;
    }
	
	public static long lookupContactIDByPhoneNumber(String phoneNumber, Context context) {
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));
            Cursor cursor = context.getContentResolver().query(uri, PROJECTION1, null, null, null);
            if (cursor != null) {
                long id = -1;
                if (cursor.moveToFirst()) {
                    id = cursor.getLong(CONTACT_ID_INDX1);
                }
                cursor.close();
                return id;
            }
        } catch (Exception ex) {
            if (LogLevel.MARKET) {
                MarketLog.e(TAG, "Look-up contact by phone number failed: " + ex.getMessage());
            }
        }

        return -1;
    }

    private static final String[] PROJECTION1 = { ContactsContract.CommonDataKinds.Phone.CONTACT_ID };
    private static final int CONTACT_ID_INDX1 = 0;

    
    public static Bitmap getContactPhoto(Context context, long photoId, BitmapFactory.Options options) {
        if (photoId < 0) {
            if (LogLevel.DEV) {
                DevLog.w(TAG, "Photo retrieving failed. Id is invalid.");
            }
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId),
                    new String[] { Photo.PHOTO }, null, null, null);

            if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                byte[] photoData = cursor.getBlob(0);
                // Workaround for Android Issue 8488 http://code.google.com/p/android/issues/detail?id=8488
                if (options == null) {
                    options = new BitmapFactory.Options();
                }
                options.inTempStorage = new byte[16 * 1024];
                options.inSampleSize = 2;
                return BitmapFactory.decodeByteArray(photoData, 0, photoData.length, options);
            }
        } catch (java.lang.Throwable error) {
            if (LogLevel.MARKET) {
                MarketLog.w(TAG, "Photo retrieving failed (id = " + photoId + "): " + error.getMessage());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
    
    public static Bitmap getContactInfoPhotoByContactId(Context context, long contactId) {
        if (contactId < 0) {
            if (LogLevel.DEV) {
                DevLog.w(TAG, "getContactInfoPhotoByContactId failed, contactId is invalid.");
            }
            return null;
        }

        try{
        	Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);  
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);  
            return BitmapFactory.decodeStream(input); 
        } catch (java.lang.Throwable error) {
            if (LogLevel.MARKET) {
                MarketLog.w(TAG, "getContactInfoPhotoByContactId failed (contactId = " + contactId + "): " + error.getMessage());
            }
        }
        
        return null;
        
    }
    public static Bitmap getContactInfoPhoto(Context context, long photoId) {
        if (photoId < 0) {
            if (LogLevel.DEV) {
                DevLog.w(TAG, "Photo retrieving failed. Id is invalid.");
            }
            return null;
        }

        Cursor cursor = null;
        BitmapFactory.Options options = null;
        try {
            cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId),
                    new String[] { Photo.PHOTO }, null, null, null);

            if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                byte[] photoData = cursor.getBlob(0);
                // Workaround for Android Issue 8488 http://code.google.com/p/android/issues/detail?id=8488
                if (options == null) {
                    options = new BitmapFactory.Options();
                }
//                options.inTempStorage = new byte[1024 * 1024];
//                options.inSampleSize = 1;
                return BitmapFactory.decodeByteArray(photoData, 0, photoData.length, null);
            }
        } catch (java.lang.Throwable error) {
            if (LogLevel.MARKET) {
                MarketLog.w(TAG, "Photo retrieving failed (id = " + photoId + "): " + error.getMessage());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
    
    public static String formatAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}
    
    public static BindSync syncBind(Context context, ContactBinding bind) {
        BindSync sync = new BindSync();
        sync.bind = bind;
        if (!bind.isValid) {
            sync.syncState = BindSync.State.INVALID_RECORD;
            return sync;
        }
        ContactBinding cleanBind = new ContactBinding();
        cleanBind.hasContact = false;
        cleanBind.phoneId = -1;
        cleanBind.isValid = bind.isValid;
        cleanBind.originalNumber = bind.originalNumber;
        cleanBind.photoId = -1;
        cleanBind.contact_id = -1;

        if (bind.hasContact) {
            if (bind.isPersonalContact) {
                return syncPersonalBind(context, sync);
            } 
            
            ContactBinding newBind = bindContactByNumber(context, bind.originalNumber);
            if (newBind.isValid) {
                if (newBind.hasContact) {
                    sync.bind = newBind;
                    sync.syncState = BindSync.State.REBIND;
                    return sync;
                }
            }

            sync.syncState = BindSync.State.GONE;
            sync.bind = cleanBind;
            return sync;
        }

        /**
         * Has not contact.
         */
        ContactBinding newBind = bindContactByNumber(context, bind.originalNumber);
        if (newBind.isValid) {
            if (newBind.hasContact) {
                sync.bind = newBind;
                sync.syncState = BindSync.State.BIND;
                return sync;
            }
        }

        sync.syncState = BindSync.State.NOT_CHANGED;
        return sync;
    }
    
    public static BindSync syncPersonalBind(Context context, BindSync bindSync) {
        Cursor cursor = null;
        try {
            Uri phoneUri = ContentUris.withAppendedId(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, bindSync.bind.phoneId);
            cursor = context.getContentResolver().query(phoneUri, PROJECTION8, null, null, null);

            boolean found = false;
            String displayName = null;
            String phoneNumber = null;
            String phoneNumberTag = null;
            long photoId = -1;
            
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    displayName = cursor.getString(DISPLAY_NAME_INDX8);
                    phoneNumber = cursor.getString(NUMBER_ID_INDX8);
                    int type = cursor.getInt(TYPE_INDX8);
                    photoId = cursor.getLong(PHOTO_ID_INDX8);
                    phoneNumberTag = getPhoneNumberTag(context, type);
                    if (isCustomTagPhoneNumber(context, type)) {
                        String label = cursor.getString(LABEL_INDX8);
                        if (label != null) {
                        	phoneNumberTag = label;
                        }
                    }
                    bindSync.bind.contact_id = cursor.getLong(CONTACT_ID_INDX8);
                    found = true;
                }
            }
            if (found) {
                boolean success = true;
                if (!compareStrings(phoneNumber, bindSync.bind.phoneNumber)) {
                	success = false;
                }
                
                if (success) {
                    if (!compareStrings(displayName, bindSync.bind.displayName)) {
                        bindSync.bind.displayName = displayName;
                        bindSync.syncState = BindSync.State.UPDATED;
                        success = false;
                    } 
                    if (!compareStrings(phoneNumberTag, bindSync.bind.phoneNumberTag)) {
                        bindSync.bind.phoneNumberTag = phoneNumberTag;
                        bindSync.syncState = BindSync.State.UPDATED;
                        success = false;
                    }
                    
                    if (photoId != bindSync.bind.photoId){
                    	bindSync.bind.photoId = photoId;
                    	 bindSync.syncState = BindSync.State.UPDATED;
                         success = false;
                    }
                    
                    if(success){
                    	bindSync.syncState = BindSync.State.NOT_CHANGED;
                    	return bindSync;
                    }
                }
                
            }
            
        } catch (java.lang.Throwable error) {
            if (LogLevel.MARKET) {
                MarketLog.e(TAG, "SyncBind: " + error.getMessage());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        ContactBinding newBind = bindContactByNumber(context, bindSync.bind.originalNumber);
        if (newBind.isValid) {
            if (newBind.hasContact) {
                bindSync.bind = newBind;
                bindSync.syncState = BindSync.State.REBIND;
                return bindSync;
            }
        }

        ContactBinding cleanBind = new ContactBinding();
        cleanBind.hasContact = false;
        cleanBind.phoneId = -1;
        cleanBind.isValid = bindSync.bind.isValid;
        cleanBind.originalNumber = bindSync.bind.originalNumber;
        
        bindSync.syncState = BindSync.State.GONE;
        bindSync.bind = cleanBind;
        return bindSync;
    }
    
    public static final ContactBinding bindContactByNumber(Context context, String number) {
    	
        ContactBinding bind = new ContactBinding();
        bind.originalNumber = number;
        bind.isValid = true;
        bind.hasContact = false;
        
        return bindPersonalContact(context, bind);
    }
    
   public static ContactBinding bindPersonalContact(Context context, ContactBinding binding) {
    	Cursor cursor = null;
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(binding.originalNumber));
            cursor = context.getContentResolver().query(uri, PROJECTION4, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                	binding.phoneNumber = cursor.getString(NUMBER_INDX4);
                    binding.displayName = cursor.getString(DISPLAY_NAME_INDX4);
                    int type = cursor.getInt(TYPE_INDX4);
                    binding.phoneId = getPhoneIdFromLookUp(context, cursor.getLong(ID_INDX4), binding.phoneNumber, type);
                    
                    if (binding.phoneId == -1) {
                        if (LogLevel.MARKET) {
                            MarketLog.e(TAG, "bindPersonalContact, phoneId is not defined for lookup " + cursor.getLong(ID_INDX4));
                        }
                        binding.hasContact = false;
                        return binding;
                    }
                    
                    binding.hasContact = true;
                    binding.isPersonalContact = true;
                    binding.phoneNumberTag = getPhoneNumberTag(context, type);
                    if (isCustomTagPhoneNumber(context, type)) {
                        String label = cursor.getString(LABEL_INDX4);
                        if (label != null) {
                            binding.phoneNumberTag = label;
                        }
                    }
                    Cursor c = null;
                    try {
                        Uri phoneUri = ContentUris.withAppendedId(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, binding.phoneId);
                        c = context.getContentResolver().query(phoneUri, PROJECTION8x, null, null, null);
                        if (c != null) {
                            if (c.moveToFirst()) {
                                binding.contact_id = c.getLong(CONTACT_ID_INDX8x);
                                binding.photoId = c.getLong(PHOTO_ID_INDX8x);
                            }
                        }
                    } catch (java.lang.Throwable error) {
                        if (LogLevel.MARKET) {
                            MarketLog.e(TAG, "SyncBind2: " + error.getMessage());
                        }
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }
                }
            }

        } catch (java.lang.Throwable error) {
            if (LogLevel.MARKET) {
            	MarketLog.e(TAG, "Contact binding: " + error.getMessage());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return binding;
    }

    private static final String[] PROJECTION4 = {
            ContactsContract.PhoneLookup._ID, 
            ContactsContract.PhoneLookup.DISPLAY_NAME, 
            ContactsContract.PhoneLookup.TYPE,
            ContactsContract.PhoneLookup.LABEL,
            ContactsContract.PhoneLookup.NUMBER};
    private static final int ID_INDX4 = 0;
    private static final int DISPLAY_NAME_INDX4 = 1;
    private static final int TYPE_INDX4 = 2;
    private static final int LABEL_INDX4 = 3;
    private static final int NUMBER_INDX4 = 4;
    
    public static String getPhoneNumberTag(Context context, long tag) {
        switch ((int)tag) {
        case (ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM):
            return context.getString(R.string.phone_tag_custom);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE):
            return context.getString(R.string.phone_tag_mobile);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_HOME):
            return context.getString(R.string.phone_tag_home);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_WORK):
            return context.getString(R.string.phone_tag_work);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_OTHER):
            return context.getString(R.string.phone_tag_other);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_MAIN):
            return context.getString(R.string.phone_tag2_main);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_CAR):
            return context.getString(R.string.phone_tag2_car);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN):
            return context.getString(R.string.phone_tag2_company_main);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE):
            return context.getString(R.string.phone_tag2_work_mobile);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK):
            return context.getString(R.string.phone_tag_fax_work);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT):
            return context.getString(R.string.phone_tag2_assistant);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK):
            return context.getString(R.string.phone_tag2_callback);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME):
            return context.getString(R.string.phone_tag_fax_home);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_ISDN):
            return context.getString(R.string.phone_tag2_isdn);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_MMS):
            return context.getString(R.string.phone_tag2_mms);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX):
            return context.getString(R.string.phone_tag2_other_fax);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_PAGER):
            return context.getString(R.string.phone_tag_pager);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER):
            return context.getString(R.string.phone_tag2_work_pager);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_RADIO):
            return context.getString(R.string.phone_tag2_radio);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_TELEX):
            return context.getString(R.string.phone_tag2_telex);
        case (ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD):
            return context.getString(R.string.phone_tag2_tty_tdd);
        }
        return context.getString(R.string.phone_tag_other);
    }
    
    public static boolean isCustomTagPhoneNumber(Context context, long tag) {
        return (ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM == tag);
    }
    
    private static long getPhoneIdFromLookUp(Context context, long contactId, String number, long type) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PROJECTION9,
                    "((" + ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?) AND ("
                            + ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ) AND (" +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = ? ))" ,
                    new String[] { number, String.valueOf(contactId), String.valueOf(type)}, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    return cursor.getLong(ID_INDX9);
                }
            }
        } catch (java.lang.Throwable error) {
            if (LogLevel.MARKET) {
                MarketLog.e(TAG, "getPhoneIdFromLookUp: " + error.getMessage());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return -1;
    }
    
	private static final String[] PROJECTION8 = {
			ContactsContract.CommonDataKinds.Phone.NUMBER,
			ContactsContract.CommonDataKinds.Phone.TYPE,
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
			ContactsContract.CommonDataKinds.Phone.LABEL,
			ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY,
			ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
			ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
			ContactsContract.CommonDataKinds.Phone.PHOTO_ID};

	private static final int NUMBER_ID_INDX8 = 0;
	private static final int TYPE_INDX8 = 1;
	private static final int DISPLAY_NAME_INDX8 = 2;
	private static final int LABEL_INDX8 = 3;
	private static final int IS_PRIMARY_INDX8 = 4;
	private static final int CONTACT_ID_INDX8 = 5;
	private static final int LOOKUP_KEY_INDX8 = 6;
	private static final int PHOTO_ID_INDX8 = 7;

	private static final String[] PROJECTION8x = {
			ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
			ContactsContract.CommonDataKinds.Phone.PHOTO_ID };

	private static final int CONTACT_ID_INDX8x = 0;
	private static final int PHOTO_ID_INDX8x = 1;

    private static final String[] PROJECTION9 = {
        ContactsContract.CommonDataKinds.Phone._ID,
        ContactsContract.CommonDataKinds.Phone.NUMBER, 
        ContactsContract.CommonDataKinds.Phone.TYPE,
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
    private static final int ID_INDX9 = 0;
    
    public PhoneContact lookUpPhoneContactById(Context context, long phoneId) {
    	PhoneContact contact = new PhoneContact();
        Cursor cursor = null;
        try {
            Uri phoneUri = ContentUris.withAppendedId(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, phoneId);
            cursor = context.getContentResolver().query(phoneUri, PROJECTION5, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    contact.phoneId = phoneId;
                    contact.contactId = cursor.getLong(CONTACT_ID_INDX5);
                    contact.number = cursor.getString(NUMBER_INDX5);
                    contact.type = cursor.getInt(TYPE_INDX5);
                    contact.label = cursor.getString(LABEL_INDX5);
                    contact.displayName = cursor.getString(DISPLAY_NAME_INDX5);
                    contact.photoId = cursor.getLong(PHOTO_ID_INDX5);
                    
                    String contactId = String.valueOf(contact.contactId);
                    String companySelection = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] companySelectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                    
                    Cursor companyCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, PROJECTION_COMPANY, 
                    		companySelection, companySelectionArgs, null);
                    if(companyCursor != null && companyCursor.moveToFirst()){
                    	String company = companyCursor.getString(COMPANY_INDX);
                    	if(company != null && !TextUtils.isEmpty(company)){
                    		contact.companyName = company;
                    	}
                    }
                    return contact;
                }
            }
        } catch (Exception ex) {
            if (LogLevel.MARKET) {
                MarketLog.e(TAG, "lookUpPhoneContactById() failed: " + ex.getMessage());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private static final String[] PROJECTION5 = { ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, 
            ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.LABEL};
    private static final int CONTACT_ID_INDX5 = 0;
    private static final int NUMBER_INDX5 = 1;
    private static final int TYPE_INDX5 = 2;
    private static final int DISPLAY_NAME_INDX5 = 3;
    private static final int PHOTO_ID_INDX5 = 4;
    private static final int LABEL_INDX5 = 5;
    
    private static final String[] PROJECTION_COMPANY = {
    	ContactsContract.CommonDataKinds.Organization.COMPANY,
    	ContactsContract.CommonDataKinds.Organization.TITLE,
    	ContactsContract.CommonDataKinds.Organization.DEPARTMENT};
    private static final int COMPANY_INDX = 0;
    private static final int COMPANY_TITLE_INDX = 1;
    private static final int COMPANY_DEPARTMENT_INDX = 2;
    
    private static final String[] PROJECTION_NAME = {
    	ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
    	ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
    	ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
    	ContactsContract.CommonDataKinds.StructuredName.SUFFIX,};
    private static final int GIVEN_NAME_INDX = 0;
    private static final int MIDDLE_NAME_INDX = 1;
    private static final int FAMILY_NAME_INDX = 2;
    private static final int SUFFIX_INDX = 3;
    
    public static String getEmailTag(Context context, long tag) {
        switch ((int)tag) {
        case (ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM):
            return context.getString(R.string.email_tag_custom);
        case (ContactsContract.CommonDataKinds.Email.TYPE_HOME):
            return context.getString(R.string.phone_tag_mobile);
        case (ContactsContract.CommonDataKinds.Email.TYPE_MOBILE):
            return context.getString(R.string.email_tag_home);
        case (ContactsContract.CommonDataKinds.Email.TYPE_WORK):
            return context.getString(R.string.email_tag_work);
        case (ContactsContract.CommonDataKinds.Email.TYPE_OTHER):
            return context.getString(R.string.email_tag_other);
        }
        return context.getString(R.string.email_tag_other);
    }
    
    public static List<EmailContact> getEmailAddresses(Context context, long contactId) {
        ArrayList<EmailContact> list = new ArrayList<EmailContact>();
        if (contactId < 0) {
            if (LogLevel.MARKET) {
            	MarketLog.w(TAG, "getEmailAddresses error: id is invalid");
            }
            return list;
        }
        
        Cursor c = null;
        try {
            c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION10,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { String.valueOf(contactId) }, null);
            if (c != null) {
                c.moveToPosition(-1);
                while (c.moveToNext()) {
                    try {
                    	EmailContact e = new EmailContact();
                        e.displayName = c.getString(DISPLAY_NAME_INDX10);
                        e.contactId = contactId;
                        e.emailAddress = c.getString(DATA_INDX10);
                        int type = c.getInt(TYPE_INDX10);
                        Resources res = context.getResources();
                        if (type == ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM) {
                            e.emailTag = c.getString(LABEL_INDX10);
                        } else {
                        	e.emailTag = getEmailTag(context, type);
                        }
                        
                        if (e.emailTag == null || e.emailTag.trim().length() < 1) {
                            e.emailTag = res.getString(R.string.email_tag_other);
                        }
                        
                        list.add(e);
                    } catch (java.lang.Throwable error) {
                        if (LogLevel.MARKET) {
                        	MarketLog.e(TAG, "getEmailAddresses - read error: " + error.getMessage());
                        }
                    }
                }
            }
        } catch (java.lang.Throwable error) {
            if (LogLevel.MARKET) {
                MarketLog.e(TAG, "getEmailAddresses: " + error.getMessage());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return list;
        
    }
    
    private static final String[] PROJECTION10 = {
        ContactsContract.CommonDataKinds.Email.TYPE,
        ContactsContract.CommonDataKinds.Email.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Email.LABEL,
        ContactsContract.CommonDataKinds.Email.DATA,
    };
    private static final int TYPE_INDX10 = 0;
    private static final int DISPLAY_NAME_INDX10 = 1;
    private static final int LABEL_INDX10 = 2;
    private static final int DATA_INDX10 = 3;
    
    public static List<TaggedContactPhoneNumber> getPersonalContactPhoneNumbers(Context context, long contactId) {
        ArrayList<TaggedContactPhoneNumber> list = new ArrayList<TaggedContactPhoneNumber>();
        if (contactId < 0) {
            if (LogLevel.MARKET) {
                MarketLog.w(TAG, "getPersonalContactPhoneNumbers error: id is invalid");
            }
            return list;
        }

        Cursor cursor = null;

        try {
            String[] whereArgs = new String[] { String.valueOf(contactId) };
            cursor = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PROJECTION7, 
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", whereArgs, null);

            if (cursor != null) {
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    try {
                    	String number = cursor.getString(NUMBER_INDX7);
                    	if(TextUtils.isEmpty(number)){
                    		continue;
                    	}
                        TaggedContactPhoneNumber num = new TaggedContactPhoneNumber();
                        num.originalNumber = NumberHelper.formatPhoneNumber(number);
                        
                        String name = cursor.getString(DISPLAY_NAME_INDX7);
                        if(!TextUtils.isEmpty(name)){
                        	num.displayName = name;
                        }
                        
                        int type = cursor.getInt(TYPE_INDX7);
                        num.numberTag = getPhoneNumberTag(context, type);
                        if (isCustomTagPhoneNumber(context, type)) {
                            String label = cursor.getString(LABEL_INDX7);
                            if (label != null) {
                                num.numberTag = label;
                            }
                        }
                        
                        if (cursor.getInt(IS_PRIMARY_INDX7) != 0) {
                            num.isDefault = true;
                        }
                        num.id = cursor.getLong(ID_INDX7);
                        num.contact_id 	= cursor.getLong(CONTACT_ID_INDX7);
                        num.lookup_key 	= cursor.getString(LOOKUP_KEY_INDX7);
                        num.photo_id    = cursor.getLong(PHOTO_ID_INDX7);
                        num.phone_id	= num.id;
                        
                        list.add(num);
                    } catch (java.lang.Throwable error) {
                        if (LogLevel.MARKET) {
                            MarketLog.e(TAG, "getPersonalContactPhoneNumbers read error: " + error.getMessage());
                        }
                    }
                }
            }
        } catch (java.lang.Throwable ex) {
            if (LogLevel.MARKET) {
                MarketLog.e(TAG, "getPersonalContactPhoneNumbers error failed: " + ex.getMessage());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    private static final String[] PROJECTION7 = {
        ContactsContract.CommonDataKinds.Phone.TYPE,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY,
        ContactsContract.CommonDataKinds.Phone.LABEL,
        ContactsContract.CommonDataKinds.Phone._ID,
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
        ContactsContract.CommonDataKinds.Phone.PHOTO_ID
    };
    
    private static final int TYPE_INDX7 			= 0;
    private static final int DISPLAY_NAME_INDX7 	= 1;
    private static final int NUMBER_INDX7 			= 2;
    private static final int IS_PRIMARY_INDX7 		= 3;
    private static final int LABEL_INDX7 			= 4;
    private static final int ID_INDX7 				= 5;
    private static final int CONTACT_ID_INDX7 		= 6;
    private static final int LOOKUP_KEY_INDX7 		= 7;
    private static final int PHOTO_ID_INDX7 		= 8;
    
    public static ContactInfo getContactInfoByContactId(Context context, long contactId){
    	 if (contactId < 0) {
             if (LogLevel.MARKET) {
                 MarketLog.w(TAG, "getContactInfoByContactId error: id is invalid");
             }
             return null;
         }
    	 
    	 ContactInfo contactInfo = new ContactInfo();
    	 Cursor cursor = null;

         try {
             String[] whereArgs = new String[] { String.valueOf(contactId) };
             cursor = context.getContentResolver().query(
            		 ContactsContract.Contacts.CONTENT_URI,
                     PROJECTION11, 
                     ContactsContract.Contacts._ID + " = ?", whereArgs, null);

             if (cursor != null && cursor.moveToFirst()) {
            	 contactInfo.contactId = cursor.getLong(CONTACT_ID_INDX11);
            	 contactInfo.displayName = cursor.getString(DISPLAY_NAME_INDX11);
            	 contactInfo.photoId = cursor.getLong(PHOTO_ID_INDX11);
            	 contactInfo.lookupKey = cursor.getString(LOOKUP_KEY_INDX11);
            	 
            	 String companySelection = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                 String[] companySelectionArgs = new String[]{String.valueOf(contactId), ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                 
                 Cursor companyCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, PROJECTION_COMPANY, 
                 		companySelection, companySelectionArgs, null);
                 if(companyCursor != null && companyCursor.moveToFirst()){
                 	String company = companyCursor.getString(COMPANY_INDX);
                 	if(company != null && !TextUtils.isEmpty(company)){
                 		contactInfo.companyName = company;
                 	}
                 	String title = companyCursor.getString(COMPANY_TITLE_INDX);
                 	if(title != null && !TextUtils.isEmpty(title)){
                 		contactInfo.jobTitle = title;
                 	}
                 	String department = companyCursor.getString(COMPANY_DEPARTMENT_INDX);
                 	if(department != null && !TextUtils.isEmpty(department)){
                 		contactInfo.department = department;
                 	}
                 }
                 
                 if(companyCursor != null && !companyCursor.isClosed()){
                	 companyCursor.close();
                 }
                 
                 String nameSelection = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                 String[] nameSelectionArgs = new String[]{String.valueOf(contactId), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
                 Cursor nameCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, PROJECTION_NAME, 
                		 nameSelection, nameSelectionArgs, null);
                 if(nameCursor != null && nameCursor.moveToFirst()){
                  	String firstName = nameCursor.getString(GIVEN_NAME_INDX);
                  	if(firstName != null && !TextUtils.isEmpty(firstName)){
                  		contactInfo.firstName = firstName;
                  	}
                  	String middleName = nameCursor.getString(MIDDLE_NAME_INDX);
                  	if(middleName != null && !TextUtils.isEmpty(middleName)){
                  		contactInfo.middleName = middleName;
                  	}
                  	String lastName = nameCursor.getString(FAMILY_NAME_INDX);
                  	if(lastName != null && !TextUtils.isEmpty(lastName)){
                  		contactInfo.lastName = lastName;
                  	}
                  	String suffix = nameCursor.getString(SUFFIX_INDX);
                  	if(suffix != null && !TextUtils.isEmpty(suffix)){
                  		contactInfo.suffix = suffix;
                  	}
                  }
                 if(nameCursor != null && !nameCursor.isClosed()){
                	 nameCursor.close();
                 }
                 
             }
             
         } catch (java.lang.Throwable ex) {
             if (LogLevel.MARKET) {
                 MarketLog.e(TAG, "getContactInfoByContactId error failed: " + ex.getMessage());
             }
         } finally {
             if (cursor != null && !cursor.isClosed()) {
                 cursor.close();
             }
         }
    	 
         return contactInfo;
    }
    
	private static final String[] PROJECTION11 = {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,
			ContactsContract.Contacts.PHOTO_ID,
			ContactsContract.Contacts.LOOKUP_KEY};
	private static final int CONTACT_ID_INDX11 = 0;
	private static final int DISPLAY_NAME_INDX11 = 1;
	private static final int PHOTO_ID_INDX11 = 2;
	private static final int LOOKUP_KEY_INDX11 = 3;
	
	/**
	 * Compare two strings
	 * 
	 */
	public static boolean compareStrings(String str1, String str2) {
        if (str1 == null) {
            if (str2 != null) {
                return false;
            }
            return true;
        } else {
            if (str2 == null) {
                return false;
            }
            return str1.equalsIgnoreCase(str2);
        }
    }
	
	/**
	 * To log the sync trace 
	 * 
	 */
	public static void bindSyncTrace(Context context, BindSync bsync, String logTag, String logTag2, long recordId) {
        if (LogLevel.MARKET) {
            try {
                BindSync.State state =  bsync.syncState;
                StringBuffer sb = new StringBuffer();
                sb.append(logTag2);
                sb.append(":BindSync:id=");
                sb.append(recordId);
                sb.append(':');
                if (state == BindSync.State.INVALID_RECORD) {
                    sb.append("INVALID_RECORD");
                } else if (state == BindSync.State.GONE) {
                    sb.append("GONE:");
                    sb.append(bsync.bind.originalNumber);
                } else if (state == BindSync.State.BIND) {
                    if (LogLevel.DEV) {
                        sb.append("BIND:");
                        sb.append(bsync.bind.originalNumber);
                        if (bsync.bind.isPersonalContact) {
                            sb.append(":Personal:");
                        } else {
                            sb.append(":Company:");
                        }
                        sb.append(bsync.bind.phoneId);
                    }
                } else if (state == BindSync.State.UPDATED) {
                    if (LogLevel.DEV) {
                        sb.append("UPDATED:");
                        sb.append(bsync.bind.originalNumber);
                        if (bsync.bind.isPersonalContact) {
                            sb.append(":Personal:");
                        } else {
                            sb.append(":Company:");
                        }
                        sb.append(bsync.bind.phoneId);
                    }
                } else if (state == BindSync.State.REBIND) {
                    sb.append("REBIND:");
                    sb.append(bsync.bind.originalNumber);
                    if (bsync.bind.isPersonalContact) {
                        sb.append(":Personal:");
                    } else {
                        sb.append(":Company:");
                    }
                    sb.append(bsync.bind.phoneId);
                } 
                
                if (LogLevel.MARKET) {
                    MarketLog.d(logTag, sb.toString());
                }
            } catch (Exception ex) {
            }
        }
    }
	
	/**
	 * Get contact address
	 * 
	 */
	public static List<AddressContact> getAddressContact(Context context, long contactId) {
        ArrayList<AddressContact> list = new ArrayList<AddressContact>();
        if (contactId < 0) {
            if (LogLevel.MARKET) {
            	MarketLog.w(TAG, "getAddressContact error: id is invalid");
            }
            return list;
        }
        
        Cursor c = null;
        try {
            c = context.getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, PROJECTION12,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { String.valueOf(contactId) }, null);
            if (c != null) {
                c.moveToPosition(-1);
                while (c.moveToNext()) {
                    try {
                    	AddressContact address = new AddressContact();
                    	address.street = c.getString(STREET_INDX12);
                    	address.city = c.getString(CITY_INDX12);
                    	address.region = c.getString(REGION_INDX12);
                    	address.country = c.getString(COUNTRY_INDX12);
                    	address.postcode = c.getString(POSTCODE_INDX12);
                    	address.formatted_address = c.getString(FORMATTED_ADDRESS_INDX12);
                        int type = c.getInt(TYPE_INDX12);
                        Resources res = context.getResources();
                        if (type == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM) {
                        	address.type = c.getString(LABEL_INDX12);
                        } else {
                            if (type == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME) {
                            	address.type= res.getString(R.string.address_tag_home);
                            } else if (type == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK) {
                            	address.type = res.getString(R.string.address_tag_work);
                            } else if (type == ContactsContract.CommonDataKinds.Email.TYPE_OTHER) {
                            	address.type = res.getString(R.string.address_tag_other);
                            }
                        }
                        
                        if (address.type == null || address.type.trim().length() < 1) {
                        	address.type = res.getString(R.string.address_tag_other);
                        }
                        
                        list.add(address);
                    } catch (java.lang.Throwable error) {
                        if (LogLevel.MARKET) {
                        	MarketLog.e(TAG, "getEmailAddresses - read error: " + error.getMessage());
                        }
                    }
                }
            }
        } catch (java.lang.Throwable error) {
            if (LogLevel.MARKET) {
                MarketLog.e(TAG, "getEmailAddresses: " + error.getMessage());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return list;
        
    }
    
    private static final String[] PROJECTION12 = {
        ContactsContract.CommonDataKinds.StructuredPostal.STREET,
        ContactsContract.CommonDataKinds.StructuredPostal.CITY,
        ContactsContract.CommonDataKinds.StructuredPostal.REGION,
        ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
        ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
        ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
        ContactsContract.CommonDataKinds.StructuredPostal.LABEL
    };
    
    private static final int STREET_INDX12 = 0;
    private static final int CITY_INDX12 = 1;
    private static final int REGION_INDX12 = 2;
    private static final int COUNTRY_INDX12 = 3;
    private static final int POSTCODE_INDX12 = 4;
    private static final int FORMATTED_ADDRESS_INDX12 = 5;
    private static final int TYPE_INDX12 = 6;
    private static final int LABEL_INDX12 = 7;
    
}
