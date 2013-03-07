package org.zenja.addressallocator;


import java.util.ArrayList;
import java.util.List;

import gmi.GroupProxy;

public class AddressClient {

	private static final String GROUP_NAME="allocator-server-group";
    private static final String SERVER_ADDRESS = "localhost";

    private static String clientName;
    private static int port;
    private static int numLease;	// the num of addresses the client want to lease
    
    DHCP dhcpServer = null;
    
    private GroupProxy groupProxy;
    
	public static void main(String[] args) {
		try {
			for (int i = 0; i < args.length; i += 2) {
				if (args[i].equals("-c")) {
					clientName = args[i + 1];
				} else if (args[i].equals("-p")) {
					port = Integer.parseInt(args[i + 1]);
				} else if (args[i].equals("-n")) {
					numLease = Integer.parseInt(args[i + 1]);
				} else {
					usage();
				}
			}
		} catch (Exception e) {
			usage();
		}
		
		new AddressClient();
	}
	
	public AddressClient() {
		/*
		 * Getting GroupProxy Object to communicate with 
		 * Object group through Spread Toolkit.
		 */
		groupProxy = new GroupProxy(this, clientName, port, GROUP_NAME, SERVER_ADDRESS);
		
		/*
		 * Retrieve a proxy for an object group 
		 * implementing the DHCP interface.
		 */
		dhcpServer = (DHCP)groupProxy.getServer();
		
		/*
		 * Lease numLease addresses
		 */
		System.out.println("[Step 1] Lease " + numLease + "addresses: \n");
		
		for (int i = 0; i < numLease; i++) {
			AddressPoolItem item = dhcpServer.leaseNewAddress(clientName);
			if (item == null) {
				System.out.println("There are no free addresses on the server you are connecting to. " +
						"Please try again later.");
			} else {
				System.out.println("New address leased: " + item.getAddress());
				System.out.println("Server: " + item.getServer());
				System.out.println("ExpireAt: " + item.getExpireAt());
			}
		}
		
		/*
		 * Get lease info
		 */
		System.out.println("\n[Step 2] Get some lease info: \n");
		
		List<String> partAddressListOne = new ArrayList<String>();
		partAddressListOne.add("129.241.209.1");
		partAddressListOne.add("129.241.209.5");
		partAddressListOne.add("129.241.209.10");
		partAddressListOne.add("129.241.209.11");
		partAddressListOne.add("129.241.209.15");
		partAddressListOne.add("129.241.209.20");
		
		for (String s : partAddressListOne) {
			AddressPoolItem item = dhcpServer.getLeaseInfo(s);
			System.out.println("\nLease info for " + item.getAddress() +": ");
			System.out.println(
					item.getAddress() + 
					"\tclient: " + item.getClient() + 
					"\tserver: " + item.getServer() + 
					"\texpireAt: " + item.getExpireAt());
		}
		
		/*
		 * Sleep for a while
		 */
		System.out.println("\nNow sleep for 3 seconds...\n");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		/*
		 * Renew lease
		 */
		System.out.println("\n[Step 3] Try to refresh (extend the lease of) some addresses: \n");
		
		List<String> partAddressListTwo = new ArrayList<String>();
		partAddressListTwo.add("129.241.209.1");
		partAddressListTwo.add("129.241.209.3");
		partAddressListTwo.add("129.241.209.5");
		partAddressListTwo.add("129.241.209.7");
		partAddressListTwo.add("129.241.209.9");
		partAddressListTwo.add("129.241.209.11");
		partAddressListTwo.add("129.241.209.13");
		partAddressListTwo.add("129.241.209.15");
		partAddressListTwo.add("129.241.209.17");
		partAddressListTwo.add("129.241.209.19");
		
		for (String s : partAddressListTwo) {
			AddressPoolItem item = dhcpServer.getLeaseInfo(s);
			
			System.out.println("\nTrying to extend the lease of address: " + s);
			System.out.println("Current lease info: ");
			System.out.println(
					item.getAddress() + 
					"\tclient: " + item.getClient() + 
					"\tserver: " + item.getServer() + 
					"\texpireAt: " + item.getExpireAt());
			
			boolean isSuccess = dhcpServer.refresh(s, clientName);
			if (isSuccess == true) {
				System.out.println("Lease successfully extended, now the new lease information is: ");
				
				item = dhcpServer.getLeaseInfo(s);
				
				System.out.println(
						item.getAddress() + 
						"\tclient: " + item.getClient() + 
						"\tserver: " + item.getServer() + 
						"\texpireAt: " + item.getExpireAt());
			} else {
				System.out.println("Failed to extend the lease, " +
						"check if the address is not in use, " +
						"or if the address is leased for " + clientName + " yourself");
			}
		}
		
	}

	private static void usage() {
    	System.out.println("Usage Client :: Client -c <clientname> -p <port> -n <numLease>");
    	System.exit(1);
    }
}
