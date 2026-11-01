package com.weddinglage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "wedding_configuration")
public class WeddingConfiguration {

    @Id
    private Short id = 1;

    @Column(name = "partner_names")
    private String partnerNames;

    @Column(name = "wedding_at", nullable = false)
    private Instant weddingAt;

    @Column(name = "welcome_message")
    private String welcomeMessage;

    @Column(name = "story_html")
    private String storyHtml;

    @Column(name = "gift_iban")
    private String giftIban;

    @Column(name = "gift_holder")
    private String giftHolder;

    @Column(name = "show_story", nullable = false)
    private boolean showStory = true;

    @Column(name = "show_venue", nullable = false)
    private boolean showVenue = true;

    @Column(name = "show_timeline", nullable = false)
    private boolean showTimeline = true;

    @Column(name = "show_playlist", nullable = false)
    private boolean showPlaylist = true;

    @Column(name = "show_gifts", nullable = false)
    private boolean showGifts = true;

    @Column(name = "show_rsvp", nullable = false)
    private boolean showRsvp = true;

    @Column(name = "show_photos", nullable = false)
    private boolean showPhotos = true;

    @Column(name = "show_ornaments", nullable = false)
    private boolean showOrnaments = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getPartnerNames() {
        return partnerNames;
    }

    public void setPartnerNames(String partnerNames) {
        this.partnerNames = partnerNames;
    }

    public Instant getWeddingAt() {
        return weddingAt;
    }

    public void setWeddingAt(Instant weddingAt) {
        this.weddingAt = weddingAt;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public String getStoryHtml() {
        return storyHtml;
    }

    public void setStoryHtml(String storyHtml) {
        this.storyHtml = storyHtml;
    }

    public String getGiftIban() {
        return giftIban;
    }

    public void setGiftIban(String giftIban) {
        this.giftIban = giftIban;
    }

    public String getGiftHolder() {
        return giftHolder;
    }

    public void setGiftHolder(String giftHolder) {
        this.giftHolder = giftHolder;
    }

    public boolean isShowStory() {
        return showStory;
    }

    public void setShowStory(boolean showStory) {
        this.showStory = showStory;
    }

    public boolean isShowVenue() {
        return showVenue;
    }

    public void setShowVenue(boolean showVenue) {
        this.showVenue = showVenue;
    }

    public boolean isShowTimeline() {
        return showTimeline;
    }

    public void setShowTimeline(boolean showTimeline) {
        this.showTimeline = showTimeline;
    }

    public boolean isShowPlaylist() {
        return showPlaylist;
    }

    public void setShowPlaylist(boolean showPlaylist) {
        this.showPlaylist = showPlaylist;
    }

    public boolean isShowGifts() {
        return showGifts;
    }

    public void setShowGifts(boolean showGifts) {
        this.showGifts = showGifts;
    }

    public boolean isShowRsvp() {
        return showRsvp;
    }

    public void setShowRsvp(boolean showRsvp) {
        this.showRsvp = showRsvp;
    }

    public boolean isShowPhotos() {
        return showPhotos;
    }

    public void setShowPhotos(boolean showPhotos) {
        this.showPhotos = showPhotos;
    }

    public boolean isShowOrnaments() {
        return showOrnaments;
    }

    public void setShowOrnaments(boolean showOrnaments) {
        this.showOrnaments = showOrnaments;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
