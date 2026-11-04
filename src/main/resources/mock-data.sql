-- ================================================================
-- PRIMARELLA - DADOS PARA DEMONSTRAÇÃO
-- Executar manualmente no BANCO DE DESENVOLVIMENTO.
-- (não é executado automaticamente pelo Spring.)
-- ================================================================

START TRANSACTION;

-- 1) Usuário técnico da demonstração (necessário pelas FKs created_by).
-- Login criado na primeira execução:
--   e-mail: demo@primarella.com
--   senha:  Primarella@2026
-- O valor salvo abaixo é um hash BCrypt, não a senha em texto puro.
INSERT INTO usuario
(nome, email, senha, ativo, created_at, created_by, updated_at, updated_by)
VALUES
    ('Mariana Costa', 'demo@primarella.com',
     '$2y$10$zGhjpHO0zX32T.5Ttv6uoumAS0NabYAli8BULLKKa5kKGUDjlTKMm',
     TRUE, NOW(), NULL, NOW(), NULL)
    ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id);

SET @usuario_id = LAST_INSERT_ID();

INSERT INTO cargo (nome, descricao)
VALUES ('Administrador Demo', 'Cargo utilizado apenas para os dados de demonstração')
    ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id);

SET @cargo_id = LAST_INSERT_ID();

INSERT INTO usuario_cargo (usuario_id, cargo_id, created_at, created_by)
VALUES (@usuario_id, @cargo_id, NOW(), @usuario_id)
    ON DUPLICATE KEY UPDATE id = id;

-- 2) Insumos: 42 ativos e 6 inativos.

INSERT INTO insumo
(nome, unidade_medida, quantidade_atual, percentual_perda, ativo,
 created_at, created_by, updated_at, updated_by)
