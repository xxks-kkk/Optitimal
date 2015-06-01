package edu.wisc.cs.optitimal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import edu.cs.wisc.optitimal.R;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class LineChartFragment extends Fragment {

	private final String CATEGORIES = "Preference Category";

	private SharedPreferences settings;

	private Cursor reminder;

	public static final String DEFAULT_LINE_CHART_FRAGMENT_TAG = "lineChartFragmentTag";

	private static final String LOGTAG = "LineChartFragment";

	private GraphicalView mChart;

	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

	private XYSeries mCurrentSeries;

	private XYSeriesRenderer mCurrentRenderer;

	private String selectedCategory;

	private LinearLayout layout;

	private long earliest;

	private long lastest;

	private int weekNum;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		reminder = getActivity().getContentResolver().query(ReminderProvider.CONTENT_URI, null, null, null, null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.line_chart, container, false);

		layout = (LinearLayout) v.findViewById(R.id.LineChart);
		Log.d(LOGTAG, "get here");
		if (mChart == null) {
			initChart();
			addSampleData();
			mChart = ChartFactory.getCubeLineChartView(getActivity(), mDataset, mRenderer, 0.3f);
			layout.addView(mChart);
		} else{
			Log.d(LOGTAG, "get the else part in the onResume()");
			mChart.repaint();
		}
		return v;
	}

	private void initChart() {
		mCurrentSeries = new XYSeries("Time Usage");
		mDataset.addSeries(mCurrentSeries);
		mCurrentRenderer = new XYSeriesRenderer();
		mCurrentRenderer.setColor(Color.GREEN);
		mRenderer.addSeriesRenderer(mCurrentRenderer);
		mRenderer.setShowLegend(true);
		mRenderer.setXLabelsAngle(270);
		mRenderer.setMargins(new int[] { 25,35, 35, 15 });
		mRenderer.setYLabelsAlign(Align.LEFT);
		mRenderer.setXLabelsAlign(Align.RIGHT);
		mRenderer.setScale(7);

		mRenderer.addXTextLabel(0,"Sunday		");
		mRenderer.addXTextLabel(1,"Monday		");
		mRenderer.addXTextLabel(2,"Tuesday		");
		mRenderer.addXTextLabel(3,"Wednesday	");
		mRenderer.addXTextLabel(4,"Thursday		");
		mRenderer.addXTextLabel(5,"Friday		");
		mRenderer.addXTextLabel(6,"Saturday		");
		mRenderer.setYTitle("Time in minutes");
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setYAxisMin(0);
		//mRenderer.setDisplayValues(true);
	}

	public void addSampleData() {

		mCurrentSeries.clear();

		long[] timeDiff = getData(reminder);
		//List<Long> timeDiff = getData(reminder);
		List<Long> debug = new ArrayList<Long>();
		for(Long i: timeDiff) debug.add(i);
		Log.d(LOGTAG, "debug: "+ debug.toString());
		//		for(int i = 1; i < timeDiff.size(); i++){
		//			mCurrentSeries.add(i, timeDiff.get(i));
		//		}

		for(int i = 0; i < timeDiff.length; i++){
			mCurrentSeries.add(i, timeDiff[i]);
		}

	}


	/*
	 * Find the earliest & the lastest date in the data base 
	 * 
	 * */
	//	public int findScale(){
	//		//		Calendar earliest = Calendar.getInstance();
	//		//		Calendar lastest = Calendar.getInstance();
	//
	//
	//
	//		reminder.moveToFirst();
	//
	//		if(reminder.getCount() > 0){
	//			String category = reminder.getString(reminder.getColumnIndex(ReminderProvider.COLUMN_CATEGORY));
	//			if (category.equals(selectedCategory)){
	//				Long startTime = reminder.getLong(reminder.getColumnIndex(ReminderProvider.COLUMN_START_DATE_TIME));
	//				earliest = startTime;
	//				lastest = startTime;
	//			}
	//		}
	//		reminder.moveToNext();		
	//
	//		if(reminder.getCount() > 1){
	//			while(reminder.moveToNext()){
	//				String category = reminder.getString(reminder.getColumnIndex(ReminderProvider.COLUMN_CATEGORY));
	//				if (category.equals(selectedCategory)){
	//					Long startTime = reminder.getLong(reminder.getColumnIndex(ReminderProvider.COLUMN_START_DATE_TIME));
	//					//Compare and get the earliest time
	//					if (earliest > startTime){
	//						earliest = startTime;
	//					}					
	//					//Compare and get the lastest time
	//					if (lastest < startTime){
	//						lastest = startTime;
	//					}
	//
	//				}
	//			}
	//		}
	//
	//		long diffInMillisec =lastest - earliest;
	//		int diffInDay = (int) (diffInMillisec / (1000*60*60*24)) + 1;return diffInDay;
	//	}

	/**
	 * query the database for the COLUMN_START_DATE_TIME and COLUMN_END_DATE_TIME column,
	 * take difference and put them into a list
	 * @return a list of long integers (minutes)
	 */
	//public List<Long> getData(Cursor reminder){
	public long[] getData(Cursor reminder){
		//List<Long> timeDiff = new ArrayList<Long>(8);
		long[] timeDiff = new long[7];
		Log.i(LOGTAG, "Returned " + reminder.getCount() + " rows");

		//reminder.moveToFirst();
		reminder = getActivity().getContentResolver().query(ReminderProvider.CONTENT_URI, null, null, null, null);

		if(reminder.getCount() > 0){
			while(reminder.moveToNext()){
				String category = reminder.getString(reminder.getColumnIndex(ReminderProvider.COLUMN_CATEGORY));
				if (category.equals(selectedCategory)){
					Long startTime = reminder.getLong(reminder.getColumnIndex(ReminderProvider.COLUMN_START_DATE_TIME));
					Long endTime = reminder.getLong(reminder.getColumnIndex(ReminderProvider.COLUMN_END_DATE_TIME));
					if (isDateInCurrectWeek(startTime) || isDateInCurrectWeek(endTime)){

						// For testing purpose
						Date startDate = new Date(startTime);
						int startDateOfWeek = startDate.getDay();

						Date endDate = new Date(endTime);
						int endDateOfWeek = endDate.getDay();

						//						int startDay = startDate.getDate();
						//						int endDay = endDate.getDate();
						//						int startMonth = startDate.getMonth();
						//						int endMonth = endDate.getMonth();
						long duration = (endTime - startTime) / (1000*60*60*24);
						//						
						//						int duration = (endMonth - startMonth) * endDate.get

						long diff =  (endTime - startTime)/60000;
						//long diff_avg = Math.round(diff/duration);
						long diff_avg = 0;
						
						if (duration == 0){
							diff_avg = diff;
						}else{
							diff_avg = Math.round(diff/duration);
						}

						if (isDateInCurrectWeek(startTime) && isDateInCurrectWeek(endTime)){

							Log.d("debug1", "...Start: " + startDateOfWeek + "...End: " + endDateOfWeek);
							for (int i = startDateOfWeek; i <= endDateOfWeek; i++){
								timeDiff[i] += diff_avg;
							}
						}else if (isDateInCurrectWeek(startTime)){

							Log.d("debug2", "...Start: " + startDateOfWeek + "...End: " + endDateOfWeek);
							for (int i = startDateOfWeek; i < 7 ; i++){
								timeDiff[i] += diff_avg;
							}
						}else if (isDateInCurrectWeek(endTime)){

							Log.d("debug3", "...Start: " + startDateOfWeek + "...End: " + endDateOfWeek);
							for (int i = 0; i <= endDateOfWeek; i++){
								timeDiff[i] += diff_avg;
							}
						}else if (isDateInDuration(startTime, endTime)){
							for (int i = 0; i < 7; i ++){
								timeDiff[i] += diff_avg;
							}
						}

						//timeDiff[index] += diff;
						//timeDiff.add(dateOfWeek, timeDiff.get(dateOfWeek) + diff);
					}

				}
			}
		}
		return timeDiff;
	}


	private boolean isDateInDuration (Long startDate, Long endDate){
		Calendar currentCalendar = Calendar.getInstance();
		int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
		week += weekNum;;
		int year = currentCalendar.get(Calendar.YEAR);

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTimeInMillis(startDate);
		int startWeek = startCalendar.get(Calendar.WEEK_OF_YEAR);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTimeInMillis(endDate);
		int endWeek = endCalendar.get(Calendar.WEEK_OF_YEAR);

		if ( startWeek<= week && week <= endWeek){
			return true;
		}
		return false;

	}




	private boolean isDateInCurrectWeek(Long millisec){
		Calendar currentCalendar = Calendar.getInstance();
		int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
		week += weekNum;
		Log.d(LOGTAG, "......isData week: " +  week);
		int year = currentCalendar.get(Calendar.YEAR);

		Calendar targetCalendar = Calendar.getInstance();
		targetCalendar.setTimeInMillis(millisec);
		int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
		int targetYear = targetCalendar.get(Calendar.YEAR);
		return week == targetWeek && year == targetYear;
	}

	public void setSelectedCategory(String selectedCategory){
		this.selectedCategory = selectedCategory;
	}

	public void setWeekNum (int weekNum){
		this.weekNum = weekNum;
	}

	public GraphicalView getGraphicView(){
		return mChart;
	}
}
