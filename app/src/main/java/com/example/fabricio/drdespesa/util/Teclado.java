package com.example.fabricio.drdespesa.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Teclado {

    public static void esconderTeclado(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
