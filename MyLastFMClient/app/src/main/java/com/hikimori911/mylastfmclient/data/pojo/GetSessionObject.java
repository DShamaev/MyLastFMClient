package com.hikimori911.mylastfmclient.data.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hikimori911 on 30.03.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetSessionObject extends ResponseBase{
    public LFMSession session;
}
