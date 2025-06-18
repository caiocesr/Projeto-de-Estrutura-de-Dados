package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimuladorGUI extends JFrame {
    private Simulador simulador;
    private JTextArea areaLog;
    private JButton btnIniciar;
    private JButton btnProximoTurno;
    private JButton btnSimulacaoCompleta;
    private JButton btnReiniciar;
    private JPanel painelEstatisticas;
    private JLabel lblTurno;
    private JLabel lblLixoColetado;
    private JLabel lblCaminhoesGrandes;
    private JLabel lblTempoMedioEspera;
    private boolean simulacaoIniciada;

    public SimuladorGUI() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        simulador = new Simulador();
        simulacaoIniciada = false;
        atualizarInterface();
    }

    private void initializeComponents() {
        setTitle("Simulador de Coleta de Lixo - Teresina");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaLog.setBackground(Color.BLACK);
        areaLog.setForeground(Color.GREEN);

        btnIniciar = new JButton("Iniciar Simulação");
        btnProximoTurno = new JButton("Próximo Turno");
        btnSimulacaoCompleta = new JButton("Simulação Completa (20 turnos)");
        btnReiniciar = new JButton("Reiniciar");

        lblTurno = new JLabel("Turno: 0");
        lblLixoColetado = new JLabel("Lixo Coletado: 0 kg");
        lblCaminhoesGrandes = new JLabel("Caminhões Grandes: 0");
        lblTempoMedioEspera = new JLabel("Tempo Médio Espera: 0.00");

        painelEstatisticas = new JPanel(new GridLayout(2, 2, 10, 10));
        painelEstatisticas.setBorder(BorderFactory.createTitledBorder("Estatísticas em Tempo Real"));
        painelEstatisticas.add(lblTurno);
        painelEstatisticas.add(lblLixoColetado);
        painelEstatisticas.add(lblCaminhoesGrandes);
        painelEstatisticas.add(lblTempoMedioEspera);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel painelSuperior = new JPanel(new FlowLayout());
        painelSuperior.add(btnIniciar);
        painelSuperior.add(btnProximoTurno);
        painelSuperior.add(btnSimulacaoCompleta);
        painelSuperior.add(btnReiniciar);

        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Log da Simulação"));

        JPanel painelLateral = new JPanel(new BorderLayout());
        painelLateral.add(painelEstatisticas, BorderLayout.NORTH);
        painelLateral.add(criarPainelInformacoes(), BorderLayout.CENTER);

        add(painelSuperior, BorderLayout.NORTH);
        add(scrollLog, BorderLayout.CENTER);
        add(painelLateral, BorderLayout.EAST);
    }

    private JPanel criarPainelInformacoes() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createTitledBorder("Informações do Sistema"));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setBackground(getBackground());
        infoArea.setText(
                "CONFIGURAÇÃO DA SIMULAÇÃO:\n\n" +
                        "Zonas: 5 (Sul, Norte, Centro, Leste, Sudeste)\n\n" +
                        "Caminhões Pequenos:\n" +
                        "- 2x de 2 toneladas\n" +
                        "- 2x de 4 toneladas\n" +
                        "- 1x de 8 toneladas\n" +
                        "- 1x de 10 toneladas\n\n" +
                        "Estações: 2\n" +
                        "Caminhões Grandes: 20 toneladas\n\n" +
                        "Máx. viagens/dia: 8\n" +
                        "Tempo máx. espera: 5 turnos\n\n" +
                        "OBJETIVO:\n" +
                        "Determinar o número mínimo\n" +
                        "de caminhões de 20 toneladas\n" +
                        "necessários para atender\n" +
                        "a demanda de Teresina."
        );

        painel.add(new JScrollPane(infoArea));
        return painel;
    }

    private void setupEventHandlers() {
        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarSimulacao();
            }
        });

        btnProximoTurno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executarProximoTurno();
            }
        });

        btnSimulacaoCompleta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executarSimulacaoCompleta();
            }
        });

        btnReiniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciarSimulacao();
            }
        });
    }

    private void iniciarSimulacao() {
        simulador = new Simulador();
        simulacaoIniciada = true;
        areaLog.setText(simulador.getLog());
        atualizarInterface();

        JOptionPane.showMessageDialog(this,
                "Simulação iniciada!\nUse 'Próximo Turno' para avançar passo a passo\n" +
                        "ou 'Simulação Completa' para executar 20 turnos automaticamente.",
                "Simulação Iniciada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void executarProximoTurno() {
        if (!simulacaoIniciada) {
            JOptionPane.showMessageDialog(this,
                    "Inicie a simulação primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Executa apenas 1 turno
        simulador.executarSimulacao(simulador.getTurnoAtual() + 1);
        areaLog.setText(simulador.getLog());
        atualizarInterface();

        // Auto-scroll para o final
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    private void executarSimulacaoCompleta() {
        if (!simulacaoIniciada) {
            iniciarSimulacao();
        }

        // Executa simulação completa de 20 turnos
        simulador.executarSimulacao(20);
        areaLog.setText(simulador.getLog());
        atualizarInterface();

        // Auto-scroll para o final
        areaLog.setCaretPosition(areaLog.getDocument().getLength());

        // Mostra resultado final
        JOptionPane.showMessageDialog(this,
                "Simulação completa!\n\n" +
                        "Resultado: " + simulador.getTotalCaminhoesGrandes() +
                        " caminhões de 20 toneladas necessários\n" +
                        "Total de lixo coletado: " + simulador.getTotalLixoColetado() + " kg\n" +
                        "Tempo médio de espera: " + String.format("%.2f", simulador.getTempoMedioEspera()) + " turnos",
                "Simulação Finalizada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void reiniciarSimulacao() {
        simulador = new Simulador();
        simulacaoIniciada = false;
        areaLog.setText("");
        atualizarInterface();

        JOptionPane.showMessageDialog(this,
                "Simulação reiniciada! Clique em 'Iniciar Simulação' para começar.",
                "Reiniciado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void atualizarInterface() {
        btnIniciar.setEnabled(!simulacaoIniciada);
        btnProximoTurno.setEnabled(simulacaoIniciada);
        btnSimulacaoCompleta.setEnabled(true);
        btnReiniciar.setEnabled(true);

        if (simulacaoIniciada) {
            lblTurno.setText("Turno: " + simulador.getTurnoAtual());
            lblLixoColetado.setText("Lixo Coletado: " + simulador.getTotalLixoColetado() + " kg");
            lblCaminhoesGrandes.setText("Caminhões Grandes: " + simulador.getTotalCaminhoesGrandes());
            lblTempoMedioEspera.setText("Tempo Médio Espera: " +
                    String.format("%.2f", simulador.getTempoMedioEspera()) + " turnos");
        } else {
            lblTurno.setText("Turno: 0");
            lblLixoColetado.setText("Lixo Coletado: 0 kg");
            lblCaminhoesGrandes.setText("Caminhões Grandes: 0");
            lblTempoMedioEspera.setText("Tempo Médio Espera: 0.00");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new SimuladorGUI().setVisible(true);
            }
        });
    }
}
