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

import dev.wfj.gwtpdfbox.cos.COSBase;
import dev.wfj.gwtpdfbox.util.Matrix;
import dev.wfj.gwtpdfbox.contentstream.PDFStreamEngine;
import dev.wfj.gwtpdfbox.contentstream.operator.Operator;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorName;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorProcessor;

/**
 * BT: Begin text.
 *
 * @author Ben Litchfield
 * @author Laurent Huault
 */
public class BeginText extends OperatorProcessor
{
    public BeginText(PDFStreamEngine context)
    {
        super(context);
    }

    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        PDFStreamEngine context = getContext();
        context.setTextMatrix( new Matrix());
        context.setTextLineMatrix( new Matrix() );
        context.beginText();
    }

    @Override
    public String getName()
    {
        return OperatorName.BEGIN_TEXT;
    }
}
