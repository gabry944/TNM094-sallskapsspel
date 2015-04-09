package com.google.sprint1;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NsdHelper {

	Context mContext;
	Handler mUpdateHandler;

	NsdManager mNsdManager;
	NsdManager.ResolveListener mResolveListener;
	NsdManager.DiscoveryListener mDiscoveryListener;
	NsdManager.RegistrationListener mRegistrationListener;

	public static final String SERVICE_TYPE = "_http._tcp.";
	public static final String TAG = "NsdHelper";
	public static final String SERVICE_NAME = "ARGame";
	
	public String mServiceName = "";

	public boolean serviceResolved = false;
	public boolean discoveryReady;
	NsdServiceInfo mService;
	
	/**Constructor */
	public NsdHelper(Context context, Handler handler) {
		discoveryReady = false;
		mContext = context;
		mUpdateHandler = handler;
		mNsdManager = (NsdManager) context
				.getSystemService(Context.NSD_SERVICE);
	}
	
	/**Initialize various listeners for the NsdHelper */
	public void initializeNsd() {
		initializeResolveListener();
		initializeDiscoveryListener();
		initializeRegistrationListener();
	}

	public void initializeDiscoveryListener() {
		mDiscoveryListener = new NsdManager.DiscoveryListener() {

			@Override
			public void onDiscoveryStarted(String regType) {
				Log.d(TAG, "Service discovery started");

			}

			@Override
			public void onServiceFound(NsdServiceInfo service) {

				Log.d(TAG,
						"Service discovery success. Found: "
								+ service.getServiceName());

				if (!service.getServiceType().equals(SERVICE_TYPE)) {
					Log.d(TAG,
							"Unknown Service Type: " + service.getServiceType());
				} else if (service.getServiceName().equals(mServiceName)) {
					Log.d(TAG, "Same machine: " + mServiceName);
				} else if (service.getServiceName().contains(SERVICE_NAME)) {

					Bundle bundle = new Bundle();
					bundle.putParcelable("found", service);
					Message msg = new Message();
					msg.setData(bundle);
					mUpdateHandler.sendMessage(msg);

				}
				discoveryReady = true;
			}

			@Override
			public void onServiceLost(NsdServiceInfo service) {
				Log.e(TAG, "service lost(service discoovery): " + service.getServiceName());

				Bundle bundle = new Bundle();
				bundle.putParcelable("lost", service);
				Message msg = new Message();
				msg.setData(bundle);
				mUpdateHandler.sendMessage(msg);

			}

			@Override
			public void onDiscoveryStopped(String serviceType) {
				Log.i(TAG, "Discovery stopped: " + serviceType);
				mUpdateHandler.sendEmptyMessage(1);
			}

			@Override
			public void onStartDiscoveryFailed(String serviceType, int errorCode) {
				Log.e(TAG, "Discovery failed: Error code:" + errorCode);
				mNsdManager.stopServiceDiscovery(this);
			}

			@Override
			public void onStopDiscoveryFailed(String serviceType, int errorCode) {
				Log.e(TAG, "Discovery failed: Error code:" + errorCode);
				mNsdManager.stopServiceDiscovery(this);
			}
		};
	}

	public void initializeResolveListener() {
		mResolveListener = new NsdManager.ResolveListener() {

			@Override
			public void onResolveFailed(NsdServiceInfo serviceInfo,
					int errorCode) {
				Log.e(TAG, "Resolve failed. Error code: " + errorCode);
				serviceResolved = true;
			}

			@Override
			public void onServiceResolved(NsdServiceInfo serviceInfo) {
				Log.e(TAG, "Resolve Succeeded. ");
				Log.e(TAG, "Added service: " + serviceInfo.getServiceName());
				Log.e(TAG, "IP: " + serviceInfo.getHost());

				if (serviceInfo.getServiceName().equals(mServiceName)) {
					Log.d(TAG, "Same IP.");
					return;
				}

				mService = serviceInfo;
				serviceResolved = true;
			}
		};
	}

	public void initializeRegistrationListener() {
		mRegistrationListener = new NsdManager.RegistrationListener() {

			@Override
			public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
				Log.d(TAG, "Service registered: " + NsdServiceInfo);
				mServiceName = NsdServiceInfo.getServiceName();
			}

			@Override
			public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {

				Log.d(TAG, "Service registration failed. Error: " + arg1);

			}

			@Override
			public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

				Log.d(TAG,
						"Service unregistered: " + serviceInfo.getServiceName());

			}

			@Override
			public void onUnregistrationFailed(NsdServiceInfo serviceInfo,
					int errorCode) {
				Log.d(TAG, "Unregistration failed: " + errorCode);
			}

		};
	}
	
	/**Registers service on the network */
	public void registerService(int port) {
		NsdServiceInfo serviceInfo = new NsdServiceInfo();
		serviceInfo.setPort(port);
		serviceInfo.setServiceName(SERVICE_NAME);
		serviceInfo.setServiceType(SERVICE_TYPE);
		mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD,
				mRegistrationListener);

	}
	
	/**Starts the service discovery */
	public void discoverServices() {
		mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD,
				mDiscoveryListener);
	}
	
	/**Stops the service discovery	 */
	public void stopDiscovery() {
		Log.d(TAG, "NsdHelper: stop discovery");

		mNsdManager.stopServiceDiscovery(mDiscoveryListener);
	}

	public NsdServiceInfo resolveService(NsdServiceInfo service) {
		serviceResolved = false;
		mNsdManager.resolveService(service, mResolveListener);
		while (!serviceResolved) {
			// Wait for service to be resolved
		}
		return mService;
	}

	public NsdServiceInfo getChosenServiceInfo() {
		return mService;
	}

	/**
	 * Used to unregister service from network and stop the service discovery 
	 * at the same time.
	 */
	public void tearDown() {
		Log.d(TAG, "tearing down");
		mNsdManager.unregisterService(mRegistrationListener);
		mNsdManager.stopServiceDiscovery(mDiscoveryListener);

	}
	
	public void unregisterService(){
		Log.d(TAG, "NsdHelper: unregister service");

		mNsdManager.unregisterService(mRegistrationListener);
	}
}
