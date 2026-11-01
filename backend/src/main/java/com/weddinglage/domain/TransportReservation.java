package com.weddinglage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transport_reservation")
public class TransportReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rsvp_id", nullable = false)
    private Rsvp rsvp;

    @Column(name = "bus_slot", nullable = false)
    private String busSlot;

    @Column(name = "stop_id")
    private String stopId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private int seats = 1;

    public Long getId() {
        return id;
    }

    public Rsvp getRsvp() {
        return rsvp;
    }

    public void setRsvp(Rsvp rsvp) {
        this.rsvp = rsvp;
    }

    public String getBusSlot() {
        return busSlot;
    }

    public void setBusSlot(String busSlot) {
        this.busSlot = busSlot;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }
}
