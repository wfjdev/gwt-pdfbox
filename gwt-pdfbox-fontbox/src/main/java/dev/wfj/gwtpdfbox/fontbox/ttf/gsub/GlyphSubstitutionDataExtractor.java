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

package dev.wfj.gwtpdfbox.fontbox.ttf.gsub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.wfj.gwtpdfbox.fontbox.ttf.model.GsubData;
import dev.wfj.gwtpdfbox.fontbox.ttf.model.Language;
import dev.wfj.gwtpdfbox.fontbox.ttf.model.MapBackedGsubData;
import dev.wfj.gwtpdfbox.fontbox.ttf.table.common.CoverageTable;
import dev.wfj.gwtpdfbox.fontbox.ttf.table.common.FeatureListTable;
import dev.wfj.gwtpdfbox.fontbox.ttf.table.common.FeatureRecord;
import dev.wfj.gwtpdfbox.fontbox.ttf.table.common.LangSysTable;
import dev.wfj.gwtpdfbox.fontbox.ttf.table.common.LookupListTable;
import dev.wfj.gwtpdfbox.fontbox.ttf.table.common.LookupSubTable;
import dev.wfj.gwtpdfbox.fontbox.ttf.table.common.LookupTable;
import dev.wfj.gwtpdfbox.fontbox.ttf.table.common.ScriptTable;
import dev.wfj.gwtpdfbox.fontbox.ttf.table.gsub.LigatureSetTable;
import dev.wfj.gwtpdfbox.fontbox.ttf.table.gsub.LigatureTable;
import dev.wfj.gwtpdfbox.fontbox.ttf.table.gsub.LookupTypeLigatureSubstitutionSubstFormat1;
import dev.wfj.gwtpdfbox.fontbox.ttf.table.gsub.LookupTypeSingleSubstFormat1;
import dev.wfj.gwtpdfbox.fontbox.ttf.table.gsub.LookupTypeSingleSubstFormat2;
import elemental2.dom.DomGlobal;

/**
 * This class has utility methods to extract meaningful data from the highly obfuscated GSUB Tables. This data is then
 * used to determine which combination of Glyphs or words have to be replaced.
 * 
 * @author Palash Ray
 * 
 */
public class GlyphSubstitutionDataExtractor
{

    
    public GsubData getGsubData(Map<String, ScriptTable> scriptList,
            FeatureListTable featureListTable, LookupListTable lookupListTable)
    {

        ScriptTableDetails scriptTableDetails = getSupportedLanguage(scriptList);

        if (scriptTableDetails == null)
        {
            return GsubData.NO_DATA_FOUND;
        }

        ScriptTable scriptTable = scriptTableDetails.getScriptTable();

        Map<String, Map<List<Integer>, Integer>> gsubData = new LinkedHashMap<>();
        // the starting point is really the scriptTags
        if (scriptTable.getDefaultLangSysTable() != null)
        {
            populateGsubData(gsubData, scriptTable.getDefaultLangSysTable(), featureListTable,
                    lookupListTable);
        }
        for (LangSysTable langSysTable : scriptTable.getLangSysTables().values())
        {
            populateGsubData(gsubData, langSysTable, featureListTable, lookupListTable);
        }

        return new MapBackedGsubData(scriptTableDetails.getLanguage(),
                scriptTableDetails.getFeatureName(), gsubData);
    }

    private ScriptTableDetails getSupportedLanguage(Map<String, ScriptTable> scriptList)
    {
        for (Language lang : Language.values())
        {
            for (String scriptName : lang.getScriptNames())
            {
                ScriptTable value = scriptList.get(scriptName);
                if (value != null)
                {
                    return new ScriptTableDetails(lang, scriptName, value);
                }
            }
        }
        return null;
    }

    private void populateGsubData(Map<String, Map<List<Integer>, Integer>> gsubData,
            LangSysTable langSysTable, FeatureListTable featureListTable,
            LookupListTable lookupListTable)
    {
        FeatureRecord[] featureRecords = featureListTable.getFeatureRecords();
        for (int featureIndex : langSysTable.getFeatureIndices())
        {
            if (featureIndex < featureRecords.length)
            {
                populateGsubData(gsubData, featureRecords[featureIndex], lookupListTable);
            }
        }
    }

    private void populateGsubData(Map<String, Map<List<Integer>, Integer>> gsubData,
            FeatureRecord featureRecord, LookupListTable lookupListTable)
    {
        LookupTable[] lookups = lookupListTable.getLookups();
        Map<List<Integer>, Integer> glyphSubstitutionMap = new LinkedHashMap<>();
        for (int lookupIndex : featureRecord.getFeatureTable().getLookupListIndices())
        {
            if (lookupIndex < lookups.length)
            {
                extractData(glyphSubstitutionMap, lookups[lookupIndex]);
            }
        }

        DomGlobal.console.debug("*********** extracting GSUB data for the feature: "
                + featureRecord.getFeatureTag() + ", glyphSubstitutionMap: "
                + glyphSubstitutionMap);

        gsubData.put(featureRecord.getFeatureTag(),
                Collections.unmodifiableMap(glyphSubstitutionMap));
    }

