package com.nada.tp1.llm;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonGenerator;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@SessionScoped
public class JsonAdapterPourGemini implements Serializable {

    @Inject
    private LlmClientPourGemini llmClient;

    private String systemRole;
    private String requeteJson;   // historique envoyé à chaque requête (null au début)

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
        this.requeteJson = null;  // nouvelle conversation
    }

    public LlmInteraction envoyerRequete(String question) throws RequeteException {
        String aEnvoyer;
        if (requeteJson == null) {
            aEnvoyer = creerRequeteJson(systemRole, question);
        } else {
            aEnvoyer = ajouteMessage(requeteJson, "user", question);
        }

        try (Response r = llmClient.envoyerRequete(aEnvoyer)) {
            String corps = r.readEntity(String.class);
            if (r.getStatus() != 200) {
                throw new RequeteException("Statut HTTP " + r.getStatus() + " : " + corps);
            }
            String reponse = extractReponse(corps);
            this.requeteJson = ajouteMessage(aEnvoyer, "model", reponse);
            return new LlmInteraction(prettyPrinting(aEnvoyer), prettyPrinting(corps), reponse);
        } catch (RequeteException e) {
            throw e;
        } catch (Exception e) {
            throw new RequeteException("Problème avec la requête : " + e.getMessage(), e);
        }
    }

    private JsonObject message(String role, String texte) {
        return Json.createObjectBuilder()
                .add("role", role)
                .add("parts", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder().add("text", texte)))
                .build();
    }

    private String creerRequeteJson(String role, String question) {
        return Json.createObjectBuilder()
                .add("system_instruction", Json.createObjectBuilder()
                        .add("parts", Json.createArrayBuilder()
                                .add(Json.createObjectBuilder().add("text", role))))
                .add("contents", Json.createArrayBuilder().add(message("user", question)))
                .build().toString();
    }

    private String ajouteMessage(String json, String role, String texte) {
        JsonObject o = Json.createReader(new StringReader(json)).readObject();
        JsonArray contents = Json.createArrayBuilder(o.getJsonArray("contents"))
                .add(message(role, texte)).build();
        return Json.createObjectBuilder(o).add("contents", contents).build().toString();
    }

    private String extractReponse(String json) {
        JsonObject o = Json.createReader(new StringReader(json)).readObject();
        return o.getJsonArray("candidates").getJsonObject(0)
                .getJsonObject("content").getJsonArray("parts")
                .getJsonObject(0).getString("text");
    }

    private String prettyPrinting(String json) {
        JsonObject o = Json.createReader(new StringReader(json)).readObject();
        StringWriter sw = new StringWriter();
        Json.createWriterFactory(Map.of(JsonGenerator.PRETTY_PRINTING, true))
                .createWriter(sw).write(o);
        return sw.toString();
    }
}