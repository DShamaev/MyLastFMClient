package com.hikimori911.mylastfmclient.data.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Дмитрий on 12.04.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LFMEvent {
    public long id;
    public String title;
    public LFMArtist artists;
    public LFMVenue venue;
    public String startDate;
    public String description;
    public int attendance;
    public LFMImage[] image;
    public int cancelled;
}