    private void extractData(Map<List<Integer>, Integer> glyphSubstitutionMap,
            LookupTable lookupTable)
    {

        for (LookupSubTable lookupSubTable : lookupTable.getSubTables())
        {
            if (lookupSubTable instanceof LookupTypeLigatureSubstitutionSubstFormat1)
            {
                extractDataFromLigatureSubstitutionSubstFormat1Table(glyphSubstitutionMap,
                        (LookupTypeLigatureSubstitutionSubstFormat1) lookupSubTable);
            }
            else if (lookupSubTable instanceof LookupTypeSingleSubstFormat1)
            {
                extractDataFromSingleSubstTableFormat1Table(glyphSubstitutionMap,
                        (LookupTypeSingleSubstFormat1) lookupSubTable);
            }
            else if (lookupSubTable instanceof LookupTypeSingleSubstFormat2)
            {
                extractDataFromSingleSubstTableFormat2Table(glyphSubstitutionMap,
                        (LookupTypeSingleSubstFormat2) lookupSubTable);
            }
            else
            {
                // usually null, due to being skipped in GlyphSubstitutionTable.readLookupTable()
                DomGlobal.console.debug("The type " + lookupSubTable + " is not yet supported, will be ignored");
            }
        }

    }

    private void extractDataFromSingleSubstTableFormat1Table(
            Map<List<Integer>, Integer> glyphSubstitutionMap,
            LookupTypeSingleSubstFormat1 singleSubstTableFormat1)
    {
        CoverageTable coverageTable = singleSubstTableFormat1.getCoverageTable();
        for (int i = 0; i < coverageTable.getSize(); i++)
        {
            int coverageGlyphId = coverageTable.getGlyphId(i);
            int substituteGlyphId = coverageGlyphId + singleSubstTableFormat1.getDeltaGlyphID();
            putNewSubstitutionEntry(glyphSubstitutionMap, substituteGlyphId,
                    Collections.singletonList(coverageGlyphId));
        }
    }

    private void extractDataFromSingleSubstTableFormat2Table(
            Map<List<Integer>, Integer> glyphSubstitutionMap,
            LookupTypeSingleSubstFormat2 singleSubstTableFormat2)
    {

        CoverageTable coverageTable = singleSubstTableFormat2.getCoverageTable();

        if (coverageTable.getSize() != singleSubstTableFormat2.getSubstituteGlyphIDs().length)
        {
            throw new IllegalArgumentException(
                    "The no. coverage table entries should be the same as the size of the substituteGlyphIDs");
        }

        for (int i = 0; i < coverageTable.getSize(); i++)
        {
            int coverageGlyphId = coverageTable.getGlyphId(i);
            int substituteGlyphId = coverageGlyphId
                    + singleSubstTableFormat2.getSubstituteGlyphIDs()[i];
            putNewSubstitutionEntry(glyphSubstitutionMap, substituteGlyphId,
                    Collections.singletonList(coverageGlyphId));
        }
    }

    private void extractDataFromLigatureSubstitutionSubstFormat1Table(
            Map<List<Integer>, Integer> glyphSubstitutionMap,
            LookupTypeLigatureSubstitutionSubstFormat1 ligatureSubstitutionTable)
    {

        for (LigatureSetTable ligatureSetTable : ligatureSubstitutionTable.getLigatureSetTables())
        {
            for (LigatureTable ligatureTable : ligatureSetTable.getLigatureTables())
            {
                extractDataFromLigatureTable(glyphSubstitutionMap, ligatureTable);
            }

        }

    }

    private void extractDataFromLigatureTable(Map<List<Integer>, Integer> glyphSubstitutionMap,
            LigatureTable ligatureTable)
    {
        int[] componentGlyphIDs = ligatureTable.getComponentGlyphIDs();
        List<Integer> glyphsToBeSubstituted = new ArrayList<>(componentGlyphIDs.length);
        for (int componentGlyphID : componentGlyphIDs)
        {
            glyphsToBeSubstituted.add(componentGlyphID);
        }

        DomGlobal.console.debug("glyphsToBeSubstituted: " + glyphsToBeSubstituted);

        putNewSubstitutionEntry(glyphSubstitutionMap, ligatureTable.getLigatureGlyph(),
                glyphsToBeSubstituted);

    }

    private void putNewSubstitutionEntry(Map<List<Integer>, Integer> glyphSubstitutionMap,
            int newGlyph, List<Integer> glyphsToBeSubstituted)
    {
        Integer oldValue = glyphSubstitutionMap.put(glyphsToBeSubstituted, newGlyph);

        if (oldValue != null)
        {
            String message = "For the newGlyph: " + newGlyph + ", newValue: "
                    + glyphsToBeSubstituted + " is trying to override the oldValue: " + oldValue;
            DomGlobal.console.warn(message);
        }
    }

    private static class ScriptTableDetails
    {
        private final Language language;
        private final String featureName;
        private final ScriptTable scriptTable;

        private ScriptTableDetails(Language language, String featureName, ScriptTable scriptTable)
        {
            this.language = language;
            this.featureName = featureName;
            this.scriptTable = scriptTable;
        }

        public Language getLanguage()
        {
            return language;
        }

        public String getFeatureName()
        {
            return featureName;
        }

        public ScriptTable getScriptTable()
        {
            return scriptTable;
        }

    }

}
