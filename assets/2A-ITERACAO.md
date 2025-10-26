# Instruções para a 2ª iteração — INF1636

*Transcrição em Markdown das orientações originais.* 

**Trabalho de INF1636**
**Data:** 13/10/2025
**Professor:** Ivan Mathias Filho

---

## Objetivo

A 2ª iteração tem como objetivo iniciar a implementação de uma interface gráfica por meio da qual se possa jogar **Banco Imobiliário**. Nem todas as funcionalidades precisam ser acionadas via interface gráfica nesta etapa. **Salvamento** e **recuperação** de partidas serão implementados a partir da 3ª iteração.

**Resultado esperado:** uma versão executável do jogo, com algumas limitações. 

---

## Interface Gráfica

* **Tecnologias obrigatórias:** componentes **Java Swing** e **Java2D**.
* **Exibição obrigatória via Java2D:** **tabuleiro**, **piões** e **cartas** devem ser renderizados pela API Java2D usando `Graphics2D.drawImage()`.
* **Não será aceito:** exibir imagens dos elementos de jogo usando componentes Swing como `JPanel`, `JButton`, `JLabel` etc.
* **Recursos de imagem:** arquivos com imagens foram disponibilizados na página da disciplina no EAD.
* **Dimensões máximas das janelas:** **1280 (largura) × 800 (altura)** pixels. 

---

## Janela Inicial

* Permitir **(a)** inclusão dos dados dos jogadores (3 a 6) de uma **nova partida** ou **(b)** continuação de partida salva (esta opção será tratada a partir da 3ª iteração).
* Após a escolha, **fechar** a janela inicial e **abrir** a janela do tabuleiro.
* Dados de jogador: **identificador** (string de 1 a 8 caracteres alfanuméricos) e **cor do pião**.
* O sistema deve **impedir cores repetidas** de piões.
* **Na 2ª iteração:** implementar **apenas** a **definição do número de jogadores**. 

---

## O Tabuleiro

* O tabuleiro **NÃO** pode conter **nenhum** elemento Java Swing.
* Deve ser construído **apenas** com `fill()`, `draw()` e `drawImage()` de `Graphics2D`.
* **É proibido** inserir componentes Swing em `JFrame`/`JPanel` para facilitar a construção do tabuleiro.
* **Exceções únicas:**

  * `JPopupMenu`, `JMenu` ou `JButton` para **salvar/carregar** jogo.
  * `JButton` para **disparar a simulação** do lançamento dos dados. 

---

## As Jogadas

* **Regra geral:** tudo que **não** exigir intervenção do jogador deve ser feito **automaticamente** pelo programa.
* Indicar claramente a **cor do jogador da vez** (mensagem no tabuleiro ou uso de cores). Exemplo: pintar a área dos dados com a cor do jogador vigente (vide **Figura 1** no enunciado original).
* **Evitar** caixas de diálogo (`JDialog`, `JOptionPane`, etc.) para mensagens simples, pois exigem intervenção e **atrapalham o fluxo**.
* O resultado do lançamento dos dados deve ser exibido por **figuras** representando os números (as **seis** figuras estão no EAD).
* **Para testes:** já na 2ª iteração, deve existir uma forma de **definir manualmente** os valores dos dados (ex.: **dois combo boxes** com inteiros **1 a 6**). 

---

## Deslocamento dos Piões

* **Sugestão:** criar **6 pistas imaginárias** cobrindo todo o perímetro do tabuleiro.
* Benefício: evita algoritmo especial para posicionar piões quando **dois ou mais** ocuparem a **mesma casa**. 

---

## Deck de Cartas de Sorte/Revés

* O **deck** não precisa ser exibido junto ao tabuleiro (economia de espaço).
* A **carta recebida** deve ser **exibida a todos** (no painel do tabuleiro ou em uma caixa de diálogo).
* **Arquitetura MVC:**

  * As **imagens/textos** das cartas pertencem à **View**.
  * O **Model** representa o **significado** de cada carta.
  * Desejável: cada carta do Model ter um **identificador** para permitir à View recuperar a imagem em **tempo constante** (ex.: chave em `HashMap`). 

---

## Informações Sobre o Estado do Jogo

### Propriedades

Ao posicionar um pião em uma **propriedade**, exibir:

* A **carta** relativa à propriedade.
* Se houver proprietário, a **cor** dele.
* **Preço de compra** da propriedade.
* Se for **terreno**, o **número de casas** e **hotéis** construídos. 

### Propriedades e Finanças do Jogador

* Exibir (e **atualizar**) os dados do **jogador da vez** durante sua jogada.
* Local de exibição: no **painel do tabuleiro** ou em **janela** dedicada.
* Sugestão no painel:

  * **Dinheiro**: como texto simples.
  * **Lista de propriedades**: em uma **combo box**; ao selecionar, exibir os **detalhes** no painel ou em outra janela. 

---

## Operações do Jogador da Vez

* Durante a jogada, o jogador pode **construir casa/hotel** e **vender propriedades** ao banco.
* Criar **diálogos específicos** para essas operações.
* Usar **`JButton`** ou **menus** para ativá-las (sugestões; a implementação fica livre).
* Ao realizar **débito/crédito**, informar:

  * **Montante** debitado/creditado do jogador da vez.
  * **Quem recebeu** o crédito/débito.
  * **Saldo** de quem recebeu o crédito/débito.
* Como o jogador pode executar **várias operações** por jogada, pode ser necessário um **`JButton`** (ou similar) para **encerrar** a jogada e **passar a vez**. 

---

## Funcionalidades Obrigatórias (2ª iteração)

Implementar via interface gráfica:

* Exibição da **janela inicial**.
* **Definição dos jogadores** da partida.
* Exibição do **tabuleiro**.
* **Sorteio** da **ordem** dos jogadores.
* **Lançamento dos dados** e **movimentação** dos piões.
* Exibição das **cartas** com dados das **propriedades**. 

---

## Design e Implementação

* Será avaliada a aplicação correta de técnicas de **design** e **programação** vistas no curso:

  * **Acoplamento** e **coesão** adequados.
  * **Organização** do aplicativo em **pacotes**.
  * Uso **OBRIGATÓRIO** dos **Design Patterns**:

    * **Observer**
    * **Façade**
    * **Singleton**
* **Proibição de acesso direto** a elementos gráficos (View) a partir de métodos no **Model**.
* A **atualização** das janelas para refletir o resultado da jogada deve ser feita **OBRIGATORIAMENTE** por meio do **Observer**.
* O **Controller** é responsável por:

  * Abertura de **`JOptionPane`** para mensagens relevantes da rodada.
  * Abertura de **`JFileChooser`** para **salvar** o estado do jogo. 

---

## Observação Final

As instruções desta iteração apresentam uma **visão global** da arquitetura a ser implementada, **sem compromisso** de que tudo seja plenamente atendido **na 2ª iteração**. 
