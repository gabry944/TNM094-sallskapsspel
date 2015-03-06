package com.jens.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.SharedPreferences;

public interface FileIO {
	public InputStream readFile(String file) throws IOException;
	
	public OutputStream writeFile(String file) throws IOException;
	
	public InputStream readAsset(String file) throws IOException;
	
	// Allows access and modify preference data
	public SharedPreferences getSharedPref();
}
