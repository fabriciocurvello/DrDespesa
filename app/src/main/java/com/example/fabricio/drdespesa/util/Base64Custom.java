package com.example.fabricio.drdespesa.util;

import android.util.Base64;

public class Base64Custom {

    //Recebe um String retorna criptografia Base64 String
    //replaceAll substitui um caratcter por outro. Neste caso está removendo espaços antes e depois, substituindo por nada
    public static String codificarBase64(String texto) {
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    //Recebe um String criptografada em Base64 e retorna um String descriptografada
    public static String decodificarBase64(String textoCodificado) {
        return new String( Base64.decode(textoCodificado, Base64.DEFAULT) );
    }
}
