package com.example.books;

public class BookDetail {
    private String title;
    private String author;
    private String previewLink;
    private String image;

    public BookDetail(String title, String author, String previewLink,String image) {
        this.title = title;
        this.author = author;
        this.previewLink = previewLink;
        this.image=image;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public String getImage() {
        return image;
    }
}
