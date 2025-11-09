package model.core.entities;

class FreeParking extends Space {
    FreeParking(String name, Space next) {
        super(name, next);
    }
    
    @Override
    public void event(Player player) {
        // Não faz nada
    }
}
