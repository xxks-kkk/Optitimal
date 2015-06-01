package edu.wisc.cs.optitimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.cs.wisc.optitimal.R;
import edu.wisc.cs.optitimal.LineChartActivity.CustomOnItemSelectedListener;

import org.achartengine.model.SeriesSelection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.support.v4.app.FragmentActivity;

public class PieChartActivity extends FragmentActivity {

	private Cursor reminder;

	long[] pieChartValues= new long[3];

	public static final String TYPE = "type";

	private SharedPreferences settings;

	// indicate if there is data in the db, used by PieChartFragment
	public static int isData = 1;

	public Spinner spinner3;

	public final static String LOGTAG = "PieChartActivity";

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
		reminder = getContentResolver().query(ReminderProvider.CONTENT_URI, null, null, null, null);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pie_chart_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		settings = PreferenceManager.getDefaultSharedPreferences(this);

		//addItemsOnSpinner3();
//		addListenerOnSpinnerItemSelection();

		android.app.Fragment fragment = getFragmentManager().findFragmentByTag(
				PieChartFragment.DEFAULT_PIE_CHART_FRAGMENT_TAG);

		if (fragment == null) {
			android.app.FragmentManager fragmentManager = getFragmentManager();
			android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			fragment = new PieChartFragment();

			fragmentTransaction.add(R.id.plotting_pie, fragment, PieChartFragment.DEFAULT_PIE_CHART_FRAGMENT_TAG);
			fragmentTransaction.commit();
		}
		
		mNextButton = (ImageButton)findViewById(R.id.next_week_pie);
		mPrevButton = (ImageButton)findViewById(R.id.prev_week_pie);
		textView = (TextView)findViewById(R.id.pieChart_textView);

		Calendar current = Calendar.getInstance();

		dayOfWeek = current.get(Calendar.DAY_OF_WEEK);
		startRange = current.getTimeInMillis() - (long) dayOfWeek * 1000*60*60*24;
		endRange = current.getTimeInMillis() + (long)(7-dayOfWeek)*1000*60*60*24;

		mNextButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				weekNum ++;
				PieChartFragment fragment = (PieChartFragment) getFragmentManager().findFragmentByTag(PieChartFragment.DEFAULT_PIE_CHART_FRAGMENT_TAG);
				fragment.setWeekNum(weekNum);
				fragment.getData(reminder);
				fragment.fillPieChart();
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
				PieChartFragment fragment = (PieChartFragment) getFragmentManager().findFragmentByTag(PieChartFragment.DEFAULT_PIE_CHART_FRAGMENT_TAG);
				fragment.setWeekNum(weekNum);
				fragment.getData(reminder);
				fragment.fillPieChart();
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


//	public void addItemsOnSpinner3(){
//
//		spinner3 = (Spinner) findViewById(R.id.spinner3);
//
//		List<String> list = new ArrayList<String>();
//		list.add("Week");
//		list.add("Month");
//
//		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_spinner_item, list);
//		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spinner3.setAdapter(dataAdapter);
//	}
//
//	public void addListenerOnSpinnerItemSelection(){
//		spinner3.setOnItemSelectedListener(new CustomOnItemSelectedListener());
//	}

	public class CustomOnItemSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {


		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isData == 1){
			final PieChartFragment fragment = (PieChartFragment) getFragmentManager().findFragmentByTag(PieChartFragment.DEFAULT_PIE_CHART_FRAGMENT_TAG);
			fragment.getGraphicView().setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = fragment.getGraphicView().getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						Toast.makeText(PieChartActivity.this, "No chart element selected", Toast.LENGTH_SHORT)
						.show();
					} else {
						for (int i = 0; i < fragment.getCategorySeries().getItemCount(); i++) {
							fragment.getDefaultRenderer().getSeriesRendererAt(i).setHighlighted(i == seriesSelection.getPointIndex());
						}
						fragment.getGraphicView().repaint();
						//	            Toast.makeText(
						//	                    PieChartActivity.this,
						//	                    "Chart data point index " + seriesSelection.getPointIndex() + " selected"
						//	                        + " point value=" + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
						//	              }
						//  Log.d("!!!!", Integer.toString((seriesSelection.getPointIndex())));
						Toast.makeText(
								PieChartActivity.this,
								"In total, you spent " + seriesSelection.getValue() + " minutes on the activity " + fragment.getCategorySeries().getCategory(seriesSelection.getPointIndex()), Toast.LENGTH_SHORT).show();
					}
				}
			});
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
	}
}

