# Bank Account API

## 1. Présentation

API REST de gestion de comptes bancaires et de leurs transactions, développée en Java avec Spring Boot. L'application suit une architecture **MVC** et communique avec la base de données via **JDBC** (`JdbcTemplate`), sans ORM.

## 2. Stack technique

- Java / Spring Boot
- Architecture MVC
- Spring JDBC (`JdbcTemplate`)
- PostgreSQL
- PostgreSQL JDBC Driver
- Configuration via variables d'environnement (`.env`)

## 3. API

| Méthode | Route                          | Rôle                                                        |
|---------|---------------------------------|---------------------------------------------------------------------|
| `GET`   | `/transactions?type=in\|out`    | Liste les transactions filtrées par type (`in` ou `out`)            |
| `GET`   | `/accounts/{id}/transactions`   | Liste les transactions d'un compte donné                            |
| `POST`  | `/transactions`                 | Crée une nouvelle transaction                                       |
| `GET`   | `/accounts/{id}/balance`        | Retourne le solde actuel d'un compte                                |

### Exemple — `POST /transactions`

Requête :

```json
{
  "accountId": "1a2b3c",
  "transactionType": "IN",
  "amount": 150.00,
  "reason": "Dépôt initial"
}
```

Réponse :

```json
{
  "id": "9f8e7d",
  "createdAt": "2026-08-20T10:15:30Z",
  "transactionType": "IN",
  "amount": 150.00,
  "reason": "Dépôt initial"
}
```

### Exemple — `GET /accounts/{id}/balance`

```json
{
  "accountId": "1a2b3c",
  "balance": 150.00
}
```

## 4. Configuration

L'application se configure via les variables d'environnement suivantes :

| Variable      | Description                          |
|---------------|---------------------------------------|
| `DB_URL`      | URL de connexion PostgreSQL           |
| `DB_USERNAME` | Nom d'utilisateur de la base          |
| `DB_PASSWORD` | Mot de passe de la base               |

Ces valeurs sont stockées dans un fichier `.env`, qui **ne doit jamais être commité** dans le dépôt.

## 5. `.env` et `.env.example`

Un fichier `.env.example` est fourni comme modèle :

```env
DB_URL=jdbc:postgresql://localhost:5432/bank_db
DB_USERNAME=postgres
DB_PASSWORD=changeme
```

Étapes :

1. Copier le fichier d'exemple :
   ```bash
   cp .env.example .env
   ```
2. Renseigner les valeurs réelles dans `.env`.
3. S'assurer que `application.properties` (ou `.yml`) référence bien ces variables, ex. :
   ```properties
   spring.datasource.url=${DB_URL}
   spring.datasource.username=${DB_USERNAME}
   spring.datasource.password=${DB_PASSWORD}
   ```

## 6. Installation et lancement

### Prérequis

- JDK 17+
- Maven (ou Gradle, selon le build utilisé)
- PostgreSQL installé et démarré

### Installation

```bash
git clone <url-du-repo>
cd bank-account-api
mvn install
```

### Configuration de PostgreSQL

Créer la base de données :

```sql
CREATE DATABASE bank_db;
```

Configurer `.env` comme décrit à la section 5.

### Lancement

```bash
mvn spring-boot:run
```

L'application démarre par défaut sur `http://localhost:8080`.

## 7. Structure du projet

```
src/main/java/com/bank/
├── controller/     # Contrôleurs REST (endpoints)
├── service/        # Logique métier
├── repository/     # Accès aux données via JdbcTemplate
├── model/          # Entités (Account, Transaction)
└── enums/          # AccountType, TransactionType
```

## 8. Git

- **Ne pas versionner** : `.env`, fichiers de build (`target/`), fichiers IDE locaux.
- **À versionner** : `.env.example`, sert de modèle de configuration pour les autres développeurs.
