import java.nio.file.*;
import java.util.function.Consumer;
import config.Settings;
import utils.FileNameUtils;
import utils.FileMoverService;

/**
 * WatcherService — encapsula a lógica original de monitoramento.
 * Implementa Runnable para rodar em thread separada.
 *
 * Substitua Settings, FileNameUtils e FileMoverService
 * pelas suas classes originais (config/, utils/).
 */
public class WatcherService implements Runnable {

    private final Consumer<String> logger;
    private volatile boolean running = true;

    private final Settings settings            = new Settings();
    private final FileNameUtils nameUtils       = new FileNameUtils();
    private final FileMoverService moverService = new FileMoverService(nameUtils);

    public WatcherService(Consumer<String> logger) {
        this.logger = logger;
    }

    @Override
    public void run() {
        String userHome = System.getProperty("user.home");
        Path source      = Path.of(userHome, "Downloads");
        Path destBase    = Path.of(userHome, "Organized Files");
        String baseStr   = destBase + java.io.File.separator;

        // cria pasta destino
        try {
            Files.createDirectories(destBase);
        } catch (java.io.IOException e) {
            log("Erro ao criar pasta destino: " + e.getMessage());
            return;
        }

        // move arquivos existentes
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(source)) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    String ext    = extractExtension(path.getFileName().toString());
                    String folder = folderFor(ext);
                    log("Extensão detectada: " + ext + " → pasta: " + folder);
                    String dest   = baseStr + folder;
                    moveFile(path, dest);
                }
            }
        } catch (java.io.IOException e) {
            log("Erro ao listar Downloads: " + e.getMessage());
        }

        // abre pasta destino na UI do SO
        try {
            if (java.awt.Desktop.isDesktopSupported())
                java.awt.Desktop.getDesktop().open(destBase.toFile());
        } catch (Exception ignored) {}

        // inicia o vigia
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            source.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            log("Watcher started at: " + source);

            while (running && !Thread.currentThread().isInterrupted()) {
                WatchKey key;
                try {
                    key = watcher.poll(500, java.util.concurrent.TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    break;
                }
                if (key == null) continue;

                for (WatchEvent<?> event : key.pollEvents()) {
                    Path name     = (Path) event.context();
                    Path full     = source.resolve(name);
                    if (!Files.isRegularFile(full)) continue;

                    log("New file detected: " + name);
                    String ext    = extractExtension(name.toString());
                    String folder = folderFor(ext);
                    log("Extension: " + ext + " → folder: " + folder);
                    String dest   = baseStr + folder;

                    for (int i = 0; i < 5 && Files.exists(full) && running; i++) {
                        moveFile(full, dest);
                        if (!Files.exists(full)) break;
                        try { Thread.sleep(500); } catch (InterruptedException e) { return; }
                    }
                }
                key.reset();
            }
        } catch (java.io.IOException e) {
            log("Watcher error: " + e.getMessage());
        }
    }

    public void stop() { running = false; }

    // ── helpers (substitua por Settings/FileNameUtils/FileMoverService) ───────

    private String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot).toLowerCase() : "";
    }

    private String folderFor(String ext) {
        return settings.getPastaDestino(ext);
    }

    private void moveFile(Path source, String destFolder) {
        moverService.moverArquivo(source.toString(), destFolder);
    }

    private void log(String msg) {
        if (logger != null) logger.accept(msg);
        else System.out.println(msg);
    }
}
