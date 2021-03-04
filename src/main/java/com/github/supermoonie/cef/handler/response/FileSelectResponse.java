package com.github.supermoonie.cef.handler.response;

/**
 * @author supermoonie
 * @since 2021/3/4
 */
public class FileSelectResponse {

    private String path;

    private Long size;

    private Long modifyDate;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Long modifyDate) {
        this.modifyDate = modifyDate;
    }
}
