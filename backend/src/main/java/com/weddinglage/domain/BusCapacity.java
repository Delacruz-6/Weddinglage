package com.weddinglage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bus_capacity")
public class BusCapacity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bus_slot", nullable = false, unique = true)
    private String busSlot;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "waitlist_enabled", nullable = false)
    private boolean waitlistEnabled;

    public Long getId() {
        return id;
    }

    public String getBusSlot() {
        return busSlot;
    }

    public void setBusSlot(String busSlot) {
        this.busSlot = busSlot;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isWaitlistEnabled() {
        return waitlistEnabled;
    }

    public void setWaitlistEnabled(boolean waitlistEnabled) {
        this.waitlistEnabled = waitlistEnabled;
    }
}
