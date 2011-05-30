package name.wadewalker.tutorial;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

//==============================================================================
/**
 * Sets the initial layout for the app's views and editors.
 * Copyright (c) 2010-2011 Wade Walker. Free for any use, but credit is appreciated.
 * @author Wade Walker
 */
public class Perspective implements IPerspectiveFactory {

    //==============================================================================
    /**
     * <p>Puts the single JOGL view in the editor area.</p>
     * {@inheritDoc}
     */
    public void createInitialLayout( IPageLayout ipagelayout ) {
        ipagelayout.setEditorAreaVisible( true );
    }
}
