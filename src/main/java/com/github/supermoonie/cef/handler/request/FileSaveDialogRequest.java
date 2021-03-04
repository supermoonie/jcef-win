package com.github.supermoonie.cef.handler.request;

/**
 * @author supermoonie
 * @date 2021-03-04
 */
public class FileSaveDialogRequest {

    private String baseDir;

    private String title;

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
