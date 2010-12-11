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

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }

    //================================================================
    /**
     * Configures the window before it opens.
     *
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#preWindowOpen()
     */
    @Override
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer iworkbenchwindowconfigurer = getWindowConfigurer();
        iworkbenchwindowconfigurer.setTitle( "Tutorial" );
        iworkbenchwindowconfigurer.setInitialSize( new Point( 400, 300) );
        iworkbenchwindowconfigurer.setShowMenuBar( true );
        iworkbenchwindowconfigurer.setShowCoolBar( true );
        iworkbenchwindowconfigurer.setShowStatusLine( true );
    }

    //================================================================
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
        } catch( PartInitException partinitexception ) {
            // TODO: add error handling
        }
    }
}
