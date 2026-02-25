CREATE TABLE tipo_sala (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(20) NOT NULL UNIQUE,
    percentual_adicional DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    percentual_reembolso DECIMAL(5,2) NOT NULL DEFAULT 60.00,
    descricao VARCHAR(200),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_tipo_sala_nome CHECK (nome IN ('Standard', 'Premium', 'VIP'))
);

CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    corporativo BOOLEAN NOT NULL DEFAULT FALSE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE recursos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(200),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE salas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    tipo_sala_id INT NOT NULL,
    capacidade INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tipo_sala_id) REFERENCES tipo_sala(id)
);

CREATE TABLE sala_recursos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sala_id INT NOT NULL,
    recurso_id INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sala_id) REFERENCES salas(id) ON DELETE CASCADE,
    FOREIGN KEY (recurso_id) REFERENCES recursos(id) ON DELETE CASCADE,
    UNIQUE KEY uk_sala_recurso (sala_id, recurso_id)
);

CREATE TABLE reservas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sala_id INT NOT NULL,
    cliente_id INT NOT NULL,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP NOT NULL,
    custo_calculado DECIMAL(10,2),
    status_reserva VARCHAR(20) DEFAULT 'Ativa',
    data_cancelamento TIMESTAMP NULL,
    valor_reembolso DECIMAL(10,2) NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sala_id) REFERENCES salas(id),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE,
    CONSTRAINT chk_status_reserva CHECK (status_reserva IN ('Ativa', 'Cancelada', 'Concluida')),
    CONSTRAINT chk_datas CHECK (data_fim > data_inicio)
);

CREATE INDEX IX_reservas_sala_periodo ON reservas(sala_id, data_inicio, data_fim);
CREATE INDEX IX_reservas_cliente ON reservas(cliente_id);
CREATE INDEX IX_reservas_periodo ON reservas(data_inicio, data_fim);
CREATE INDEX IX_reservas_status ON reservas(status_reserva);
CREATE INDEX IX_salas_tipo ON salas(tipo_sala_id);
CREATE INDEX IX_clientes_cpf ON clientes(cpf);

INSERT INTO tipo_sala (nome, percentual_adicional, percentual_reembolso, descricao) VALUES
('Standard', 0.00, 60.00, 'Sala básica com equipamentos essenciais'),
('Premium', 15.00, 40.00, 'Sala com recursos adicionais e maior conforto'),
('VIP', 30.00, 30.00, 'Sala premium com todos os recursos disponíveis');

INSERT INTO recursos (nome, descricao) VALUES
('Quadro Branco', 'Quadro branco para apresentações'),
('Projetor', 'Projetor multimídia'),
('Videoconferência', 'Sistema de videoconferência'),
('Ar-condicionado', 'Sistema de climatização'),
('Televisão', 'TV de grande porte'),
('Sistema de Som', 'Sistema de áudio profissional'),
('Wi-Fi Premium', 'Internet de alta velocidade'),
('Café e Água', 'Serviço de café e água');

INSERT INTO salas (codigo, tipo_sala_id, capacidade) VALUES
('A101', 1, 10),
('A102', 1, 8),
('B201', 2, 15),
('B202', 2, 12),
('C301', 3, 20),
('C302', 3, 18);

INSERT INTO sala_recursos (sala_id, recurso_id) VALUES
(1, 7),
(2, 1),
(2, 7),
(3, 1),
(3, 2),
(3, 4),
(3, 7),
(4, 1),
(4, 2),
(4, 4),
(4, 7),
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5), (5, 6), (5, 7), (5, 8),
(6, 1), (6, 2), (6, 3), (6, 4), (6, 5), (6, 6), (6, 7), (6, 8);

-- Dados fictícios inseridos exclusivamente para fins de teste, creditos: 4Devs
INSERT INTO clientes (nome, cpf, corporativo) VALUES
('Danilo João Castro', '802.521.302-19', FALSE),
('Mariane Sophie', '149.696.142-02', TRUE),
