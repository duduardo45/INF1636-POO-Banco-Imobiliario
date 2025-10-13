package model.core.entities;

/**
 * Enum que representa os motivos pelos quais um jogador pode entrar na prisão.
 */
public enum PrisonReason {
    LANDING_ON_GO_TO_PRISON,  // Caiu na casa "Go to Prison"
    CARD_GO_TO_PRISON,        // Recebeu carta "GoToPrisonCard"
    THIRD_DOUBLE              // Tirou doubles pela 3ª vez consecutiva
}
