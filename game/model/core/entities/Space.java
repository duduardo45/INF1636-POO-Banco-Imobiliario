package model.core.entities;

public abstract class Space {
    // ... conteúdo da classe Space ...
    protected final String name;
    private final Space next;
    public Space(String name, Space next) { 
    	this.name = name; 
    	this.next = next;
    	}
    public String getName() { return this.name; }
    public Space getNext() { return this.next; }
    public abstract void event(Player player);
}
