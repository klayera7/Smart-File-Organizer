import config.Settings;
import utils.FileHandler;
import java.util.Map;
import java.util.HashMap;

void main(String[] args) {

    Map<String, String> meuMapaDeRegras = new HashMap<>();

    meuMapaDeRegras.put(".pdf", "Documentos");
    meuMapaDeRegras.put(".jpg", "Imagens");
    meuMapaDeRegras.put(".mp3", "Música");

    Settings settings = new Settings(meuMapaDeRegras);

    String caminhoDoArquivoOrigem = "C:\\Users\\alexk\\OneDrive\\Área de Trabalho\\Origem\\IMG_2450.jpg";

    FileHandler handler = new FileHandler();

    String extensao = handler.extrairExtensao(caminhoDoArquivoOrigem);

    String pastaDestino = settings.getPastaDestino(extensao);


    String caminhoDaPastaDestino = "C:\\Users\\alexk\\OneDrive\\Área de Trabalho\\Destino\\" + pastaDestino;
    handler.moverArquivo(caminhoDoArquivoOrigem, caminhoDaPastaDestino);

}