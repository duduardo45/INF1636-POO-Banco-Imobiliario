package model.core.entities.spaces;

abstract class Company extends Property {
    private final int base_rent;
    
    public Company(String name, int cost, int base_rent) {
    	super(name, cost);
    	this.base_rent = base_rent;
    }
}
