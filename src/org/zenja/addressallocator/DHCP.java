package org.zenja.addressallocator;

import gmi.ExternalGMIListener;

public interface DHCP extends ExternalGMIListener {
	public AddressPoolItem leaseNewAddress(String clientName);
}
