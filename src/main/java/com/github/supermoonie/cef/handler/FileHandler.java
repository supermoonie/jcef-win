package com.github.supermoonie.cef.handler;

import com.github.supermoonie.cef.request.FileSelectRequest;
import com.github.supermoonie.cef.util.Jackson;
import org.apache.commons.lang3.StringUtils;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * @author supermoonie
 * @since 2021/3/3
 */
public class FileHandler extends CefMessageRouterHandlerAdapter {

    private static final String FILE_SELECT = "file:select:";
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
        }
        if (request.startsWith(FILE_SELECT)) {
            String req = request.replace(FILE_SELECT, "");
            if (StringUtils.isEmpty(req)) {
                callback.failure(405, "cmd: " + FILE_SELECT + " args is empty!");
                return true;
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
                Action details = fileChooser.getActionMap().get("viewTypeDetails");
                details.actionPerformed(null);
                fileChooser.setFileSelectionMode(fileSelectRequest.getSelectType());
                if (null != fileSelectRequest.getExtensionFilter() && fileSelectRequest.getExtensionFilter().size() > 0) {
                    fileChooser.setFileFilter(new FileNameExtensionFilter(fileSelectRequest.getDesc(), fileSelectRequest.getExtensionFilter().toArray(new String[]{})));
                }
                if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(owner)) {
                    File selectedFile = fileChooser.getSelectedFile();
                    callback.success(selectedFile.getAbsolutePath());
                } else {
                    callback.failure(-1, "No Selected!");
                }
            });
            return true;
        }
        return false;
    }
}
