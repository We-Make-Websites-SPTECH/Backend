-- Carga de demonstração para executar após schema.sql, em banco vazio.
START TRANSACTION;

-- Referência única de data e hora para toda a carga.
SET @agora = NOW();
-- Cadastros antigos ficam fora dos 30 dias da dashboard, o recente fica dentro.
SET @cadastro = DATE_SUB(@agora, INTERVAL 50 DAY);
SET @cadastro_recente = DATE_SUB(@agora, INTERVAL 5 DAY);
-- Datas de criação das operações de ontem e do lançamento manual.
SET @ontem = DATE_SUB(@agora, INTERVAL 1 DAY);
SET @manual = DATE_SUB(@agora, INTERVAL 2 DAY);
-- Personalização ocorre 5 minutos após a criação, confirmação e baixa, 10 minutos após.
SET @personalizado_manual = DATE_ADD(@manual, INTERVAL 5 MINUTE);
SET @processado_manual = DATE_ADD(@manual, INTERVAL 10 MINUTE);
-- Mesma sequência de edição e confirmação para as operações de ontem.
SET @personalizado_ontem = DATE_ADD(@ontem, INTERVAL 5 MINUTE);
SET @processado_ontem = DATE_ADD(@ontem, INTERVAL 10 MINUTE);
-- Datas das perdas por quebra, em gramas e unidades.
SET @quebra_mussarela = DATE_SUB(@agora, INTERVAL 4 DAY);
SET @quebra_frango = DATE_SUB(@agora, INTERVAL 9 DAY);
SET @quebra_embalagens = DATE_SUB(@agora, INTERVAL 6 DAY);
-- Saída avulsa de embalagens, separada do consumo dos produtos.
SET @saida_embalagens = DATE_SUB(@agora, INTERVAL 8 DAY);
-- Ajustes de saldo após conferência: aumento de mussarela e redução de cebola.
SET @ajuste_mussarela = DATE_SUB(@agora, INTERVAL 3 DAY);
SET @ajuste_cebola = DATE_SUB(@agora, INTERVAL 2 DAY);
-- Hash compatível com o backend para a senha Primarella@2026.
SET @senha = '{pbkdf2@SpringSecurity_v5_8}35f736f0d3e9ec9ea404868a63cbea0055e2703e8ee39fab3d8948989c90f741340f122bdefe8018f12f3a5a9c886c5e';

-- Semana 1: há 21 dias. Compra precede o upload do TXT e a confirmação da baixa.
SET @data_s1 = DATE_SUB(CURDATE(), INTERVAL 21 DAY);
SET @compra_s1 = DATE_ADD(@data_s1, INTERVAL 1 MINUTE); -- Entrada no estoque.
SET @envio_s1 = DATE_ADD(@data_s1, INTERVAL 2 MINUTE); -- Criação da importação.
SET @processado_s1 = DATE_ADD(@data_s1, INTERVAL 3 MINUTE); -- Confirmação e consumo.

-- Semana 2: há 14 dias, seguindo a mesma ordem de compra, upload e confirmação.
SET @data_s2 = DATE_SUB(CURDATE(), INTERVAL 14 DAY);
SET @compra_s2 = DATE_ADD(@data_s2, INTERVAL 1 MINUTE);
SET @envio_s2 = DATE_ADD(@data_s2, INTERVAL 2 MINUTE);
SET @processado_s2 = DATE_ADD(@data_s2, INTERVAL 3 MINUTE);

-- Semana 3: há 7 dias.
SET @data_s3 = DATE_SUB(CURDATE(), INTERVAL 7 DAY);
SET @compra_s3 = DATE_ADD(@data_s3, INTERVAL 1 MINUTE);
SET @envio_s3 = DATE_ADD(@data_s3, INTERVAL 2 MINUTE);
SET @processado_s3 = DATE_ADD(@data_s3, INTERVAL 3 MINUTE);

-- Semana 4: hoje.
SET @data_s4 = DATE_SUB(CURDATE(), INTERVAL 0 DAY);
SET @compra_s4 = DATE_ADD(@data_s4, INTERVAL 1 MINUTE);
SET @envio_s4 = DATE_ADD(@data_s4, INTERVAL 2 MINUTE);
SET @processado_s4 = DATE_ADD(@data_s4, INTERVAL 3 MINUTE);

-- Usuários ativos para login e um inativo para verificar bloqueios.
INSERT INTO usuario
(id, nome, email, senha, ativo, created_at, created_by, updated_at, updated_by)
VALUES
    (1, 'Mariana Costa', 'demo@primarella.com', @senha, TRUE, @cadastro, NULL, @cadastro, NULL),
    (2, 'Carlos Almeida', 'operador@primarella.com', @senha, TRUE, @cadastro, 1, @cadastro, 1),
    (3, 'Paula Santos', 'inativo@primarella.com', @senha, FALSE, @cadastro, 1, @agora, 1);

-- IDs fixos dos usuários, reutilizados nas referências da carga.
SET @usuario_id = 1;
SET @operador_id = 2;
SET @inativo_id = 3;

-- Cargo associado aos usuários de demonstração.
INSERT INTO cargo
(id, nome, descricao)
VALUES
    (1, 'Administrador', 'Administração da pizzaria');

SET @cargo_id = 1;

-- Vínculos dos usuários com o cargo cadastrado.
INSERT INTO usuario_cargo
(usuario_id, cargo_id, created_at, created_by)
VALUES
    (@usuario_id, @cargo_id, @cadastro, @usuario_id),
    (@operador_id, @cargo_id, @cadastro, @usuario_id),
    (@inativo_id, @cargo_id, @cadastro, @usuario_id);

