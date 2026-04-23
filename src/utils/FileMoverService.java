package utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
// Novos imports adicionados para a etapa 4 funcionarem
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

public class FileMoverService {

    private final FileNameUtils fileNameUtils;

    public FileMoverService() {
        this.fileNameUtils = new FileNameUtils();
    }

    public FileMoverService(FileNameUtils fileNameUtils) {
        this.fileNameUtils = fileNameUtils;
    }

    public boolean isArquivoLivre(Path arquivo) {
        try (FileChannel channel = FileChannel.open(arquivo, StandardOpenOption.WRITE)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void moverArquivo(String nomeArquivo, String pastaDestino) {
        try {
            Path caminhoOrigem = Path.of(nomeArquivo);
            Path caminhoDestino = Path.of(pastaDestino);
            Files.createDirectories(caminhoDestino);

            Path destinoFinal = caminhoDestino.resolve(caminhoOrigem.getFileName());

            int contador = 1;
            String nomeOriginal = caminhoOrigem.getFileName().toString();

            while (Files.exists(destinoFinal)) {
                System.out.println("Arquivo ja existe: " + "[" + destinoFinal + "]");
                String novoNome = fileNameUtils.gerarNomeComContador(nomeOriginal, contador);
                destinoFinal = caminhoDestino.resolve(novoNome);
                contador++;
            }

            Files.move(caminhoOrigem, destinoFinal);
            System.out.println("Arquivo movido: " + nomeArquivo + " -> " + "[" + destinoFinal + "]");

        } catch (IOException e) {
            System.out.println("Erro ao mover arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}