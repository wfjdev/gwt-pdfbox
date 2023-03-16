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
package dev.wfj.gwtpdfbox.pdmodel.documentinterchange.markedcontent;

import dev.wfj.gwtpdfbox.cos.COSBase;
import dev.wfj.gwtpdfbox.cos.COSDictionary;
import dev.wfj.gwtpdfbox.cos.COSName;
import dev.wfj.gwtpdfbox.pdmodel.common.COSObjectable;
import dev.wfj.gwtpdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import dev.wfj.gwtpdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentMembershipDictionary;

/**
 * A property list is a dictionary containing private information meaningful to the conforming
 * writer creating the marked content.
 */
public class PDPropertyList implements COSObjectable
{
    protected final COSDictionary dict;

    /**
     * Creates a property list from the given dictionary.
     * @param dict COS dictionary
     */
    public static PDPropertyList create(COSDictionary dict)
    {
        COSBase item = dict.getItem(COSName.TYPE);
        if (COSName.OCG.equals(item))
        {
            return new PDOptionalContentGroup(dict);
        }
        else if (COSName.OCMD.equals(item))
        {
            return new PDOptionalContentMembershipDictionary(dict);
        }
        else
        {
            // todo: more types
            return new PDPropertyList(dict);
        }
    }

    /**
     * Constructor for subclasses.
     */
    protected PDPropertyList()
    {
        this.dict = new COSDictionary();
    }

    /**
     * Constructor for subclasses.
     */
    protected PDPropertyList(COSDictionary dict)
    {
        this.dict = dict;
    }

    @Override
    public COSDictionary getCOSObject()
    {
        return dict;
    }
}
