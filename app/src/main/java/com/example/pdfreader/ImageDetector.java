package com.example.pdfreader;

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

public class ImageDetector implements RenderListener {
    public void beginTextBlock() { }
    public void endTextBlock() { }
    public void renderText(TextRenderInfo renderInfo) { }

    public void renderImage(ImageRenderInfo renderInfo) {
        imageFound = true;
    }

    boolean imageFound = false;
}

