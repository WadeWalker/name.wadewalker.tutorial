package name.wadewalker.tutorial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.contexts.Context;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.Scheme;
import org.eclipse.jface.bindings.keys.KeyBinding;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

//==============================================================================
/**
 * The activator class controls the plug-in life cycle.
 *
 * Copyright (c) 2010-2011 Wade Walker. Free for any use, but credit is appreciated.
 * @author Wade Walker
 */
public class Activator extends AbstractUIPlugin {

    /** The plug-in ID. */
    public static final String ssPluginID = "name.wadewalker.tutorial"; //$NON-NLS-1$

    /** The shared instance. */
    private static Activator sactivator;
    
    /** Eclipse package needed during key binding. */
    private static final String ssDefaultSchemeID = "org.eclipse.ui.defaultAcceleratorConfiguration";

    /** Eclipse package needed during key binding. */
    private static final String ssParentContextID = "org.eclipse.ui.contexts.window";

    /** ID of command category used in key binding. */
    private static final String ssCommandCategoryID = "Tutorial.commands.category";

    /** Name of command category used in key binding. */
    private static final String ssCommandCategoryName = "Tutorial commands";

    /** ID of command context used in key binding. */
    private static final String ssContextID = "viewerKeyContext";

    /** Name of command context used in key binding. */
    private static final String ssContextName = "In Placement Editor";

