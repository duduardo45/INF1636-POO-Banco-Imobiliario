package controller;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * SaveFileManager - Gerencia localização e nomenclatura de arquivos de salvamento
 * 
 * Esta classe utilitária centraliza a lógica de:
 * - Criação e acesso à pasta de saves
 * - Geração de nomes de arquivo informativos
 * - Configuração de JFileChoosers para save/load
 */
public class SaveFileManager {
    private static final String SAVES_DIR = "partidas_salvas";
    
    /**
     * Retorna o diretório de saves, criando-o se não existir
     * 
     * @return File representando o diretório de saves
     */
    public static File getSavesDirectory() {
        File savesDir = new File(SAVES_DIR);
        
        // Criar diretório se não existir
        if (!savesDir.exists()) {
            savesDir.mkdirs();
        }
        
        return savesDir;
    }
    
    /**
     * Gera um nome de arquivo sugerido baseado nos jogadores e timestamp
     * 
     * Formato: partida_[jogador1]_[jogador2]_..._[data]_[hora].txt
     * Exemplo: partida_Joao_Maria_Jose_2025-11-23_14-30.txt
     * 
     * @param playerNames Lista de nomes dos jogadores
     * @return Nome de arquivo sugerido
     */
    public static String generateSaveFileName(List<String> playerNames) {
        StringBuilder fileName = new StringBuilder("partida");
        
        // Adicionar nomes dos jogadores (máximo 3 para não ficar muito longo)
        int maxPlayers = Math.min(3, playerNames.size());
        for (int i = 0; i < maxPlayers; i++) {
            String name = playerNames.get(i);
            // Remover espaços e caracteres especiais do nome
            name = name.replaceAll("[^a-zA-Z0-9]", "");
            fileName.append("_").append(name);
        }
        
        // Se houver mais de 3 jogadores, indicar
        if (playerNames.size() > 3) {
            fileName.append("_e").append(playerNames.size() - 3).append("mais");
        }
        
        // Adicionar timestamp (data e hora)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        String timestamp = dateFormat.format(new Date());
        fileName.append("_").append(timestamp);
        
        // Adicionar extensão
        fileName.append(".txt");
        
        return fileName.toString();
    }
    
    /**
     * Cria um JFileChooser configurado para salvar partidas
     * 
     * @param suggestedFileName Nome sugerido para o arquivo
     * @return JFileChooser configurado
     */
    public static JFileChooser createSaveChooser(String suggestedFileName) {
        JFileChooser fileChooser = new JFileChooser();
        
        // Configurar diretório inicial
        File savesDir = getSavesDirectory();
        fileChooser.setCurrentDirectory(savesDir);
        
        // Configurar título
        fileChooser.setDialogTitle("Salvar Jogo");
        
        // Configurar filtro de extensão
        FileNameExtensionFilter filter = 
            new FileNameExtensionFilter("Arquivos de Salvamento (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        
        // Configurar nome sugerido
        if (suggestedFileName != null && !suggestedFileName.isEmpty()) {
            fileChooser.setSelectedFile(new File(savesDir, suggestedFileName));
        }
        
        return fileChooser;
    }
    
    /**
     * Cria um JFileChooser configurado para salvar partidas, com um arquivo existente como padrão
     * Usado quando o jogo foi carregado e queremos sobrescrever o mesmo arquivo
     * 
     * @param existingFilePath Caminho completo do arquivo existente
     * @param fallbackFileName Nome alternativo se o arquivo não existir
     * @return JFileChooser configurado
     */
    public static JFileChooser createSaveChooserWithExistingFile(String existingFilePath, String fallbackFileName) {
        JFileChooser fileChooser = new JFileChooser();
        
        // Configurar título
        fileChooser.setDialogTitle("Salvar Jogo");
        
        // Configurar filtro de extensão
        FileNameExtensionFilter filter = 
            new FileNameExtensionFilter("Arquivos de Salvamento (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        
        // Se temos um arquivo existente, usar ele
        if (existingFilePath != null && !existingFilePath.isEmpty()) {
            File existingFile = new File(existingFilePath);
            if (existingFile.exists()) {
                fileChooser.setSelectedFile(existingFile);
                fileChooser.setCurrentDirectory(existingFile.getParentFile());
                return fileChooser;
            }
        }
        
        // Fallback: usar diretório de saves e nome sugerido
        File savesDir = getSavesDirectory();
        fileChooser.setCurrentDirectory(savesDir);
        if (fallbackFileName != null && !fallbackFileName.isEmpty()) {
            fileChooser.setSelectedFile(new File(savesDir, fallbackFileName));
        }
        
        return fileChooser;
    }
    
    /**
     * Cria um JFileChooser configurado para carregar partidas
     * 
     * @return JFileChooser configurado
     */
    public static JFileChooser createLoadChooser() {
        JFileChooser fileChooser = new JFileChooser();
        
        // Configurar diretório inicial
        File savesDir = getSavesDirectory();
        fileChooser.setCurrentDirectory(savesDir);
        
        // Configurar título
        fileChooser.setDialogTitle("Carregar Jogo Salvo");
        
        // Configurar filtro de extensão
        FileNameExtensionFilter filter = 
            new FileNameExtensionFilter("Arquivos de Salvamento (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        
        return fileChooser;
    }
    
    /**
     * Verifica se o diretório de saves existe e contém arquivos
     * 
     * @return true se existem saves salvos
     */
    public static boolean hasSavedGames() {
        File savesDir = new File(SAVES_DIR);
        if (!savesDir.exists() || !savesDir.isDirectory()) {
            return false;
        }
        
        File[] files = savesDir.listFiles((dir, name) -> name.endsWith(".txt"));
        return files != null && files.length > 0;
    }
}