VALUES
    ('Mussarela', 'GRAMA', 28600.000, 5, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Farinha', 'GRAMA', 20800.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Calabresa', 'GRAMA', 14200.000, 5, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Molho de tomate', 'GRAMA', 11500.000, 2, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Cebola', 'GRAMA', 4300.000, 8, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Tomate', 'GRAMA', 6000.000, 5, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Azeitona', 'GRAMA', 1300.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Frango desfiado', 'GRAMA', 1000.000, 5, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Catupiry', 'GRAMA', 1000.000, 5, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Orégano', 'GRAMA', 500.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Presunto', 'GRAMA', 3500.000, 4, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Bacon', 'GRAMA', 2200.000, 5, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Milho', 'GRAMA', 1700.000, 3, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Ervilha', 'GRAMA', 1400.000, 3, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Palmito', 'GRAMA', 1000.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Champignon', 'GRAMA', 900.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Pimentão', 'GRAMA', 1200.000, 5, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Rúcula', 'GRAMA', 600.000, 10, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Manjericão', 'GRAMA', 300.000, 5, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Alho', 'GRAMA', 700.000, 3, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Requeijão', 'GRAMA', 1600.000, 4, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Parmesão', 'GRAMA', 1200.000, 3, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Cheddar', 'GRAMA', 1300.000, 4, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Carne seca', 'GRAMA', 1500.000, 5, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Chocolate', 'GRAMA', 2800.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Banana', 'GRAMA', 1900.000, 5, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Canela', 'GRAMA', 250.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Morango', 'GRAMA', 1000.000, 5, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Coco ralado', 'GRAMA', 800.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Batata palha', 'GRAMA', 1800.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Maçã', 'GRAMA', 900.000, 5, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Peito de peru', 'GRAMA', 1700.000, 3, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Coca-Cola 2L', 'UNIDADE', 18.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Guaraná 2L', 'UNIDADE', 20.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Caixa de pizza G', 'UNIDADE', 25.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Caixa de pizza M', 'UNIDADE', 30.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Suco de laranja 1L', 'UNIDADE', 12.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Água 500ml', 'UNIDADE', 20.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Refrigerante lata', 'UNIDADE', 48.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Embalagem sobremesa', 'UNIDADE', 25.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Guardanapo pacote', 'UNIDADE', 10.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Sacola delivery', 'UNIDADE', 80.000, 0, TRUE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Brócolis', 'GRAMA', 0.000, 8, FALSE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Alface', 'GRAMA', 0.000, 5, FALSE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Gorgonzola', 'GRAMA', 0.000, 0, FALSE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Pêssego', 'GRAMA', 0.000, 5, FALSE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Caixa de pizza broto', 'UNIDADE', 0.000, 0, FALSE, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Copo descartável', 'UNIDADE', 0.000, 0, FALSE, NOW(), @usuario_id, NOW(), @usuario_id)
    ON DUPLICATE KEY UPDATE
                         unidade_medida = VALUES(unidade_medida),
                         quantidade_atual = VALUES(quantidade_atual),
                         percentual_perda = VALUES(percentual_perda),
                         ativo = VALUES(ativo),
                         updated_at = NOW(),
                         updated_by = @usuario_id;

-- IDs dos insumos utilizados nas receitas, alertas e movimentos.
SET @i_mussarela = (SELECT id FROM insumo WHERE nome = 'Mussarela');
SET @i_farinha = (SELECT id FROM insumo WHERE nome = 'Farinha');
SET @i_calabresa = (SELECT id FROM insumo WHERE nome = 'Calabresa');
SET @i_molho = (SELECT id FROM insumo WHERE nome = 'Molho de tomate');
SET @i_cebola = (SELECT id FROM insumo WHERE nome = 'Cebola');
SET @i_tomate = (SELECT id FROM insumo WHERE nome = 'Tomate');
SET @i_azeitona = (SELECT id FROM insumo WHERE nome = 'Azeitona');
SET @i_frango = (SELECT id FROM insumo WHERE nome = 'Frango desfiado');
SET @i_catupiry = (SELECT id FROM insumo WHERE nome = 'Catupiry');
SET @i_oregano = (SELECT id FROM insumo WHERE nome = 'Orégano');
SET @i_presunto = (SELECT id FROM insumo WHERE nome = 'Presunto');
SET @i_milho = (SELECT id FROM insumo WHERE nome = 'Milho');
SET @i_manjericao = (SELECT id FROM insumo WHERE nome = 'Manjericão');
SET @i_rucula = (SELECT id FROM insumo WHERE nome = 'Rúcula');
SET @i_coca = (SELECT id FROM insumo WHERE nome = 'Coca-Cola 2L');
SET @i_guarana = (SELECT id FROM insumo WHERE nome = 'Guaraná 2L');
SET @i_caixa_g = (SELECT id FROM insumo WHERE nome = 'Caixa de pizza G');
SET @i_caixa_m = (SELECT id FROM insumo WHERE nome = 'Caixa de pizza M');
SET @i_suco = (SELECT id FROM insumo WHERE nome = 'Suco de laranja 1L');

-- 3) Produtos do cardápio.
INSERT INTO produto
(nome, ativo, tipo, tamanho, created_at, created_by, updated_at, updated_by)
VALUES
    ('Pizza Calabresa', TRUE, 'TRADICIONAL', 35, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Pizza Mussarela', TRUE, 'TRADICIONAL', 35, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Pizza Portuguesa', TRUE, 'TRADICIONAL', 35, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Pizza Frango com Catupiry', TRUE, 'TRADICIONAL', 35, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Pizza Margherita', TRUE, 'TRADICIONAL', 35, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Pizza Calabresa Trem', TRUE, 'TREM', 40, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Beirute de Frango', TRUE, 'BEIRUTE', 25, NOW(), @usuario_id, NOW(), @usuario_id),
    ('Beirute de Calabresa', TRUE, 'BEIRUTE', 25, NOW(), @usuario_id, NOW(), @usuario_id)
    ON DUPLICATE KEY UPDATE
                         ativo = VALUES(ativo),
                         tipo = VALUES(tipo),
                         updated_at = NOW(),
                         updated_by = @usuario_id;

SET @p_calabresa = (SELECT id FROM produto WHERE nome = 'Pizza Calabresa' AND tamanho = 35);
SET @p_mussarela = (SELECT id FROM produto WHERE nome = 'Pizza Mussarela' AND tamanho = 35);
SET @p_portuguesa = (SELECT id FROM produto WHERE nome = 'Pizza Portuguesa' AND tamanho = 35);
SET @p_frango = (SELECT id FROM produto WHERE nome = 'Pizza Frango com Catupiry' AND tamanho = 35);
SET @p_margherita = (SELECT id FROM produto WHERE nome = 'Pizza Margherita' AND tamanho = 35);
SET @p_trem = (SELECT id FROM produto WHERE nome = 'Pizza Calabresa Trem' AND tamanho = 40);
SET @p_beirute_frango = (SELECT id FROM produto WHERE nome = 'Beirute de Frango' AND tamanho = 25);
SET @p_beirute_calabresa = (SELECT id FROM produto WHERE nome = 'Beirute de Calabresa' AND tamanho = 25);

