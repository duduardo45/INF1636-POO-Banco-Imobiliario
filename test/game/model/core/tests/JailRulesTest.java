package game.model.core.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import model.core.entities.Player;
import model.core.entities.Dice;
import model.core.entities.PrisonReason;
import model.core.entities.spaces.GoToPrison;
import model.core.entities.cards.GoToPrisonCard;
import model.core.entities.cards.GetOutPrisonCard;

/**
 * Testes para as regras de entrada e saída da prisão - Models Soltos.
 * Testa diretamente as classes de domínio sem controller.
 * 
 * Cenários testados:
 * 1. Entrada na prisão por espaço "Vá para a Prisão"
 * 2. Entrada na prisão por carta "Vá para a Prisão"
 * 3. Entrada na prisão por terceira dupla consecutiva
 * 4. Saída da prisão por dupla
 * 5. Saída da prisão por carta "Saída Livre"
 * 6. Contador de duplas consecutivas
 * 7. Contador de turnos na prisão
 * 8. Múltiplos jogadores na prisão
 * 9. Fluxo completo de entrada e saída
 */
public class JailRulesTest {

    private Player player1;
    private Player player2;
    private Dice dice;
    private GoToPrison goToPrisonSpace;
    private GoToPrisonCard goToPrisonCard;
    private GetOutPrisonCard getOutPrisonCard;

    @Before
    public void setUp() {
        player1 = new Player("Jogador 1", "Azul", 1500);
        player2 = new Player("Jogador 2", "Vermelho", 1500);
        dice = new Dice(12345L);
        goToPrisonSpace = new GoToPrison();
        goToPrisonCard = new GoToPrisonCard("Vá para a Prisão");
        getOutPrisonCard = new GetOutPrisonCard("Saída Livre da Prisão", player1);
    }

    // ===== TESTES DE ENTRADA NA PRISÃO =====

    @Test
    public void testEnterPrisonByGoToPrisonSpace() {
        // Arrange: Jogador cai no espaço "Vá para a Prisão"
        
        // Act: Executa a ação do espaço
        goToPrisonSpace.executeAction(player1);
        
        // Assert: Jogador deve estar na prisão
        assertTrue("Jogador deve estar na prisão", player1.isInPrison());
        assertEquals("Motivo deve ser LANDING_ON_GO_TO_PRISON", PrisonReason.LANDING_ON_GO_TO_PRISON, player1.getPrisonReason());
        assertEquals("Turnos na prisão deve ser 0", 0, player1.getPrisonTurns());
        assertEquals("Contador de duplas deve ser resetado", 0, player1.getConsecutiveDoubles());
    }

    @Test
    public void testEnterPrisonByCard() {
        // Arrange: Jogador recebe cartão "Vá para a Prisão"
        player1.addCard(goToPrisonCard);
        
        // Act: Jogador usa o cartão
        goToPrisonCard.executeAction(player1);
        
        // Assert: Jogador deve estar na prisão
        assertTrue("Jogador deve estar na prisão", player1.isInPrison());
        assertEquals("Motivo deve ser CARD_GO_TO_PRISON", PrisonReason.CARD_GO_TO_PRISON, player1.getPrisonReason());
        assertEquals("Turnos na prisão deve ser 0", 0, player1.getPrisonTurns());
        assertEquals("Contador de duplas deve ser resetado", 0, player1.getConsecutiveDoubles());
    }

    @Test
    public void testEnterPrisonByThirdDouble() {
        // Arrange: Jogador já tirou 2 duplas consecutivas
        player1.setConsecutiveDoubles(2);
        
        // Act: Jogador tira terceira dupla
        dice.rollTwo();
        if (dice.getLastDice1() == dice.getLastDice2()) {
            player1.enterPrison(PrisonReason.THIRD_DOUBLE);
        }
        
        // Assert: Jogador deve estar na prisão
        assertTrue("Jogador deve estar na prisão", player1.isInPrison());
        assertEquals("Motivo deve ser THIRD_DOUBLE", PrisonReason.THIRD_DOUBLE, player1.getPrisonReason());
        assertEquals("Turnos na prisão deve ser 0", 0, player1.getPrisonTurns());
        assertEquals("Contador de duplas deve ser resetado", 0, player1.getConsecutiveDoubles());
    }

    // ===== TESTES DE CONTADOR DE DUPLAS =====

    @Test
    public void testConsecutiveDoublesCounter() {
        // Arrange: Jogador sem duplas consecutivas
        assertEquals("Contador inicial deve ser 0", 0, player1.getConsecutiveDoubles());
        
        // Act: Jogador tira dupla
        dice.rollTwo();
        if (dice.getLastDice1() == dice.getLastDice2()) {
            player1.incrementConsecutiveDoubles();
        }
        
        // Assert: Contador deve ser 1
        assertEquals("Contador de duplas deve ser 1", 1, player1.getConsecutiveDoubles());
    }

