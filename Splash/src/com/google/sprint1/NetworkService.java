//package com.google.sprint1;
//
//import java.util.ArrayList;
//
//import android.app.Service;
//import android.content.Intent;
//import android.net.nsd.NsdServiceInfo;
//import android.os.Binder;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.widget.ArrayAdapter;
//
//public class NetworkService extends Service {
//	public static final String TAG = "NetworkService";
//
//	private Handler mUpdateHandler;
//	
//	MobileConnection mConnection;
//	
//	public NsdHelper mNsdHelper;
//	
//	public ArrayAdapter<String> listAdapter;
//	public ArrayList<NsdServiceInfo> serviceList;
//	public ArrayList<String> serviceNameList;
//	
//	public Handler mNSDHandler;
//
//	private final IBinder mBinder = new LocalBinder();
//	/**Called when a class tries to bind to the service*/
//	@Override
//	public IBinder onBind(Intent intent) {
//		// TODO Auto-generated method stub
//		return mBinder;
//	}
//	
//	@Override
//	public void onCreate() {
//
//		// Creates new handler and a MobileConnection
//		mUpdateHandler = new Handler();
//		mConnection = new MobileConnection(mUpdateHandler);
//		
//		//ArrayList to store all services
//		serviceList = new ArrayList<NsdServiceInfo>();
//		//ArrayList to store all names of services
//		serviceNameList = new ArrayList<String>();
//
//		//The listAdapter only holds the name of the services and not the total
//		//NsdServiceInfo items.
//		listAdapter = new ArrayAdapter<String>(this,
//				R.layout.custom_list_for_services,
//				serviceNameList);
//		
//		mNSDHandler = new Handler() {
//			@Override
//			// Called whenever a message is sent to the handler.
//			// Currently assumes that the message contains a NsdServiceInfo
//			// object.
//			public void handleMessage(Message msg) {
//				NsdServiceInfo service;
//				// If message is of type 1, meaning "delete list".
//				// TODO: Should probably be an enum
//				if (msg.what == 1) {
//					listAdapter.clear();
//				}
//				// If key is "found", add NsdServiceInfo to serviceList and service name to serviceNameList.
//				else if ((service = (NsdServiceInfo) msg.getData().get("found")) != null) {
//					
//					serviceList.add(service);
//					serviceNameList.add(service.getServiceName());
//			
//				}
//				
//				// If key is "lost", remove from serviceList and serviceNameList
//				else if ((service = (NsdServiceInfo) msg.getData().get("lost")) != null) {
//					
//					for(int i = 0; i < serviceList.size(); i++){
//						if(serviceList.get(i).getServiceName().equals(service.getServiceName()))
//							serviceList.remove(i);
//					}
//					for(int i = 0; i < serviceNameList.size(); i++){
//						if(serviceNameList.get(i).equals(service.getServiceName()))
//							serviceNameList.remove(i);
//					}
//					
//				}
//				
//				// Notify adapter that the list is updated.
//				listAdapter.notifyDataSetChanged();
//
//			}
//		};
//		
//		initNsdHelper();
//
//	}
//	
//	/**Called when the user exits GameActivity and tear down the MobileConnection*/
//	@Override
//	public void onDestroy() {
//
//		// Tearing MobileConnection dofwn when user exits app
//		mConnection.tearDown();
//
//		super.onDestroy();
//	}
//	
//	public void initNsdHelper(){
//		mNsdHelper = new NsdHelper(this, mNSDHandler);
//		mNsdHelper.initializeNsd();
//	}
//
//	public class LocalBinder extends Binder {
//		NetworkService getService() {
//			return NetworkService.this;
//		}
//	}
//	
//
//
//}
