package name.wadewalker.tutorial.jogleditor;

import name.wadewalker.tutorial.Activator;

import org.eclipse.jface.action.Action;

//==============================================================================
/**
 * Runs or pauses the simulation on alternate button presses.
 * Changes the picture and tooltip of the button accordingly.
 *
 * Copyright (c) 2010-2011 Wade Walker. Free for any use, but credit is appreciated.
 * @author Wade Walker
 */
public class RunPauseAction extends Action {

    /** Unique action ID for the Eclipse platform. */
    public static final String ssID = "JOGLEditor.RunPauseAction";

    /** True if the simulation is running, false if it's paused. */
    private boolean bRunning;

    //==============================================================================
    /**
     * Constructor.
     */
    public RunPauseAction() {
        super( "Run", Action.AS_PUSH_BUTTON );
        setToolTipText( "Run simulation" );
        setImageDescriptor( Activator.getIcon( "jogleditor/resume_co.gif" ) );
        setId( ssID );
        setActionDefinitionId( ssID );
    }

    //==============================================================================
    /**
     * Code to run the action.
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        if( bRunning ) {
            setToolTipText( "Run simulation" );
            setImageDescriptor( Activator.getIcon( "jogleditor/resume_co.gif" ) );
        }
        else {
            setToolTipText( "Pause simulation" );
            setImageDescriptor( Activator.getIcon( "jogleditor/suspend_co.gif" ) );
        }
        bRunning = !bRunning;
    }

    //==============================================================================
    /**
     * Accessor.
     * @return true if the simulation is running, false otherwise.
     */
    public boolean isRunning() {
        return( bRunning );
    }
}
