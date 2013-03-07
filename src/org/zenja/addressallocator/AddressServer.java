package org.zenja.addressallocator;

import java.util.Vector;

import org.zenja.addressallocator.exceptions.NoFreeAddressException;

import gmi.MembershipListener;
import gmi.ServerSideProxy;
import gmi.View;
import gmi.protocols.Anycast;

public class AddressServer implements DHCP, InternalDHCP, MembershipListener {

	private static final long serialVersionUID = 1L;
	
	private static final String GROUP_NAME="allocator-server-group";
    private static final String SERVER_ADDRESS = "localhost";
    
    private static String serverName = null;
    private static int port = 0;
    
    private ServerSideProxy proxy;
    private InternalDHCP internalDHCP;
    
    private AddressPool pool;
    private AddressPoolLocalManager manager;

	public static void main(String[] args) {
		try {
			for (int i = 0; i < args.length; i += 2) {
				if (args[i].equals("-c")) {
					serverName = args[i + 1];
				} else if (args[i].equals("-p")) {
					port = Integer.parseInt(args[i + 1]);
				} else {
					usage();
				}
			}
		} catch (Exception e) {
			usage();
		}
		
		new AddressServer();
		
		// thread sleep to avoid cpu utilization 
		// when they are waiting for any messages
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void usage() {
		System.out.println("Usage Server :: server -c <srvname> -p <port>");
		System.exit(1);
	}
	
	public AddressServer() {
		// Get address pool and manager
		pool = AddressPool.getInstance();
		manager = new AddressPoolLocalManager(pool, serverName);
		
		// Getting ServerSideProxy Object to communicate with Spread Daemon.
    	proxy = new ServerSideProxy(this, port, serverName, SERVER_ADDRESS);
    	
    	// Join server group.
    	proxy.join(GROUP_NAME);
    	
    	// Get InternalDHCP
    	internalDHCP = (InternalDHCP)proxy.getInternalStub(InternalDHCP.class);
	}
	
	public void printAddresses() {
		manager.printAddressList();
	}
	
	@Anycast public AddressPoolItem leaseNewAddress(String clientName) {
		AddressPoolItem item = null;
		String address = null;
		try {
			// get a new address
			item = manager.getNewAddress();
			address = item.getAddress();
			
			// lease the address (only change local state)
			item.lease(serverName, clientName);
			
			// notify other servers to spread the change
			Long expireAt = item.getExpireAt();
			internalDHCP.setLease(address, serverName, clientName, expireAt);
			
		} catch (NoFreeAddressException e) {
			System.err.println("Addresses on server " + serverName + " are all occupied, please wait.");
			System.err.println("Now cleaning the expired leases...");
			manager.cleanExpiredLeases();
			
			// notify other server to clean expired leases;
			internalDHCP.notifyExpiration();
		}
		
		return item;
	}
	
	/*
	 * InternalDHCP#setLease
	 */
	@Override
	public void setLease(String address, String serverName, String clientName, Long expireAt) {
		manager.setLease(address, serverName, clientName, expireAt);
		System.out.println(
				"[Internal Call: Set Lease] server: " + serverName + 
				", client: " + clientName + 
				", expireAt: " + expireAt);
		System.out.println("\nNow the new address table looks like: ");
		manager.printAddressList();
	}
	
	/*
	 * InternalDHCP#notifyExpiration
	 */
	@Override
	public void notifyExpiration() {
		System.out.println("Expiration notification received, now cleaning...");
		manager.cleanExpiredLeases();
		System.out.println("Expiration items cleaned! Now the table looks like: ");
		manager.printAddressList();
	}
	

	@Override
	public void ViewChange(View view) {
		int myPosition = -1;
		int numServer = view.getView().size();
		String identifier = proxy.getIdentifier();
		
		for (int i = 0; i < numServer; i++) {
			if (view.memberHasPosition(i, identifier)) {
				myPosition = i;
				break;
			}
		}
		
		if (myPosition != -1) {
			// perform (re-)registration and take-over action
			manager.register(myPosition, numServer);
			
			// print new (local) pool state
			System.out.println("System rearrange occured, now the address table is: ");
			manager.printAddressList();
		}
	}

}
