package com.example.hellofx.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for the Video Library System.
 * Defines every operation available to Admin and Customer clients via RMI.
 */
public interface VLSService extends Remote {

    // ── Genre operations ─────────────────────────────────────────────────────

    /**
     * Returns all active genres as [id, name].
     * @return list of genre arrays
     * @throws RemoteException if a network communication error occurs
     */
    List<String[]> getAllGenres() throws RemoteException;

    /**
     * Adds a new genre to the database.
     * @param name the genre name
     * @return true if the insert succeeded
     * @throws RemoteException if a network communication error occurs
     */
    boolean addGenre(String name) throws RemoteException;

    /**
     * Soft-deletes a genre by setting isactive to 0.
     * @param genreId the genre's primary key
     * @return true if the update succeeded
     * @throws RemoteException if a network communication error occurs
     */
    boolean removeGenre(int genreId) throws RemoteException;

    // ── Movie operations ──────────────────────────────────────────────────────

    /**
     * Adds a new movie linked to a genre.
     * @param title   the movie title
     * @param genreId foreign key referencing genres.id
     * @return true if the insert succeeded
     * @throws RemoteException if a network communication error occurs
     */
    boolean addMovie(String title, int genreId) throws RemoteException;

    /**
     * Soft-deletes a movie by setting isactive to 0.
     * @param movieId the movie's primary key
     * @return true if the update succeeded
     * @throws RemoteException if a network communication error occurs
     */
    boolean removeMovie(int movieId) throws RemoteException;

    /**
     * Returns all active movies joined with genre name, as [id, title, genre].
     * @return list of movie arrays
     * @throws RemoteException if a network communication error occurs
     */
    List<String[]> getActiveMovies() throws RemoteException;

    /**
     * Returns active movies for one specific genre, as [id, title].
     * Used by the Rentals GUI to filter the Movies combo by genre.
     * @param genreId the genre to filter by
     * @return list of movie arrays
     * @throws RemoteException if a network communication error occurs
     */
    List<String[]> getMoviesByGenre(int genreId) throws RemoteException;

    // ── Client operations ─────────────────────────────────────────────────────

    /**
     * Registers a new customer in the database.
     * @param fullname the customer's full name
     * @param phone    the customer's phone number
     * @param email    the customer's email address
     * @return true if the insert succeeded
     * @throws RemoteException if a network communication error occurs
     */
    boolean registerClient(String fullname, String phone, String email) throws RemoteException;

    /**
     * Soft-deletes a customer by setting isactive to 0.
     * @param clientId the client's primary key
     * @return true if the update succeeded
     * @throws RemoteException if a network communication error occurs
     */
    boolean removeClient(int clientId) throws RemoteException;

    /**
     * Returns all active clients as [id, fullname].
     * @return list of client arrays
     * @throws RemoteException if a network communication error occurs
     */
    List<String[]> getActiveClients() throws RemoteException;

    // ── Rental operations ─────────────────────────────────────────────────────

    /**
     * Records a new rental for a client.
     * @param clientId the client's primary key
     * @param movieId  the movie's primary key
     * @return true if the insert succeeded
     * @throws RemoteException if a network communication error occurs
     */
    boolean rentMovie(int clientId, int movieId) throws RemoteException;

    /**
     * Marks a rental as returned (Returned = 1).
     * @param rentalId the rental record's primary key
     * @return true if the update succeeded
     * @throws RemoteException if a network communication error occurs
     */
    boolean returnMovie(int rentalId) throws RemoteException;

    /**
     * Returns all unreturned (Returned = 0) rentals for a client, as [rental_id, title].
     * Populates the Borrowed combo in the Rentals GUI.
     * @param clientId the client's primary key
     * @return list of active rental arrays
     * @throws RemoteException if a network communication error occurs
     */
    List<String[]> getActiveRentals(int clientId) throws RemoteException;

    /**
     * Returns all completed (Returned = 1) rentals for a client, as [rental_id, title].
     * Populates the Returned combo in the Rentals GUI.
     * @param clientId the client's primary key
     * @return list of returned rental arrays
     * @throws RemoteException if a network communication error occurs
     */
    List<String[]> getReturnedRentals(int clientId) throws RemoteException;
}