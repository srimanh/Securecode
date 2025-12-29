package com.securecode.backend;

public class PolicyChunk {
    private String text;
    private String category;
    private String source;
    private String severity;

    public PolicyChunk() {}

    public PolicyChunk(String text, String category, String source, String severity) {
        this.text = text;
        this.category = category;
        this.source = source;
        this.severity = severity;
    }

    // Getters and setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @Override
    public String toString() {
        return "PolicyChunk{" +
                "text='" + text + '\'' +
                ", category='" + category + '\'' +
                ", source='" + source + '\'' +
                ", severity='" + severity + '\'' +
                '}';
    }
}