package view;

import estruturas.Lista;
import view.estruturas.Zona;
import view.caminhoes.CaminhaoPequeno;
import view.estruturas.EstacaoTransferencia;
import model.grafo.Vertice;
import model.grafo.Grafo;
import model.grafo.Dijkstra;

import java.util.Random;

public class Simulador {
    private Lista<Zona> zonas;
    private Lista<CaminhaoPequeno> caminhoesPequenos;
    private Lista<EstacaoTransferencia> estacoes;
    private Grafo grafo;
    private Lista<Vertice> vertices;
    private Lista<String> nomes;
    private int turno;
    private int proximoIdPequeno = 1;
    private Random random;
    private int caminhõesGrandesUsados = 0;
    private int totalTempoEspera = 0;
    private int totalDescarregamentos = 0;

    public Simulador() {
        zonas = new Lista<>();
        caminhoesPequenos = new Lista<>();
        estacoes = new Lista<>();
        grafo = new Grafo();
        vertices = new Lista<>();
        nomes = new Lista<>();
        random = new Random();
        turno = 0;

        inicializarZonas();
        inicializarGrafo();
        inicializarEstacoes();
        inicializarCaminhoesPequenos(5);
    }

    private void inicializarZonas() {
        zonas.adicionar(new Zona("A", 500, 1200));
        zonas.adicionar(new Zona("B", 600, 1000));
        zonas.adicionar(new Zona("C", 300, 900));
        zonas.adicionar(new Zona("D", 700, 1300));
        zonas.adicionar(new Zona("E", 400, 1100));
    }

    private void inicializarGrafo() {
        for (int i = 0; i < zonas.tamanho(); i++) {
            Zona z = zonas.get(i);
            Vertice v = new Vertice(z.getNome());
            grafo.adicionarVertice(v);
            vertices.adicionar(v);
            nomes.adicionar(z.getNome());
        }

        grafo.adicionarAresta("A", "B", 2);
        grafo.adicionarAresta("B", "A", 2);
        grafo.adicionarAresta("A", "C", 5);
        grafo.adicionarAresta("C", "A", 5);
        grafo.adicionarAresta("B", "C", 1);
        grafo.adicionarAresta("C", "B", 1);
        grafo.adicionarAresta("B", "D", 4);
        grafo.adicionarAresta("D", "B", 4);
        grafo.adicionarAresta("C", "E", 3);
        grafo.adicionarAresta("E", "C", 3);
        grafo.adicionarAresta("D", "E", 2);
        grafo.adicionarAresta("E", "D", 2);
    }

    private void inicializarEstacoes() {
        estacoes.adicionar(new EstacaoTransferencia(1));
        estacoes.adicionar(new EstacaoTransferencia(2));
    }

    private void inicializarCaminhoesPequenos(int quantidade) {
        int[] capacidades = {2000, 4000, 8000, 10000};
        for (int i = 0; i < quantidade; i++) {
            int capacidade = capacidades[random.nextInt(capacidades.length)];
            caminhoesPequenos.adicionar(new CaminhaoPequeno(proximoIdPequeno++, capacidade, 5));
        }
    }

    public Lista<String> avancarTurno(Simulador controller) {
        Lista<String> log = new Lista<>();
        turno++;

        for (int i = 0; i < caminhoesPequenos.tamanho(); i++) {
            CaminhaoPequeno caminhao = caminhoesPequenos.get(i);
            if (!caminhao.podeViajar()) continue;

            // Escolhe uma origem aleatória (nome de uma zona)
            int origemIndex = random.nextInt(zonas.tamanho());
            String origemNome = zonas.get(origemIndex).getNome();

            Zona zonaEscolhida = escolherZonaMaisProxima(origemNome);
            int lixo = zonaEscolhida.gerarLixo();

            caminhao.carregar(lixo);
            log.adicionar("Turno " + turno + ": " + caminhao.getTipo() + " " + caminhao.getId() +
                    " coletou " + lixo + "kg na zona " + zonaEscolhida.getNome());

            if (caminhao.estaCheio()) {
                EstacaoTransferencia est = estacoes.get(random.nextInt(estacoes.tamanho()));
                est.adicionarCaminhaoPequeno(caminhao);
                log.adicionar("Caminhão " + caminhao.getId() + " enviado para Estação " + est.getId());
            }
        }

        for (int i = 0; i < estacoes.tamanho(); i++) {
            estacoes.get(i).processar(controller);
        }

        return log;
    }

    private Zona escolherZonaMaisProxima(String origem) {
        Dijkstra dijkstra = new Dijkstra(grafo, nomes, vertices);
        return dijkstra.zonaMaisProxima(origem, zonas);
    }

    public Lista<String> getStatusCaminhoes() {
        Lista<String> status = new Lista<>();
        for (int i = 0; i < caminhoesPequenos.tamanho(); i++) {
            status.adicionar(caminhoesPequenos.get(i).toString());
        }
        return status;
    }

    public int getTurno() {
        return turno;
    }

    public Lista<Zona> getZonas() {
        return zonas;
    }

    public Lista<EstacaoTransferencia> getEstacoes() {
        return estacoes;
    }

    public void registrarNovoCaminhaoGrande() {
        caminhõesGrandesUsados++;
    }

    public void registrarTempoEspera(int tempo) {
        totalTempoEspera += tempo;
    }

    public void registrarDescarregamento() {
        totalDescarregamentos++;
    }

    public String gerarResumoEstatisticas() {
        StringBuilder sb = new StringBuilder("=== ESTATÍSTICAS FINAIS ===\n");
        sb.append("Turnos executados: ").append(turno).append("\n");
        sb.append("Caminhões grandes utilizados: ").append(caminhõesGrandesUsados).append("\n");
        sb.append("Total de descarregamentos: ").append(totalDescarregamentos).append("\n");
        if (totalDescarregamentos > 0) {
            double mediaEspera = (double) totalTempoEspera / totalDescarregamentos;
            sb.append("Tempo médio de espera: ").append(String.format("%.2f", mediaEspera)).append(" turnos\n");
        } else {
            sb.append("Tempo médio de espera: N/A\n");
        }
        return sb.toString();
    }
}
