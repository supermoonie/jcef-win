package com.github.supermoonie.cef;

import com.formdev.flatlaf.extras.SVGUtils;
import com.github.supermoonie.cef.cefhandler.ContextMenuHandler;
import com.github.supermoonie.cef.cefhandler.JSDialogHandler;
import com.github.supermoonie.cef.dialog.DownloadDialog;
import com.github.supermoonie.cef.handler.FileHandler;
import com.github.supermoonie.cef.handler.FileServerHandler;
import com.github.supermoonie.cef.handler.SvgConvertHandler;
import com.github.supermoonie.cef.ui.MenuBar;
import org.apache.commons.lang3.SystemUtils;
import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.JCefLoader;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefFocusHandlerAdapter;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.network.CefRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * This is a simple example application using JCEF.
 * It displays a JFrame with a JTextField at its top and a CefBrowser in its
 * center. The JTextField is used to enter and assign an URL to the browser UI.
 * No additional handlers or callbacks are used in this example.
 * <p>
 * The number of used JCEF classes is reduced (nearly) to its minimum and should
 * assist you to get familiar with JCEF.
 * <p>
 * For a more feature complete example have also a look onto the example code
 * within the package "tests.detailed".
 */
public class App extends JFrame {
    private static final long serialVersionUID = -5570653778104813836L;
    private final JTextField address_;
    private final CefApp cefApp_;
    private final CefClient client_;
    private final CefBrowser browser_;
    private final Component browerUI_;
    private boolean browserFocus_ = true;

