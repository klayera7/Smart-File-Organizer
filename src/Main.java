import config.Settings;
import utils.FileHandler;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

void main(String[] args) {

    Settings settings = new Settings();
    FileHandler handler = new FileHandler();

    String usuarioHome = System.getProperty("user.home");
    Path pastaOrigem = Path.of(usuarioHome, "OneDrive", "Área de Trabalho", "Origem");
    String baseDestino = Path.of(usuarioHome, "OneDrive", "Área de Trabalho", "Destino").toString() + "\\";

    try {
        Files.createDirectories(pastaOrigem);
        Files.createDirectories(Path.of(baseDestino));
    } catch (java.io.IOException e) {
        System.out.println("Erro ao criar pastas base: " + e.getMessage());
    }


    try {
        WatchService vigia = FileSystems.getDefault().newWatchService();
        pastaOrigem.register(vigia, StandardWatchEventKinds.ENTRY_CREATE);
        System.out.println("Vigia em execução... Pasta [Origem] sendo vigiada.");

        while (true) {

            WatchKey chave = vigia.take();

            for (WatchEvent<?> evento : chave.pollEvents()) {

                Path nomeDoArquivoNovo = (Path) evento.context();
                Path caminhoCompleto = pastaOrigem.resolve(nomeDoArquivoNovo);

                if (Files.isRegularFile(caminhoCompleto)) {
                    System.out.println("Novo arquivo detectado: " + nomeDoArquivoNovo);

                    String extensao = handler.extrairExtensao(nomeDoArquivoNovo.toString());
                    String pastaDestino = baseDestino + settings.getPastaDestino(extensao);

                    int tentativas = 0;

                    while (Files.exists(caminhoCompleto) && tentativas < 5) {

                        handler.moverArquivo(caminhoCompleto.toString(), pastaDestino);


                        if (!Files.exists(caminhoCompleto)) {
                            System.out.println("✅ Movido na velocidade da luz!");
                            break;
                        }

                        System.out.println("Arquivo trancado pelo Windows. Tentando de novo...");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {}

                        tentativas++;
                    }
                }
            }
            chave.reset();
        }


    } catch (Exception e) {
        System.out.println("Erro ao iniciar o vigário: " + e.getMessage());
        e.printStackTrace();
    }




}