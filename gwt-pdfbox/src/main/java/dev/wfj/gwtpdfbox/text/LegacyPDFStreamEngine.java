/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.wfj.gwtpdfbox.text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

import dev.wfj.gwtpdfbox.GwtFontResources;
import dev.wfj.gwtpdfbox.contentstream.PDFStreamEngine;
import dev.wfj.gwtpdfbox.pdmodel.PDPage;
import dev.wfj.gwtpdfbox.pdmodel.font.encoding.GlyphList;
import dev.wfj.gwtpdfbox.pdmodel.common.PDRectangle;
import dev.wfj.gwtpdfbox.pdmodel.font.PDCIDFont;
import dev.wfj.gwtpdfbox.pdmodel.font.PDCIDFontType2;
import dev.wfj.gwtpdfbox.pdmodel.font.PDFont;
import dev.wfj.gwtpdfbox.pdmodel.font.PDSimpleFont;
import dev.wfj.gwtpdfbox.pdmodel.font.PDTrueTypeFont;
import dev.wfj.gwtpdfbox.pdmodel.font.PDType0Font;
import dev.wfj.gwtpdfbox.pdmodel.font.PDType3Font;
import dev.wfj.gwtpdfbox.pdmodel.graphics.state.PDGraphicsState;
import dev.wfj.gwtpdfbox.util.Matrix;
import dev.wfj.gwtpdfbox.util.Vector;
import elemental2.dom.DomGlobal;
import dev.wfj.gwtpdfbox.contentstream.operator.DrawObject;
import dev.wfj.gwtpdfbox.contentstream.operator.state.Concatenate;
import dev.wfj.gwtpdfbox.contentstream.operator.state.Restore;
import dev.wfj.gwtpdfbox.contentstream.operator.state.Save;
import dev.wfj.gwtpdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import dev.wfj.gwtpdfbox.contentstream.operator.state.SetMatrix;
import dev.wfj.gwtpdfbox.contentstream.operator.text.BeginText;
import dev.wfj.gwtpdfbox.contentstream.operator.text.EndText;
import dev.wfj.gwtpdfbox.contentstream.operator.text.SetFontAndSize;
import dev.wfj.gwtpdfbox.contentstream.operator.text.SetTextHorizontalScaling;
import dev.wfj.gwtpdfbox.contentstream.operator.text.ShowTextAdjusted;
import dev.wfj.gwtpdfbox.contentstream.operator.text.ShowTextLine;
import dev.wfj.gwtpdfbox.contentstream.operator.text.ShowTextLineAndSpace;
import dev.wfj.gwtpdfbox.contentstream.operator.text.MoveText;
import dev.wfj.gwtpdfbox.contentstream.operator.text.MoveTextSetLeading;
import dev.wfj.gwtpdfbox.contentstream.operator.text.NextLine;
import dev.wfj.gwtpdfbox.contentstream.operator.text.SetCharSpacing;
import dev.wfj.gwtpdfbox.contentstream.operator.text.SetTextLeading;
import dev.wfj.gwtpdfbox.contentstream.operator.text.SetTextRenderingMode;
import dev.wfj.gwtpdfbox.contentstream.operator.text.SetTextRise;
import dev.wfj.gwtpdfbox.contentstream.operator.text.SetWordSpacing;
import dev.wfj.gwtpdfbox.contentstream.operator.text.ShowText;
import dev.wfj.gwtpdfbox.cos.COSDictionary;
import dev.wfj.gwtpdfbox.fontbox.ttf.TrueTypeFont;
import dev.wfj.gwtpdfbox.fontbox.util.BoundingBox;
import dev.wfj.gwtpdfbox.pdmodel.font.PDFontDescriptor;

/**
 * LEGACY text calculations which are known to be incorrect but are depended on by PDFTextStripper.
 * 
 * This class exists only so that we don't break the code of users who have their own subclasses of
 * PDFTextStripper. It replaces the mostly empty implementation of showGlyph() in PDFStreamEngine
 * with a heuristic implementation which is backwards compatible.
 *
 * DO NOT USE THIS CODE UNLESS YOU ARE WORKING WITH PDFTextStripper.
 * THIS CODE IS DELIBERATELY INCORRECT, USE PDFStreamEngine INSTEAD.
 */
class LegacyPDFStreamEngine extends PDFStreamEngine
{
    private int pageRotation;
    private PDRectangle pageSize;
    private Matrix translateMatrix;
    private static final GlyphList GLYPHLIST;
    private final Map<COSDictionary, Float> fontHeightMap = new HashMap<>();