    //==============================================================================
    /**
     * Constructor.
     */
    public Activator() {
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public void start( BundleContext bundlecontext ) throws Exception {
        super.start( bundlecontext );
        sactivator = this;
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public void stop( BundleContext bundlecontext ) throws Exception {
        sactivator = null;
        super.stop( bundlecontext );
    }

    //==============================================================================
    /**
     * Accessor.
     *
     * @return the shared instance.
     */
    public static Activator getDefault() {
        return sactivator;
    }

    //==============================================================================
    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param sPath the path
     * @return the image descriptor.
     */
    public static ImageDescriptor getImageDescriptor( String sPath ) {
        return imageDescriptorFromPlugin( ssPluginID, sPath );
    }

    //==============================================================================
    /**
     * Returns an image descriptor for the specified icon.
     *
     * @param sIconPath Path to the icon file inside the "icons"
     * directory of the plugin project.
     * @return the image descriptor for the specified icon.
     */
    public static ImageDescriptor getIcon( String sIconPath ) {

        ImageDescriptor imagedescriptor = Activator.getImageDescriptor( "icons/" + sIconPath );
        if( imagedescriptor == null )
            imagedescriptor = ImageDescriptor.getMissingImageDescriptor();

        return( imagedescriptor );
    }

    //==============================================================================
    /**
     * Opens an error dialog box and logs the error.
     * @param sDialogTitle Title of dialog box.
     * @param istatus Status object to get message from.
     */
    public static void openError( String sDialogTitle, IStatus istatus ) {
        ErrorDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                               sDialogTitle,
                               null,
                               istatus );
        Activator.getDefault().log( sDialogTitle + " : " + istatus.getMessage() );
    }

    //==============================================================================
    /**
     * Opens an "internal error" dialog box and logs the error.
     * @param exception Exception containing the message to put in the dialog box.
     */
    public static void openError( Exception exception ) {
        openError( "Internal error", new Status( Status.ERROR,
                                                 Activator.ssPluginID,
                                                   exception.getMessage() != null
                                                 ? exception.getMessage()
                                                 : exception.getClass().toString() ) );
        Activator.getDefault().log( exception.getMessage(), exception );
    }

    //==============================================================================
    /**
     * Opens an error dialog box and logs the error.
     * @param sDialogTitle Dialog box title.
     * @param sMessage Message to put in dialog box.
     */
    public static void openError( String sDialogTitle, String sMessage ) {
        openError( sDialogTitle, new Status( Status.ERROR, Activator.ssPluginID, sMessage ) );
        Activator.getDefault().log( sDialogTitle + " : " + sMessage );
    }

    //==============================================================================
    /**
     * Prints a message to the Eclipse log.
     *
     * @param sMessage Message to print.
     */
    public void log( String sMessage ) {
        log( sMessage, null );
    }

    //==============================================================================
    /**
     * Prints a message to the Eclipse log.
     *
     * @param sMessage Message to print.
     * @param exception Exception we're logging.
     */
    public void log( String sMessage, Exception exception ) {
        getLog().log( new Status( Status.INFO, ssPluginID, Status.OK, sMessage, exception ) );
    }

    //==============================================================================
    /**
     * Creates key bindings for an array of actions, saving them to
     * the preference store if needed.
     *
     * Only creates a new key sequence if one doesn't exist for this
     * context; otherwise, leaves any altered user preferences in place.
     *
     * Takes an array so the number of writes to the preference store
     * can be minimized.
     *
     * TODO: this code is using the binding service in a way that
     * Paul Webster (the guy responsible for this part of Eclipse)
     * seems uncomfortable with, but right now there's no other option.
     * This code may be able to be replaced after Eclipse 3.4 sometime,
     * when PW finally puts in a programmatic interface for this.
     *
     * @param aiaction Actions to create key binding for.
     * @param asKey Keys to bind actions to (in the format required by KeySequence).
     * If the key string of an action is null, this method still activates
     * a handler and defines this action's command, which is needed to stop
     * some logged warnings when showing actions in menus.
     * @param iworkbenchpartsite Site to do the binding at.
     * @throws ParseException if one of the key strings can't be parsed.
     * @throws IOException if the key bindings can't be saved to the preference store.
     */
    public static void createKeyBinding( IAction [] aiaction,
                                         String [] asKey,
                                         IWorkbenchPartSite iworkbenchpartsite )
        throws ParseException, IOException {
        assert( aiaction.length == asKey.length );

        // services outside loop for speed
        IHandlerService ihandlerservice = (IHandlerService)iworkbenchpartsite.getService( IHandlerService.class );
        ICommandService icommandservice = (ICommandService)iworkbenchpartsite.getService( ICommandService.class );
        IContextService icontextservice = (IContextService)iworkbenchpartsite.getService( IContextService.class );
        IBindingService ibindingservice = (IBindingService)PlatformUI.getWorkbench().getAdapter( IBindingService.class );

        // current bindings
        Binding [] abinding = ibindingservice.getBindings();

        // new  bindings
        List<Binding> listNewBindings = new ArrayList<Binding>( 0 );

        for( int i = 0; i < aiaction.length; i++ ) {

            ActionHandler actionhandler = new ActionHandler( aiaction[i] );
            ihandlerservice.activateHandler( aiaction[i].getActionDefinitionId(), actionhandler );
    
            Category category = icommandservice.getCategory( ssCommandCategoryID );
            if( !category.isDefined() )
                category.define( ssCommandCategoryName, null );
    
            Command command = icommandservice.getCommand( aiaction[i].getActionDefinitionId() );
            if( !command.isDefined() )
                command.define( aiaction[i].getText(), aiaction[i].getDescription(), category );

            Context context = icontextservice.getContext( ssContextID );
            if( !context.isDefined() )
                context.define( ssContextName, null, ssParentContextID );

            icontextservice.activateContext( ssContextID );

            boolean bCreateNewBinding = true;

            // make sure the binding doesn't already exist for this context
            for( Binding binding : abinding ) {                    
                if(    (binding.getParameterizedCommand() != null)
                    && binding.getParameterizedCommand().getCommand().getId().equals( aiaction[i].getActionDefinitionId() )
                    && binding.getContextId().equals( ssContextID ) )
                    bCreateNewBinding = false;
            }

            if( bCreateNewBinding && (asKey[i] != null) ) {
                ParameterizedCommand parameterizedcommand = new ParameterizedCommand( command, null );
                KeySequence keysequence = KeySequence.getInstance( asKey[i] );
                listNewBindings.add( new KeyBinding( keysequence, parameterizedcommand,
                                                     ssDefaultSchemeID, ssContextID,
                                                     null, null, null,
                                                     Binding.USER ) );
            }
        }

        if( listNewBindings.size() == 0 )
            return;

        // add new bindings to current and save them
        Binding [] abindingNew = listNewBindings.toArray( new Binding [] {} );
        Binding [] abindingPlusNew = new Binding[abinding.length + abindingNew.length];

        System.arraycopy( abinding, 0, abindingPlusNew, 0, abinding.length );
        System.arraycopy( abindingNew, 0, abindingPlusNew, abinding.length, abindingNew.length );

        Scheme schemeDefault = ibindingservice.getScheme( ssDefaultSchemeID );
        ibindingservice.savePreferences( schemeDefault, abindingPlusNew );
    }
}