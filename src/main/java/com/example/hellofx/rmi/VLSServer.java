package com.example.hellofx.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Entry point for the VLS RMI Server.
 * Run this on the Server PC before launching any client.
 */
public class VLSServer {

    /**
     * Starts the RMI registry and binds the VLS service.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // IMPORTANT: set this to PC 1's actual LAN IP address
            System.setProperty("java.rmi.server.hostname", "192.168.100.36");

            VLSServiceImpl service = new VLSServiceImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("VLSService", service);

            System.out.println("[Server] VLS Server running on port 1099. Waiting for clients...");
        } catch (Exception e) {
            System.err.println("[Server] Startup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}