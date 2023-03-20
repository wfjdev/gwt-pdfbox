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
package dev.wfj.gwtpdfbox.contentstream.operator.graphics;

import java.io.IOException;
import java.util.List;
import java.awt.geom.Point2D;

import dev.wfj.gwtpdfbox.contentstream.PDFGraphicsStreamEngine;
import dev.wfj.gwtpdfbox.contentstream.operator.MissingOperandException;
import dev.wfj.gwtpdfbox.cos.COSBase;
import dev.wfj.gwtpdfbox.cos.COSNumber;
import elemental2.dom.DomGlobal;
import dev.wfj.gwtpdfbox.contentstream.operator.Operator;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorName;

/**
 * l Append straight line segment to path.
 *
 * @author Ben Litchfield
 */
public class LineTo extends GraphicsOperatorProcessor
{
    public LineTo(PDFGraphicsStreamEngine context)
    {
        super(context);
    }

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        if (operands.size() < 2)
        {
            throw new MissingOperandException(operator, operands);
        }
        COSBase base0 = operands.get(0);
        if (!(base0 instanceof COSNumber))
        {
            return;
        }
        COSBase base1 = operands.get(1);
        if (!(base1 instanceof COSNumber))
        {
            return;
        }
        // append straight line segment from the current point to the point
        COSNumber x = (COSNumber) base0;
        COSNumber y = (COSNumber) base1;

        PDFGraphicsStreamEngine context = getGraphicsContext();
        Point2D.Float pos = context.transformedPoint(x.floatValue(), y.floatValue());

        if (context.getCurrentPoint() == null)
        {
            DomGlobal.console.warn("LineTo (" + pos.x + "," + pos.y + ") without initial MoveTo");
            context.moveTo(pos.x, pos.y);
        }
        else
        {
            context.lineTo(pos.x, pos.y);
        }
    }

    @Override
    public String getName()
    {
        return OperatorName.LINE_TO;
    }
}
