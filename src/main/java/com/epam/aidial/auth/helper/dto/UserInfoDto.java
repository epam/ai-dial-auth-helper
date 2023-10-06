package com.epam.aidial.auth.helper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInfoDto {
    private String name;

    private String email;

    private String picture;

    @JsonProperty("job_title")
    private String jobTitle;

    private String sub;

    public String getName() {
        return name;
    }

    public UserInfoDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserInfoDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPicture() {
        return picture;
    }

    public UserInfoDto setPicture(String picture) {
        this.picture = picture;
        return this;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public UserInfoDto setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        return this;
    }

    public String getSub() {
        return sub;
    }

    public UserInfoDto setSub(String sub) {
        this.sub = sub;
        return this;
    }
}
