package org.stuchat.chatserver.exceptions;

import lombok.Data;

import java.util.Date;

@Data
public class StandardException {
    private int status;
    private String message;
    private Date timestamp;

    public StandardException(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}