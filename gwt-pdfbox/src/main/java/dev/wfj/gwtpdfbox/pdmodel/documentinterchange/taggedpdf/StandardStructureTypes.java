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
package dev.wfj.gwtpdfbox.pdmodel.documentinterchange.taggedpdf;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import elemental2.dom.DomGlobal;

/**
 * The standard structure types.
 * 
 * @author Johannes Koch
 */
public class StandardStructureTypes {

    // Grouping Elements
    /**
     * Document
     */
    public static final String DOCUMENT = "Document";

    /**
     * Part
     */
    public static final String PART = "Part";

    /**
     * Art
     */
    public static final String ART = "Art";

    /**
     * Sect
     */
    public static final String SECT = "Sect";

    /**
     * Div
     */
    public static final String DIV = "Div";

    /**
     * BlockQuote
     */
    public static final String BLOCK_QUOTE = "BlockQuote";

    /**
     * Caption
     */
    public static final String CAPTION = "Caption";

    /**
     * TOC
     */
    public static final String TOC = "TOC";

    /**
     * TOCI
     */
    public static final String TOCI = "TOCI";

    /**
     * Index
     */
    public static final String INDEX = "Index";

    /**
     * NonStruct
     */
    public static final String NON_STRUCT = "NonStruct";

    /**
     * Private
     */
    public static final String PRIVATE = "Private";

    // Block-Level Structure Elements
    /**
     * P
     */
    public static final String P = "P";

    /**
     * H
     */
    public static final String H = "H";

    /**
     * H1
     */
    public static final String H1 = "H1";

    /**
     * H2
     */
    public static final String H2 = "H2";

    /**
     * H3
     */
    public static final String H3 = "H3";

    /**
     * H4
     */
    public static final String H4 = "H4";

    /**
     * H5
     */
    public static final String H5 = "H5";

    /**
     * H6
     */
    public static final String H6 = "H6";

    /**
     * L
     */
    public static final String L = "L";

    /**
     * LI
     */
    public static final String LI = "LI";

    /**
     * Lbl
     */
    public static final String LBL = "Lbl";

    /**
     * LBody
     */
    public static final String L_BODY = "LBody";

    /**
     * Table
     */
    public static final String TABLE = "Table";

    /**
     * TR
     */
    public static final String TR = "TR";

    /**
     * TH
     */
    public static final String TH = "TH";

    /**
     * TD
     */
    public static final String TD = "TD";

    /**
     * THead
     */
    public static final String T_HEAD = "THead";

    /**
     * TBody
     */
    public static final String T_BODY = "TBody";

    /**
     * TFoot
     */
    public static final String T_FOOT = "TFoot";

    // Inline-Level Structure Elements
    /**
     * Span
     */
    public static final String SPAN = "Span";

    /**
     * Quote
     */
    public static final String QUOTE = "Quote";

    /**
     * Note
     */
    public static final String NOTE = "Note";

    /**
     * Reference
     */
    public static final String REFERENCE = "Reference";

    /**
     * BibEntry
     */
    public static final String BIB_ENTRY = "BibEntry";

    /**
     * Code
     */
    public static final String CODE = "Code";

    /**
     * Link
     */
    public static final String LINK = "Link";

    /**
     * Annot
     */
    public static final String ANNOT = "Annot";

    /**
     * Ruby
     */
    public static final String RUBY = "Ruby";

    /**
     * RB
     */
    public static final String RB = "RB";

    /**
     * RT
     */
    public static final String RT = "RT";

    /**
     * RP
     */
    public static final String RP = "RP";

    /**
     * Warichu
     */
    public static final String WARICHU = "Warichu";

    /**
     * WT
     */
    public static final String WT = "WT";

    /**
     * WP
     */
    public static final String WP = "WP";

    // Illustration Elements
    /**
     * Figure
     */
    public static final String Figure = "Figure";

    /**
     * Formula
     */
    public static final String FORMULA = "Formula";

    /**
     * Form
     */
    public static final String FORM = "Form";

    /**
     * All standard structure types.
     */
    public static final List<String> types = new ArrayList<>();

    static {
        types.add(DOCUMENT);
        types.add(PART);

        types.add(ART);

        types.add(SECT);

        types.add(DIV);
        types.add(BLOCK_QUOTE);
        types.add(CAPTION);
        types.add(TOC);
        types.add(TOCI);
        types.add(INDEX);
        types.add(NON_STRUCT);
        types.add(PRIVATE);
        types.add(P);
        types.add(H);
        types.add(H1);
        types.add(H2);
        types.add(H3);
        types.add(H4);
        types.add(H5);
        types.add(H6);
        types.add(L);
        types.add(LI);
        types.add(LBL);
        types.add(L_BODY);
        types.add(TABLE);
        types.add(TR);
        types.add(TH);
        types.add(TD);
        types.add(T_HEAD);
        types.add(T_BODY);
        types.add(T_FOOT);
        types.add(SPAN);
        types.add(QUOTE);
        types.add(NOTE);
        types.add(REFERENCE);
        types.add(BIB_ENTRY);
        types.add(CODE);
        types.add(LINK);
        types.add(ANNOT);
        types.add(RUBY);
        types.add(RB);
        types.add(RT);
        types.add(RP);
        types.add(WARICHU);
        types.add(WT);
        types.add(WP);
        types.add(Figure);
        types.add(FORMULA);
        types.add(FORM);

        /*
         * Field[] fields = StandardStructureTypes.class.getFields();
         * for (Field field : fields)
         * {
         * if (Modifier.isFinal(field.getModifiers()))
         * {
         * try
         * {
         * types.add(field.get(null).toString());
         * }
         * catch (IllegalArgumentException | IllegalAccessException e)
         * {
         * DomGlobal.console.error(e,e);
         * }
         * }
         * }
         */
        Collections.sort(types);
    }

    private StandardStructureTypes() {
    }
}
