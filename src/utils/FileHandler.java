package utils;

public class FileHandler {

    public String extrairExtensao(String nomeArquivo){

        int posicaoPonto = nomeArquivo.lastIndexOf(".");

        if (posicaoPonto == -1) {
            return "";
        }

        return nomeArquivo.substring(posicaoPonto);
    }

}