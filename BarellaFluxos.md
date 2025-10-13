- Rodada de dados e Avanço do Pino - Barella
```mermaid
flowchart LR;
    Turn -- calls --> Player -- Rolls --> Dice -- Advances --> Car;
    Car -- checks --> id([InPrison]);
    id([InPrison]) -- Does --> id2{SaidaDaPrisao};
    id([InPrison]) -- "Moves To" --> Space;
```

Falta a parte inicial (Turn calls Player)

Neste fluxo, a ideia é que o Controller chame o método **rollDice** e também o método <b>isInPrison</b>. Se este método retornar true, então o fluxo de **SaidaDaPrisão** e seus métodos próprios devem ser chamados. Se retornar false, o método **advancePosition** é chamado para avançar o Peão (Car).


- Avanço do Carro - Barella
```mermaid
flowchart LR;
    Car -- Lands --> Space -- Triggers --> id3{{Event}};
    id3{{Event}} --> id1{Automatic} -- passes --> Turn;
    id3{{Event}} --> id2{Choices} -- Asks --> Player;
    Player -- Responds --> id2{Choices} -- passes --> Turn;
```
Caso o jogador estivesse fora da prisão, ele andaria X espaços e cairia em um novo espaço. A depender do espaço em que caiu, ele poderá tomar uma decisão ou não. 
Casos em que ele não toma nenhuma decisão:

- Paga aluguel
- GoToPrison
- Paga de um Revés
- Recebe de uma Sorte

É claro que, caso ele não tenha dinheiro, poderá decidir? qual propriedade vender, entrando em um outro fluxo.


- Casas de Escolha (Construção) - Barella
```mermaid
flowchart LR
    id2([Type]) --> Company -- passes --> Turn;
    id2([Type]) --> Place -- checks --> id1([Buildings]);
    id1([Buildings]) -- passes --> Turn;
    id1([Buildings]) -- buys --> id4[(Hotel)];
    id1([Buildings]) -- buys --> id3[(House)]; 
```

FlowCharts específicos:
Rodada de dados e Avanço do Pino
```mermaid
flowchart TD
    subgraph View
        A["User clicks &quot;Roll Dice&quot; button"]
    end

    subgraph Controller
        B["Handles &quot;Roll Dice&quot; click event"]
        C["1. Gets current Player from Turn model"]
        D["2. Calls Dice.roll()"]
        E["5. Gets dice result from Dice model"]
        F["6. Tells View to display dice animation/result"]
        G["7. Calls Player.getCar().advancePosition(result)"]
        H["8. Tells View to update Car's visual position on board"]
        I((Triggers 'Car Advancement' Flow))
    end

    subgraph Model
        M_Dice[Dice]
        M_Car[Car]
        M_Player[Player]
        
        D_roll["roll()"]
        C_advance["advancePosition(steps)"]
        
        M_Dice -- contains --> D_roll
        M_Car -- contains --> C_advance
    end

    A --> B
    B --> C
    C --> D
    D --> M_Dice
    M_Dice -- "3. Generates random numbers & returns sum" --> E
    E --> F
    F --> G
    G -- "calls method on Car instance" --> M_Car
    M_Car -- "7a. Updates its internal 'position' variable" --> G
    G --> H
    H --> I
```


Avanço do Carro:
```mermaid
flowchart TD
    subgraph Controller
        A["Flow starts: Car has moved"]
        B["1. Gets new Space from Player.getCar().getPosition()"]
        C{"2. Determines type of Space"}
        
        D["Event: Automatic (Start, Tax, etc.)"]
        E["Event: Property"]
        F["Event: Go To Prison"]

        G["2a. Checks Property owner"]
    end

    subgraph Model
        M_Car[Car]
        M_Space[Space]
        M_Property[Property]
        M_Player[Player]
        
        C_pos["getPosition()"]
        P_owner["getOwner()"]
        
        M_Car -- owns --> C_pos
        M_Property -- owns --> P_owner
    end
    
    subgraph View
        V_Update["Update Player UI (e.g., money)"]
        V_BuyChoice["Display &quot;Buy Property?&quot; choice"]
    end

    A --> B
    B -- |calls| --> M_Car
    M_Car -- |returns Space object| --> B
    B --> C
    
    C -- "e.g., Start, Tax" --> D
    D -- "Triggers automatic action" --> V_Update

    C -- "is a Property" --> E
    E -- "calls property.getOwner()" --> M_Property
    M_Property -- "returns owner or null" --> G
    
    subgraph "Property Ownership Logic"
        direction LR
        G_Start("Check Owner")
        G_Start -- "Unowned" --> H["(Triggers Buy/Auction Flow)"] --> V_BuyChoice
        G_Start -- "Owned" --> I{"Is owner the<br/>current player?"}
        I -- "No" --> J["(Triggers Rent Payment Flow)"]
        I -- "Yes" --> K["(Triggers Construction Flow<br/>or ends turn)"]
    end

    G --> G_Start

    C -- "is GoToPrison space" --> F
    F -- "(Triggers Go To Prison Flow)" --> EndPrison((End))

```

Casas de Escolha (Construção):

```mermaid
flowchart TD
    subgraph Controller
        A["Start: Player lands on an owned Property"]
        B["1. Gets Property object from Car's position"]
        C{"2. Checks if Property is a 'Place' (property instanceof Place)"}
        
        D["3. If yes, queries Model for build options"]
        E{"place.canBuildHouse()?<br/>place.canBuildHotel()?<br/>player.getMoney() >= place.getHousePrice()?"}
        F["4. Based on results, tells View which buttons to enable/disable"]
        G["Handles &quot;Build House&quot; click"]
        H["5. Calls player.pay(house_price)"]
        I["6. Calls place.buildHouse()"]
        J["8. Tells View to update"]
    end

    subgraph View
        V_Enable["Enable/Disable &quot;Build House&quot;, &quot;Build Hotel&quot; buttons"]
        V_Click["User clicks &quot;Build House&quot;"]
        V_Update["Update UI:<br/>- Redraw property with new house<br/>- Update player's money display"]
        V_NoOptions["Show no build options"]
    end

    subgraph Model
        M_Place[Place]
        M_Player[Player]
        
        Pl_canBuild["canBuildHouse() / getHousePrice()"]
        Pl_build["buildHouse()"]
        P_pay["pay(amount)"]
        
        M_Place -- contains --> Pl_canBuild
        M_Place -- contains --> Pl_build
        M_Player -- contains --> P_pay
    end

    A --> B
    B --> C
    
    C -- "No (it's a Company)" --> V_NoOptions

    C -- "Yes (it's a Place)" --> D
    D -- |calls...| --> M_Place
    M_Place -- |...returns info| --> E
    E --> F
    F --> V_Enable
    V_Enable --> V_Click
    V_Click --> G
    G --> H
    H -- |calls...| --> M_Player
    M_Player -- |7. Player's money is reduced| --> I
    I -- |calls...| --> M_Place
    M_Place -- "7a. House count increments,<br/>rent is updated" --> I
    I --> J
    J --> V_Update



```