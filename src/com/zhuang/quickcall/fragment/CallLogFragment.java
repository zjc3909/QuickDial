package com.zhuang.quickcall.fragment;

import java.util.HashMap;
import java.util.List;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.QuickCallConstants;
import com.zhuang.quickcall.R;
import com.zhuang.quickcall.calllog.CallLogInfo;
import com.zhuang.quickcall.calllog.CallLogInfoActivity;
import com.zhuang.quickcall.calllog.CallLogUtils;
import com.zhuang.quickcall.contacts.ContactBinding;
import com.zhuang.quickcall.contacts.ContactInfo;
import com.zhuang.quickcall.contacts.ContactInfoActivity;
import com.zhuang.quickcall.contacts.ContactsSelectorActivity;
import com.zhuang.quickcall.contacts.ContactsUtils;
import com.zhuang.quickcall.contacts.EmailContact;
import com.zhuang.quickcall.contacts.EmailSelectorDialog;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhuang.quickcall.quickcall.CreateQuickCallActivity;
import com.zhuang.quickcall.quickcall.QuickCallChangeActivity;
import com.zhuang.quickcall.quickcall.QuickCallInfo;
import com.zhuang.quickcall.quickcall.QuickCallUtils;
import com.zhuang.quickcall.utils.DateTimeUtils;
import com.zhuang.quickcall.utils.DialUtils;
import com.zhuang.quickcall.utils.DialogUtils;
import com.zhuang.quickcall.utils.EmailSender;
import com.zhuang.quickcall.utils.ImageLoader;
import com.zhuang.quickcall.utils.widgets.QuickCallAlertDialog;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class CallLogFragment extends Fragment {

	private static final String TAG = "[ZHUANG]CallLogFragment";
	
	private static final int QUERY_COMPLETE = 0;
	private static final int QUERY_NO_CONTACTS = 1;
	
	private static final int EDIT_CONTACT_REQUEST = 78;
	
	private static final int QUERY_TOKEN = 304;

    private ListView mCallLogListView;
	private View mEmptyView;
	private View mQueryProgressView;
	private TextView mEmptyTextView;
	
	protected ContactsAdapter mContactsAdapter;
	private QueryContactsHandler mQueryContactsHandler;
	
	private HashMap<Long, Boolean> mDateTimeMap;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(LogLevel.MARKET){
			MarketLog.i(TAG, "onAttach...");
		}
        
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onCreate...");
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onCreateView...");
		}
		View v = inflater.inflate(R.layout.call_log_fragment_layout, container, false);
		buildLayout(v);
		mQueryContactsHandler = new QueryContactsHandler(getActivity());
		mContactsAdapter = new ContactsAdapter(getActivity());
		mCallLogListView.setAdapter(mContactsAdapter);
		
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onViewCreated...");
		}
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onResume(){
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onResume...");
		}
		super.onResume();
		startEmptyQuery();
	}
	
	@Override
	public void onPause(){
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onPause...");
		}
		super.onPause();
		
	}
	
	@Override
	public void onDestroy(){
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onDestroy...");
		}
		super.onDestroy();
		if (mQueryContactsHandler != null) {
			mQueryContactsHandler.cancelOperation(QUERY_TOKEN);
			mQueryContactsHandler = null;
		}
		
	}
	
	private void startEmptyQuery(){
		if (mQueryContactsHandler == null) {
			if(LogLevel.DEV){
				DevLog.e(TAG, "startEmptyQuery() failed, mQueryContactsHandler is null.");
			}
			return;
		}
		mQueryContactsHandler.cancelOperation(QUERY_TOKEN);
		
		Uri uri = CallLogUtils.CALL_LOG_CONTENT_URI;
		mQueryContactsHandler.startQuery(QUERY_TOKEN, null, uri, 
				CallLogUtils.SUMMARY_PROJECTION, 
				null, 
				null, 
				CallLogUtils.DEFAULT_SORT_ORDER);
	}
	
	private Handler mStopLoadHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch(msg.what){
			case QUERY_COMPLETE:
				mEmptyView.setVisibility(View.GONE);
				mQueryProgressView.setVisibility(View.GONE);
				mEmptyTextView.setVisibility(View.GONE);
				break;
			case QUERY_NO_CONTACTS:
				mEmptyView.setVisibility(View.VISIBLE);
				mQueryProgressView.setVisibility(View.GONE);
				mEmptyTextView.setVisibility(View.VISIBLE);
				mEmptyTextView.setText(R.string.no_contacts);
				break;
			default:
				break;
				}
		}
		
	};
	
	private void buildLayout(View v){

		if(LogLevel.DEV){
			DevLog.d(TAG, "Build layout.");
		}
		
		mCallLogListView = (ListView) v.findViewById(R.id.calllog_listview);
		
		mEmptyView = v.findViewById(R.id.empty_layout);
		mQueryProgressView = v.findViewById(R.id.query_proLoading);
		mEmptyTextView = (TextView) v.findViewById(R.id.empty_textview);
		mEmptyView.setVisibility(View.VISIBLE);
		mQueryProgressView.setVisibility(View.VISIBLE);
		mEmptyTextView.setVisibility(View.GONE);
		
		mCallLogListView.setOnItemClickListener(onItemClickListener);
		mCallLogListView.setOnItemLongClickListener(onItemLongClickListener);
	}
	
	private final class QueryContactsHandler extends AsyncQueryHandler {
		
		public QueryContactsHandler(Context context) {
			super(context.getContentResolver());
		}
		
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor == null || cursor.isClosed() || !cursor.moveToFirst()) {
				mStopLoadHandler.sendEmptyMessage(QUERY_NO_CONTACTS);
				return;
			}
			
			if (!getActivity().isFinishing()) {
				
				if(cursor.getCount() > 0) {
        			mStopLoadHandler.sendEmptyMessage(QUERY_COMPLETE);
        		} else {
        			mStopLoadHandler.sendEmptyMessage(QUERY_NO_CONTACTS);
        		}
				
				if(mDateTimeMap != null){
					mDateTimeMap.clear();
					mDateTimeMap = null;
				}
				
				mDateTimeMap = new HashMap<Long, Boolean>();
				
				long currentDate = 0;
				while(!cursor.isAfterLast()){
					long _id = cursor.getLong(CallLogUtils.ID_INDEX);
					long date = cursor.getLong(CallLogUtils.DATE_INDEX);
					date = DateTimeUtils.getStartTimeOfDay(date);
					if(date != currentDate){
						mDateTimeMap.put(_id, true);
						currentDate = date;
					} else {
						mDateTimeMap.put(_id, false);
					}
					cursor.moveToNext();
				}
				mContactsAdapter.changeCursor(cursor);
			} else {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}
		}
	}
	
	private class ContactItemView{
		public ImageView photoView;
		public TextView nameView;
		public TextView numberView;
		public ImageView typeView;
		public TextView timeView;
		public TextView topDateView;
		
	}
	
	protected class ContactsAdapter extends ResourceCursorAdapter {

		private ImageLoader mImageLoader;
		
        public ContactsAdapter(Context context) {
			super(context, R.layout.call_log_list_item_layout, null);
			mImageLoader = new ImageLoader(context, ImageLoader.TYPE_CALL_LOG);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = super.newView(context, cursor, parent);
			final ContactItemView cache = new ContactItemView();
			cache.nameView = (TextView) view.findViewById(R.id.call_log_name_view);
			cache.photoView = (ImageView) view.findViewById(R.id.call_log_photo_view);
			cache.numberView = (TextView) view.findViewById(R.id.call_log_number_view);
			cache.timeView = (TextView) view.findViewById(R.id.call_log_time_view);
			cache.typeView = (ImageView) view.findViewById(R.id.call_log_type_image_view);
			cache.topDateView = (TextView) view.findViewById(R.id.top_date_view);
			view.setTag(cache);
			
			return view;
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			if(cursor == null || cursor.isClosed()){
				return;
			}
			final ContactItemView cache = (ContactItemView) view.getTag();
			CallLogInfo callInfo = CallLogUtils.readCallLogInfoByCursor(cursor);
			if(callInfo == null){
				return;
			}
			cache.nameView.setText(TextUtils.isEmpty(callInfo.name) ? callInfo.number : callInfo.name);
			cache.numberView.setText(TextUtils.isEmpty(callInfo.name) ? context.getString(R.string.unsaved) : callInfo.number);
			if(mDateTimeMap.containsKey(callInfo._id) && mDateTimeMap.get(callInfo._id)){
				cache.topDateView.setVisibility(View.VISIBLE);
				cache.topDateView.setText(DateTimeUtils.getDateWeekLabel(callInfo.date));
				if(DateTimeUtils.getDateLabel(callInfo.date).equals(DateTimeUtils.getDateLabel(System.currentTimeMillis()))){
					cache.topDateView.setText(R.string.today);
				}
			} else {
				cache.topDateView.setVisibility(View.GONE);
			}
			cache.timeView.setText(DateTimeUtils.getTimeLabel(callInfo.date));
			if(callInfo.type == CallLog.Calls.INCOMING_TYPE){
				cache.typeView.setImageResource(R.drawable.icon_history_incoming);
			} else if (callInfo.type == CallLog.Calls.MISSED_TYPE){
				cache.typeView.setImageResource(R.drawable.icon_history_missed);
			} else if (callInfo.type == CallLog.Calls.OUTGOING_TYPE){
				cache.typeView.setImageResource(R.drawable.icon_history_outbound);
			} else {
				cache.typeView.setImageResource(R.drawable.icon_history_cancel);
			}
			
			
			if(TextUtils.isEmpty(callInfo.number)){
				cache.photoView.setImageResource(R.drawable.icon_contact_list_default_round_photo);
				return;
			}
			mImageLoader.displayImage(callInfo.number, cache.photoView);
			
		}
	}

	private OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
			
			String number = cursor.getString(CallLogUtils.NUMBER_INDEX);
			if(LogLevel.DEV){
				DevLog.d(TAG, "onListItemClick number : " + number);
			}
			switchCallLogInfo(number);
		}
	};
	private OnItemLongClickListener	onItemLongClickListener = new AdapterView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			if(mContactsAdapter == null){
				if(LogLevel.MARKET){
					MarketLog.e(TAG, "onItemLongClickListener mContactsAdapter = null");
				}
				return false;
			}
			
			Cursor cursor = (Cursor) mContactsAdapter.getItem(position);
			if (cursor == null) {
				return false;
			}
			showLongClickDialog(cursor);
			return false;
		}
	};
	
	private void showLongClickDialog(final Cursor cursor){
		final CallLogInfo callLogInfo = CallLogUtils.readCallLogInfoByCursor(cursor);
		if(callLogInfo == null){
			return;
		}
		
		ContactBinding binding = ContactsUtils.bindContactByNumber(getActivity(), callLogInfo.number);
		final long contactId = ContactsUtils.lookupContactIDByPhoneNumber(callLogInfo.number, getActivity());
		List<EmailContact> mailList = ContactsUtils.getEmailAddresses(getActivity(), contactId);
		final Uri personUri = ContentUris.withAppendedId(ContactsUtils.Uri_People, contactId);
		
		if(LogLevel.DEV){
			DevLog.d(TAG, "showLongClickDialog callLogInfo : " + callLogInfo.toString());
		}
		
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_long_click_contact_layout, null);
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(getActivity());
		builder.setIcon(R.drawable.icon_dialog_title_for_menu);
		builder.setTitle(TextUtils.isEmpty(callLogInfo.name) ? callLogInfo.number : callLogInfo.name);
		builder.setContentView(v);
		
		final QuickCallAlertDialog dialog = builder.create();
		Button callBtn = (Button) dialog.findViewById(R.id.button_call_contact);
		Button sendMessageBtn = (Button) dialog.findViewById(R.id.button_send_message);
		Button viewContactBtn = (Button) dialog.findViewById(R.id.button_view_contact);
		Button sendEmailBtn = (Button) dialog.findViewById(R.id.button_send_email);
		Button editBtn = (Button) dialog.findViewById(R.id.button_edit_contact);
		Button buttonCreate = (Button) dialog.findViewById(R.id.button_create_new_contact);
		Button buttonAdd = (Button) dialog.findViewById(R.id.button_add_to_contact);
		Button deleteBtn = (Button) dialog.findViewById(R.id.button_delete_contact);
		Button createQuickCallBtn = (Button) dialog.findViewById(R.id.button_create_new_quick_call);
		
		callBtn.setVisibility(View.VISIBLE);
		sendMessageBtn.setVisibility(View.VISIBLE);
		callBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				DialUtils.callNumber(getActivity(), callLogInfo.number);
			}
		});
		
		sendMessageBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				DialUtils.sendMessage(getActivity(), callLogInfo.number);
			}
		});
		if(binding.hasContact){
			viewContactBtn.setVisibility(View.VISIBLE);
			editBtn.setVisibility(View.VISIBLE);
			buttonCreate.setVisibility(View.GONE);
			buttonAdd.setVisibility(View.GONE);
		} else {
			editBtn.setVisibility(View.GONE);
			viewContactBtn.setVisibility(View.GONE);
			buttonCreate.setVisibility(View.VISIBLE);
			buttonAdd.setVisibility(View.VISIBLE);
		}
		
		if(mailList.size() > 0){
			sendEmailBtn.setVisibility(View.VISIBLE);
			sendEmailBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					sendPersonalEmail(cursor, contactId);
				}
			});
			
		} else {
			sendEmailBtn.setVisibility(View.GONE);
		}
		
		viewContactBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(LogLevel.DEV){
					DevLog.d(TAG, "view contact contactId : " + contactId);
				}
				switchContactInfo(contactId);
			}
		});
		
		editBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				editContact(personUri);
			}
		});
		
		buttonCreate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(LogLevel.DEV){
					DevLog.d(TAG, "user click create new contact.");
				}
				try {
					startActivity(ContactsUtils.getCreateNewContactIntent(getActivity(), null, callLogInfo.number));
				} catch (ActivityNotFoundException e) {
					if (LogLevel.MARKET) {
						MarketLog.e(TAG, "create new contact start activity error: " + e.toString());
					}
				}
			}
		});
		
		buttonAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(LogLevel.DEV){
					DevLog.d(TAG, "user click add to existing contact.");
				}
				try {
					startActivity(ContactsUtils.getAddToContactIntent(getActivity(), callLogInfo.number));
				} catch (ActivityNotFoundException e) {
					if (LogLevel.MARKET) {
						MarketLog.e(TAG, "add to exsiting contact start activity error: " + e.toString());
					}
				}
			}
		});
		
		deleteBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				showDeleteDialog(callLogInfo._id);
			}
		});
		
		createQuickCallBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(QuickCallUtils.hasQuickTraceSet(getActivity(), callLogInfo.number)){
					QuickCallInfo info = QuickCallUtils.getQuickCallInfoByNumber(getActivity(), callLogInfo.number);
					if(info != null){
						Toast.makeText(getActivity(), R.string.this_number_has_been_set, Toast.LENGTH_LONG).show();
						Intent intent = new Intent(getActivity(), QuickCallChangeActivity.class);
						intent.putExtra(QuickCallConstants.EXTRA_SELECT_QUICK_CALL_ID, info._id);
						getActivity().startActivity(intent);
						return;
					}
				} 
				
				Intent intent = new Intent(getActivity(), CreateQuickCallActivity.class);
				intent.putExtra(QuickCallConstants.EXTRA_SELECT_NAME, callLogInfo.name);
				intent.putExtra(QuickCallConstants.EXTRA_SELECT_NUMBER, callLogInfo.number);
				long contactId = ContactsUtils.lookupContactIDByPhoneNumber(callLogInfo.number, getActivity());
				ContactInfo contactInfo = ContactsUtils.getContactInfoByContactId(getActivity(), contactId);
				intent.putExtra(QuickCallConstants.EXTRA_SELECT_PHOTO_ID, contactInfo.photoId);
				getActivity().startActivity(intent);
			}
		});
		
		dialog.show();
		DialogUtils.setDialogWidth(getActivity(), dialog);
	}
	
	private boolean sendPersonalEmail(Cursor cursor, long personId) {
		if (personId == -1) {
			return false;
		}

		List<EmailContact> emails = ContactsUtils.getEmailAddresses(getActivity(), personId);
		if (emails.size() == 0) {
			return false;
		} else if (emails.size() == 1) {
			EmailSender.sendEmail(getActivity(), new String[]{emails.get(0).emailAddress}, null, null);
		} else {
			EmailSelectorDialog emailDialog = new EmailSelectorDialog(getActivity(), emails);
			emailDialog.show();
		}

		return true;
	}
	
	private void editContact(final Uri personUri) {
		Intent intent = new Intent(Intent.ACTION_EDIT, personUri);
		try {
			startActivityForResult(Intent.createChooser(intent, getString(R.string.menu_editContact)), EDIT_CONTACT_REQUEST);
		} catch (android.content.ActivityNotFoundException e) {
			if (LogLevel.MARKET) {
				MarketLog.e(TAG, "editItem(): " + e.getMessage());
			}
		}
	}
	
	private void showDeleteDialog(final long _id){
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(getActivity());
		builder.setIcon(R.drawable.icon_dialog_title);
		builder.setTitle(R.string.delete_contact);
		builder.setMessage(R.string.delete_contact_message);
		builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				try {
					CallLogUtils.deleteSingleCallLog(getActivity(), _id);
					startEmptyQuery();
				} catch (SQLiteException e) {
					if (LogLevel.DEV) {
						DevLog.e(TAG, "Error deleting calllog.", e);
					} else if (LogLevel.MARKET) {
						MarketLog.e(TAG, "Cannot delete calllog: SQLite error.");
					}
					DialogUtils.showQuickCallAlertDialog(getActivity(), R.string.delete_contact_failed_title, R.string.delete_contact_failed);
				}
			}
		});
		builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		QuickCallAlertDialog dialog = builder.create();
		dialog.show();
		DialogUtils.setDialogWidth(getActivity(), dialog);
	}
		
	private void switchCallLogInfo(String number){
		Intent intent = new Intent(getActivity(), CallLogInfoActivity.class);
		intent.putExtra(QuickCallConstants.EXTRA_CALL_LOG_NUMBER, number);
		getActivity().startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	private void switchContactInfo(long personId){
		Intent intent = new Intent(getActivity(), ContactInfoActivity.class);
		intent.putExtra(QuickCallConstants.EXTRA_CONTACT_PERSON_ID, personId);
		getActivity().startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
}
