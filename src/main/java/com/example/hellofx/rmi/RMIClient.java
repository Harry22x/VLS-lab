package com.example.hellofx.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Utility class that connects to the RMI server and returns the service stub.
 */
public class RMIClient {

    /** IP address of the Server PC. Change this to PC 1's LAN IP. */
    private static final String SERVER_IP = "192.168.100.36";

    /** RMI registry port (must match VLSServer). */
    private static final int PORT = 1099;

    /**
     * Looks up and returns the remote VLSService stub.
     * @return a connected {@link VLSService} stub
     * @throws Exception if the server cannot be reached
     */
    public static VLSService getService() throws Exception {
        Registry registry = LocateRegistry.getRegistry(SERVER_IP, PORT);
        return (VLSService) registry.lookup("VLSService");
    }
}