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
package dev.wfj.gwtpdfbox.contentstream.operator.text;

import java.util.List;

import dev.wfj.gwtpdfbox.contentstream.PDFStreamEngine;
import dev.wfj.gwtpdfbox.contentstream.operator.MissingOperandException;
import dev.wfj.gwtpdfbox.contentstream.operator.Operator;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorName;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorProcessor;
import dev.wfj.gwtpdfbox.cos.COSBase;
import dev.wfj.gwtpdfbox.cos.COSNumber;
import dev.wfj.gwtpdfbox.util.Matrix;
import elemental2.dom.DomGlobal;

/**
 * Td: Move text position.
 *
 * @author Laurent Huault
 */
public class MoveText extends OperatorProcessor
{
    public MoveText(PDFStreamEngine context)
    {
        super(context);
    }

    @Override
    public void process(Operator operator, List<COSBase> arguments) throws MissingOperandException
    {
        if (arguments.size() < 2)
        {
            throw new MissingOperandException(operator, arguments);
        }
        PDFStreamEngine context = getContext();
        Matrix textLineMatrix = context.getTextLineMatrix();
        if (textLineMatrix == null)
        {
            DomGlobal.console.warn("TextLineMatrix is null, " + getName() + " operator will be ignored");
            return;
        }        
        
        COSBase base0 = arguments.get(0);
        COSBase base1 = arguments.get(1);
        if (!(base0 instanceof COSNumber))
        {
            return;
        }
        if (!(base1 instanceof COSNumber))
        {
            return;
        }
        COSNumber x = (COSNumber) base0;
        COSNumber y = (COSNumber) base1;

        Matrix matrix = new Matrix(1, 0, 0, 1, x.floatValue(), y.floatValue());
        textLineMatrix.concatenate(matrix);
        context.setTextMatrix(textLineMatrix.clone());
    }

    @Override
    public String getName()
    {
        return OperatorName.MOVE_TEXT;
    }
}
