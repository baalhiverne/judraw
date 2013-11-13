package org.judraw.judrawlib;

import java.util.EventListener;

public interface DataReceivedListener extends EventListener {
	
	public void  dataChanged(Object source, byte[] data);

}
