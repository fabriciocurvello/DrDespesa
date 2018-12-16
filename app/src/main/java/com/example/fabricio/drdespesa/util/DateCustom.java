package com.example.fabricio.drdespesa.util;

import java.text.SimpleDateFormat;

public class DateCustom {

    //Retorna a data atual
    public static String dataAtual() {

        //Retorna a data atual representada em um número long
        long data = System.currentTimeMillis();

        //Defindo o padrão de Formatação da data para exibição
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        //Aplicando o padrão à data atual
        String dataString = simpleDateFormat.format(data);

        return dataString;
    }

    //Retorna o ano e mês da data escolhida pelo usuário
    public static String anoMesDataEscolhida(String data){

        String retornoData[] = data.split("/");
        String dia = retornoData[0];//dia 23
        String mes = retornoData[1];//mes 01
        String ano = retornoData[2];//ano 2018

        String anoMes = ano + mes;
        return anoMes;
    }

}