    /**
     * To display a simple browser window, it suffices completely to create an
     * instance of the class CefBrowser and to assign its UI component to your
     * application (e.g. to your content pane).
     * But to be more verbose, this CTOR keeps an instance of each object on the
     * way to the browser UI.
     */
    private App(String startURL, boolean useOSR, boolean isTransparent) throws IOException {
        setIconImages(SVGUtils.createWindowIconImages("/lighting.svg"));
        // (1) The entry point to JCEF is always the class CefApp. There is only one
        //     instance per application and therefore you have to call the method
        //     "getInstance()" instead of a CTOR.
        //
        //     CefApp is responsible for the global CEF context. It loads all
        //     required native libraries, initializes CEF accordingly, starts a
        //     background task to handle CEF's message loop and takes care of
        //     shutting down CEF after disposing it.
        CefApp.addAppHandler(new CefAppHandlerAdapter(null) {
            @Override
            public void stateHasChanged(org.cef.CefApp.CefAppState state) {
                // Shutdown the app if the native CEF part is terminated
                if (state == CefAppState.TERMINATED) {
                    System.exit(0);
                }
            }
        });
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = useOSR;
        cefApp_ = JCefLoader.installAndLoadCef(settings);

        // (2) JCEF can handle one to many browser instances simultaneous. These
        //     browser instances are logically grouped together by an instance of
        //     the class CefClient. In your application you can create one to many
        //     instances of CefClient with one to many CefBrowser instances per
        //     client. To get an instance of CefClient you have to use the method
        //     "createClient()" of your CefApp instance. Calling an CTOR of
        //     CefClient is not supported.
        //
        //     CefClient is a connector to all possible events which come from the
        //     CefBrowser instances. Those events could be simple things like the
        //     change of the browser title or more complex ones like context menu
        //     events. By assigning handlers to CefClient you can control the
        //     behavior of the browser. See tests.detailed.MainFrame for an example
        //     of how to use these handlers.
        client_ = cefApp_.createClient();

        // (3) One CefBrowser instance is responsible to control what you'll see on
        //     the UI component of the instance. It can be displayed off-screen
        //     rendered or windowed rendered. To get an instance of CefBrowser you
        //     have to call the method "createBrowser()" of your CefClient
        //     instances.
        //
        //     CefBrowser has methods like "goBack()", "goForward()", "loadURL()",
        //     and many more which are used to control the behavior of the displayed
        //     content. The UI is held within a UI-Compontent which can be accessed
        //     by calling the method "getUIComponent()" on the instance of CefBrowser.
        //     The UI component is inherited from a java.awt.Component and therefore
        //     it can be embedded into any AWT UI.
        browser_ = client_.createBrowser(startURL, useOSR, isTransparent);
        browerUI_ = browser_.getUIComponent();
        // (4) For this minimal browser, we need only a text field to enter an URL
        //     we want to navigate to and a CefBrowser window to display the content
        //     of the URL. To respond to the input of the user, we're registering an
        //     anonymous ActionListener. This listener is performed each time the
        //     user presses the "ENTER" key within the address field.
        //     If this happens, the entered value is passed to the CefBrowser
        //     instance to be loaded as URL.
        address_ = new JTextField(startURL, 100);
        address_.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browser_.loadURL(address_.getText());
            }
        });

        // Update the address field when the browser URL changes.
        client_.addDisplayHandler(new CefDisplayHandlerAdapter() {

            @Override
            public void onTitleChange(CefBrowser browser, String title) {
                App.this.setTitle(title);
            }

            @Override
            public void onAddressChange(CefBrowser browser, CefFrame frame, String url) {
                address_.setText(url);
            }
        });
        client_.addJSDialogHandler(new JSDialogHandler());
        client_.addDownloadHandler(new DownloadDialog(this));
        client_.addContextMenuHandler(new ContextMenuHandler(this));
        client_.addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {
                browser.executeJavaScript(
//                        "window.File = function(){}; window.FileList = function(){}; window.FileServer = function(){}; " +
                        "window.saveAs = function(a, b) {var reader = new FileReader();\n" +
                                " reader.readAsDataURL(a); \n" +
                                " reader.onloadend = function() {\n" +
                                "     var base64data = reader.result;\n" +
                                "     window.saveImage({request: base64data, onSuccess: function(res) {alert('success!')}});\n" +
                                "     console.log(base64data);\n" +
                                " };}", null, 0);
                super.onLoadStart(browser, frame, transitionType);
            }
        });

        // Clear focus from the browser when the address field gains focus.
        address_.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!browserFocus_) {
                    return;
                }
                browserFocus_ = false;
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                address_.requestFocus();
            }
        });

        // Clear focus from the address field when the browser gains focus.
        client_.addFocusHandler(new CefFocusHandlerAdapter() {
            @Override
            public void onGotFocus(CefBrowser browser) {
                if (browserFocus_) {
                    return;
                }
                browserFocus_ = true;
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                browser.setFocus(true);
            }

            @Override
            public void onTakeFocus(CefBrowser browser, boolean next) {
                browserFocus_ = false;
            }
        });

        CefMessageRouter fileRouter = CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig("fileQuery", "cancelFilerQuery"));
        fileRouter.addHandler(new FileHandler(this), false);
        CefMessageRouter svgRouter = CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig("svgQuery", "cancelSvgQuery"));
        svgRouter.addHandler(new SvgConvertHandler(), false);
        client_.addMessageRouter(fileRouter);
        client_.addMessageRouter(svgRouter);
        CefMessageRouter fileServerRouter = CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig("saveImage", "cancelSaveImage"));
        fileServerRouter.addHandler(new FileServerHandler(this), false);
        client_.addMessageRouter(fileServerRouter);
        MenuBar menuBar = new MenuBar(this, browser_);
        setJMenuBar(menuBar);

        // (5) All UI components are assigned to the default content pane of this
        //     JFrame and afterwards the frame is made visible to the user.
        getContentPane().add(address_, BorderLayout.NORTH);
        getContentPane().add(browerUI_, BorderLayout.CENTER);
        pack();
        setSize(1200, 800);
        setResizable(true);
        setVisible(true);
        setLocationRelativeTo(null);

        // (6) To take care of shutting down CEF accordingly, it's important to call
        //     the method "dispose()" of the CefApp instance if the Java
        //     application will be closed. Otherwise you'll get asserts from CEF.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                dispose();
            }
        });
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
//        // Perform startup initialization on platforms that require it.
//        if (!CefApp.startup(args)) {
//            System.out.println("Startup initialization failed!");
//            return;
//        }

        if (SystemUtils.IS_OS_MAC_OSX) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        // enable window decorations
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
//        FlatLightLaf.install();
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

//        FlatInspector.install("ctrl shift alt X");
//        FlatUIDefaultsInspector.install("ctrl shift alt Y");

        // The simple example application is created as anonymous class and points
        // to Google as the very first loaded page. Windowed rendering mode is used by
        // default. If you want to test OSR mode set |useOsr| to true and recompile.
        boolean useOsr = false;
//        new App("file://D:\\Projects\\jcef-win\\src\\main\\resources\\Main.html", useOsr, false);
        new App("http://localhost:3050/", useOsr, false);
    }
}