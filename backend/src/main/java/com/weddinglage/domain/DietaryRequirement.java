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
@Table(name = "dietary_requirement")
public class DietaryRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rsvp_id", nullable = false)
    private Rsvp rsvp;

    @Column(name = "requirement_type", nullable = false)
    private String requirementType;

    private String detail;

    public Long getId() {
        return id;
    }

    public Rsvp getRsvp() {
        return rsvp;
    }

    public void setRsvp(Rsvp rsvp) {
        this.rsvp = rsvp;
    }

    public String getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(String requirementType) {
        this.requirementType = requirementType;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
