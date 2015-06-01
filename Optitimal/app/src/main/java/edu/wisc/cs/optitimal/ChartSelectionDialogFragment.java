package edu.wisc.cs.optitimal;

import edu.cs.wisc.optitimal.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class ChartSelectionDialogFragment extends DialogFragment {

	public static final String DEFAULT_DIALOG_FRAGMENT_TAG = "ChartSelectionDialogFragmentTag";
	int selectedItem;
	public static final String LOGTAG = "ChartSelectionDialogFragment";
	int defaultSelection = 0;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.select);

		builder.setSingleChoiceItems(R.array.chart_type, defaultSelection, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int pos) {
				Log.d(LOGTAG,"pos: " + pos);
				selectedItem = pos;
			}
		});

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			Activity act1 = getActivity();

			@Override
			public void onClick(DialogInterface dialog, int id) {
				if (selectedItem == 1){
					if (!(act1 instanceof LineChartActivity)) {
						Intent intent = new Intent(getActivity(), LineChartActivity.class);
						startActivity(intent);
					}
				}
				else if (selectedItem == 0){
					if (!(act1 instanceof PieChartActivity)){
						Intent intent = new Intent(getActivity(), PieChartActivity.class);
						startActivity(intent);
					}
				}

			}
		});

		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int id) {
				// user cancelled the dialog
				Activity act = getActivity();
				if (act instanceof ChartSelectionDialogFragmentWrapper){
					Log.d(LOGTAG, "hahahah, finally here");
					startActivity(new Intent(getActivity(),TaskPreferences.class));
				}
			}
		});

		// Create the AlertDialog object and return it
		AlertDialog dialog = builder.create();
		return dialog;

	}
}
