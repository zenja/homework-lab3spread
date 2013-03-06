package org.zenja.addressallocator;

public class AddressPoolItem {
	private String address;
	private String client;	// if client == null, then it is not used
	private Long expireAt;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public Long getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(Long expireAt) {
		this.expireAt = expireAt;
	}

	/**
	 * Constructor
	 */
	public AddressPoolItem(String address, String client, Long expireAt) {
		this.address = address;
		this.client = client;
		this.expireAt = expireAt;
	}
}
