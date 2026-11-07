## Utilisation de l'IA : clé API non reçue par Gemini (erreur 403)

**Problème :** mon application démarrait, mais chaque question retournait une erreur 403 "unregistered callers". Ma clé était dans la variable GEMINI_KEY, mais elle n'arrivait pas à Gemini.

**Raison de l'IA :** j'avais relu mon code sans trouver l'erreur, donc j'ai demandé de l'aide à Claude.

**Prompt :** Expliquer moi pourquoi et où j'ai fait l'erreur pour que mon cle api ne fonctionne pas

**Réponse de l'IA :** IntelliJ avait été lancé avant la création de GEMINI_KEY, donc Payara n'héritait pas de la variable. Il faut arrêter Payara, fermer complètement IntelliJ, puis vérifier avec `echo $env:GEMINI_KEY` dans un nouveau PowerShell.

**Ce que j'ai appris :** un programme lit les variables d'environnement au démarrage, donc il faut le relancer après les avoir créées. 