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
	public String mServiceName = "ARGame";

	public boolean haveActiveService = false;
	NsdServiceInfo mService;
	List<NsdServiceInfo> mServices = new ArrayList<NsdServiceInfo>();

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

	public void initializeDiscoveryListener() {
		mDiscoveryListener = new NsdManager.DiscoveryListener() {

			@Override
			public void onDiscoveryStarted(String regType) {
				Log.d(TAG, "Service discovery started");
				mServices.clear();
			}

			@Override
			public void onServiceFound(NsdServiceInfo service) {
				Log.d(TAG, "Service discovery success" + service);
				if (!service.getServiceType().equals(SERVICE_TYPE)) {
					Log.d(TAG,
							"Unknown Service Type: " + service.getServiceType());
				} else if (service.getServiceName().equals(mServiceName)) {
					Log.d(TAG, "Same machine: " + mServiceName);
					haveActiveService = true;
				} else if (service.getServiceName().contains(mServiceName)) {
					// mServices.add(service);
					mNsdManager.resolveService(service, mResolveListener);

				}
			}

			@Override
			public void onServiceLost(NsdServiceInfo service) {
				Log.e(TAG, "service lost" + service);
				mServices.remove(service);
				if (mService == service) {
					mService = null;
				}
				if (service.getServiceName().equals(mServiceName)) {
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
				Log.e(TAG, "Resolve failed" + errorCode);
				mServices.remove(serviceInfo);
			}

			@Override
			public void onServiceResolved(NsdServiceInfo serviceInfo) {
				Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

				if (serviceInfo.getServiceName().equals(mServiceName)) {
					Log.d(TAG, "Same IP.");
					return;
				}

				mService = serviceInfo;
				mServices.add(serviceInfo);
			}
		};
	}

	public void initializeRegistrationListener() {
		mRegistrationListener = new NsdManager.RegistrationListener() {

			@Override
			public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
				Log.d(TAG, "Service registred: " + NsdServiceInfo);
				mServiceName = NsdServiceInfo.getServiceName();
			}

			@Override
			public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
			}

			@Override
			public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

				Log.d(TAG, "Service unregistered: " + serviceInfo);
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
			serviceInfo.setServiceName(mServiceName);
			serviceInfo.setServiceType(SERVICE_TYPE);
			mNsdManager.registerService(serviceInfo,
					NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
		} else {
			Log.d(TAG, "Service already registered");
		}

	}

	public void discoverServices() {
		// mServices.clear();
		mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD,
				mDiscoveryListener);
	}

	public void stopDiscovery() {
		mNsdManager.stopServiceDiscovery(mDiscoveryListener);
	}

	public NsdServiceInfo getChosenServiceInfo() {
		return mService;
	}

	public List<NsdServiceInfo> getChosenServiceInfoList() {
		return mServices;
	}

	public void tearDown() {
		// mNsdManager.unregisterService(mRegistrationListener);
		mNsdManager.stopServiceDiscovery(mDiscoveryListener);
	}
}
