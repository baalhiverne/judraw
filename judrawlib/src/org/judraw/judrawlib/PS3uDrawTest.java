package org.judraw.judrawlib;

import java.awt.Point;

import com.codeminders.hidapi.ClassPathLibraryLoader;

public class PS3uDrawTest implements ButtonStateListener, DPadStateListener, Runnable{
	
	private InputDevice device;
	
	public PS3uDrawTest(InputDevice device) {
		this.device = device;
	}
	public static void main(String[] args) {
		ClassPathLibraryLoader.loadNativeHIDLibrary();
		InputDevice device = new PS3InputDevice();
		PS3uDrawTest test = new PS3uDrawTest(device);
		device.addButtonStateListener(test);
		device.addDPadStateListener(test);
		Thread thread = new Thread(test);
		thread.start();
	}

	@Override
	public void buttonStateChanged(Object source, TabletButtonState buttonState) {
		System.out.println(buttonState);
	}

	@Override
	public void DPadStateChanged(Object source, TabletDPadState DPadState) {
		System.out.println(DPadState);
	}

	@Override
	public void run() {
		while(true) {
			System.out.println("Pen pressure "+device.getPenPressure());
			Point coord = device.getPressurePoint();
			System.out.println("Pen pressure type "+device.getPressureType());
			System.out.println("Pen position x="+coord.x+" y="+coord.y);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println("Error interrupted PS3uDrawTest");
				e.printStackTrace();
			}
		}
		
	}

}
