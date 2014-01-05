package com.hilton.effectiveandroid.sensors;

public interface OrientationListener {
    public void onOrientationChanged(float azimuth, float pitch, float roll);
    public void onActionUp();
    public void onActionDown();
}