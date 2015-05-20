package com.paveynganpi.snapshare.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by paveynganpi on 5/17/15.
 */
public class TwitterFolloweePojo {

    @JsonProperty("ids")
    public List<String> followeeIds;

    public List<String> getFolloweeIds() {
        return followeeIds;
    }

    public void setFolloweeIds(List<String> followeeIds) {
        this.followeeIds = followeeIds;
    }
}
