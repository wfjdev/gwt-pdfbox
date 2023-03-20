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

import dev.wfj.gwtpdfbox.contentstream.PDFGraphicsStreamEngine;
import dev.wfj.gwtpdfbox.contentstream.operator.MissingOperandException;
import dev.wfj.gwtpdfbox.cos.COSBase;
import dev.wfj.gwtpdfbox.cos.COSName;
import dev.wfj.gwtpdfbox.pdmodel.MissingResourceException;
import dev.wfj.gwtpdfbox.pdmodel.graphics.PDXObject;
import dev.wfj.gwtpdfbox.contentstream.operator.Operator;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorName;

/**
 * Do: Draws an XObject.
 *
 * @author Ben Litchfield
 * @author John Hewson
 */
public final class DrawObject extends GraphicsOperatorProcessor
{
    public DrawObject(PDFGraphicsStreamEngine context)
    {
        super(context);
    }

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        if (operands.isEmpty())
        {
            throw new MissingOperandException(operator, operands);
        }
        COSBase base0 = operands.get(0);
        if (!(base0 instanceof COSName))
        {
            return;
        }
        COSName objectName = (COSName) base0;
        PDFGraphicsStreamEngine context = getGraphicsContext();
        PDXObject xobject = context.getResources().getXObject(objectName);

        if (xobject == null)
        {
            throw new MissingResourceException("Missing XObject: " + objectName.getName());
        }
        /* else if (xobject instanceof PDImageXObject)
        {
            PDImageXObject image = (PDImageXObject)xobject;
            context.drawImage(image);
        }
        else if (xobject instanceof PDFormXObject)
        {
            try
            {
                context.increaseLevel();
                if (context.getLevel() > 50)
                {
                    LOG.error("recursion is too deep, skipping form XObject");
                    return;
                }
                if (xobject instanceof PDTransparencyGroup)
                {
                    context.showTransparencyGroup((PDTransparencyGroup) xobject);
                }
                else
                {
                    context.showForm((PDFormXObject) xobject);
                }
            }
            finally
            {
                context.decreaseLevel();
            }
        } */
    }

    @Override
    public String getName()
    {
        return OperatorName.DRAW_OBJECT;
    }
}