    static
    {
        try (InputStream input = new ByteArrayInputStream(GwtFontResources.INSTANCE.additionalGlyphs().getText().getBytes()))
        {
            GLYPHLIST = new GlyphList(GlyphList.getAdobeGlyphList(), GwtFontResources.INSTANCE.additionalGlyphs().getText());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor.
     */
    LegacyPDFStreamEngine()
    {
        addOperator(new BeginText(this));
        addOperator(new Concatenate(this));
        addOperator(new DrawObject(this)); // special text version
        addOperator(new EndText(this));
        addOperator(new SetGraphicsStateParameters(this));
        addOperator(new Save(this));
        addOperator(new Restore(this));
        addOperator(new NextLine(this));
        addOperator(new SetCharSpacing(this));
        addOperator(new MoveText(this));
        addOperator(new MoveTextSetLeading(this));
        addOperator(new SetFontAndSize(this));
        addOperator(new ShowText(this));
        addOperator(new ShowTextAdjusted(this));
        addOperator(new SetTextLeading(this));
        addOperator(new SetMatrix(this));
        addOperator(new SetTextRenderingMode(this));
        addOperator(new SetTextRise(this));
        addOperator(new SetWordSpacing(this));
        addOperator(new SetTextHorizontalScaling(this));
        addOperator(new ShowTextLine(this));
        addOperator(new ShowTextLineAndSpace(this));
    }

    /**
     * This will initialize and process the contents of the stream.
     *
     * @param page the page to process
     * @throws java.io.IOException if there is an error accessing the stream.
     */
    @Override
    public void processPage(PDPage page) throws IOException
    {
        this.pageRotation = page.getRotation();
        this.pageSize = page.getCropBox();
        
        if (Double.compare(pageSize.getLowerLeftX(), 0) == 0 && Double.compare(pageSize.getLowerLeftY(), 0) == 0)
        {
            translateMatrix = null;
        }
        else
        {
            // translation matrix for cropbox
            translateMatrix = Matrix.getTranslateInstance(-pageSize.getLowerLeftX(), -pageSize.getLowerLeftY());
        }            
        super.processPage(page);
    }

    /**
     * Called when a glyph is to be processed. The heuristic calculations here were originally
     * written by Ben Litchfield for PDFStreamEngine.
     */
    @Override
    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, Vector displacement)
            throws IOException
    {
        //
        // legacy calculations which were previously in PDFStreamEngine
        //
        //  DO NOT USE THIS CODE UNLESS YOU ARE WORKING WITH PDFTextStripper.
        //  THIS CODE IS DELIBERATELY INCORRECT
        //

        PDGraphicsState state = getGraphicsState();
        Matrix ctm = state.getCurrentTransformationMatrix();
        float fontSize = state.getTextState().getFontSize();
        float horizontalScaling = state.getTextState().getHorizontalScaling() / 100f;
        Matrix textMatrix = getTextMatrix();

        float displacementX = displacement.getX();
        // the sorting algorithm is based on the width of the character. As the displacement
        // for vertical characters doesn't provide any suitable value for it, we have to 
        // calculate our own
        if (font.isVertical())
        {
            displacementX = font.getWidth(code) / 1000;
            // there may be an additional scaling factor for true type fonts
            TrueTypeFont ttf = null;
            if (font instanceof PDTrueTypeFont)
            {
                 ttf = ((PDTrueTypeFont)font).getTrueTypeFont();
            }
            else if (font instanceof PDType0Font)
            {
                PDCIDFont cidFont = ((PDType0Font)font).getDescendantFont();
                if (cidFont instanceof PDCIDFontType2)
                {
                    ttf = ((PDCIDFontType2)cidFont).getTrueTypeFont();
                }
            }
            if (ttf != null && ttf.getUnitsPerEm() != 1000)
            {
                displacementX *= 1000f / ttf.getUnitsPerEm();
            }
        }

        //
        // legacy calculations which were previously in PDFStreamEngine
        //
        //  DO NOT USE THIS CODE UNLESS YOU ARE WORKING WITH PDFTextStripper.
        //  THIS CODE IS DELIBERATELY INCORRECT
        //
        
        // (modified) combined displacement, this is calculated *without* taking the character
        // spacing and word spacing into account, due to legacy code in TextStripper
        float tx = displacementX * fontSize * horizontalScaling;
        float ty = displacement.getY() * fontSize;

        // (modified) combined displacement matrix
        Matrix td = Matrix.getTranslateInstance(tx, ty);

        // (modified) text rendering matrix
        Matrix nextTextRenderingMatrix = td.multiply(textMatrix).multiply(ctm); // text space -> device space
        float nextX = nextTextRenderingMatrix.getTranslateX();
        float nextY = nextTextRenderingMatrix.getTranslateY();

        // (modified) width and height calculations
        float dxDisplay = nextX - textRenderingMatrix.getTranslateX();
        Float fontHeight = fontHeightMap.get(font.getCOSObject());
        if (fontHeight == null)
        {
            fontHeight = computeFontHeight(font);
            fontHeightMap.put(font.getCOSObject(), fontHeight);
        }
        float dyDisplay = fontHeight * textRenderingMatrix.getScalingFactorY();

        //
        // start of the original method
        //

        // Note on variable names. There are three different units being used in this code.
        // Character sizes are given in glyph units, text locations are initially given in text
        // units, and we want to save the data in display units. The variable names should end with
        // Text or Disp to represent if the values are in text or disp units (no glyph units are
        // saved).

        float glyphSpaceToTextSpaceFactor = 1 / 1000f;
        if (font instanceof PDType3Font)
        {
            glyphSpaceToTextSpaceFactor = font.getFontMatrix().getScaleX();
        }

        float spaceWidthText = 0;
        try
        {
            // to avoid crash as described in PDFBOX-614, see what the space displacement should be
            spaceWidthText = font.getSpaceWidth() * glyphSpaceToTextSpaceFactor;
        }
        catch (Exception exception)
        {
            DomGlobal.console.warn(exception, exception);
        }

        if (Float.compare(spaceWidthText, 0) == 0)
        {
            spaceWidthText = font.getAverageFontWidth() * glyphSpaceToTextSpaceFactor;
            // the average space width appears to be higher than necessary so make it smaller
            spaceWidthText *= .80f;
        }
        if (Float.compare(spaceWidthText, 0) == 0)
        {
            spaceWidthText = 1.0f; // if could not find font, use a generic value
        }

        // the space width has to be transformed into display units
        float spaceWidthDisplay = spaceWidthText * textRenderingMatrix.getScalingFactorX();

        // use our additional glyph list for Unicode mapping
        String unicode = font.toUnicode(code, GLYPHLIST);

        // when there is no Unicode mapping available, Acrobat simply coerces the character code
        // into Unicode, so we do the same. Subclasses of PDFStreamEngine don't necessarily want
        // this, which is why we leave it until this point in PDFTextStreamEngine.
        if (unicode == null)
        {
            if (font instanceof PDSimpleFont)
            {
                char c = (char) code;
                unicode = new String(new char[] { c });
            }
            else
            {
                // Acrobat doesn't seem to coerce composite font's character codes, instead it
                // skips them. See the "allah2.pdf" TestTextStripper file.
                return;
            }
        }

        // adjust for cropbox if needed
        Matrix translatedTextRenderingMatrix;
        if (translateMatrix == null)
        {
            translatedTextRenderingMatrix = textRenderingMatrix;
        }
        else
        {
            translatedTextRenderingMatrix = Matrix.concatenate(translateMatrix, textRenderingMatrix);
            nextX -= pageSize.getLowerLeftX();
            nextY -= pageSize.getLowerLeftY();
        }

        processTextPosition(new TextPosition(pageRotation, pageSize.getWidth(),
                pageSize.getHeight(), translatedTextRenderingMatrix, nextX, nextY,
                Math.abs(dyDisplay), dxDisplay,
                Math.abs(spaceWidthDisplay), unicode, new int[] { code } , font, fontSize,
                (int)(fontSize * textMatrix.getScalingFactorX())));
    }

    /**
     * Compute the font height. Override this if you want to use own calculations.
     * 
     * @param font the font.
     * @return the font height.
     * 
     * @throws IOException if there is an error while getting the font bounding box.
     */
    protected float computeFontHeight(PDFont font) throws IOException
    {
        BoundingBox bbox = font.getBoundingBox();
        if (bbox.getLowerLeftY() < Short.MIN_VALUE)
        {
            // PDFBOX-2158 and PDFBOX-3130
            // files by Salmat eSolutions / ClibPDF Library
            bbox.setLowerLeftY(- (bbox.getLowerLeftY() + 65536));
        }
        // 1/2 the bbox is used as the height todo: why?
        float glyphHeight = bbox.getHeight() / 2;

        // sometimes the bbox has very high values, but CapHeight is OK
        PDFontDescriptor fontDescriptor = font.getFontDescriptor();
        if (fontDescriptor != null)
        {
            float capHeight = fontDescriptor.getCapHeight();
            if (Float.compare(capHeight, 0) != 0 &&
                    (capHeight < glyphHeight || Float.compare(glyphHeight, 0) == 0))
            {
                glyphHeight = capHeight;
            }
            // PDFBOX-3464, PDFBOX-4480, PDFBOX-4553:
            // sometimes even CapHeight has very high value, but Ascent and Descent are ok
            float ascent = fontDescriptor.getAscent();
            float descent = fontDescriptor.getDescent();
            if (capHeight > ascent && ascent > 0 && descent < 0 &&
                    ((ascent - descent) / 2 < glyphHeight || Float.compare(glyphHeight, 0) == 0))
            {
                glyphHeight = (ascent - descent) / 2;
            }
        }

        // transformPoint from glyph space -> text space
        float height;
        if (font instanceof PDType3Font)
        {
            height = font.getFontMatrix().transformPoint(0, glyphHeight).y;
        }
        else
        {
            height = glyphHeight / 1000;
        }

        return height;
    }

    /**
     * A method provided as an event interface to allow a subclass to perform some specific
     * functionality when text needs to be processed.
     *
     * @param text The text to be processed.
     */
    protected void processTextPosition(TextPosition text)
    {
        // subclasses can override to provide specific functionality
    }
}
