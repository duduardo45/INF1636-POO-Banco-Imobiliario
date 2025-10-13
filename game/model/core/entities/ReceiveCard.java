package model.core.entities;

class ReceiveCard extends LuckCard {
    private final int value;
    
    public ReceiveCard(String story, int value) {
    	super(LuckType.LUCKY, story);
    	this.value = value;
    }
}
