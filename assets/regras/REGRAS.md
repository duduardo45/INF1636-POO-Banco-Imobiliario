# Banco Imobiliário — Regras para o Trabalho (INF1636)

> Fonte primária: manual impresso do jogo (imagens fornecidas pelo grupo).  
> Adaptações obrigatórias para o trabalho: “Regras Adicionais” do professor (prevalecem sobre o manual).

---

## Componentes (visão do manual)
- 32 **casas** plásticas  
- 12 **hotéis** plásticos  
- 28 **títulos de propriedades**  
- 30 **cartões Sorte/Revés**  
- 2 **dados**  
- 6 **piões**  
- **380 notas**  
- 1 **tabuleiro**

## Objetivo
Tornar-se o jogador mais rico por meio de compra, aluguel e venda de propriedades.

## Jogadores
- Participam de **2 a 6** jogadores. Cada um escolhe a cor do seu pião e o posiciona no ponto de partida.
- Embaralhe e posicione **Sorte/Revés** viradas para baixo no local indicado do tabuleiro.

## Banco
- Todo o dinheiro restante, bem como os títulos de propriedade ainda não comprados, ficam com o **banco**.  
- Recomenda-se uma pessoa como banqueiro (no manual). **(Vide adaptações abaixo: no trabalho, o software exerce esse papel.)**

## Dinheiro inicial (manual)
Cada jogador recebe: 8×$1, 10×$5, 10×$10, 10×$50, 8×$100 e 2×$500. O restante vai para o banco.

## Como jogar (manual)
1. O primeiro jogador **lança os dados** e avança seu pião pela **esquerda** o número de espaços indicados.  
2. Vários piões podem ocupar o mesmo espaço.  
3. **Ao cair** em um espaço, cumpra a instrução do espaço:
   - Pagar impostos,
   - Receber lucros,
   - Retirar e executar **Sorte/Revés**,
   - Pagar alugueis/taxas, etc.
4. **Duplas**: quem tirar duas faces iguais ganha **novo lançamento**; na **terceira dupla consecutiva**, vai para a **prisão**.

## Prisão (manual)
- Vai para a prisão ao **cair** no espaço “Vá para a Prisão” **ou** ao tirar **3 duplas seguidas**.  
- **Saída (manual)**: em até **3** jogadas seguintes, se tirar **dupla**, sai e avança; caso contrário, na **4ª jogada** paga **$500 ao banco** e sai; também sai com o cartão **“Saída Livre da Prisão”**.

## Ponto de Partida — Honorários (manual)
- Ao **passar** ou **parar** no **Ponto de Partida**, receba **$200** do banco como **honorários**.

## Terreno/Empresa com Dono (manual)
- Ao cair num terreno/empresa **com proprietário**, o jogador paga **aluguel/taxa** conforme o **título**.
- O dono deve **cobrar** antes de o adversário lançar os dados novamente; caso contrário, **perde o direito** (regra do manual).

## Construções (manual)
- Ao possuir **todo o grupo de propriedades** de uma mesma cor, o jogador pode **construir casas** e depois **hotel**.  
- Máximos do manual: até **4 casas** por propriedade; depois pode **construir 1 hotel**.  
- Distribuição: construir de forma **equilibrada** (não colocar a 2ª casa em um terreno antes de ter **1 casa** em cada terreno do grupo, etc.).  
- **Uma propriedade não pode ter 3 casas** se outra do mesmo grupo tiver **0**; etc.

## Trocas/Vendas entre Jogadores (manual)
- São permitidas **negociações** de terrenos e empresas (preços a combinar).

## Hipotecas (manual)
- Terrenos **sem construção** (ou com venda prévia das construções) e **empresas** podem ser hipotecados conforme valores dos títulos.  
- O **resgate** de hipoteca segue o valor especificado.

## Pagamentos (manual)
- **Sempre em dinheiro**.  
- Se não tiver como pagar, o jogador deve **vender casas/hotéis** pela **metade do preço pago**, **hipotecar** ou **vender propriedades** (inclusive em leilão).  

