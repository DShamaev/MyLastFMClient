package com.hikimori911.mylastfmclient.data.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Дмитрий on 12.04.2015.
 */
public class LFMTrackArtist {
    @JsonProperty("#text")
    public String text;
    public String mbid;
}
