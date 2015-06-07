package com.google.sprint1;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;


/**
 * Class to handle the dialog for the instructions, the dialog will dismiss on touch.
 */
 
public class InstructionsDialog extends DialogFragment 
{
	public static final String TAG = "InstructionsDialog";
	public int counter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.instructionsdialog, container, false);
		
		//this don't work as i want
		//LinearLayout  displayInstructions = (LinearLayout) rootView.findViewById(R.id.instructions);
		//android.graphics.drawable.Drawable d = displayInstructions.getBackground();
		
		//so i keep a counter in order to know which image to change to.
		counter = 1;
		
		//Change/Close dialog on touch
		rootView.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View rootView) {			
				if (getDialog() != null)
				{
					LinearLayout  displayInstructions = (LinearLayout) rootView.findViewById(R.id.instructions);
					if (counter == 1)
					{
						displayInstructions.setBackgroundResource(R.drawable.drag);
						//Log.d(TAG, "1");
					}
					else if (counter == 2)
					{
						displayInstructions.setBackgroundResource(R.drawable.hit);		
						//Log.d(TAG, "2")	;			
					}
					else if (counter == 3)
					{
						displayInstructions.setBackgroundResource(R.drawable.points);
					}
					else //if (counter == 4)
					{
						getDialog().dismiss();
					}
					
					counter++;
				}
			}
		});
		
		//No dialog title
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		return rootView;	
	}
}

