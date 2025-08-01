//package com.joaonardi.gerenciadorocupacional.model;
//
//public enum Vencimentos {
//    VENCIDO(0),
//    VENCE_SEMANA(),
//    VENCE_MES(),
//    VENCE_SEMESTRE();
//
//    private final int valor;
//
//    Vencimentos(int valor){
//        this.valor = valor;
//    }
//
//    public int getValor() {
//        return valor;
//    }
//    public static Vencimentos fromValor(int valor){
//        for (Vencimentos v : Vencimentos.values()){
//            if (v.getValor() == valor){
//                return v;
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public String toString() {
//       switch (this){
//           case VENCIDO -> { return  "Vencido";}
//           case VENCE_SEMANA -> {return }
//       }
//    }
//}
