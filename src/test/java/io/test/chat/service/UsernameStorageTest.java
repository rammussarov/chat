package io.test.chat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsernameStorageTest {

    UsernameStorage usernameStorage;

    @BeforeEach
    void setUp() {
        usernameStorage = new UsernameStorage();
        usernameStorage.addNewUser("Jack");
        usernameStorage.addNewUser("Paul");
        usernameStorage.addNewUser("John");
        usernameStorage.addNewUser("Barry");
    }

    @Test
    void getUsernamesTest() {
        assertEquals(4, usernameStorage.getUsernames().size());
    }

    @Test
    void addNewUserTest() {
        usernameStorage.addNewUser("Mike");
        assertEquals(5, usernameStorage.getUsernames().size());
    }

    @Test
    void isExistTrueTest() {
        final boolean exists = usernameStorage.isExist("Jack");
        assertEquals(true, exists);
    }

    @Test
    void isExistFalseTest() {
        final boolean exists = usernameStorage.isExist("Kate");
        assertEquals(false, exists);
    }

    @Test
    void removeUserTest() {
        usernameStorage.removeUser("Jack");
        assertEquals(3, usernameStorage.getUsernames().size());
    }
}