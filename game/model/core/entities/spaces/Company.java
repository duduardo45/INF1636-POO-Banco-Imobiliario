package model.core.entities.spaces;

abstract class Company extends Property {
    private final int base_rent;
    
    public Company(int base_rent) {
    	super();
    	this.base_rent = base_rent;
    }
}