-- 4) Receitas padrão (quantidades por item cadastrado no cardápio).
INSERT INTO produto_insumo
(produto_id, insumo_id, quantidade, created_at, created_by, updated_at, updated_by)
VALUES
    (@p_calabresa, @i_farinha, 200.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_calabresa, @i_mussarela, 250.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_calabresa, @i_calabresa, 100.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_calabresa, @i_molho, 80.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_calabresa, @i_cebola, 40.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_calabresa, @i_oregano, 3.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_calabresa, @i_azeitona, 10.000, NOW(), @usuario_id, NOW(), @usuario_id),

    (@p_mussarela, @i_farinha, 200.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_mussarela, @i_mussarela, 280.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_mussarela, @i_molho, 70.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_mussarela, @i_oregano, 3.000, NOW(), @usuario_id, NOW(), @usuario_id),

    (@p_portuguesa, @i_farinha, 200.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_portuguesa, @i_mussarela, 150.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_portuguesa, @i_presunto, 100.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_portuguesa, @i_cebola, 25.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_portuguesa, @i_azeitona, 15.000, NOW(), @usuario_id, NOW(), @usuario_id),

    (@p_frango, @i_farinha, 200.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_frango, @i_frango, 160.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_frango, @i_catupiry, 100.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_frango, @i_molho, 60.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_frango, @i_milho, 40.000, NOW(), @usuario_id, NOW(), @usuario_id),

    (@p_margherita, @i_farinha, 200.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_margherita, @i_mussarela, 200.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_margherita, @i_tomate, 80.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_margherita, @i_molho, 50.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_margherita, @i_manjericao, 5.000, NOW(), @usuario_id, NOW(), @usuario_id),

    (@p_trem, @i_farinha, 300.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_trem, @i_mussarela, 350.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_trem, @i_calabresa, 180.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_trem, @i_molho, 100.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_trem, @i_cebola, 50.000, NOW(), @usuario_id, NOW(), @usuario_id),

    (@p_beirute_frango, @i_frango, 120.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_beirute_frango, @i_catupiry, 80.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_beirute_frango, @i_tomate, 40.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_beirute_frango, @i_rucula, 20.000, NOW(), @usuario_id, NOW(), @usuario_id),

    (@p_beirute_calabresa, @i_calabresa, 100.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_beirute_calabresa, @i_mussarela, 120.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_beirute_calabresa, @i_tomate, 50.000, NOW(), @usuario_id, NOW(), @usuario_id),
    (@p_beirute_calabresa, @i_cebola, 20.000, NOW(), @usuario_id, NOW(), @usuario_id)
    ON DUPLICATE KEY UPDATE
                         quantidade = VALUES(quantidade),
                         updated_at = NOW(),
                         updated_by = @usuario_id;

-- 5) Limites de alerta por cargo para alguns insumos principais.
INSERT INTO alerta_estoque_baixo
(cargo_id, insumo_id, quantidade_minima, ativo, created_at, updated_at)
VALUES
    (@cargo_id, @i_mussarela, 2000.000, TRUE, NOW(), NOW()),
    (@cargo_id, @i_farinha, 3000.000, TRUE, NOW(), NOW()),
    (@cargo_id, @i_calabresa, 1800.000, TRUE, NOW(), NOW()),
    (@cargo_id, @i_molho, 1500.000, TRUE, NOW(), NOW()),
    (@cargo_id, @i_cebola, 700.000, TRUE, NOW(), NOW()),
    (@cargo_id, @i_coca, 10.000, TRUE, NOW(), NOW())
    ON DUPLICATE KEY UPDATE
                         quantidade_minima = VALUES(quantidade_minima),
                         ativo = VALUES(ativo),
                         updated_at = NOW();

