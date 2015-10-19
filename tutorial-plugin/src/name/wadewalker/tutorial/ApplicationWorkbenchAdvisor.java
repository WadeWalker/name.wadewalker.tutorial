package name.wadewalker.tutorial;

import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

//==============================================================================
/**
 * Configures and initializes the workbench.
 *
 * Copyright (c) 2010-2011 Wade Walker. Free for any use, but credit is appreciated.
 * @author Wade Walker
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    /** String used to identify the workbench's perspective. */
    private static final String ssPerspectiveID = "name.wadewalker.tutorial.perspective"; //$NON-NLS-1$

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor( IWorkbenchWindowConfigurer iworkbenchwindowconfigurer ) {
        return new ApplicationWorkbenchWindowAdvisor( iworkbenchwindowconfigurer );
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public String getInitialWindowPerspectiveId() {
        return ssPerspectiveID;
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize( IWorkbenchConfigurer iworkbenchconfigurer ) {
        super.initialize( iworkbenchconfigurer );

        // remember layout and window sizes between runs
        iworkbenchconfigurer.setSaveAndRestore( true );

        // turn on curved tabs
        PlatformUI.getPreferenceStore().setValue( IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false );
    }
}
