package org.zenja.addressallocator;

import gmi.InternalGMIListener;

public interface InternalDHCP extends InternalGMIListener {

	public void setLease(String address, String serverName, String clientName, Long expireAt);

	public void notifyExpiration();

}
