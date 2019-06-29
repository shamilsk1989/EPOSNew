package com.alhikmahpro.www.e_pos.interfaces;

import android.os.Bundle;

public interface FragmentActionListener {

    int ACTION_VALUE_FRAGMENT_SELECTED = 0;
    String KEY_SELECTED_FRAGMENT="KEY_SELECTED_FRAGMENT";
    void onActionPerformed(Bundle bundle);
    void onBackActionPerformed();

}
