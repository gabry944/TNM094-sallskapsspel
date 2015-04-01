package com.google.sprint1;

import java.io.Serializable;

public class TestClass implements Serializable {
	
	private int mValue;
	private String mText;
	
	TestClass(int value, String text)
	{
		mValue = value;
		mText = text;
	}
	
	public String toString()
	{
		return ("TestObject. Value: " + mValue +". Text:" + mText);
	}
}
