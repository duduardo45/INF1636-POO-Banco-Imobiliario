package model.core.entities;

import java.util.List;

import model.core.entities.cards.LuckCard;
import model.core.entities.spaces.Property;

public class Player {
    private Car my_car;
    private Money held;
    private List<Property> owned;
    private List<LuckCard> cards_in_hand;

    public void payTo(Player recipient, Money amount) {
        if (this.held.value > amount.value) {
            recipient.held.value += amount.value;
            held.value -= amount.value;
            return;
        }
    }
}