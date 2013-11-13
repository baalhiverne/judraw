package org.judraw.judrawlib;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public abstract class InputDevice {
	private short penPressure;
	private int multiTouchDistance;
	private Point pressurePoint;
	private TabletButtonState buttonState;
	private TabletDPadState DPadState;
	private TabletPressureType pressureType;
	private TabletAccelerometerData accelerometerData;
	private List<ButtonStateListener> buttonListeners;
	private List<DPadStateListener> DPadListeners;
	
	public InputDevice() {
		buttonState = new TabletButtonState();
		DPadState = new TabletDPadState();
		accelerometerData = new TabletAccelerometerData();
		pressureType  = TabletPressureType.NOT_PRESSED;
		pressurePoint = new Point();
		buttonListeners = new ArrayList<ButtonStateListener>();
		DPadListeners = new ArrayList<DPadStateListener>();
	}
	
	public void addButtonStateListener(ButtonStateListener listener) {
		buttonListeners.add(listener);
	}
	
	public void addDPadStateListener(DPadStateListener listener) {
		DPadListeners.add(listener);
	}
	
	protected List<ButtonStateListener> getButtonStateListeners() {
		return buttonListeners;
	}
	
	protected List<DPadStateListener> getDPadStateListeners() {
		return DPadListeners;
	}
	
	public int getPenPressure() {
		return penPressure;
	}
	
	public void setPenPressure(short penPressure) {
		this.penPressure = penPressure;
	}

	public int getMultiTouchDistance() {
		return multiTouchDistance;
	}

	public void setMultiTouchDistance(int multiTouchDistance) {
		this.multiTouchDistance = multiTouchDistance;
	}

	public Point getPressurePoint() {
		return pressurePoint;
	}

	public void setPressurePoint(Point pressurePoint) {
		this.pressurePoint = pressurePoint;
	}

	public TabletButtonState getButtonState() {
		return buttonState;
	}

	public void setButtonState(TabletButtonState buttonState) {
		this.buttonState = buttonState;
	}

	public TabletDPadState getDPadState() {
		return DPadState;
	}

	public void setDPadState(TabletDPadState dPadState) {
		DPadState = dPadState;
	}

	public TabletPressureType getPressureType() {
		return pressureType;
	}

	public void setPressureType(TabletPressureType pressureType) {
		this.pressureType = pressureType;
	}

	public TabletAccelerometerData getAccelerometerData() {
		return accelerometerData;
	}

	public void setAccelerometerData(TabletAccelerometerData accelerometerData) {
		this.accelerometerData = accelerometerData;
	}
	
}
