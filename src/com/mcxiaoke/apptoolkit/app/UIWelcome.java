package com.mcxiaoke.apptoolkit.app;

import android.content.Intent;
import android.os.Bundle;

public class UIWelcome extends UIBaseSupport {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, UIHome.class));
        finish();
    }
}
