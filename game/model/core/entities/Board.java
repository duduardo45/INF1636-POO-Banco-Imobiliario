package model.core.entities;

import java.util.List;

class Board { 
    private List<Space> all_spaces;
    
    Board(List<Space> spaces) { 
        this.all_spaces = spaces;
    }
    
    Space getSpaceAt(int index) { 
        if (index >= 0 && index < all_spaces.size()) {
            return all_spaces.get(index);
        }
        return null;
    }
    
    int getTotalSpaces() { 
        return all_spaces.size();
    }
    
    int getPrisonPosition() { 
        // Prisão está na posição 10 (baseado no tabuleiro padrão)
        return 10;
    }
    
    List<Space> getAllSpaces() { 
        return all_spaces;
    }
}
