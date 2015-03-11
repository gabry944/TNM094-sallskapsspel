package com.google.sprint1;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class ChoosePeerDialogFragment extends DialogFragment {

	WifiP2pDeviceList mDeviceList;
	private List<WifiP2pDevice> peerList = new ArrayList<WifiP2pDevice>();
	
	static ChoosePeerDialogFragment newInstance(WifiP2pDeviceList devices) {
	    
		ChoosePeerDialogFragment f = new ChoosePeerDialogFragment();
	    //Log.i("WIFI", "I newInstance");
	    Bundle args = new Bundle();
	    args.putParcelable("devices", devices);
	    f.setArguments(args);

	    return f;
	}
	

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		mDeviceList = getArguments().getParcelable("devices");
		
		peerList = new ArrayList(mDeviceList.getDeviceList());
		ArrayAdapter<WifiP2pDevice> adapter = new ArrayAdapter<WifiP2pDevice>(getActivity(), android.R.layout.select_dialog_multichoice);
		adapter.addAll(peerList);
		Log.i("WIFI", "booom!");
		
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Found following peers:");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}