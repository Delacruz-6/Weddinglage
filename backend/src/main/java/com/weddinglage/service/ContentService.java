package com.weddinglage.service;

import com.weddinglage.domain.PlaylistTrack;
import com.weddinglage.domain.TimelineEvent;
import com.weddinglage.domain.Venue;
import com.weddinglage.domain.VenueImage;
import com.weddinglage.repository.PlaylistTrackRepository;
import com.weddinglage.repository.TimelineEventRepository;
import com.weddinglage.repository.VenueRepository;
import com.weddinglage.web.dto.PlaylistTrackDto;
import com.weddinglage.web.dto.TimelineEventDto;
import com.weddinglage.web.dto.VenueDto;
import com.weddinglage.web.error.ApiException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentService {

    private final VenueRepository venueRepository;
    private final TimelineEventRepository timelineEventRepository;
    private final PlaylistTrackRepository playlistTrackRepository;

    public ContentService(
            VenueRepository venueRepository,
            TimelineEventRepository timelineEventRepository,
            PlaylistTrackRepository playlistTrackRepository) {
        this.venueRepository = venueRepository;
        this.timelineEventRepository = timelineEventRepository;
        this.playlistTrackRepository = playlistTrackRepository;
    }

    @Transactional(readOnly = true)
    public VenueDto getVenue() {
        return toVenueDto(requireVenue());
    }

    @Transactional
    public VenueDto updateVenue(VenueDto dto) {
        Venue venue = requireVenue();
        venue.setName(dto.name());
        venue.setAddressLine(dto.addressLine());
        venue.setCity(dto.city());
        venue.setPostalCode(dto.postalCode());
        venue.setCountry(dto.country());
        venue.setLatitude(dto.latitude());
        venue.setLongitude(dto.longitude());
        venue.setHowToArrive(dto.howToArrive());
        venue.setParkingNotes(dto.parkingNotes());
        venue.getImages().clear();
        if (dto.images() != null) {
            for (VenueDto.VenueImageDto imageDto : dto.images()) {
                VenueImage image = new VenueImage();
                image.setVenue(venue);
                image.setImageUrl(imageDto.imageUrl());
                image.setAltText(imageDto.altText());
                image.setSortOrder(imageDto.sortOrder());
                venue.getImages().add(image);
            }
        }
        venue.setUpdatedAt(Instant.now());
        return toVenueDto(venueRepository.save(venue));
    }

    @Transactional(readOnly = true)
    public List<TimelineEventDto> listTimeline() {
        return timelineEventRepository.findAllByOrderBySortOrderAscEventTimeAsc().stream()
                .map(this::toTimelineDto)
                .toList();
    }

    @Transactional
    public TimelineEventDto createTimeline(TimelineEventDto dto) {
        TimelineEvent event = new TimelineEvent();
        applyTimeline(event, dto);
        return toTimelineDto(timelineEventRepository.save(event));
    }

    @Transactional
    public TimelineEventDto updateTimeline(Long id, TimelineEventDto dto) {
        TimelineEvent event = timelineEventRepository.findById(id)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Evento no encontrado", HttpStatus.NOT_FOUND));
        applyTimeline(event, dto);
        event.setUpdatedAt(Instant.now());
        return toTimelineDto(timelineEventRepository.save(event));
    }

    @Transactional
    public void deleteTimeline(Long id) {
        if (!timelineEventRepository.existsById(id)) {
            throw new ApiException("NOT_FOUND", "Evento no encontrado", HttpStatus.NOT_FOUND);
        }
        timelineEventRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PlaylistTrackDto> listPlaylist() {
        return playlistTrackRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .map(this::toPlaylistDto)
                .toList();
    }

    @Transactional
    public PlaylistTrackDto createTrack(PlaylistTrackDto dto) {
        PlaylistTrack track = new PlaylistTrack();
        applyTrack(track, dto);
        return toPlaylistDto(playlistTrackRepository.save(track));
    }

    @Transactional
    public PlaylistTrackDto updateTrack(Long id, PlaylistTrackDto dto) {
        PlaylistTrack track = playlistTrackRepository.findById(id)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Pista no encontrada", HttpStatus.NOT_FOUND));
        applyTrack(track, dto);
        track.setUpdatedAt(Instant.now());
        return toPlaylistDto(playlistTrackRepository.save(track));
    }

    @Transactional
    public void deleteTrack(Long id) {
        if (!playlistTrackRepository.existsById(id)) {
            throw new ApiException("NOT_FOUND", "Pista no encontrada", HttpStatus.NOT_FOUND);
        }
        playlistTrackRepository.deleteById(id);
    }

    private Venue requireVenue() {
        return venueRepository.findWithImagesById((short) 1)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Venue no encontrado", HttpStatus.NOT_FOUND));
    }

    private void applyTimeline(TimelineEvent event, TimelineEventDto dto) {
        event.setEventTime(dto.eventTime());
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setIcon(dto.icon());
        event.setSortOrder(dto.sortOrder());
    }

    private void applyTrack(PlaylistTrack track, PlaylistTrackDto dto) {
        track.setTitle(dto.title());
        track.setArtist(dto.artist());
        track.setSourceUrl(dto.sourceUrl());
        track.setLicenseNote(dto.licenseNote());
        track.setSortOrder(dto.sortOrder());
    }

    private VenueDto toVenueDto(Venue venue) {
        List<VenueDto.VenueImageDto> images = venue.getImages().stream()
                .map(i -> new VenueDto.VenueImageDto(i.getId(), i.getImageUrl(), i.getAltText(), i.getSortOrder()))
                .toList();
        return new VenueDto(
                venue.getName(),
                venue.getAddressLine(),
                venue.getCity(),
                venue.getPostalCode(),
                venue.getCountry(),
                venue.getLatitude(),
                venue.getLongitude(),
                venue.getHowToArrive(),
                venue.getParkingNotes(),
                images);
    }

    private TimelineEventDto toTimelineDto(TimelineEvent event) {
        return new TimelineEventDto(
                event.getId(),
                event.getEventTime(),
                event.getTitle(),
                event.getDescription(),
                event.getIcon(),
                event.getSortOrder());
    }

    private PlaylistTrackDto toPlaylistDto(PlaylistTrack track) {
        return new PlaylistTrackDto(
                track.getId(),
                track.getTitle(),
                track.getArtist(),
                track.getSourceUrl(),
                track.getLicenseNote(),
                track.getSortOrder());
    }
}
