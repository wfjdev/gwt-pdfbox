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

import java.io.IOException;
import java.util.List;

import dev.wfj.gwtpdfbox.contentstream.PDFStreamEngine;
import dev.wfj.gwtpdfbox.contentstream.operator.MissingOperandException;
import dev.wfj.gwtpdfbox.cos.COSBase;
import dev.wfj.gwtpdfbox.cos.COSNumber;
import dev.wfj.gwtpdfbox.contentstream.operator.Operator;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorName;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorProcessor;

/**
 * Tc: Set character spacing.
 *
 * @author Laurent Huault
 */
public class SetCharSpacing extends OperatorProcessor
{
    public SetCharSpacing(PDFStreamEngine context)
    {
        super(context);
    }

    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        if (arguments.isEmpty())
        {
            throw new MissingOperandException(operator, arguments);
        }

        // there are some documents which are incorrectly structured, and have
        // a wrong number of arguments to this, so we will assume the last argument
        // in the list
        Object charSpacing = arguments.get(arguments.size()-1);
        if (charSpacing instanceof COSNumber)
        {
            COSNumber characterSpacing = (COSNumber)charSpacing;
            getContext().getGraphicsState().getTextState()
                    .setCharacterSpacing(characterSpacing.floatValue());
        }
    }

    @Override
    public String getName()
    {
        return OperatorName.SET_CHAR_SPACING;
    }
}
