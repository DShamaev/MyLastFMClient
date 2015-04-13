package com.hikimori911.mylastfmclient.data.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Дмитрий on 12.04.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LFMVenue {
    public long id;
    public String name;
    public LFMLocation location;
}
