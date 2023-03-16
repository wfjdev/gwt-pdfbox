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
package dev.wfj.gwtpdfbox.pdmodel;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import dev.wfj.gwtpdfbox.cos.COSBase;
import dev.wfj.gwtpdfbox.cos.COSDictionary;
import dev.wfj.gwtpdfbox.cos.COSName;
import dev.wfj.gwtpdfbox.cos.COSObject;
import dev.wfj.gwtpdfbox.cos.COSStream;
import dev.wfj.gwtpdfbox.pdmodel.common.COSObjectable;
import dev.wfj.gwtpdfbox.pdmodel.graphics.PDXObject;

/**
 * A set of resources available at the page/pages/stream level.
 * 
 * @author Ben Litchfield
 * @author John Hewson
 */
public final class PDResources implements COSObjectable
{
    private final COSDictionary resources;
    private final ResourceCache cache;
    
    /**
     * Constructor for embedding.
     */
    public PDResources()
    {
        resources = new COSDictionary();
        cache = null;
    }

    /**
     * Constructor for reading.
     *
     * @param resourceDictionary The cos dictionary for this resource.
     */
    public PDResources(COSDictionary resourceDictionary)
    {
        if (resourceDictionary == null)
        {
            throw new IllegalArgumentException("resourceDictionary is null");
        }
        resources = resourceDictionary;
        cache = null;
    }
    
    /**
     * Constructor for reading.
     *
     * @param resourceDictionary The cos dictionary for this resource.
     * @param resourceCache The document's resource cache, may be null.
     */
    public PDResources(COSDictionary resourceDictionary, ResourceCache resourceCache)
    {
        if (resourceDictionary == null)
        {
            throw new IllegalArgumentException("resourceDictionary is null");
        }
        resources = resourceDictionary;
        cache = resourceCache;
    }

    /**
     * Returns the underlying dictionary.
     */
    @Override
    public COSDictionary getCOSObject()
    {
        return resources;
    }
    
    /**
     * Returns true if the given color space name exists in these resources.
     *
     * @param name Name of the color space resource.
     */
    public boolean hasColorSpace(COSName name)
    {
        return get(COSName.COLORSPACE, name) != null;
    }

    /**
     * Tells whether the XObject resource with the given name is an image.
     *
     * @param name Name of the XObject resource.
     * @return true if it is an image XObject, false if not.
     */
    public boolean isImageXObject(COSName name)
    {
        // get the instance
        COSBase value = get(COSName.XOBJECT, name);
        if (value == null)
        {
            return false;
        }
        else if (value instanceof COSObject)
        {
            value = ((COSObject) value).getObject();
        }
        if (!(value instanceof COSStream))
        {
            return false;
        }
        COSStream stream = (COSStream) value;
        return COSName.IMAGE.equals(stream.getCOSName(COSName.SUBTYPE));
    }

    /**
     * Returns the XObject resource with the given name, or null if none exists.
     * 
     * @param name Name of the XObject resource.
     * @throws IOException if something went wrong.
     */
    public PDXObject getXObject(COSName name) throws IOException
    {
        COSObject indirect = getIndirect(COSName.XOBJECT, name);
        if (cache != null && indirect != null)
        {
            PDXObject cached = cache.getXObject(indirect);
            if (cached != null)
            {
                return cached;
            }
        }

        // get the instance
        PDXObject xobject;
        COSBase value = get(COSName.XOBJECT, name);
        if (value == null)
        {
            xobject = null;
        }
        else if (value instanceof COSObject)
        {
            xobject = PDXObject.createXObject(((COSObject) value).getObject(), this);
        }
        else
        {
            xobject = PDXObject.createXObject(value, this);
        }
        if (cache != null && indirect != null && isAllowedCache(xobject))
        {
            cache.put(indirect, xobject);
        }
        return xobject;
    }

    private boolean isAllowedCache(PDXObject xobject)
    {
        return true;
    }

    /**
     * Returns the resource with the given name and kind as an indirect object, or null.
     */
    private COSObject getIndirect(COSName kind, COSName name)
    {
        COSDictionary dict = resources.getCOSDictionary(kind);
        if (dict == null)
        {
            return null;
        }
        COSBase base = dict.getItem(name);
        if (base instanceof COSObject)
        {
            return (COSObject)base;
        }
        // not an indirect object. Resource may have been added at runtime.
        return null;
    }
    
