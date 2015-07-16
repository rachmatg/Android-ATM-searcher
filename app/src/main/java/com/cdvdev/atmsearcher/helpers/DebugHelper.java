package com.cdvdev.atmsearcher.helpers;

import com.cdvdev.atmsearcher.models.Atm;

import java.util.ArrayList;

public class DebugHelper {

    /**
     * Method which generated list of ATMS
     * @return ArrayList
     */
    public static ArrayList<Atm> getDemoAtmList(){
        ArrayList<Atm> list = new ArrayList<>();
        Atm atm = null;

        for (int i = 0; i < 50; i++){
            atm = new Atm();
            atm.setName("ATM-" + i);
            list.add(atm);
        }

        return list;
    }
}
