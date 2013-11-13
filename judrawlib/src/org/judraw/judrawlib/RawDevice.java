package org.judraw.judrawlib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDDeviceNotFoundException;
import com.codeminders.hidapi.HIDManager;

public class RawDevice implements Runnable {

	private List<DataReceivedListener> listeners;
	private HIDDevice device;
	private static int MAX_BUFFER_LENGTH = 2048;
	private static int MAX_THREAD_WAIT = 1000;
	private Thread readThread;
	
	public RawDevice(int vendorID, int productID) {
		listeners = new ArrayList<DataReceivedListener>();
		HIDManager manager = null;
		
		try {
			manager = HIDManager.getInstance();
		} catch (IOException e) {
			System.err.println("Error retrieving HIDManager instance");
			e.printStackTrace();
		}
		
		try {
			device = manager.openById(vendorID, productID, null);
		} catch (HIDDeviceNotFoundException e) {
			System.err.println("HID Device vendor="+vendorID+" product="+productID+" not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error trying to open HID Device vendor="+vendorID+" product="+productID);
			e.printStackTrace();
		}
		finally {
			manager.release();
		}
		readThread = new Thread(this);
		readThread.start();
		
	}
	
	public static boolean isConnected(int vendorID, int productID) {
		HIDManager manager = null;
		HIDDeviceInfo[] devices = null;
		try {
			manager = HIDManager.getInstance();
		} catch (IOException e) {
			System.err.println("Error retrieving HIDManager instance");
			e.printStackTrace();
		}
		
		
		try {
			devices = manager.listDevices();
		} catch (IOException e) {
			System.err.println("Error listing HID devices");
			e.printStackTrace();
		}
		manager.release();
		for (int i = 0; i < devices.length; i++) {
			HIDDeviceInfo deviceInfo = devices[i];
			if((deviceInfo.getVendor_id() == vendorID) && (deviceInfo.getProduct_id() == productID)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void run() {
		while(true) {
			byte[] receivedData = null;
			int bytesRead = 0;
			byte[] buffer = new byte[MAX_BUFFER_LENGTH];
			try {
				bytesRead = device.read(buffer);
			} catch (IOException e) {
				System.err.println("Error trying to read HID device");
				e.printStackTrace();
			}
			receivedData = java.util.Arrays.copyOf(buffer, bytesRead);
			
			for(DataReceivedListener listener: listeners) {
				listener.dataChanged(this, receivedData);
			}
		}

	}
	
	public void addListener(DataReceivedListener listener) {
		listeners.add(listener);
	}
	
	public void finalize() throws InterruptedException {
		try {
			device.close();
		} catch (IOException e) {
			System.err.println("Error trying to close device");
			e.printStackTrace();
		}
		readThread.join(MAX_THREAD_WAIT);
		
	}

}
