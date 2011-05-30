package name.wadewalker.tutorial;

import java.util.ArrayList;
import java.util.List;

//==============================================================================
/**
 * A source of some time-varying data. In this case, it's
 * a traveling sine wave drawn with a set of quads.
 *
 * This data source is supposed to mimic something like a dynamically
 * changing graph or fluid field, where you can't just draw the
 * same static object over and over -- you have to stream it to
 * the graphics card on every frame.
 * 
 * Copyright (c) 2010 Wade Walker. Free for any use, but credit is appreciated.
 * 
 * @author Wade Walker
 * @version 1.0
 */
public class DataSource {

    /** Number of x increments going across the data. */
    protected static final int siXIncrements = 300000;

    /** Tracks the current time it arbitrary units. */
    protected double dT;

    /** List of data objects in this application. */
    protected List<DataObject> listDataObjects;

    /** Represents some piece of data to be drawn. */
    public class DataObject {
        /** X coordinate of object origin. */
        private double dX;

        /** Y coordinate of object origin. */
        private double dY;

        /** Width of object. */
        private double dWidth;
        
        /** Height of object. */
        private double dHeight;
        
        /** RGB color components. */
        private float [] afColor;

        //==============================================================================
        /**
         * Constructor.
         */
        public DataObject() {
        }

        //==============================================================================
        /**
         * Accessor.
         * @return {@link #dX}
         */
        public float getX() {
            return( (float)dX );
        }

        //==============================================================================
        /**
         * Accessor.
         * @param dXParam {@link #dX}
         */
        public void setX( double dXParam ) {
            dX = dXParam;
        }

        //==============================================================================
        /**
         * Accessor.
         * @return {@link #dY}
         */
        public float getY() {
            return( (float)dY );
        }

        //==============================================================================
        /**
         * Accessor.
         * @param dYParam {@link #dY}
         */
        public void setY( double dYParam ) {
            dY = dYParam;
        }

        //==============================================================================
        /**
         * Accessor.
         * @return {@link #dWidth}
         */
        public float getWidth() {
            return( (float)dWidth );
        }

        //==============================================================================
        /**
         * Accessor.
         * @param dWidthParam {@link #dWidth}
         */
        public void setWidth( double dWidthParam ) {
            dWidth = dWidthParam;
        }

        //==============================================================================
        /**
         * Accessor.
         * @return {@link #dHeight}
         */
        public float getHeight() {
            return( (float)dHeight );
        }

        //==============================================================================
        /**
         * Accessor.
         * @param dHeightParam {@link #dHeight}
         */
        public void setHeight( double dHeightParam ) {
            dHeight = dHeightParam;
        }

        //==============================================================================
        /**
         * Accessor.
         * @return {@link #afColor}
         */
        public float [] getColor() {
            return( afColor );
        }

        //==============================================================================
        /**
         * Accessor.
         * @param afColorParam {@link #afColor}
         */
        public void setColor( float [] afColorParam ) {
            afColor = afColorParam;
        }
    }

    //==============================================================================
    /**
     * Constructor. Creates empty data objects with random colors.
     */
    public DataSource() {
        listDataObjects = new ArrayList<DataObject>();
        for( int i = 0; i < siXIncrements; i++ ) {
            DataObject dataobject = new DataObject();
            dataobject.setColor( new float [] {(float)Math.random(), (float)Math.random(), (float)Math.random()} );
            listDataObjects.add( dataobject );
        }
    }

    //==============================================================================
    /**
     * Updates the data objects to the current time and returns them.
     * @return a list of the data objects.
     */
    public List<DataObject> getData() {
        double dXIncrement = 2.0 * Math.PI * (1.0 / siXIncrements);
        double dX = -Math.PI;
        for( int i = 0; i < siXIncrements; dX += dXIncrement, i++ ) {
            double dY = Math.sin( dX + dT );
            DataObject dataobject = listDataObjects.get( i );
            dataobject.setX( dX );
            dataobject.setY( dY > 0 ? 0 : dY );
            dataobject.setWidth( dXIncrement );
            dataobject.setHeight( Math.abs( dY ) );
        }
        return( listDataObjects );
    }
    
    //==============================================================================
    /**
     * Increments the current time.
     * @param dDeltaT Amount to increment time by, in arbitrary units.
     */
    public void incrementTime( double dDeltaT ) {
        dT += dDeltaT;
    }
}
