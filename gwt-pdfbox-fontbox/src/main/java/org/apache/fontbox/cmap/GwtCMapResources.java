package org.apache.fontbox.cmap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.client.ClientBundle.Source;

public interface GwtCMapResources extends ClientBundle {
    GwtCMapResources INSTANCE = GWT.create(GwtCMapResources.class);
  
    @Source("cmap/Identity-H")
    TextResource identityH();
}
