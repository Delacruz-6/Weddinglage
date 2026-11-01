package com.weddinglage.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rsvp")
public class Rsvp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitation_id", unique = true)
    private Invitation invitation;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "edit_token_hash", nullable = false)
    private String editTokenHash;

    @Column(nullable = false)
    private boolean attending;

    @Column(nullable = false)
    private int adults;

    @Column(nullable = false)
    private int children;

    private String notes;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "rsvp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietaryRequirement> dietaryRequirements = new ArrayList<>();

    @OneToMany(mappedBy = "rsvp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransportReservation> transportReservations = new ArrayList<>();

    @OneToMany(mappedBy = "rsvp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SongSuggestion> songSuggestions = new ArrayList<>();

    @OneToMany(mappedBy = "rsvp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RsvpChild> childGuests = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Invitation getInvitation() {
        return invitation;
    }

    public void setInvitation(Invitation invitation) {
        this.invitation = invitation;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEditTokenHash() {
        return editTokenHash;
    }

    public void setEditTokenHash(String editTokenHash) {
        this.editTokenHash = editTokenHash;
    }

    public boolean isAttending() {
        return attending;
    }

    public void setAttending(boolean attending) {
        this.attending = attending;
    }

    public int getAdults() {
        return adults;
    }

    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<DietaryRequirement> getDietaryRequirements() {
        return dietaryRequirements;
    }

    public List<TransportReservation> getTransportReservations() {
        return transportReservations;
    }

    public List<SongSuggestion> getSongSuggestions() {
        return songSuggestions;
    }

    public List<RsvpChild> getChildGuests() {
        return childGuests;
    }
}
