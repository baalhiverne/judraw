package org.judraw.judrawlib;

import java.util.EventListener;

public interface DPadStateListener extends EventListener {
	public void DPadStateChanged(Object source, TabletDPadState DPadState);
}
