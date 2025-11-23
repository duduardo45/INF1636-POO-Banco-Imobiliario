# Implementação Save/Load - Grupo 7

## Resumo da Implementação

Esta implementação adiciona funcionalidade completa de salvamento e carregamento de partidas ao Banco Imobiliário, conforme especificado no **Grupo 7** da lista-tarefas.md (linhas 57-66).

## ✅ Requisitos Atendidos

Todos os itens do Grupo 7 foram implementados:

- ✅ Implementar Salvamento de partida
  - ✅ Usar `JFileChooser`
  - ✅ Formato `.txt` puro (UTF-8)
  - ✅ Só permitir salvar antes de rolar os dados
- ✅ Implementar Carregamento de partida (parser do `.txt` para o `GameState`)
- ✅ Descrever (em um arquivo .txt) o formato usado no txt para o Ivan conseguir editar na mão
- ✅ Adicionar botão específico para sair do jogo (salvando o progresso)

## 📁 Arquivos Criados

### Documentação
1. **SAVE_FORMAT.txt** - Especificação completa do formato de arquivo
   - Descrição detalhada de todas as seções
   - Exemplos práticos
   - Guia para edição manual
   - Referência de índices do tabuleiro

2. **TESTE_SAVE_LOAD.txt** - Plano de testes completo
   - 12 cenários de teste
   - Instruções passo a passo
   - Resultados esperados

3. **IMPLEMENTACAO_SAVE_LOAD_RESUMO.md** - Este arquivo

### Código - Model
1. **game/model/core/entities/GameStateSaver.java**
   - Serializa estado do jogo para texto
   - Formato estruturado em seções
   - UTF-8 encoding
   - Validação de estado (não permite salvar após rolar dados)

2. **game/model/core/entities/GameStateLoader.java**
   - Parser completo do formato de salvamento
   - Reconstrói estado do jogo
   - Validações e tratamento de erros
   - Suporte a edição manual

### Código - Controller
Modificado: **game/controller/GameController.java**
- `saveGame(String filePath)` - Salva jogo
- `loadGame(String filePath)` - Carrega jogo
- `canSaveGame()` - Verifica se pode salvar
- `saveAndExit(String filePath)` - Salva e encerra

### Código - View
Modificado: **game/view/InitialFrame.java**
- Botão "Carregar Jogo"
- JFileChooser para seleção de arquivo
- Tratamento de erros
- Abre BoardFrame com jogo carregado

Modificado: **game/view/BoardFrame.java**
- Botão "Salvar e Sair"
- Diálogo de confirmação
- JFileChooser para salvar
- Botão desabilitado após rolar dados
- Tooltip explicativo

### Código - Model (Suporte)
Modificado: **game/model/core/entities/ModelFacade.java**
- Getters para serialização: `getBoard()`, `getBank()`, `getAllPlayers()`, etc.
- Setters para loading: `loadGameState()`, `setCurrentPlayerIndex()`, etc.

Modificado: **game/model/core/entities/Player.java**
- `setBalance()` - Define saldo diretamente
- `setPrisonState()` - Define estado de prisão
- `setConsecutiveDoubles()` - Define duplas consecutivas
- `addPropertyWithoutPayment()` - Adiciona propriedade sem debitar
- `addGetOutPrisonCards()` - Adiciona cartas

Modificado: **game/model/core/entities/Place.java**
- `setHouses()` - Define número de casas
- `setHotels()` - Define número de hotéis

## 🔄 Fluxo de Salvamento

```
1. Jogador clica "Salvar e Sair" (ANTES de rolar dados)
2. Sistema verifica se pode salvar (canSaveGame())
3. Mostra diálogo de confirmação
4. Jogador confirma
5. Abre JFileChooser para escolher local
6. GameStateSaver serializa estado para texto UTF-8
7. Arquivo é salvo
8. Mensagem de sucesso
9. Sistema encerra (System.exit(0))
```

## 🔄 Fluxo de Carregamento

```
1. Jogador abre InitialFrame
2. Clica em "Carregar Jogo"
3. Abre JFileChooser para escolher arquivo
4. GameStateLoader lê e valida arquivo
5. Parser reconstrói estado do jogo:
   - Cria tabuleiro padrão
   - Reconstrói jogadores com posições/saldos
   - Aplica propriedades e construções
   - Restaura flags de jogo
6. ModelFacade é configurado com estado carregado
7. BoardFrame abre com jogo carregado
8. Jogador continua partida
```

## 📋 Formato do Arquivo

O arquivo de salvamento é dividido em 3 seções:

### [GAME_STATE]
- CurrentPlayerIndex
- HasBuiltThisTurn
- DiceRolledThisTurn
- PropertyJustBought
- LastDiceRoll

### [PLAYERS]
Para cada jogador:
- Name, Color, Balance, Position
- InPrison, TurnsInPrison
- GetOutPrisonCards, ConsecutiveDoubles
- Properties (lista)

### [PROPERTIES]
Para cada propriedade:
- Name, BoardIndex
- Owner
- Houses, Hotels

## ⚙️ Características Técnicas

### Segurança
- **Salvamento bloqueado após rolar dados** - Garante consistência do estado
- **Validação de formato** - Parser detecta arquivos corrompidos
- **Tratamento de erros** - Mensagens claras para o usuário

### Usabilidade
- **JFileChooser** - Interface padrão do sistema
- **Filtro .txt** - Apenas arquivos de texto
- **Confirmação antes de sair** - Evita perda acidental
- **Feedback visual** - Botão desabilitado quando não pode salvar
- **Tooltips** - Explicam restrições

### Compatibilidade
- **UTF-8** - Suporta caracteres acentuados (São Paulo, José, etc.)
- **Formato texto puro** - Editável em qualquer editor
- **Multiplataforma** - Funciona em Windows, Mac, Linux

### Limitações Conhecidas
1. **Baralho de Sorte/Revés não é salvo**
   - É recriado e embaralhado ao carregar
   - Cartas na mão dos jogadores SÃO salvas

2. **Histórico de log não é salvo**
   - Inicia vazio ao carregar

## 🧪 Como Testar

Consulte **TESTE_SAVE_LOAD.txt** para plano completo de testes.

Teste rápido:
1. Inicie novo jogo (2 jogadores)
2. Não role dados
3. Clique "Salvar e Sair"
4. Confirme e salve como "teste.txt"
5. Reabra o jogo
6. Clique "Carregar Jogo"
7. Selecione "teste.txt"
8. Verifique que o jogo foi restaurado

## 📖 Para Edição Manual

Consulte **SAVE_FORMAT.txt** para:
- Formato detalhado de cada campo
- Exemplos completos
- Regras de validação
- Tabela de índices do tabuleiro
- Erros comuns e soluções

## 🎯 Filosofia de Implementação

Seguindo a instrução: **"Vamos tentar mudar o mínimo possível do que já está implementado no projeto, priorizando adicionar implementação do que alterar qualquer coisa."**

- ✅ Apenas **adicionamos** métodos às classes existentes
- ✅ Não alteramos comportamentos existentes
- ✅ Classes novas em pacotes apropriados
- ✅ Integração não-invasiva com código existente

## 🚀 Pronto para Uso

A implementação está completa e pronta para uso. Todos os requisitos do Grupo 7 foram atendidos conforme especificado na lista-tarefas.md.

