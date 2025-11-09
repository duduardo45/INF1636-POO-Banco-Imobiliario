package model.core.entities;

abstract class Space {
    // ... Space class content ...
    protected final String name;
    private Space next;
    
    public Space(String name, Space next) { 
    	this.name = name; 
    	this.next = next;
    }
    
    public String getName() { return this.name; }
    public Space getNext() { return this.next; }
    public abstract String event(Player player);
    public void setNext(Space next) { 
    	this.next = next;
    }
}
