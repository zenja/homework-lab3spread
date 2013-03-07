package org.zenja.addressallocator;

import java.util.List;

import org.zenja.addressallocator.exceptions.NoFreeAddressException;

public class AddressPoolLocalManager {
	private AddressPool pool;
	private String serverName;
	private int myPosition;
	private int numServer;
	
	public AddressPoolLocalManager(AddressPool pool, String serverName) {
		this.pool = pool;
		this.serverName = serverName;
	}
	
	/**
	 * Register the change of servers, 
	 * and perform a take-over action
	 * 
	 * @param myPosition The position of the current server, start from 0
	 * @param numServer The total number of servers
	 */
	public void register(int myPosition, int numServer) {
		this.myPosition = myPosition;
		this.numServer = numServer;
		
		/*
		 * take-over actions
		 */
		int start = getStartIndex();
		int end = getEndIndex();
		
		List<AddressPoolItem> addressList = pool.getAddressList();
		AddressPoolItem theItem = null;
		
		System.out.println("New allocation [start-end): " + "[" + start + "-" + end + ")");
		
		for (int i = start; i < end; ++i) {
			if (addressList.get(i).isFree() == false 
					&& addressList.get(i).getServer().equals(serverName) == false) {
				theItem = addressList.get(i);
				theItem.setServer(serverName);
			}
		}
	}
	
	public AddressPoolItem getNewAddress() throws NoFreeAddressException {
		int start = getStartIndex();
		int end = getEndIndex();
				
		List<AddressPoolItem> addressList = pool.getAddressList();
		AddressPoolItem theItem = null;
		
		for (int i = start; i < end; ++i) {
			if (addressList.get(i).isFree()) {
				theItem = addressList.get(i);
				break;
			}
		}
		
		// if no free space
		if (theItem == null) {
			throw new NoFreeAddressException();
		} else {
			// lease the address and return the address string
			return theItem;
		}
	}
	
	public void cleanExpiredLeases() {
		pool.cleanExpiredLeases();
	}
	
	private int getStartIndex() {
		return pool.getSize() / numServer * myPosition;
	}
	
	private int getEndIndex() {
		int startIndex, endIndex;
		startIndex = 0 + pool.getSize() / numServer * myPosition;
		endIndex = startIndex + pool.getSize() / numServer;
		
		return endIndex;
	}
	
	public void setLease(String address, String serverName, String clientName, Long expireAt) {
		AddressPoolItem item = pool.getItemByAddress(address);
		item.setServer(serverName);
		item.setClient(clientName);
		item.setExpireAt(expireAt);
	}
	
	public void printAddressList() {
		System.out.println("Address Table:");
		for (AddressPoolItem item : pool.getAddressList()) {
			System.out.println(
					item.getAddress() + 
					"\tclient: " + item.getClient() + 
					"\tserver: " + item.getServer() + 
					"\texpireAt: " + item.getExpireAt());
		}
		System.out.println("");
	}
	
	
	/*
	 * Test Code
	 */
	public static void main(String args[]) {
		AddressPoolLocalManager manager = new AddressPoolLocalManager(AddressPool.getInstance(), "TestServer");
		manager.register(0, 2);
		try {
			for (int i = 0; i < 11; i++) {
				AddressPoolItem item = manager.getNewAddress();
				System.out.println("To be leased: " + item.getAddress());
				item.lease(manager.serverName, "TestClient");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
