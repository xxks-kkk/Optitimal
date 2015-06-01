package edu.wisc.cs.optitimal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import edu.cs.wisc.optitimal.R;
import edu.cs.wisc.optitimal.R.string;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

public class PieChartFragment extends Fragment {

	public static final String LOGTAG = "PieChartFragment";

	public static final String DEFAULT_PIE_CHART_FRAGMENT_TAG = "pieChartFragmentTag";

	private Cursor reminder;

	private final String CATEGORIES = "Preference Category";

	long[] pieChartValues;

	List<String> categories;

	long total = 0;

	private SharedPreferences settings;

	public static final String TYPE = "type";

	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.YELLOW, Color.DKGRAY,Color.RED};

	private CategorySeries mSeries = new CategorySeries("title");

	private DefaultRenderer mRenderer = new DefaultRenderer();

	private GraphicalView mChartView;

	private LinearLayout layout;

	private long earliest;

	private long lastest;

	private int weekNum;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		reminder = getActivity().getContentResolver().query(ReminderProvider.CONTENT_URI, null, null, null, null);
		settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pie_chart, container, false);

		categories = getCategories();

		if(categories.size() == 0){
			Toast.makeText(getActivity(), 
					"No category is selected",
					Toast.LENGTH_LONG).show();
		}

		if(reminder.getCount() == 0){
			PieChartActivity.isData = 0;
			Toast.makeText(getActivity(), 
					"No data in this week has been entered yet",
					Toast.LENGTH_SHORT).show();
			return v;
		}

		pieChartValues = new long[categories.size()];
		initChart();

		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) v.findViewById(R.id.PieChart);
			mChartView = ChartFactory.getPieChartView(getActivity(), mSeries, mRenderer);
			layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}

		getData(reminder);
		fillPieChart();

		return v;
	}

	public List<String> getCategories(){
		Set<String> defaultString = new HashSet<String>();
		Set<String> list2 = settings.getStringSet(CATEGORIES, defaultString);

		Log.d(LOGTAG, "list2: " + list2.toString());

		List<String> list = new ArrayList<String>();
		for(String i: list2){
			list.add(i);
		}

		return list;
	}

	public void initChart(){
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setLabelsTextSize(15);
		mRenderer.setLegendTextSize(15);
		mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setStartAngle(90);
		mRenderer.setLabelsColor(Color.WHITE);
		mRenderer.setFitLegend(true);
		mRenderer.setClickEnabled(true);
		mRenderer.setSelectableBuffer(10);
	}

	public void fillPieChart(){
		//for(int i=0; i<pieCha 	rtValues.length;i++)
		for (int i = 0; i < pieChartValues.length; i++)
		{
			//mSeries.add(" Share Holder " + (mSeries.getItemCount() + 1), pieChartValues[i]);
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			//renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
			renderer.setColor(COLORS[i]);
			mRenderer.addSeriesRenderer(renderer);
			if (mChartView != null)
				mChartView.repaint();     
		}
	}

	public void getData(Cursor reminder){
		//		long study = 0;
		//		long sports = 0;
		//		long hangOut = 0;

		//reminder.moveToFirst();
		reminder = getActivity().getContentResolver().query(ReminderProvider.CONTENT_URI, null, null, null, null);
		for (int i = 0 ; i < pieChartValues.length; i ++){
			pieChartValues[i] = 0;
		}
		if(reminder.getCount() > 0){
			while(reminder.moveToNext()){
				String category = reminder.getString(reminder.getColumnIndex(ReminderProvider.COLUMN_CATEGORY));
				Long startTime = reminder.getLong(reminder.getColumnIndex(ReminderProvider.COLUMN_START_DATE_TIME));					
				Long endTime = reminder.getLong(reminder.getColumnIndex(ReminderProvider.COLUMN_END_DATE_TIME));
				Long timeDiff = (endTime - startTime)/60000;

				if (isDateInCurrectWeek(startTime) || isDateInCurrectWeek(endTime)){
					for(int i = 0; i < categories.size(); i++){
						if (category.equals(categories.get(i))){
							pieChartValues[i] += timeDiff;
						}
					}
				}


			}

		}


		mSeries.clear();
		boolean flag = false;
		for (int j = 0; j < pieChartValues.length; j ++){
			if (pieChartValues[j] != 0){
				flag = true;
				break;
			}
		}
		if (!flag){
			Toast.makeText(getActivity(), 
					"No data has been entered yet",
					Toast.LENGTH_SHORT).show();
		}else{

			for (int i = 0; i < categories.size(); i++){			
				mSeries.add(categories.get(i), pieChartValues[i]);
			}
		}
	}

	public GraphicalView getGraphicView(){
		return mChartView;
	}

	public CategorySeries getCategorySeries(){
		return mSeries;
	}

	public DefaultRenderer getDefaultRenderer(){
		return mRenderer;
	}

	public void setWeekNum (int weekNum){
		this.weekNum = weekNum;
	}

	private boolean isDateInCurrectWeek(Long millisec){
		Calendar currentCalendar = Calendar.getInstance();
		int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
		week += weekNum;
		int year = currentCalendar.get(Calendar.YEAR);

		Calendar targetCalendar = Calendar.getInstance();
		targetCalendar.setTimeInMillis(millisec);
		int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
		int targetYear = targetCalendar.get(Calendar.YEAR);
		return week == targetWeek && year == targetYear;
	}
}
