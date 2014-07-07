package com.zhuang.quickcall.contacts;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhuang.quickcall.utils.NumberHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class ContactsSearchUtils {

	private static final String TAG = "[EASIIO] ContactsSearchUtils";
	
	public static final class PersonalPhones {
		private PersonalPhones() {
		};
		
		private static final String PERSONAL_QUERY_SELECTION = Phone.DISPLAY_NAME + " LIKE ? || '%'" + " OR " +
	    		"REPLACE(REPLACE(REPLACE(" + Phone.NUMBER + ", \"-\", \"\"), \" \", \"\"), \"+\", \"\")" + " LIKE ? || '%'" + " OR " +
	    		"REPLACE(REPLACE(REPLACE(" + Phone.NUMBER + ", \"-\", \"\"), \" \", \"\"), \"+\", \"\")" + " LIKE ? || '%'" + " OR " +
	    		Phone.NUMBER + " LIKE ? || '%'" + " OR " +
				Phone.NUMBER + " LIKE ? || '%'";
	    
		/**
		 * Personal projection.
		 */
		static final String[] PERSONAL_PHONE_PROJECTION = new String[] {
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID, // 0
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, // 1
				ContactsContract.CommonDataKinds.Phone.TYPE, // 2
				ContactsContract.CommonDataKinds.Phone.NUMBER, // 3
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID //4
		};

		static final int ID_COLUMN_INDEX = 0;
		static final int NAME_COLUMN_INDEX = 1;
		static final int TYPE_COLUMN_INDEX = 2;
		static final int NUMBER_COLUMN_INDEX = 3;
		static final int PHOTO_ID_COLUMN_INDEX = 4;

	}

	public static class SearchContact {
		int id;
		String number;
		int type;

		public SearchContact(int id, String number, int type) {
			this.id = id;
			this.number = number;
			this.type = type;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj != null && obj.getClass() == SearchContact.class) {
				SearchContact other = (SearchContact) obj;
				return this.id == other.id && this.number == other.number && this.type == other.type;
			}
			return false;
		}
	}

	public static void searchContacts(Context context, String what, SortedSet<SearchItem> results) {
    	
    	String parsedWhat = "1" + what;
    	
		Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				PersonalPhones.PERSONAL_PHONE_PROJECTION, PersonalPhones.PERSONAL_QUERY_SELECTION,
				new String[] { what, what, what, parsedWhat, parsedWhat }, null);

		if (cursor == null) {
			return;
		}

		if (cursor.getCount() <= 0) {
			cursor.close();
			return;
		}

		Set<SearchContact> contactSet = new HashSet<SearchContact>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(PersonalPhones.ID_COLUMN_INDEX);
			int type = cursor.getInt(PersonalPhones.TYPE_COLUMN_INDEX);
			String number = cursor.getString(PersonalPhones.NUMBER_COLUMN_INDEX);

			if (contactSet.contains(new SearchContact(id, NumberHelper.formatPhoneNumber(number), type))) {
				continue;
			}
			results.add(new SearchItem(SearchItem.ANDROID_CONTACT,
					cursor.getString(PersonalPhones.NAME_COLUMN_INDEX), number, type, ContactsUtils.getPhoneNumberTag(context, type)
							+ ": "));
		}
		cursor.close();
	}
	
	public static Cursor searchContacts(Context context, String what){
		try{
			TreeSet<SearchItem> resultsSet;
			resultsSet = new TreeSet<SearchItem>(new SearchItem.ItemComparator());
			resultsSet.clear();
			searchContacts(context, what, resultsSet);
			MatrixCursor cursor = new MatrixCursor(SearchItem.COLUMN_NAMES);
	        Iterator<SearchItem> iterator = resultsSet.iterator();
	        SearchItem item;
	        int id = 0;
	        while (iterator.hasNext()) {
	            item = iterator.next();
	            item.setID(id++);
	            cursor.addRow(item.getColumns());
	        }
	        return cursor;
		} catch (Exception e){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "searchContacts error : " + e.toString());
			}
			
			return null;
		}
	}
}
