package com.hikimori911.mylastfmclient.data.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Дмитрий on 12.04.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LFMRecentTracks {
    @JsonIgnore
    public LFMTrack[] tracks;

    @JsonSetter("track")
    private void setTracks(final Object track) {
        if(track!=null){
            if(track.getClass() == ArrayList.class){
                ObjectMapper mapper = new ObjectMapper();
                ArrayList<LinkedHashMap> eventsList = (ArrayList<LinkedHashMap>) track;
                int size = eventsList.size();
                tracks = new LFMTrack[size];
                for (int i=0;i<size;i++){
                    tracks[i] = mapper.convertValue(eventsList.get(i),LFMTrack.class);
                }
            }else if(track.getClass() == LinkedHashMap.class){
                ObjectMapper mapper = new ObjectMapper();
                tracks = new LFMTrack[1];
                tracks[0] = mapper.convertValue(track,LFMTrack.class);
            }
        }
    }
    
    @JsonProperty("@attr")
    public LFMAttr attr;
}
