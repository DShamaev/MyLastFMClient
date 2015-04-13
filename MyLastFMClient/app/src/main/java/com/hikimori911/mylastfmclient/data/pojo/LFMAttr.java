package com.hikimori911.mylastfmclient.data.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Дмитрий on 12.04.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LFMAttr {
    public String user;
    public long page;
    public long perPage;
    public long total;
}
