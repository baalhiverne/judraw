package org.judraw.judrawlib;

import java.awt.Point;
import java.util.List;

public class PS3InputDevice extends InputDevice implements DataReceivedListener{
	private static final int VENDOR_ID = 0x20D6;
	private static final int PRODUCT_ID = 0xCB17;
	private static final int MULTITOUCH_SENSITIVITY = 30;
	private static final int UNKNOWN_DATA1_SIZE = 4;
	private RawDevice device = null;
	private byte[] unknownData;
	private int multiTouchTimer;
	private int previousMultiTouchDistance;


	private enum RawPressureType {
		NOT_PRESSED((byte)0x00),
		PEN_PRESSED((byte)0x40),
		FINGER_PRESSED((byte)0x80);

		private final byte value;
		private RawPressureType(byte value) {
			this.value = value;
		}

		byte value() {
			return value;
		}
	};


	public PS3InputDevice() {
		super();
		unknownData = new byte[UNKNOWN_DATA1_SIZE];
		device = new RawDevice(VENDOR_ID,PRODUCT_ID);
		device.addListener(this);
	}
	
	public boolean isPressed() {
		boolean pressed = false;
		if(getPressureType() == TabletPressureType.NOT_PRESSED) {
			pressed = true;
		}
		return pressed;
	}

	public boolean isZooming() {
		boolean zoomed = false;
		if(getMultiTouchDistance() > previousMultiTouchDistance) {
			zoomed = true;
		}
		return zoomed;
	}

	public boolean isPinching() {
		boolean pinched = false;
		if(getMultiTouchDistance() < previousMultiTouchDistance) {
			pinched = true;
		}
		return pinched;
	}

	public static boolean IsReceiverDetected() {
		return RawDevice.isConnected(VENDOR_ID, PRODUCT_ID);
	}

	public byte[] getUnknownData() {
		return unknownData;
	}
	public void setUnknownData(byte[] unknownData) {
		this.unknownData = unknownData;
	}

