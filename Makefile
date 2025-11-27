MVNW = ./mvnw
JAR = target/*.jar
DC = docker compose

help:
	@echo "Comandos disponíveis:"
	@echo "  make build     - Compila o projeto (sem testes)"
	@echo "  make run       - Executa o Spring Boot"
	@echo "  make jar       - Executa o .jar gerado"
	@echo "  make clean     - Limpa artefatos"
	@echo "  make up        - Sobe containers Docker"
	@echo "  make down      - Derruba containers"
	@echo "  make logs      - Mostra logs dos containers"
	@echo "  make restart   - Reinicia Docker Compose"

build:
	$(MVNW) clean package -DskipTests

run:
	$(MVNW) spring-boot:run

jar:
	java -jar $(JAR)

clean:
	$(MVNW) clean
	rm -rf target

up:
	$(DC) up -d

down:
	$(DC) down

logs:
	$(DC) logs -f

restart:
	$(DC) down
	$(DC) up -d
