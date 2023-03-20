package dev.wfj.gwtpdfbox.pdmodel.font;

import java.util.ArrayList;
import java.util.List;

import dev.wfj.gwtpdfbox.fontbox.ttf.TrueTypeFont;

public class EmbeddedFontProvider extends FontProvider {
    private final FontCache cache;
    private final List<FontInfo> fontInfoList = new ArrayList<>();
    

    public EmbeddedFontProvider(FontCache fontcache) {
        cache = fontcache;
    }

    @Override
    public List<? extends FontInfo> getFontInfo() {
        return fontInfoList;
    }

    @Override
    public String toDebugString()
    {
        StringBuilder sb = new StringBuilder();
        for (FontInfo info : fontInfoList)
        {
            sb.append(info.getFormat());
            sb.append(": ");
            sb.append(info.getPostScriptName());
            sb.append(": ");
            //sb.append(info.file.getPath());
            sb.append('\n');
        }
        return sb.toString();
    }

    public void addFont(TrueTypeFont lastResortFont) {
    }
}
