# NeuroLib Core

Backend API for a book platform with JWT auth, file storage in S3, and AI-powered book recommendations using vector search (pgvector + Gemini).

## Tech Stack

- Java 21
- Spring Boot 4 (WebMVC, Security, Validation, Data JPA)
- PostgreSQL 17 + pgvector
- Liquibase (schema migrations)
- Spring AI
  - Google GenAI Chat
  - Google GenAI Embeddings
  - PgVector vector store
- AWS SDK v2 (S3)

## Prerequisites

- JDK 21
- Docker (for PostgreSQL + pgvector)
- Google Gemini API key
- AWS S3 bucket + credentials

## Environment Variables

Copy `.env.example` to `.env` and fill values:

```bash
cp .env.example .env
```

Important variables:

- Database: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- JWT: `JWT_SECRET`, `JWT_EXPIRATION_MS`, `JWT_REFRESH_EXPIRATION_MS`
- S3: `AWS_REGION`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_BUCKET`, `AWS_S3_PATH`
- AI: `GEMINI_API_KEY`, `CHAT_MODEL`, `EMBEDDINGS_MODEL`, `EMBEDDINGS_DIMENSIONS`

Recommended defaults:

- `CHAT_MODEL=gemini-2.5-flash`
- `EMBEDDINGS_MODEL=gemini-embedding-2`
- `EMBEDDINGS_DIMENSIONS=1536` (must match your embedding model output)
- `AWS_S3_PATH=https://s3.<region>.amazonaws.com` (for example `https://s3.eu-central-1.amazonaws.com`)

## Run with Docker (DB only)

Start PostgreSQL + pgvector:

```bash
docker compose up -d
```

This project uses:

```yaml
image: pgvector/pgvector:pg17
```
## Run Application

```bash
./gradlew bootRun
```

or build and run jar:

```bash
./gradlew build
java -jar build/libs/neurolib-0.0.1-SNAPSHOT.jar
```