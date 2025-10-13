package model.core.entities;

public class ReceiveFromOthersCard extends LuckCard {
    private final int value;
    
    public ReceiveFromOthersCard(String story, int value) {
    	super(LuckType.LUCKY, story);
    	
    	this.value = value;
    }
}
