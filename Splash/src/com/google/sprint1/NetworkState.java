package com.google.sprint1;

import java.util.ArrayList;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;

public class NetworkState {
	
	private static NetworkState instance = null;
	
    private Handler mUpdateHandler;
	
	MobileConnection mConnection;
	
	public NsdHelper mNsdHelper;
	
	public ArrayAdapter<String> listAdapter;
	public ArrayList<NsdServiceInfo> serviceList;
	public ArrayList<String> serviceNameList;
	
	public Handler mNSDHandler;
	
	private Context context;

	protected  NetworkState() {
		
	}
	
	public synchronized static NetworkState getState() {
		if (instance == null){
			instance = new NetworkState();
			//instance.init();
		}
		return instance;
	}
	
	/** Initialize the Game State, currently called the first time getState is called*/
	public void init(Context context){
//		powerUps = new ArrayList<PowerUp>();
//		
//		players = new ArrayList<Player>();
		this.context = context; 
		
		mUpdateHandler = new Handler();
		mConnection = new MobileConnection(mUpdateHandler);
		
		//ArrayList to store all services
		serviceList = new ArrayList<NsdServiceInfo>();
		//ArrayList to store all names of services
		serviceNameList = new ArrayList<String>();

		//The listAdapter only holds the name of the services and not the total
		//NsdServiceInfo items.
		listAdapter = new ArrayAdapter<String>(this.context,
				R.layout.custom_list_for_services,
				serviceNameList);
		
		mNSDHandler = new Handler() {
			@Override
			// Called whenever a message is sent to the handler.
			// Currently assumes that the message contains a NsdServiceInfo
			// object.
			public void handleMessage(Message msg) {
				NsdServiceInfo service;
				// If message is of type 1, meaning "delete list".
				// TODO: Should probably be an enum
				if (msg.what == 1) {
					listAdapter.clear();
				}
				// If key is "found", add NsdServiceInfo to serviceList and service name to serviceNameList.
				else if ((service = (NsdServiceInfo) msg.getData().get("found")) != null) {
					
					serviceList.add(service);
					serviceNameList.add(service.getServiceName());
			
				}
				
				// If key is "lost", remove from serviceList and serviceNameList
				else if ((service = (NsdServiceInfo) msg.getData().get("lost")) != null) {
					
					for(int i = 0; i < serviceList.size(); i++){
						if(serviceList.get(i).getServiceName().equals(service.getServiceName()))
							serviceList.remove(i);
					}
					for(int i = 0; i < serviceNameList.size(); i++){
						if(serviceNameList.get(i).equals(service.getServiceName()))
							serviceNameList.remove(i);
					}
					
				}
				
				// Notify adapter that the list is updated.
				listAdapter.notifyDataSetChanged();

			}
		};
		
		initNsdHelper();

	}
	
	public void initNsdHelper(){
		mNsdHelper = new NsdHelper(context, mNSDHandler);
		mNsdHelper.initializeNsd();
	}
}
