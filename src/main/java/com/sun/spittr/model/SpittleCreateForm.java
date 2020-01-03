package com.sun.spittr.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SpittleCreateForm {

    @NotNull
    @Size(min = 1, max = 100, message = "title length must be in 1 - 100")
    String title;
    @NotNull
    @Size(min = 1, message = "content length must be in 1 - 10000")
    String content;

    public SpittleCreateForm() {
    }

    public SpittleCreateForm(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