    @Test
    public void testConsecutiveDoublesReset() {
        // Arrange: Jogador com 2 duplas consecutivas
        player1.setConsecutiveDoubles(2);
        assertEquals("Contador inicial deve ser 2", 2, player1.getConsecutiveDoubles());
        
        // Act: Jogador tira dupla (terceira) e vai para prisão
        dice.rollTwo();
        if (dice.getLastDice1() == dice.getLastDice2()) {
            player1.enterPrison(PrisonReason.THIRD_DOUBLE);
        }
        
        // Assert: Contador deve ser resetado
        assertEquals("Contador deve ser resetado", 0, player1.getConsecutiveDoubles());
    }

    // ===== TESTES DE SAÍDA DA PRISÃO =====

    @Test
    public void testLeavePrisonByDoubles() {
        // Arrange: Jogador na prisão
        player1.enterPrison(PrisonReason.LANDING_ON_GO_TO_PRISON);
        assertTrue("Jogador deve estar na prisão", player1.isInPrison());
        
        // Act: Jogador tira dupla
        dice.rollTwo();
        if (dice.getLastDice1() == dice.getLastDice2()) {
            player1.leavePrison();
        }
        
        // Assert: Jogador deve sair da prisão
        assertFalse("Jogador não deve estar na prisão", player1.isInPrison());
        assertNull("Motivo deve ser null", player1.getPrisonReason());
        assertEquals("Turnos devem ser resetados", 0, player1.getPrisonTurns());
    }

    @Test
    public void testStayInPrisonWhenNotDouble() {
        // Arrange: Jogador na prisão
        player1.enterPrison(PrisonReason.LANDING_ON_GO_TO_PRISON);
        
        // Act: Jogador tira dados que não são dupla
        dice.rollTwo();
        if (dice.getLastDice1() != dice.getLastDice2()) {
            player1.incrementPrisonTurns();
        }
        
        // Assert: Jogador deve continuar na prisão
        assertTrue("Jogador deve continuar na prisão", player1.isInPrison());
        assertEquals("Turnos na prisão deve ser 1", 1, player1.getPrisonTurns());
    }

    @Test
    public void testLeavePrisonByCard() {
        // Arrange: Jogador na prisão com carta de saída
        player1.enterPrison(PrisonReason.LANDING_ON_GO_TO_PRISON);
        player1.setHasGetOutOfPrisonCard(true);
        
        // Act: Jogador usa carta de saída
        getOutPrisonCard.executeAction(player1);
        
        // Assert: Jogador deve sair da prisão
        assertFalse("Jogador não deve estar na prisão", player1.isInPrison());
        assertFalse("Jogador não deve ter mais a carta", player1.hasGetOutOfPrisonCard());
        assertNull("Motivo deve ser null", player1.getPrisonReason());
        assertEquals("Turnos devem ser resetados", 0, player1.getPrisonTurns());
    }

    @Test
    public void testCannotUseCardWhenNotInPrison() {
        // Arrange: Jogador não está na prisão mas tem carta
        player1.setHasGetOutOfPrisonCard(true);
        
        // Act: Tenta usar carta
        boolean canUse = getOutPrisonCard.canUse(player1);
        
        // Assert: Não deve poder usar
        assertFalse("Não deve poder usar carta fora da prisão", canUse);
    }

    @Test
    public void testCannotUseCardWhenNoCard() {
        // Arrange: Jogador na prisão sem carta
        player1.enterPrison(PrisonReason.LANDING_ON_GO_TO_PRISON);
        
        // Act: Tenta usar carta
        boolean canUse = getOutPrisonCard.canUse(player1);
        
        // Assert: Não deve poder usar
        assertFalse("Não deve poder usar carta sem ter ela", canUse);
    }

    // ===== TESTES DE TURNOS NA PRISÃO =====

    @Test
    public void testPrisonTurnsIncrement() {
        // Arrange: Jogador na prisão
        player1.enterPrison(PrisonReason.LANDING_ON_GO_TO_PRISON);
        assertEquals("Turnos iniciais devem ser 0", 0, player1.getPrisonTurns());
        
        // Act: Jogador fica mais um turno
        player1.incrementPrisonTurns();
        
        // Assert: Turnos devem incrementar
        assertEquals("Turnos na prisão deve ser 1", 1, player1.getPrisonTurns());
    }

    @Test
    public void testPrisonTurnsResetOnExit() {
        // Arrange: Jogador na prisão com turnos
        player1.enterPrison(PrisonReason.LANDING_ON_GO_TO_PRISON);
        player1.incrementPrisonTurns();
        player1.incrementPrisonTurns();
        assertEquals("Deve ter 2 turnos", 2, player1.getPrisonTurns());
        
        // Act: Jogador sai da prisão
        player1.leavePrison();
        
        // Assert: Turnos devem ser resetados
        assertEquals("Turnos devem ser resetados", 0, player1.getPrisonTurns());
    }

    // ===== TESTES DE MÚLTIPLOS JOGADORES =====

    @Test
    public void testMultiplePlayersPrison() {
        // Arrange: Dois jogadores
        
        // Act: Um entra na prisão
        player1.enterPrison(PrisonReason.LANDING_ON_GO_TO_PRISON);
        
        // Assert: Apenas um deve estar na prisão
        assertTrue("Jogador 1 deve estar na prisão", player1.isInPrison());
        assertFalse("Jogador 2 não deve estar na prisão", player2.isInPrison());
    }

