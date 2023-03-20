package dev.wfj.gwtpdfbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.DataResource.MimeType;

public interface GwtFontResources extends ClientBundle {
    GwtFontResources INSTANCE = GWT.create(GwtFontResources.class);
  
    @Source("Helvetica.afm")
    TextResource helveticaAfm();

    @MimeType("font/otf")
    @Source("Helvetica.otf")
    DataResource helveticaOtf();

    @MimeType("font/ttf")
    @Source("Helvetica.ttf")
    DataResource liberationSansRegularTtf();

    @Source("glyphlist/glyphlist.txt")
    TextResource glyphlistGlyphs();

    @Source("glyphlist/zapfdingbats.txt")
    TextResource zapfdingbatsGlyphs();

    @Source("glyphlist/additional.txt")
    TextResource additionalGlyphs();
  }