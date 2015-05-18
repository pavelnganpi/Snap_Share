package com.paveynganpi.snapshare.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by paveynganpi on 5/17/15.
 */
public class TwitterUserpojo {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonProperty("name")
    public String name;

    @JsonProperty("profile_image_url")
    public String profileImageUrl;

    @JsonProperty("default_profile_image")
    public Boolean defaulProfileImage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Boolean getDefaulProfileImage() {
        return defaulProfileImage;
    }

    public void setDefaulProfileImage(Boolean defaulProfileImage) {
        this.defaulProfileImage = defaulProfileImage;
    }
}
