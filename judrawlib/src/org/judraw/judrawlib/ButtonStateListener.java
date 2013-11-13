package org.judraw.judrawlib;

import java.util.EventListener;

public interface ButtonStateListener extends EventListener {
	public void buttonStateChanged(Object source, TabletButtonState buttonState);
}
