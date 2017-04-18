package com.vitaliikuznetsov.vkt.model;

/**
 * Created by VItalii on 15/04/2017.
 */

public class Event {

    private int notification;
    private Object object;
    private String message;
    private boolean success;

    private Event(int notification, Object object, String message, boolean success) {
        this.notification = notification;
        this.object = object;
        this.message = message;
        this.success = success;
    }

    static public Event successEvent(int notification, Object object) {
        return new Event(notification, object, null, true);
    }

    static public Event failEvent(int notification, String message) {
        return new Event(notification, null, message, false);
    }

    public int getNotification() {
        return notification;
    }

    public Object getObject() {
        return object;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
