package com.cdvdev.atmsearcher.listeners;

import com.cdvdev.atmsearcher.models.Atm;

/**
 * Listener for fragments callbacks
 */
public interface FragmentListener {
    void onAtmListItemSelected(Atm atm);
    void onChangeAppBarTitle(int res);
    void onSetHomeAsUpEnabled(boolean isEnabled);
    void onViewAtmOnMap(Atm atm);
}
