package model.core.entities.spaces;

import java.util.Map;

import model.core.entities.spaces.Property;

enum Building {
    HOUSE,
    HOTEL
}

abstract class Place extends Property {
    private final int base_rent;
    private final int house_price;
    private final int hotel_price;
    private final int hotel_rent;
    private final Map<int, int> house_rent;
    private Map<Building, int> buildings;
    private int current_rent;
}
