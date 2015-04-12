package com.hikimori911.mylastfmclient.data.pojo;

/**
 * Created by Дмитрий on 12.04.2015.
 */
public class LFMEvent {
    public long id;
    public String title;
    public LFMArtist artists;
    public LFMVenue venue;
    public String startDate;
    public String description;
    public int attendance;
    public LFMImage[] image;
    public boolean cancelled;
}
