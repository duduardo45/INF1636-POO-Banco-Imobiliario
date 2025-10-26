# Regras Adicionais — INF1636

*Transcrição em Markdown das orientações originais.* 

**Trabalho de INF1636**
**Data:** 20/04/2022
**Professor:** Ivan Mathias Filho

---

## Regras do Jogo

O programa deve implementar as regras do arquivo **Regras-Banco-Imobiliario.pdf** (disponível no EAD). Para adaptar o jogo físico à simulação em computador, aplicam-se as seguintes modificações:

### Componentes

* As **32 casas plásticas**, os **12 hotéis plásticos** e as **380 notas** **não** serão representadas por imagens.
* O programa deve **armazenar**:

  * o **montante de dinheiro** de cada jogador;
  * as **quantidades de casas e hotéis** existentes em cada propriedade.
* Serão representados por **imagens**: **dados**, **títulos de propriedade**, **piões** e **cartões de sorte/revés** (imagens já publicadas no EAD).

### Jogadores

* O **número de jogadores** (2 a 6) será definido no **frame inicial**.
* Cada jogador inicia com **$4.000** unidades monetárias; o banco inicia com **$200.000**.

### Banqueiro

* O papel de **banqueiro** é exercido pelo **software**.

### Começo do Jogo

* **Não é necessário** sortear a ordem dos jogadores.
* A ordem pode ser **fixa** e associada às **cores dos piões**.

### Prisão

* O jogador **sai da prisão automaticamente** se:

  * **obter dois números iguais** no lançamento dos dados; **ou**
  * **possuir o cartão de saída** livre da prisão.
* Ao usá-lo, o **cartão deve ser devolvido ao deck**.
* **Pagamento de multa** ao banqueiro **não será considerado**.

### Terreno ou Empresa com Dono

* **Pagamento de aluguel/taxa** deve ser **automático**.

### Trocas e Vendas entre Jogadores

* **Não serão implementadas**.

### Construções

> Como trocas e vendas entre jogadores não serão implementadas, ajustam-se as regras de construção:

* **Primeira vez** que um jogador cair em uma propriedade: **pode comprá-la**, se estiver disponível.
* **Vezes subsequentes** na **mesma propriedade**: **pode construir** **um único imóvel por vez**.
* Para **construir um hotel**, a propriedade deve possuir **pelo menos uma casa**.
* **Limites por propriedade:** até **4 casas** e **1 hotel**.

### Hipotecas

* **Não serão implementadas**.

### Pagamentos

* **Devem ser automáticos** (débito e crédito).
* Se um jogador **precisar de dinheiro**, deverá **vender uma propriedade ao banco**, recebendo **90% do valor** da propriedade (**valor do terreno + casas + hotéis**).

### Falência

* **Empréstimos** e **doações** **não serão implementados**.
* Jogador **falido sai do jogo**.

### Término do Jogo

* O jogo termina quando os **jogadores decidirem**.
* Nesse momento, apura-se o **capital acumulado** por jogador e define-se a **posição** de cada um.

---

## Observação

Caso, no desenvolvimento, sejam detectadas **dificuldades excessivas** para implementar alguma regra, este documento **poderá ser atualizado** listando as regras que ficarão de fora do trabalho. 
