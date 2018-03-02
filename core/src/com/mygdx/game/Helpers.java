package com.mygdx.game;

public class Helpers
{
    //denna klass innehåller hjälpmetoder som används av flera klasser
    
    public static double random(double min, double max)
    {
        return Math.floor(Math.random() * (max - min + 1) + min);
    }
}
