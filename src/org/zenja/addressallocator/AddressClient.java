package org.zenja.addressallocator;


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
	}

	private static void usage() {
    	System.out.println("Usage Client :: Client -c <clientname> -p <port> -n <numLease>");
    	System.exit(1);
    }
}
