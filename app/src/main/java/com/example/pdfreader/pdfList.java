package com.example.pdfreader;

import java.io.File;

class pdfList {
    private String displayName;
    private String filePath;
    private File file;

    pdfList(String displayName, String filePath, File file) {
        this.displayName = displayName;
        this.filePath = filePath;
        this.file = file;
    }

    public String getDisplayName(){
        return displayName;
    }

    public String getFilePath(){
        return filePath;
    }

    public File getFile() {
        return file;
    }
};


