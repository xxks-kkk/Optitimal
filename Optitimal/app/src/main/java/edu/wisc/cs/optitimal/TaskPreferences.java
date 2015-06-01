package edu.wisc.cs.optitimal;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnCreateContextMenuListener;
import edu.cs.wisc.optitimal.R;

public class TaskPreferences extends PreferenceActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.task_preferences);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Set the time default to a numeric number only
		EditTextPreference timeDefault = (EditTextPreference) findPreference(getString(R.string.pref_default_time_from_now_key));
		timeDefault.getEditText() .setKeyListener(
				DigitsKeyListener.getInstance());

		//Set Categories
		// MultiSelectListPreference category = (MultiSelectListPreference) findPreference(getString(R.array.pref_upload_catrgories));

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent parentActivityIntent = new Intent(this, ReminderListActivity.class);
			parentActivityIntent.addFlags(
					Intent.FLAG_ACTIVITY_CLEAR_TOP |
					Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		case R.id.menu_insert:
			Intent intent = new Intent(this, ReminderEditActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_settings:
			if (!(this instanceof TaskPreferences)){
				startActivity(new Intent(this, TaskPreferences.class));
			}
			return true;
			//TODO: modified here
		case R.id.menu_plot:
			startActivity(new Intent(this, ChartSelectionDialogFragmentWrapper.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
