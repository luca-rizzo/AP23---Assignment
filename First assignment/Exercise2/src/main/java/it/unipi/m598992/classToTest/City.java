package it.unipi.m598992.classToTest;

import it.unipi.m598992.xmlSerializer.annotations.XMLable;
import it.unipi.m598992.xmlSerializer.annotations.XMLfield;

@XMLable
public class City {

    @XMLfield(type = "String", name = "nameOfCity")
    private final String name;
    @XMLfield(type = "int")
    private final int numberOfCitizen;
    @XMLfield(type = "boolean", name = "isCapital")
    private final boolean capital;

    private final String notSerializedField;

    public City(String name, int numberOfCitizen, boolean capital, String notSerializedField) {
        this.name = name;
        this.numberOfCitizen = numberOfCitizen;
        this.capital = capital;
        this.notSerializedField = notSerializedField;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfCitizen() {
        return numberOfCitizen;
    }

    public boolean isCapital() {
        return capital;
    }

    public String getNotSerializedField() {
        return notSerializedField;
    }
}
