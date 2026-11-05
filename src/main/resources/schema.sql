-- Execute em banco vazio antes de data.sql.

-- Funções dos usuários, associadas às permissões em permissao_cargo.
CREATE TABLE cargo (
    id INT AUTO_INCREMENT,
    nome VARCHAR(45) NOT NULL,
    descricao VARCHAR(255) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_cargo_nome UNIQUE (nome)
);

-- Permissões identificadas por código, para associação aos cargos.
CREATE TABLE permissao (
    id INT AUTO_INCREMENT,
    codigo VARCHAR(60) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_permissao_codigo UNIQUE (codigo)
);

-- Credenciais e situação do usuário, o primeiro cadastro pode não ter autor.
CREATE TABLE usuario (
    id INT AUTO_INCREMENT,
    nome VARCHAR(45) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by INT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by INT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_usuario_email UNIQUE (email),
    CONSTRAINT fk_usuario_created_by FOREIGN KEY (created_by) REFERENCES usuario (id),
    CONSTRAINT fk_usuario_updated_by FOREIGN KEY (updated_by) REFERENCES usuario (id)
);

-- Relaciona permissões aos cargos, sem repetir a mesma combinação.
CREATE TABLE permissao_cargo (
    id INT AUTO_INCREMENT,
    permissao_id INT NOT NULL,
    cargo_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_permissao_cargo UNIQUE (permissao_id, cargo_id),
    CONSTRAINT fk_permissao_cargo_permissao FOREIGN KEY (permissao_id) REFERENCES permissao (id),
    CONSTRAINT fk_permissao_cargo_cargo FOREIGN KEY (cargo_id) REFERENCES cargo (id),
    CONSTRAINT fk_permissao_cargo_criado FOREIGN KEY (created_by) REFERENCES usuario (id)
);

-- Saldo em gramas ou unidades e percentual de perda aplicado às entradas.
CREATE TABLE insumo (
    id INT AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    unidade_medida VARCHAR(32) NOT NULL CHECK (unidade_medida IN ('GRAMA','UNIDADE')),
    quantidade_atual DECIMAL(12,3) NOT NULL DEFAULT 0 CONSTRAINT chk_insumo_saldo CHECK (quantidade_atual >= 0),
    percentual_perda INT NOT NULL CONSTRAINT chk_insumo_perda CHECK (percentual_perda >= 0 AND percentual_perda < 100),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by INT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_insumo_nome UNIQUE (nome),
    CONSTRAINT fk_insumo_created_by FOREIGN KEY (created_by) REFERENCES usuario (id),
    CONSTRAINT fk_insumo_updated_by FOREIGN KEY (updated_by) REFERENCES usuario (id)
);

-- Cadastro do cardápio, identificado por nome e tamanho.
CREATE TABLE produto (
    id INT AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    tipo VARCHAR(32) NOT NULL CONSTRAINT chk_produto_tipo CHECK (tipo IN ('TRADICIONAL','BEIRUTE','TREM')),
    tamanho INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by INT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_produto_nome_tamanho UNIQUE (nome, tamanho),
    CONSTRAINT fk_produto_created_by FOREIGN KEY (created_by) REFERENCES usuario (id),
    CONSTRAINT fk_produto_updated_by FOREIGN KEY (updated_by) REFERENCES usuario (id)
);

-- Receita padrão: quantidade de cada insumo necessária por unidade do produto.
CREATE TABLE produto_insumo (
    id INT AUTO_INCREMENT,
    produto_id INT NOT NULL,
    insumo_id INT NOT NULL,
    quantidade DECIMAL(12,3) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by INT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_produto_insumo UNIQUE (produto_id, insumo_id),
    CONSTRAINT chk_produto_insumo_qtd CHECK (quantidade > 0),
    CONSTRAINT fk_produto_insumo_produto FOREIGN KEY (produto_id) REFERENCES produto (id),
    CONSTRAINT fk_produto_insumo_insumo FOREIGN KEY (insumo_id) REFERENCES insumo (id),
    CONSTRAINT fk_produto_insumo_created_by FOREIGN KEY (created_by) REFERENCES usuario (id),
    CONSTRAINT fk_produto_insumo_updated_by FOREIGN KEY (updated_by) REFERENCES usuario (id)
);

