SHOW databases;
USE banco_dados_banco_av2;

CREATE TABLE tipo_bancaria (
    ID_TIPO INT PRIMARY KEY,
    TRANSACAO_TIPO VARCHAR(50)
);

INSERT INTO tipo_bancaria (ID_TIPO, TRANSACAO_TIPO) VALUES (1, 'pagamento');
INSERT INTO tipo_bancaria (ID_TIPO, TRANSACAO_TIPO) VALUES (2, 'deposito');
INSERT INTO tipo_bancaria (ID_TIPO, TRANSACAO_TIPO) VALUES (3, 'saque');
INSERT INTO tipo_bancaria (ID_TIPO, TRANSACAO_TIPO) VALUES (4, 'transferencia');



SHOW TABLES;
describe tipo_bancaria;
SELECT * FROM tipo_bancaria;


drop table tipo_bancaria;
ALTER TABLE transacoes_bancaria
DROP FOREIGN KEY transacoes_bancaria_ibfk_2;

DELETE FROM tipo_bancaria WHERE ID_TIPO = 5;



