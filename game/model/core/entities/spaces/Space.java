package model.core.entities.spaces;

public abstract class Space {
    // ... conteúdo da classe Space ...
    protected final String name;
    public Space(String name) { this.name = name; }
    public String getName() { return this.name; }
    public abstract void event(Player player);
}
