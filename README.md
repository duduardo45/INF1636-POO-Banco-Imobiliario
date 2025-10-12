# INF1636 - Programação Orientada a Objetos - Banco Imobiliário

Este repositório guarda o código do trabalho de POO sobre o tema Banco Imobiliário
feito pelo grupo de integrantes:
 - 2310822 - Eduardo Eugênio de Souza
 - 2310540 - Pedro Carneiro Nogueira
 - 2311203 - Pedro Nogueira Barella

## Funcionalidades necessárias para o programa
- Permitir até 6 jogadores
- Lançamento de dados
- distribuir cartas
- movimentar peões no tabuleiro
- decidir o vencedor

## Diagrama de Classes para o trabalho

```mermaid
graph
    id(Building) --> id2[(House)];
    id(Building) --> id3[(Hotel)];
    Money;
    Bank;
    Board ---> Space;
    Space --> Property;
    Space --> Start;
    Space --> Prison;
    Space --> GoToPrison;
    Space --> Chance;
    Space --> Tax;
    Space --> Profit;
    Property --> Company;
    Property --> Place;
    Player;
    Car;
    Dice;
    Card --> ReceiveCard;
    Card --> ReceiveFromOthersCard;
    Card --> PayCard;
    Card --> GetOutPrisonCard;
    Card --> GoToPrisonCard;
    Turn;
```
## Modelagem de Classes
Leia sobre a interpretação do diagrama aqui: [mermaid ER diagrams](https://mermaid.js.org/syntax/entityRelationshipDiagram.html)
```mermaid
erDiagram
    Player ||..o{ Money : has
    Player ||--|| Car : uses
    Bank ||..o{ Money : has
    Board ||--|{ Space : contains
    Car }o..|| Space : "is in"
    Space ||--|| Property : is
    Player |o..o{ Property : owns
    Property ||--o{ Building : has
```
## Ações possíveis
- Rodada de dados e Avanço do Pino
```mermaid
flowchart LR;
    Turn -- calls --> Player -- Rolls --> Dice -- Advances --> Car;
    Car -- checks --> id([InPrison]);
    id([InPrison]) -- Does --> id2{SaidaDaPrisao};
    id([InPrison]) -- "Moves To" --> Space;
```
- Entrada na Prisão
```mermaid
flowchart LR;
Player -- "Lands in" --> GotoPrison --> i{{EnterPrison}};
Player -- "Rolls" --> Dice -- "Equals for the 3rd time" --> i{{EnterPrison}};
i{{EnterPrison}} -- Move --> Car -- to --> Prison;
Player -- Receives --> GoToPrisonCard --> i{{EnterPrison}};
```

- Saída da Prisão
```mermaid
flowchart LR;
Prison -- Rolls --> Dice;
Dice -- Equals --> id1{{LeaveAndAvance}} -- Advance --> Car;
Dice -- "Not Equal" --> id2{{Remain}};

Prison -- "Has" --> GetOutPrisonCard --> id3{{Leave}};
```
- Avanço do Carro
```mermaid
flowchart LR;
    Car -- Lands --> Space -- Triggers --> id3{{Event}};
    id3{{Event}} --> id1{Automatic} -- passes --> Turn;
    id3{{Event}} --> id2{Choices} -- Asks --> Player;
    Player -- Responds --> id2{Choices} -- passes --> Turn;
```

- Casas de Escolha (Propriedade)
```mermaid
flowchart TD;
    Property -- calls --> id3{{Event}} -- checks --> id1([Available]);
    id1([Available]) -- asks --> Player;
    Player -- buys --> Property;
    Player -- does --> t{{Nothing}};
    id1([Available]) -- checks --> Owner;
    Owner -- other --> id5{Pagamento de Aluguel};
    Owner -- myself --> id6{Construção};
```

- Casas de Escolha (Construção)
```mermaid
flowchart LR
    id2([Type]) --> Company -- passes --> Turn;
    id2([Type]) --> Place -- checks --> id1([Buildings]);
    id1([Buildings]) -- passes --> Turn;
    id1([Buildings]) -- buys --> id4[(Hotel)];
    id1([Buildings]) -- buys --> id3[(House)]; 
```

- Casas de Escolha (Pagamento de Aluguel)
```mermaid
flowchart LR;
    id2([Type]) --> Company -- Rolls --> Dice -- "passes amount" --> id4{Pagamento entre Jogadores};
    id2([Type]) --> Place-- "checks amount of" --> Building -- "passes amount" --> id4{Pagamento entre Jogadores};
```
- Casas de Escolha (Pagamento entre Jogadores)
```mermaid
flowchart TD;
    Payer -- checks --> id1([Amount]); 
    id1([Amount]) -- "has enough" --> id2{{pays}} --> Owner;
    id1([Amount]) -- "checks" --> id4[/OwnedProperties/];
    id4[/OwnedProperties/] -- "has" --> id3{{sell}};
    id3{{sell}} -- "checks" --> id1([Amount]);
    id4[/OwnedProperties/] -- "doesn't have" --> id5{{Bankrupt}};
```