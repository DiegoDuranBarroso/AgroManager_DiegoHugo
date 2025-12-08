package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Finca;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class IaFincaService {

    @Value("${google.ai.api.key}")
    private String googleApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generarSugerenciasParaFinca(Finca finca) {

        // 1. Construimos el prompt con los datos de la finca
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un asesor agrícola experto. ");
        prompt.append("Analiza la siguiente finca y propón entre 3 y 5 sugerencias de mejora prácticas y claras.\n\n");
        prompt.append("Nombre: ").append(finca.getNombre()).append("\n");
        prompt.append("Estado: ").append(finca.getEstado()).append("\n");
        prompt.append("Área (ha): ").append(
                finca.getArea() != null ? finca.getArea() : "desconocida"
        ).append("\n");
        prompt.append("Ciudad: ").append(
                finca.getCiudad() != null ? finca.getCiudad() : "desconocida"
        ).append("\n");
        prompt.append("Provincia: ").append(
                finca.getProvincia() != null ? finca.getProvincia() : "desconocida"
        ).append("\n");

        if (finca.getLatitud() != null && finca.getLongitud() != null) {
            prompt.append("Latitud: ").append(finca.getLatitud()).append("\n");
            prompt.append("Longitud: ").append(finca.getLongitud()).append("\n");
        }

        prompt.append("\nResponde en español, en formato de lista con viñetas, ");
        prompt.append("centrándote en recomendaciones agrícolas y de gestión de mano de obra.\n");

        // 2. URL de Gemini: modelo actual gemini-2.5-flash (según docs actuales)
        String modelName = "gemini-2.5-flash";
        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + modelName + ":generateContent";

        // 3. Cuerpo JSON para Gemini
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt.toString())
                        ))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 🔑 Autenticación correcta: cabecera x-goog-api-key
        headers.set("x-goog-api-key", googleApiKey);

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

            // 4. Extraer texto: candidates[0].content.parts[0].text
            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) responseBody.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "La IA no ha generado sugerencias.";
            }

            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> content =
                    (Map<String, Object>) firstCandidate.get("content");
            if (content == null) {
                return "La IA no ha generado contenido.";
            }

            List<Map<String, Object>> parts =
                    (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                return "La IA no ha generado sugerencias.";
            }

            Object text = parts.get(0).get("text");
            return text != null ? text.toString() : "La IA no ha devuelto texto.";

        } catch (HttpClientErrorException e) {
            // Errores HTTP con más detalle (404, 401, etc.)
            String cuerpo = e.getResponseBodyAsString();
            return "Error al llamar a la IA: " + e.getStatusCode()
                    + " " + e.getStatusText()
                    + " - " + cuerpo;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al llamar a la IA: " + e.getMessage();
        }
    }
}
