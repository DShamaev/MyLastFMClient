package com.hikimori911.mylastfmclient.data.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Дмитрий on 12.04.2015.
 */
public class LFMRecentTracks {
    public LFMTrack[] track;
    @SerializedName("@attr")
    public LFMAttr attr;
}
