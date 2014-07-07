package com.zhuang.quickcall.contacts;

import java.util.List;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.QuickCallConstants;
import com.zhuang.quickcall.R;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhuang.quickcall.quickcall.CreateQuickCallActivity;
import com.zhuang.quickcall.utils.DialogUtils;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ContactsSelectorActivity extends ContactsActivity {

	private static final String TAG = "[ZHUANG] ContactsSelectorActivity";
	
	public static final int REQUEST_CODE = 10011;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		super.isSelectorActivity = true;
		if(LogLevel.MARKET){
			MarketLog.d(TAG, "onCreate...");
		}
		
		mAddContact.setVisibility(View.INVISIBLE);
		TextView title = (TextView) this.findViewById(R.id.contacts_title_view);
		title.setText(R.string.title_select_contact);
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		if(mContactsAdapter == null){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "onListItemClick mContactsAdapter = null");
			}
			return;
		}
		
		Cursor cursor = (Cursor) mContactsAdapter.getItem(position);
		if (cursor == null) {
			return;
		}
		
		long personId = cursor.getLong(ContactsProjection.PersonalContacts.ID_COLUMN_INDEX);
		int hasContact = cursor.getInt(ContactsProjection.PersonalContacts.HAS_PHONE_NUMBER_COLUMN_INDEX);
		if(hasContact == 0){
			if(LogLevel.MARKET){
				MarketLog.d(TAG, "onListItemClick no phone.");
			}
			DialogUtils.showQuickCallAlertDialog(this, R.string.select_contact_no_contact_title, R.string.select_contact_no_contact_message);
			return;
		}
		String name = cursor.getString(ContactsProjection.PersonalContacts.DISPLAY_NAME_COLUMN_INDEX);
		if(LogLevel.DEV){
			DevLog.d(TAG, "onListItemClick personId : " + personId + ", name : " + name);
		}
		
		createNewQuickCall(personId, name);
		
	}
	
	private void createNewQuickCall(long peopleId, String name){
		List<TaggedContactPhoneNumber> list = ContactsUtils.getPersonalContactPhoneNumbers(this, peopleId);
		if(list == null || list.isEmpty()){
			if(LogLevel.DEV){
				DevLog.e(TAG, "sendMessage : list is null");
			}
			return;
		} else if (list.size() == 1){
			TaggedContactPhoneNumber numbers = list.get(0);
			Intent intent = new Intent(ContactsSelectorActivity.this, CreateQuickCallActivity.class);
			intent.putExtra(QuickCallConstants.EXTRA_SELECT_NAME, name);
			intent.putExtra(QuickCallConstants.EXTRA_SELECT_NUMBER, numbers.originalNumber);
			intent.putExtra(QuickCallConstants.EXTRA_SELECT_PHOTO_ID, numbers.photo_id);
			ContactsSelectorActivity.this.startActivityForResult(intent, REQUEST_CODE);
			return;
		}
		
		new PhoneSelectorDialog(this, list, name, PhoneSelectorDialog.TYPE_SELECT_CONTACT).show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == CreateQuickCallActivity.RESULT_SUCCESS){
			finish();
		}
	}
	
}
