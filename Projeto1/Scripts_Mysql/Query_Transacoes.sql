SHOW databases;
USE banco_dados_banco_av2;

CREATE TABLE transacoes_bancaria (
    ID_TRANSACAO INT PRIMARY KEY AUTO_INCREMENT,
    ID_CONTA INT,
    DATA_TRANSACAO DATE,
    VALOR_TRANSACAO DECIMAL(10, 2),
    ID_TIPO INT,
    FOREIGN KEY (ID_CONTA) REFERENCES conta_bancaria (ID_CONTA),
    FOREIGN KEY (ID_TIPO) REFERENCES tipo_bancaria (ID_TIPO)
);

SHOW TABLES;
describe transacoes_bancaria;
SELECT * FROM transacoes_bancaria;

drop table transacoes_bancaria;