    /**
     * Returns the resource with the given name and kind, or null.
     */
    private COSBase get(COSName kind, COSName name)
    {
        COSDictionary dict = resources.getCOSDictionary(kind);
        return dict != null ? dict.getDictionaryObject(name) : null;
    }

    /**
     * Returns the names of the color space resources, if any.
     */
    public Iterable<COSName> getColorSpaceNames()
    {
        return getNames(COSName.COLORSPACE);
    }

    /**
     * Returns the names of the XObject resources, if any.
     */
    public Iterable<COSName> getXObjectNames()
    {
        return getNames(COSName.XOBJECT);
    }

    /**
     * Returns the names of the font resources, if any.
     */
    public Iterable<COSName> getFontNames()
    {
        return getNames(COSName.FONT);
    }

    /**
     * Returns the names of the property list resources, if any.
     */
    public Iterable<COSName> getPropertiesNames()
    {
        return getNames(COSName.PROPERTIES);
    }

    /**
     * Returns the names of the shading resources, if any.
     */
    public Iterable<COSName> getShadingNames()
    {
        return getNames(COSName.SHADING);
    }

    /**
     * Returns the names of the pattern resources, if any.
     */
    public Iterable<COSName> getPatternNames()
    {
        return getNames(COSName.PATTERN);
    }

    /**
     * Returns the names of the extended graphics state resources, if any.
     */
    public Iterable<COSName> getExtGStateNames()
    {
        return getNames(COSName.EXT_G_STATE);
    }

    /**
     * Returns the resource names of the given kind.
     */
    private Iterable<COSName> getNames(COSName kind)
    {
        COSDictionary dict = resources.getCOSDictionary(kind);
        return dict != null ? dict.keySet() : Collections.emptySet();
    }

    /**
     * Adds the given XObject to the resources of the current page and returns the name for the
     * new resources. Returns the existing resource name if the given item already exists.
     *
     * @param xobject the XObject to add
     * @param prefix the prefix to be used when creating the resource name
     * @return the name of the resource in the resources dictionary
     */
    public COSName add(PDXObject xobject, String prefix)
    {
        return add(COSName.XOBJECT, prefix, xobject);
    }

    /**
     * Adds the given resource if it does not already exist.
     */
    private COSName add(COSName kind, String prefix, COSObjectable object)
    {
        // return the existing key if the item exists already
        COSDictionary dict = resources.getCOSDictionary(kind);
        if (dict != null && dict.containsValue(object.getCOSObject()))
        {
            return dict.getKeyForValue(object.getCOSObject());
        }

        // PDFBOX-4509: It could exist as an indirect object, happens when a font is taken from the 
        // AcroForm default resources of a loaded PDF.
        if (dict != null && COSName.FONT.equals(kind))
        {
            for (Map.Entry<COSName, COSBase> entry : dict.entrySet())
            {
                if (entry.getValue() instanceof COSObject &&
                    object.getCOSObject() == ((COSObject) entry.getValue()).getObject())
                {
                    return entry.getKey();
                }
            }
        }

        // add the item with a new key
        COSName name = createKey(kind, prefix);
        put(kind, name, object);
        return name;
    }

    /**
     * Returns a unique key for a new resource.
     */
    private COSName createKey(COSName kind, String prefix)
    {
        COSDictionary dict = resources.getCOSDictionary(kind);
        if (dict == null)
        {
            return COSName.getPDFName(prefix + 1);
        }

        // find a unique key
        String key;
        int n = dict.keySet().size();
        do
        {
            ++n;
            key = prefix + n;
        }
        while (dict.containsKey(key));
        return COSName.getPDFName(key);
    }

    /**
     * Sets the value of a given named resource.
     */
    private void put(COSName kind, COSName name, COSObjectable object)
    {
        COSDictionary dict = resources.getCOSDictionary(kind);
        if (dict == null)
        {
            dict = new COSDictionary();
            resources.setItem(kind, dict);
        }
        dict.setItem(name, object);
    }

    /**
     * Sets the XObject resource with the given name.
     *
     * @param name the name of the resource
     * @param xobject the XObject to be added
     */
    public void put(COSName name, PDXObject xobject)
    {
        put(COSName.XOBJECT, name, xobject);
    }

    /**
     * Returns the resource cache associated with the Resources, or null if there is none.
     */
    public ResourceCache getResourceCache()
    {
        return cache;
    }
}
