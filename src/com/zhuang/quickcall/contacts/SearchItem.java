/** 
 * Copyright (C) 2010-2011, RingCentral, Inc. 
 * All Rights Reserved.
 */
package com.zhuang.quickcall.contacts;

import java.util.Comparator;

import android.app.SearchManager;
import android.provider.BaseColumns;

public class SearchItem {

	static final int RC_EXTENSION = -1;         //Don't change this constants!!!
    static final int ANDROID_CONTACT = 1;       //(they are used by ItemComparator class)
    
    private static final String ID = BaseColumns._ID; 
    private static final String NAME = SearchManager.SUGGEST_COLUMN_TEXT_1; 
    private static final String NUMBER = SearchManager.SUGGEST_COLUMN_INTENT_DATA; 
    private static final String LABEL = "label";
    private static final String LABEL_NUMBER = SearchManager.SUGGEST_COLUMN_TEXT_2; 

    public static final String[] COLUMN_NAMES = { ID, NAME, NUMBER, LABEL, LABEL_NUMBER };

    public static int COLUMN_ID           = 0;
    public static int COLUMN_NAME         = 1;
    public static int COLUMN_NUMBER       = 2;
    public static int COLUMN_LABEL        = 3;
    public static int COLUMN_LABEL_NUMBER = 4;
    
    private int contactType;
    private int phoneType;
    private String[] columns;


    SearchItem(int contactType, String name, String number, int phoneType, String label) {
        this.contactType = contactType;
        this.phoneType = phoneType;
        columns = new String[] { "0", name, number, label, label + number };
    }

    String[] getColumns() {
        return columns;
    }
    
    void setID(int id) {
        columns[COLUMN_ID] = String.valueOf(id);
    }

///////////////////////////////////////////////////////////////////////////////    
    
    static class ItemComparator implements Comparator<SearchItem> {

        @Override
        public int compare(SearchItem object1, SearchItem object2) {
            int result;
            
            //Compare names
            result = object1.columns[COLUMN_NAME].compareToIgnoreCase(object2.columns[COLUMN_NAME]);
            if (result != 0) {
                return result;
            }
            
            //Compare contact types: RC Extensions will be always at the top of the list for items with the same name 
            if (object1.contactType != object2.contactType) {
                return object1.contactType;           
            }
        
            //Compare phone types for Android contacts(Note that now ext have phone type, too)
			if (object1.phoneType < object2.phoneType) {
				return -1;
			} else if (object1.phoneType > object2.phoneType) {
				return +1;
			}
        
            //Compare phone numbers
            return object1.columns[COLUMN_NUMBER].compareTo(object2.columns[COLUMN_NUMBER]);
        }
    }
}
