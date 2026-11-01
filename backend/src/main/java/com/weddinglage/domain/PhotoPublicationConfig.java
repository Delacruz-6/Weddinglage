package com.weddinglage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "photo_publication_config")
public class PhotoPublicationConfig {

    @Id
    private Short id = 1;

    @Column(name = "publish_at")
    private Instant publishAt;

    @Column(name = "admin_authorized", nullable = false)
    private boolean adminAuthorized;

    @Column(name = "authorized_by")
    private String authorizedBy;

    @Column(name = "authorized_at")
    private Instant authorizedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Instant getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(Instant publishAt) {
        this.publishAt = publishAt;
    }

    public boolean isAdminAuthorized() {
        return adminAuthorized;
    }

    public void setAdminAuthorized(boolean adminAuthorized) {
        this.adminAuthorized = adminAuthorized;
    }

    public String getAuthorizedBy() {
        return authorizedBy;
    }

    public void setAuthorizedBy(String authorizedBy) {
        this.authorizedBy = authorizedBy;
    }

    public Instant getAuthorizedAt() {
        return authorizedAt;
    }

    public void setAuthorizedAt(Instant authorizedAt) {
        this.authorizedAt = authorizedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
