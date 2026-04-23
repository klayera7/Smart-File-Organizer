package utils;

public class FileNameUtils {

    public String extrairExtensao(String nomeArquivo) {
        int posicaoPonto = nomeArquivo.lastIndexOf(".");

        if (posicaoPonto == -1) {
            return "";
        }
        return nomeArquivo.substring(posicaoPonto);
    }

    public String gerarNomeComContador(String nomeOriginal, int contador) {
        int posicaoPonto = nomeOriginal.lastIndexOf(".");

        String nomeSemExtensao;
        String extensao;
        
        if (posicaoPonto != -1) {
            nomeSemExtensao = nomeOriginal.substring(0, posicaoPonto);
            extensao = nomeOriginal.substring(posicaoPonto);
        } else {
            nomeSemExtensao = nomeOriginal;
            extensao = "";
        }

        return nomeSemExtensao + "(" + contador + ")" + extensao;
    }
}
