package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Finca;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class IaFincaService {

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generarSugerenciasParaFinca(Finca finca) {

        // 1. Construimos el prompt con los datos de la finca
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un asesor agrícola experto. ");
        prompt.append("Analiza la siguiente finca y propón entre 3 y 5 sugerencias de mejora prácticas y claras.Es importante tener en cuenta la localización.\n\n");
        prompt.append("Nombre: ").append(finca.getNombre()).append("\n");
        prompt.append("Estado: ").append(finca.getEstado()).append("\n");
        prompt.append("Área (ha): ").append(finca.getArea() != null ? finca.getArea() : "desconocida").append("\n");
        prompt.append("Ciudad: ").append(finca.getCiudad() != null ? finca.getCiudad() : "desconocida").append("\n");
        prompt.append("Provincia: ").append(finca.getProvincia() != null ? finca.getProvincia() : "desconocida").append("\n");
        if (finca.getLatitud() != null && finca.getLongitud() != null) {
            prompt.append("Latitud: ").append(finca.getLatitud()).append("\n");
            prompt.append("Longitud: ").append(finca.getLongitud()).append("\n");
        }

        prompt.append("\nResponde en español, en formato de lista con viñetas, ");
        prompt.append("centrándote en recomendaciones agrícolas y de gestión de mano de obra.\n");

        // 2. URL de OpenRouter
        String url = "https://openrouter.ai/api/v1/chat/completions";

        // 3. Cuerpo JSON para OpenRouter (formato OpenAI-like)
        Map<String, Object> body = Map.of(
                "model", "openai/gpt-4o-mini-2024-07-18",
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "Eres un asesor agrícola especializado en optimización de fincas y recursos."
                        ),
                        Map.of(
                                "role", "user",
                                "content", prompt.toString()
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openRouterApiKey);

        // Cabeceras recomendadas por OpenRouter (opcionales pero buenas para “quedar bien”)
        headers.add("HTTP-Referer", "http://localhost:8080");
        headers.add("X-Title", "AgroManager - IA de fincas");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                return "No se ha recibido respuesta de la IA.";
            }

            // Extraer texto: choices[0].message.content
            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) responseBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                return "La IA no ha generado sugerencias.";
            }

            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message =
                    (Map<String, Object>) firstChoice.get("message");

            Object content = message.get("content");
            return content != null ? content.toString() : "La IA no ha devuelto texto.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al llamar a la IA: " + e.getMessage();
        }
    }
}
