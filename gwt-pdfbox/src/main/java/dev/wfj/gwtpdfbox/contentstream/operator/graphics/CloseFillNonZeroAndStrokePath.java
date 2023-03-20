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

import java.util.List;

import dev.wfj.gwtpdfbox.cos.COSBase;
import dev.wfj.gwtpdfbox.contentstream.PDFGraphicsStreamEngine;
import dev.wfj.gwtpdfbox.contentstream.PDFStreamEngine;
import dev.wfj.gwtpdfbox.contentstream.operator.Operator;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorName;

import java.io.IOException;

/**
 * b Close, fill and stroke the path with non-zero winding rule.
 *
 * @author Ben Litchfield
 */
public final class CloseFillNonZeroAndStrokePath extends GraphicsOperatorProcessor
{
    public CloseFillNonZeroAndStrokePath(PDFGraphicsStreamEngine context)
    {
        super(context);
    }

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        PDFStreamEngine context = getContext();
        context.processOperator(OperatorName.CLOSE_PATH, operands);
        context.processOperator(OperatorName.FILL_NON_ZERO_AND_STROKE, operands);
    }

    @Override
    public String getName()
    {
        return OperatorName.CLOSE_FILL_NON_ZERO_AND_STROKE;
    }
}
