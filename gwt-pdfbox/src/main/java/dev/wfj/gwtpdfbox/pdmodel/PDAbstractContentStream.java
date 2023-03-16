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
package dev.wfj.gwtpdfbox.pdmodel;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorName;
import dev.wfj.gwtpdfbox.cos.COSName;
import dev.wfj.gwtpdfbox.pdmodel.graphics.state.RenderingMode;
import dev.wfj.gwtpdfbox.util.NumberFormatUtil;
import elemental2.dom.DomGlobal;

/**
 * Provides the ability to write to a content stream.
 *
 * @author Ben Litchfield
 */
abstract class PDAbstractContentStream implements Closeable
{
    
    protected final PDDocument document; // may be null

    protected final OutputStream outputStream;
    protected final PDResources resources;

    protected boolean inTextMode = false;

    // number format
    private final byte[] formatBuffer = new byte[32];

    /**
     * Create a new appearance stream.
     *
     * @param document may be null
     * @param outputStream The appearances output stream to write to.
     * @param resources The resources to use
     */
    PDAbstractContentStream(PDDocument document, OutputStream outputStream, PDResources resources)
    {
        this.document = document;
        this.outputStream = outputStream;
        this.resources = resources;
    }

    /**
     * Begin some text operations.
     *
     * @throws IOException If there is an error writing to the stream or if you attempt to
     *         nest beginText calls.
     * @throws IllegalStateException If the method was not allowed to be called at this time.
     */
    public void beginText() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: Nested beginText() calls are not allowed.");
        }
        writeOperator(OperatorName.BEGIN_TEXT);
        inTextMode = true;
    }

    /**
     * End some text operations.
     *
     * @throws IOException If there is an error writing to the stream or if you attempt to
     *         nest endText calls.
     * @throws IllegalStateException If the method was not allowed to be called at this time.
     */
    public void endText() throws IOException
    {
        if (!inTextMode)
        {
            throw new IllegalStateException("Error: You must call beginText() before calling endText.");
        }
        writeOperator(OperatorName.END_TEXT);
        inTextMode = false;
    }
    

    /**
     * Shows the given text at the location specified by the current text matrix with the given
     * interspersed positioning. This allows the user to efficiently position each glyph or sequence
     * of glyphs.
     *
     * @param textWithPositioningArray An array consisting of String and Float types. Each String is
     * output to the page using the current text matrix. Using the default coordinate system, each
     * interspersed number adjusts the current text matrix by translating to the left or down for
     * horizontal and vertical text respectively. The number is expressed in thousands of a text
     * space unit, and may be negative.
     *
     * @throws IOException if an io exception occurs.
     */
    public void showTextWithPositioning(Object[] textWithPositioningArray) throws IOException
    {
        write("[");
        for (Object obj : textWithPositioningArray)
        {
            if (obj instanceof String)
            {
                showTextInternal((String) obj);
            }
            else if (obj instanceof Float)
            {
                writeOperand((Float) obj);
            }
            else
            {
                throw new IllegalArgumentException("Argument must consist of array of Float and String types");
            }
        }
        write("] ");
        writeOperator(OperatorName.SHOW_TEXT_ADJUSTED);
    }

    /**
     * Shows the given text at the location specified by the current text matrix.
     *
     * @param text The Unicode text to show.
     * @throws IOException If an io exception occurs.
     * @throws IllegalArgumentException if a character isn't supported by the current font
     */
    public void showText(String text) throws IOException
    {
        showTextInternal(text);
        write(" ");
        writeOperator(OperatorName.SHOW_TEXT);
    }

    /**
     * Outputs a string using the correct encoding and subsetting as required.
     *
     * @param text The Unicode text to show.
     * 
     * @throws IOException If an io exception occurs.
     */
    protected void showTextInternal(String text) throws IOException
    {
        if (!inTextMode)
        {
            throw new IllegalStateException("Must call beginText() before showText()");
        }

    }

    /**
     * Sets the text leading.
     *
     * @param leading The leading in unscaled text units.
     * @throws IOException If there is an error writing to the stream.
     */
    public void setLeading(float leading) throws IOException
    {
        writeOperand(leading);
        writeOperator(OperatorName.SET_TEXT_LEADING);
    }

    /**
     * Move to the start of the next line of text. Requires the leading (see {@link #setLeading})
     * to have been set.
     *
     * @throws IOException If there is an error writing to the stream.
     */
    public void newLine() throws IOException
    {
        if (!inTextMode)
        {
            throw new IllegalStateException("Must call beginText() before newLine()");
        }
        writeOperator(OperatorName.NEXT_LINE);
    }

    /**
     * The Td operator.
     * Move to the start of the next line, offset from the start of the current line by (tx, ty).
     *
     * @param tx The x translation.
     * @param ty The y translation.
     * @throws IOException If there is an error writing to the stream.
     * @throws IllegalStateException If the method was not allowed to be called at this time.
     */
    public void newLineAtOffset(float tx, float ty) throws IOException
    {
        if (!inTextMode)
        {
            throw new IllegalStateException("Error: must call beginText() before newLineAtOffset()");
        }
        writeOperand(tx);
        writeOperand(ty);
        writeOperator(OperatorName.MOVE_TEXT);
    }


    /**
     * q operator. Saves the current graphics state.
     * @throws IOException If an error occurs while writing to the stream.
     */
    public void saveGraphicsState() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: Saving the graphics state is not allowed within text objects.");
        }

        writeOperator(OperatorName.SAVE);
    }

    /**
     * Q operator. Restores the current graphics state.
     * @throws IOException If an error occurs while writing to the stream.
     */
    public void restoreGraphicsState() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: Restoring the graphics state is not allowed within text objects.");
        }
        writeOperator(OperatorName.RESTORE);
    }


    /**
     * Set the stroking color in the DeviceRGB color space. Range is 0..1.
     *
     * @param r The red value
     * @param g The green value.
     * @param b The blue value.
     * @throws IOException If an IO error occurs while writing to the stream.
     * @throws IllegalArgumentException If the parameters are invalid.
     */
    public void setStrokingColor(float r, float g, float b) throws IOException
    {
        if (isOutsideOneInterval(r) || isOutsideOneInterval(g) || isOutsideOneInterval(b))
        {
            throw new IllegalArgumentException("Parameters must be within 0..1");
        }
        writeOperand(r);
        writeOperand(g);
        writeOperand(b);
        writeOperator(OperatorName.STROKING_COLOR_RGB);
    }

    /**
     * Set the stroking color in the DeviceCMYK color space. Range is 0..1
     *
     * @param c The cyan value.
     * @param m The magenta value.
     * @param y The yellow value.
     * @param k The black value.
     * @throws IOException If an IO error occurs while writing to the stream.
     * @throws IllegalArgumentException If the parameters are invalid.
     */
    public void setStrokingColor(float c, float m, float y, float k) throws IOException
    {
        if (isOutsideOneInterval(c) || isOutsideOneInterval(m) || isOutsideOneInterval(y) || isOutsideOneInterval(k))
        {
            throw new IllegalArgumentException("Parameters must be within 0..1");
        }
        writeOperand(c);
        writeOperand(m);
        writeOperand(y);
        writeOperand(k);
        writeOperator(OperatorName.STROKING_COLOR_CMYK);
    }

    /**
     * Set the stroking color in the DeviceGray color space. Range is 0..1.
     *
     * @param g The gray value.
     * @throws IOException If an IO error occurs while writing to the stream.
     * @throws IllegalArgumentException If the parameter is invalid.
     */
    public void setStrokingColor(float g) throws IOException
    {
        if (isOutsideOneInterval(g))
        {
            throw new IllegalArgumentException("Parameter must be within 0..1, but is " + g);
        }
        writeOperand(g);
        writeOperator(OperatorName.STROKING_COLOR_GRAY);
    }

    /**
     * Set the non-stroking color in the DeviceRGB color space. Range is 0..1.
     *
     * @param r The red value.
     * @param g The green value.
     * @param b The blue value.
     * @throws IOException If an IO error occurs while writing to the stream.
     * @throws IllegalArgumentException If the parameters are invalid.
     */
    public void setNonStrokingColor(float r, float g, float b) throws IOException
    {
        if (isOutsideOneInterval(r) || isOutsideOneInterval(g) || isOutsideOneInterval(b))
        {
            throw new IllegalArgumentException("Parameters must be within 0..1");
        }
        writeOperand(r);
        writeOperand(g);
        writeOperand(b);
        writeOperator(OperatorName.NON_STROKING_RGB);
    }

    /**
     * Set the non-stroking color in the DeviceCMYK color space. Range is 0..1.
     *
     * @param c The cyan value.
     * @param m The magenta value.
     * @param y The yellow value.
     * @param k The black value.
     * @throws IOException If an IO error occurs while writing to the stream.
     */
    public void setNonStrokingColor(float c, float m, float y, float k) throws IOException
    {
        if (isOutsideOneInterval(c) || isOutsideOneInterval(m) || isOutsideOneInterval(y) || isOutsideOneInterval(k))
        {
            throw new IllegalArgumentException("Parameters must be within 0..1");
        }
        writeOperand(c);
        writeOperand(m);
        writeOperand(y);
        writeOperand(k);
        writeOperator(OperatorName.NON_STROKING_CMYK);
    }

    /**
     * Set the non-stroking color in the DeviceGray color space. Range is 0..1.
     *
     * @param g The gray value.
     * @throws IOException If an IO error occurs while writing to the stream.
     * @throws IllegalArgumentException If the parameter is invalid.
     */
    public void setNonStrokingColor(float g) throws IOException
    {
        if (isOutsideOneInterval(g))
        {
            throw new IllegalArgumentException("Parameter must be within 0..1, but is " + g);
        }
        writeOperand(g);
        writeOperator(OperatorName.NON_STROKING_GRAY);
    }

    /**
     * Add a rectangle to the current path.
     *
     * @param x The lower left x coordinate.
     * @param y The lower left y coordinate.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @throws IOException If the content stream could not be written.
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void addRect(float x, float y, float width, float height) throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: addRect is not allowed within a text block.");
        }
        writeOperand(x);
        writeOperand(y);
        writeOperand(width);
        writeOperand(height);
        writeOperator(OperatorName.APPEND_RECT);
    }

    /**
     * Append a cubic Bézier curve to the current path. The curve extends from the current point to
     * the point (x3, y3), using (x1, y1) and (x2, y2) as the Bézier control points.
     *
     * @param x1 x coordinate of the point 1
     * @param y1 y coordinate of the point 1
     * @param x2 x coordinate of the point 2
     * @param y2 y coordinate of the point 2
     * @param x3 x coordinate of the point 3
     * @param y3 y coordinate of the point 3
     * @throws IOException If the content stream could not be written.
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: curveTo is not allowed within a text block.");
        }
        writeOperand(x1);
        writeOperand(y1);
        writeOperand(x2);
        writeOperand(y2);
        writeOperand(x3);
        writeOperand(y3);
        writeOperator(OperatorName.CURVE_TO);
    }

    /**
     * Append a cubic Bézier curve to the current path. The curve extends from the current point to
     * the point (x3, y3), using the current point and (x2, y2) as the Bézier control points.
     *
     * @param x2 x coordinate of the point 2
     * @param y2 y coordinate of the point 2
     * @param x3 x coordinate of the point 3
     * @param y3 y coordinate of the point 3
     * @throws IllegalStateException If the method was called within a text block.
     * @throws IOException If the content stream could not be written.
     */
    public void curveTo2(float x2, float y2, float x3, float y3) throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: curveTo2 is not allowed within a text block.");
        }
        writeOperand(x2);
        writeOperand(y2);
        writeOperand(x3);
        writeOperand(y3);
        writeOperator(OperatorName.CURVE_TO_REPLICATE_INITIAL_POINT);
    }

    /**
     * Append a cubic Bézier curve to the current path. The curve extends from the current point to
     * the point (x3, y3), using (x1, y1) and (x3, y3) as the Bézier control points.
     *
     * @param x1 x coordinate of the point 1
     * @param y1 y coordinate of the point 1
     * @param x3 x coordinate of the point 3
     * @param y3 y coordinate of the point 3
     * @throws IOException If the content stream could not be written.
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void curveTo1(float x1, float y1, float x3, float y3) throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: curveTo1 is not allowed within a text block.");
        }
        writeOperand(x1);
        writeOperand(y1);
        writeOperand(x3);
        writeOperand(y3);
        writeOperator(OperatorName.CURVE_TO_REPLICATE_FINAL_POINT);
    }

    /**
     * Move the current position to the given coordinates.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @throws IOException If the content stream could not be written.
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void moveTo(float x, float y) throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: moveTo is not allowed within a text block.");
        }
        writeOperand(x);
        writeOperand(y);
        writeOperator(OperatorName.MOVE_TO);
    }

    /**
     * Draw a line from the current position to the given coordinates.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @throws IOException If the content stream could not be written.
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void lineTo(float x, float y) throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: lineTo is not allowed within a text block.");
        }
        writeOperand(x);
        writeOperand(y);
        writeOperator(OperatorName.LINE_TO);
    }

    /**
     * Stroke the path.
     * 
     * @throws IOException If the content stream could not be written
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void stroke() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: stroke is not allowed within a text block.");
        }
        writeOperator(OperatorName.STROKE_PATH);
    }

    /**
     * Close and stroke the path.
     * 
     * @throws IOException If the content stream could not be written
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void closeAndStroke() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: closeAndStroke is not allowed within a text block.");
        }
        writeOperator(OperatorName.CLOSE_AND_STROKE);
    }

    /**
     * Fills the path using the nonzero winding number rule.
     *
     * @throws IOException If the content stream could not be written
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void fill() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: fill is not allowed within a text block.");
        }
        writeOperator(OperatorName.FILL_NON_ZERO);
    }

    /**
     * Fills the path using the even-odd winding rule.
     *
     * @throws IOException If the content stream could not be written
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void fillEvenOdd() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: fillEvenOdd is not allowed within a text block.");
        }
        writeOperator(OperatorName.FILL_EVEN_ODD);
    }

    /**
     * Fill and then stroke the path, using the nonzero winding number rule to determine the region
     * to fill. This shall produce the same result as constructing two identical path objects,
     * painting the first with {@link #fill() } and the second with {@link #stroke() }.
     *
     * @throws IOException If the content stream could not be written
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void fillAndStroke() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: fillAndStroke is not allowed within a text block.");
        }
        writeOperator(OperatorName.FILL_NON_ZERO_AND_STROKE);
    }

    /**
     * Fill and then stroke the path, using the even-odd rule to determine the region to
     * fill. This shall produce the same result as constructing two identical path objects, painting
     * the first with {@link #fillEvenOdd() } and the second with {@link #stroke() }.
     *
     * @throws IOException If the content stream could not be written
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void fillAndStrokeEvenOdd() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: fillAndStrokeEvenOdd is not allowed within a text block.");
        }
        writeOperator(OperatorName.FILL_EVEN_ODD_AND_STROKE);
    }

    /**
     * Close, fill, and then stroke the path, using the nonzero winding number rule to determine the
     * region to fill. This shall have the same effect as the sequence {@link #closePath() }
     * and then {@link #fillAndStroke() }.
     *
     * @throws IOException If the content stream could not be written
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void closeAndFillAndStroke() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: closeAndFillAndStroke is not allowed within a text block.");
        }
        writeOperator(OperatorName.CLOSE_FILL_NON_ZERO_AND_STROKE);
    }

    /**
     * Close, fill, and then stroke the path, using the even-odd rule to determine the region to
     * fill. This shall have the same effect as the sequence {@link #closePath() }
     * and then {@link #fillAndStrokeEvenOdd() }.
     *
     * @throws IOException If the content stream could not be written
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void closeAndFillAndStrokeEvenOdd() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: closeAndFillAndStrokeEvenOdd is not allowed within a text block.");
        }
        writeOperator(OperatorName.CLOSE_FILL_EVEN_ODD_AND_STROKE);
    }

    /**
     * Closes the current subpath.
     *
     * @throws IOException If the content stream could not be written
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void closePath() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: closePath is not allowed within a text block.");
        }
        writeOperator(OperatorName.CLOSE_PATH);
    }

    /**
     * Intersects the current clipping path with the current path, using the nonzero rule.
     *
     * @throws IOException If the content stream could not be written
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void clip() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: clip is not allowed within a text block.");
        }
        writeOperator(OperatorName.CLIP_NON_ZERO);
        
        // end path without filling or stroking
        writeOperator(OperatorName.ENDPATH);
    }

    /**
     * Intersects the current clipping path with the current path, using the even-odd rule.
     *
     * @throws IOException If the content stream could not be written
     * @throws IllegalStateException If the method was called within a text block.
     */
    public void clipEvenOdd() throws IOException
    {
        if (inTextMode)
        {
            throw new IllegalStateException("Error: clipEvenOdd is not allowed within a text block.");
        }
        writeOperator(OperatorName.CLIP_EVEN_ODD);
        
        // end path without filling or stroking
        writeOperator(OperatorName.ENDPATH);
    }

    /**
     * Set line width to the given value.
     *
     * @param lineWidth The width which is used for drawing.
     * @throws IOException If the content stream could not be written
     */
    public void setLineWidth(float lineWidth) throws IOException
    {
        writeOperand(lineWidth);
        writeOperator(OperatorName.SET_LINE_WIDTH);
    }

    /**
     * Set the line join style.
     *
     * @param lineJoinStyle 0 for miter join, 1 for round join, and 2 for bevel join.
     * @throws IOException If the content stream could not be written.
     * @throws IllegalArgumentException If the parameter is not a valid line join style.
     */
    public void setLineJoinStyle(int lineJoinStyle) throws IOException
    {
        if (lineJoinStyle >= 0 && lineJoinStyle <= 2)
        {
            writeOperand(lineJoinStyle);
            writeOperator(OperatorName.SET_LINE_JOINSTYLE);
        }
        else
        {
            throw new IllegalArgumentException("Error: unknown value for line join style");
        }
    }

    /**
     * Set the line cap style.
     *
     * @param lineCapStyle 0 for butt cap, 1 for round cap, and 2 for projecting square cap.
     * @throws IOException If the content stream could not be written.
     * @throws IllegalArgumentException If the parameter is not a valid line cap style.
     */
    public void setLineCapStyle(int lineCapStyle) throws IOException
    {
        if (lineCapStyle >= 0 && lineCapStyle <= 2)
        {
            writeOperand(lineCapStyle);
            writeOperator(OperatorName.SET_LINE_CAPSTYLE);
        }
        else
        {
            throw new IllegalArgumentException("Error: unknown value for line cap style");
        }
    }

    /**
     * Set the line dash pattern.
     *
     * @param pattern The pattern array
     * @param phase The phase of the pattern
     * @throws IOException If the content stream could not be written.
     */
    public void setLineDashPattern(float[] pattern, float phase) throws IOException
    {
        write("[");
        for (float value : pattern)
        {
            writeOperand(value);
        }
        write("] ");
        writeOperand(phase);
        writeOperator(OperatorName.SET_LINE_DASHPATTERN);
    }

    /**
     * Set the miter limit.
     *
     * @param miterLimit the new miter limit.
     * @throws IOException If the content stream could not be written.
     * @throws IllegalArgumentException If the parameter is \u2264 0.
     */
    public void setMiterLimit(float miterLimit) throws IOException
    {
        if (miterLimit <= 0.0)
        {
            throw new IllegalArgumentException("A miter limit <= 0 is invalid and will not render in Acrobat Reader");
        }
        writeOperand(miterLimit);
        writeOperator(OperatorName.SET_LINE_MITERLIMIT);
    }

    /**
     * Begin a marked content sequence.
     *
     * @param tag the tag
     * @throws IOException If the content stream could not be written
     */
    public void beginMarkedContent(COSName tag) throws IOException
    {
        writeOperand(tag);
        writeOperator(OperatorName.BEGIN_MARKED_CONTENT);
    }

    /**
     * End a marked content sequence.
     *
     * @throws IOException If the content stream could not be written
     */
    public void endMarkedContent() throws IOException
    {
        writeOperator(OperatorName.END_MARKED_CONTENT);
    }

    /**
     * Write a comment line.
     *
     * @param comment
     * @throws IOException If the content stream could not be written.
     * @throws IllegalArgumentException If the comment contains a newline. This is not allowed,
     * because the next line could be ordinary PDF content.
     */
    public void addComment(String comment) throws IOException
    {
        if (comment.indexOf('\n') >= 0 || comment.indexOf('\r') >= 0)
        {
            throw new IllegalArgumentException("comment should not include a newline");
        }
        outputStream.write('%');
        outputStream.write(comment.getBytes());
        outputStream.write('\n');
    }

    /**
     * Writes a real number to the content stream.
     * @param real
     * @throws java.io.IOException
     * @throws IllegalArgumentException if the parameter is not a finite number
     */
    protected void writeOperand(float real) throws IOException
    {
        if (!Float.isFinite(real))
        {
            throw new IllegalArgumentException(real + " is not a finite number");
        }
        int byteCount = NumberFormatUtil.formatFloatFast(real, 5, formatBuffer);

        if (byteCount == -1)
        {
            //Fast formatting failed
            write(Float.toString(real));
        }
        else
        {
            outputStream.write(formatBuffer, 0, byteCount);
        }
        outputStream.write(' ');
    }

    /**
     * Writes an integer number to the content stream.
     * @param integer
     * @throws java.io.IOException
     */
    protected void writeOperand(int integer) throws IOException
    {
        write(Integer.toString(integer));
        outputStream.write(' ');
    }

    /**
     * Writes a COSName to the content stream.
     * @param name
     * @throws java.io.IOException
     */
    protected void writeOperand(COSName name) throws IOException
    {
        name.writePDF(outputStream);
        outputStream.write(' ');
    }

    /**
     * Writes a string to the content stream as ASCII.
     * @param text
     * @throws java.io.IOException
     */
    protected void writeOperator(String text) throws IOException
    {
        outputStream.write(text.getBytes());
        outputStream.write('\n');
    }

    /**
     * Writes a string to the content stream as ASCII.
     * @param text
     * @throws java.io.IOException
     */
    protected void write(String text) throws IOException
    {
        outputStream.write(text.getBytes());
    }

    /**
     * Writes a byte[] to the content stream.
     * @param data
     * @throws java.io.IOException
     */
    protected void write(byte[] data) throws IOException
    {
        outputStream.write(data);
    }
    
    /**
     * Writes a newline to the content stream as ASCII.
     * @throws java.io.IOException
     */
    protected void writeLine() throws IOException
    {
        outputStream.write('\n');
    }

    /**
     * Writes binary data to the content stream.
     * @param data
     * @throws java.io.IOException
     */
    protected void writeBytes(byte[] data) throws IOException
    {
        outputStream.write(data);
    }

    /**
     * Close the content stream.  This must be called when you are done with this object.
     *
     * @throws IOException If the underlying stream has a problem being written to.
     */
    @Override
    public void close() throws IOException
    {
        if (inTextMode)
        {
            DomGlobal.console.warn("You did not call endText(), some viewers won't display your text");
        }
        outputStream.close();
    }

    protected boolean isOutside255Interval(int val)
    {
        return val < 0 || val > 255;
    }

    private boolean isOutsideOneInterval(double val)
    {
        return val < 0 || val > 1;
    }

    /**
     * Set the character spacing. The value shall be added to the horizontal or vertical component
     * of the glyph's displacement, depending on the writing mode.
     *
     * @param spacing character spacing
     * @throws IOException If the content stream could not be written.
     */
    public void setCharacterSpacing(float spacing) throws IOException
    {
        writeOperand(spacing);
        writeOperator(OperatorName.SET_CHAR_SPACING);
    }

    /**
     * Set the word spacing. The value shall be added to the horizontal or vertical component of the
     * ASCII SPACE character, depending on the writing mode.
     * <p>
     * This will have an effect only with Type1 and TrueType fonts, not with Type0 fonts. The PDF
     * specification tells why: "Word spacing shall be applied to every occurrence of the
     * single-byte character code 32 in a string when using a simple font or a composite font that
     * defines code 32 as a single-byte code. It shall not apply to occurrences of the byte value 32
     * in multiple-byte codes."
     *
     * @param spacing word spacing
     * @throws IOException If the content stream could not be written.
     */
    public void setWordSpacing(float spacing) throws IOException
    {
        writeOperand(spacing);
        writeOperator(OperatorName.SET_WORD_SPACING);
    }

    /**
     * Set the horizontal scaling to scale / 100.
     *
     * @param scale number specifying the percentage of the normal width. Default value: 100 (normal
     * width).
     * @throws IOException If the content stream could not be written.
     */
    public void setHorizontalScaling(float scale) throws IOException
    {
        writeOperand(scale);
        writeOperator(OperatorName.SET_TEXT_HORIZONTAL_SCALING);
    }

    /**
     * Set the text rendering mode. This determines whether showing text shall cause glyph outlines
     * to be stroked, filled, used as a clipping boundary, or some combination of the three.
     *
     * @param rm The text rendering mode.
     * @throws IOException If the content stream could not be written.
     */
    public void setRenderingMode(RenderingMode rm) throws IOException
    {
        writeOperand(rm.intValue());
        writeOperator(OperatorName.SET_TEXT_RENDERINGMODE);
    }

    /**
     * Set the text rise value, i.e. move the baseline up or down. This is useful for drawing
     * superscripts or subscripts.
     *
     * @param rise Specifies the distance, in unscaled text space units, to move the baseline up or
     * down from its default location. 0 restores the default location.
     * @throws IOException
     */
    public void setTextRise(float rise) throws IOException
    {
        writeOperand(rise);
        writeOperator(OperatorName.SET_TEXT_RISE);
    }

}
