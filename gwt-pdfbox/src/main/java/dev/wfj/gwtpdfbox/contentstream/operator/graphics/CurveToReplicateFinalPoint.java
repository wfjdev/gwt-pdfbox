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
import dev.wfj.gwtpdfbox.contentstream.operator.Operator;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorName;

/**
 * y Append curved segment to path with final point replicated.
 *
 * @author Ben Litchfield
 */
public final class CurveToReplicateFinalPoint extends GraphicsOperatorProcessor
{
    public CurveToReplicateFinalPoint(PDFGraphicsStreamEngine context)
    {
        super(context);
    }

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        if (operands.size() < 4)
        {
            throw new MissingOperandException(operator, operands);
        }
        if (!checkArrayTypesClass(operands, COSNumber.class))
        {
            return;
        }
        COSNumber x1 = (COSNumber)operands.get(0);
        COSNumber y1 = (COSNumber)operands.get(1);
        COSNumber x3 = (COSNumber)operands.get(2);
        COSNumber y3 = (COSNumber)operands.get(3);

        PDFGraphicsStreamEngine context = getGraphicsContext();
        Point2D.Float point1 = context.transformedPoint(x1.floatValue(), y1.floatValue());
        Point2D.Float point3 = context.transformedPoint(x3.floatValue(), y3.floatValue());

        context.curveTo(point1.x, point1.y,
                        point3.x, point3.y,
                        point3.x, point3.y);
    }

    @Override
    public String getName()
    {
        return OperatorName.CURVE_TO_REPLICATE_FINAL_POINT;
    }
}
