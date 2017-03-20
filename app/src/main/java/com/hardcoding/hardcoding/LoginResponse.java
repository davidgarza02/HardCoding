package com.hardcoding.hardcoding;

import java.util.List;

/**
 * Created by davidgarza on 27/02/17.
 */
public class LoginResponse {
    private User user;
    private List<Transaction> transactions;

    public User getUser() {
        return user;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
