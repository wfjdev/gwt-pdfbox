package dev.wfj.gwtpdfbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.TextResource;

public interface FontResources extends ClientBundle {
    FontResources INSTANCE = GWT.create(FontResources.class);
  
    @Source("Helvetica.afm")
    TextResource helvetica();
  }