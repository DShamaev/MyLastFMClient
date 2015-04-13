package com.hikimori911.mylastfmclient.data.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hikimori911 on 13.04.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetUserInfoObject extends ResponseBase {
    public LFMUser user;
}
