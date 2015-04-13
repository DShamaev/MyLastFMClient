package com.hikimori911.mylastfmclient.data.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Дмитрий on 12.04.2015.
 */
public class LFMGetEvent {
    @JsonIgnore
    public LFMEvent[] events;

    @JsonSetter("event")
    private void setEvent(final Object event) {
        if(event!=null){
            if(event.getClass() == ArrayList.class){
                ObjectMapper mapper = new ObjectMapper();
                ArrayList<LinkedHashMap> eventsList = (ArrayList<LinkedHashMap>) event;
                int size = eventsList.size();
                events = new LFMEvent[size];
                for (int i=0;i<size;i++){
                    events[i] = mapper.convertValue(eventsList.get(i),LFMEvent.class);
                }
            }else if(event.getClass() == LinkedHashMap.class){
                ObjectMapper mapper = new ObjectMapper();
                events = new LFMEvent[1];
                events[0] = mapper.convertValue(event,LFMEvent.class);
            }
        }
    }

    @JsonProperty("@attr")
    public LFMAttr attr;
}
