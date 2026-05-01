package kpi.zakrevskyi.neurolib.domain;

import lombok.Getter;

@Getter
public enum FileType {
    AVATAR("avatars"),
    BOOK_COVER("book-covers"),
    BOOK_PDF("book-pdfs");

    private final String folderName;

    FileType(String folderName) {
        this.folderName = folderName;
    }
}