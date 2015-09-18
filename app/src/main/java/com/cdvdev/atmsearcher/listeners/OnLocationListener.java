package com.cdvdev.atmsearcher.listeners;

import com.cdvdev.atmsearcher.models.LocationPoint;

public interface OnLocationListener {
    void onUpdateLocation(LocationPoint locationPoint);
}