-- 6) Histórico de vendas e movimentações das últimas quatro semanas.
-- O serviço da dashboard considera os últimos 30 dias e agrupa por semana.
-- As datas abaixo são sempre o mesmo dia da semana, nas quatro semanas recentes.
SET @dia_semana = WEEKDAY(CURDATE());
SET @segunda_atual = DATE_SUB(CURDATE(), INTERVAL @dia_semana DAY);
SET @data_s1 = DATE_ADD(DATE_SUB(@segunda_atual, INTERVAL 3 WEEK), INTERVAL @dia_semana DAY);
SET @data_s2 = DATE_ADD(DATE_SUB(@segunda_atual, INTERVAL 2 WEEK), INTERVAL @dia_semana DAY);
SET @data_s3 = DATE_ADD(DATE_SUB(@segunda_atual, INTERVAL 1 WEEK), INTERVAL @dia_semana DAY);
SET @data_s4 = CURDATE();

-- Limpa apenas o lote de demonstração de uma execução anterior.
-- As vendas do lote são identificadas por created_at = 2000-01-01.
DELETE FROM movimentacao_estoque
WHERE observacao LIKE 'MOCK_PRIMARELLA:%';

DELETE ivi
FROM item_venda_insumo ivi
JOIN item_venda iv ON iv.id = ivi.item_venda_id
JOIN venda v ON v.id = iv.venda_id
WHERE v.created_by = @usuario_id
  AND v.created_at = '2000-01-01 00:00:00';

DELETE iv
FROM item_venda iv
JOIN venda v ON v.id = iv.venda_id
WHERE v.created_by = @usuario_id
  AND v.created_at = '2000-01-01 00:00:00';

DELETE FROM venda
WHERE created_by = @usuario_id
  AND created_at = '2000-01-01 00:00:00';

-- Semana 1
INSERT INTO venda (data_hora, origem, status, created_at, created_by)
VALUES (@data_s1, 'IFOOD', 'ATIVA', '2000-01-01 00:00:00', @usuario_id);
SET @venda_s1 = LAST_INSERT_ID();
INSERT INTO item_venda (venda_id, produto_id, quantidade)
VALUES (@venda_s1, @p_calabresa, 25);
SET @item_s1 = LAST_INSERT_ID();

INSERT INTO item_venda_insumo (item_venda_id, insumo_id, quantidade, created_at, created_by, updated_at, updated_by)
SELECT @item_s1, i.id, d.quantidade, NOW(), @usuario_id, NOW(), NULL
FROM insumo i
         JOIN (
    SELECT 'Mussarela' nome, 9000.000 quantidade UNION ALL
    SELECT 'Farinha', 7200.000 UNION ALL
    SELECT 'Calabresa', 3400.000 UNION ALL
    SELECT 'Molho de tomate', 2800.000 UNION ALL
    SELECT 'Cebola', 1450.000 UNION ALL
    SELECT 'Coca-Cola 2L', 22.000 UNION ALL
    SELECT 'Guaraná 2L', 16.000 UNION ALL
    SELECT 'Caixa de pizza G', 14.000 UNION ALL
    SELECT 'Caixa de pizza M', 12.000 UNION ALL
    SELECT 'Suco de laranja 1L', 6.000
) d ON d.nome = i.nome;

