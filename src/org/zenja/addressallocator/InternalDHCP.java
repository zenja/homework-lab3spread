package org.zenja.addressallocator;

import gmi.InternalGMIListener;

public interface InternalDHCP extends InternalGMIListener {

	public void reserveAddress(String address, String serverName, String clientName, Long expireAt);

	public void notifyExpiration();

}
