package com.github.supermoonie.cef.handler;

import com.github.supermoonie.cef.handler.request.FileSaveDialogRequest;
import com.github.supermoonie.cef.handler.request.FileSelectRequest;
import com.github.supermoonie.cef.handler.response.FileSelectResponse;
import com.github.supermoonie.cef.util.Jackson;
import org.apache.commons.lang3.StringUtils;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.JFileChooser.*;

/**
 * @author supermoonie
 * @since 2021/3/3
 */
public class FileHandler extends CefMessageRouterHandlerAdapter {

    private static final String FILE_SELECT = "file:select:";
    private static final String FILE_SAVE_DIALOG = "file:save_dialog:";
    private static final String FILE_USER_HOME = "file:user_home";

    private final JFrame owner;

    public FileHandler(JFrame owner) {
        this.owner = owner;
    }

    @Override
    public boolean onQuery(CefBrowser browser,
                           CefFrame frame,
                           long queryId,
                           String request,
                           boolean persistent,
                           CefQueryCallback callback) {
        if (request.equals(FILE_USER_HOME)) {
            String userHome = System.getProperty("user.home");
            callback.success(userHome);
            return true;
        } else if (request.startsWith(FILE_SAVE_DIALOG)) {
            onSaveDialog(request, callback);
            return true;
        } else if (request.startsWith(FILE_SELECT)) {
            onSelect(request, callback);
            return true;
        }
        return false;
    }

    private void onSaveDialog(String request, CefQueryCallback callback) {
        String req = request.replace(FILE_SAVE_DIALOG, "");
        FileSaveDialogRequest fileSaveDialogRequest;
        if (StringUtils.isEmpty(req)) {
            fileSaveDialogRequest = new FileSaveDialogRequest();
        } else {
            fileSaveDialogRequest = Jackson.parse(req, FileSaveDialogRequest.class);
        }
        SwingUtilities.invokeLater(() -> {
            JFileChooser fileChooser;
            if (!StringUtils.isEmpty(fileSaveDialogRequest.getBaseDir()) &&
                    new File(fileSaveDialogRequest.getBaseDir()).exists()) {
                fileChooser = new JFileChooser(fileSaveDialogRequest.getBaseDir());
            } else {
                fileChooser = new JFileChooser();
            }
            if (!StringUtils.isEmpty(fileSaveDialogRequest.getTitle())) {
                fileChooser.setDialogTitle(fileSaveDialogRequest.getTitle());
            }
            fileChooser.setFileSelectionMode(DIRECTORIES_ONLY);
            if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(owner)) {
                File selectedDir = fileChooser.getSelectedFile();
                callback.success(selectedDir.getAbsolutePath());
            }
        });
    }

    private void onSelect(String request, CefQueryCallback callback) {
        String req = request.replace(FILE_SELECT, "");
        if (StringUtils.isEmpty(req)) {
            callback.failure(405, "cmd: " + FILE_SELECT + " args is empty!");
            return;
        }
        final FileSelectRequest fileSelectRequest = Jackson.parse(req, FileSelectRequest.class);
        SwingUtilities.invokeLater(() -> {
            JFileChooser fileChooser;
            if (!StringUtils.isEmpty(fileSelectRequest.getBaseDir()) &&
                    new File(fileSelectRequest.getBaseDir()).exists()) {
                fileChooser = new JFileChooser(fileSelectRequest.getBaseDir());
            } else {
                fileChooser = new JFileChooser();
            }
            if (!StringUtils.isEmpty(fileSelectRequest.getTitle())) {
                fileChooser.setDialogTitle(fileSelectRequest.getTitle());
            }
            fileChooser.setMultiSelectionEnabled(null == fileSelectRequest.getMultiSelectionEnabled() ? false : fileSelectRequest.getMultiSelectionEnabled());
            Action details = fileChooser.getActionMap().get("viewTypeDetails");
            details.actionPerformed(null);
            switch (fileSelectRequest.getSelectType()) {
                case DIRECTORIES_ONLY:
                    fileChooser.setFileSelectionMode(DIRECTORIES_ONLY);
                    break;
                case FILES_AND_DIRECTORIES:
                    fileChooser.setFileSelectionMode(FILES_AND_DIRECTORIES);
                    break;
                default:
                    fileChooser.setFileSelectionMode(FILES_ONLY);
                    break;
            }
            if (null != fileSelectRequest.getExtensionFilter() && fileSelectRequest.getExtensionFilter().size() > 0) {
                fileChooser.setFileFilter(new FileNameExtensionFilter(fileSelectRequest.getDesc(), fileSelectRequest.getExtensionFilter().toArray(new String[]{})));
            }
            if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(owner)) {
                File[] selectedFiles = fileChooser.getSelectedFiles();
                List<FileSelectResponse> responseList = new ArrayList<>(selectedFiles.length);
                for (File file : selectedFiles) {
                    FileSelectResponse res = new FileSelectResponse();
                    res.setPath(file.getAbsolutePath());
                    res.setSize(file.length());
                    res.setModifyDate(file.lastModified());
                    responseList.add(res);
                }
                callback.success(Jackson.toJsonString(responseList));
            } else {
                callback.failure(-1, "No Selected!");
            }
        });
    }
}
