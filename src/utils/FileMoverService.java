package utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class FileMoverService {
    
    private final FileNameUtils fileNameUtils;

    public FileMoverService() {
        this.fileNameUtils = new FileNameUtils();
    }

    public FileMoverService(FileNameUtils fileNameUtils) {
        this.fileNameUtils = fileNameUtils;
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
                System.out.println("Arquivo já existe: " + "[" + destinoFinal + "]");
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
