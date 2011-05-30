package name.wadewalker.tutorial;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

//==============================================================================
/**
 * This class controls what happens when the app starts and stops. This is RCP
 * boilerplate.
 *
 * Copyright (c) 2010-2011 Wade Walker. Free for any use, but credit is appreciated.
 * @author Wade Walker
 */
public class Application implements IApplication {

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public Object start( IApplicationContext iapplicationcontext ) throws Exception {
        Display display = PlatformUI.createDisplay();
        try {
            int iReturnCode = PlatformUI.createAndRunWorkbench( display, new ApplicationWorkbenchAdvisor() );
            if (iReturnCode == PlatformUI.RETURN_RESTART)
                return IApplication.EXIT_RESTART;
            else
                return IApplication.EXIT_OK;
        }
        finally {
            display.dispose();
        }
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (!PlatformUI.isWorkbenchRunning())
            return;
        final IWorkbench iworkbench = PlatformUI.getWorkbench();
        final Display display = iworkbench.getDisplay();
        display.syncExec( new Runnable() {
            public void run() {
                if (!display.isDisposed())
                    iworkbench.close();
            }
        });
    }
}
