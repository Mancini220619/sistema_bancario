

import java.util.Scanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class ContaBancaria {

    void abrirConta() throws SQLException {
        
            String tipo_conta = " ";
            boolean status_conta;
            double saldo_conta = 0;
            String nome_conta;
            String cpf_conta;
            

            Scanner tipoForm = new Scanner(System.in);
            
            System.out.println("Qual tipo da conta? 'C' ou 'P'");
            
            try {
            tipo_conta = tipoForm.next().toUpperCase();

            if (tipo_conta.equals("C")) {
                // Código para conta corrente
                System.out.println("Conta corrente selecionada.");
                tipo_conta = "C";
            } else if (tipo_conta.equals("P")) {
                // Código para conta poupança
                System.out.println("Conta poupança selecionada.");
                tipo_conta = "P";
            } else {
                System.out.println("Tipo de conta inválido.");
            }
            } catch (Exception e) {
                System.out.println("Ocorreu um erro durante a leitura da entrada do usuário.");
            }
        
        if (tipo_conta == "C"){
            saldo_conta = 50.00;
            System.out.println("Conta com saldo inicial de R$ " + saldo_conta);
        }else{
            saldo_conta = 150.00;
            System.out.println("Conta com saldo inicial de R$ " + saldo_conta);
            
        }    
        
        
//dados de ingresso para abertura de conta
        
        Scanner inputNome = new Scanner(System.in);
        System.out.println("Digite o nome completo: ");
        nome_conta = inputNome.nextLine().toUpperCase();
        
        Scanner inputCpf = new Scanner(System.in);
        System.out.println("Digite o CPF: ");
        cpf_conta = inputCpf.nextLine();
        System.out.println("CPF formatado: " + cpf_conta);
        
        status_conta = true;
        
        System.out.println("Conta Ativa");
        
        
    // inserir os dados no banco de dados
        try (Connection connection = ConnectionDB.getConexaoMySQL()) {
            String sql = "INSERT INTO conta_bancaria (tipo_conta, saldo_conta, nome_conta, cpf_conta, status_conta,numero_conta) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, tipo_conta);
            statement.setDouble(2, saldo_conta);
            statement.setString(3, nome_conta);
            statement.setString(4, cpf_conta);
            statement.setBoolean(5, status_conta);
            statement.setInt(6, gerarNumeroConta(connection));


            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Dados da conta inseridos com sucesso no banco de dados.");
            } else {
                System.out.println("Falha ao inserir os dados da conta no banco de dados.");
            }
        }catch (SQLException e) {
            System.out.println("Ocorreu um erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    
   
    //classe para gerar numero de conta 
        private int gerarNumeroConta(Connection connection) throws SQLException {
            int numeroConta;
            String query = "SELECT MAX(numero_conta) FROM conta_bancaria";
            try (PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int maxNumeroConta = resultSet.getInt(1);
                        if (maxNumeroConta == 0){
                            int base = 1000;
                            numeroConta = base;

                        }else{
                            int proximoNumero = (maxNumeroConta + 1); // Calcula o próximo número de conta
                            numeroConta = proximoNumero;
                        }
                    
                    
                } else {
                    numeroConta = 1000;
                }
            }
            return numeroConta;
}

        
    //empurrar tela
        void empurrarTela() {

            for (int i = 0; i < 1; i++) {
            System.out.println();
            }
        }
        
    //consultando contas bancarias
        
    void listarContas() {
        try (Connection connection = ConnectionDB.getConexaoMySQL()) {
            String sql = "SELECT tipo_conta, nome_conta, status_conta, numero_conta FROM conta_bancaria";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String tipoConta = resultSet.getString("tipo_conta");
                String nomeConta = resultSet.getString("nome_conta");
                boolean statusConta = resultSet.getBoolean("status_conta");
                int numeroConta = resultSet.getInt("numero_conta");

                System.out.println("Tipo da conta: " + tipoConta + " Nome: " + nomeConta + " Status: " + (statusConta ? "Ativa" : "Inativa") + " Número da conta: " + numeroConta );

                empurrarTela();
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Ocorreu um erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }


    //operar conta, saldo, extrato, pagamento, deposito, transferencia e saque
    
    void operarConta() {
        int numeroDigitado = 0;

        Scanner digiteConta = new Scanner(System.in);
        System.out.println("Entre com o numero da conta com 4 digitos: ");
        numeroDigitado = digiteConta.nextInt();

        empurrarTela();

        // verificar se a conta existe
        if (verificarContaExiste(numeroDigitado)) {
            int variacao = 0;

            do {
                System.out.println("----------------$$$$---------------");
                System.out.println("1-SALDO");
                System.out.println("2-EXTRATO");
                System.out.println("3-PAGAMENTO");
                System.out.println("4-DEPOSITO");
                System.out.println("5-TRANSFERÊNCIA");
                System.out.println("6-SAQUE");
                System.out.println("7-VOLTAR AO MENU PRINCIPAL");

                Scanner digiteOperar = new Scanner(System.in);
                variacao = digiteOperar.nextInt();

                switch (variacao) {
                    case 1:
                        consultaSaldo(numeroDigitado);
                        break;
                    case 2:
                        consultarExtrato (numeroDigitado);
                        break;
                    case 3:
                        efetuarPagamento (numeroDigitado);
                        break;
                    case 4:
                        efetuarDeposito (numeroDigitado);
                        break;
                    case 5:
                        efetuarTransferencia ();
                        break;
                    case 6:
                        efetuarSaque (numeroDigitado);
                        break;
                    case 7:
                        System.out.println("Voltando ao menu principal...");
                        empurrarTela();
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                        break;
                }
            } while (variacao != 7);
        } else {
            System.out.println("A conta de número " + numeroDigitado + " não existe.");
        }
    }
            //verificando se conta existe no DB
    boolean verificarContaExiste(int numeroConta) {
        try (Connection connection = ConnectionDB.getConexaoMySQL()) {
            String sql = "SELECT * FROM conta_bancaria WHERE NUMERO_CONTA = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, numeroConta);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                resultSet.close();
                statement.close();
                return true; // A conta existe
            } else {
                resultSet.close();
                statement.close();
                return false; // A conta não existe
            }
        } catch (SQLException e) {
            System.out.println("Ocorreu um erro ao conectar ao banco de dados: " + e.getMessage());
            return false; // Erro ao conectar ao banco de dados
        }
    }
    
    //metodo para consulta de saldo, numero da conta e nome

    void consultaSaldo(int numeroDigitado) {
        try (Connection connection = ConnectionDB.getConexaoMySQL()) {
            String sql = "SELECT SALDO_CONTA, NOME_CONTA, NUMERO_CONTA FROM conta_bancaria WHERE NUMERO_CONTA = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, numeroDigitado);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                double saldoConta = resultSet.getDouble("SALDO_CONTA");
                String nomeConta = resultSet.getString("NOME_CONTA");
                int numeroConta = resultSet.getInt("NUMERO_CONTA");

                System.out.println("Saldo da conta: R$ " + saldoConta);
                System.out.println("Nome do titular: " + nomeConta);
                System.out.println("Número da conta: " + numeroConta);
            } else {
                System.out.println("A conta de número " + numeroDigitado + " não existe.");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Ocorreu um erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }
    
    // pagamento de conta
    
void efetuarPagamento(int numeroDigitado) {
    Scanner inputValorPagamento = new Scanner(System.in);
    System.out.println("Digite o valor do pagamento: ");
    double valorPagamento = inputValorPagamento.nextDouble();

    try (Connection connection = ConnectionDB.getConexaoMySQL()) {
        // Obter o ID da conta com base no número da conta
        String idContaQuery = "SELECT ID_CONTA FROM conta_bancaria WHERE NUMERO_CONTA = ?";
        PreparedStatement idContaStatement = connection.prepareStatement(idContaQuery);
        idContaStatement.setInt(1, numeroDigitado);

        ResultSet idContaResult = idContaStatement.executeQuery();

        if (idContaResult.next()) {
            int idConta = idContaResult.getInt("ID_CONTA");
            System.out.println("ID da conta: " + idConta);

            // Verificar o saldo da conta
            String saldoQuery = "SELECT SALDO_CONTA FROM conta_bancaria WHERE ID_CONTA = ?";
            PreparedStatement saldoStatement = connection.prepareStatement(saldoQuery);
            saldoStatement.setInt(1, idConta);

            ResultSet saldoResult = saldoStatement.executeQuery();

            if (saldoResult.next()) {
                double saldoConta = saldoResult.getDouble("SALDO_CONTA");
                System.out.println("Saldo da conta: R$ " + saldoConta);

                if (valorPagamento <= saldoConta) {
                    System.out.println("Pagamento aprovado.");

                    // Inserir a transação no banco de dados
                    String insertTransacaoQuery = "INSERT INTO transacoes_bancaria (ID_CONTA, DATA_TRANSACAO, VALOR_TRANSACAO, ID_TIPO) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertTransacaoStatement = connection.prepareStatement(insertTransacaoQuery);
                    insertTransacaoStatement.setInt(1, idConta);
                    insertTransacaoStatement.setDate(2, new Date(System.currentTimeMillis()));
                    insertTransacaoStatement.setDouble(3, valorPagamento);
                    insertTransacaoStatement.setInt(4, 1); // ID_TIPO 1 representa o tipo "pagamento"

                    int rowsInserted = insertTransacaoStatement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Transação registrada com sucesso.");

                        // Atualizar o saldo na tabela conta_bancaria
                        double novoSaldo = saldoConta - valorPagamento;
                        String updateSaldoQuery = "UPDATE conta_bancaria SET SALDO_CONTA = ? WHERE ID_CONTA = ?";
                        PreparedStatement updateSaldoStatement = connection.prepareStatement(updateSaldoQuery);
                        updateSaldoStatement.setDouble(1, novoSaldo);
                        updateSaldoStatement.setInt(2, idConta);

                        int rowsUpdated = updateSaldoStatement.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("Saldo atualizado com sucesso.");
                        } else {
                            System.out.println("Falha ao atualizar o saldo.");
                        }

                        updateSaldoStatement.close();
                    } else {
                        System.out.println("Falha ao registrar a transação.");
                    }

                    insertTransacaoStatement.close();
                } else {
                    System.out.println("Saldo insuficiente para realizar o pagamento.");
                }
            } else {
                System.out.println("Não foi possível obter o saldo da conta.");
            }

            saldoResult.close();
            saldoStatement.close();
        } else {
            System.out.println("A conta de número " + numeroDigitado + " não existe.");
        }

        idContaResult.close();
        idContaStatement.close();
    } catch (SQLException e) {
        System.out.println("Ocorreu um erro ao conectar ao banco de dados: " + e.getMessage());
    }
}


//efetuar deposito 

void efetuarDeposito(int numeroDigitado) {
    Scanner inputValorDeposito = new Scanner(System.in);
    System.out.println("Digite o valor do depósito: ");
    double valorDeposito = inputValorDeposito.nextDouble();

    try (Connection connection = ConnectionDB.getConexaoMySQL()) {
        // Obter o ID da conta com base no número da conta
        String idContaQuery = "SELECT ID_CONTA FROM conta_bancaria WHERE NUMERO_CONTA = ?";
        PreparedStatement idContaStatement = connection.prepareStatement(idContaQuery);
        idContaStatement.setInt(1, numeroDigitado);

        ResultSet idContaResult = idContaStatement.executeQuery();

        if (idContaResult.next()) {
            int idConta = idContaResult.getInt("ID_CONTA");
            System.out.println("ID da conta: " + idConta);

            // Verificar o saldo da conta
            String saldoQuery = "SELECT SALDO_CONTA FROM conta_bancaria WHERE ID_CONTA = ?";
            PreparedStatement saldoStatement = connection.prepareStatement(saldoQuery);
            saldoStatement.setInt(1, idConta);

            ResultSet saldoResult = saldoStatement.executeQuery();

            if (saldoResult.next()) {
                double saldoConta = saldoResult.getDouble("SALDO_CONTA");
                System.out.println("Saldo da conta: R$ " + saldoConta);

                // Atualizar o saldo na tabela conta_bancaria
                double novoSaldo = saldoConta + valorDeposito;
                String updateSaldoQuery = "UPDATE conta_bancaria SET SALDO_CONTA = ? WHERE ID_CONTA = ?";
                PreparedStatement updateSaldoStatement = connection.prepareStatement(updateSaldoQuery);
                updateSaldoStatement.setDouble(1, novoSaldo);
                updateSaldoStatement.setInt(2, idConta);

                int rowsUpdated = updateSaldoStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Depósito realizado com sucesso.");

                    // Inserir a transação no banco de dados
                    String insertTransacaoQuery = "INSERT INTO transacoes_bancaria (ID_CONTA, DATA_TRANSACAO, VALOR_TRANSACAO, ID_TIPO) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertTransacaoStatement = connection.prepareStatement(insertTransacaoQuery);
                    insertTransacaoStatement.setInt(1, idConta);
                    insertTransacaoStatement.setDate(2, new Date(System.currentTimeMillis()));
                    insertTransacaoStatement.setDouble(3, valorDeposito);
                    insertTransacaoStatement.setInt(4, 2); // ID_TIPO 2 representa o tipo "depósito"

                    int rowsInserted = insertTransacaoStatement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Transação de depósito registrada com sucesso.");
                    } else {
                        System.out.println("Falha ao registrar a transação de depósito.");
                    }

                    insertTransacaoStatement.close();
                } else {
                    System.out.println("Falha ao realizar o depósito.");
                }

                updateSaldoStatement.close();
            } else {
                System.out.println("Não foi possível obter o saldo da conta.");
            }

            saldoResult.close();
            saldoStatement.close();
        } else {
            System.out.println("A conta de número " + numeroDigitado + " não existe.");
        }

        idContaResult.close();
        idContaStatement.close();
    } catch (SQLException e) {
        System.out.println("Ocorreu um erro ao conectar ao banco de dados: " + e.getMessage());
    }
}


