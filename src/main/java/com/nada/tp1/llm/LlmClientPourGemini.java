package com.nada.tp1.llm;

import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;

@Dependent
public class LlmClientPourGemini implements Serializable {

    // Vérifiez le nom du modèle le moins cher sur https://ai.google.dev/gemini-api/docs/pricing
    private static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent";

    private final String key;
    private final Client clientRest;
    private final WebTarget target;

    public LlmClientPourGemini() {
        this.key = System.getenv("GEMINI_KEY");
        this.clientRest = ClientBuilder.newClient();
        this.target = clientRest.target(URL).queryParam("key", this.key);
    }

    public Response envoyerRequete(String requeteJson) {
        Entity<String> entity = Entity.entity(requeteJson, MediaType.APPLICATION_JSON);
        return target.request(MediaType.APPLICATION_JSON).post(entity);
    }
}