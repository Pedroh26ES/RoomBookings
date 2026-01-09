-- 1. CRIAÇÃO DAS TABELAS 
-- =====================================================

-- Tabela de tipos de sala
CREATE TABLE tipo_sala (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(20) NOT NULL UNIQUE,
    percentual_adicional DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    percentual_reembolso DECIMAL(5,2) NOT NULL DEFAULT 60.00,
    descricao VARCHAR(200),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_tipo_sala_nome CHECK (nome IN ('Standard', 'Premium', 'VIP'))
);

-- Tabela de clientes
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    corporativo BOOLEAN NOT NULL DEFAULT FALSE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de recursos
CREATE TABLE recursos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(200),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de salas
CREATE TABLE salas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    tipo_sala_id INT NOT NULL,
    capacidade INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tipo_sala_id) REFERENCES tipo_sala(id)
);

-- Tabela de relacionamento sala-recursos
CREATE TABLE sala_recursos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sala_id INT NOT NULL,
    recurso_id INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sala_id) REFERENCES salas(id) ON DELETE CASCADE,
    FOREIGN KEY (recurso_id) REFERENCES recursos(id) ON DELETE CASCADE,
    UNIQUE KEY uk_sala_recurso (sala_id, recurso_id)
);

-- Tabela de reservas
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

-- 2. CRIAÇÃO DOS ÍNDICES
-- =====================================================
CREATE INDEX IX_reservas_sala_periodo ON reservas(sala_id, data_inicio, data_fim);
CREATE INDEX IX_reservas_cliente ON reservas(cliente_id);
CREATE INDEX IX_reservas_periodo ON reservas(data_inicio, data_fim);
CREATE INDEX IX_reservas_status ON reservas(status_reserva);
CREATE INDEX IX_salas_tipo ON salas(tipo_sala_id);
CREATE INDEX IX_clientes_cpf ON clientes(cpf);

-- 3. INSERÇÃO DE DADOS INICIAIS
-- =====================================================

-- Inserir tipos de sala
INSERT INTO tipo_sala (nome, percentual_adicional, percentual_reembolso, descricao) VALUES
('Standard', 0.00, 60.00, 'Sala básica com equipamentos essenciais'),
('Premium', 15.00, 40.00, 'Sala com recursos adicionais e maior conforto'),
('VIP', 30.00, 30.00, 'Sala premium com todos os recursos disponíveis');

-- Inserir recursos disponíveis
INSERT INTO recursos (nome, descricao) VALUES
('Quadro Branco', 'Quadro branco para apresentações'),
('Projetor', 'Projetor multimídia'),
('Videoconferência', 'Sistema de videoconferência'),
('Ar-condicionado', 'Sistema de climatização'),
('Televisão', 'TV de grande porte'),
('Sistema de Som', 'Sistema de áudio profissional'),
('Wi-Fi Premium', 'Internet de alta velocidade'),
('Café e Água', 'Serviço de café e água');

-- Inserir algumas salas de exemplo
INSERT INTO salas (codigo, tipo_sala_id, capacidade) VALUES
('A101', 1, 10),  -- Standard
('A102', 1, 8),   -- Standard
('B201', 2, 15),  -- Premium
('B202', 2, 12),  -- Premium
('C301', 3, 20),  -- VIP
('C302', 3, 18);  -- VIP

-- Associar recursos às salas Standard (ID 1 e 2)
INSERT INTO sala_recursos (sala_id, recurso_id) VALUES
-- Sala A101 (Standard) - Wi-Fi Premium sempre
(1, 7),
-- Sala A102 (Standard) - Wi-Fi Premium + Quadro Branco
(2, 1),
(2, 7);

-- Associar recursos às salas Premium (ID 3 e 4)
INSERT INTO sala_recursos (sala_id, recurso_id) VALUES
-- Sala B201 (Premium)
(3, 1), -- Quadro Branco
(3, 2), -- Projetor
(3, 4), -- Ar-condicionado
(3, 7), -- Wi-Fi Premium
-- Sala B202 (Premium)
(4, 1), -- Quadro Branco
(4, 2), -- Projetor
(4, 4), -- Ar-condicionado
(4, 7); -- Wi-Fi Premium

-- Associar recursos às salas VIP (ID 5 e 6) - todos os recursos
INSERT INTO sala_recursos (sala_id, recurso_id) VALUES
-- Sala C301 (VIP) - todos os recursos
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5), (5, 6), (5, 7), (5, 8),
-- Sala C302 (VIP) - todos os recursos
(6, 1), (6, 2), (6, 3), (6, 4), (6, 5), (6, 6), (6, 7), (6, 8);

-- Inserir alguns clientes de exemplo
INSERT INTO clientes (nome, cpf, corporativo) VALUES
('João Silva', '123.456.789-01', FALSE),
('Maria Santos', '987.654.321-02', TRUE),
('Carlos Oliveira', '456.789.123-03', FALSE),
('Ana Costa', '321.654.987-04', TRUE),
('Almeida', '789.123.456-05', FALSE);