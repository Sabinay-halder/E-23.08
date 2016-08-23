package com.widevision.quemvaita.util;

public interface AsyncCallback<T>
{
	public void onOperationCompleted(T result, Exception e);
}
