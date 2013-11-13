package org.judraw.judrawlib;

public class TabletButtonState {
	public boolean circleHeld;
    public boolean crossHeld;
    public boolean squareHeld;
    public boolean triangleHeld;
    public boolean PSHeld;
    public boolean selectHeld;
    public boolean startHeld;
    public boolean leftStickHeld;
    public boolean rightStickHeld;
    public boolean leftButtonHeld;
    public boolean rightButtonHeld;
    
    public String toString() {
    	String out = "Buttons pressed: ";
    	if(circleHeld)
    		out += "Circle ";
    	if(crossHeld)
    		out += "Cross ";
    	if(squareHeld)
    		out += "Square ";
    	if(triangleHeld)
    		out += "Triangle ";
    	if(PSHeld)
    		out += "PS ";
    	if(selectHeld)
    		out += "Select ";
    	if(startHeld)
    		out += "Start ";
    	if(leftStickHeld)
    		out += "Left Stick ";
    	if(rightStickHeld)
    		out += "Right Stick ";
    	if(leftButtonHeld)
    		out += "Left Button ";
    	if(rightButtonHeld)
    		out += "Right Button ";

		return out;
    	
    }
}
