package com.badbones69.crazycrates.managers.events.enums;

public enum EventType {

    event_key_given("event_key_given"),
    event_key_sent("event_key_sent"),
    event_key_received("event_key_received"),
    event_key_transferred("event_key_transferred"),
    event_key_removed("event_key_removed"),
    event_key_taken("event_key_taken"),
    event_key_taken_multiple("event_key_taken_multiple"),

    event_command_sent("event_command_sent"),
    event_command_failed("event_command_failed"),

    event_crate_opened("event_crate_opened"),
    event_crate_force_opened("event_crate_force_opened"),
    event_crate_mass_opened("event_crate_mass_opened");

    private final String event;

    EventType(final String event) {
        this.event = event;
    }

    public final String getEvent() {
        return this.event;
    }
}