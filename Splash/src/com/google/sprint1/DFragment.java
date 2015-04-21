package com.google.sprint1;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
 
public class DFragment extends DialogFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialogfragment, container,
				false);
		
		Dialog dialog = getDialog();
		
		//Close dialog on touch
		rootView.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View rootView) {
			getDialog().dismiss();
		}
		});
		
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		return rootView;
	}
}

