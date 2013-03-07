package org.zenja.addressallocator;

import gmi.ExternalGMIListener;

public interface DHCP extends ExternalGMIListener {
	public AddressPoolItem leaseNewAddress(String clientName);
	public AddressPoolItem getLeaseInfo(String address);
	
	// renew lease
	public boolean refresh(String address, String clientName);
}
