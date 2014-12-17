package org.andengine.engine.camera;


/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 22:11:17 - 25.03.2010
 */
public class SmoothCamera extends ZoomCamera {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected float mMaxVelocityX;
	protected float mMaxVelocityY;
	protected float mMaxZoomFactorChange;

	protected float mTargetCenterX;
	protected float mTargetCenterY;

	protected float mTargetZoomFactor;
	
	protected boolean mEasingEnabled = true;

	// ===========================================================
	// Constructors
	// ===========================================================

	public SmoothCamera(final float pX, final float pY, final float pWidth, final float pHeight, final float pMaxVelocityX, final float pMaxVelocityY, final float pMaxZoomFactorChange, boolean pEasingEnabled) {
		super(pX, pY, pWidth, pHeight);
		this.mMaxVelocityX = pMaxVelocityX;
		this.mMaxVelocityY = pMaxVelocityY;
		this.mMaxZoomFactorChange = pMaxZoomFactorChange;

		this.mTargetCenterX = this.getCenterX();
		this.mTargetCenterY = this.getCenterY();

		this.mTargetZoomFactor = 1.0f;
		
		this.mEasingEnabled = pEasingEnabled;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean ismEasingEnabled() {
		return mEasingEnabled;
	}

	public void setEasingEnabled(boolean pEasingEnabled) {
		this.mEasingEnabled = pEasingEnabled;
	}

	public float getTargetCenterX() {
		return this.mTargetCenterX;
	}

	public float getTargetCenterY() {
		return this.mTargetCenterY;
	}

	public float getTargetZoomFactor() {
		return this.mTargetZoomFactor;
	}

	@Override
	public void setCenter(final float pCenterX, final float pCenterY) {
		this.mTargetCenterX = pCenterX;
		this.mTargetCenterY = pCenterY;
	}

	public void setCenterDirect(final float pCenterX, final float pCenterY) {
		super.setCenter(pCenterX, pCenterY);
		this.mTargetCenterX = pCenterX;
		this.mTargetCenterY = pCenterY;
	}

	@Override
	public void setZoomFactor(final float pZoomFactor) {
		if(this.mTargetZoomFactor != pZoomFactor) {
			if(this.mTargetZoomFactor == this.mZoomFactor) {
				this.mTargetZoomFactor = pZoomFactor;

				this.onSmoothZoomStarted();
			} else {
				this.mTargetZoomFactor = pZoomFactor;
			}
		}
	}

	public void setZoomFactorDirect(final float pZoomFactor) {
		if(this.mTargetZoomFactor != this.mZoomFactor) {
			this.mTargetZoomFactor = pZoomFactor;
			super.setZoomFactor(pZoomFactor);

			this.onSmoothZoomFinished();
		} else {
			this.mTargetZoomFactor = pZoomFactor;
			super.setZoomFactor(pZoomFactor);
		}
	}

	public float getMaxVelocityX() {
		return this.mMaxVelocityX;
	}

	public void setMaxVelocityX(final float pMaxVelocityX) {
		this.mMaxVelocityX = pMaxVelocityX;
	}

	public float getMaxVelocityY() {
		return this.mMaxVelocityY;
	}

	public void setMaxVelocityY(final float pMaxVelocityY) {
		this.mMaxVelocityY = pMaxVelocityY;
	}

	public void setMaxVelocity(final float pMaxVelocityX, final float pMaxVelocityY) {
		this.mMaxVelocityX = pMaxVelocityX;
		this.mMaxVelocityY = pMaxVelocityY;
	}

	public float getMaxZoomFactorChange() {
		return this.mMaxZoomFactorChange;
	}

	public void setMaxZoomFactorChange(final float pMaxZoomFactorChange) {
		this.mMaxZoomFactorChange = pMaxZoomFactorChange;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	protected void onSmoothZoomStarted() {

	}

	protected void onSmoothZoomFinished() {

	}

	@Override
	public void onUpdate(final float pSecondsElapsed) {
		super.onUpdate(pSecondsElapsed);
		/* Update center. */
		final float currentCenterX = this.getCenterX();
		final float currentCenterY = this.getCenterY();

		final float targetCenterX = this.mTargetCenterX;
		final float targetCenterY = this.mTargetCenterY;

		if(currentCenterX != targetCenterX || currentCenterY != targetCenterY) {
			final float diffX = targetCenterX - currentCenterX;
			final float diffY = targetCenterY - currentCenterY;
			float dX = 0;
			float dY = 0;
			
			if (mEasingEnabled){
				dX = ((targetCenterX - currentCenterX) / 2) * (pSecondsElapsed / .25f);
				dY = ((targetCenterY - currentCenterY) / 2) * (pSecondsElapsed / .25f);
			}
			else{
				dX = this.limitToMaxVelocityX(diffX, pSecondsElapsed);
				dY = this.limitToMaxVelocityY(diffY, pSecondsElapsed);
			}
			super.setCenter(currentCenterX + dX, currentCenterY + dY);
		}

		/* Update zoom. */
		final float currentZoom = this.getZoomFactor();

		final float targetZoomFactor = this.mTargetZoomFactor;

		if(currentZoom != targetZoomFactor) {
			float absoluteZoomDifference = targetZoomFactor - currentZoom;
			if (mEasingEnabled){
				absoluteZoomDifference = absoluteZoomDifference * .25f;
			}
			final float zoomChange = this.limitToMaxZoomFactorChange(absoluteZoomDifference, pSecondsElapsed);
			super.setZoomFactor(currentZoom + zoomChange);

			if(this.mZoomFactor == this.mTargetZoomFactor) {
				this.onSmoothZoomFinished();
			}
		}
	}

	protected float limitToMaxVelocityX(final float pValue, final float pSecondsElapsed) {
		if(pValue > 0) {
			return Math.min(pValue, this.mMaxVelocityX * pSecondsElapsed);
		} else {
			return Math.max(pValue, -this.mMaxVelocityX * pSecondsElapsed);
		}
	}

	protected float limitToMaxVelocityY(final float pValue, final float pSecondsElapsed) {
		if(pValue > 0) {
			return Math.min(pValue, this.mMaxVelocityY * pSecondsElapsed);
		} else {
			return Math.max(pValue, -this.mMaxVelocityY * pSecondsElapsed);
		}
	}

	protected float limitToMaxZoomFactorChange(final float pValue, final float pSecondsElapsed) {
		if(pValue > 0) {
			return Math.min(pValue, this.mMaxZoomFactorChange * pSecondsElapsed);
		} else {
			return Math.max(pValue, -this.mMaxZoomFactorChange * pSecondsElapsed);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
