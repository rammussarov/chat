package io.test.chat.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 *  Service for storing active users.
 */

@Service
public class UsernameStorage {
    private Set<String> usernames = new HashSet<>();

    /**
     * Returns all active users.
     *
     * @return set of all active users.
     */
    public Set<String> getUsernames() {
        return usernames;
    }

    /**
     * Adds new user to storage.
     *
     * @param username - new user to add.
     */
    public void addNewUser(String username) {
        usernames.add(username);
    }

    /**
     * Checks if user is presented in storage.
     *
     * @param username - user for checking.
     * @return <tt>true</tt> if storage contains passed username.
     */
    public boolean isExist(String username) {
        return usernames.contains(username);
    }

    /**
     * Removes user from storage.
     *
     * @param username - user for removal.
     */
    public void removeUser(String username) {
        usernames.remove(username);
    }
}
