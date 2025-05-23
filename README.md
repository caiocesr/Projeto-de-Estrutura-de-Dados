**Simulador de Coleta de Lixo**

Este projeto é um simulador completo de coleta de lixo com controle de turnos, caminhões, estações de transferência e lógica de rotas via grafo e algoritmo de Dijkstra.  

---

> Funcionalidades

- *Caminhões pequenos* com diferentes capacidades (2, 4, 8 e 10 toneladas) e limite de viagens por dia.
- *Caminhões grandes* com capacidade de 20 toneladas, tolerância de espera e descarregamento automático.
- *Geração aleatória de lixo* em zonas geográficas.
- *Estação de transferência* com fila de descarregamento.
- *Simulação controlada por turnos* ou automática com timer.
- *Painel gráfico* exibindo zonas, conexões e a zona atual de coleta.
- *Log de eventos em tempo real* via interface gráfica (Swing).
- *Grafo com Dijkstra* para determinar a melhor zona para coleta com base em distância.
- *Estatísticas finais* com:
  - Turnos executados
  - Caminhões grandes acionados
  - Tempo médio de espera
  - Total de lixo coletado

---

> Estrutura de Pacotes

```
src/
├── caminhoes/
    ├── Caminhao.java
    ├── CaminhaoGrande.java
    └── CaminhaoPequeno.java
├── estruturas/
│   ├── EstacaoTransferencia.java
    ├── Fila.java
│   ├── Lista.java
│   ├── No.java
    └── Zona.java
├── model/
│   └── grafo/
│       ├── Vertice.java
│       ├── Aresta.java
│       ├── Grafo.java
│       ├── RegistroAresta.java
│       └── Dijkstra.java
├── view/
│   ├── Main.java
│   ├── MainView.java
│   ├── MapaZonasPanel.java
    └── SimulacaoController.java
```
---


Este projeto *não utiliza estruturas de dados prontas do Java* como "ArrayList", "Map", "Set", "HashMap", etc., de acordo com o que foi exigido nas especificações.

As bibliotecas a seguir foram utilizadas para construção da interface gráfica e controle de eventos:

### `javax.swing.*`
> Usada para construir a interface gráfica do usuário (GUI).  
> Contém componentes como:
- `JFrame` – janela principal
- `JButton` – botões de clique
- `JTextArea` – área de texto
- `JScrollPane` – barra de rolagem para o log

### `java.awt.*`
> Biblioteca para componentes gráficos e layouts.
- `BorderLayout`, `BoxLayout`, `GridLayout`
- `Font`, `Color`, `Graphics2D`

### `java.awt.event.*`
> Usada para lidar com **eventos de usuário**, como cliques em botões.
- `ActionListener`, `ActionEvent`

### `java.util.Random`
> Único utilitário usado da `java.util`, apenas para geração de números aleatórios.  
> Não seria algo a se fazer com estrutura de dados, por isso a utilização.

---

> Exemplo de Saída (GUI)

```
🟢 Simulação iniciada...
Turno 1: Caminhão Pequeno 1 coletou 800kg na zona B
Turno 1: Caminhão 1 enviado para Estação 1
Turno 2: Caminhão Pequeno 2 coletou 600kg na zona C
...
=== ESTATÍSTICAS FINAIS ===
Turnos executados: 10
Caminhões grandes utilizados: 3
Total de descarregamentos: 8
Tempo médio de espera: 1.75 turnos
```

---

> Observação

Este projeto foi construído visando representar uma simulação de coleta de lixo a partir de caminhões com tamanhos variados na cidade de Teresina de maneira simples e objetiva.