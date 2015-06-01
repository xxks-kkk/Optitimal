package edu.wisc.cs.optitimal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import edu.cs.wisc.optitimal.R;

public class ReminderEditActivity extends FragmentActivity implements
OnFinishEditor {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminder_edit_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		Fragment fragment = getSupportFragmentManager().findFragmentByTag(
				ReminderEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);

		if (fragment == null) {
			fragment = new ReminderEditFragment();
			Bundle args = new Bundle();
			args.putLong(ReminderProvider.COLUMN_ROWID, getIntent()
					.getLongExtra(ReminderProvider.COLUMN_ROWID, 0L));
			fragment.setArguments(args);

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.add(R.id.edit_container, fragment,
					ReminderEditFragment.DEFAULT_EDIT_FRAGMENT_TAG);
			transaction.commit();
		}
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
			if (!(this instanceof ReminderEditActivity)){

				Intent intent = new Intent(this, ReminderEditActivity.class);
				startActivity(intent);
			}
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(this, TaskPreferences.class));
			return true;
			//TODO: modified here
		case R.id.menu_plot:
			android.support.v4.app.DialogFragment fragment = new ChartSelectionDialogFragment();
			android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
			fragment.show(fragmentManager, ChartSelectionDialogFragment.DEFAULT_DIALOG_FRAGMENT_TAG);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finishEditor() {
		finish();
	}
}
