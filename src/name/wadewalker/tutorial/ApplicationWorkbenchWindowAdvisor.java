package name.wadewalker.tutorial;

import name.wadewalker.tutorial.jogleditor.JOGLEditor;
import name.wadewalker.tutorial.jogleditor.JOGLEditorInput;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

//==============================================================================
/**
 * Configures the workbench window before and after it opens.
 * 
 * Copyright (c) 2010-2011 Wade Walker. Free for any use, but credit is appreciated.
 * @author Wade Walker
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    /** Used if window size not remembered from previous run. */
    private static final int siInitialWindowWidth = 400;

    /** Used if window size not remembered from previous run. */
    private static final int siInitialWindowHeight = 300;
    
    //==============================================================================
    /**
     * Constructor.
     * @param iworkbenchwindowconfigurer Default configurer.
     */
    public ApplicationWorkbenchWindowAdvisor( IWorkbenchWindowConfigurer iworkbenchwindowconfigurer ) {
        super( iworkbenchwindowconfigurer );
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public ActionBarAdvisor createActionBarAdvisor( IActionBarConfigurer iactionbarconfigurer ) {
        return new ApplicationActionBarAdvisor( iactionbarconfigurer );
    }

    //==============================================================================
    /**
     * Configures the window size, window title, et cetera before it opens.
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#preWindowOpen()
     */
    @Override
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer iworkbenchwindowconfigurer = getWindowConfigurer();
        iworkbenchwindowconfigurer.setTitle( "Tutorial" );
        iworkbenchwindowconfigurer.setInitialSize( new Point( siInitialWindowWidth, siInitialWindowHeight) );
        iworkbenchwindowconfigurer.setShowMenuBar( true );
        iworkbenchwindowconfigurer.setShowCoolBar( true );
        iworkbenchwindowconfigurer.setShowStatusLine( true );
    }

    //==============================================================================
    /**
     * Performs final setup once the window is open.
     *
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowOpen()
     */
    @Override
    public void postWindowOpen() {

        IWorkbenchWindow iworkbenchwindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        try {
            iworkbenchwindow.getActivePage().openEditor( new JOGLEditorInput(), JOGLEditor.ssID );
        }
        catch( PartInitException partinitexception ) {
            // TODO: add error handling
        }
    }
}
