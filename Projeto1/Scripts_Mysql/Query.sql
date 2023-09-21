CREATE DATABASE banco_dados_banco_av2;
SHOW databases;
USE banco_dados_banco_av2;

CREATE TABLE conta_bancaria (
    ID_CONTA INT PRIMARY KEY AUTO_INCREMENT,
    TIPO_CONTA VARCHAR(1),
    SALDO_CONTA DECIMAL(10, 2),
    NOME_CONTA VARCHAR(40),
    CPF_CONTA VARCHAR(11),
    STATUS_CONTA TINYINT(1),
    NUMERO_CONTA INT(4)
);

SHOW TABLES;
describe conta_bancaria;
SELECT * FROM conta_bancaria;



drop table conta_bancaria;
DELETE FROM conta_bancaria WHERE id_conta = 3