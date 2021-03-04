package com.github.supermoonie.cef.handler.request;

import java.util.List;

/**
 * @author supermoonie
 * @date 2021-03-04
 */
public class SvgToPngRequest {

    private String baseDir;

    private List<String> files;

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
