package view;

import view.estruturas.*;
import view.caminhoes.*;

public class Simulador {
    private Lista<Zona> zonas;
    private Lista<CaminhaoPequeno> caminhoesPequenos;
    private Lista<EstacaoTransferencia> estacoes;
    private int turnoAtual;
    private int totalLixoColetado;
    private int totalDescarregamentos;
    private double tempoMedioEspera;
    private StringBuilder log;

    public Simulador() {
        inicializarSimulacao();
    }

    private void inicializarSimulacao() {
        zonas = new Lista<>();
        caminhoesPequenos = new Lista<>();
        estacoes = new Lista<>();
        turnoAtual = 0;
        totalLixoColetado = 0;
        totalDescarregamentos = 0;
        tempoMedioEspera = 0.0;
        log = new StringBuilder();

        criarZonas();
        criarCaminhoesPequenos();
        criarEstacoes();

        log.append("=== SIMULADOR DE COLETA DE LIXO DE TERESINA ===\n");
        log.append("Simulação inicializada com:\n");
        log.append("- ").append(zonas.tamanho()).append(" zonas\n");
        log.append("- ").append(caminhoesPequenos.tamanho()).append(" caminhões pequenos\n");
        log.append("- ").append(estacoes.tamanho()).append(" estações de transferência\n\n");
    }

    private void criarZonas() {
        String[] nomesZonas = {"Sul", "Norte", "Centro", "Leste", "Sudeste"};

        for (int i = 0; i < nomesZonas.length; i++) {
            int[] geracao = ConfiguracaoSimulacao.GERACAO_LIXO_ZONAS[i];
            Zona zona = new Zona(nomesZonas[i], geracao[0], geracao[1]);
            zonas.adicionar(zona);
        }
    }

    private void criarCaminhoesPequenos() {
        int id = 1;

        for (int i = 0; i < ConfiguracaoSimulacao.NUM_CAMINHOES_2T; i++) {
            caminhoesPequenos.adicionar(new CaminhaoPequeno(id++, 2000,
                    ConfiguracaoSimulacao.MAX_VIAGENS_POR_DIA));
        }

        for (int i = 0; i < ConfiguracaoSimulacao.NUM_CAMINHOES_4T; i++) {
            caminhoesPequenos.adicionar(new CaminhaoPequeno(id++, 4000,
                    ConfiguracaoSimulacao.MAX_VIAGENS_POR_DIA));
        }

        for (int i = 0; i < ConfiguracaoSimulacao.NUM_CAMINHOES_8T; i++) {
            caminhoesPequenos.adicionar(new CaminhaoPequeno(id++, 8000,
                    ConfiguracaoSimulacao.MAX_VIAGENS_POR_DIA));
        }

        for (int i = 0; i < ConfiguracaoSimulacao.NUM_CAMINHOES_10T; i++) {
            caminhoesPequenos.adicionar(new CaminhaoPequeno(id++, 10000,
                    ConfiguracaoSimulacao.MAX_VIAGENS_POR_DIA));
        }
    }

    private void criarEstacoes() {
        estacoes.adicionar(new EstacaoTransferencia(1,
                ConfiguracaoSimulacao.TEMPO_MAXIMO_ESPERA_ESTACAO));
        estacoes.adicionar(new EstacaoTransferencia(2,
                ConfiguracaoSimulacao.TEMPO_MAXIMO_ESPERA_ESTACAO));
    }

    public void executarSimulacao(int numeroTurnos) {
        log.append("Iniciando simulação de ").append(numeroTurnos).append(" turnos...\n\n");

        for (int turno = 1; turno <= numeroTurnos; turno++) {
            turnoAtual = turno;
            executarTurno();
        }

        calcularEstatisticasFinais();
        gerarRelatorioFinal();
    }

    private void executarTurno() {
        log.append("=== TURNO ").append(turnoAtual).append(" ===\n");
        gerarLixoNasZonas();
        executarColeta();
        processarEstacoes();
        log.append("\n");
    }

    private void gerarLixoNasZonas() {
        for (int i = 0; i < zonas.tamanho(); i++) {
            Zona zona = zonas.obter(i);
            int lixoAnterior = zona.getLixoDisponivel();
            zona.gerarLixo();
            int lixoGerado = zona.getLixoDisponivel() - lixoAnterior;

            log.append("Zona ").append(zona.getNome())
                    .append(": +").append(lixoGerado).append("kg de lixo gerado ")
                    .append("(total: ").append(zona.getLixoDisponivel()).append("kg)\n");
        }
    }

    private void executarColeta() {
        for (int i = 0; i < caminhoesPequenos.tamanho(); i++) {
            CaminhaoPequeno caminhao = caminhoesPequenos.obter(i);

            if (caminhao.podeTrabalhar() && !caminhao.estaLotado() &&
                    caminhao.getStatus().equals("Disponível")) {

                realizarColeta(caminhao);
            }

            if (caminhao.estaLotado() && !caminhao.getStatus().equals("Na fila da estação")) {
                enviarParaEstacao(caminhao);
            }
        }
    }

