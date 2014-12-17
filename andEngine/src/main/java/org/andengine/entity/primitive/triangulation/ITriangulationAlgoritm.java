package org.andengine.entity.primitive.triangulation;

import org.andengine.util.math.Vector2;

import java.util.List;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 20:16:04 - 14.09.2010
 */
public interface ITriangulationAlgoritm {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * @return a {@link java.util.List} of {@link com.badlogic.gdx.math.Vector2} objects where every three {@link com.badlogic.gdx.math.Vector2} objects form a triangle.
	 */
	public List<Vector2> computeTriangles(final List<Vector2> pVertices);
}
