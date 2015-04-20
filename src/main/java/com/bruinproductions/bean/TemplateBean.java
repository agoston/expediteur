package com.bruinproductions.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

// TODO: add some way to identify the user/session so that we can tie this data to them

@Document(collection = "templates")
public class TemplateBean {
    @Id
    private String id;

    @Field
    private String tag;
    @Field
    private String preTag;
    @Field
    private String postTag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPreTag() {
        return preTag;
    }

    public void setPreTag(String preTag) {
        this.preTag = preTag;
    }

    public String getPostTag() {
        return postTag;
    }

    public void setPostTag(String postTag) {
        this.postTag = postTag;
    }
}