    private void realizarColeta(CaminhaoPequeno caminhao) {
        Zona melhorZona = null;
        int maiorQuantidadeLixo = 0;

        for (int i = 0; i < zonas.tamanho(); i++) {
            Zona zona = zonas.obter(i);
            if (zona.getLixoDisponivel() > maiorQuantidadeLixo) {
                maiorQuantidadeLixo = zona.getLixoDisponivel();
                melhorZona = zona;
            }
        }

        if (melhorZona != null && maiorQuantidadeLixo > 0) {
            int capacidadeDisponivel = caminhao.getCapacidadeMaxima() - caminhao.getCargaAtual();
            int lixoColetado = melhorZona.coletarLixo(capacidadeDisponivel);

            caminhao.setCargaAtual(caminhao.getCargaAtual() + lixoColetado);
            caminhao.setZonaAtual(melhorZona.getNome());
            caminhao.realizarViagem();

            totalLixoColetado += lixoColetado;

            log.append("Caminhão ").append(caminhao.getId())
                    .append(" coletou ").append(lixoColetado).append("kg na zona ")
                    .append(melhorZona.getNome())
                    .append(" (carga atual: ").append(caminhao.getCargaAtual()).append("kg)\n");
        }
    }

    private void enviarParaEstacao(CaminhaoPequeno caminhao) {
        EstacaoTransferencia melhorEstacao = estacoes.obter(0);
        for (int i = 1; i < estacoes.tamanho(); i++) {
            EstacaoTransferencia estacao = estacoes.obter(i);
            if (estacao.getTamanhoFila() < melhorEstacao.getTamanhoFila()) {
                melhorEstacao = estacao;
            }
        }

        melhorEstacao.adicionarCaminhaoParaDescarregar(caminhao);

        log.append("Caminhão ").append(caminhao.getId())
                .append(" enviado para Estação ").append(melhorEstacao.getId())
                .append(" (fila: ").append(melhorEstacao.getTamanhoFila()).append(")\n");
    }

    private void processarEstacoes() {
        for (int i = 0; i < estacoes.tamanho(); i++) {
            EstacaoTransferencia estacao = estacoes.obter(i);
            int descarregamentosAntes = estacao.getTotalDescarregamentos();

            estacao.processarDescarregamento();

            int novosDescarregamentos = estacao.getTotalDescarregamentos() - descarregamentosAntes;
            totalDescarregamentos += novosDescarregamentos;

            if (novosDescarregamentos > 0) {
                log.append("Estação ").append(estacao.getId())
                        .append(": ").append(novosDescarregamentos).append(" descarregamento(s)\n");
            }
        }
    }

    private void calcularEstatisticasFinais() {
        int totalTempoEspera = 0;
        int totalCaminhoes = 0;

        for (int i = 0; i < caminhoesPequenos.tamanho(); i++) {
            CaminhaoPequeno caminhao = caminhoesPequenos.obter(i);
            totalTempoEspera += caminhao.getTempoEspera();
            totalCaminhoes++;
        }

        tempoMedioEspera = totalCaminhoes > 0 ? (double) totalTempoEspera / totalCaminhoes : 0.0;
    }

    private void gerarRelatorioFinal() {
        log.append("\n=== RELATÓRIO FINAL DA SIMULAÇÃO ===\n");
        log.append("Turnos executados: ").append(turnoAtual).append("\n");
        log.append("Total de lixo coletado: ").append(totalLixoColetado).append("kg\n");
        log.append("Total de descarregamentos: ").append(totalDescarregamentos).append("\n");
        log.append("Tempo médio de espera: ").append(String.format("%.2f", tempoMedioEspera)).append(" turnos\n");

        int totalCaminhoesGrandes = 0;
        for (int i = 0; i < estacoes.tamanho(); i++) {
            EstacaoTransferencia estacao = estacoes.obter(i);
            int numCaminhoes = estacao.getNumCaminhoesGrandes();
            totalCaminhoesGrandes += numCaminhoes;

            log.append("Estação ").append(estacao.getId())
                    .append(": ").append(numCaminhoes).append(" caminhão(ões) grande(s)\n");
        }

        log.append("Total de caminhões grandes necessários: ").append(totalCaminhoesGrandes).append("\n");

        log.append("\n=== LIXO RESTANTE NAS ZONAS ===\n");
        for (int i = 0; i < zonas.tamanho(); i++) {
            Zona zona = zonas.obter(i);
            log.append("Zona ").append(zona.getNome())
                    .append(": ").append(zona.getLixoDisponivel()).append("kg\n");
        }
        log.append("\n=== RESPOSTA À PERGUNTA PRINCIPAL ===\n");
        log.append("Quantos caminhões de 20 toneladas no mínimo o município deverá possuir?\n");
        log.append("RESPOSTA: ").append(totalCaminhoesGrandes).append(" caminhões de 20 toneladas\n");
        log.append("Esta quantidade foi determinada pela simulação baseada na demanda real de coleta.\n");
    }

    public int getTurnoAtual() { return turnoAtual; }
    public int getTotalLixoColetado() { return totalLixoColetado; }
    public int getTotalDescarregamentos() { return totalDescarregamentos; }
    public double getTempoMedioEspera() { return tempoMedioEspera; }
    public String getLog() { return log.toString(); }
    public Lista<Zona> getZonas() { return zonas; }
    public Lista<CaminhaoPequeno> getCaminhoesPequenos() { return caminhoesPequenos; }
    public Lista<EstacaoTransferencia> getEstacoes() { return estacoes; }

    public int getTotalCaminhoesGrandes() {
        int total = 0;
        for (int i = 0; i < estacoes.tamanho(); i++) {
            total += estacoes.obter(i).getNumCaminhoesGrandes();
        }
        return total;
    }
}
