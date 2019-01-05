package name.wadewalker.tutorial.jogleditor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;

import com.jogamp.common.nio.Buffers;
import com.jogamp.common.util.JarUtil;

import name.wadewalker.tutorial.Activator;
import name.wadewalker.tutorial.DataSource;
import name.wadewalker.tutorial.DataSource.DataObject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

//==============================================================================
/**
 * Copyright (c) 2010-2011 Wade Walker. Free for any use, but credit is appreciated.
 * @author Wade Walker
 */
public class JOGLEditor extends EditorPart {

    /** Workbench uses this ID to refer to instances of this type of editor. */
    public static final String ssID = "name.wadewalker.tutorial.jogleditor";

    /** Constant used in FPS calculation. */ 
    protected static final long slMillisecondsPerSecond = 1000; 

    /** Ratio of world-space units to screen pixels.
     * Increasing this zooms the display out,
     * decreasing it zooms the display in. */
    protected static final float sfObjectUnitsPerPixel = 0.03f;

    /** Amount to increment time on each sim step. */
    protected static final double sdTimeStep = 0.005;
    
    /** Milliseconds to sleep in each render cycle. */
    protected static final int siSleepPerStepMS = 1;

    /** Holds the OpenGL canvas. */
    protected Composite composite;

    /** Widget that displays OpenGL content. */
    protected GLCanvas glcanvas;

    /** Used to get OpenGL object that we need to access OpenGL functions. */
    protected GLContext glcontext;

    /** Source of data to draw. */
    protected DataSource datasource;

    /** X distance to translate the viewport by. */
    protected float fViewTranslateX;

    /** Y distance to translate the viewport by. */
    protected float fViewTranslateY;

    /** Index of vertex buffer object. We store interleaved vertex and color data here
     * like this: x0, r0, y0, g0, z0, b0, x1, r1, y1, g1, z1, b1...
     * Stored in an array because glGenBuffers requires it. */
    protected int [] aiVertexBufferIndices = new int [] {-1};

    /** Number of frames drawn since last FPS calculation. */
    protected int iFPSFrames;

    /** Time in milliseconds at start of FPS calculation interval. */
    protected long lFPSIntervalStartTimeMS;

    //==============================================================================
    /**
     * Constructor.
     */
    public JOGLEditor() {
        datasource = new DataSource();
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave( IProgressMonitor iprogressmonitor ) {
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveAs() {
    }

    //==============================================================================
    /**
     * <p>Sets up key action handlers.</p>
     * {@inheritDoc}
     */
    @Override
    public void init( IEditorSite ieditorsite, IEditorInput ieditorinput ) throws PartInitException {

        setSite( ieditorsite );
        if( ieditorinput != null )
            setInput( ieditorinput );

        // create action handlers
        try {
            Activator.createKeyBinding(
                new IAction [] {
                    ieditorsite.getActionBars().getGlobalActionHandler( RunPauseAction.ssID ),
                }, 
                new String [] {
                    "Space",
                },
                getSite() );
        }
        catch( ParseException parseexception ) {
            throw new PartInitException( parseexception.getMessage() );
        }
        catch( IOException ioexception ) {
            throw new PartInitException( ioexception.getMessage() );
        }
    }

    //==============================================================================
    /**
     * Disposes all OpenGL resources in case this view is closed and reopened.
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        disposeVertexBuffers();
        glcanvas.dispose();
        super.dispose();
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirty() {
        return false;
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    //==============================================================================
    /**
     * <p>Sets up an OpenGL canvas to draw in.</p>
     * {@inheritDoc}
     */
    @Override
    public void createPartControl( Composite compositeParent ) {
        JarUtil.setResolver( new JarUtil.Resolver() {
            public URL resolve( URL url ) {
                try {
                    URL urlTest = FileLocator.resolve( url );
                    // HACK: required because FileLocator.resolve() doesn't return an
                    // escaped URL, which makes conversion to a URI inside JOGL fail.
                    // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=145096 for details.
                    URI uriResolved = null;
                    try {
                        uriResolved = new URI(urlTest.getProtocol(), urlTest.getPath(), null);
                    }
                    catch( URISyntaxException urisyntaxexception ) {
                        // should never happen, since FileLocator's URLs should at least be syntactically correct
                        urisyntaxexception.printStackTrace();
                    }
                    URL urlNew = uriResolved.toURL();
                    return( urlNew ); 
                }
                catch( IOException ioexception ) {
                    return( url );
                }
            }
        } );

        GLProfile glprofile = GLProfile.get( GLProfile.GL2 );

        composite = new Composite( compositeParent, SWT.NONE );
        composite.setLayout( new FillLayout() );

        GLData gldata = new GLData();
        gldata.doubleBuffer = true;
        glcanvas = new GLCanvas( composite, SWT.NO_BACKGROUND, gldata );
        glcanvas.setCurrent();
        glcontext = GLDrawableFactory.getFactory( glprofile ).createExternalGLContext();

        glcanvas.addListener( SWT.Resize, new Listener() {
            public void handleEvent( Event event ) {
                glcanvas.setCurrent();
                glcontext.makeCurrent();
                GL2 gl2 = glcontext.getGL().getGL2();
                setTransformsAndViewport( gl2 );
                glcontext.release();
            }
        });

        glcontext.makeCurrent();
        GL2 gl2 = glcontext.getGL().getGL2();
        gl2.setSwapInterval( 1 );
        gl2.glClearColor( 1.0f, 1.0f, 1.0f, 1.0f );
        gl2.glColor3f( 1.0f, 0.0f, 0.0f );
        gl2.glHint( GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST );
        gl2.glClearDepth( 1.0 );
        gl2.glLineWidth( 2 );
        gl2.glEnable( GL.GL_DEPTH_TEST );
        glcontext.release();

        // spawn a worker thread to call the renderer in a loop until the program closes.
        (new Thread() {
            public void run() {

                // look at the run/pause button state to see whether we should be running or not 
                RunPauseAction runpauseaction = (RunPauseAction)getEditorSite().getActionBars().getGlobalActionHandler( RunPauseAction.ssID );

                // render once to get it on screen (we start out paused)
                render();

                try {
                    while( (glcanvas != null) && !glcanvas.isDisposed() ) {
                        // if we're running, render in the GUI thread
                        if( runpauseaction.isRunning() )
                            render();
                        // else we're paused, so sleep for a little so we don't peg the CPU
                        else
                            sleep( siSleepPerStepMS );
                    }
                }
                catch( InterruptedException interruptedexception ) {
                    // if sleep interrupted just let the thread quite
                }
            }
        }).start();
    }

    //==============================================================================
    /**
     * Calculates the FPS and shows it in the status line.
     */
    protected void calculateAndShowFPS() {
        ++iFPSFrames;
        long lTime = System.currentTimeMillis();
        // update the FPS (once per second at most, to avoid flooding
        // the UI with text updates)
        long lTimeIntervalMS = lTime - lFPSIntervalStartTimeMS;
        if( lTimeIntervalMS >= slMillisecondsPerSecond ) {
            lFPSIntervalStartTimeMS = lTime;
            int iFPS = (int)((double)(iFPSFrames * slMillisecondsPerSecond) / (double)lTimeIntervalMS);
            iFPSFrames = 0;
            getEditorSite().getActionBars().getStatusLineManager().setMessage( String.format( "FPS: %d", iFPS ) );
        }
    }

    //==============================================================================
    /**
     * Renders into the GUI thread synchronously. Meant to be called
     * from a worker thread.
     */
    private void render() {

        PlatformUI.getWorkbench().getDisplay().syncExec( new Runnable() {
            public void run() {
                if( (glcanvas != null) && !glcanvas.isDisposed() ) {
                    glcanvas.setCurrent();
                    glcontext.makeCurrent();
                    GL2 gl2 = glcontext.getGL().getGL2();
                    gl2.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
                    gl2.glClearColor( 1.0f, 1.0f, 1.0f, 1.0f );

                    // create vertex buffers if needed, then copy data in
                    int [] aiNumOfVertices = createAndFillVertexBuffer( gl2, datasource.getData() );

                    // needed so material for quads will be set from color map
                    gl2.glColorMaterial( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE );
                    gl2.glEnable( GL2.GL_COLOR_MATERIAL );

                    // draw all quads in vertex buffer
                    gl2.glBindBuffer( GL.GL_ARRAY_BUFFER, aiVertexBufferIndices[0] );
                    gl2.glEnableClientState( GL2.GL_VERTEX_ARRAY );
                    gl2.glEnableClientState( GL2.GL_COLOR_ARRAY );
                    gl2.glVertexPointer( 3, GL.GL_FLOAT, 6 * Buffers.SIZEOF_FLOAT, 0 );
                    gl2.glColorPointer( 3, GL.GL_FLOAT, 6 * Buffers.SIZEOF_FLOAT, 3 * Buffers.SIZEOF_FLOAT );
                    gl2.glPolygonMode( GL.GL_FRONT, GL2.GL_FILL );
                    gl2.glDrawArrays( GL2.GL_QUADS, 0, aiNumOfVertices[0] );

                    // disable arrays once we're done
                    gl2.glBindBuffer( GL.GL_ARRAY_BUFFER, 0 );
                    gl2.glDisableClientState( GL2.GL_VERTEX_ARRAY );
                    gl2.glDisableClientState( GL2.GL_COLOR_ARRAY );
                    gl2.glDisable( GL2.GL_COLOR_MATERIAL );

                    glcanvas.swapBuffers();
                    glcontext.release();

                    // advance time so the data changes for the next frame
                    datasource.incrementTime( sdTimeStep );

                    calculateAndShowFPS();
                }
            }
        });
    }

    //==============================================================================
    /**
     * Sets up an orthogonal projection suitable for a 2D CAD program.
     *
     * @param gl2 GL object to set transforms and viewport on.
     */
    protected void setTransformsAndViewport( GL2 gl2 ) {

        Rectangle rectangle = glcanvas.getClientArea();
        int iWidth = rectangle.width;
        int iHeight = Math.max( rectangle.height, 1 );

        gl2.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
        gl2.glLoadIdentity();

        // set the clipping planes based on the ratio of object units
        // to screen pixels, but preserving the correct aspect ratio
        GLU glu = new GLU();
        glu.gluOrtho2D( -(sfObjectUnitsPerPixel * iWidth) / 2.0f,
                         (sfObjectUnitsPerPixel * iWidth) / 2.0f,
                        -(sfObjectUnitsPerPixel * iHeight) / 2.0f,
                         (sfObjectUnitsPerPixel * iHeight) / 2.0f );

        gl2.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
        gl2.glViewport( 0, 0, iWidth, iHeight );
        gl2.glLoadIdentity();
        gl2.glTranslatef( fViewTranslateX, fViewTranslateY, 0.0f );
    }

    //==============================================================================
    /**
     * Creates vertex buffer object used to store vertices and colors
     * (if it doesn't exist). Fills the object with the latest
     * vertices and colors from the data store.
     *
     * @param gl2 GL object used to access all GL functions.
     * @param listDataObjects Data objects to get vertices from.
     * @return the number of vertices in each of the buffers.
     */
    protected int [] createAndFillVertexBuffer( GL2 gl2, List<DataObject> listDataObjects ) {

        int [] aiNumOfVertices = new int [] {listDataObjects.size() * 4};
        
        // create vertex buffer object if needed
        if( aiVertexBufferIndices[0] == -1 ) {
            // check for VBO support
            if(    !gl2.isFunctionAvailable( "glGenBuffers" )
                || !gl2.isFunctionAvailable( "glBindBuffer" )
                || !gl2.isFunctionAvailable( "glBufferData" )
                || !gl2.isFunctionAvailable( "glDeleteBuffers" ) ) {
                Activator.openError( "Error", "Vertex buffer objects not supported." );
            }

            gl2.glGenBuffers( 1, aiVertexBufferIndices, 0 );

            // create vertex buffer data store without initial copy
            gl2.glBindBuffer( GL.GL_ARRAY_BUFFER, aiVertexBufferIndices[0] );
            gl2.glBufferData( GL.GL_ARRAY_BUFFER,
                              aiNumOfVertices[0] * 3 * Buffers.SIZEOF_FLOAT * 2,
                              null,
                              GL2.GL_DYNAMIC_DRAW );
        }

        // map the buffer and write vertex and color data directly into it
        gl2.glBindBuffer( GL.GL_ARRAY_BUFFER, aiVertexBufferIndices[0] );
        ByteBuffer bytebuffer = gl2.glMapBuffer( GL.GL_ARRAY_BUFFER, GL2.GL_WRITE_ONLY );
        FloatBuffer floatbuffer = bytebuffer.order( ByteOrder.nativeOrder() ).asFloatBuffer();

        for( DataObject dataobject : listDataObjects )
            storeVerticesAndColors( floatbuffer, dataobject );

        gl2.glUnmapBuffer( GL.GL_ARRAY_BUFFER );

        return( aiNumOfVertices );
    }

    //==============================================================================
    /**
     * Stores the vertices and colors of one object interleaved into
     * a buffer (vertices in counterclockwise order).
     * @param floatbuffer Buffer to store vertices and colors in.
     * @param dataobject Object whose vertices and colors are stored.
     */
    protected void storeVerticesAndColors( FloatBuffer floatbuffer, DataObject dataobject ) {

        floatbuffer.put( dataobject.getX() );
        floatbuffer.put( dataobject.getY() );
        floatbuffer.put( 0.0f );

        floatbuffer.put( dataobject.getColor()[0] );
        floatbuffer.put( dataobject.getColor()[1] );
        floatbuffer.put( dataobject.getColor()[2] );

        floatbuffer.put( dataobject.getX() + dataobject.getWidth() );
        floatbuffer.put( dataobject.getY() );
        floatbuffer.put( 0.0f );

        floatbuffer.put( dataobject.getColor()[0] );
        floatbuffer.put( dataobject.getColor()[1] );
        floatbuffer.put( dataobject.getColor()[2] );

        floatbuffer.put( dataobject.getX() + dataobject.getWidth() );
        floatbuffer.put( dataobject.getY() + dataobject.getHeight() );
        floatbuffer.put( 0.0f );

        floatbuffer.put( dataobject.getColor()[0] );
        floatbuffer.put( dataobject.getColor()[1] );
        floatbuffer.put( dataobject.getColor()[2] );

        floatbuffer.put( dataobject.getX() );
        floatbuffer.put( dataobject.getY() + dataobject.getHeight() );
        floatbuffer.put( 0.0f );        

        floatbuffer.put( dataobject.getColor()[0] );
        floatbuffer.put( dataobject.getColor()[1] );
        floatbuffer.put( dataobject.getColor()[2] );
    }

    //==============================================================================
    /**
     * Deletes the vertex and color buffers.
     */
    protected void disposeVertexBuffers() {
        glcontext.makeCurrent();
        GL2 gl2 = glcontext.getGL().getGL2();
        gl2.glDeleteBuffers( 1, aiVertexBufferIndices, 0 );
        aiVertexBufferIndices[0] = -1;
        glcontext.release();
    }

    //==============================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
    }
}
