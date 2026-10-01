# Intégration continue

Le dépôt exécute un pipeline GitHub Actions (`.github/workflows/ci.yml`) à chaque push et pull request ciblant `develop` ou `master`. Vous n'avez rien à installer : GitHub l'exécute automatiquement quand vous ouvrez une pull request.

## Ce que fait le pipeline

```mermaid
flowchart LR
    A[Push ou pull request] --> B[Tests unitaires et couverture]
    A --> C[Tests instrumentés]
    B --> D[Rapports JaCoCo XML + HTML]
    D --> E[Commentaire de couverture sur la pull request]
    D --> F[Seuil de 80 % de couverture de lignes pour openpay-sdk]
    C --> G[Rapports des tests sur émulateur]
```

### Tests unitaires et couverture

Ce job s'exécute à chaque push et pull request :

1. Compile le projet avec le JDK 17 et exécute `testDebugUnitTest` pour tous les modules.
2. Génère les rapports de couverture JaCoCo (`jacocoTestReport`).
3. Applique le minimum de 80 % de couverture de lignes sur `openpay-sdk` (`jacocoCoverageVerification`). Le build échoue quand la couverture passe sous ce seuil.
4. Sur les pull requests, publie (et maintient à jour) un commentaire avec la couverture globale et celle des fichiers modifiés par la pull request, via [Madrapps/jacoco-report](https://github.com/Madrapps/jacoco-report).
5. Téléverse les rapports HTML de couverture et de tests comme artefacts du build, afin que vous puissiez les télécharger et les consulter depuis la page de l'exécution.

### Tests instrumentés

Ce job démarre un émulateur Android 14 (API 34) sur le runner et exécute `connectedDebugAndroidTest`. L'émulateur a besoin de l'accélération matérielle, c'est pourquoi le job active KVM avant de le lancer. Les rapports de tests sont téléversés comme artefacts.

## Lire le commentaire de couverture

Chaque pull request reçoit un seul commentaire intitulé **Unit test coverage**, actualisé à chaque push. Il affiche :

- **La couverture globale** du projet, marquée d'un ✅ quand elle atteint 80 % ou plus.
- **La couverture des fichiers modifiés**, pour que les relecteurs voient si le nouveau code est testé sans ouvrir le rapport complet.

## Exécuter les mêmes vérifications en local

```bash
./gradlew testDebugUnitTest jacocoTestReport :openpay-sdk:jacocoCoverageVerification
./gradlew connectedDebugAndroidTest   # nécessite un appareil ou un émulateur
```

Le rapport HTML est généré dans `<module>/build/reports/jacoco/jacocoTestReport/html/index.html`.

## Déploiement continu

Un second workflow (`.github/workflows/cd.yml`) publie la bibliothèque sur
Maven Central. Il s'exécute quand un tag de version (`v1.2.3`) est poussé —
normalement le tag du commit de merge d'une release sur `master` — et peut
aussi être lancé manuellement depuis l'onglet Actions.

```mermaid
flowchart LR
    A[Push du tag v*] --> B[Tests unitaires + seuil de couverture]
    B --> C[Vérifier tag == VERSION_NAME]
    C --> D[Signer et téléverser vers l'API de staging]
    D --> E[Promouvoir le dépôt de staging vers le Portal]
    E --> F[Publish manuel sur central.sonatype.com]
    E --> G[Release GitHub avec notes générées]
```

1. **Les tests d'abord** — la release est bloquée si les tests unitaires
   ou le seuil de couverture de 80 % échouent.
2. **Garde de version** — le workflow échoue si le tag ne correspond pas à
   `VERSION_NAME` dans `gradle.properties` ; une release mal étiquetée ne
   peut donc pas partir.
3. **Téléversement signé** — les artefacts sont signés en PGP et envoyés
   vers l'API de staging OSSRH de Sonatype. Les identifiants
   et la clé de signature proviennent des secrets du dépôt
   (`MAVEN_REPOSITORY_USERNAME`, `MAVEN_REPOSITORY_PASSWORD`,
   `SIGNING_KEY`, `SIGNING_PASSWORD`) ; ils ne vivent jamais dans le dépôt.
4. **Promotion vers le Portal** — l'API de staging garde le téléversement
   dans un dépôt ouvert que le Portal n'affiche pas. Le workflow appelle
   l'API manuelle pour le promouvoir, ce qui fait apparaître le déploiement
   sous **Publish → Deployments** dans le Portal.
5. **Confirmation manuelle** — rien ne devient public automatiquement :
   une personne mainteneuse doit appuyer sur **Publish** sur le déploiement
   validé dans [central.sonatype.com](https://central.sonatype.com). Le
   résumé de l'exécution pointe vers cette étape.
6. Une release GitHub avec notes générées est créée pour le tag.