    // ===== TESTES DE FLUXO COMPLETO =====

    @Test
    public void testCompletePrisonFlow() {
        // Arrange: Jogador normal
        assertFalse("Jogador não deve estar na prisão inicialmente", player1.isInPrison());
        
        // Act: Entra na prisão
        player1.enterPrison(PrisonReason.LANDING_ON_GO_TO_PRISON);
        assertTrue("Deve estar na prisão", player1.isInPrison());
        assertEquals("Motivo deve ser LANDING_ON_GO_TO_PRISON", PrisonReason.LANDING_ON_GO_TO_PRISON, player1.getPrisonReason());
        
        // Act: Fica 2 turnos
        player1.incrementPrisonTurns();
        player1.incrementPrisonTurns();
        assertEquals("Deve ter 2 turnos", 2, player1.getPrisonTurns());
        
        // Act: Sai da prisão
        player1.leavePrison();
        
        // Assert: Deve estar livre
        assertFalse("Não deve estar na prisão", player1.isInPrison());
        assertNull("Motivo deve ser null", player1.getPrisonReason());
        assertEquals("Turnos devem ser resetados", 0, player1.getPrisonTurns());
    }

    @Test
    public void testPrisonReasonTracking() {
        // Arrange: Jogador normal
        
        // Act: Entra por diferentes motivos
        player1.enterPrison(PrisonReason.LANDING_ON_GO_TO_PRISON);
        assertEquals("Motivo deve ser LANDING_ON_GO_TO_PRISON", PrisonReason.LANDING_ON_GO_TO_PRISON, player1.getPrisonReason());
        
        player1.leavePrison();
        player1.enterPrison(PrisonReason.CARD_GO_TO_PRISON);
        assertEquals("Motivo deve ser CARD_GO_TO_PRISON", PrisonReason.CARD_GO_TO_PRISON, player1.getPrisonReason());
        
        player1.leavePrison();
        player1.enterPrison(PrisonReason.THIRD_DOUBLE);
        assertEquals("Motivo deve ser THIRD_DOUBLE", PrisonReason.THIRD_DOUBLE, player1.getPrisonReason());
    }

    // ===== TESTES DE CARTAS =====

    @Test
    public void testAddAndRemoveCards() {
        // Arrange: Jogador sem cartas
        assertFalse("Jogador não deve ter carta inicialmente", player1.hasCard(goToPrisonCard));
        
        // Act: Adiciona carta
        player1.addCard(goToPrisonCard);
        
        // Assert: Deve ter a carta
        assertTrue("Jogador deve ter a carta", player1.hasCard(goToPrisonCard));
        
        // Act: Remove carta
        player1.removeCard(goToPrisonCard);
        
        // Assert: Não deve ter a carta
        assertFalse("Jogador não deve ter a carta", player1.hasCard(goToPrisonCard));
    }

    @Test
    public void testGetOutPrisonCardApplyEffect() {
        // Arrange: Jogador na prisão com carta
        player1.enterPrison(PrisonReason.LANDING_ON_GO_TO_PRISON);
        player1.setHasGetOutOfPrisonCard(true);
        
        // Act: Aplica efeito da carta
        boolean result = getOutPrisonCard.applyEffect();
        
        // Assert: Deve funcionar
        assertTrue("Aplicação deve ser bem-sucedida", result);
        assertFalse("Jogador não deve estar na prisão", player1.isInPrison());
        assertFalse("Jogador não deve ter mais a carta", player1.hasGetOutOfPrisonCard());
    }

    // ===== TESTES DE DADOS =====

    @Test
    public void testDiceRolling() {
        // Arrange: Dados
        
        // Act: Rola dois dados
        int[] result = dice.rollTwo();
        
        // Assert: Deve retornar array de 2 elementos
        assertEquals("Deve retornar array de 2 elementos", 2, result.length);
        assertTrue("Primeiro dado entre 1 e 6", result[0] >= 1 && result[0] <= 6);
        assertTrue("Segundo dado entre 1 e 6", result[1] >= 1 && result[1] <= 6);
        
        // Assert: Métodos de leitura devem funcionar
        assertEquals("getLastDice1 deve retornar primeiro valor", result[0], dice.getLastDice1());
        assertEquals("getLastDice2 deve retornar segundo valor", result[1], dice.getLastDice2());
        assertEquals("getLastDiceSum deve retornar soma", result[0] + result[1], dice.getLastDiceSum());
    }

    @Test
    public void testDiceDoubleDetection() {
        // Arrange: Dados
        
        // Act: Rola dados até conseguir dupla
        boolean isDouble = false;
        int attempts = 0;
        while (!isDouble && attempts < 100) {
            dice.rollTwo();
            isDouble = dice.rollDouble();
            attempts++;
        }
        
        // Assert: Se conseguiu dupla, deve ser válida
        if (isDouble) {
            assertEquals("Dados devem ser iguais", dice.getLastDice1(), dice.getLastDice2());
        }
    }
}