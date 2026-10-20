
CREATE DATABASE IF NOT EXISTS wmw;
USE wmw;

-- ---------------------------------------------------------------------
-- 1. CARGO
-- ---------------------------------------------------------------------
CREATE TABLE cargo (
                       id INT NOT NULL AUTO_INCREMENT,
                       nome VARCHAR(45) NOT NULL,
                       descricao VARCHAR(255) NULL,
                       PRIMARY KEY (id),
                       UNIQUE KEY uq_cargo_nome (nome)
);

-- ---------------------------------------------------------------------
-- 2. PERMISSAO
-- `codigo` é o identificador estável usado no código do backend
-- (ex: 'ESTOQUE_EDITAR'). `descricao` é só para exibição na tela.
-- ---------------------------------------------------------------------
CREATE TABLE permissao (
                           id INT NOT NULL AUTO_INCREMENT,
                           codigo VARCHAR(60) NOT NULL,
                           descricao VARCHAR(255) NOT NULL,
                           PRIMARY KEY (id),
                           UNIQUE KEY uq_permissao_codigo (codigo)
);

-- ---------------------------------------------------------------------
-- 3. USUARIO
-- created_by/updated_by são auto-relacionamento e precisam ser nullable:
-- o primeiro usuário não tem quem o criou.
-- (v03) O cargo do usuário deixou de ser uma coluna aqui: a relação
-- usuário <-> cargo agora é N:N e fica em usuario_cargo (tabela 13).
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
                         id INT NOT NULL AUTO_INCREMENT,
                         nome VARCHAR(45) NOT NULL,
                         email VARCHAR(255) NOT NULL,
                         senha VARCHAR(255) NOT NULL,
                         ativo TINYINT(1) NOT NULL DEFAULT 1,
                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         created_by INT NULL,
                         updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         updated_by INT NULL,
                         PRIMARY KEY (id),
                         UNIQUE KEY uq_usuario_email (email),
                         CONSTRAINT fk_usuario_created_by FOREIGN KEY (created_by) REFERENCES usuario (id),
                         CONSTRAINT fk_usuario_updated_by FOREIGN KEY (updated_by) REFERENCES usuario (id)
);

-- ---------------------------------------------------------------------
-- 4. PERMISSAO_CARGO
-- ---------------------------------------------------------------------
CREATE TABLE permissao_cargo (
                                 id INT NOT NULL AUTO_INCREMENT,
                                 permissao_id INT NOT NULL,
                                 cargo_id INT NOT NULL,
                                 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 created_by INT NOT NULL,
                                 PRIMARY KEY (id),
                                 UNIQUE KEY uq_permissao_cargo (permissao_id, cargo_id),
                                 KEY idx_permissao_cargo_cargo (cargo_id),
                                 KEY idx_permissao_cargo_permissao (permissao_id),
                                 CONSTRAINT fk_permissao_cargo_permissao FOREIGN KEY (permissao_id) REFERENCES permissao (id),
                                 CONSTRAINT fk_permissao_cargo_cargo FOREIGN KEY (cargo_id) REFERENCES cargo (id),
                                 CONSTRAINT fk_permissao_cargo_criado FOREIGN KEY (created_by) REFERENCES usuario (id)
);

-- ---------------------------------------------------------------------
-- 5. INSUMO
-- ---------------------------------------------------------------------
CREATE TABLE insumo (
                        id INT NOT NULL AUTO_INCREMENT,
                        nome VARCHAR(100) NOT NULL,
                        unidade_medida ENUM('GRAMA','ML','UNIDADE') NOT NULL,
                        quantidade_atual DECIMAL(12,3) NOT NULL DEFAULT 0,
                        ativo TINYINT(1) NOT NULL DEFAULT 1,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        created_by INT NOT NULL,
                        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        updated_by INT NULL,
                        PRIMARY KEY (id),
                        UNIQUE KEY uq_insumo_nome (nome),
                        CONSTRAINT fk_insumo_created_by FOREIGN KEY (created_by) REFERENCES usuario (id),
                        CONSTRAINT fk_insumo_updated_by FOREIGN KEY (updated_by) REFERENCES usuario (id)
);

