package org.judraw.judrawlib;

public class TabletDPadState {
	public boolean upHeld;
	public boolean downHeld;
	public boolean leftHeld;
	public boolean rightHeld;

	public String toString() {
		String out = "DPad pressed: ";
		if(upHeld)
			out += "Up ";
		if(downHeld)
			out += "Down ";
		if(leftHeld)
			out += "Left ";
		if(rightHeld)
			out += "Right ";
		return out;
	}
}
