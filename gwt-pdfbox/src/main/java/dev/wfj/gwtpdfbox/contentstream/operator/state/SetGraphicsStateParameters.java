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

import java.io.IOException;
import java.util.List;

import dev.wfj.gwtpdfbox.cos.COSBase;
import dev.wfj.gwtpdfbox.cos.COSName;
import dev.wfj.gwtpdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import elemental2.dom.DomGlobal;
import dev.wfj.gwtpdfbox.contentstream.operator.Operator;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorName;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorProcessor;
import dev.wfj.gwtpdfbox.contentstream.PDFStreamEngine;
import dev.wfj.gwtpdfbox.contentstream.operator.MissingOperandException;

/**
 * gs: Set parameters from graphics state parameter dictionary.
 *
 * @author Ben Litchfield
 */
public class SetGraphicsStateParameters extends OperatorProcessor
{
    public SetGraphicsStateParameters(PDFStreamEngine context)
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
        COSBase base0 = arguments.get(0);
        if (!(base0 instanceof COSName))
        {
            return;
        }
        
        // set parameters from graphics state parameter dictionary
        COSName graphicsName = (COSName) base0;
        PDFStreamEngine context = getContext();
        PDExtendedGraphicsState gs = context.getResources().getExtGState(graphicsName);
        if (gs == null)
        {
            DomGlobal.console.error("name for 'gs' operator not found in resources: /" + graphicsName.getName());
            return;
        }
        gs.copyIntoGraphicsState( context.getGraphicsState() );
    }

    @Override
    public String getName()
    {
        return OperatorName.SET_GRAPHICS_STATE_PARAMS;
    }
}