//transferencia entre contas


void efetuarTransferencia() {
    Scanner inputContaOrigem = new Scanner(System.in);
    System.out.println("Digite o número da conta de origem: ");
    int numeroContaOrigem = inputContaOrigem.nextInt();

    Scanner inputContaDestino = new Scanner(System.in);
    System.out.println("Digite o número da conta de destino: ");
    int numeroContaDestino = inputContaDestino.nextInt();

    Scanner inputValorTransferencia = new Scanner(System.in);
    System.out.println("Digite o valor da transferência: ");
    double valorTransferencia = inputValorTransferencia.nextDouble();

    try (Connection connection = ConnectionDB.getConexaoMySQL()) {
        // Obter o ID da conta de origem com base no número da conta
        String idContaOrigemQuery = "SELECT ID_CONTA FROM conta_bancaria WHERE NUMERO_CONTA = ?";
        PreparedStatement idContaOrigemStatement = connection.prepareStatement(idContaOrigemQuery);
        idContaOrigemStatement.setInt(1, numeroContaOrigem);

        ResultSet idContaOrigemResult = idContaOrigemStatement.executeQuery();

        if (idContaOrigemResult.next()) {
            int idContaOrigem = idContaOrigemResult.getInt("ID_CONTA");
            System.out.println("ID da conta de origem: " + idContaOrigem);

            // Verificar o saldo da conta de origem
            String saldoOrigemQuery = "SELECT SALDO_CONTA FROM conta_bancaria WHERE ID_CONTA = ?";
            PreparedStatement saldoOrigemStatement = connection.prepareStatement(saldoOrigemQuery);
            saldoOrigemStatement.setInt(1, idContaOrigem);

            ResultSet saldoOrigemResult = saldoOrigemStatement.executeQuery();

            if (saldoOrigemResult.next()) {
                double saldoContaOrigem = saldoOrigemResult.getDouble("SALDO_CONTA");
                //

                if (valorTransferencia <= saldoContaOrigem) {
                    // Obter o ID da conta de destino com base no número da conta
                    String idContaDestinoQuery = "SELECT ID_CONTA FROM conta_bancaria WHERE NUMERO_CONTA = ?";
                    PreparedStatement idContaDestinoStatement = connection.prepareStatement(idContaDestinoQuery);
                    idContaDestinoStatement.setInt(1, numeroContaDestino);

                    ResultSet idContaDestinoResult = idContaDestinoStatement.executeQuery();

                    if (idContaDestinoResult.next()) {
                        int idContaDestino = idContaDestinoResult.getInt("ID_CONTA");
                        System.out.println("ID da conta de destino: " + idContaDestino);

                        // Realizar a transferência
                        // Atualizar o saldo da conta de origem
                        double novoSaldoOrigem = saldoContaOrigem - valorTransferencia;
                        String updateSaldoOrigemQuery = "UPDATE conta_bancaria SET SALDO_CONTA = ? WHERE ID_CONTA = ?";
                        PreparedStatement updateSaldoOrigemStatement = connection.prepareStatement(updateSaldoOrigemQuery);
                        updateSaldoOrigemStatement.setDouble(1, novoSaldoOrigem);
                        updateSaldoOrigemStatement.setInt(2, idContaOrigem);
                        System.out.println("Saldo da conta de origem: R$ " + novoSaldoOrigem);
                        

                        int rowsUpdatedOrigem = updateSaldoOrigemStatement.executeUpdate();
                        if (rowsUpdatedOrigem > 0) {
                            // Atualizar o saldo da conta de destino
                            String saldoDestinoQuery = "SELECT SALDO_CONTA FROM conta_bancaria WHERE ID_CONTA = ?";
                            PreparedStatement saldoDestinoStatement = connection.prepareStatement(saldoDestinoQuery);
                            saldoDestinoStatement.setInt(1, idContaDestino);

                            ResultSet saldoDestinoResult = saldoDestinoStatement.executeQuery();

                            if (saldoDestinoResult.next()) {
                                double saldoContaDestino = saldoDestinoResult.getDouble("SALDO_CONTA");
                                //

                                double novoSaldoDestino = saldoContaDestino + valorTransferencia;
                                String updateSaldoDestinoQuery = "UPDATE conta_bancaria SET SALDO_CONTA = ? WHERE ID_CONTA = ?";
                                PreparedStatement updateSaldoDestinoStatement = connection.prepareStatement(updateSaldoDestinoQuery);
                                updateSaldoDestinoStatement.setDouble(1, novoSaldoDestino);
                                updateSaldoDestinoStatement.setInt(2, idContaDestino);
                                
                                System.out.println("Saldo da conta de destino: R$ " + novoSaldoDestino);

                                int rowsUpdatedDestino = updateSaldoDestinoStatement.executeUpdate();
                                if (rowsUpdatedDestino > 0) {
                                    System.out.println("Transferência concluída com sucesso!");
                                    
                                    String insertTransacaoQuery = "INSERT INTO transacoes_bancaria (id_tipo, data_transacao, valor_transacao, id_conta) VALUES (?, ?, ?, ?)";
                                        PreparedStatement insertTransacaoStatement = connection.prepareStatement(insertTransacaoQuery);
                                        insertTransacaoStatement.setInt(1, 4); // id_tipo 4 - transferencia
                                        insertTransacaoStatement.setDate(2, new Date(System.currentTimeMillis()));
                                        insertTransacaoStatement.setDouble(3, valorTransferencia);
                                        insertTransacaoStatement.setInt(4, idContaOrigem);

                                        int rowsInserted = insertTransacaoStatement.executeUpdate();
                                        if (rowsInserted > 0) {
                                            System.out.println("Dados da transferência inseridos com sucesso na tabela 'transacao'.");
                                        } else {
                                            System.out.println("Erro ao inserir os dados da transferência na tabela 'transacao'.");
                                        }
                                    
                                } else {
                                    System.out.println("Erro ao atualizar o saldo da conta de destino.");
                                }
                            } else {
                                System.out.println("Conta de destino não encontrada.");
                            }
                        } else {
                            System.out.println("Saldo insuficiente na conta de origem.");
                        }
                    } else {
                        System.out.println("Conta de destino não encontrada.");
                    }
                } else {
                    System.out.println("Valor da transferência excede o saldo da conta de origem.");
                }
            } else {
                System.out.println("Conta de origem não encontrada.");
            }
        } else {
            System.out.println("Conta de origem não encontrada.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


// saque bancario


void efetuarSaque(int numeroDigitado) {
    Scanner inputValorSaque = new Scanner(System.in);
    System.out.println("Digite o valor do saque: ");
    double valorSaque = inputValorSaque.nextDouble();

    try (Connection connection = ConnectionDB.getConexaoMySQL()) {
        // Obter o ID da conta com base no número da conta
        String idContaQuery = "SELECT ID_CONTA FROM conta_bancaria WHERE NUMERO_CONTA = ?";
        PreparedStatement idContaStatement = connection.prepareStatement(idContaQuery);
        idContaStatement.setInt(1, numeroDigitado);

        ResultSet idContaResult = idContaStatement.executeQuery();

        if (idContaResult.next()) {
            int idConta = idContaResult.getInt("ID_CONTA");
            System.out.println("ID da conta: " + idConta);

            // Verificar o saldo da conta
            String saldoQuery = "SELECT SALDO_CONTA FROM conta_bancaria WHERE ID_CONTA = ?";
            PreparedStatement saldoStatement = connection.prepareStatement(saldoQuery);
            saldoStatement.setInt(1, idConta);

            ResultSet saldoResult = saldoStatement.executeQuery();

            if (saldoResult.next()) {
                double saldoConta = saldoResult.getDouble("SALDO_CONTA");
                System.out.println("Saldo da conta: R$ " + saldoConta);

                if (valorSaque <= saldoConta) {
                    // Atualizar o saldo na tabela conta_bancaria
                    double novoSaldo = saldoConta - valorSaque;
                    String updateSaldoQuery = "UPDATE conta_bancaria SET SALDO_CONTA = ? WHERE ID_CONTA = ?";
                    PreparedStatement updateSaldoStatement = connection.prepareStatement(updateSaldoQuery);
                    updateSaldoStatement.setDouble(1, novoSaldo);
                    updateSaldoStatement.setInt(2, idConta);

                    int rowsUpdated = updateSaldoStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Saque realizado com sucesso.");

                        // Inserir a transação no banco de dados
                        String insertTransacaoQuery = "INSERT INTO transacoes_bancaria (ID_CONTA, DATA_TRANSACAO, VALOR_TRANSACAO, ID_TIPO) VALUES (?, ?, ?, ?)";
                        PreparedStatement insertTransacaoStatement = connection.prepareStatement(insertTransacaoQuery);
                        insertTransacaoStatement.setInt(1, idConta);
                        insertTransacaoStatement.setDate(2, new Date(System.currentTimeMillis()));
                        insertTransacaoStatement.setDouble(3, valorSaque);
                        insertTransacaoStatement.setInt(4, 3); // ID_TIPO 3 representa o tipo "saque"

                        int rowsInserted = insertTransacaoStatement.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("Transação de saque registrada com sucesso.");
                        } else {
                            System.out.println("Falha ao registrar a transação de saque.");
                        }

                        insertTransacaoStatement.close();
                    } else {
                        System.out.println("Falha ao realizar o saque.");
                    }

                    updateSaldoStatement.close();
                } else {
                    System.out.println("Saldo insuficiente para realizar o saque.");
                }
            } else {
                System.out.println("Não foi possível obter o saldo da conta.");
            }

            saldoResult.close();
            saldoStatement.close();
        } else {
            System.out.println("A conta de número " + numeroDigitado + " não existe.");
        }

        idContaResult.close();
        idContaStatement.close();
            } catch (SQLException e) {
            System.out.println("Ocorreu um erro ao conectar ao banco de dados: " + e.getMessage());
        }
}

    
// retirar extrato da conta

void consultarExtrato(int numeroDigitado) {
    try (Connection connection = ConnectionDB.getConexaoMySQL()) {
        // Obter o ID da conta com base no número da conta
        String idContaQuery = "SELECT ID_CONTA FROM conta_bancaria WHERE NUMERO_CONTA = ?";
        PreparedStatement idContaStatement = connection.prepareStatement(idContaQuery);
        idContaStatement.setInt(1, numeroDigitado);

        ResultSet idContaResult = idContaStatement.executeQuery();

        if (idContaResult.next()) {
            int idConta = idContaResult.getInt("ID_CONTA");
            System.out.println("ID da conta: " + idConta);

            // Consultar as transações bancárias da conta
            String extratoQuery = "SELECT * FROM transacoes_bancaria WHERE ID_CONTA = ? ORDER BY DATA_TRANSACAO";
            PreparedStatement extratoStatement = connection.prepareStatement(extratoQuery);
            extratoStatement.setInt(1, idConta);

            ResultSet extratoResult = extratoStatement.executeQuery();

            System.out.println("Extrato bancário da conta " + numeroDigitado + ":");

            while (extratoResult.next()) {
                int idTransacao = extratoResult.getInt("ID_TRANSACAO");
                Date dataTransacao = extratoResult.getDate("DATA_TRANSACAO");
                double valorTransacao = extratoResult.getDouble("VALOR_TRANSACAO");
                int idTipo = extratoResult.getInt("ID_TIPO");

                String tipoTransacao = "";
                if (idTipo == 1) {
                    tipoTransacao = "Pagamento";
                } else if (idTipo == 2) {
                    tipoTransacao = "Depósito";
                } else if (idTipo == 3) {
                    tipoTransacao = "Saque";
                } else if (idTipo == 4) {
                    tipoTransacao = "Transferência";
                }

                System.out.println("ID da transação: " + idTransacao + " Data da transação: " + dataTransacao + " Valor da transação: R$ " + valorTransacao + " Tipo da transação: " + tipoTransacao);

            }

            extratoResult.close();
            extratoStatement.close();
        } else {
            System.out.println("A conta de número " + numeroDigitado + " não existe.");
        }

        idContaResult.close();
        idContaStatement.close();
    } catch (SQLException e) {
        System.out.println("Ocorreu um erro ao conectar ao banco de dados: " + e.getMessage());
    }
}

//encerrar conta

void encerrarConta() {
    Scanner inputNumeroConta = new Scanner(System.in);
    System.out.println("Digite o número da conta que deseja encerrar: ");
    int numeroConta = inputNumeroConta.nextInt();

    try (Connection connection = ConnectionDB.getConexaoMySQL()) {
        // Obter o ID da conta com base no número da conta
        String idContaQuery = "SELECT ID_CONTA FROM conta_bancaria WHERE NUMERO_CONTA = ?";
        PreparedStatement idContaStatement = connection.prepareStatement(idContaQuery);
        idContaStatement.setInt(1, numeroConta);

        ResultSet idContaResult = idContaStatement.executeQuery();

        if (idContaResult.next()) {
            int idConta = idContaResult.getInt("ID_CONTA");
            System.out.println("ID da conta: " + idConta);

            // Verificar o saldo da conta
            String saldoQuery = "SELECT SALDO_CONTA FROM conta_bancaria WHERE ID_CONTA = ?";
            PreparedStatement saldoStatement = connection.prepareStatement(saldoQuery);
            saldoStatement.setInt(1, idConta);

            ResultSet saldoResult = saldoStatement.executeQuery();

            if (saldoResult.next()) {
                double saldoConta = saldoResult.getDouble("SALDO_CONTA");
                System.out.println("Saldo da conta: R$ " + saldoConta);

                if (saldoConta == 0) {
                    // Atualizar o status da conta para desativada
                    String updateStatusQuery = "UPDATE conta_bancaria SET STATUS_CONTA = 0 WHERE ID_CONTA = ?";
                    PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery);
                    updateStatusStatement.setInt(1, idConta);

                    int rowsUpdated = updateStatusStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Conta encerrada com sucesso.");
                    } else {
                        System.out.println("Falha ao encerrar a conta.");
                    }

                    updateStatusStatement.close();
                } else {
                    System.out.println("A conta não pode ser encerrada pois possui saldo.");
                }

                saldoStatement.close();
            } else {
                System.out.println("Não foi possível obter o saldo da conta.");
            }

            saldoResult.close();
        } else {
            System.out.println("A conta de número " + numeroConta + " não existe.");
        }

        idContaResult.close();
        idContaStatement.close();
    } catch (SQLException e) {
        System.out.println("Ocorreu um erro ao conectar ao banco de dados: " + e.getMessage());
    }
}










}