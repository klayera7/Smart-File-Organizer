import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FileOrganizerApp {

    // ── i18n ──────────────────────────────────────────────────────────────────
    private static final String[][] LABELS = {
        // EN, PT
        {"File Organizer",              "Organizador de Arquivos"},
        {"Start",                       "Iniciar"},
        {"Stop",                        "Parar"},
        {"Idle",                        "Parado"},
        {"Running",                     "Executando"},
        {"Watching: ~/Downloads",       "Monitorando: ~/Downloads"},
        {"Activity log",                "Log de atividade"},
        {"Clear",                       "Limpar"},
        {"Minimizes to tray on close",  "Minimiza para bandeja ao fechar"},
        {"Ready.",                      "Pronto."},
        {"Watcher started.",            "Monitor iniciado."},
        {"Watcher stopped.",            "Monitor parado."},
    };
    private int langIdx = 0; // 0=EN, 1=PT

    private String lbl(int i) { return LABELS[i][langIdx]; }

    // ── estado ────────────────────────────────────────────────────────────────
    private WatcherService watcherService;
    private Thread watcherThread;

    // ── componentes ───────────────────────────────────────────────────────────
    private JFrame frame;
    private JLabel lblStatus, lblSub;
    private JButton btnStart, btnStop, btnClear;
    private JToggleButton btnEN, btnPT;
    private JTextArea logArea;
    private JLabel lblTray;
    private TrayIcon trayIcon;

    // ── cores (tema claro; adapte se quiser dark) ─────────────────────────────
    private static final Color BG       = new Color(0x1E1E1E);
    private static final Color BG2      = new Color(0x2A2A2A);
    private static final Color BORDER   = new Color(0x3D3D3D);
    private static final Color TEXT     = new Color(0xF0EFEB);
    private static final Color MUTED    = new Color(0x888780);
    private static final Color GREEN    = new Color(0x3DD68C);
    private static final Color AMBER    = new Color(0xF0A500);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileOrganizerApp().init());
    }

    private void init() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        frame = new JFrame(lbl(0));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(440, 520);
        frame.setMinimumSize(new Dimension(380, 460));
        frame.setLocationRelativeTo(null);
        frame.setBackground(BG);

        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                if (trayIcon != null) {
                    frame.setVisible(false);
                } else {
                    confirmExit();
                }
            }
        });

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildTitleBar(), BorderLayout.NORTH);
        root.add(buildBody(),     BorderLayout.CENTER);

        frame.setContentPane(root);
        setupTray();
        frame.setVisible(true);
    }

    // ── barra de título ───────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG2);
        bar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(8, 14, 8, 14)
        ));

        JLabel title = new JLabel(lbl(0));
        title.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        title.setForeground(MUTED);
        title.setHorizontalAlignment(SwingConstants.LEFT);

        // toggle idioma
        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        langPanel.setOpaque(false);
        btnEN = styledToggle("EN");
        btnPT = styledToggle("PT");
        btnEN.setSelected(true);
        btnEN.addActionListener(e -> { langIdx = 0; btnEN.setSelected(true); btnPT.setSelected(false); refreshLabels(title); });
        btnPT.addActionListener(e -> { langIdx = 1; btnPT.setSelected(true); btnEN.setSelected(false); refreshLabels(title); });
        langPanel.add(btnEN);
        langPanel.add(btnPT);

        bar.add(title,     BorderLayout.WEST);
        bar.add(langPanel, BorderLayout.EAST);
        return bar;
    }

    private JToggleButton styledToggle(String text) {
        JToggleButton b = new JToggleButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2.setColor(TEXT); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    g2.setColor(BG);
                } else {
                    g2.setColor(BG2); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    g2.setColor(MUTED);
                }
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        b.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        b.setPreferredSize(new Dimension(36, 22));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        return b;
    }

    // ── corpo ─────────────────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(22, 20, 16, 20));

        body.add(buildStatusPanel());
        body.add(Box.createVerticalStrut(18));
        body.add(buildButtonRow());
        body.add(Box.createVerticalStrut(16));
        body.add(buildLogPanel());
        body.add(Box.createVerticalStrut(12));
        body.add(buildTrayNote());
        return body;
    }

    private JPanel buildStatusPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ícone circular
        JPanel icon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color border = watcherThread != null && watcherThread.isAlive() ? GREEN : BORDER;
                g2.setColor(BG2);
                g2.fillOval(1, 1, 61, 61);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(1, 1, 61, 61);
                // ponto central
                g2.setColor(border);
                g2.fillOval(27, 27, 10, 10);
            }
        };
        icon.setPreferredSize(new Dimension(64, 64));
        icon.setMaximumSize(new Dimension(64, 64));
        icon.setOpaque(false);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblStatus = new JLabel(lbl(3));
        lblStatus.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        lblStatus.setForeground(TEXT);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblSub = new JLabel(lbl(5));
        lblSub.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblSub.setForeground(MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(icon);
        p.add(Box.createVerticalStrut(10));
        p.add(lblStatus);
        p.add(Box.createVerticalStrut(2));
        p.add(lblSub);
        return p;
    }

    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        btnStart = actionButton(lbl(1), true);
        btnStop  = actionButton(lbl(2), false);
        btnStop.setEnabled(false);

        btnStart.addActionListener(e -> startWatcher());
        btnStop.addActionListener(e -> stopWatcher());

        row.add(btnStart);
        row.add(btnStop);
        return row;
    }

    private JButton actionButton(String text, boolean primary) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isPrimary = getBackground().equals(TEXT);
                if (!isEnabled()) {
                    g2.setColor(BG2); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(MUTED);
                } else if (isPrimary) {
                    g2.setColor(TEXT); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(BG);
                } else {
                    g2.setColor(BG); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(TEXT);
                }
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        b.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        b.setBackground(primary ? TEXT : BG);
        b.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setPreferredSize(new Dimension(0, 42));
        return b;
    }

    private JPanel buildLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG2);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(5, 10, 5, 10)
        ));
        JLabel logTitle = new JLabel(lbl(6));
        logTitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        logTitle.setForeground(MUTED);
        btnClear = new JButton(lbl(7));
        btnClear.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        btnClear.setForeground(MUTED);
        btnClear.setBorderPainted(false);
        btnClear.setContentAreaFilled(false);
        btnClear.setFocusPainted(false);
        btnClear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> logArea.setText(""));
        header.add(logTitle, BorderLayout.WEST);
        header.add(btnClear, BorderLayout.EAST);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        logArea.setBackground(BG);
        logArea.setForeground(MUTED);
        logArea.setLineWrap(false);
        logArea.setBorder(new EmptyBorder(6, 10, 6, 10));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(null);

        panel.add(header, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        return panel;
    }

    private JLabel buildTrayNote() {
        lblTray = new JLabel(lbl(8), SwingConstants.CENTER);
        lblTray.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        lblTray.setForeground(MUTED);
        lblTray.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lblTray;
    }

    // ── lógica Start/Stop ─────────────────────────────────────────────────────
    private void startWatcher() {
        watcherService = new WatcherService(this::appendLog);
        watcherThread  = new Thread(watcherService, "watcher");
        watcherThread.setDaemon(true);
        watcherThread.start();

        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        lblStatus.setText(lbl(4));
        lblStatus.setForeground(GREEN);
        appendLog(lbl(10));
        frame.repaint();
    }

    private void stopWatcher() {
        if (watcherService != null) watcherService.stop();
        if (watcherThread  != null) watcherThread.interrupt();
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        lblStatus.setText(lbl(3));
        lblStatus.setForeground(TEXT);
        appendLog(lbl(11));
        frame.repaint();
    }

    public void appendLog(String msg) {
        SwingUtilities.invokeLater(() -> {
            String ts = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            logArea.append(ts + "  " + msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    // ── atualizar labels ao trocar idioma ────────────────────────────────────
    private void refreshLabels(JLabel title) {
        frame.setTitle(lbl(0));
        title.setText(lbl(0));
        btnStart.setText(lbl(1));
        btnStop.setText(lbl(2));
        lblStatus.setText(watcherThread != null && watcherThread.isAlive() ? lbl(4) : lbl(3));
        lblSub.setText(lbl(5));
        btnClear.setText(lbl(7));
        lblTray.setText(lbl(8));
        frame.repaint();
    }

    // ── bandeja do sistema ────────────────────────────────────────────────────
    private void setupTray() {
        if (!SystemTray.isSupported()) return;
        try {
            // ícone gerado programaticamente com as iniciais "FO"
            int size = 64;
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0x3DD68C));
            g2.fillRoundRect(0, 0, size, size, 18, 18);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
            FontMetrics fm = g2.getFontMetrics();
            String initials = "FO";
            g2.drawString(initials, (size - fm.stringWidth(initials)) / 2, (size + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();

            trayIcon = new TrayIcon(img, "Smart File Organizer");
            trayIcon.setImageAutoSize(true);

            PopupMenu menu = new PopupMenu();

            MenuItem lblStatus = new MenuItem("● Idle");
            lblStatus.setEnabled(false);
            MenuItem open = new MenuItem("Open window");
            MenuItem separator1 = new MenuItem("-");
            MenuItem startItem = new MenuItem("Start watcher");
            MenuItem stopItem  = new MenuItem("Stop watcher");
            MenuItem separator2 = new MenuItem("-");
            MenuItem exit = new MenuItem("Exit");

            open.addActionListener(e -> {
                frame.setVisible(true);
                frame.setState(JFrame.NORMAL);
                frame.toFront();
            });

            startItem.addActionListener(e -> {
                startWatcher();
                lblStatus.setLabel("● Running");
                startItem.setEnabled(false);
                stopItem.setEnabled(true);
            });

            stopItem.addActionListener(e -> {
                stopWatcher();
                lblStatus.setLabel("● Idle");
                startItem.setEnabled(true);
                stopItem.setEnabled(false);
            });
            stopItem.setEnabled(false);

            exit.addActionListener(e -> confirmExit());

            trayIcon.addActionListener(e -> {
                frame.setVisible(true);
                frame.setState(JFrame.NORMAL);
                frame.toFront();
            });

            // tooltip dinâmico ao passar o mouse
            trayIcon.setToolTip("Smart File Organizer — Idle\nClick to open");

            menu.add(lblStatus);
            menu.addSeparator();
            menu.add(open);
            menu.addSeparator();
            menu.add(startItem);
            menu.add(stopItem);
            menu.addSeparator();
            menu.add(exit);

            trayIcon.setPopupMenu(menu);
            SystemTray.getSystemTray().add(trayIcon);

            // sincroniza tooltip com estado real do watcher
            new Timer(2000, e -> {
                boolean alive = watcherThread != null && watcherThread.isAlive();
                trayIcon.setToolTip("Smart File Organizer — " + (alive ? "Running" : "Idle") + "\nClick to open");
            }).start();

        } catch (AWTException e) {
            trayIcon = null;
        }
    }

    private void confirmExit() {
        int r = JOptionPane.showConfirmDialog(frame,
                langIdx == 0 ? "Exit Smart File Organizer?" : "Sair do Organizador?",
                lbl(0), JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            stopWatcher();
            System.exit(0);
        }
    }
}
