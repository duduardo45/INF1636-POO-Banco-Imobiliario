### 🛠️ Grupo 1: Arquitetura, Refatoração e Bugs Críticos
*Foco: Limpar o código e garantir que a base não quebre antes de adicionar coisas novas.*

- [ ] Mover lógica do `ModelFacade` para um `GameController` (Refatoração estrutural).
- [ ] Resolver erro de `allPlayers` (Bug crítico).
- [x] Criar a estrutura do objeto `GameState` (necessário para o salvamento futuro).
- [ ] Retornar/Exibir o motivo técnico pelo qual uma operação foi ou não possível (Feedback de erro).

### 🔄 Grupo 2: Fluxo de Turno e Controles
*Foco: Garantir que o jogador só possa fazer o que é permitido em cada momento.*

- [ ] Proibir o jogador de agir antes de rolar os dados.
- [ ] Bloquear teclas de comando em momentos indevidos (ex: `C`/`V` na casa de Parada Livre).  As hotkeys funcionam, mas mostram mensagens de erro. Isso não é suficiente?
- [ ] Fazer o turno passar automaticamente ao fazer uma escolha (quando aplicável). 
- [X] **Feature de Teste (Pedido do Ivan):** Permitir rolagem de dados escolhida pelo usuário manualmente. - Barella
- [X] Implementar Hotkey ou Botão principal para passar o turno.
- [ ] Eliminar de fato o jogador do ciclo de turnos quando ele for removido.
- [ ] Dar chance extra para usuário ao conseguir duplas (exceto na 3ª dupla, pois nesse caso ele vai para a prisão).

### 🏠 Grupo 3: Economia e Propriedades
*Foco: Compra, venda, aluguel e construção.*

- [X] Rever e ajustar preço de casas e hotéis.
- [X] Implementar a separação lógica entre Hotel e Casa.
- [X] Testar o fluxo de compra de hotéis. - Barella
- [X] Impedir venda e compra da mesma propriedade no mesmo turno (e vice-versa).
- [X] **Companhias:** Multiplicar o preço do aluguel pelo valor dos dados.
- [X] **Companhias:** Remover a opção de "Comprar Casa" para este tipo de propriedade. - As hotkeys funcionam, mas mostram mensagens de erro. Os botões não aparecem, como esperado.
- [ ] Checar se valor HONORARIO de $200 está sendo distribuido ao jogador quando ele passa pela casa de inicio após o começo do jogo. (ver manual)

### 💸 Grupo 4: Falência e Eliminação
*Foco: O que acontece quando o dinheiro acaba.*

- [X] Permitir escolha de qual propriedade vender em caso de falência (atualmente só tenta vender a atual). - Barella
- [ ] Bloquear o jogador de passar a vez se estiver em estado de falência.
- [ ] Proibir o jogador de apertar `E` (render-se) se ainda tiver dinheiro.

### 🎲 Grupo 5: Regras Especiais do Tabuleiro
*Foco: Prisão e Sorte/Revés.*

- [ ] Fazer a Prisão de fato prender o jogador (bloquear movimento/ações).
- [ ] Inicializar o `luckDeck` com as cartas reais.
- [ ] Mostrar na tela a carta de Sorte ou Revés que foi sorteada.

### 🖥️ Grupo 6: Interface (UI) e Feedback Visual
*Foco: Melhorar a experiência do usuário e visualização.*

- [X] Converter opções de escolha (hotkeys) em Botões clicáveis. - Barella(parcial: Compra de casas, hoteis e Compra e Venda de propriedades.)
- [ ] Terminar criação de botões (verificar botões faltantes)
- [ ] Mostrar visualmente quais propriedades são de qual jogador (ex: transparência colorida sobre a casa). 
- [ ] Adicionar texto no tabuleiro mostrando quantidade de casas e hotéis em cada território.
- [X] Criar um `BoardPanel` para histórico de acontecimentos.
- [X] Implementar notificações de eventos (quem pagou aluguel, quanto, etc.) no histórico.
- [ ] Garantir que o popup de "Fim de Jogo" apareça apenas uma vez.

### 💾 Grupo 7: Persistência (Save/Load) e Finalização
*Foco: Salvar o jogo e calcular o vencedor.*

- [ ] Adicionar Botão para terminar a partida manualmente e calcular vencedor (baseado no dinheiro).
- [ ] Implementar Salvamento de partida:
    - [ ] Usar `JFileChooser`.
    - [ ] Formato `.txt` puro (UTF-8).
    - [ ] Só permitir salvar antes de rolar os dados.
- [ ] Implementar Carregamento de partida (parser do `.txt` para o `GameState`).
- [ ] Adicionar botão específico para encerrar o jogo/sair.

