package com.hikimori911.mylastfmclient.data.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Дмитрий on 12.04.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LFMTrack {
    public LFMTrackArtist artist;
    public String name;
    public String url;
    public LFMDate date;
    public String mbid;
    public int streamable;
}
