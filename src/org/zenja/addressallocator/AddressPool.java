package org.zenja.addressallocator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AddressPool {
	
	private static AddressPool instance = null;
	
	/*
	 * HashMap and ArrayList for the same pool data, 
	 * using redundant space to provide different operation complexities
	 */
	private Map<String, AddressPoolItem> addressTable = new HashMap<String, AddressPoolItem>();
	private List<AddressPoolItem> addressList = new ArrayList<AddressPoolItem>();
	
	private AddressPool() {
		initPool();
	}
	
	private void initPool() {
		/*
		 * read addresses in allocator.conf and initiate pool
		 */
		try {
			Properties prop = new Properties();
			InputStream in = getClass().getResourceAsStream("allocator.conf");
			prop.load(in);
			
			String addressesStr = prop.getProperty("addresses");
			for (String s : addressesStr.split(",")) {
				AddressPoolItem addr = new AddressPoolItem(s, null, null, null);
				addressTable.put(s, addr);
				addressList.add(addr);
			}
			
			in.close();
		} catch (IOException e) {
			System.err.println("[Error] AddressPool initialization failed! Please check the allocator.conf");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static AddressPool getInstance() {
		if (instance == null) {
			instance = new AddressPool();
		}
		
		return instance;
	}
	
	public int getSize() {
		return addressTable.size();
	}
	
	public List<AddressPoolItem> getAddressList() {
		return addressList;
	}
	
	public Map<String, AddressPoolItem> getAddressTable() {
		return addressTable;
	}

	public void cleanExpiredLeases() {
		long currentTime = System.currentTimeMillis();
		for (AddressPoolItem item : addressList) {
			if (item.getExpireAt() != null && item.getExpireAt() <= currentTime) {
				item.free();
			}
		}
	}

	public AddressPoolItem getItemByAddress(String address) {
		return addressTable.get(address);
	}
}
