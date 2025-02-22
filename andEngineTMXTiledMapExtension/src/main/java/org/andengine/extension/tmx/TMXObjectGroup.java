package org.andengine.extension.tmx;

import java.util.ArrayList;

import org.andengine.extension.tmx.util.constants.TMXConstants;
import org.andengine.util.SAXUtils;
import org.xml.sax.Attributes;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 11:20:49 - 29.07.2010
 */
public class TMXObjectGroup implements TMXConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final String mName;
	private final int mWidth;
	private final int mHeight;
	private final ArrayList<TMXObject> mTMXObjects = new ArrayList<TMXObject>();
	private final TMXProperties<TMXObjectGroupProperty> mTMXObjectGroupProperties = new TMXProperties<TMXObjectGroupProperty>();
    private final String mColor;
    private final boolean mVisible;

    // ===========================================================
	// Constructors
	// ===========================================================

	public TMXObjectGroup(final Attributes pAttributes) {
		this.mName = pAttributes.getValue("", TMXConstants.TAG_OBJECTGROUP_ATTRIBUTE_NAME);
		this.mWidth = SAXUtils.getIntAttribute(pAttributes, TMXConstants.TAG_OBJECTGROUP_ATTRIBUTE_WIDTH, 0);
		this.mHeight = SAXUtils.getIntAttribute(pAttributes, TMXConstants.TAG_OBJECTGROUP_ATTRIBUTE_HEIGHT, 0);
        this.mColor = SAXUtils.getAttribute(pAttributes, TMXConstants.TAG_OBJECTGROUP_ATTRIBUTE_COLOR, "#000000");
        this.mVisible = SAXUtils.getIntAttribute(pAttributes, TMXConstants.TAG_OBJECTGROUP_ATTRIBUTE_VISIBLE, 1) == 1;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getName() {
		return this.mName;
	}

	public int getWidth() {
		return this.mWidth;
	}

	public int getHeight() {
		return this.mHeight;
	}

    public String getColor() {
        return this.mColor;
    }

	void addTMXObject(final TMXObject pTMXObject) {
		this.mTMXObjects.add(pTMXObject);
	}

	public ArrayList<TMXObject> getTMXObjects() {
		return this.mTMXObjects ;
	}

	public void addTMXObjectGroupProperty(final TMXObjectGroupProperty pTMXObjectGroupProperty) {
		this.mTMXObjectGroupProperties.add(pTMXObjectGroupProperty);
	}

	public TMXProperties<TMXObjectGroupProperty> getTMXObjectGroupProperties() {
		return this.mTMXObjectGroupProperties;
	}

    public boolean isVisible() {
        return mVisible;
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
