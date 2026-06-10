package com.example.hellofx.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Utility class that connects to the RMI server and returns the service stub.
 */
public class RMIClient {

    /** IP address of the Server PC.  */
    private static final String SERVER_IP = "10.55.29.100";

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