package model.core.entities;

class FreeParking extends Space {
    FreeParking(String name, Space next) {
        super(name, next);
    }
    
    @Override
    public String event(Player player) {
        // Não faz nada
        return "";
    }
}
