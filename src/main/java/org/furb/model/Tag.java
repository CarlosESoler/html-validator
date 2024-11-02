package org.furb.model;

public class Tag implements Comparable<Tag> {
    private int frequency;
    private String content;
    private String contentCleared;

    public Tag(String content) {
        this.content = content;
    }

    public boolean isValidTag() {
        return content.startsWith("<") && content.endsWith(">") && !content.startsWith("< ");
    }

    public void tagClear(){
        this.contentCleared = this.content.replaceAll("<(\\w+)[^>]*>", "<$1>");
    }

    public boolean isFinal() {
        return content.startsWith("</");
    }

    public String getContent() {
        return content;
    }

    public String getContentCleared() { return contentCleared; }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isSingleton() {
        for (SingletonTags tag : SingletonTags.values()) {
            if (tag.getContent().equalsIgnoreCase(this.contentCleared)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Tag o) {
        return this.content.compareTo(o.content);
    }

    @Override
    public boolean equals(Object obj) {
        return this.contentCleared.equals(((Tag) obj).contentCleared);
    }

    public enum SingletonTags {
        META("<meta>"),
        BASE("<base>"),
        BR("<br>"),
        COL("<col>"),
        COMMAND("<command>"),
        EMBED("<embed>"),
        HR("<hr>"),
        IMG("<img>"),
        INPUT("<input>"),
        LINK("<link>"),
        PARAM("<param>"),
        SOURCE("<source>"),
        DOCTYPE("<!DOCTYPE>");

        private final String content;

        SingletonTags(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

}
