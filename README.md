# TP18 - Service gRPC avec Spring Boot

Service gRPC pour la gestion de comptes bancaires développé avec Spring Boot.

## Structure du Projet

```
grpc2/
├── pom.xml                          # Configuration Maven avec toutes les dépendances
├── CompteService.proto              # Fichier proto à importer dans BloomRPC
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ma/projet/grpc/
│   │   │       ├── Grpc2Application.java      # Classe principale Spring Boot
│   │   │       └── controllers/
│   │   │           └── CompteServiceImpl.java # Implémentation du service gRPC
│   │   ├── proto/
│   │   │   └── CompteService.proto            # Définition du service gRPC
│   │   └── resources/
│   │       └── application.properties         # Configuration Spring Boot
│   └── test/
└── examples/
    └── bloomrpc-examples.json       # Exemples de requêtes JSON pour BloomRPC
```

## Prérequis

- Java 20
- Maven 3.6+
- BloomRPC (pour tester le service)

## Installation et Configuration

### 1. Compiler le projet

```bash
mvn clean compile
```

Cette commande génère automatiquement les classes Java à partir du fichier `.proto`.

### 2. Lancer l'application

```bash
mvn spring-boot:run
```

Le serveur gRPC sera accessible sur le port **9090** (configuré dans `application.properties`).

## Test avec BloomRPC

Consultez le fichier **BLOOMRPC_GUIDE.md** pour un guide détaillé sur l'utilisation de BloomRPC.

### Démarrage rapide :

1. **Installer BloomRPC** : https://github.com/bloomrpc/bloomrpc/releases

2. **Importer le fichier proto** :
   - Ouvrir BloomRPC
   - File > Import Protobuf
   - Sélectionner `CompteService.proto` (à la racine du projet ou dans `src/main/proto/`)

3. **Configurer l'adresse du serveur** :
   ```
   localhost:9090
   ```

4. **Tester les méthodes** :
   - `AllComptes` : `{}`
   - `SaveCompte` : Voir les exemples dans `examples/bloomrpc-examples.json`
   - `CompteById` : `{"id": "uuid-du-compte"}`
   - `TotalSolde` : `{}`

## Méthodes du Service

### 1. AllComptes
Récupère tous les comptes bancaires.

**Requête** :
```json
{}
```

**Réponse** :
```json
{
  "comptes": [
    {
      "id": "...",
      "solde": 1500.50,
      "dateCreation": "2024-01-15",
      "type": "COURANT"
    }
  ]
}
```

### 2. SaveCompte
Crée un nouveau compte bancaire.

**Requête** :
```json
{
  "compte": {
    "solde": 1500.50,
    "dateCreation": "2024-01-15",
    "type": "COURANT"
  }
}
```

**Types de compte** : `COURANT` ou `EPARGNE`

### 3. CompteById
Récupère un compte par son ID.

**Requête** :
```json
{
  "id": "uuid-du-compte"
}
```

### 4. TotalSolde
Calcule les statistiques globales des soldes.

**Requête** :
```json
{}
```

**Réponse** :
```json
{
  "stats": {
    "count": 2,
    "sum": 15000.00,
    "average": 7500.00
  }
}
```

## Configuration

### Ports
- **HTTP** : 8080 (si vous ajoutez des endpoints REST)
- **gRPC** : 9090

### Base de données
- **H2** en mémoire (données perdues au redémarrage)
- Console H2 disponible sur : http://localhost:8080/h2-console

## Technologies Utilisées

- **Spring Boot** 3.2.0
- **gRPC** 1.53.0
- **Protocol Buffers** 3.21.12
- **H2 Database** (en mémoire)
- **Lombok**
- **Spring Data JPA**

## Notes

- Les comptes sont stockés en mémoire (ConcurrentHashMap) pour ce TP
- Les données sont perdues lors du redémarrage du serveur
- Pour une utilisation en production, connecter à une vraie base de données avec Spring Data JPA

