package com.github.supermoonie.cef.ui;

import com.github.supermoonie.cef.dialog.DevToolsDialog;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author supermoonie
 * @since 2021/3/1
 */
public class MenuBar extends JMenuBar {

    private final Frame owner_;
    private final CefBrowser browser_;

    public MenuBar(Frame owner_,
                   CefBrowser browser_) {
        this.owner_ = owner_;
        this.browser_ = browser_;
        JMenu testMenu = new JMenu("Tests");
        final JMenuItem showDevTools = new JMenuItem("Show DevTools");
        showDevTools.addActionListener(e -> {
            DevToolsDialog devToolsDlg =
                    new DevToolsDialog(MenuBar.this.owner_, "DEV Tools", MenuBar.this.browser_);
            devToolsDlg.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentHidden(ComponentEvent e) {
                    showDevTools.setEnabled(true);
                }
            });
            devToolsDlg.setVisible(true);
            showDevTools.setEnabled(false);
        });
        testMenu.add(showDevTools);

        add(testMenu);

    }
}
