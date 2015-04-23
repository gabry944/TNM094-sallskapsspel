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

/**
 * Class to handle the dialog for the instructions, the dialog will dismiss after 10 seconds 
 * 
 * or on touch.
 * 
 * 
 */
 
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
			if (getDialog() != null)
       		 getDialog().dismiss();
			time.cancel();
		}
		});
		
		//No dialog title
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	
		//Timer that closes the dialog after 10 seconds 
        time.schedule(new TimerTask() {
             public void run() {
            	 if (getDialog() != null)
            		 getDialog().dismiss();
                 time.cancel();
             }
        }, 10000);
		
		return rootView;	
	}
}

