package com.google.sprint1;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
 
public class InstructionsDialog extends DialogFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.instructionsdialog, container,
				false);
		
		final Timer time = new Timer();
		
		//Close dialog on touch
		rootView.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View rootView) {
			getDialog().dismiss();
			time.cancel();
		}
		});
		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	
		//Timer that closes the dialog after 10 seconds 
        time.schedule(new TimerTask() {
             public void run() {
                 getDialog().dismiss();
                 time.cancel();
             }
        }, 10000);
		
		return rootView;	
	}
}

