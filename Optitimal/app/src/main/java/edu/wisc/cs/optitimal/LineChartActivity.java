package edu.wisc.cs.optitimal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.achartengine.model.SeriesSelection;

import edu.cs.wisc.optitimal.R;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * do the plotting
 * @author zeyuan
 *
 */
public class LineChartActivity extends FragmentActivity{

	private final String CATEGORIES = "Preference Category";

	private SharedPreferences settings;

	private static final String LOGTAG = "DataPlot";

	private LinearLayout layout;

	private Spinner spinner1;

//	private Spinner spinner2;

	private ImageButton mNextButton;

	private ImageButton mPrevButton;

	private int weekNum = 0;

	private Calendar startCalendar = Calendar.getInstance();

	private Calendar endCalendar = Calendar.getInstance();

	private TextView textView;

	private long startCurr;

	private int dayOfWeek;

	private long startRange;

	private long endRange;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.line_chart_activity);
		weekNum = 0;

		settings = PreferenceManager.getDefaultSharedPreferences(this);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		addItemsOnSpinner1();
		//		addItemsOnSpinner2();
		addListenerOnSpinnerItemSelection();
		android.app.Fragment fragment = getFragmentManager().findFragmentByTag(
				LineChartFragment.DEFAULT_LINE_CHART_FRAGMENT_TAG);


		if (fragment == null) {
			FragmentManager fragmentManager = getFragmentManager();
			android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			fragment = new LineChartFragment();

			fragmentTransaction.add(R.id.plotting, fragment, LineChartFragment.DEFAULT_LINE_CHART_FRAGMENT_TAG);
			fragmentTransaction.commit();
		}
		mNextButton = (ImageButton)findViewById(R.id.next_week);
		mPrevButton = (ImageButton)findViewById(R.id.prev_week);
		textView = (TextView)findViewById(R.id.lineChart_textView);

		Calendar current = Calendar.getInstance();

		dayOfWeek = current.get(Calendar.DAY_OF_WEEK);
		startRange = current.getTimeInMillis() - (long) dayOfWeek * 1000*60*60*24;
		endRange = current.getTimeInMillis() + (long)(7-dayOfWeek)*1000*60*60*24;
		//startCurr = startRange;



		mNextButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				weekNum ++;
				LineChartFragment fragment = (LineChartFragment) getFragmentManager().findFragmentByTag(LineChartFragment.DEFAULT_LINE_CHART_FRAGMENT_TAG);
				fragment.setWeekNum(weekNum);
				fragment.addSampleData();
				fragment.getGraphicView().repaint();

				if (weekNum == 0){
					textView.setText("Current Week");
				}else{

					startRange += (long) 1000*60*60*24*7 ;
					endRange += (long)1000*60*60*24*7;
							
					startCalendar.setTimeInMillis(startRange);
					endCalendar.setTimeInMillis(endRange);


					String text = startCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + 
							startCalendar.get(Calendar.DATE) + " - " +
							endCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + 
							endCalendar.get(Calendar.DATE);
					textView.setText(text);
				}

			}
		});

		mPrevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				weekNum --;
				LineChartFragment fragment = (LineChartFragment) getFragmentManager().findFragmentByTag(LineChartFragment.DEFAULT_LINE_CHART_FRAGMENT_TAG);
				fragment.setWeekNum(weekNum);
				fragment.addSampleData();
				fragment.getGraphicView().repaint();

				if (weekNum == 0){
					textView.setText("Current Week");
				}else{
					
					startRange -= (long) 1000*60*60*24*7 ;
					endRange -= (long)1000*60*60*24*7;
					
					startCalendar.setTimeInMillis(startRange);
					endCalendar.setTimeInMillis(endRange);


					String text = startCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + 
							startCalendar.get(Calendar.DATE) + " - " +
							endCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + 
							endCalendar.get(Calendar.DATE);
					textView.setText(text);

				}

			}
		});

	}




	public void addItemsOnSpinner1() {

		spinner1 = (Spinner) findViewById(R.id.spinner1);

		Set<String> defaultString = new HashSet<String>();
		Set<String> list2 = settings.getStringSet(CATEGORIES, defaultString);

		Log.d(LOGTAG, "list2: " + list2.toString());

		List<String> list = new ArrayList<String>();
		for(String i: list2){
			list.add(i);
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(dataAdapter);

	}

	//		public void addItemsOnSpinner2(){
	//	
	//			spinner2 = (Spinner) findViewById(R.id.spinner2);
	//	
	//			List<String> list = new ArrayList<String>();
	//			list.add("Current Week");
	//			list.add("Current Month");
	//			list.add()
	//	
	//			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
	//					android.R.layout.simple_spinner_item, list);
	//			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	//			spinner2.setAdapter(dataAdapter);
	//		}

	public void addListenerOnSpinnerItemSelection(){
		spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
		//		spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}

	public class CustomOnItemSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			Log.d(LOGTAG, "!!!!id: " + id);
			int spinnerID = parent.getId();
			if (spinnerID == R.id.spinner1){
				//TODO: Here might be a bug
				Log.d(LOGTAG, "Passing on onItemSelected for different pos: " + pos);
				LineChartFragment fragment = (LineChartFragment) getFragmentManager().findFragmentByTag(LineChartFragment.DEFAULT_LINE_CHART_FRAGMENT_TAG);
				fragment.setSelectedCategory(parent.getItemAtPosition(pos).toString());
				fragment.addSampleData();
				fragment.getGraphicView().repaint();
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	}

	//	public void getStartRange(){
	//		Calendar current = Calendar.getInstance();
	//		long startRange = current.getTimeInMillis();
	//		int dayOfWeek = current.get(Calendar.DAY_OF_WEEK);
	//		
	//		startRange += 
	//		
	//		return ;
	//	}



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
	}}
