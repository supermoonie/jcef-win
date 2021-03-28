package com.github.supermoonie.cef.handler;

import org.apache.commons.io.FileUtils;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static javax.swing.JFileChooser.DIRECTORIES_ONLY;

/**
 * @author supermoonie
 * @date 2021-03-28
 */
public class FileServerHandler extends CefMessageRouterHandlerAdapter {

    private final JFrame owner;

    public FileServerHandler(JFrame owner) {
        this.owner = owner;
    }

    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("...");
            byte[] image = Base64.getDecoder().decode(request.replace("data:image/png;base64,", ""));
            JFileChooser fileChooser = new JFileChooser();
            if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(owner)) {
                File selectFile = fileChooser.getSelectedFile();
                try {
                    FileUtils.writeByteArrayToFile(selectFile, image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callback.success(selectFile.getAbsolutePath());
            }
        });

        return true;
    }
}
