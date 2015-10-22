package com.cdvdev.atmsearcher.listeners;

import android.view.View;

import com.cdvdev.atmsearcher.models.Atm;

import java.util.ArrayList;

/**
 * Listener for fragments callbacks
 */
public interface FragmentListener {
    void onAtmListItemSelected(Atm atm);
    void onChangeAppBarTitle(int res);
    void onSetHomeAsUpEnabled(boolean isEnabled);
    void onViewAtmOnMap(Atm atm);
    boolean onGetUpdateProgress();
    void onRefreshData();
    void onShowFab(int srcResId);
    void onHideFab();
}