INSERT INTO movimentacao_estoque
(insumo_id, item_venda_insumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
SELECT insumo_id, id, @usuario_id, 'SAIDA', 'VENDA', quantidade,
       'MOCK_PRIMARELLA: venda semana 1', @data_s1
FROM item_venda_insumo WHERE item_venda_id = @item_s1;

-- Semana 2
INSERT INTO venda (data_hora, origem, status, created_at, created_by)
VALUES (@data_s2, '99', 'ATIVA', '2000-01-01 00:00:00', @usuario_id);
SET @venda_s2 = LAST_INSERT_ID();
INSERT INTO item_venda (venda_id, produto_id, quantidade)
VALUES (@venda_s2, @p_calabresa, 25);
SET @item_s2 = LAST_INSERT_ID();

INSERT INTO item_venda_insumo (item_venda_id, insumo_id, quantidade, created_at, created_by, updated_at, updated_by)
SELECT @item_s2, i.id, d.quantidade, NOW(), @usuario_id, NOW(), NULL
FROM insumo i
         JOIN (
    SELECT 'Mussarela' nome, 10000.000 quantidade UNION ALL
    SELECT 'Farinha', 8000.000 UNION ALL
    SELECT 'Calabresa', 3800.000 UNION ALL
    SELECT 'Molho de tomate', 3200.000 UNION ALL
    SELECT 'Cebola', 1600.000 UNION ALL
    SELECT 'Coca-Cola 2L', 24.000 UNION ALL
    SELECT 'Guaraná 2L', 18.000 UNION ALL
    SELECT 'Caixa de pizza G', 16.000 UNION ALL
    SELECT 'Caixa de pizza M', 13.000 UNION ALL
    SELECT 'Suco de laranja 1L', 7.000
) d ON d.nome = i.nome;

INSERT INTO movimentacao_estoque
(insumo_id, item_venda_insumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
SELECT insumo_id, id, @usuario_id, 'SAIDA', 'VENDA', quantidade,
       'MOCK_PRIMARELLA: venda semana 2', @data_s2
FROM item_venda_insumo WHERE item_venda_id = @item_s2;

-- Semana 3
INSERT INTO venda (data_hora, origem, status, created_at, created_by)
VALUES (@data_s3, 'KEETA', 'ATIVA', '2000-01-01 00:00:00', @usuario_id);
SET @venda_s3 = LAST_INSERT_ID();
INSERT INTO item_venda (venda_id, produto_id, quantidade)
VALUES (@venda_s3, @p_calabresa, 25);
SET @item_s3 = LAST_INSERT_ID();

INSERT INTO item_venda_insumo (item_venda_id, insumo_id, quantidade, created_at, created_by, updated_at, updated_by)
SELECT @item_s3, i.id, d.quantidade, NOW(), @usuario_id, NOW(), NULL
FROM insumo i
         JOIN (
    SELECT 'Mussarela' nome, 9500.000 quantidade UNION ALL
    SELECT 'Farinha', 7800.000 UNION ALL
    SELECT 'Calabresa', 3700.000 UNION ALL
    SELECT 'Molho de tomate', 3100.000 UNION ALL
    SELECT 'Cebola', 1550.000 UNION ALL
    SELECT 'Coca-Cola 2L', 25.000 UNION ALL
    SELECT 'Guaraná 2L', 18.000 UNION ALL
    SELECT 'Caixa de pizza G', 16.000 UNION ALL
    SELECT 'Caixa de pizza M', 13.000 UNION ALL
    SELECT 'Suco de laranja 1L', 7.000
) d ON d.nome = i.nome;

INSERT INTO movimentacao_estoque
(insumo_id, item_venda_insumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
SELECT insumo_id, id, @usuario_id, 'SAIDA', 'VENDA', quantidade,
       'MOCK_PRIMARELLA: venda semana 3', @data_s3
FROM item_venda_insumo WHERE item_venda_id = @item_s3;

-- Semana 4
INSERT INTO venda (data_hora, origem, status, created_at, created_by)
VALUES (@data_s4, 'IFOOD', 'ATIVA', '2000-01-01 00:00:00', @usuario_id);
SET @venda_s4 = LAST_INSERT_ID();
INSERT INTO item_venda (venda_id, produto_id, quantidade)
VALUES (@venda_s4, @p_calabresa, 25);
SET @item_s4 = LAST_INSERT_ID();

INSERT INTO item_venda_insumo (item_venda_id, insumo_id, quantidade, created_at, created_by, updated_at, updated_by)
SELECT @item_s4, i.id, d.quantidade, NOW(), @usuario_id, NOW(), NULL
FROM insumo i
         JOIN (
    SELECT 'Mussarela' nome, 10000.000 quantidade UNION ALL
    SELECT 'Farinha', 8200.000 UNION ALL
    SELECT 'Calabresa', 3900.000 UNION ALL
    SELECT 'Molho de tomate', 3300.000 UNION ALL
    SELECT 'Cebola', 1700.000 UNION ALL
    SELECT 'Coca-Cola 2L', 25.000 UNION ALL
    SELECT 'Guaraná 2L', 19.000 UNION ALL
    SELECT 'Caixa de pizza G', 18.000 UNION ALL
    SELECT 'Caixa de pizza M', 14.000 UNION ALL
    SELECT 'Suco de laranja 1L', 8.000
) d ON d.nome = i.nome;

INSERT INTO movimentacao_estoque
(insumo_id, item_venda_insumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
SELECT insumo_id, id, @usuario_id, 'SAIDA', 'VENDA', quantidade,
       'MOCK_PRIMARELLA: venda semana 4', @data_s4
FROM item_venda_insumo WHERE item_venda_id = @item_s4;

-- Entradas em gramas: 52 kg + 48 kg + 46 kg + 39 kg = 185 kg.
INSERT INTO movimentacao_estoque
(insumo_id, item_venda_insumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
SELECT i.id, NULL, @usuario_id, 'ENTRADA', 'COMPRA', d.quantidade,
       CONCAT('MOCK_PRIMARELLA: compra semana 1 - ', d.nome), @data_s1
FROM insumo i
         JOIN (
    SELECT 'Mussarela' nome, 20000.000 quantidade UNION ALL
    SELECT 'Farinha', 15000.000 UNION ALL
    SELECT 'Calabresa', 8000.000 UNION ALL
    SELECT 'Molho de tomate', 6000.000 UNION ALL
    SELECT 'Cebola', 3000.000
) d ON d.nome = i.nome;

INSERT INTO movimentacao_estoque
(insumo_id, item_venda_insumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
SELECT i.id, NULL, @usuario_id, 'ENTRADA', 'COMPRA', d.quantidade,
       CONCAT('MOCK_PRIMARELLA: compra semana 2 - ', d.nome), @data_s2
FROM insumo i
         JOIN (
    SELECT 'Mussarela' nome, 16000.000 quantidade UNION ALL
    SELECT 'Farinha', 14000.000 UNION ALL
    SELECT 'Calabresa', 8000.000 UNION ALL
    SELECT 'Molho de tomate', 7000.000 UNION ALL
    SELECT 'Cebola', 3000.000
) d ON d.nome = i.nome;

INSERT INTO movimentacao_estoque
(insumo_id, item_venda_insumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
SELECT i.id, NULL, @usuario_id, 'ENTRADA', 'COMPRA', d.quantidade,
       CONCAT('MOCK_PRIMARELLA: compra semana 3 - ', d.nome), @data_s3
FROM insumo i
         JOIN (
    SELECT 'Mussarela' nome, 16000.000 quantidade UNION ALL
    SELECT 'Farinha', 13000.000 UNION ALL
    SELECT 'Calabresa', 7000.000 UNION ALL
    SELECT 'Molho de tomate', 7000.000 UNION ALL
    SELECT 'Cebola', 3000.000
) d ON d.nome = i.nome;

INSERT INTO movimentacao_estoque
(insumo_id, item_venda_insumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
SELECT i.id, NULL, @usuario_id, 'ENTRADA', 'COMPRA', d.quantidade,
       CONCAT('MOCK_PRIMARELLA: compra semana 4 - ', d.nome), @data_s4
FROM insumo i
         JOIN (
    SELECT 'Mussarela' nome, 12000.000 quantidade UNION ALL
    SELECT 'Farinha', 12000.000 UNION ALL
    SELECT 'Calabresa', 6000.000 UNION ALL
    SELECT 'Molho de tomate', 6000.000 UNION ALL
    SELECT 'Cebola', 3000.000
) d ON d.nome = i.nome;

-- Entradas em unidades: 100 + 100 + 90 + 80 = 370 unidades.
INSERT INTO movimentacao_estoque
(insumo_id, item_venda_insumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
SELECT i.id, NULL, @usuario_id, 'ENTRADA', 'COMPRA', d.quantidade,
       CONCAT('MOCK_PRIMARELLA: compra unidades semana 1 - ', d.nome), @data_s1
FROM insumo i
         JOIN (
    SELECT 'Coca-Cola 2L' nome, 30.000 quantidade UNION ALL
    SELECT 'Guaraná 2L', 25.000 UNION ALL
    SELECT 'Caixa de pizza G', 20.000 UNION ALL
    SELECT 'Caixa de pizza M', 15.000 UNION ALL
    SELECT 'Suco de laranja 1L', 10.000
) d ON d.nome = i.nome;

INSERT INTO movimentacao_estoque
(insumo_id, item_venda_insumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
SELECT i.id, NULL, @usuario_id, 'ENTRADA', 'COMPRA', d.quantidade,
       CONCAT('MOCK_PRIMARELLA: compra unidades semana 2 - ', d.nome), @data_s2
FROM insumo i
         JOIN (
    SELECT 'Coca-Cola 2L' nome, 30.000 quantidade UNION ALL
    SELECT 'Guaraná 2L', 25.000 UNION ALL
    SELECT 'Caixa de pizza G', 20.000 UNION ALL
    SELECT 'Caixa de pizza M', 15.000 UNION ALL
    SELECT 'Suco de laranja 1L', 10.000
) d ON d.nome = i.nome;

INSERT INTO movimentacao_estoque
(insumo_id, item_venda_insumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
SELECT i.id, NULL, @usuario_id, 'ENTRADA', 'COMPRA', d.quantidade,
       CONCAT('MOCK_PRIMARELLA: compra unidades semana 3 - ', d.nome), @data_s3
FROM insumo i
         JOIN (
    SELECT 'Coca-Cola 2L' nome, 25.000 quantidade UNION ALL
    SELECT 'Guaraná 2L', 25.000 UNION ALL
    SELECT 'Caixa de pizza G', 20.000 UNION ALL
    SELECT 'Caixa de pizza M', 12.000 UNION ALL
    SELECT 'Suco de laranja 1L', 8.000
) d ON d.nome = i.nome;

INSERT INTO movimentacao_estoque
(insumo_id, item_venda_insumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
SELECT i.id, NULL, @usuario_id, 'ENTRADA', 'COMPRA', d.quantidade,
       CONCAT('MOCK_PRIMARELLA: compra unidades semana 4 - ', d.nome), @data_s4
FROM insumo i
         JOIN (
    SELECT 'Coca-Cola 2L' nome, 20.000 quantidade UNION ALL
    SELECT 'Guaraná 2L', 20.000 UNION ALL
    SELECT 'Caixa de pizza G', 15.000 UNION ALL
    SELECT 'Caixa de pizza M', 15.000 UNION ALL
    SELECT 'Suco de laranja 1L', 10.000
) d ON d.nome = i.nome;

-- Quebras: 1,4 + 2,1 + 3,0 + 1,9 = 8,4 kg.
-- Outras saídas de demonstração: 14,75 + 8,3 + 4,35 + 3,0 = 30,4 kg.
INSERT INTO movimentacao_estoque
(insumo_id, item_venda_insumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
SELECT i.id, NULL, @usuario_id, 'SAIDA', d.motivo, d.quantidade,
       CONCAT('MOCK_PRIMARELLA: ', d.motivo, ' semana ', d.semana), d.data_hora
FROM insumo i
         JOIN (
    SELECT 'Cebola' nome, 'QUEBRA' motivo, 1400.000 quantidade, 1 semana, @data_s1 data_hora UNION ALL
    SELECT 'Molho de tomate', 'QUEBRA', 2100.000, 2, @data_s2 UNION ALL
    SELECT 'Farinha', 'QUEBRA', 3000.000, 3, @data_s3 UNION ALL
    SELECT 'Mussarela', 'QUEBRA', 1900.000, 4, @data_s4 UNION ALL
    SELECT 'Tomate', 'OUTRO', 14750.000, 1, @data_s1 UNION ALL
    SELECT 'Azeitona', 'OUTRO', 8300.000, 2, @data_s2 UNION ALL
    SELECT 'Frango desfiado', 'OUTRO', 4350.000, 3, @data_s3 UNION ALL
    SELECT 'Catupiry', 'OUTRO', 3000.000, 4, @data_s4
) d ON d.nome = i.nome;

COMMIT;

-- Valores aproximados que a API da dashboard deve calcular, filtrando GRAMA:
-- Entradas: 185.000 g; saídas: 142.000 g; quebras: 8.400 g.
-- Ranking de vendas (GRAMA): Mussarela 38.500 g; Farinha 31.200 g;
-- Calabresa 14.800 g; Molho 12.400 g; Cebola 6.300 g.
-- Ranking de vendas (UNIDADE): Coca-Cola 96; Guaraná 71;
-- Caixa G 64; Caixa M 52; Suco 28.
-- Status: 42 ativos / 6 inativos.
