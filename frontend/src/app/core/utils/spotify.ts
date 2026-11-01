/** Convert a public Spotify playlist URL/URI into a Spotify URI. */
export function toSpotifyPlaylistUri(input: string | null | undefined): string | null {
  if (!input?.trim()) {
    return null;
  }
  const raw = input.trim();
  const uri = raw.match(/^spotify:playlist:([a-zA-Z0-9]+)$/);
  if (uri) {
    return `spotify:playlist:${uri[1]}`;
  }
  try {
    const url = new URL(raw);
    if (!url.hostname.includes('spotify.com')) {
      return null;
    }
    const parts = url.pathname.split('/').filter(Boolean);
    const playlistIdx = parts.indexOf('playlist');
    if (playlistIdx >= 0 && parts[playlistIdx + 1]) {
      return `spotify:playlist:${parts[playlistIdx + 1].split('?')[0]}`;
    }
  } catch {
    return null;
  }
  return null;
}

/** Convert a public Spotify playlist URL/URI into an embeddable iframe src. */
export function toSpotifyEmbedUrl(
  input: string | null | undefined,
  options?: { autoplay?: boolean }
): string | null {
  const playlistUri = toSpotifyPlaylistUri(input);
  if (!playlistUri) {
    return null;
  }
  const id = playlistUri.replace('spotify:playlist:', '');
  const params = new URLSearchParams({
    utm_source: 'generator',
    theme: '0'
  });
  if (options?.autoplay !== false) {
    params.set('autoplay', '1');
  }
  return `https://open.spotify.com/embed/playlist/${id}?${params.toString()}`;
}
