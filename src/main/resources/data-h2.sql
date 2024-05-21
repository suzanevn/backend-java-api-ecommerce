INSERT INTO USUARIOS(id, login, senha, role) VALUES
    (
        1,
        'admin',
        '$2a$10$A6pkZBY9kHL922FAv1diaOqwTswnjZuKtVSGAdfKajyphsxoaf2ii', --password
        'ADMIN'
    ),
    (
        2,
        'user',
        '$2a$10$0UGRGmithRKlAVd4P7.CnO/6OAOeOEKrTzUsRrkOT9k10AyVjz.KC', --senha
        'USER'
    )
;
ALTER SEQUENCE USUARIOS_SEQ RESTART WITH 3;


INSERT INTO categorias (id, nome) VALUES
    (
        1,
        'Eletronico'
    ),
    (
        2,
        'Jardim'
    ),
    (
        3,
        'Kit'
    )
;
ALTER SEQUENCE CATEGORIAS_SEQ RESTART WITH 4;

INSERT INTO produtos (id, nome, descricao, referencia, valor_unitario, categoria_id) VALUES
    (
        1,
        'Motosserra Semi Profissional Gasolina 50,2Cc Tcs53X-20',
        'As Motosserras Toyama Foram Desenvolvidas E Fabricadas Especialmente Para O Usuário Que Busca Qualidade De Equipamento Com Baixo Custo De Aquisição. Para Torná-La Leve, Durável E Confiável Utilizamos Em Sua Produção, Liga De Magnésio De Alta Resistência, Equipadas Com As Correntes Oregon.',
        'TCS53H18',
        399.90,
        1
    ),
    (
        2,
        'Tesoura Para Poda + Serrote de Poda',
        'Tesourão para Poda Linha Bronze, para podar frutíferas, flores e plantas ornamentais, galhos e ramos de árvores onde não é possível alcançar com as tesouras normais. Para ajudar no seu trabalho, esse jogo contém um serrote para poda com 2 lâminas.',
        'KIT140',
        163.54,
        3
    ),
    (
        3,
        'Kit para Jardinagem Horta Tramontina 04 Peças com Luva',
        'Kit para Jardinagem Horta Tramontina 04 Peças com Luva. Kit completo com a qualidade Tramontina para pequenas hortas, jardins, hobby, floricultura.',
        '55374237',
        57.34,
        3
    ),
    (
       4,
        'Enxada',
        'Enxada Tramontina para uso em jardins ou reformas.',
        'ENX1234',
        80.50,
        2
    )
;

ALTER SEQUENCE PRODUTOS_SEQ RESTART WITH 5;