## Falência (manual)
- Se, mesmo após vender e hipotecar, **não conseguir pagar**, o jogador **vai à falência** e **deixa o jogo**.  
- O dinheiro obtido com as vendas/hipotecas vai ao **credor** (jogador/banco).  
- **Empréstimos** entre jogadores **são permitidos** no manual.

## Observação (manual)
- **Durante um jogo, nenhum jogador pode dar/emprestar dinheiro a outro** — (nota presente no verso; prevalecer a leitura do professor nas adaptações abaixo).

## Término do Jogo (manual)
- O jogo termina quando **restar apenas um jogador** (os demais faliram). Alternativamente, podem comparar patrimônios ao final pactuado.

---

# Adaptações Oficiais para o Trabalho (prevalecem sobre o manual)

> **Estas modificações são obrigatórias e substituem as regras originais onde houver conflito.** :contentReference[oaicite:0]{index=0}

## Representação/Interface
- **Não usar imagens** para casas/hotéis/notas; o programa apenas mantém contagens e saldos.  
- Dados, títulos, piões e cartões **podem** ser representados por imagens disponibilizadas pela disciplina. :contentReference[oaicite:1]{index=1}

## Jogadores e Saldos Iniciais
- Defina **2 a 6** jogadores **no frame inicial**.  
- Cada jogador começa com **$4.000** e o **banco** com **$200.000**. :contentReference[oaicite:2]{index=2}

## Banqueiro
- O papel de **banqueiro é do software** (sem pessoa humana). :contentReference[oaicite:3]{index=3}

## Ordem de Jogada
- **Dispensa sorteio**: ordem **fixa** associada às **cores dos piões**. :contentReference[oaicite:4]{index=4}

## Prisão (para o trabalho)
- **Sai da prisão** automaticamente se:
  1) **tirar dupla** em um dos lançamentos (até 3 tentativas), **ou**  
  2) **usar cartão “Saída Livre da Prisão”** (o cartão é **devolvido ao deck**).  
- **Não existe pagamento de multa** ao banqueiro nesta versão. :contentReference[oaicite:5]{index=5}

## Aluguel/Taxas
- Pagamento a **terreno/empresa com dono** é **automático** (débito do pagador e crédito do dono). :contentReference[oaicite:6]{index=6}

## Trocas e Vendas entre Jogadores
- **Não serão implementadas**. :contentReference[oaicite:7]{index=7}

## Construções (para o trabalho)
- Como não há trocas/vendas entre jogadores, **não é necessário possuir o grupo inteiro** para construir. Regras:
  - **1ª vez** que o jogador cai numa propriedade disponível: **pode comprá-la**.  
  - **Nas vezes seguintes** em que cair na **mesma propriedade**, **pode construir 1 imóvel por vez**.  
  - Para **construir um hotel**, a propriedade deve possuir **pelo menos uma casa**.  
  - Limites: até **4 casas** e **1 hotel** por propriedade. :contentReference[oaicite:8]{index=8}

## Hipotecas
- **Não serão implementadas**. :contentReference[oaicite:9]{index=9}

## Pagamentos Automáticos e Liquidez
- **Todos os pagamentos** (impostos, aluguéis/taxas, etc.) devem ser **automáticos**.  
- Se um jogador **precisar de dinheiro**, ele **vende uma propriedade ao banco** por **90% do valor** (terreno + casas + hotel). :contentReference[oaicite:10]{index=10}

## Falência (para o trabalho)
- **Empréstimos e doações** não existem.  
- Se o jogador **falir**, **sai do jogo** imediatamente. :contentReference[oaicite:11]{index=11}

## Término do Jogo (para o trabalho)
- O jogo termina **quando os jogadores decidirem** (não precisa “restar um”).  
- No encerramento, apure o **capital acumulado** de cada jogador e **classifique** as posições. :contentReference[oaicite:12]{index=12}

---

## Prioridade/Conflitos
Quando houver diferença entre o texto do manual e os itens desta seção de **Adaptações**, **valem as adaptações** definidas pelo professor para o trabalho. :contentReference[oaicite:13]{index=13}
