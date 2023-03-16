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
 //import java.lang.ref.SoftReference;
 import java.util.HashMap;
 import java.util.Map;
 import dev.wfj.gwtpdfbox.cos.COSObject;
 import dev.wfj.gwtpdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
 import dev.wfj.gwtpdfbox.pdmodel.font.PDFont;
 import dev.wfj.gwtpdfbox.pdmodel.graphics.PDXObject;
 import dev.wfj.gwtpdfbox.pdmodel.graphics.color.PDColorSpace;
 import dev.wfj.gwtpdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
 import dev.wfj.gwtpdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
 
 /**
  * A resource cached based on SoftReference, retains resources until memory pressure causes them
  * to be garbage collected.
  *
  * @author John Hewson
  */
 public class DefaultResourceCache implements ResourceCache
 {
     private final Map<COSObject, PDFont> fonts =
             new HashMap<>();
     
     private final Map<COSObject, PDColorSpace> colorSpaces =
             new HashMap<>();
 
     private final Map<COSObject, PDXObject> xobjects =
             new HashMap<>();
 
     private final Map<COSObject, PDExtendedGraphicsState> extGStates =
             new HashMap<>();
 
     //private final Map<COSObject, PDShading> shadings =
     //        new HashMap<>();
 
     private final Map<COSObject, PDAbstractPattern> patterns =
             new HashMap<>();
 
     private final Map<COSObject, PDPropertyList> properties =
             new HashMap<>();
 
     @Override
     public PDFont getFont(COSObject indirect) throws IOException
     {
         PDFont font = fonts.get(indirect);
         if (font != null)
         {
             return font;
         }
         return null;
     }
 
     @Override
     public void put(COSObject indirect, PDFont font) throws IOException
     {
         fonts.put(indirect, font);
     }
 
     @Override
     public PDColorSpace getColorSpace(COSObject indirect) throws IOException
     {
         PDColorSpace colorSpace = colorSpaces.get(indirect);
         if (colorSpace != null)
         {
             return colorSpace;
         }
         return null;
     }
 
     @Override
     public void put(COSObject indirect, PDColorSpace colorSpace) throws IOException
     {
         colorSpaces.put(indirect, colorSpace);
     }
 
     @Override
     public PDExtendedGraphicsState getExtGState(COSObject indirect)
     {
         PDExtendedGraphicsState extGState = extGStates.get(indirect);
         if (extGState != null)
         {
             return extGState;
         }
         return null;
     }
 
     @Override
     public void put(COSObject indirect, PDExtendedGraphicsState extGState)
     {
         extGStates.put(indirect, extGState);
     }
 
     /*@Override
     public PDShading getShading(COSObject indirect) throws IOException
     {
         PDShading> shading = shadings.get(indirect);
         if (shading != null)
         {
             return shading;
         }
         return null;
     }*/
 
     /*@Override
     public void put(COSObject indirect, PDShading shading) throws IOException
     {
         shadings.put(indirect, shading));
     }*/
 
     @Override
     public PDAbstractPattern getPattern(COSObject indirect) throws IOException
     {
         PDAbstractPattern pattern = patterns.get(indirect);
         if (pattern != null)
         {
             return pattern;
         }
         return null;
     }
 
     @Override
     public void put(COSObject indirect, PDAbstractPattern pattern) throws IOException
     {
         patterns.put(indirect, pattern);
     }
     
     @Override
     public PDPropertyList getProperties(COSObject indirect)
     {
         PDPropertyList propertyList = properties.get(indirect);
         if (propertyList != null)
         {
             return propertyList;
         }
         return null;
     }
 
     @Override
     public void put(COSObject indirect, PDPropertyList propertyList)
     {
         properties.put(indirect, propertyList);
     }
 
     @Override
     public PDXObject getXObject(COSObject indirect) throws IOException
     {
        PDXObject xobject = xobjects.get(indirect);
         if (xobject != null)
         {
             return xobject;
         }
         return null;
     }
 
     @Override
     public void put(COSObject indirect, PDXObject xobject) throws IOException
     {
         xobjects.put(indirect, xobject);
     }
 }
 