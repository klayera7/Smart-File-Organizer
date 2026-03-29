import config.Settings;
import utils.FileHandler;
import java.util.Map;
import java.util.HashMap;

void main(String[] args) {

    Map<String, String> meuMapaDeRegras = new HashMap<>();

    // Imagens
    meuMapaDeRegras.put(".jpg", "Imagens");
    meuMapaDeRegras.put(".jpeg", "Imagens");
    meuMapaDeRegras.put(".png", "Imagens");
    meuMapaDeRegras.put(".gif", "Imagens");
    meuMapaDeRegras.put(".svg", "Imagens");
    meuMapaDeRegras.put(".webp", "Imagens");

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

    String caminhoDoArquivoOrigem = "C:\\Users\\alexk\\OneDrive\\Área de Trabalho\\Origem\\IMG_2450.jpg";

    FileHandler handler = new FileHandler();

    String extensao = handler.extrairExtensao(caminhoDoArquivoOrigem);

    String pastaDestino = settings.getPastaDestino(extensao);

    String caminhoDaPastaDestino = "C:\\Users\\alexk\\OneDrive\\Área de Trabalho\\Destino\\" + pastaDestino;
    handler.moverArquivo(caminhoDoArquivoOrigem, caminhoDaPastaDestino);

}