package org.andengine.extension.tmx;

import java.util.LinkedList;

import org.andengine.extension.tmx.util.constants.TMXConstants;
import org.andengine.util.SAXUtils;
import org.xml.sax.Attributes;

import android.util.Pair;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 11:21:01 - 29.07.2010
 */
public class TMXObject implements TMXConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final String mName;
	private final String mType;
	private final float mX;
	private final float mY;
	private final float mWidth;
	private final float mHeight;
	private final int mGid;
    private final long mGidRaw;
    private final float mRotation;
	private final TMXProperties<TMXObjectProperty> mTMXObjectProperties = new TMXProperties<TMXObjectProperty>();
	private final LinkedList<Pair<Float,Float>> mTMXObjectPolyline = new LinkedList<Pair<Float,Float>>();
	private final boolean flippedHorizontal;
    private final boolean flippedVertical;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TMXObject(final Attributes pAttributes) {
		this.mName = pAttributes.getValue("", TMXConstants.TAG_OBJECT_ATTRIBUTE_NAME);
		this.mType = pAttributes.getValue("", TMXConstants.TAG_OBJECT_ATTRIBUTE_TYPE);
		this.mX = SAXUtils.getFloatAttributeOrThrow(pAttributes, TMXConstants.TAG_OBJECT_ATTRIBUTE_X);
		this.mY = SAXUtils.getFloatAttributeOrThrow(pAttributes, TMXConstants.TAG_OBJECT_ATTRIBUTE_Y);
		this.mWidth = SAXUtils.getFloatAttribute(pAttributes, TMXConstants.TAG_OBJECT_ATTRIBUTE_WIDTH, 0f);
		this.mHeight = SAXUtils.getFloatAttribute(pAttributes, TMXConstants.TAG_OBJECT_ATTRIBUTE_HEIGHT, 0f);
		this.mGidRaw = SAXUtils.getLongAttribute(pAttributes, TMXConstants.TAG_TILE_ATTRIBUTE_GID, 0);
        this.mGid = getGidAttribute(); //SAXUtils.getIntAttribute(pAttributes, TMXConstants.TAG_TILE_ATTRIBUTE_GID, 0);
        this.mRotation = SAXUtils.getFloatAttribute(pAttributes, TMXConstants.TAG_OBJECT_ATTRIBUTE_ROTATION, 0);
        flippedHorizontal = getFlippedHorizontalAttribute();
        flippedVertical = getFlippedVerticalAttribute();
	}

    private boolean getFlippedVerticalAttribute() {
        if (this.mGidRaw == 0) return false;
        return (this.mGidRaw & FLIPPED_VERTICALLY_FLAG) > 0;
    }

    private boolean getFlippedHorizontalAttribute() {
        if (this.mGidRaw == 0) return false;
        return (this.mGidRaw & FLIPPED_HORIZONTALLY_FLAG) > 0;
    }

    private int getGidAttribute() {
        if (this.mGidRaw == 0) return 0;

        long gid = this.mGidRaw;
        gid &= ~(FLIPPED_HORIZONTALLY_FLAG | FLIPPED_VERTICALLY_FLAG | FLIPPED_DIAGONALLY_FLAG);

        return (int) gid;
    }

    // ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getName() {
		return this.mName;
	}

	public String getType() {
		return this.mType;
	}

	public float getX() {
		return this.mX;
	}

	public float getY() {
		return this.mY;
	}

	public float getWidth() {
		return this.mWidth;
	}

	public float getHeight() {
		return this.mHeight;
	}

	public int getGid() {
		return this.mGid;
	}

    public float getRotation() {
        return mRotation;
    }

	public void addTMXObjectProperty(final TMXObjectProperty pTMXObjectProperty) {
		this.mTMXObjectProperties.add(pTMXObjectProperty);
	}

	public TMXProperties<TMXObjectProperty> getTMXObjectProperties() {
		return this.mTMXObjectProperties;
	}

	public void setTMXObjectPolygon(final String pPolygon) {
        this.mTMXObjectPolyline.addAll(parsePoints(pPolygon));
        Pair<Float, Float> origin = mTMXObjectPolyline.get(0);
        this.mTMXObjectPolyline.add(new Pair<Float,Float>(origin.first, origin.second));
}
       
	public void setTMXObjectPolyline(final String pPolyline) {
	        this.mTMXObjectPolyline.addAll(parsePoints(pPolyline));
	}
	       
	private LinkedList<Pair<Float,Float>> parsePoints(final String pPoints) {
	        LinkedList<Pair<Float,Float>> list = new LinkedList<Pair<Float,Float>>();
            Float x,y;
	       
	        String[] tokens = pPoints.split(" ");
	        for(int i = 0; i < tokens.length; i++){
	                x = Float.parseFloat(tokens[i].split(",")[0]);
	                y = Float.parseFloat(tokens[i].split(",")[1]);
	                list.add(new Pair<Float,Float>(x,y));
	        }
	       
	        return list;
	}
	 
	public LinkedList<Pair<Float,Float>> getTMXObjectPolyline() {
	        return this.mTMXObjectPolyline;
	}

    public boolean isFlippedHorizontal() {
        return flippedHorizontal;
    }

    public boolean isFlippedVertical() {
        return flippedVertical;
    }

    // ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
