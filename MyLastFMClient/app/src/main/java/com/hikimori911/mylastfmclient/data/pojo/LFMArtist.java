package com.hikimori911.mylastfmclient.data.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Дмитрий on 12.04.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LFMArtist {
    @JsonIgnore
    public String[] artists;

    @JsonSetter("artist")
    private void setArtists(final Object artist) {
        if(artist!=null){
            if(artist.getClass() == ArrayList.class){
                ObjectMapper mapper = new ObjectMapper();
                ArrayList<LinkedHashMap> eventsList = (ArrayList<LinkedHashMap>) artist;
                int size = eventsList.size();
                artists = new String[size];
                for (int i=0;i<size;i++){
                    artists[i] = mapper.convertValue(eventsList.get(i),String.class);
                }
            }else if(artist.getClass() == LinkedHashMap.class){
                ObjectMapper mapper = new ObjectMapper();
                artists = new String[1];
                artists[0] = mapper.convertValue(artist,String.class);
            }
        }
    }

    public String headliner;
}
