package com.github.supermoonie.cef.request;

import java.util.List;

/**
 * @author supermoonie
 * @date 2021-03-03
 */
public class FileSelectRequest {

    /**
     * 0: files only
     * 1: directories only
     * 2: files and directories
     */
    private Integer selectType;

    private String baseDir;

    private String desc;

    private List<String> extensionFilter;

    public Integer getSelectType() {
        return selectType;
    }

    public void setSelectType(Integer selectType) {
        this.selectType = selectType;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getExtensionFilter() {
        return extensionFilter;
    }

    public void setExtensionFilter(List<String> extensionFilter) {
        this.extensionFilter = extensionFilter;
    }
}
