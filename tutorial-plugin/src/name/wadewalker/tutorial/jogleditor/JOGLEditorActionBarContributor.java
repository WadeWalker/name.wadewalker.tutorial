package name.wadewalker.tutorial.jogleditor;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.EditorActionBarContributor;

//==============================================================================
/**
 * Contributes the JOGLEditor's actions to the action bar.
 *
 * Copyright (c) 2010-2011 Wade Walker. Free for any use, but credit is appreciated.
 * @author Wade Walker
 */
public class JOGLEditorActionBarContributor extends EditorActionBarContributor {

    /** Runs and pauses the simulation. */
    private RunPauseAction runpauseaction;

    //==============================================================================
    /**
     * Constructor. Creates the actions.
     */
    public JOGLEditorActionBarContributor() {
        runpauseaction = new RunPauseAction();
    }

    //==============================================================================
    /**
     * Registers action handlers.
     *
     * @param iactionbars Used to register global actions.
     * @param iworkbenchpage Used to set part listeners.
     * @see org.eclipse.ui.part.EditorActionBarContributor#init(org.eclipse.ui.IActionBars, org.eclipse.ui.IWorkbenchPage)
     */
    @Override
    public void init( IActionBars iactionbars, IWorkbenchPage iworkbenchpage ) {
        super.init( iactionbars, iworkbenchpage );

        // register handlers
        iactionbars.setGlobalActionHandler( RunPauseAction.ssID, runpauseaction );
    }

    //==============================================================================
    /**
     * Contributes the editor's actions to the tool bar.
     *
     * @param itoolbarmanager Used to add actions to the editor tool bar.
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
     */
    @Override
    public void contributeToToolBar( IToolBarManager itoolbarmanager ) {
        itoolbarmanager.add( runpauseaction );
    }
}
