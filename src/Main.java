import config.Settings;
import utils.FileHandler;

void main(String[] args) {

    Map<String, String> meuMapaDeRegras = new HashMap<>();

    meuMapaDeRegras.put(".pdf", "Documentos");
    meuMapaDeRegras.put(".jpg", "Imagens");
    meuMapaDeRegras.put(".mp3", "Música");

    Settings settings = new Settings(meuMapaDeRegras);

    String meuArquivo = "arquivo.jpg";

    FileHandler handler = new FileHandler();

    String extensao = handler.extrairExtensao(meuArquivo);

    String pastaDestino = settings.getPastaDestino(extensao);

    System.out.println("Arquivo: " + meuArquivo);
    System.out.println("Extensão: " + extensao);
    System.out.println("Pasta de destino: " + pastaDestino);

}