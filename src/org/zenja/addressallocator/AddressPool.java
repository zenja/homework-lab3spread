package org.zenja.addressallocator;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AddressPool {
	
	private static AddressPool instance = null;
	private Map<String, AddressPoolItem> pool = new HashMap<String, AddressPoolItem>();
	
	public static AddressPool getInstance() {
		if (instance == null) {
			instance = new AddressPool();
		}
		
		return instance;
	}
	
	private AddressPool() {
		initPool();
	}
	
	private void initPool() {
		/*
		 * read addresses in address.conf and init pool
		 */
		try {
			Properties prop = new Properties();
			InputStream in = getClass().getResourceAsStream("address.conf");
			prop.load(in);
			
			String addressesStr = prop.getProperty("addresses");
			for (String s : addressesStr.split(",")) {
				AddressPoolItem addr = new AddressPoolItem(s, null, null);
				pool.put(s, addr);
			}
			
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
