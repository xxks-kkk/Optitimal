package edu.wisc.cs.optitimal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import edu.cs.wisc.optitimal.R;

public class ReminderEditFragment extends Fragment implements
OnDateSetListener, OnTimeSetListener, LoaderCallbacks<Cursor> {
	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

	private final String LOGTAG = "ReminderEditFragment";
	private Spinner spinner2;

	private final String CATEGORIES = "Preference Category";

	private SharedPreferences settings;

	//
	// Dialog Constants
	//
	static final String YEAR = "year";
	static final String MONTH = "month";
	static final String DAY = "day";
	static final String HOUR = "hour";
	static final String MINS = "mins";
	static final String CALENDAR = "calendar";

	//
	// Date Format
	//
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String TIME_FORMAT = "kk:mm";

	private EditText mTitleText;
	private EditText mBodyText;
	private Button mStartDateButton;
	private Button mEndDateButton;
	private Button mStartTimeButton;
	private Button mEndTimeButton;
	private Button mConfirmButton;
	private long mRowId;
	private Calendar mCalendar; //used for startTime Calendar
	private Calendar mEndCalendar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// If we're restoring state from a previous activity, restore the
		// previous date as well, otherwise use now
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(CALENDAR)) {
			mCalendar = (Calendar) savedInstanceState.getSerializable(CALENDAR); //TODO
		} else {
			mCalendar = Calendar.getInstance();
			mEndCalendar = Calendar.getInstance();
		}

		Bundle arguments = getArguments();
		if (arguments != null) {
			mRowId = arguments.getLong(ReminderProvider.COLUMN_ROWID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.reminder_edit, container, false);

		mTitleText = (EditText) v.findViewById(R.id.title);
		mBodyText = (EditText) v.findViewById(R.id.body);
		mStartDateButton = (Button) v.findViewById(R.id.start_date);
		mEndDateButton = (Button) v.findViewById(R.id.end_date);

		mStartTimeButton = (Button) v.findViewById(R.id.start_time);
		mEndTimeButton = (Button) v.findViewById(R.id.end_time);

		mConfirmButton = (Button) v.findViewById(R.id.confirm);

		settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
		addItemsOnSpinner2(v);

		mStartDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showStartDatePicker();
			}
		});
		mEndDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showEndDatePicker();
			}
		});

		mStartTimeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showStartTimePicker();
			}
		});

		mEndTimeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showEndTimePicker();
			}
		});

		mConfirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mCalendar.getTimeInMillis() >= mEndCalendar.getTimeInMillis() ){
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage("The start time cannot be bigger or equal to the end time!")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}else{
					ContentValues values = new ContentValues();
					values.put(ReminderProvider.COLUMN_ROWID, mRowId);
					values.put(ReminderProvider.COLUMN_TITLE, mTitleText.getText()
							.toString());
					values.put(ReminderProvider.COLUMN_BODY, mBodyText.getText()
							.toString());
					values.put(ReminderProvider.COLUMN_START_DATE_TIME,
							mCalendar.getTimeInMillis());
					values.put(ReminderProvider.COLUMN_END_DATE_TIME, mEndCalendar.getTimeInMillis());
					//TODO: modified 05/04 (might not be the right place)
					values.put(ReminderProvider.COLUMN_CATEGORY, String.valueOf(spinner2.getSelectedItem()));

					if (mRowId == 0) {
						Uri itemUri = getActivity().getContentResolver().insert(
								ReminderProvider.CONTENT_URI, values);
						mRowId = ContentUris.parseId(itemUri);
					} else {
						int count = getActivity().getContentResolver().update(
								ContentUris.withAppendedId(
										ReminderProvider.CONTENT_URI, mRowId),
										values, null, null);
						if (count != 1)
							throw new IllegalStateException("Unable to update "
									+ mRowId);
					}

					Toast.makeText(getActivity(),
							getString(R.string.task_saved_message),
							Toast.LENGTH_SHORT).show();
					((OnFinishEditor) getActivity()).finishEditor();
					new ReminderManager(getActivity()).setReminder(mRowId,
							mCalendar);  //TODO
				}
			}

		});

		if (mRowId == 0) {
			// This is a new task - add defaults from preferences if set.
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			String defaultTitleKey = getString(R.string.pref_task_title_key);
			String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);

			String defaultTitle = prefs.getString(defaultTitleKey, null);
			String defaultTime = prefs.getString(defaultTimeKey, null);


			if (defaultTitle != null)
				mTitleText.setText(defaultTitle);

			if (defaultTime != null && defaultTime.length()>0 ){
				mCalendar.add(Calendar.MINUTE, Integer.parseInt(defaultTime));
				mEndCalendar.add(Calendar.MINUTE, Integer.parseInt(defaultTime));
			}
			updateButtons();

		} else {

			// Fire off a background loader to retrieve the data from the
			// database
			getLoaderManager().initLoader(0, null, this);

		}

		return v;
	}

	public void addItemsOnSpinner2(View v) {

		spinner2 = (Spinner) v.findViewById(R.id.spinner2);

		Set<String> defaultString = new HashSet<String>();
		Set<String> list2 = settings.getStringSet(CATEGORIES, defaultString);
		List<String> list = new ArrayList<String>();
		for(String i: list2){
			list.add(i);
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(dataAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the calendar instance in case the user changed it
		outState.putSerializable(CALENDAR, mCalendar); //TODO
	}

	private void showStartDatePicker() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		DialogFragment newFragment = new DatePickerDialogFragment();
		Bundle args = new Bundle();
		args.putInt(YEAR, mCalendar.get(Calendar.YEAR));
		args.putInt(MONTH, mCalendar.get(Calendar.MONTH));
		args.putInt(DAY, mCalendar.get(Calendar.DAY_OF_MONTH));
		newFragment.setArguments(args);
		newFragment.show(ft, "startDatePicker");
	}

	private void showEndDatePicker() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		DialogFragment newFragment = new DatePickerDialogFragment();
		Bundle args = new Bundle();
		args.putInt(YEAR, mEndCalendar.get(Calendar.YEAR));
		args.putInt(MONTH, mEndCalendar.get(Calendar.MONTH));
		args.putInt(DAY, mEndCalendar.get(Calendar.DAY_OF_MONTH));
		newFragment.setArguments(args);
		newFragment.show(ft, "endDatePicker");
	}

	private void showStartTimePicker() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		DialogFragment newFragment = new TimePickerDialogFragment();
		Bundle args = new Bundle();
		args.putInt(HOUR, mCalendar.get(Calendar.HOUR_OF_DAY));
		args.putInt(MINS, mCalendar.get(Calendar.MINUTE));
		newFragment.setArguments(args);
		newFragment.show(ft, "startTimePicker");
	}

	private void showEndTimePicker() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		DialogFragment newFragment = new TimePickerDialogFragment();
		Bundle args = new Bundle();
		args.putInt(HOUR, mEndCalendar.get(Calendar.HOUR_OF_DAY));
		args.putInt(MINS, mEndCalendar.get(Calendar.MINUTE));
		newFragment.setArguments(args);
		newFragment.show(ft, "endTimePicker");
	}

	private void updateButtons() {
		// Set the time button text
		SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
		String startTimeForButton = timeFormat.format(mCalendar.getTime());
		String endTimeForButton = timeFormat.format(mEndCalendar.getTime());
		mStartTimeButton.setText(startTimeForButton);
		mEndTimeButton.setText(endTimeForButton);

		// Set the date button text
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		String dateForButton = dateFormat.format(mCalendar.getTime());
		String endDateForButton = dateFormat.format(mEndCalendar.getTime());
		mStartDateButton.setText(dateForButton);
		mEndDateButton.setText(endDateForButton);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		FragmentManager fragmanager = getFragmentManager();
		if(fragmanager.findFragmentByTag("startDatePicker") != null) {
			mCalendar.set(Calendar.YEAR, year);
			mCalendar.set(Calendar.MONTH, monthOfYear);
			mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		}
		if(fragmanager.findFragmentByTag("endDatePicker") != null) {
			mEndCalendar.set(Calendar.YEAR, year);
			mEndCalendar.set(Calendar.MONTH, monthOfYear);
			mEndCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		} 
		updateButtons();
	}

	@Override
	public void onTimeSet(TimePicker view, int hour, int minute) {
		FragmentManager fragmanager = getFragmentManager();

		if(fragmanager.findFragmentByTag("startTimePicker") != null) {
			mCalendar.set(Calendar.HOUR_OF_DAY, hour);
			mCalendar.set(Calendar.MINUTE, minute);
		}
		if(fragmanager.findFragmentByTag("endTimePicker") != null) {
			mEndCalendar.set(Calendar.HOUR_OF_DAY, hour);
			mEndCalendar.set(Calendar.MINUTE, minute);
		}


		updateButtons();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), ContentUris.withAppendedId(
				ReminderProvider.CONTENT_URI, mRowId), null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor reminder) {
		// Close this fragment down if the item we're editing was deleted
		if (reminder.getCount() == 0) {
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					((OnFinishEditor) getActivity()).finishEditor();
				}
			});
			return;
		}

		mTitleText.setText(reminder.getString(reminder
				.getColumnIndexOrThrow(ReminderProvider.COLUMN_TITLE)));
		mBodyText.setText(reminder.getString(reminder
				.getColumnIndexOrThrow(ReminderProvider.COLUMN_BODY)));

		//here we load user spinner choice from database
		ArrayAdapter myAdap = (ArrayAdapter) spinner2.getAdapter();
		int pos = myAdap.getPosition(reminder.getString(reminder.getColumnIndexOrThrow(ReminderProvider.COLUMN_CATEGORY)));
		spinner2.setSelection(pos);

		// Get the date from the database
		Long dateInMillis = reminder.getLong(reminder
				.getColumnIndexOrThrow(ReminderProvider.COLUMN_START_DATE_TIME));


		Date date = new Date(dateInMillis);
		mCalendar.setTime(date);//TODO

		Long dateInMills2 = reminder.getLong(reminder.getColumnIndexOrThrow(ReminderProvider.COLUMN_END_DATE_TIME));

		Date date2 = new Date(dateInMills2);
		mEndCalendar.setTime(date2); //TODO

		updateButtons();

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// nothing to reset for this fragment
	}
}