-- Insumos ativos e inativos, com saldos suficientes, baixos e zerados.
-- Os saldos finais correspondem às entradas e saídas inseridas ao fim da carga.
INSERT INTO insumo
(id, nome, unidade_medida, quantidade_atual, percentual_perda, ativo, created_at, created_by, updated_at, updated_by)
VALUES
    (1, 'Mussarela', 'GRAMA', 57910.000, 5, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (2, 'Farinha', 'GRAMA', 45400.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (3, 'Calabresa', 'GRAMA', 31300.000, 5, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (4, 'Molho de tomate', 'GRAMA', 8720.000, 2, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (5, 'Cebola', 'GRAMA', 3480.000, 8, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (6, 'Tomate', 'GRAMA', 6000.000, 5, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (7, 'Azeitona', 'GRAMA', 1100.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (8, 'Frango desfiado', 'GRAMA', 900.000, 5, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (9, 'Catupiry', 'GRAMA', 1000.000, 5, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (10, 'Presunto', 'GRAMA', 3500.000, 4, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (11, 'Bacon', 'GRAMA', 2200.000, 5, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (12, 'Milho', 'GRAMA', 1700.000, 3, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (13, 'Ervilha', 'GRAMA', 1400.000, 3, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (14, 'Palmito', 'GRAMA', 1000.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (15, 'Champignon', 'GRAMA', 900.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (16, 'Requeijão', 'GRAMA', 1600.000, 4, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (17, 'Parmesão', 'GRAMA', 1200.000, 3, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (18, 'Cheddar', 'GRAMA', 1300.000, 4, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (19, 'Carne seca', 'GRAMA', 1500.000, 5, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (20, 'Chocolate', 'GRAMA', 2800.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (21, 'Banana', 'GRAMA', 1900.000, 5, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (22, 'Canela', 'GRAMA', 250.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (23, 'Morango', 'GRAMA', 1000.000, 5, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (24, 'Coco ralado', 'GRAMA', 800.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (25, 'Batata palha', 'GRAMA', 1800.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (26, 'Maçã', 'GRAMA', 900.000, 5, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (27, 'Peito de peru', 'GRAMA', 1700.000, 3, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (28, 'Coca-Cola 2L', 'UNIDADE', 18.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (29, 'Guaraná 2L', 'UNIDADE', 20.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (30, 'Caixa de pizza G', 'UNIDADE', 63.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (31, 'Caixa de pizza M', 'UNIDADE', 25.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (32, 'Suco de laranja 1L', 'UNIDADE', 12.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (33, 'Água 500ml', 'UNIDADE', 20.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (34, 'Refrigerante lata', 'UNIDADE', 48.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (35, 'Embalagem sobremesa', 'UNIDADE', 25.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (36, 'Guardanapo pacote', 'UNIDADE', 10.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (37, 'Sacola delivery', 'UNIDADE', 80.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (38, 'Brócolis', 'GRAMA', 0.000, 8, FALSE, @cadastro, @usuario_id, @agora, @usuario_id),
    (39, 'Alface', 'GRAMA', 0.000, 5, FALSE, @cadastro, @usuario_id, @agora, @usuario_id),
    (40, 'Gorgonzola', 'GRAMA', 0.000, 0, FALSE, @cadastro, @usuario_id, @agora, @usuario_id),
    (41, 'Pêssego', 'GRAMA', 0.000, 5, FALSE, @cadastro, @usuario_id, @agora, @usuario_id),
    (42, 'Caixa de pizza broto', 'UNIDADE', 0.000, 0, FALSE, @cadastro, @usuario_id, @agora, @usuario_id),
    (43, 'Copo descartável', 'UNIDADE', 0.000, 0, FALSE, @cadastro, @usuario_id, @agora, @usuario_id),
    (44, 'Provolone', 'GRAMA', 60.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (45, 'Chocolate branco', 'GRAMA', 0.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (46, 'Cream cheese', 'GRAMA', 985.000, 0, TRUE, @cadastro, @usuario_id, @agora, @usuario_id),
    (47, 'Pepperoni', 'GRAMA', 5.000, 10, TRUE, @cadastro_recente, @usuario_id, @agora, @usuario_id);

-- IDs dos insumos para facilitar a leitura das receitas e movimentações.
SET @i_mussarela = 1;
SET @i_farinha = 2;
SET @i_calabresa = 3;
SET @i_molho = 4;
SET @i_cebola = 5;
SET @i_tomate = 6;
SET @i_azeitona = 7;
SET @i_frango = 8;
SET @i_catupiry = 9;
SET @i_presunto = 10;
SET @i_bacon = 11;
SET @i_milho = 12;
SET @i_ervilha = 13;
SET @i_palmito = 14;
SET @i_champignon = 15;
SET @i_requeijao = 16;
SET @i_parmesao = 17;
SET @i_cheddar = 18;
SET @i_carne_seca = 19;
SET @i_chocolate = 20;
SET @i_banana = 21;
SET @i_canela = 22;
SET @i_morango = 23;
SET @i_coco_ralado = 24;
SET @i_batata_palha = 25;
SET @i_maca = 26;
SET @i_peito_de_peru = 27;
SET @i_coca = 28;
SET @i_guarana = 29;
SET @i_caixa_g = 30;
SET @i_caixa_m = 31;
SET @i_suco = 32;
SET @i_agua_500ml = 33;
SET @i_refrigerante_lata = 34;
SET @i_embalagem_sobremesa = 35;
SET @i_guardanapo_pacote = 36;
SET @i_sacola_delivery = 37;
SET @i_brocolis = 38;
SET @i_alface = 39;
SET @i_gorgonzola = 40;
SET @i_pessego = 41;
SET @i_caixa_de_pizza_broto = 42;
SET @i_copo_descartavel = 43;
SET @i_provolone = 44;
SET @i_chocolate_branco = 45;
SET @i_cream_cheese = 46;
SET @i_pepperoni = 47;

-- Cardápio com tipos distintos, um produto inativo e outro ainda sem receita.
INSERT INTO produto
(id, nome, ativo, tipo, tamanho, created_at, created_by, updated_at, updated_by)
VALUES
    (1, 'Pizza Calabresa', TRUE, 'TRADICIONAL', 35, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (2, 'Pizza Mussarela', TRUE, 'TRADICIONAL', 35, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (3, 'Pizza Portuguesa', TRUE, 'TRADICIONAL', 35, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (4, 'Pizza Frango com Catupiry', TRUE, 'TRADICIONAL', 35, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (5, 'Pizza Margherita', TRUE, 'TRADICIONAL', 35, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (6, 'Pizza Calabresa Trem', TRUE, 'TREM', 40, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (7, 'Beirute de Frango', TRUE, 'BEIRUTE', 25, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (8, 'Beirute de Calabresa', TRUE, 'BEIRUTE', 25, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (9, 'Pizza Quatro Queijos', TRUE, 'TRADICIONAL', 35, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (10, 'Pizza de Brócolis', FALSE, 'TRADICIONAL', 35, @cadastro, @usuario_id, @agora, @usuario_id),
    (11, 'Beirute de Provolone', TRUE, 'BEIRUTE', 25, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (12, 'Pizza de Chocolate Branco', TRUE, 'TRADICIONAL', 35, @cadastro, @usuario_id, @cadastro, @usuario_id);

-- IDs dos produtos referenciados nas receitas e operações.
SET @p_calabresa = 1;
SET @p_mussarela = 2;
SET @p_portuguesa = 3;
SET @p_frango = 4;
SET @p_margherita = 5;
SET @p_trem = 6;
SET @p_beirute_frango = 7;
SET @p_beirute_calabresa = 8;
SET @p_quatro_queijos = 9;
SET @p_brocolis = 10;
SET @p_beirute_provolone = 11;
SET @p_chocolate_branco = 12;

-- Quantidades por unidade do produto, embalagens também compõem o consumo.
INSERT INTO produto_insumo
(produto_id, insumo_id, quantidade, created_at, created_by, updated_at, updated_by)
VALUES
    (@p_calabresa, @i_farinha, 200.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_calabresa, @i_mussarela, 250.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_calabresa, @i_calabresa, 100.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_calabresa, @i_molho, 80.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_calabresa, @i_cebola, 40.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_calabresa, @i_azeitona, 10.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_mussarela, @i_farinha, 200.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_mussarela, @i_mussarela, 280.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_mussarela, @i_molho, 70.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_portuguesa, @i_farinha, 200.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_portuguesa, @i_mussarela, 150.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_portuguesa, @i_presunto, 100.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_portuguesa, @i_cebola, 25.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_portuguesa, @i_azeitona, 15.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_frango, @i_farinha, 200.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_frango, @i_frango, 160.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_frango, @i_catupiry, 100.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_frango, @i_molho, 60.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_frango, @i_milho, 40.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_margherita, @i_farinha, 200.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_margherita, @i_mussarela, 200.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_margherita, @i_tomate, 80.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_margherita, @i_molho, 50.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_trem, @i_farinha, 300.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_trem, @i_mussarela, 350.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_trem, @i_calabresa, 180.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_trem, @i_molho, 100.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_trem, @i_cebola, 50.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_beirute_frango, @i_frango, 120.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_beirute_frango, @i_catupiry, 80.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_beirute_frango, @i_tomate, 40.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_beirute_calabresa, @i_calabresa, 100.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_beirute_calabresa, @i_mussarela, 120.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_beirute_calabresa, @i_tomate, 50.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_beirute_calabresa, @i_cebola, 20.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_brocolis, @i_mussarela, 100.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_beirute_provolone, @i_provolone, 80.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_chocolate_branco, @i_chocolate_branco, 10.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_calabresa, @i_caixa_g, 1.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_mussarela, @i_caixa_g, 1.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_portuguesa, @i_caixa_g, 1.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_frango, @i_caixa_g, 1.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_margherita, @i_caixa_g, 1.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_beirute_frango, @i_caixa_m, 1.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_beirute_calabresa, @i_caixa_m, 1.000, @cadastro, @usuario_id, @cadastro, @usuario_id),
    (@p_beirute_provolone, @i_caixa_m, 1.000, @cadastro, @usuario_id, @cadastro, @usuario_id);

-- Registro da inclusão dos ingredientes nas receitas cadastradas.
INSERT INTO produto_insumo_historico
(produto_id, insumo_id, acao, quantidade_anterior, quantidade_nova, usuario_id, data_hora)
VALUES
    (@p_calabresa, @i_farinha, 'ADICIONADO', NULL, 200.000, @usuario_id, @cadastro),
    (@p_calabresa, @i_mussarela, 'ADICIONADO', NULL, 250.000, @usuario_id, @cadastro),
    (@p_calabresa, @i_calabresa, 'ADICIONADO', NULL, 100.000, @usuario_id, @cadastro),
    (@p_calabresa, @i_molho, 'ADICIONADO', NULL, 80.000, @usuario_id, @cadastro),
    (@p_calabresa, @i_cebola, 'ADICIONADO', NULL, 40.000, @usuario_id, @cadastro),
    (@p_calabresa, @i_azeitona, 'ADICIONADO', NULL, 10.000, @usuario_id, @cadastro),
    (@p_mussarela, @i_farinha, 'ADICIONADO', NULL, 200.000, @usuario_id, @cadastro),
    (@p_mussarela, @i_mussarela, 'ADICIONADO', NULL, 280.000, @usuario_id, @cadastro),
    (@p_mussarela, @i_molho, 'ADICIONADO', NULL, 70.000, @usuario_id, @cadastro),
    (@p_portuguesa, @i_farinha, 'ADICIONADO', NULL, 200.000, @usuario_id, @cadastro),
    (@p_portuguesa, @i_mussarela, 'ADICIONADO', NULL, 150.000, @usuario_id, @cadastro),
    (@p_portuguesa, @i_presunto, 'ADICIONADO', NULL, 100.000, @usuario_id, @cadastro),
    (@p_portuguesa, @i_cebola, 'ADICIONADO', NULL, 25.000, @usuario_id, @cadastro),
    (@p_portuguesa, @i_azeitona, 'ADICIONADO', NULL, 15.000, @usuario_id, @cadastro),
    (@p_frango, @i_farinha, 'ADICIONADO', NULL, 200.000, @usuario_id, @cadastro),
    (@p_frango, @i_frango, 'ADICIONADO', NULL, 160.000, @usuario_id, @cadastro),
    (@p_frango, @i_catupiry, 'ADICIONADO', NULL, 100.000, @usuario_id, @cadastro),
    (@p_frango, @i_molho, 'ADICIONADO', NULL, 60.000, @usuario_id, @cadastro),
    (@p_frango, @i_milho, 'ADICIONADO', NULL, 40.000, @usuario_id, @cadastro),
    (@p_margherita, @i_farinha, 'ADICIONADO', NULL, 200.000, @usuario_id, @cadastro),
    (@p_margherita, @i_mussarela, 'ADICIONADO', NULL, 200.000, @usuario_id, @cadastro),
    (@p_margherita, @i_tomate, 'ADICIONADO', NULL, 80.000, @usuario_id, @cadastro),
    (@p_margherita, @i_molho, 'ADICIONADO', NULL, 50.000, @usuario_id, @cadastro),
    (@p_trem, @i_farinha, 'ADICIONADO', NULL, 300.000, @usuario_id, @cadastro),
    (@p_trem, @i_mussarela, 'ADICIONADO', NULL, 350.000, @usuario_id, @cadastro),
    (@p_trem, @i_calabresa, 'ADICIONADO', NULL, 180.000, @usuario_id, @cadastro),
    (@p_trem, @i_molho, 'ADICIONADO', NULL, 100.000, @usuario_id, @cadastro),
    (@p_trem, @i_cebola, 'ADICIONADO', NULL, 50.000, @usuario_id, @cadastro),
    (@p_beirute_frango, @i_frango, 'ADICIONADO', NULL, 120.000, @usuario_id, @cadastro),
    (@p_beirute_frango, @i_catupiry, 'ADICIONADO', NULL, 80.000, @usuario_id, @cadastro),
    (@p_beirute_frango, @i_tomate, 'ADICIONADO', NULL, 40.000, @usuario_id, @cadastro),
    (@p_beirute_calabresa, @i_calabresa, 'ADICIONADO', NULL, 100.000, @usuario_id, @cadastro),
    (@p_beirute_calabresa, @i_mussarela, 'ADICIONADO', NULL, 120.000, @usuario_id, @cadastro),
    (@p_beirute_calabresa, @i_tomate, 'ADICIONADO', NULL, 50.000, @usuario_id, @cadastro),
    (@p_beirute_calabresa, @i_cebola, 'ADICIONADO', NULL, 20.000, @usuario_id, @cadastro),
    (@p_brocolis, @i_mussarela, 'ADICIONADO', NULL, 100.000, @usuario_id, @cadastro),
    (@p_beirute_provolone, @i_provolone, 'ADICIONADO', NULL, 80.000, @usuario_id, @cadastro),
    (@p_chocolate_branco, @i_chocolate_branco, 'ADICIONADO', NULL, 10.000, @usuario_id, @cadastro),
    (@p_calabresa, @i_caixa_g, 'ADICIONADO', NULL, 1.000, @usuario_id, @cadastro),
    (@p_mussarela, @i_caixa_g, 'ADICIONADO', NULL, 1.000, @usuario_id, @cadastro),
    (@p_portuguesa, @i_caixa_g, 'ADICIONADO', NULL, 1.000, @usuario_id, @cadastro),
    (@p_frango, @i_caixa_g, 'ADICIONADO', NULL, 1.000, @usuario_id, @cadastro),
    (@p_margherita, @i_caixa_g, 'ADICIONADO', NULL, 1.000, @usuario_id, @cadastro),
    (@p_beirute_frango, @i_caixa_m, 'ADICIONADO', NULL, 1.000, @usuario_id, @cadastro),
    (@p_beirute_calabresa, @i_caixa_m, 'ADICIONADO', NULL, 1.000, @usuario_id, @cadastro),
    (@p_beirute_provolone, @i_caixa_m, 'ADICIONADO', NULL, 1.000, @usuario_id, @cadastro);

-- Limites de estoque baixo para os insumos acompanhados pelo cargo.
INSERT INTO alerta_estoque_baixo
(cargo_id, insumo_id, quantidade_minima, ativo, created_at, updated_at)
VALUES
    (@cargo_id, @i_mussarela, 2000.000, TRUE, @cadastro, @cadastro),
    (@cargo_id, @i_farinha, 3000.000, TRUE, @cadastro, @cadastro),
    (@cargo_id, @i_calabresa, 1800.000, TRUE, @cadastro, @cadastro),
    (@cargo_id, @i_molho, 1500.000, TRUE, @cadastro, @cadastro),
    (@cargo_id, @i_cebola, 700.000, TRUE, @cadastro, @cadastro),
    (@cargo_id, @i_coca, 10.000, TRUE, @cadastro, @cadastro),
    (@cargo_id, @i_provolone, 100.000, TRUE, @cadastro, @cadastro),
    (@cargo_id, @i_chocolate_branco, 10.000, TRUE, @cadastro, @cadastro),
    (@cargo_id, @i_pepperoni, 10.000, TRUE, @cadastro, @cadastro);

-- Histórico concluído e prévias de TXT/manual disponíveis para editar e confirmar.
INSERT INTO importacao
(id, origem, nome_arquivo, data_referencia, created_at, usuario_id, status)
VALUES
    (1, 'TXT', 'demonstracao-semana-1.txt', @data_s1, @envio_s1, @operador_id, 'CONCLUIDO'),
    (2, 'TXT', 'demonstracao-semana-2.txt', @data_s2, @envio_s2, @operador_id, 'CONCLUIDO'),
    (3, 'TXT', 'demonstracao-semana-3.txt', @data_s3, @envio_s3, @operador_id, 'CONCLUIDO'),
    (4, 'TXT', 'demonstracao-semana-4.txt', @data_s4, @envio_s4, @operador_id, 'CONCLUIDO'),
    (5, 'MANUAL', NULL, DATE(@manual), @manual, @usuario_id, 'CONCLUIDO'),
    (6, 'TXT', 'demonstracao-previa.txt', CURDATE(), @agora, @usuario_id, 'PREVIA'),
    (7, 'MANUAL', NULL, CURDATE(), @agora, @operador_id, 'PREVIA'),
    (8, 'TXT', 'demonstracao-estoque-insuficiente.txt', DATE(@ontem), @ontem, @usuario_id, 'CONCLUIDO'),
    (9, 'MANUAL', NULL, DATE(@ontem), @ontem, @usuario_id, 'CONCLUIDO'),
    (10, 'TXT', 'demonstracao-falha.txt', DATE(@ontem), @ontem, @usuario_id, 'CONCLUIDO'),
    (11, 'MANUAL', NULL, CURDATE(), @agora, @usuario_id, 'PREVIA');

-- Linhas válidas, rejeitadas e processadas, erros preservam a identificação da linha.
INSERT INTO importacao_linha
(id, importacao_id, numero_linha, nome_atual, produto_id, quantidade_atual, status, erro_codigo, erro_mensagem, processado_em, updated_at)
VALUES
    (1, 1, 1, 'Pizza Calabresa', @p_calabresa, 2, 'PROCESSADA', NULL, NULL, @processado_s1, NULL),
    (2, 1, 2, 'Pizza Mussarela', @p_mussarela, 3, 'PROCESSADA', NULL, NULL, @processado_s1, NULL),
    (3, 2, 1, 'Pizza Calabresa', @p_calabresa, 3, 'PROCESSADA', NULL, NULL, @processado_s2, NULL),
    (4, 2, 2, 'Pizza Mussarela', @p_mussarela, 4, 'PROCESSADA', NULL, NULL, @processado_s2, NULL),
    (5, 3, 1, 'Pizza Calabresa', @p_calabresa, 4, 'PROCESSADA', NULL, NULL, @processado_s3, NULL),
    (6, 3, 2, 'Pizza Mussarela', @p_mussarela, 5, 'PROCESSADA', NULL, NULL, @processado_s3, NULL),
    (7, 4, 1, 'Pizza Calabresa', @p_calabresa, 5, 'PROCESSADA', NULL, NULL, @processado_s4, NULL),
    (8, 4, 2, 'Pizza Mussarela', @p_mussarela, 6, 'PROCESSADA', NULL, NULL, @processado_s4, NULL),
    (9, 5, 1, 'Pizza Calabresa', @p_calabresa, 5, 'PROCESSADA', NULL, NULL, @processado_manual, @personalizado_manual),
    (10, 6, 1, 'Pizza Calabresa', @p_calabresa, 2, 'PENDENTE', NULL, NULL, NULL, NULL),
    (11, 6, 2, 'Pizza de Atum', NULL, 1, 'INVALIDA', 'LINHA_INVALIDA', 'Produto inexistente.', NULL, NULL),
    (12, 6, 3, 'Pizza Mussarela', NULL, NULL, 'INVALIDA', 'FORMATO_INVALIDO', 'ID e quantidade devem ser inteiros positivos; nome é obrigatório.', NULL, NULL),
    (13, 6, 4, 'Pizza de Brócolis', @p_brocolis, 1, 'INVALIDA', 'LINHA_INVALIDA', 'Produto desativado.', NULL, NULL),
    (14, 6, 5, 'Pizza Quatro Queijos', @p_quatro_queijos, 1, 'INVALIDA', 'LINHA_INVALIDA', 'Produto sem receita.', NULL, NULL),
    (15, 7, 1, 'Pizza Mussarela', @p_mussarela, 3, 'PENDENTE', NULL, NULL, NULL, NULL),
    (16, 7, 2, 'Pizza Frango com Catupiry', @p_frango, 1, 'PENDENTE', NULL, NULL, NULL, NULL),
    (17, 7, 3, 'Beirute de Provolone', @p_beirute_provolone, 5, 'PENDENTE', NULL, NULL, NULL, NULL),
    (18, 8, 1, 'Beirute de Provolone', @p_beirute_provolone, 5, 'PROCESSADA', NULL, NULL, @processado_ontem, NULL),
    (19, 8, 2, 'Pizza Portuguesa', NULL, NULL, 'INVALIDA', 'FORMATO_INVALIDO', 'ID e quantidade devem ser inteiros positivos; nome é obrigatório.', NULL, NULL),
    (20, 8, 3, 'Pizza de Chocolate Branco', @p_chocolate_branco, 2, 'PROCESSADA', NULL, NULL, @processado_ontem, NULL),
    (21, 9, 1, 'Pizza Calabresa', @p_calabresa, 1, 'PROCESSADA', NULL, NULL, @processado_ontem, @personalizado_ontem),
    (22, 10, 1, 'Pizza Frango com Catupiry', @p_frango, 1, 'INVALIDA', 'FALHA_PROCESSAMENTO', 'Não foi possível processar este item. Nenhum ingrediente dele foi descontado.', NULL, NULL),
    (23, 11, 1, 'Pizza Calabresa', @p_calabresa, 5, 'PENDENTE', NULL, NULL, NULL, @agora);

-- Consumo por ingrediente: previsto na prévia e efetivado após a confirmação.
-- Inclui personalização e baixa parcial, linhas sem ingredientes não geram consumo.
INSERT INTO importacao_linha_consumo
(id, linha_id, insumo_id, quantidade_prevista, quantidade_efetivada, aviso)
VALUES
    (1, 1, @i_farinha, 400.000, 400.000, NULL),
    (2, 1, @i_mussarela, 500.000, 500.000, NULL),
    (3, 1, @i_calabresa, 200.000, 200.000, NULL),
    (4, 1, @i_molho, 160.000, 160.000, NULL),
    (5, 1, @i_cebola, 80.000, 80.000, NULL),
    (6, 1, @i_azeitona, 20.000, 20.000, NULL),
    (7, 2, @i_farinha, 600.000, 600.000, NULL),
    (8, 2, @i_mussarela, 840.000, 840.000, NULL),
    (9, 2, @i_molho, 210.000, 210.000, NULL),
    (10, 3, @i_farinha, 600.000, 600.000, NULL),
    (11, 3, @i_mussarela, 750.000, 750.000, NULL),
    (12, 3, @i_calabresa, 300.000, 300.000, NULL),
    (13, 3, @i_molho, 240.000, 240.000, NULL),
    (14, 3, @i_cebola, 120.000, 120.000, NULL),
    (15, 3, @i_azeitona, 30.000, 30.000, NULL),
    (16, 4, @i_farinha, 800.000, 800.000, NULL),
    (17, 4, @i_mussarela, 1120.000, 1120.000, NULL),
    (18, 4, @i_molho, 280.000, 280.000, NULL),
    (19, 5, @i_farinha, 800.000, 800.000, NULL),
    (20, 5, @i_mussarela, 1000.000, 1000.000, NULL),
    (21, 5, @i_calabresa, 400.000, 400.000, NULL),
    (22, 5, @i_molho, 320.000, 320.000, NULL),
    (23, 5, @i_cebola, 160.000, 160.000, NULL),
    (24, 5, @i_azeitona, 40.000, 40.000, NULL),
    (25, 6, @i_farinha, 1000.000, 1000.000, NULL),
    (26, 6, @i_mussarela, 1400.000, 1400.000, NULL),
    (27, 6, @i_molho, 350.000, 350.000, NULL),
    (28, 7, @i_farinha, 1000.000, 1000.000, NULL),
    (29, 7, @i_mussarela, 1250.000, 1250.000, NULL),
    (30, 7, @i_calabresa, 500.000, 500.000, NULL),
    (31, 7, @i_molho, 400.000, 400.000, NULL),
    (32, 7, @i_cebola, 200.000, 200.000, NULL),
    (33, 7, @i_azeitona, 50.000, 50.000, NULL),
    (34, 8, @i_farinha, 1200.000, 1200.000, NULL),
    (35, 8, @i_mussarela, 1680.000, 1680.000, NULL),
    (36, 8, @i_molho, 420.000, 420.000, NULL),
    (37, 9, @i_farinha, 1000.000, 1000.000, NULL),
    (38, 9, @i_mussarela, 1250.000, 1250.000, NULL),
    (39, 9, @i_calabresa, 500.000, 500.000, NULL),
    (40, 9, @i_molho, 400.000, 400.000, NULL),
    (41, 9, @i_cebola, 160.000, 160.000, NULL),
    (42, 9, @i_azeitona, 60.000, 60.000, NULL),
    (43, 9, @i_cream_cheese, 15.000, 15.000, NULL),
    (44, 10, @i_farinha, 400.000, NULL, NULL),
    (45, 10, @i_mussarela, 500.000, NULL, NULL),
    (46, 10, @i_calabresa, 200.000, NULL, NULL),
    (47, 10, @i_molho, 160.000, NULL, NULL),
    (48, 10, @i_cebola, 80.000, NULL, NULL),
    (49, 10, @i_azeitona, 20.000, NULL, NULL),
    (50, 15, @i_farinha, 600.000, NULL, NULL),
    (51, 15, @i_mussarela, 840.000, NULL, NULL),
    (52, 15, @i_molho, 210.000, NULL, NULL),
    (53, 16, @i_farinha, 200.000, NULL, NULL),
    (54, 16, @i_frango, 160.000, NULL, NULL),
    (55, 16, @i_catupiry, 100.000, NULL, NULL),
    (56, 16, @i_molho, 60.000, NULL, NULL),
    (57, 16, @i_milho, 40.000, NULL, NULL),
    (58, 17, @i_provolone, 400.000, NULL, NULL),
    (59, 18, @i_provolone, 400.000, 120.000, 'Estoque insuficiente: saldo zerado; descontada somente a quantidade disponível.'),
    (60, 20, @i_chocolate_branco, 20.000, 0.000, 'Estoque insuficiente: saldo zerado; descontada somente a quantidade disponível.'),
    (61, 22, @i_farinha, 200.000, NULL, NULL),
    (62, 22, @i_frango, 160.000, NULL, NULL),
    (63, 22, @i_catupiry, 100.000, NULL, NULL),
    (64, 22, @i_molho, 60.000, NULL, NULL),
    (65, 22, @i_milho, 40.000, NULL, NULL),
    (66, 23, @i_farinha, 1000.000, NULL, NULL),
    (67, 23, @i_mussarela, 1250.000, NULL, NULL),
    (68, 23, @i_calabresa, 500.000, NULL, NULL),
    (69, 23, @i_molho, 400.000, NULL, NULL),
    (70, 23, @i_cebola, 160.000, NULL, NULL),
    (71, 23, @i_azeitona, 60.000, NULL, NULL),
    (72, 23, @i_cream_cheese, 15.000, NULL, NULL),
    (73, 1, @i_caixa_g, 2.000, 2.000, NULL),
    (74, 2, @i_caixa_g, 3.000, 3.000, NULL),
    (75, 3, @i_caixa_g, 3.000, 3.000, NULL),
    (76, 4, @i_caixa_g, 4.000, 4.000, NULL),
    (77, 5, @i_caixa_g, 4.000, 4.000, NULL),
    (78, 6, @i_caixa_g, 5.000, 5.000, NULL),
    (79, 7, @i_caixa_g, 5.000, 5.000, NULL),
    (80, 8, @i_caixa_g, 6.000, 6.000, NULL),
    (81, 9, @i_caixa_g, 5.000, 5.000, NULL),
    (82, 10, @i_caixa_g, 2.000, NULL, NULL),
    (83, 15, @i_caixa_g, 3.000, NULL, NULL),
    (84, 16, @i_caixa_g, 1.000, NULL, NULL),
    (85, 22, @i_caixa_g, 1.000, NULL, NULL),
    (86, 23, @i_caixa_g, 5.000, NULL, NULL),
    (87, 17, @i_caixa_m, 5.000, NULL, NULL),
    (88, 18, @i_caixa_m, 5.000, 5.000, NULL);

-- Saldo inicial, compras líquidas de perda, consumo, quebras e ajustes de estoque.
-- Só consumos efetivados acima de zero geram movimentação.
INSERT INTO movimentacao_estoque
(insumo_id, importacao_linha_consumo_id, usuario_id, tipo, motivo, quantidade, observacao, data_hora)
VALUES
    (@i_mussarela, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 28600.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_farinha, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 20800.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_calabresa, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 14200.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_molho, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 11500.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_cebola, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 4300.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_tomate, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 6000.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_azeitona, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1300.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_frango, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1000.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_catupiry, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1000.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_presunto, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 3500.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_bacon, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 2200.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_milho, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1700.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_ervilha, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1400.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_palmito, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1000.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_champignon, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 900.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_requeijao, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1600.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_parmesao, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1200.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_cheddar, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1300.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_carne_seca, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1500.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_chocolate, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 2800.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_banana, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1900.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_canela, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 250.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_morango, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1000.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_coco_ralado, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 800.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_batata_palha, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1800.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_maca, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 900.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_peito_de_peru, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1700.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_coca, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 18.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_guarana, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 20.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_caixa_g, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 25.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_caixa_m, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 30.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_suco, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 12.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_agua_500ml, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 20.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_refrigerante_lata, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 48.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_embalagem_sobremesa, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 25.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_guardanapo_pacote, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 10.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_sacola_delivery, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 80.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_brocolis, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 0.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_alface, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 0.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_gorgonzola, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 0.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_pessego, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 0.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_caixa_de_pizza_broto, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 0.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_copo_descartavel, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 0.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_provolone, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 120.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_chocolate_branco, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 0.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_cream_cheese, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 1000.000, 'Saldo inicial', DATE_ADD(@cadastro, INTERVAL 1 MINUTE)),
    (@i_pepperoni, NULL, @usuario_id, 'ENTRADA', 'SALDO_INICIAL', 5.000, 'Saldo inicial', DATE_ADD(@cadastro_recente, INTERVAL 1 MINUTE)),
    (@i_mussarela, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 9500.000, 'Compra semana 1', @compra_s1),
    (@i_farinha, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 8000.000, 'Compra semana 1', @compra_s1),
    (@i_calabresa, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 4750.000, 'Compra semana 1', @compra_s1),
    (@i_caixa_g, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 20.000, 'Compra semana 1', @compra_s1),
    (@i_mussarela, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 9500.000, 'Compra semana 2', @compra_s2),
    (@i_farinha, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 8000.000, 'Compra semana 2', @compra_s2),
    (@i_calabresa, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 4750.000, 'Compra semana 2', @compra_s2),
    (@i_caixa_g, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 20.000, 'Compra semana 2', @compra_s2),
    (@i_mussarela, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 9500.000, 'Compra semana 3', @compra_s3),
    (@i_farinha, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 8000.000, 'Compra semana 3', @compra_s3),
    (@i_calabresa, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 4750.000, 'Compra semana 3', @compra_s3),
    (@i_caixa_g, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 20.000, 'Compra semana 3', @compra_s3),
    (@i_mussarela, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 9500.000, 'Compra semana 4', @compra_s4),
    (@i_farinha, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 8000.000, 'Compra semana 4', @compra_s4),
    (@i_calabresa, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 4750.000, 'Compra semana 4', @compra_s4),
    (@i_caixa_g, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 20.000, 'Compra semana 4', @compra_s4),
    (@i_farinha, 1, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 400.000, NULL, @processado_s1),
    (@i_mussarela, 2, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 500.000, NULL, @processado_s1),
    (@i_calabresa, 3, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 200.000, NULL, @processado_s1),
    (@i_molho, 4, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 160.000, NULL, @processado_s1),
    (@i_cebola, 5, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 80.000, NULL, @processado_s1),
    (@i_azeitona, 6, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 20.000, NULL, @processado_s1),
    (@i_farinha, 7, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 600.000, NULL, @processado_s1),
    (@i_mussarela, 8, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 840.000, NULL, @processado_s1),
    (@i_molho, 9, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 210.000, NULL, @processado_s1),
    (@i_farinha, 10, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 600.000, NULL, @processado_s2),
    (@i_mussarela, 11, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 750.000, NULL, @processado_s2),
    (@i_calabresa, 12, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 300.000, NULL, @processado_s2),
    (@i_molho, 13, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 240.000, NULL, @processado_s2),
    (@i_cebola, 14, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 120.000, NULL, @processado_s2),
    (@i_azeitona, 15, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 30.000, NULL, @processado_s2),
    (@i_farinha, 16, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 800.000, NULL, @processado_s2),
    (@i_mussarela, 17, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 1120.000, NULL, @processado_s2),
    (@i_molho, 18, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 280.000, NULL, @processado_s2),
    (@i_farinha, 19, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 800.000, NULL, @processado_s3),
    (@i_mussarela, 20, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 1000.000, NULL, @processado_s3),
    (@i_calabresa, 21, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 400.000, NULL, @processado_s3),
    (@i_molho, 22, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 320.000, NULL, @processado_s3),
    (@i_cebola, 23, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 160.000, NULL, @processado_s3),
    (@i_azeitona, 24, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 40.000, NULL, @processado_s3),
    (@i_farinha, 25, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 1000.000, NULL, @processado_s3),
    (@i_mussarela, 26, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 1400.000, NULL, @processado_s3),
    (@i_molho, 27, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 350.000, NULL, @processado_s3),
    (@i_farinha, 28, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 1000.000, NULL, @processado_s4),
    (@i_mussarela, 29, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 1250.000, NULL, @processado_s4),
    (@i_calabresa, 30, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 500.000, NULL, @processado_s4),
    (@i_molho, 31, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 400.000, NULL, @processado_s4),
    (@i_cebola, 32, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 200.000, NULL, @processado_s4),
    (@i_azeitona, 33, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 50.000, NULL, @processado_s4),
    (@i_farinha, 34, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 1200.000, NULL, @processado_s4),
    (@i_mussarela, 35, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 1680.000, NULL, @processado_s4),
    (@i_molho, 36, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 420.000, NULL, @processado_s4),
    (@i_farinha, 37, @usuario_id, 'SAIDA', 'CONSUMO_PRODUTO', 1000.000, NULL, @processado_manual),
    (@i_mussarela, 38, @usuario_id, 'SAIDA', 'CONSUMO_PRODUTO', 1250.000, NULL, @processado_manual),
    (@i_calabresa, 39, @usuario_id, 'SAIDA', 'CONSUMO_PRODUTO', 500.000, NULL, @processado_manual),
    (@i_molho, 40, @usuario_id, 'SAIDA', 'CONSUMO_PRODUTO', 400.000, NULL, @processado_manual),
    (@i_cebola, 41, @usuario_id, 'SAIDA', 'CONSUMO_PRODUTO', 160.000, NULL, @processado_manual),
    (@i_azeitona, 42, @usuario_id, 'SAIDA', 'CONSUMO_PRODUTO', 60.000, NULL, @processado_manual),
    (@i_cream_cheese, 43, @usuario_id, 'SAIDA', 'CONSUMO_PRODUTO', 15.000, NULL, @processado_manual),
    (@i_provolone, 59, @usuario_id, 'SAIDA', 'CONSUMO_PRODUTO', 120.000, NULL, @processado_ontem),
    (@i_mussarela, NULL, @usuario_id, 'SAIDA', 'QUEBRA', 150.000, 'Produto danificado', @quebra_mussarela),
    (@i_frango, NULL, @usuario_id, 'SAIDA', 'QUEBRA', 100.000, 'Produto danificado', @quebra_frango),
    (@i_caixa_g, NULL, @usuario_id, 'SAIDA', 'OUTRO', 2.000, 'Uso de embalagens', @saida_embalagens),
    (@i_mussarela, NULL, @usuario_id, 'ENTRADA', 'AJUSTE', 1250.000, 'Conferência de estoque', @ajuste_mussarela),
    (@i_cebola, NULL, @usuario_id, 'SAIDA', 'AJUSTE', 100.000, 'Conferência de estoque', @ajuste_cebola),
    (@i_provolone, NULL, @usuario_id, 'ENTRADA', 'COMPRA', 60.000, 'Reposição de provolone', @agora),
    (@i_caixa_g, 73, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 2.000, NULL, @processado_s1),
    (@i_caixa_g, 74, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 3.000, NULL, @processado_s1),
    (@i_caixa_g, 75, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 3.000, NULL, @processado_s2),
    (@i_caixa_g, 76, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 4.000, NULL, @processado_s2),
    (@i_caixa_g, 77, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 4.000, NULL, @processado_s3),
    (@i_caixa_g, 78, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 5.000, NULL, @processado_s3),
    (@i_caixa_g, 79, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 5.000, NULL, @processado_s4),
    (@i_caixa_g, 80, @operador_id, 'SAIDA', 'CONSUMO_PRODUTO', 6.000, NULL, @processado_s4),
    (@i_caixa_g, 81, @usuario_id, 'SAIDA', 'CONSUMO_PRODUTO', 5.000, NULL, @processado_manual),
    (@i_caixa_m, 88, @usuario_id, 'SAIDA', 'CONSUMO_PRODUTO', 5.000, NULL, @processado_ontem),
    (@i_caixa_g, NULL, @usuario_id, 'SAIDA', 'QUEBRA', 3.000, 'Embalagens danificadas', @quebra_embalagens);

COMMIT;
