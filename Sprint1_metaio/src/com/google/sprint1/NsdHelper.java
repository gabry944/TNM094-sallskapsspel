package com.google.sprint1;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdManager;
import android.util.Log;

public class NsdHelper {

	Context mContext;

	NsdManager mNsdManager;
	NsdManager.ResolveListener mResolveListener;
	NsdManager.DiscoveryListener mDiscoveryListener;
	NsdManager.RegistrationListener mRegistrationListener;

	public static final String SERVICE_TYPE = "_http._tcp.";

	public static final String TAG = "NsdHelper";
	public String mServiceName = "";
	public static String SERVICE_NAME = "ARGame";

	public boolean haveActiveService = false;
	public boolean serviceResolved = false;
	NsdServiceInfo mService;
	List<NsdServiceInfo> mFoundServices = new ArrayList<NsdServiceInfo>();

	public NsdHelper(Context context) {
		mContext = context;
		mNsdManager = (NsdManager) context
				.getSystemService(Context.NSD_SERVICE);
	}

	public void initializeNsd() {
		initializeResolveListener();
		initializeDiscoveryListener();
		initializeRegistrationListener();

		// mNsdManager.init(mContext.getMainLooper(), this);

	}

	public void initDiscoveryListener() {
		initializeDiscoveryListener();
		initializeRegistrationListener();
		discoverServices();


	}

	public void initializeDiscoveryListener() {
		mDiscoveryListener = new NsdManager.DiscoveryListener() {

			@Override
			public void onDiscoveryStarted(String regType) {
				Log.d(TAG, "Service discovery started");

			}

			@Override
			public void onServiceFound(NsdServiceInfo service) {

				Log.d(TAG, "Service discovery success. Found: " + service.getServiceName());


				if (!service.getServiceType().equals(SERVICE_TYPE)) {
					Log.d(TAG,
							"Unknown Service Type: " + service.getServiceType());
				} else if (service.getServiceName().equals(mServiceName)) {
					Log.d(TAG, "Same machine: " + mServiceName);
					haveActiveService = true;
				} else if (service.getServiceName().contains(SERVICE_NAME)) {
					Log.d(TAG, "Service added to List.");
					mFoundServices.add(service);
					//mNsdManager.resolveService(service, mResolveListener);

				} 
			}

			@Override
			public void onServiceLost(NsdServiceInfo service) {
				Log.e(TAG, "service lost: " + service.getServiceName());
				mFoundServices.remove(service);
				if (mService == service) {
					mService = null;
					haveActiveService = false;
				}
			}

			@Override
			public void onDiscoveryStopped(String serviceType) {
				Log.i(TAG, "Discovery stopped: " + serviceType);
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
				//mFoundServices.add(serviceInfo);
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

				Log.d(TAG, "Service unregistered: " + serviceInfo.getServiceName());

			}

			@Override
			public void onUnregistrationFailed(NsdServiceInfo serviceInfo,
					int errorCode) {
				Log.d(TAG, "Unregistration failed: " + errorCode);
			}

		};
	}

	public void registerService(int port) {

		if (!haveActiveService) {
			NsdServiceInfo serviceInfo = new NsdServiceInfo();
			serviceInfo.setPort(port);
			serviceInfo.setServiceName(SERVICE_NAME);
			serviceInfo.setServiceType(SERVICE_TYPE);
			mNsdManager.registerService(serviceInfo,
					NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
		} else {
			Log.d(TAG, "Service already registered");
		}

	}

	public void discoverServices() {
		
		mFoundServices.clear();
		mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD,
				mDiscoveryListener);
	}

	public void stopDiscovery() {
		mNsdManager.stopServiceDiscovery(mDiscoveryListener);
	}

	public NsdServiceInfo resolveService(NsdServiceInfo service) {
		serviceResolved = false;
		mNsdManager.resolveService(service, mResolveListener);
		while (!serviceResolved)
		{
			//Wait for service to be resolved
		}
		return mService;
	}
	public NsdServiceInfo getChosenServiceInfo() {
		return mService;
	}


	public List<NsdServiceInfo> getFoundServices() {
		return mFoundServices;
	}


	public void tearDown() {
		Log.d(TAG, "tearing down");
		mNsdManager.unregisterService(mRegistrationListener);
		mNsdManager.stopServiceDiscovery(mDiscoveryListener);

	}
}
