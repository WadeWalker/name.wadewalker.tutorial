package name.wadewalker.tutorial;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

//==============================================================================
/**
 * Creates, adds, and disposes the actions for the workbench window.
 *
 * Copyright (c) 2010-2011 Wade Walker. Free for any use, but credit is appreciated.
 * @author Wade Walker
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    /** Exits the app. */
    private IWorkbenchAction iworkbenchactionExit;

    //==============================================================================
    /**
     * Constructor.
     *
     * @param iactionbarconfigurer Data needed to configure the action bar.
     */
    public ApplicationActionBarAdvisor( IActionBarConfigurer iactionbarconfigurer ) {
        super( iactionbarconfigurer );
    }

    //==============================================================================
    /**
     * Creates and registers actions.
     * 
     * @param iworkbenchwindow Window the actions are for.
     * @see org.eclipse.ui.application.ActionBarAdvisor#makeActions(org.eclipse.ui.IWorkbenchWindow)
     */
    @Override
    protected void makeActions( final IWorkbenchWindow iworkbenchwindow ) {
        iworkbenchactionExit = ActionFactory.QUIT.create( iworkbenchwindow );
        register( iworkbenchactionExit );
    }

    //==============================================================================
    /**
     * Puts actions into the main menu bar.
     *
     * @param imenumanagerBar The menu manager for the menu bar.
     * @see org.eclipse.ui.application.ActionBarAdvisor#fillMenuBar(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    protected void fillMenuBar( IMenuManager imenumanagerBar ) {

        MenuManager menumanagerFile = new MenuManager( "&File", IWorkbenchActionConstants.M_FILE );
        imenumanagerBar.add( menumanagerFile );
        menumanagerFile.add( iworkbenchactionExit );
    }

    //==============================================================================
    /**
     * Puts the tool bar into the "cool bar" for the window.
     *
     * @param icoolbarmanager Used to add tool bars.
     * @see org.eclipse.ui.application.ActionBarAdvisor#fillCoolBar(org.eclipse.jface.action.ICoolBarManager)
     */
    @Override
    protected void fillCoolBar( ICoolBarManager icoolbarmanager ) {
        super.fillCoolBar( icoolbarmanager );

        ToolBarManager toolbarmanager = new ToolBarManager();
        icoolbarmanager.add( toolbarmanager );
    }
}