-- Alterações da receita, com quantidades anteriores, novas e usuário responsável.
CREATE TABLE produto_insumo_historico (
    id BIGINT AUTO_INCREMENT,
    produto_id INT NOT NULL,
    insumo_id INT NOT NULL,
    acao VARCHAR(32) NOT NULL CHECK (acao IN ('ADICIONADO', 'QUANTIDADE_ALTERADA', 'REMOVIDO')),
    quantidade_anterior DECIMAL(12,3) NULL,
    quantidade_nova DECIMAL(12,3) NULL,
    usuario_id INT NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_pih_produto FOREIGN KEY (produto_id) REFERENCES produto (id),
    CONSTRAINT fk_pih_insumo FOREIGN KEY (insumo_id) REFERENCES insumo (id),
    CONSTRAINT fk_pih_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

-- Estrutura de vendas comerciais preservada, TXT/manual utiliza importacao.
CREATE TABLE venda (
    id INT AUTO_INCREMENT,
    data_hora TIMESTAMP NOT NULL,
    origem VARCHAR(32) NOT NULL CHECK (origem IN ('KEETA','99','IFOOD', 'NAO_ESPECIFICADO')),
    status VARCHAR(32) NOT NULL DEFAULT 'ATIVA' CHECK (status IN ('ATIVA', 'CANCELADA')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by INT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_venda_created_by FOREIGN KEY (created_by) REFERENCES usuario (id),
    CONSTRAINT fk_venda_updated_by FOREIGN KEY (updated_by) REFERENCES usuario (id)
);

-- Produtos e quantidades pertencentes a uma venda comercial.
CREATE TABLE item_venda (
    id INT AUTO_INCREMENT,
    venda_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_item_venda_qtd CHECK (quantidade > 0),
    CONSTRAINT fk_item_venda_venda FOREIGN KEY (venda_id) REFERENCES venda (id),
    CONSTRAINT fk_item_venda_produto FOREIGN KEY (produto_id) REFERENCES produto (id)
);

-- Composição do item vendido, independente de alterações futuras na receita.
CREATE TABLE item_venda_insumo (
    id INT AUTO_INCREMENT,
    item_venda_id INT NOT NULL,
    insumo_id INT NOT NULL,
    quantidade DECIMAL(12,3) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by INT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_item_venda_insumo UNIQUE (item_venda_id, insumo_id),
    CONSTRAINT chk_ivi_qtd CHECK (quantidade > 0),
    CONSTRAINT fk_ivi_item_venda FOREIGN KEY (item_venda_id) REFERENCES item_venda (id),
    CONSTRAINT fk_ivi_insumo FOREIGN KEY (insumo_id) REFERENCES insumo (id),
    CONSTRAINT fk_ivi_created_by FOREIGN KEY (created_by) REFERENCES usuario (id),
    CONSTRAINT fk_ivi_updated_by FOREIGN KEY (updated_by) REFERENCES usuario (id)
);

-- Operação TXT/manual, com data de referência, responsável e estado da confirmação.
CREATE TABLE importacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    origem VARCHAR(16) NOT NULL DEFAULT 'TXT' CHECK (origem IN ('TXT','MANUAL')),
    nome_arquivo VARCHAR(255) NULL,
    data_referencia DATE NOT NULL,
    created_at DATETIME(6) NOT NULL,
    usuario_id INT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PREVIA',
    CONSTRAINT chk_importacao_status CHECK (status IN ('PREVIA','PROCESSANDO','CONCLUIDO')),
    CONSTRAINT fk_importacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

-- Produto e quantidade da operação, linhas inválidas guardam o motivo do erro.
CREATE TABLE importacao_linha (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    importacao_id BIGINT NOT NULL,
    numero_linha INT NOT NULL,
    nome_atual VARCHAR(255) NULL,
    produto_id INT NULL,
    quantidade_atual INT NULL,
    status VARCHAR(32) NOT NULL,
    erro_codigo VARCHAR(60) NULL,
    erro_mensagem VARCHAR(255) NULL,
    processado_em DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    CONSTRAINT uq_il_numero UNIQUE (importacao_id, numero_linha),
    CONSTRAINT chk_il_numero CHECK (numero_linha > 0),
    CONSTRAINT chk_il_status CHECK (status IN ('INVALIDA','PENDENTE','PROCESSADA')),
    CONSTRAINT fk_il_importacao FOREIGN KEY (importacao_id) REFERENCES importacao (id),
    CONSTRAINT fk_il_produto FOREIGN KEY (produto_id) REFERENCES produto (id)
);

-- Totais previstos e efetivados por insumo da linha, incluindo personalizações.
CREATE TABLE importacao_linha_consumo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    linha_id BIGINT NOT NULL,
    insumo_id INT NOT NULL,
    quantidade_prevista DECIMAL(12,3) NOT NULL,
    quantidade_efetivada DECIMAL(12,3) NULL,
    aviso VARCHAR(255) NULL,
    CONSTRAINT uq_ilc_linha_insumo UNIQUE (linha_id, insumo_id),
    CONSTRAINT uq_ilc_id_insumo UNIQUE (id, insumo_id),
    CONSTRAINT chk_ilc_prevista CHECK (quantidade_prevista > 0),
    CONSTRAINT chk_ilc_efetivada CHECK (
        quantidade_efetivada IS NULL OR
        (quantidade_efetivada >= 0 AND quantidade_efetivada <= quantidade_prevista)
    ),
    CONSTRAINT fk_ilc_linha FOREIGN KEY (linha_id) REFERENCES importacao_linha (id),
    CONSTRAINT fk_ilc_insumo FOREIGN KEY (insumo_id) REFERENCES insumo (id)
);

-- Entradas e saídas que explicam o saldo, vinculadas ao consumo quando aplicável.
-- Quantidade zero é permitida apenas na entrada de saldo inicial.
CREATE TABLE movimentacao_estoque (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    insumo_id INT NOT NULL,
    item_venda_insumo_id INT NULL,
    importacao_linha_consumo_id BIGINT NULL,
    usuario_id INT NOT NULL,
    tipo VARCHAR(32) NOT NULL CHECK (tipo IN ('ENTRADA','SAIDA')),
    motivo VARCHAR(32) NOT NULL CHECK (
        motivo IN ('VENDA','COMPRA','QUEBRA','AJUSTE','OUTRO','CANCELAMENTO_VENDA','CONSUMO_PRODUTO','SALDO_INICIAL')
    ),
    quantidade DECIMAL(12,3) NOT NULL,
    observacao VARCHAR(255) NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_mov_item_venda_insumo_tipo UNIQUE (item_venda_insumo_id, tipo),
    CONSTRAINT chk_mov_quantidade CHECK (
        quantidade > 0 OR (quantidade = 0 AND motivo = 'SALDO_INICIAL' AND tipo = 'ENTRADA')
    ),
    CONSTRAINT chk_mov_saldo_inicial CHECK (motivo <> 'SALDO_INICIAL' OR tipo = 'ENTRADA'),
    CONSTRAINT chk_mov_consumo CHECK (
        (motivo = 'CONSUMO_PRODUTO' AND tipo = 'SAIDA' AND importacao_linha_consumo_id IS NOT NULL)
        OR (motivo <> 'CONSUMO_PRODUTO' AND importacao_linha_consumo_id IS NULL)
    ),
    CONSTRAINT uq_mov_consumo_tipo UNIQUE (importacao_linha_consumo_id, tipo),
    CONSTRAINT fk_mov_consumo_insumo FOREIGN KEY (importacao_linha_consumo_id, insumo_id)
        REFERENCES importacao_linha_consumo (id, insumo_id),
    CONSTRAINT chk_mov_venda_consistente CHECK (
        (motivo IN ('VENDA', 'CANCELAMENTO_VENDA') AND item_venda_insumo_id IS NOT NULL)
        OR (motivo NOT IN ('VENDA', 'CANCELAMENTO_VENDA') AND item_venda_insumo_id IS NULL)
    ),
    CONSTRAINT fk_mov_item_venda_insumo FOREIGN KEY (item_venda_insumo_id)
        REFERENCES item_venda_insumo (id),
    CONSTRAINT fk_mov_insumo FOREIGN KEY (insumo_id) REFERENCES insumo (id),
    CONSTRAINT fk_mov_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

-- Associa usuários a um ou mais cargos, sem repetir a mesma combinação.
CREATE TABLE usuario_cargo (
    id INT AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    cargo_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_usuario_cargo UNIQUE (usuario_id, cargo_id),
    CONSTRAINT fk_usuario_cargo_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT fk_usuario_cargo_cargo FOREIGN KEY (cargo_id) REFERENCES cargo (id),
    CONSTRAINT fk_usuario_cargo_criado FOREIGN KEY (created_by) REFERENCES usuario (id)
);

-- Limite de estoque baixo por cargo e insumo, guarda a configuração do alerta.
CREATE TABLE alerta_estoque_baixo (
    id INT AUTO_INCREMENT,
    cargo_id INT NOT NULL,
    insumo_id INT NOT NULL,
    quantidade_minima DECIMAL(12,3) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_alerta_cargo_insumo UNIQUE (cargo_id, insumo_id),
    CONSTRAINT chk_alerta_qtd_minima CHECK (quantidade_minima >= 0),
    CONSTRAINT fk_alerta_cargo FOREIGN KEY (cargo_id) REFERENCES cargo (id),
    CONSTRAINT fk_alerta_insumo FOREIGN KEY (insumo_id) REFERENCES insumo (id)
);

-- Índices para consultas por vínculos, período e estado das operações.
CREATE INDEX idx_permissao_cargo_cargo ON permissao_cargo (cargo_id);
CREATE INDEX idx_permissao_cargo_permissao ON permissao_cargo (permissao_id);
CREATE INDEX idx_produto_insumo_insumo ON produto_insumo (insumo_id);
CREATE INDEX idx_pih_produto_data ON produto_insumo_historico (produto_id, data_hora);
CREATE INDEX idx_pih_insumo ON produto_insumo_historico (insumo_id);
CREATE INDEX idx_venda_data_hora ON venda (data_hora);
CREATE INDEX idx_item_venda_venda ON item_venda (venda_id);
CREATE INDEX idx_item_venda_produto ON item_venda (produto_id);
CREATE INDEX idx_ivi_insumo ON item_venda_insumo (insumo_id);
CREATE INDEX idx_mov_insumo_data ON movimentacao_estoque (insumo_id, data_hora);
CREATE INDEX idx_mov_usuario_data ON movimentacao_estoque (usuario_id, data_hora);
CREATE INDEX idx_mov_item_venda_insumo ON movimentacao_estoque (item_venda_insumo_id);
CREATE INDEX idx_usuario_cargo_cargo ON usuario_cargo (cargo_id);
CREATE INDEX idx_alerta_insumo ON alerta_estoque_baixo (insumo_id);
CREATE INDEX idx_importacao_historico ON importacao (status, id);
CREATE INDEX idx_il_resultado ON importacao_linha (importacao_id, status);