-- ---------------------------------------------------------------------
-- 6. PRODUTO
-- ---------------------------------------------------------------------
CREATE TABLE produto (
                         id INT NOT NULL AUTO_INCREMENT,
                         nome VARCHAR(255) NOT NULL,
                         ativo TINYINT(1) NOT NULL DEFAULT 1,
                         tamanho ENUM('BROTO', 'GRANDE', 'TREM') NOT NULL,
                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         created_by INT NOT NULL,
                         updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         updated_by INT NULL,
                         PRIMARY KEY (id),
                         UNIQUE KEY uq_produto_nome_tamanho (nome, tamanho),
                         CONSTRAINT fk_produto_created_by FOREIGN KEY (created_by) REFERENCES usuario (id),
                         CONSTRAINT fk_produto_updated_by FOREIGN KEY (updated_by) REFERENCES usuario (id)
);

-- ---------------------------------------------------------------------
-- 7. PRODUTO_INSUMO (receita padrão)
-- Auditoria "de estado atual" aqui (quem/quando criou ou editou por
-- último a combinação). O HISTÓRICO de mudanças (o que mudou, edição a
-- edição, inclusive remoções) fica em produto_insumo_historico, abaixo.
-- ---------------------------------------------------------------------
CREATE TABLE produto_insumo (
                                id INT NOT NULL AUTO_INCREMENT,
                                produto_id INT NOT NULL,
                                insumo_id INT NOT NULL,
                                quantidade DECIMAL(12,3) NOT NULL,
                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                created_by INT NOT NULL,
                                updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                updated_by INT NULL,
                                PRIMARY KEY (id),
                                UNIQUE KEY uq_produto_insumo (produto_id, insumo_id),
                                KEY idx_produto_insumo_insumo (insumo_id),
                                CONSTRAINT chk_produto_insumo_qtd CHECK (quantidade > 0),
                                CONSTRAINT fk_produto_insumo_produto FOREIGN KEY (produto_id) REFERENCES produto (id),
                                CONSTRAINT fk_produto_insumo_insumo FOREIGN KEY (insumo_id) REFERENCES insumo (id),
                                CONSTRAINT fk_produto_insumo_created_by FOREIGN KEY (created_by) REFERENCES usuario (id),
                                CONSTRAINT fk_produto_insumo_updated_by FOREIGN KEY (updated_by) REFERENCES usuario (id)
);

