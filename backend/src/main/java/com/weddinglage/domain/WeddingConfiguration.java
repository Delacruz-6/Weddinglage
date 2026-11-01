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

    @Column(name = "hero_image_url")
    private String heroImageUrl;

    @Column(name = "story_image_url")
    private String storyImageUrl;

    @Column(name = "spotify_playlist_url")
    private String spotifyPlaylistUrl;

    @Column(name = "ornament_suggestion")
    private String ornamentSuggestion;

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

    @Column(name = "rsvp_show_notes", nullable = false)
    private boolean rsvpShowNotes = true;

    @Column(name = "rsvp_show_dietary", nullable = false)
    private boolean rsvpShowDietary = true;

    @Column(name = "rsvp_show_transport", nullable = false)
    private boolean rsvpShowTransport = true;

    @Column(name = "rsvp_show_songs", nullable = false)
    private boolean rsvpShowSongs = true;

    @Column(name = "rsvp_show_children", nullable = false)
    private boolean rsvpShowChildren = true;

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

    public String getHeroImageUrl() {
        return heroImageUrl;
    }

    public void setHeroImageUrl(String heroImageUrl) {
        this.heroImageUrl = heroImageUrl;
    }

    public String getStoryImageUrl() {
        return storyImageUrl;
    }

    public void setStoryImageUrl(String storyImageUrl) {
        this.storyImageUrl = storyImageUrl;
    }

    public String getSpotifyPlaylistUrl() {
        return spotifyPlaylistUrl;
    }

    public void setSpotifyPlaylistUrl(String spotifyPlaylistUrl) {
        this.spotifyPlaylistUrl = spotifyPlaylistUrl;
    }

    public String getOrnamentSuggestion() {
        return ornamentSuggestion;
    }

    public void setOrnamentSuggestion(String ornamentSuggestion) {
        this.ornamentSuggestion = ornamentSuggestion;
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

    public boolean isRsvpShowNotes() {
        return rsvpShowNotes;
    }

    public void setRsvpShowNotes(boolean rsvpShowNotes) {
        this.rsvpShowNotes = rsvpShowNotes;
    }

    public boolean isRsvpShowDietary() {
        return rsvpShowDietary;
    }

    public void setRsvpShowDietary(boolean rsvpShowDietary) {
        this.rsvpShowDietary = rsvpShowDietary;
    }

    public boolean isRsvpShowTransport() {
        return rsvpShowTransport;
    }

    public void setRsvpShowTransport(boolean rsvpShowTransport) {
        this.rsvpShowTransport = rsvpShowTransport;
    }

    public boolean isRsvpShowSongs() {
        return rsvpShowSongs;
    }

    public void setRsvpShowSongs(boolean rsvpShowSongs) {
        this.rsvpShowSongs = rsvpShowSongs;
    }

    public boolean isRsvpShowChildren() {
        return rsvpShowChildren;
    }

    public void setRsvpShowChildren(boolean rsvpShowChildren) {
        this.rsvpShowChildren = rsvpShowChildren;
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
