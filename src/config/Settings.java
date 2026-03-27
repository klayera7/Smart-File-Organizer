package config;

import java.util.Map;

public class Settings {

    private Map<String, String> regras;

    public Settings(Map<String, String> regras) {
        this.regras = regras;
    }

    public String getPastaDestino(String extensao){
        return regras.getOrDefault(extensao, "Outros");
    }

}