package model.core.entities;

class GetOutPrisonCard extends LuckCard {
    private Player owner;
    
    public GetOutPrisonCard(String story, Player owner) {
    	super(LuckType.LUCKY, story);
    	
    	this.owner = owner;
    }
}
