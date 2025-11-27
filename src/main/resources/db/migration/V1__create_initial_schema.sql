-- 1. Tabela de Usuários (Base para Cliente, Admin e Operador) [cite: 17-20]
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL -- 'ADMIN', 'OPERATOR', 'CLIENT'
);

-- 2. Tabela de Prestadores (Providers) [cite: 6]
CREATE TABLE providers (
                           id BIGSERIAL PRIMARY KEY,
                           user_id BIGINT UNIQUE REFERENCES users(id), -- Vínculo com usuário
                           slot_duration_minutes INTEGER DEFAULT 60, -- Configuração de duração [cite: 11]
                           active BOOLEAN DEFAULT TRUE
);

-- 3. Regras de Agendamento (Schedule Rules) [cite: 8]
CREATE TABLE schedule_rules (
                                id BIGSERIAL PRIMARY KEY,
                                provider_id BIGINT REFERENCES providers(id),
                                day_of_week INTEGER NOT NULL, -- 0=Domingo, 1=Segunda...
                                start_time TIME NOT NULL,
                                end_time TIME NOT NULL,
                                break_start TIME,
                                break_end TIME
);

-- 4. Feriados (Holidays) [cite: 10]
CREATE TABLE holidays (
                          id BIGSERIAL PRIMARY KEY,
                          date DATE NOT NULL UNIQUE,
                          name VARCHAR(255)
);

-- 5. Bloqueios (Blocks) [cite: 9]
CREATE TABLE blocks (
                        id BIGSERIAL PRIMARY KEY,
                        provider_id BIGINT REFERENCES providers(id),
                        start_time TIMESTAMP NOT NULL,
                        end_time TIMESTAMP NOT NULL,
                        reason VARCHAR(255)
);

-- 6. Agendamentos (Appointments) [cite: 7]
CREATE TABLE appointments (
                              id BIGSERIAL PRIMARY KEY,
                              client_id BIGINT REFERENCES users(id), -- Quem agendou (Client) [cite: 20]
                              provider_id BIGINT REFERENCES providers(id), -- Com quem foi
                              start_time TIMESTAMP NOT NULL,
                              end_time TIMESTAMP NOT NULL,
                              status VARCHAR(50) NOT NULL, -- 'CREATED', 'CONFIRMED', 'CANCELED' [cite: 27]
                              notes TEXT
);