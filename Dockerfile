FROM gradle:8.10-jdk21

# Troca para root temporariamente para poder instalar pacotes
USER root

# Instala o cliente do PostgreSQL (pg_isready, psql, etc.)
RUN apt-get update && \
    apt-get install -y postgresql-client && \
    rm -rf /var/lib/apt/lists/*

# Volta para o usuário padrão "gradle"
USER gradle

# Define o diretório de trabalho (mesmo do docker-compose)
WORKDIR /app
