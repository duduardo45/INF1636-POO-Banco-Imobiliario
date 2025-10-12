package game.model.core.spaces;

import java.util.Map;

import game.model.core.entities.Property;

private enum Building {
    House,
    Hotel
}

private class Place extends Property {
    private const int base_rent;
    private const int house_price;
    private const int hotel_price;
    private const int hotel_rent;
    private const Map<int, int> house_rent;
    private Map<Building, int> buildings;
    private int current_rent;
}
