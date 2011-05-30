package name.wadewalker.tutorial.jogleditor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

//==============================================================================
/**
 * Editor input object for the JOGL editor. Currently these editors
 * aren't associated with any files, but eventually they will be.
 *
 * Copyright (c) 2010-2011 Wade Walker. Free for any use, but credit is appreciated.
 * @author Wade Walker
 */
public class JOGLEditorInput implements IEditorInput {

    //==============================================================================
    /**
     * Returns an object which is an instance of the given class
     * associated with this object. Returns null if no such object can
     * be found.
     *
     * @param classAdapter the adapter class to look up.
     * @return an object castable to the given class, or null if this
     * object does not have an adapter for the given class.
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
    public Object getAdapter( Class classAdapter ) {
        if( classAdapter.equals( JOGLEditorInput.class ) )
            return this;

        return null;
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists() {
        return true;
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "JOGLEditor";
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public String getToolTipText() {
        return "Editor that uses JOGL";
    }
}
