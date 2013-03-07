package org.zenja.addressallocator;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;


public class AddressPoolItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String address;
	private String client;	// if client == null, then it is not used
	private String server;
	private Long expireAt;
	
	public static final int LEASE_SECONDS;
	
	static {
		/*
		 * read addresses in allocator.conf and initiate pool
		 */
		int leaseSeconds = 0;
		try {
			Properties prop = new Properties();
			InputStream in = AddressPoolItem.class.getResourceAsStream("allocator.conf");
			prop.load(in);
			
			leaseSeconds = Integer.parseInt(prop.getProperty("lease.num.seconds"));
			
			in.close();
		} catch (IOException e) {
			System.err.println("[Error] Property lease.num.seconds get failed! Please check the allocator.conf");
			e.printStackTrace();
			System.exit(-1);
		} finally {
			LEASE_SECONDS = leaseSeconds;
		}
	}

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

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
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
	public AddressPoolItem(String address, String server, String client, Long expireAt) {
		this.address = address;
		this.client = client;
		this.server = server;
		this.expireAt = expireAt;
	}
	
	public boolean isFree() {
		return server == null;
	}
	
	public void lease(String server, String client) {
		this.server = server;
		this.client = client;
		this.expireAt = System.currentTimeMillis() + LEASE_SECONDS * 1000;
	}

	public void free() {
		server = null;
		client = null;
		expireAt = null;
	}

	public boolean isExpired() {
		if (expireAt == null) {
			return false;
		} else if (expireAt.longValue() - System.currentTimeMillis() <= 0) {
			return true;
		} else {
			return false;
		}
	}
}