-- ---------------------------------------------------------------------
-- 8. PRODUTO_INSUMO_HISTORICO (log de eventos, append-only)
-- Responde: "o que mudou na receita da pizza X, quem mudou, e quando",
-- inclusive para insumos que já foram removidos da receita atual.
-- A validação de consistência entre campos fica a cargo do backend.
-- ---------------------------------------------------------------------
CREATE TABLE produto_insumo_historico (
                                          id BIGINT NOT NULL AUTO_INCREMENT,
                                          produto_id INT NOT NULL,
                                          insumo_id INT NOT NULL,
                                          acao ENUM('ADICIONADO', 'QUANTIDADE_ALTERADA', 'REMOVIDO') NOT NULL,
                                          quantidade_anterior DECIMAL(12,3) NULL,
                                          quantidade_nova DECIMAL(12,3) NULL,
                                          usuario_id INT NOT NULL,
                                          data_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          PRIMARY KEY (id),
                                          KEY idx_pih_produto_data (produto_id, data_hora),
                                          KEY idx_pih_insumo (insumo_id),
                                          CONSTRAINT fk_pih_produto FOREIGN KEY (produto_id) REFERENCES produto (id),
                                          CONSTRAINT fk_pih_insumo FOREIGN KEY (insumo_id) REFERENCES insumo (id),
                                          CONSTRAINT fk_pih_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

-- ---------------------------------------------------------------------
-- 9. VENDA
-- ---------------------------------------------------------------------
CREATE TABLE venda (
                       id INT NOT NULL AUTO_INCREMENT,
                       data_hora DATETIME NOT NULL,
                       origem ENUM('KEETA','99','IFOOD', 'NAO_ESPECIFICADO') NOT NULL,
                       status ENUM('ATIVA', 'CANCELADA') NOT NULL DEFAULT 'ATIVA',
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       created_by INT NOT NULL,
                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       updated_by INT NULL,
                       PRIMARY KEY (id),
                       KEY idx_venda_data_hora (data_hora),
                       CONSTRAINT fk_venda_created_by FOREIGN KEY (created_by) REFERENCES usuario (id),
                       CONSTRAINT fk_venda_updated_by FOREIGN KEY (updated_by) REFERENCES usuario (id)
);

-- ---------------------------------------------------------------------
-- 10. ITEM_VENDA
-- ---------------------------------------------------------------------
CREATE TABLE item_venda (
                            id INT NOT NULL AUTO_INCREMENT,
                            venda_id INT NOT NULL,
                            produto_id INT NOT NULL,
                            quantidade INT NOT NULL,
                            PRIMARY KEY (id),
                            KEY idx_item_venda_venda (venda_id),
                            KEY idx_item_venda_produto (produto_id),
                            CONSTRAINT chk_item_venda_qtd CHECK (quantidade > 0),
                            CONSTRAINT fk_item_venda_venda FOREIGN KEY (venda_id) REFERENCES venda (id),
                            CONSTRAINT fk_item_venda_produto FOREIGN KEY (produto_id) REFERENCES produto (id)
);

-- ---------------------------------------------------------------------
-- 11. ITEM_VENDA_INSUMO
-- Ao criar um item_venda, o backend copia aqui as linhas de
-- produto_insumo (a receita padrão NAQUELE momento). Se o funcionário
-- não editar nada, fica idêntico ao padrão; se editar, edita aqui.
-- Isso é o que de fato foi descontado do estoque para aquela venda —
-- fica desacoplado de mudanças futuras na receita padrão.
-- ---------------------------------------------------------------------
CREATE TABLE item_venda_insumo (
                                   id INT NOT NULL AUTO_INCREMENT,
                                   item_venda_id INT NOT NULL,
                                   insumo_id INT NOT NULL,
                                   quantidade DECIMAL(12,3) NOT NULL,
                                   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   created_by INT NOT NULL,
                                   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   updated_by INT NULL,
                                   PRIMARY KEY (id),
                                   UNIQUE KEY uq_item_venda_insumo (item_venda_id, insumo_id),
                                   KEY idx_ivi_insumo (insumo_id),
                                   CONSTRAINT chk_ivi_qtd CHECK (quantidade > 0),
                                   CONSTRAINT fk_ivi_item_venda FOREIGN KEY (item_venda_id) REFERENCES item_venda (id),
                                   CONSTRAINT fk_ivi_insumo FOREIGN KEY (insumo_id) REFERENCES insumo (id),
                                   CONSTRAINT fk_ivi_created_by FOREIGN KEY (created_by) REFERENCES usuario (id),
                                   CONSTRAINT fk_ivi_updated_by FOREIGN KEY (updated_by) REFERENCES usuario (id)
);

-- ---------------------------------------------------------------------
-- 12. MOVIMENTACAO_ESTOQUE (ledger append-only: entrada, saída, qualquer motivo)
-- ---------------------------------------------------------------------
CREATE TABLE movimentacao_estoque (
                                      id BIGINT NOT NULL AUTO_INCREMENT,
                                      insumo_id INT NOT NULL,
                                      item_venda_insumo_id INT NULL,
                                      usuario_id INT NOT NULL,
                                      tipo ENUM('ENTRADA','SAIDA') NOT NULL,
                                      motivo ENUM('VENDA','COMPRA','QUEBRA','AJUSTE','OUTRO','CANCELAMENTO_VENDA') NOT NULL,
                                      quantidade DECIMAL(12,3) NOT NULL,
                                      observacao VARCHAR(255) NULL,
                                      data_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (id),
                                      KEY idx_mov_insumo_data (insumo_id, data_hora),
                                      KEY idx_mov_usuario_data (usuario_id, data_hora),
                                      KEY idx_mov_item_venda_insumo (item_venda_insumo_id),
                                      UNIQUE KEY uq_mov_item_venda_insumo_tipo (item_venda_insumo_id, tipo),
                                      CONSTRAINT chk_mov_qtd_positiva CHECK (quantidade > 0),
                                      CONSTRAINT chk_mov_venda_consistente CHECK (
                                          (motivo IN ('VENDA', 'CANCELAMENTO_VENDA') AND item_venda_insumo_id IS NOT NULL) OR
                                          (motivo NOT IN ('VENDA', 'CANCELAMENTO_VENDA') AND item_venda_insumo_id IS NULL)
                                          ),
                                      CONSTRAINT fk_mov_insumo FOREIGN KEY (insumo_id) REFERENCES insumo (id),
                                      CONSTRAINT fk_mov_item_venda_insumo FOREIGN KEY (item_venda_insumo_id) REFERENCES item_venda_insumo (id),
                                      CONSTRAINT fk_mov_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

-- ---------------------------------------------------------------------
-- 13. USUARIO_CARGO (novo na v03)
-- Liga usuário a cargo (N:N): um usuário pode ter vários cargos.
-- As permissões efetivas do usuário são a união das permissões de todos
-- os cargos dele (via permissao_cargo). Garantir que todo usuário ativo
-- tenha ao menos um cargo fica a cargo do backend.
-- ---------------------------------------------------------------------
CREATE TABLE usuario_cargo (
                               id INT NOT NULL AUTO_INCREMENT,
                               usuario_id INT NOT NULL,
                               cargo_id INT NOT NULL,
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               created_by INT NOT NULL,
                               PRIMARY KEY (id),
                               UNIQUE KEY uq_usuario_cargo (usuario_id, cargo_id),
                               KEY idx_usuario_cargo_cargo (cargo_id),
                               CONSTRAINT fk_usuario_cargo_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
                               CONSTRAINT fk_usuario_cargo_cargo FOREIGN KEY (cargo_id) REFERENCES cargo (id),
                               CONSTRAINT fk_usuario_cargo_criado FOREIGN KEY (created_by) REFERENCES usuario (id)
);

-- ---------------------------------------------------------------------
-- 14. ALERTA_ESTOQUE_BAIXO (novo na v03)
-- Configuração de alerta de quantidade baixa: cada usuário define, por
-- insumo, abaixo de qual quantidade quer ser alertado. Guarda só a
-- CONFIGURAÇÃO; a checagem (insumo.quantidade_atual <= quantidade_minima)
-- é feita pelo backend na consulta. `quantidade_minima` está na mesma
-- unidade de medida do insumo.
-- ---------------------------------------------------------------------
CREATE TABLE alerta_estoque_baixo (
                                      id INT NOT NULL AUTO_INCREMENT,
                                      usuario_id INT NOT NULL,
                                      insumo_id INT NOT NULL,
                                      quantidade_minima DECIMAL(12,3) NOT NULL,
                                      ativo TINYINT(1) NOT NULL DEFAULT 1,
                                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      PRIMARY KEY (id),
                                      UNIQUE KEY uq_alerta_usuario_insumo (usuario_id, insumo_id),
                                      KEY idx_alerta_insumo (insumo_id),
                                      CONSTRAINT chk_alerta_qtd_minima CHECK (quantidade_minima >= 0),
                                      CONSTRAINT fk_alerta_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
                                      CONSTRAINT fk_alerta_insumo FOREIGN KEY (insumo_id) REFERENCES insumo (id)
);