	@Override
	public void dataChanged(Object source, byte[] data) {

		final int UNKNOWN_DATA1_OFFSET = 3;
		final int MULTITOUCH_DISTANCE_OFFSET = 12;
		final int PEN_PRESSURE_OFFSET = 13;
		final int PRESSURE_DATA_OFFSET = 15;
		final int ACCELEROMETER_X_OFFSET = 19;
		final int ACCELEROMETER_Y_OFFSET = 21;
		final int ACCELEROMETER_Z_OFFSET = 23;

		//Save the unknown data
		unknownData = java.util.Arrays.copyOfRange(data, UNKNOWN_DATA1_OFFSET, UNKNOWN_DATA1_SIZE);

		//Get the pressure state
		if (data[11] == RawPressureType.NOT_PRESSED.value())
			setPressureType(TabletPressureType.NOT_PRESSED);
		else if (data[11] == RawPressureType.PEN_PRESSED.value())
			setPressureType(TabletPressureType.PEN_PRESSED);
		else if (data[11] == RawPressureType.FINGER_PRESSED.value())
			setPressureType(TabletPressureType.FINGER_PRESSED);
		else
			setPressureType(TabletPressureType.MULTI_TOUCH);

		//Get the pen pressure
		setPenPressure((short)(data[PEN_PRESSURE_OFFSET] & 0xFF));

		//Get the multitouch distance
		multiTouchTimer++;
		if (multiTouchTimer > MULTITOUCH_SENSITIVITY)
		{
			multiTouchTimer = 0;
			previousMultiTouchDistance = getMultiTouchDistance();
		}
		setMultiTouchDistance(data[MULTITOUCH_DISTANCE_OFFSET]);

		//Get the (singular) pressure point
		setPressurePoint(new Point(data[PRESSURE_DATA_OFFSET] * 0x100 + data[PRESSURE_DATA_OFFSET+2], data[PRESSURE_DATA_OFFSET+1] * 0x100 + data[PRESSURE_DATA_OFFSET+3]));

		//Get the accelerometer data
		TabletAccelerometerData accelerometerData = getAccelerometerData();
		final short X_MIN = 0x1EA; final short X_MAX = 0x216;
		final short Y_MIN = 0x1EA; final short Y_MAX = 0x216;
		final short Z_MIN = 0x1EC; final short Z_MAX = 0x218;
		int x = (short)(data[ACCELEROMETER_X_OFFSET] | (data[ACCELEROMETER_X_OFFSET+1] << 8) & 0xFF);
		if (x < X_MIN) x = X_MIN; if (x > X_MAX) x = X_MAX;
		accelerometerData.xAxis = (short)((x - X_MIN) / (X_MAX - X_MIN));
		int y = (short)(data[ACCELEROMETER_Y_OFFSET] | (data[ACCELEROMETER_Y_OFFSET+1] << 8) & 0xFF);
		if (y < Y_MIN) y = Y_MIN; if (y > Y_MAX) y = Y_MAX;
		accelerometerData.yAxis = (short)((y - Y_MIN) / (Y_MAX - Y_MIN));
		int z = (short)(data[ACCELEROMETER_Z_OFFSET] | (data[ACCELEROMETER_Z_OFFSET+1] << 8) & 0xFF);
		if (z < Z_MIN) z = Z_MIN; if (z > Z_MAX) z = Z_MAX;
		accelerometerData.zAxis = (short)((z - Z_MIN) / (Z_MAX - Z_MIN));
		setAccelerometerData(accelerometerData);

		//Parse raw data for buttons
		TabletButtonState buttonState = getButtonState();
		boolean changed = false;
		boolean raw = (data[0] & 0x01) > 0;
		changed |= buttonState.squareHeld != raw; buttonState.squareHeld = raw;
		raw = (data[0] & 0x02) > 0;
		changed |= buttonState.crossHeld != raw; buttonState.crossHeld = raw;
		raw = (data[0] & 0x04) > 0;
		changed |= buttonState.circleHeld != raw; buttonState.circleHeld = raw;
		raw = (data[0] & 0x08) > 0;
		changed |= buttonState.triangleHeld != raw; buttonState.triangleHeld = raw;
		raw = (data[1] & 0x01) > 0;
		changed |= buttonState.selectHeld != raw; buttonState.selectHeld = raw;
		raw = (data[1] & 0x02) > 0;
		changed |= buttonState.startHeld != raw; buttonState.startHeld = raw;
		raw = (data[1] & 0x10) > 0;
		changed |= buttonState.PSHeld != raw; buttonState.PSHeld = raw;
		setButtonState(buttonState);
		List<ButtonStateListener> buttonListeners = getButtonStateListeners();
		if (changed) {
			for(ButtonStateListener listener: buttonListeners) {
				listener.buttonStateChanged(this, buttonState);
			}

		}
		//Now parse raw data for D-pad changes
		TabletDPadState DPadState = getDPadState();
		changed = false;
		raw = (data[2] == 0x0) || (data[2] == 0x1) || (data[2] == 0x7);
		changed |= DPadState.upHeld != raw; DPadState.upHeld = raw;
		raw = (data[2] == 0x3) || (data[2] == 0x4) || (data[2] == 0x5);
		changed |= DPadState.downHeld != raw; DPadState.downHeld = raw;
		raw = (data[2] == 0x5) || (data[2] == 0x6) || (data[2] == 0x7);
		changed |= DPadState.leftHeld != raw; DPadState.leftHeld = raw;
		raw = (data[2] == 0x1) || (data[2] == 0x2) || (data[2] == 0x3);
		changed |= DPadState.rightHeld != raw; DPadState.rightHeld = raw;
		setDPadState(DPadState);
		List<DPadStateListener> DPadListeners = getDPadStateListeners();
		if (changed) {
			for(DPadStateListener listener: DPadListeners) {
				listener.DPadStateChanged(this, DPadState);
			}
		}
	}

}
