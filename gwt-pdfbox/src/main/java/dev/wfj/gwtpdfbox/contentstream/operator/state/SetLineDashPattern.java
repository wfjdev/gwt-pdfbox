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
package dev.wfj.gwtpdfbox.contentstream.operator.state;

import java.util.List;

import dev.wfj.gwtpdfbox.contentstream.PDFStreamEngine;
import dev.wfj.gwtpdfbox.contentstream.operator.MissingOperandException;
import dev.wfj.gwtpdfbox.contentstream.operator.Operator;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorName;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorProcessor;
import dev.wfj.gwtpdfbox.cos.COSArray;
import dev.wfj.gwtpdfbox.cos.COSBase;
import dev.wfj.gwtpdfbox.cos.COSNumber;
import elemental2.dom.DomGlobal;

/**
 * d: Set the line dash pattern.
 *
 * @author Ben Litchfield
 */
public class SetLineDashPattern extends OperatorProcessor
{
    public SetLineDashPattern(PDFStreamEngine context)
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
        COSBase base0 = arguments.get(0);
        if (!(base0 instanceof COSArray))
        {
            return;
        }
        COSBase base1 = arguments.get(1);
        if (!(base1 instanceof COSNumber))
        {
            return;
        }
        COSArray dashArray = (COSArray) base0;
        int dashPhase = ((COSNumber) base1).intValue();
        
        for (COSBase base : dashArray)
        {
            if (base instanceof COSNumber)
            {
                COSNumber num = (COSNumber) base;
                if (Float.compare(num.floatValue(), 0) != 0)
                {
                    break;
                }
            }
            else
            {
                DomGlobal.console.warn("dash array has non number element " + base + ", ignored");
                dashArray = new COSArray();
                break;
            }
        }
        //getContext().setLineDashPattern(dashArray, dashPhase);
    }

    @Override
    public String getName()
    {
        return OperatorName.SET_LINE_DASHPATTERN;
    }
}
