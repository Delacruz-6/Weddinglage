package com.weddinglage.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.weddinglage.config.WeddinglageProperties;
import com.weddinglage.web.dto.SpotifyPlaylistDto;
import com.weddinglage.web.error.ApiException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class SpotifyPlaylistService {

    private static final Pattern PLAYLIST_ID = Pattern.compile(
            "(?:spotify:playlist:|open\\.spotify\\.com/(?:intl-[a-z]{2}/)?playlist/)([a-zA-Z0-9]+)");

    private final WeddinglageProperties properties;
    private final ConfigurationService configurationService;
    private final RestClient restClient = RestClient.create();
    private final AtomicReference<CachedToken> tokenCache = new AtomicReference<>();

    public SpotifyPlaylistService(WeddinglageProperties properties, ConfigurationService configurationService) {
        this.properties = properties;
        this.configurationService = configurationService;
    }

    public SpotifyPlaylistDto resolveConfiguredPlaylist() {
        String url = configurationService.getPublic().spotifyPlaylistUrl();
        if (url == null || url.isBlank()) {
            return new SpotifyPlaylistDto(null, null, null, null, List.of());
        }
        return resolve(url.trim());
    }

    public SpotifyPlaylistDto resolve(String playlistUrl) {
        String id = extractPlaylistId(playlistUrl);
        if (id == null) {
            throw new ApiException("VALIDATION", "URL de playlist de Spotify no válida", HttpStatus.BAD_REQUEST);
        }
        String uri = "spotify:playlist:" + id;
        if (!properties.getSpotify().isConfigured()) {
            return new SpotifyPlaylistDto(playlistUrl, uri, id, null, List.of());
        }
        try {
            String accessToken = accessToken();
            JsonNode playlist = restClient.get()
                    .uri("https://api.spotify.com/v1/playlists/{id}?fields=name,tracks.items(track(id,name,uri,preview_url,artists(name)))", id)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(JsonNode.class);
            if (playlist == null) {
                return new SpotifyPlaylistDto(playlistUrl, uri, id, null, List.of());
            }
            String name = text(playlist.get("name"));
            List<SpotifyPlaylistDto.SpotifyTrackDto> tracks = new ArrayList<>();
            JsonNode items = playlist.path("tracks").path("items");
            if (items.isArray()) {
                for (JsonNode item : items) {
                    JsonNode track = item.path("track");
                    if (track.isMissingNode() || track.isNull() || track.path("id").isMissingNode()) {
                        continue;
                    }
                    String trackId = text(track.get("id"));
                    String trackUri = text(track.get("uri"));
                    if (trackId == null || trackUri == null) {
                        continue;
                    }
                    tracks.add(new SpotifyPlaylistDto.SpotifyTrackDto(
                            trackId,
                            trackUri,
                            text(track.get("name")),
                            joinArtists(track.path("artists")),
                            text(track.get("preview_url"))));
                }
            }
            return new SpotifyPlaylistDto(playlistUrl, uri, id, name, List.copyOf(tracks));
        } catch (RestClientException ex) {
            return new SpotifyPlaylistDto(playlistUrl, uri, id, null, List.of());
        }
    }

    private String accessToken() {
        CachedToken cached = tokenCache.get();
        if (cached != null && cached.expiresAt().isAfter(Instant.now().plusSeconds(30))) {
            return cached.value();
        }
        String basic = Base64.getEncoder().encodeToString(
                (properties.getSpotify().getClientId() + ":" + properties.getSpotify().getClientSecret())
                        .getBytes(StandardCharsets.UTF_8));
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        JsonNode body = restClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(JsonNode.class);
        if (body == null || body.path("access_token").isMissingNode()) {
            throw new ApiException("UPSTREAM", "No se pudo autenticar con Spotify", HttpStatus.BAD_GATEWAY);
        }
        String token = body.get("access_token").asText();
        int expiresIn = body.path("expires_in").asInt(3600);
        tokenCache.set(new CachedToken(token, Instant.now().plusSeconds(Math.max(60, expiresIn - 60))));
        return token;
    }

    static String extractPlaylistId(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        Matcher m = PLAYLIST_ID.matcher(input.trim());
        return m.find() ? m.group(1) : null;
    }

    private static String text(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        String value = node.asText();
        return value == null || value.isBlank() || "null".equals(value) ? null : value;
    }

    private static String joinArtists(JsonNode artists) {
        if (artists == null || !artists.isArray() || artists.isEmpty()) {
            return null;
        }
        List<String> names = new ArrayList<>();
        for (JsonNode artist : artists) {
            String name = text(artist.get("name"));
            if (name != null) {
                names.add(name);
            }
        }
        return names.isEmpty() ? null : String.join(", ", names);
    }

    private record CachedToken(String value, Instant expiresAt) {
    }
}
