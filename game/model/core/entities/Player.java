package game.model.core.entities;

public class Player {
    private Money held;

    public void payTo(Player recipient, Money amount) {
        if (this.held.value > amount.value) {
            recipient.held.value += amount.value;
            held.value -= amount.value;
            return;
        }
    }
}