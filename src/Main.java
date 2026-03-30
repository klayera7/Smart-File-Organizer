import config.Settings;
import utils.FileHandler;
import java.nio.file.Files;
import java.nio.file.Path;

void main(String[] args) {

    Settings settings = new Settings();
    FileHandler handler = new FileHandler();

    Path pastaOrigem = Path.of("C:\\Users\\alexk\\OneDrive\\Área de Trabalho\\Origem");
    String baseDestino = "C:\\Users\\alexk\\OneDrive\\Área de Trabalho\\Destino\\";


    try (java.nio.file.DirectoryStream<Path> buscador = Files.newDirectoryStream(pastaOrigem)) {

        for (Path arquivoAtual : buscador) {
            if (Files.isRegularFile(arquivoAtual)) {
                String caminhoOrigem = arquivoAtual.toString();
                String extensao = handler.extrairExtensao(caminhoOrigem);
                String pastaDestino = settings.getPastaDestino(extensao);
                String caminhoDaPastaDestino = baseDestino + pastaDestino;
                handler.moverArquivo(caminhoOrigem, caminhoDaPastaDestino);
            }
        }
    } catch (java.io.IOException e) {
        System.out.println("Erro no buscador: " + e.getMessage());
    }


}