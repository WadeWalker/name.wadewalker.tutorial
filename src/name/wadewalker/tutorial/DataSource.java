package name.wadewalker.tutorial;

import java.util.ArrayList;
import java.util.List;

//================================================================
/**
 * A source of some time-varying data. In this case, it's
 * a traveling sine wave drawn with a set of quads.
 *
 * This data source is supposed to mimic something like a dynamically
 * changing graph or fluid field, where you can't just draw the
 * same static object over and over -- you have to stream it to
 * the graphics card on every frame.
 * 
 * Copyright: Copyright (c) 2010 Wade Walker. Free for any use, but
 * credit is appreciated.
 * 
 * @author Wade Walker
 * @version 1.0
 */
public class DataSource {

    /** Tracks the current time it arbitrary units. */
    protected double dT;

    /** Number of x increments going across the data. */
    protected int iXIncrements = 300000;

    /** List of data objects in this application. */
    protected List<DataObject> listDataObjects;

    public class DataObject {
        /** Origin of object. */
        double dX, dY;

        /** Width and height of object. */
        double dWidth, dHeight;
        
        /** RGB color components. */
        float [] afColor;

        public DataObject() {
        }

        public float getX() {
            return( (float)dX );
        }

        public void setX( double dXParam ) {
            dX = dXParam;
        }

        public float getY() {
            return( (float)dY );
        }

        public void setY( double dYParam ) {
            dY = dYParam;
        }

        public float getWidth() {
            return( (float)dWidth );
        }

        public void setWidth( double dWidthParam ) {
            dWidth = dWidthParam;
        }

        public float getHeight() {
            return( (float)dHeight );
        }

        public void setHeight( double dHeightParam ) {
            dHeight = dHeightParam;
        }

        public float [] getColor() {
            return( afColor );
        }

        public void setColor( float [] afColorParam ) {
            afColor = afColorParam;
        }
    }

    //================================================================
    /**
     * Constructor. Creates empty data objects with random colors.
     */
    public DataSource() {
        listDataObjects = new ArrayList<DataObject>();
        for( int i = 0; i < iXIncrements; i++ ) {
            DataObject dataobject = new DataObject();
            dataobject.setColor( new float [] {(float)Math.random(), (float)Math.random(), (float)Math.random()} );
            listDataObjects.add( dataobject );
        }
    }

    //================================================================
    /**
     * Updates the data objects to the current time and returns them.
     * @return a list of the data objects.
     */
    public List<DataObject> getData() {
        double dXIncrement = 2.0 * Math.PI * (1.0 / iXIncrements);
        double dX = -Math.PI;
        for( int i = 0; i < iXIncrements; dX += dXIncrement, i++ ) {
            double dY = Math.sin( dX + dT );
            DataObject dataobject = listDataObjects.get( i );
            dataobject.setX( dX );
            dataobject.setY( dY > 0 ? 0 : dY );
            dataobject.setWidth( dXIncrement );
            dataobject.setHeight( Math.abs( dY ) );
        }
        return( listDataObjects );
    }
    
    //================================================================
    /**
     * Increments the current time.
     * @param dDeltaT Amount to increment time by, in arbitrary units.
     */
    public void incrementTime( double dDeltaT ) {
        dT += dDeltaT;
    }
}
