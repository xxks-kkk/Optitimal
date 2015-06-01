package edu.wisc.cs.optitimal;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ChartSelectionDialogFragmentWrapper extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		android.support.v4.app.DialogFragment fragment = new ChartSelectionDialogFragment();
		android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
		fragment.show(fragmentManager, ChartSelectionDialogFragment.DEFAULT_DIALOG_FRAGMENT_TAG);
	}
}
