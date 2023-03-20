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

import dev.wfj.gwtpdfbox.contentstream.PDFGraphicsStreamEngine;
import dev.wfj.gwtpdfbox.contentstream.operator.OperatorName;

/**
 * F Fill path using non zero winding rule. Included only for compatibility with Acrobat.
 *
 * @author John Hewson
 */
public final class LegacyFillNonZeroRule extends FillNonZeroRule
{
    public LegacyFillNonZeroRule(PDFGraphicsStreamEngine context)
    {
        super(context);
    }

    @Override
    public String getName()
    {
        return OperatorName.LEGACY_FILL_NON_ZERO;
    }
}
