package model.core.entities.spaces;

public abstract class Space {
    protected String name;
    private Space next;
    
    public Space(String name) {
        this.name = name;
    }
    
    abstract public void event();
    
    public Space getNext() {
    	return next;
    }
    
    public String getName() {
        return name;
    }
}
