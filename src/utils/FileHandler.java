package utils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;


public class FileHandler {

    public String extrairExtensao(String nomeArquivo) {

        int posicaoPonto = nomeArquivo.lastIndexOf(".");

        if (posicaoPonto == -1) {
            return "";
        }
        return nomeArquivo.substring(posicaoPonto);
    }

        public void moverArquivo(String nomeArquivo, String pastaDestino) {
            try {

                Path caminhoOrigem = Path.of(nomeArquivo);
                Path caminhoDestino = Path.of(pastaDestino);
                Files.createDirectories(caminhoDestino);

                Path destinoFinal = caminhoDestino.resolve(caminhoOrigem.getFileName());

                Files.move(caminhoOrigem, destinoFinal);
                System.out.println("Arquivo movido: " + nomeArquivo + " -> " + destinoFinal);

            } catch (IOException e){
                System.out.println("Erro ao mover arquivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }