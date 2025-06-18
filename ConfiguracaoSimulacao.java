package view;

public class ConfiguracaoSimulacao {
    public static final int[] CAPACIDADES_CAMINHOES_PEQUENOS = {2000, 4000, 8000, 10000}; // em kg

    public static final int MAX_VIAGENS_POR_DIA = 8;
    public static final int TEMPO_VIAGEM_MIN_PICO = 30; // minutos
    public static final int TEMPO_VIAGEM_MAX_PICO = 60;
    public static final int TEMPO_VIAGEM_MIN_NORMAL = 20;
    public static final int TEMPO_VIAGEM_MAX_NORMAL = 40;

    public static final int[][] GERACAO_LIXO_ZONAS = {
            {500, 1000},   // Zona Sul
            {400, 800},    // Zona Norte
            {600, 1200},   // Zona Centro
            {300, 700},    // Zona Leste
            {450, 900}     // Zona Sudeste
    };

    public static final int TEMPO_MAXIMO_ESPERA_ESTACAO = 5; // turnos
    public static final int TOLERANCIA_CAMINHAO_GRANDE = 3; // turnos

    public static final int NUM_CAMINHOES_2T = 2;
    public static final int NUM_CAMINHOES_4T = 2;
    public static final int NUM_CAMINHOES_8T = 1;
    public static final int NUM_CAMINHOES_10T = 1;
}
