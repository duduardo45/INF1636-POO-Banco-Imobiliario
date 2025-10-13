package model.core.entities;

class Company extends Property {
    private final int base_rent;
    
    public Company(String name, Space next, int cost, int base_rent) {
    	super(name, next, cost);
    	this.base_rent = base_rent;
    }
}
