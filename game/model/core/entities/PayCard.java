package model.core.entities;

class PayCard extends LuckCard {
    private final int value;
    
    public PayCard(int value, String story) {
        super(LuckType.MISFORTUNE, story);
        this.value = value;
    }

}
