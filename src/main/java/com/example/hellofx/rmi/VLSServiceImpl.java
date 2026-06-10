package com.example.hellofx.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Server-side implementation of {@link VLSService}.
 * All database access happens here; clients never touch the DB directly.
 */

// UnicastRemoteObject makes the object accessible over a network
public class VLSServiceImpl extends UnicastRemoteObject implements VLSService {

    /** Active JDBC connection to the MySQL database on the server. */
    private Connection conn;

    /**
     * Constructs the service and opens a database connection.
     * @throws RemoteException if RMI initialisation fails
     */
    public VLSServiceImpl() throws RemoteException {
        super();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/dbmovierentals", "root", ""
            );
            System.out.println("[Server] Database connected.");
        } catch (Exception e) {
            System.err.println("[Server] DB connection failed: " + e.getMessage());
        }
    }

    // ── Genre operations ──────────────────────────────────────────────────────

    @Override
    public List<String[]> getAllGenres() throws RemoteException {
        List<String[]> list = new ArrayList<>();
        try {
            ResultSet rs = conn.prepareStatement(
                    "SELECT id, genre FROM genres WHERE isactive = 1"
            ).executeQuery();
            while (rs.next())
                list.add(new String[]{ String.valueOf(rs.getInt("id")), rs.getString("genre") });
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    @Override
    public boolean addGenre(String name) throws RemoteException {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO genres (genre, isactive) VALUES (?, 1)"
            );
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    @Override
    public boolean removeGenre(int genreId) throws RemoteException {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE genres SET isactive = 0 WHERE id = ?"
            );
            ps.setInt(1, genreId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    // ── Movie operations ──────────────────────────────────────────────────────

    @Override
    public boolean addMovie(String title, int genreId) throws RemoteException {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO movies (genre_id, Title, isactive) VALUES (?, ?, 1)"
            );
            ps.setInt(1, genreId); ps.setString(2, title);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    @Override
    public boolean removeMovie(int movieId) throws RemoteException {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE movies SET isactive = 0 WHERE id = ?"
            );
            ps.setInt(1, movieId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    @Override
    public List<String[]> getActiveMovies() throws RemoteException {
        List<String[]> list = new ArrayList<>();
        try {
            ResultSet rs = conn.prepareStatement(
                    "SELECT m.id, m.Title, g.genre FROM movies m " +
                            "JOIN genres g ON m.genre_id = g.id WHERE m.isactive = 1"
            ).executeQuery();
            while (rs.next())
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("Title"),
                        rs.getString("genre")
                });
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    @Override
    public List<String[]> getMoviesByGenre(int genreId) throws RemoteException {
        List<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, Title FROM movies WHERE genre_id = ? AND isactive = 1"
            );
            ps.setInt(1, genreId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new String[]{ String.valueOf(rs.getInt("id")), rs.getString("Title") });
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    // ── Client operations ─────────────────────────────────────────────────────

    @Override
    public boolean registerClient(String fullname, String phone, String email) throws RemoteException {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO clients (Fullname, Phone, Email, isactive) VALUES (?, ?, ?, 1)"
            );
            ps.setString(1, fullname); ps.setString(2, phone); ps.setString(3, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    @Override
    public boolean removeClient(int clientId) throws RemoteException {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE clients SET isactive = 0 WHERE id = ?"
            );
            ps.setInt(1, clientId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    @Override
    public List<String[]> getActiveClients() throws RemoteException {
        List<String[]> list = new ArrayList<>();
        try {
            ResultSet rs = conn.prepareStatement(
                    "SELECT id, Fullname FROM clients WHERE isactive = 1"
            ).executeQuery();
            while (rs.next())
                list.add(new String[]{ String.valueOf(rs.getInt("id")), rs.getString("Fullname") });
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    // ── Rental operations ─────────────────────────────────────────────────────

    @Override
    public boolean rentMovie(int clientId, int movieId) throws RemoteException {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO rentals (client_id, movie_id, Returned) VALUES (?, ?, 0)"
            );
            ps.setInt(1, clientId); ps.setInt(2, movieId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    @Override
    public boolean returnMovie(int rentalId) throws RemoteException {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE rentals SET Returned = 1 WHERE id = ?"
            );
            ps.setInt(1, rentalId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    @Override
    public List<String[]> getActiveRentals(int clientId) throws RemoteException {
        List<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT r.id, m.Title FROM rentals r " +
                            "JOIN movies m ON r.movie_id = m.id " +
                            "WHERE r.client_id = ? AND r.Returned = 0"
            );
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new String[]{ String.valueOf(rs.getInt("id")), rs.getString("Title") });
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    @Override
    public List<String[]> getReturnedRentals(int clientId) throws RemoteException {
        List<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT r.id, m.Title FROM rentals r " +
                            "JOIN movies m ON r.movie_id = m.id " +
                            "WHERE r.client_id = ? AND r.Returned = 1"
            );
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new String[]{ String.valueOf(rs.getInt("id")), rs.getString("Title") });
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }
}