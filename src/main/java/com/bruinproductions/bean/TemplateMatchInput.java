package com.bruinproductions.bean;

import java.util.List;

public class TemplateMatchInput {
    String input;
    List<String> templates;

    public TemplateMatchInput() {}

    public TemplateMatchInput(String input, List<String> templates) {
        this.input = input;
        this.templates = templates;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public List<String> getTemplates() {
        return templates;
    }

    public void setTemplates(List<String> templates) {
        this.templates = templates;
    }
}
