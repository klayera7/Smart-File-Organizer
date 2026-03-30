package config;

import java.util.HashMap;
import java.util.Map;

public class Settings {

    private Map<String, String> regras;

    public Settings() {
        this.regras = new HashMap<>();
        // Imagens
        adicionarRegras("Imagens", ".jpg", ".jpeg", ".png",
                ".gif", ".svg", ".webp", ".HEIC", ".heic",
                ".bmp", ".tiff", ".tif", ".ico", ".psd", ".raw");
        // Vídeos
        adicionarRegras("Vídeos", ".mp4", ".mkv", ".avi", ".mov",
                ".wmv", ".flv", ".webm", ".m4v", ".3gp", ".mpg", ".mpeg");
        // Documentos
        adicionarRegras("Documentos", ".pdf", ".docx", ".doc", ".txt",
                ".rtf", ".odt", ".pages", ".epub", ".mobi");
        // Planilhas
        adicionarRegras("Planilhas", ".xlsx", ".xls", ".csv", ".ods", ".numbers");
        // Apresentações
        adicionarRegras("Apresentações", ".pptx",
                ".ppt", ".odp", ".key");
        // Música
        adicionarRegras("Música", ".mp3", ".wav",
                ".flac", ".aac", ".ogg", ".wma", ".m4a", ".opus");
        // Compactados
        adicionarRegras("Compactados", ".zip", ".rar", ".7z",
                ".tar", ".gz", ".bz2", ".xz", ".pkg");
        // Código
        adicionarRegras("Código", ".java", ".py", ".js", ".html", ".css", ".cpp",
                ".c", ".php", ".rb", ".go", ".rs", ".swift", ".kt", ".ts");
        // Executáveis
        adicionarRegras("Executáveis", ".exe", ".msi", ".app", ".deb",
                ".rpm", ".dmg");
        // Fontes
        adicionarRegras("Fontes", ".ttf", ".otf", ".woff", ".woff2", ".eot");
    }

    public String getPastaDestino(String extensao){
        return regras.getOrDefault(extensao, "Outros");
    }


    private void adicionarRegras(String pastaDestino, String... extensoes) {
        for (String extensao : extensoes) {
            regras.put(extensao, pastaDestino);
        }
    }


}