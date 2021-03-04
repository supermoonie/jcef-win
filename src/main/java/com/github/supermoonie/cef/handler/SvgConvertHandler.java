package com.github.supermoonie.cef.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.supermoonie.cef.handler.request.SvgToPngRequest;
import com.github.supermoonie.cef.util.Jackson;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author supermoonie
 * @since 2021/3/4
 */
public class SvgConvertHandler extends CefMessageRouterHandlerAdapter {

    private static final String SVG_CONVERT_PNG = "convert:svg_to_png:";

    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        if (request.startsWith(SVG_CONVERT_PNG)) {
            try {
                onSvgToPng(request, callback);
                return true;
            } catch (IOException | TranscoderException e) {
                callback.failure(404, SVG_CONVERT_PNG + " error: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    private void onSvgToPng(String request, CefQueryCallback callback) throws IOException, TranscoderException {
        String req = request.replace(SVG_CONVERT_PNG, "");
        SvgToPngRequest svgToPngRequest = Jackson.parse(req, new TypeReference<SvgToPngRequest>() {
        });
        for (String path : svgToPngRequest.getFiles()) {
            File file = new File(path);
            System.out.println(file.toURI().toString());
            TranscoderInput inputSvgImage = new TranscoderInput(file.toURI().toString());
            OutputStream out = new FileOutputStream(svgToPngRequest.getBaseDir() + "/" + file.getName().replace(".svg", ".png"));
            TranscoderOutput outputPngImage = new TranscoderOutput(out);
            PNGTranscoder myConverter = new PNGTranscoder();
            myConverter.transcode(inputSvgImage, outputPngImage);
            out.flush();
            out.close();
        }
        callback.success("success");
    }
}
