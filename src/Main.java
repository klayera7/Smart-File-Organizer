import config.Settings;
import utils.FileHandler;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Path;

void main(String[] args) {

    Map<String, String> meuMapaDeRegras = new HashMap<>();

    // Imagens
    meuMapaDeRegras.put(".jpg", "Imagens");
    meuMapaDeRegras.put(".jpeg", "Imagens");
    meuMapaDeRegras.put(".png", "Imagens");
    meuMapaDeRegras.put(".gif", "Imagens");
    meuMapaDeRegras.put(".svg", "Imagens");
    meuMapaDeRegras.put(".webp", "Imagens");
    meuMapaDeRegras.put(".HEIC", "Imagens");

    // Vídeos
    meuMapaDeRegras.put(".mp4", "Vídeos");
    meuMapaDeRegras.put(".mkv", "Vídeos");
    meuMapaDeRegras.put(".avi", "Vídeos");
    meuMapaDeRegras.put(".mov", "Vídeos");

    // Documentos de Texto
    meuMapaDeRegras.put(".pdf", "Documentos");
    meuMapaDeRegras.put(".docx", "Documentos");
    meuMapaDeRegras.put(".doc", "Documentos");
    meuMapaDeRegras.put(".txt", "Documentos");

    // Planilhas e Apresentações
    meuMapaDeRegras.put(".xlsx", "Planilhas");
    meuMapaDeRegras.put(".xls", "Planilhas");
    meuMapaDeRegras.put(".csv", "Planilhas");
    meuMapaDeRegras.put(".pptx", "Apresentações");

    // Áudio
    meuMapaDeRegras.put(".mp3", "Música");
    meuMapaDeRegras.put(".wav", "Música");

    // Arquivos Compactados
    meuMapaDeRegras.put(".zip", "Compactados");
    meuMapaDeRegras.put(".rar", "Compactados");
    meuMapaDeRegras.put(".7z", "Compactados");

    Settings settings = new Settings(meuMapaDeRegras);
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