package ru.muiv.notes.model;

public class Note {
    private String id;
    private String title;
    private String content;
    private String groupName;
    private String tags;
    private String author;
    private String lastEditor;
    private String imagePath;
    private long updatedAt;

    public Note(
            String id,
            String title,
            String content,
            String groupName,
            String tags,
            String author,
            String lastEditor,
            String imagePath,
            long updatedAt
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.groupName = groupName;
        this.tags = tags;
        this.author = author;
        this.lastEditor = lastEditor;
        this.imagePath = imagePath;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getTags() {
        return tags;
    }

    public String getAuthor() {
        return author;
    }

    public String getLastEditor() {
        return lastEditor;
    }

    public String getImagePath() {
        return imagePath;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }
}