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

                int contador = 1;
                String nomeOriginal = caminhoOrigem.getFileName().toString();
                int posicaoPonto = nomeOriginal.lastIndexOf(".");
                String nomeSemExtensao = nomeOriginal.substring(0, posicaoPonto);
                String extensao = nomeOriginal.substring(posicaoPonto);
                while (Files.exists(destinoFinal)) {
                    System.out.println("Arquivo já existe: " + "[" + destinoFinal + "]");
                    String novoNome = nomeSemExtensao + "(" + contador + ")" + extensao;
                    destinoFinal = caminhoDestino.resolve(novoNome);
                    contador++;
                }

                Files.move(caminhoOrigem, destinoFinal);
                System.out.println("Arquivo movido: " + nomeArquivo + " -> " + "[" + destinoFinal + "]");

            } catch (IOException e){
                System.out.println("Erro ao mover arquivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }