//funcões:
function boolean isCartaoValido(String cartao) {
	//aplicar regras do cartão
	//Se ok
	return true;
    // Senao
    // return false;
}


public boolean isCNSValido(String s) {
	if (s.matches("[1-2]\\d{10}00[0-1]\\d") || s.matches("[7-9]\\d{14}")) {
		return somaPonderada(s) % 11 == 0;
	}
	return false;
}

private int somaPonderada(String s) {
	char[] cs = s.toCharArray();
	int soma = 0;
	for (int i = 0; i < cs.length; i++) {
		soma += Character.digit(cs[i], 10) * (15 - i);
	}
	return soma;
}

public boolenam isCNESValido(String S){
    return true;
    //Por enquanto não tem nada para avalidar aqui, achar a regra disso depois
}

public boolenam isCodigoPrestadorValido(String S){
    return true;
    //Por enquanto não tem nada para avalidar aqui, achar a regra disso depois
}


rule "1001_NumeroDaCarteiraInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
        S:solicitacao()
        not isCartaoValido(S.cartao)
	then
		S.setAutorizado(false);
		negacao N = new negacao();
		N.setCodigo(1001);
		N.setNegacao("Número da carteira inválido");
		insert( N );
		System.out.println("1001 Número da carteira inválido");
end

rule "1002_NumeroDoCartaoNacionalDeSaudeInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
        S:solicitacao()
        not isCNSValido(S.CNS)
	then
		S.setAutorizado(false);
		negacao N = new negacao();
		N.setCodigo(1002);
		N.setNegacao("Número do cartão nacional de saúde inválido");
		insert( N );
		System.out.println("1002 Número do cartão nacional de saúde inválido");
end


rule "1003_AAdmissaoDoBeneficiarioNoPrestadorOcorreuAntesDaInclusaoDoBeneficiarioNaOperadora"
    //Nao executar esta regra agora
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
        S:solicitacao(true,vDataAdmissaoBeneficiarioNoPrestador:dataadmissaobeneficiarionoprestador)
        S:beneficiario(datainclusao < vDataAdmissaoBeneficiarioNoPrestador)
	then
		negacao N = new negacao();
		N.setCodigo(1003);
		N.setNegacao("A admissão do beneficiário no prestador ocorreu antes da inclusão do beneficiário na operadora");
		insert( N );
		System.out.println("1003 A admissão do beneficiário no prestador ocorreu antes da inclusão do beneficiário na operadora");
end


rule "1004_SolicitacaoAnteriorAInclusaoDoBeneficiario"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
	    B:beneficiario(true,vDataInclusao:datainclusao)
	    S:solicitacao(autorizado==true, datasolicitacao<vDataInclusao)
	then
		S.setAutorizado(false);
		negacao N = new negacao();
		N.setCodigo(1004);
		N.setNegacao("Solicitação anterior à inclusão do beneficiário");
		insert( N );
		System.out.println("1004 Solicitação anterior à inclusão do beneficiário");
end

rule "1005_AtendimentoAnteriorAInclusaoDoBeneficiario"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
	    B:beneficiario(true,vDataInclusao:datainclusao)
	    S:solicitacao(autorizado==true, dataatendimento<vDataInclusao)
	then
		S.setAutorizado(false);
		negacao N = new negacao();
		N.setCodigo(1005);
		N.setNegacao("Atendimento anterior à inclusão do beneficiário");
		insert( N );
		System.out.println("1005 Atendimento anterior à inclusão do beneficiário");
end

rule "1006_AtendimentoAposODesligamentoDoBeneficiario"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
	    B:beneficiario(true,vDataCancelamento:datacancelamento)
	    S:solicitacao(autorizado==true, dataatendimento > vDataCancelamento)
	then
		negacao N = new negacao();
		N.setCodigo(1006);
		N.setNegacao("Atendimento após o desligamento do beneficiário");
		insert( N );
		System.out.println("1006 Atendimento após o desligamento do beneficiário");
end

rule "1007_AtendimentoDentroDaCarenciaDoBeneficiario"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
	    S:solicitacao()
	    B:beneficiario(true,vDataInclusao:datainclusao)
	    solicitacao(vDataAtendimento < vDataInclusao)
	then
		S.setAutorizado(false);
		negacao N = new negacao();
		N.setCodigo(1007);
		N.setNegacao("Atendimento dentro da carência do beneficiário");
		insert( N );
		System.out.println("1007 Atendimento dentro da carência do beneficiário");
end

rule "1008_AssinaturaDivergente"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
	    S:solicitacao(autorizado==true,assintaturadivergente==true)
	then
		S.setAutorizado(false);
		negacao N = new negacao();
		N.setCodigo(1008);
		N.setNegacao("Assinatura divergente");
		insert( N );
		System.out.println("1008 Assinatura divergente");
end

rule "1009_BeneficiarioComPagamentoEmAberto"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
	    B:beneficiario(inadimplente==true)
	then
		S.setAutorizado(false);
		negacao N = new negacao();
		N.setCodigo(1009);
		N.setNegacao("Beneficiário com pagamento em aberto");
		insert( N );
		System.out.println("1009 Beneficiário com pagamento em aberto");
end

rule "1010_AssinaturaDoTitularResponsavelInexistente"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
	    S:solicitacao(assinaturatitularinexistente==true)
	then
		S.setAutorizado(false);
		negacao N = new negacao();
		N.setCodigo(1010);
		N.setNegacao("Assinatura do titular / responsável inexistente");
		insert( N );
		System.out.println("1010 Assinatura do titular / responsável inexistente");
end

rule "1011_IdentificacaoDoBeneficiarioNaoConsistente"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
	    S:solicitacao(identificacaodobeneficiarionaoconsistente==true)
	then
		S.setAutorizado(false);
		negacao N = new negacao();
		N.setCodigo(1011);
		N.setNegacao("Identificação do beneficiário não consistente");
		insert( N );
		System.out.println("1011 Identificação do beneficiário não consistente");
end


//*******************tenho que pensar em como resolver listas **************************
rule "1012_ServicoProfissionalHospitalarNaoECobertoPeloPlanoDoBeneficiario"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
    then
		negacao N = new negacao();
		N.setCodigo(1012);
		N.setNegacao("Serviço profissional hospitalar não é coberto pelo plano do beneficiário");
		insert( N );
		System.out.println("1012 Serviço profissional hospitalar não é coberto pelo plano do beneficiário");
end

rule "1013_CadastroDoBeneficiarioComProblemas"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()
		B:beneficiario(cadastrocomproblemas==true)
	then
		negacao N = new negacao();
		N.setCodigo(1013);
		N.setNegacao("Cadastro do beneficiário com problemas");
		insert( N );
		System.out.println("1013 Cadastro do beneficiário com problemas");
end

rule "1014_BeneficiarioComDataDeExclusao"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao(vDataAtendimento)
		B:beneficiario(vDataAtendimento<datacancelamento)
	then
		negacao N = new negacao();
		N.setCodigo(1014);
		N.setNegacao("Beneficiário com data de exclusão");
		insert( N );
		System.out.println("1014 Beneficiário com data de exclusão");
end

rule "1015_IdadeDoBeneficiarioAcimaIdadeLimite"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()
		E:evento(vIdateMinima:idademinima,vIdadeMaxima:idademaxima)
		not solicitacao(idade > vIdadeMinima && < vIdadeMaxima)
	then
		negacao N = new negacao();
		N.setCodigo(1015);
		N.setNegacao("Idade do beneficiário acima idade limite");
		insert( N );
		System.out.println("1015 Idade do beneficiário acima idade limite");
end

rule "1016_BeneficiarioComAtendimentoSuspenso"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
		B:beneficiario(atendimentosuspenso==true)
	then
		negacao N = new negacao();
		N.setCodigo(1016);
		N.setNegacao("Beneficiário com atendimento suspenso");
		insert( N );
		System.out.println("1016 Beneficiário com atendimento suspenso");
end

rule "1017_DataValidadeDaCarteiraVencida"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao(vDataAtendimento:dataatendimento)
		B:beneficiario(vDataAtendimento>datavencimentocarteira)
	then
		negacao N = new negacao();
		N.setCodigo(1017);
		N.setNegacao("Data validade da carteira vencida");
		insert( N );
		System.out.println("1017 Data validade da carteira vencida");
end

rule "1018_EmpresaDoBeneficiarioSuspensaExcluida"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()
		C:contrato(suspensa==true)
	then
		negacao N = new negacao();
		N.setCodigo(1018);
		N.setNegacao("Empresa do beneficiário suspensa / excluída");
		insert( N );
		System.out.println("1018 Empresa do beneficiário suspensa / excluída");
end

rule "1019_FamiliaDoBeneficiarioComAtendimentoSuspenso"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()
		B:beneficiario(familiasuspensa==true)
	then
		negacao N = new negacao();
		N.setCodigo(1019);
		N.setNegacao("Família do beneficiário com atendimento suspenso");
		insert( N );
		System.out.println("1019 Família do beneficiário com atendimento suspenso");
end

rule "1020_ViaDeCartaoDoBeneficiarioCancelada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()
		B:beneficiario(situacaoviacartao=="C")
	then
		negacao N = new negacao();
		N.setCodigo(1020);
		N.setNegacao("Via de cartão do beneficiário cancelada");
		insert( N );
		System.out.println("1020 Via de cartão do beneficiário cancelada");
end

rule "1021_ViaDeCartaoDoBeneficiarioNaoLiberada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
		B:beneficiario(situacaoviacartao=="B")
	then
		negacao N = new negacao();
		N.setCodigo(1021);
		N.setNegacao("Via de cartão do beneficiário não liberada");
		insert( N );
		System.out.println("1021 Via de cartão do beneficiário não liberada");
end

rule "1022_ViaDeCartaoDoBeneficiarioNaoCompativel"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
		B:beneficiario(situacaoviacartao=="I")
	then
		negacao N = new negacao();
		N.setCodigo(1022);
		N.setNegacao("Via de cartão do beneficiário não compatível");
		insert( N );
		System.out.println("1022 Via de cartão do beneficiário não compatível");
end

rule "1023_NomeDoTitularInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
        S:solicitacao(nometitularinvalido==true)
	then
		negacao N = new negacao();
		N.setCodigo(1023);
		N.setNegacao("Nome do titular inválido");
		insert( N );
		System.out.println("1023 Nome do titular inválido");
end


//***************************** TEM QUE VER COMO RESOLVER LISTAS *****************************
rule "1024_PlanoNaoExistente"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao(plano no in planos())	
	then
		negacao N = new negacao();
		N.setCodigo(1024);
		N.setNegacao("Plano não existente");
		insert( N );
		System.out.println("1024 Plano não existente");
end

// A idéia aqui é ter um lista 0:Odonoto, A:Ambulatorial, H:Hospitalar, F:Obstétrico, então o beneficiario teria um conjunto como ['O','A','H','F']
// Se for 'O' (odontológico) e não estiver na lista do beneficiario negar com esta negacao
rule "1025_BeneficiarioNaoPossuiCoberturaParaAssistenciaOdontologica"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()
		E:evento(tipo=="O" && tipo not in vListaDeTiposDeCoberturaDoBeneficiario) 
	then
		negacao N = new negacao();
		N.setCodigo(1025);
		N.setNegacao("Beneficiário não possui cobertura para assistência odontológica");
		insert( N );
		System.out.println("1025 Beneficiário não possui cobertura para assistência odontológica");
end

//Assumindo que neste caso é o executor que não está credenciado
rule "1201_AtendimentoForaDaVigenciaDoContratoComOCredenciado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao(vDataAtendimento:dataatendimento)	
		Ex: executor(datadescredenciamento<vDataAtendimento)
	then
		negacao N = new negacao();
		N.setCodigo(1201);
		N.setNegacao("Atendimento fora da vigência do contrato com o credenciado");
		insert( N );
		System.out.println("1201 Atendimento fora da vigência do contrato com o credenciado");
end

rule "1202_NumeroDoCnesInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
		Ex:executor(not isCNESValido(CNES))
	then
		negacao N = new negacao();
		N.setCodigo(1202);
		N.setNegacao("Número do cnes inválido");
		insert( N );
		System.out.println("1202 Número do cnes inválido");
end

rule "1203_CodigoPrestadorInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
		Ex:executor(not isCodigoPrestadorValido(id))
	then
		negacao N = new negacao();
		N.setCodigo(1203);
		N.setNegacao("Código prestador inválido");
		insert( N );
		System.out.println("1203 Código prestador inválido");
end

rule "1204_AdmissaoAnteriorAInclusaoDoCredenciadoNaRede"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		Ex:executor(vDataCredenciamento:datacredenciamento)
		S:solicitacao(dataatendimento > vDataCredenciamento)	
	then
		negacao N = new negacao();
		N.setCodigo(1204);
		N.setNegacao("Admissão anterior à inclusão do credenciado na rede");
		insert( N );
		System.out.println("1204 Admissão anterior à inclusão do credenciado na rede");
end


//***************************************** TEM QUE VER COMO RESOLVER ESTA - PRECISA VER A REDE DO BENEFICIARIO E A REDE DO PRESTADOR EXECUTOR
rule "1205_AdmissaoAposODesligamentoDoCredenciadoDaRede"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao(vDataAtendimento:datatendimento,vRede:rede)
		Ex:executor(vRede in Redes and Redes.dataCancelamento > vDataAtendimento)
	then
		negacao N = new negacao();
		N.setCodigo(1205);
		N.setNegacao("Admissão após o desligamento do credenciado da rede");
		insert( N );
		System.out.println("1205 Admissão após o desligamento do credenciado da rede");
end

//Biblioteca para validar o CPJ CNPJ - Importar a classe
rule "1206_CpfCnpjInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		E:solicitacao(not isCPFCNPJValido(cpfcnpj))	
	then
		negacao N = new negacao();
		N.setCodigo(1206);
		N.setNegacao("Cpf / cnpj inválido");
		insert( N );
		System.out.println("1206 Cpf / cnpj inválido");
end

rule "1207_CredenciadoNaoPertenceARedeCredenciada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()
		B:beneficiario(vRede:rede)
		Ex:executor(vRede not in redes)
	then
		negacao N = new negacao();
		N.setCodigo(1207);
		N.setNegacao("Credenciado não pertence à rede credenciada");
		insert( N );
		System.out.println("1207 Credenciado não pertence à rede credenciada");
end

rule "1208_SolicitacaoAnteriorAInclusaoDoCredenciado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao(vDataSolicitacao:datasolicitacao)
		Ex: executor(vDataSolicitacao < datacredenciamento)
	then
		negacao N = new negacao();
		N.setCodigo(1208);
		N.setNegacao("Solicitação anterior à inclusão do credenciado");
		insert( N );
		System.out.println("1208 Solicitação anterior à inclusão do credenciado");
end

rule "1209_SolicitacaoAposODesligamentoDoCredenciado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao(vDataSolicitacao:datasolicitacao)
		Ex: executor(vDataSolicitacao > datadescredenciamento)
	then
		negacao N = new negacao();
		N.setCodigo(1209);
		N.setNegacao("Solicitação após o desligamento do credenciado");
		insert( N );
		System.out.println("1209 Solicitação após o desligamento do credenciado");
end

/*
// ************************* OU FAZEMOS UM SERVIÇO DE VALIDACAO DE CADASTRO AQUI OU FAZEMOS UM PROCESSO DE VALIDACÃO DE CADASTRO ANTES!!
rule "1210_SolicitanteCredenciadoNaoCadastrado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao(false)	
	then
		negacao N = new negacao();
		N.setCodigo(1210);
		N.setNegacao("Solicitante credenciado não cadastrado");
		insert( N );
		System.out.println("1210 Solicitante credenciado não cadastrado");
end
*/

/*
rule "1211_AssinaturaCarimboDoCredenciadoInexistente"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1211);
		N.setNegacao("Assinatura / carimbo do credenciado inexistente");
		insert( N );
		System.out.println("1211 Assinatura / carimbo do credenciado inexistente");
end
*/

rule "1212_AtendimentoReferenciaForaDaVigenciaDoContratoDoPrestador"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao(vDataAtendimento:dataatendimento)	
		Ex:executor(vDataAtendimento < datacredenciamento && > datadescredenciamento)
	then
		negacao N = new negacao();
		N.setCodigo(1212);
		N.setNegacao("Atendimento / referência fora da vigência do contrato do prestador");
		insert( N );
		System.out.println("1212 Atendimento / referência fora da vigência do contrato do prestador");
end

rule "1213_CboInvalido"
    /*
    http://sistemas.unasus.gov.br/ws_cbo/cbo.php?cbo=322230
    chamar este endpont passand o codigo o cbo, se estiver ok, será retornado a descricao xml, caso contrario virá vazio
	*/
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		Ex:executor(not isCBOSValido(cbos))
	then
		negacao N = new negacao();
		N.setCodigo(1213);
		N.setNegacao("Cbo (especialidade) inválido");
		insert( N );
		System.out.println("1213 Cbo (especialidade) inválido");
end

/*
Tem que resolver a listagem dos eventos do prestador para otimizar isso.
*/
rule "1214_CredenciadoNaoHabilitadoARealizarOProcedimento"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao(vEvento:evento)
		Ex:executor(not vEvento in eventosdoprestador)
	then
		negacao N = new negacao();
		N.setCodigo(1214);
		N.setNegacao("Credenciado não habilitado a realizar o procedimento");
		insert( N );
		System.out.println("1214 Credenciado não habilitado a realizar o procedimento");
end

/*
Listas abrangencia do beneficiario e do executor
*/

rule "1215_CredenciadoForaDaAbrangenciaGeograficaDoPlano"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
		B:beneficiario(vAbrangencia:abrangencia)
		Ex:executor(vAbrangencia not in abrangencia)
	then
		negacao N = new negacao();
		N.setCodigo(1215);
		N.setNegacao("Credenciado fora da abrangência geográfica do plano");
		insert( N );
		System.out.println("1215 Credenciado fora da abrangência geográfica do plano");
end

/*
Tem que ver como resolver as especialidades - webApi?
*/
rule "1216_EspecialidadeNaoCadastrada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao(especialidade not in especialidades)	
	then
		negacao N = new negacao();
		N.setCodigo(1216);
		N.setNegacao("Especialidade não cadastrada");
		insert( N );
		System.out.println("1216 Especialidade não cadastrada");
end

rule "1217_EspecialidadeNaoCadastradaParaOPrestador"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1217);
		N.setNegacao("Especialidade não cadastrada para o prestador");
		insert( N );
		System.out.println("1217 Especialidade não cadastrada para o prestador");
end

rule "1218_CodigoDePrestadorIncompativelComProcedimentoExameCobrado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1218);
		N.setNegacao("Código de prestador incompativel com procedimento / exame cobrado");
		insert( N );
		System.out.println("1218 Código de prestador incompativel com procedimento / exame cobrado");
end

rule "1301_TipoGuiaInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1301);
		N.setNegacao("Tipo guia inválido");
		insert( N );
		System.out.println("1301 Tipo guia inválido");
end

rule "1302_CodigoTipoGuiaPrincipalENumeroGuiasIncompativeis"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1302);
		N.setNegacao("Código tipo guia principal e número guias incompatíveis");
		insert( N );
		System.out.println("1302 Código tipo guia principal e número guias incompatíveis");
end

rule "1303_NaoExisteONumeroGuiaPrincipalInformado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1303);
		N.setNegacao("Não existe o número guia principal informado");
		insert( N );
		System.out.println("1303 Não existe o número guia principal informado");
end

rule "1304_CobrancaEmGuiaIndevida"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1304);
		N.setNegacao("Cobrança em guia indevida");
		insert( N );
		System.out.println("1304 Cobrança em guia indevida");
end

rule "1305_ItemPagoEmOutraGuia"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1305);
		N.setNegacao("Item pago em outra guia");
		insert( N );
		System.out.println("1305 Item pago em outra guia");
end

rule "1306_NaoExisteNumeroGuiaPrincipalEOuCodigoGuiaPrincipal"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1306);
		N.setNegacao("Não existe número guia principal e/ou código guia principal");
		insert( N );
		System.out.println("1306 Não existe número guia principal e/ou código guia principal");
end

rule "1307_NumeroDaGuiaInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1307);
		N.setNegacao("Número da guia inválido");
		insert( N );
		System.out.println("1307 Número da guia inválido");
end

rule "1308_GuiaJaApresentada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1308);
		N.setNegacao("Guia já apresentada");
		insert( N );
		System.out.println("1308 Guia já apresentada");
end

rule "1309_ProcedimentoContratadoNaoEstaDeAcordoComOTipoDeGuiaUtilizado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1309);
		N.setNegacao("Procedimento contratado não está de acordo com o tipo de guia utilizado");
		insert( N );
		System.out.println("1309 Procedimento contratado não está de acordo com o tipo de guia utilizado");
end

rule "1310_ServicoDoTipoCirurgicoEInvasivo.EquipeMedicaNaoInformadaNaGuia"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1310);
		N.setNegacao("Serviço do tipo cirúrgico e invasivo. equipe médica não informada na guia");
		insert( N );
		System.out.println("1310 Serviço do tipo cirúrgico e invasivo. equipe médica não informada na guia");
end

rule "1311_PrestadorExecutanteNaoInformado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1311);
		N.setNegacao("Prestador executante não informado");
		insert( N );
		System.out.println("1311 Prestador executante não informado");
end

rule "1312_PrestadorContratadoNaoInformado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1312);
		N.setNegacao("Prestador contratado não informado");
		insert( N );
		System.out.println("1312 Prestador contratado não informado");
end

rule "1313_GuiaComRasura"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1313);
		N.setNegacao("Guia com rasura");
		insert( N );
		System.out.println("1313 Guia com rasura");
end

rule "1314_GuiaSemAssinaturaEOuCarimboDoCredenciado."
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1314);
		N.setNegacao("Guia sem assinatura e/ou carimbo do credenciado.");
		insert( N );
		System.out.println("1314 Guia sem assinatura e/ou carimbo do credenciado.");
end

rule "1315_GuiaSemDataDoAtoCirurgico."
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1315);
		N.setNegacao("Guia sem data do ato cirúrgico.");
		insert( N );
		System.out.println("1315 Guia sem data do ato cirúrgico.");
end

rule "1316_GuiaComLocalDeAtendimentoPreenchidoIncorretamente."
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1316);
		N.setNegacao("Guia com local de atendimento preenchido incorretamente.");
		insert( N );
		System.out.println("1316 Guia com local de atendimento preenchido incorretamente.");
end

rule "1317_GuiaSemDataDoAtendimento"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1317);
		N.setNegacao("Guia sem data do atendimento");
		insert( N );
		System.out.println("1317 Guia sem data do atendimento");
end

rule "1318_GuiaComCodigoDeServicoPreenchidoIncorretamente."
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1318);
		N.setNegacao("Guia com código de serviço preenchido incorretamente.");
		insert( N );
		System.out.println("1318 Guia com código de serviço preenchido incorretamente.");
end

rule "1319_GuiaSemAssinaturaDoAssistido."
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1319);
		N.setNegacao("Guia sem assinatura do assistido.");
		insert( N );
		System.out.println("1319 Guia sem assinatura do assistido.");
end

rule "1320_IdentificacaoDoAssistidoIncompleta"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1320);
		N.setNegacao("Identificação do assistido incompleta");
		insert( N );
		System.out.println("1320 Identificação do assistido incompleta");
end

rule "1321_ValidadeDaGuiaExpirada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1321);
		N.setNegacao("Validade da guia expirada");
		insert( N );
		System.out.println("1321 Validade da guia expirada");
end

rule "1322_ComprovantePresencialOuGtoNaoEnviado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1322);
		N.setNegacao("Comprovante presencial ou gto não enviado");
		insert( N );
		System.out.println("1322 Comprovante presencial ou gto não enviado");
end

rule "1323_DataPreenchidaIncorretamente"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1323);
		N.setNegacao("Data preenchida incorretamente");
		insert( N );
		System.out.println("1323 Data preenchida incorretamente");
end

rule "1401_AcomodacaoNaoAutorizada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1401);
		N.setNegacao("Acomodação não autorizada");
		insert( N );
		System.out.println("1401 Acomodação não autorizada");
end

rule "1402_ProcedimentoNaoAutorizado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1402);
		N.setNegacao("Procedimento não autorizado");
		insert( N );
		System.out.println("1402 Procedimento não autorizado");
end

rule "1403_NaoExisteInformacaoSobreASenhaDeAutorizacaoDoProcedimento"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1403);
		N.setNegacao("Não existe informação sobre a senha de autorização do procedimento");
		insert( N );
		System.out.println("1403 Não existe informação sobre a senha de autorização do procedimento");
end

rule "1404_NaoExisteGuiaDeAutorizacaoRelacionada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1404);
		N.setNegacao("Não existe guia de autorização relacionada");
		insert( N );
		System.out.println("1404 Não existe guia de autorização relacionada");
end

rule "1405_DataDeValidadeDaSenhaEAnteriorADataDoAtendimento"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1405);
		N.setNegacao("Data de validade da senha é anterior a data do atendimento");
		insert( N );
		System.out.println("1405 Data de validade da senha é anterior a data do atendimento");
end

rule "1406_NumeroDaSenhaInformadoDiferenteDoLiberado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1406);
		N.setNegacao("Número da senha informado diferente do liberado");
		insert( N );
		System.out.println("1406 Número da senha informado diferente do liberado");
end

rule "1407_ServicoSolicitadoNaoPossuiCobertura"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1407);
		N.setNegacao("Serviço solicitado não possui cobertura");
		insert( N );
		System.out.println("1407 Serviço solicitado não possui cobertura");
end

rule "1408_QuantidadeServicoSolicitadaAcimaDaAutorizada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1408);
		N.setNegacao("Quantidade serviço solicitada acima da autorizada");
		insert( N );
		System.out.println("1408 Quantidade serviço solicitada acima da autorizada");
end

rule "1409_QuantidadeServicoSolicitadaAcimaCoberta"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1409);
		N.setNegacao("Quantidade serviço solicitada acima coberta");
		insert( N );
		System.out.println("1409 Quantidade serviço solicitada acima coberta");
end

rule "1410_ServicoSolicitadoEmCarencia"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1410);
		N.setNegacao("Serviço solicitado em carência");
		insert( N );
		System.out.println("1410 Serviço solicitado em carência");
end

rule "1411_SolicitanteNaoInformado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1411);
		N.setNegacao("Solicitante não informado");
		insert( N );
		System.out.println("1411 Solicitante não informado");
end

rule "1412_ProblemasNoSistemaAutorizador"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1412);
		N.setNegacao("Problemas no sistema autorizador");
		insert( N );
		System.out.println("1412 Problemas no sistema autorizador");
end

rule "1413_AcomodacaoNaoPossuiCobertura"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1413);
		N.setNegacao("Acomodação não possui cobertura");
		insert( N );
		System.out.println("1413 Acomodação não possui cobertura");
end

rule "1414_DataDeValidadeDaSenhaExpirada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1414);
		N.setNegacao("Data de validade da senha expirada");
		insert( N );
		System.out.println("1414 Data de validade da senha expirada");
end

rule "1415_ProcedimentoNaoAutorizadoParaOBeneficiario"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1415);
		N.setNegacao("Procedimento não autorizado para o beneficiário");
		insert( N );
		System.out.println("1415 Procedimento não autorizado para o beneficiário");
end

rule "1416_SolicitanteNaoCadastrado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1416);
		N.setNegacao("Solicitante não cadastrado");
		insert( N );
		System.out.println("1416 Solicitante não cadastrado");
end

rule "1417_SolicitanteNaoHabilitado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1417);
		N.setNegacao("Solicitante não habilitado");
		insert( N );
		System.out.println("1417 Solicitante não habilitado");
end

rule "1418_SolicitanteSuspenso"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1418);
		N.setNegacao("Solicitante suspenso");
		insert( N );
		System.out.println("1418 Solicitante suspenso");
end

rule "1419_ServicoSolicitadoJaAutorizado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1419);
		N.setNegacao("Serviço solicitado já autorizado");
		insert( N );
		System.out.println("1419 Serviço solicitado já autorizado");
end

rule "1420_ServicoSolicitadoForaDaCobertura"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1420);
		N.setNegacao("Serviço solicitado fora da cobertura");
		insert( N );
		System.out.println("1420 Serviço solicitado fora da cobertura");
end

rule "1421_ServicoSolicitadoEDePre-Existencia"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1421);
		N.setNegacao("Serviço solicitado é de pré-existência");
		insert( N );
		System.out.println("1421 Serviço solicitado é de pré-existência");
end

rule "1422_EspecialidadeNaoCadastradaParaOSolicitante"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1422);
		N.setNegacao("Especialidade não cadastrada para o solicitante");
		insert( N );
		System.out.println("1422 Especialidade não cadastrada para o solicitante");
end

rule "1423_QuantidadeSolicitadaAcimaDaQuantidadePermitida"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1423);
		N.setNegacao("Quantidade solicitada acima da quantidade permitida");
		insert( N );
		System.out.println("1423 Quantidade solicitada acima da quantidade permitida");
end

rule "1424_QuantidadeAutorizadaAcimaDaQuantidadePermitida"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1424);
		N.setNegacao("Quantidade autorizada acima da quantidade permitida");
		insert( N );
		System.out.println("1424 Quantidade autorizada acima da quantidade permitida");
end

rule "1425_NecessitaPre-AutorizacaoDaEmpresa"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1425);
		N.setNegacao("Necessita pré-autorização da empresa");
		insert( N );
		System.out.println("1425 Necessita pré-autorização da empresa");
end

rule "1426_NaoAutorizadoPelaAuditoria"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1426);
		N.setNegacao("Não autorizado pela auditoria");
		insert( N );
		System.out.println("1426 Não autorizado pela auditoria");
end

rule "1427_NecessidadeDeAuditoriaMedica"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1427);
		N.setNegacao("Necessidade de auditoria médica");
		insert( N );
		System.out.println("1427 Necessidade de auditoria médica");
end

rule "1428_FaltaDeAutorizacaoDaEmpresaDeConectividade"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1428);
		N.setNegacao("Falta de autorização da empresa de conectividade");
		insert( N );
		System.out.println("1428 Falta de autorização da empresa de conectividade");
end

rule "1429_Cbo-S(Especialidade)NaoAutorizadoARealizarOServico"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1429);
		N.setNegacao("Cbo-s (especialidade) não autorizado a realizar o serviço");
		insert( N );
		System.out.println("1429 Cbo-s (especialidade) não autorizado a realizar o serviço");
end

rule "1430_ProcedimentoOdontologicoNaoAutorizado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1430);
		N.setNegacao("Procedimento odontológico não autorizado");
		insert( N );
		System.out.println("1430 Procedimento odontológico não autorizado");
end

rule "1431_ProcedimentoNaoAutorizadoNaFaceSolicitada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1431);
		N.setNegacao("Procedimento não autorizado na face solicitada");
		insert( N );
		System.out.println("1431 Procedimento não autorizado na face solicitada");
end

rule "1432_ProcedimentoNaoAutorizadoParaDenteRegiaoSolicitada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1432);
		N.setNegacao("Procedimento não autorizado para dente/região solicitada");
		insert( N );
		System.out.println("1432 Procedimento não autorizado para dente/região solicitada");
end

rule "1433_ProcedimentoNaoAutorizado,DenteAusente"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1433);
		N.setNegacao("Procedimento não autorizado, dente ausente");
		insert( N );
		System.out.println("1433 Procedimento não autorizado, dente ausente");
end

rule "1434_CobrancaDeContaDeCtiNeonatalNaSenhaDoParto"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1434);
		N.setNegacao("Cobrança de conta de cti neonatal na senha do parto");
		insert( N );
		System.out.println("1434 Cobrança de conta de cti neonatal na senha do parto");
end

rule "1435_VigenciaDoAcordoPosteriorADataDeRealizacaoDoProcedimento"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1435);
		N.setNegacao("Vigência do acordo posterior à data de realização do procedimento");
		insert( N );
		System.out.println("1435 Vigência do acordo posterior à data de realização do procedimento");
end

rule "1436_CancelamentoDoAcordoAnteriorADataDeRealizacaoDoProcedimento"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1436);
		N.setNegacao("Cancelamento do acordo anterior à data de realização do procedimento");
		insert( N );
		System.out.println("1436 Cancelamento do acordo anterior à data de realização do procedimento");
end

rule "1437_SenhaDeAutorizacaoCancelada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1437);
		N.setNegacao("Senha de autorização cancelada");
		insert( N );
		System.out.println("1437 Senha de autorização cancelada");
end

rule "1438_ProcedimentoSolicitadoNaoAutorizadoPorNaoAtenderADiretrizDeUtilizacao(Dut)DoRolDeProcedimentosEEventosEmSaudeDaAns"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1438);
		N.setNegacao("Procedimento solicitado não autorizado por não atender a diretriz de utilização (dut) do rol de procedimentos e eventos em saúde da ans");
		insert( N );
		System.out.println("1438 Procedimento solicitado não autorizado por não atender a diretriz de utilização (dut) do rol de procedimentos e eventos em saúde da ans");
end

rule "1501_TempoDeEvolucaoDaDoencaInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1501);
		N.setNegacao("Tempo de evolução da doença inválido");
		insert( N );
		System.out.println("1501 Tempo de evolução da doença inválido");
end

rule "1502_TipoDeDoencaInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1502);
		N.setNegacao("Tipo de doença inválido");
		insert( N );
		System.out.println("1502 Tipo de doença inválido");
end

rule "1503_IndicadorDeAcidenteInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1503);
		N.setNegacao("Indicador de acidente inválido");
		insert( N );
		System.out.println("1503 Indicador de acidente inválido");
end

rule "1504_CaraterDeInternacaoInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1504);
		N.setNegacao("Caráter de internação inválido");
		insert( N );
		System.out.println("1504 Caráter de internação inválido");
end

rule "1505_RegimeDaInternacaoInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1505);
		N.setNegacao("Regime da internação inválido");
		insert( N );
		System.out.println("1505 Regime da internação inválido");
end

rule "1506_TipoDeInternacaoInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1506);
		N.setNegacao("Tipo de internação inválido");
		insert( N );
		System.out.println("1506 Tipo de internação inválido");
end

rule "1507_UrgenciaEmergenciaNaoAplicavel"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1507);
		N.setNegacao("Urgência/emergência não aplicável");
		insert( N );
		System.out.println("1507 Urgência/emergência não aplicável");
end

rule "1508_CodigoCidNaoInformado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1508);
		N.setNegacao("Código cid não informado");
		insert( N );
		System.out.println("1508 Código cid não informado");
end

rule "1509_CodigoCidInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1509);
		N.setNegacao("Código cid inválido");
		insert( N );
		System.out.println("1509 Código cid inválido");
end

rule "1601_ReincidenciaNoAtendimento"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1601);
		N.setNegacao("Reincidência no atendimento");
		insert( N );
		System.out.println("1601 Reincidência no atendimento");
end

rule "1602_TipoDeAtendimentoInvalidoOuNaoInformado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1602);
		N.setNegacao("Tipo de atendimento inválido ou não informado");
		insert( N );
		System.out.println("1602 Tipo de atendimento inválido ou não informado");
end

rule "1603_TipoDeConsultaInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1603);
		N.setNegacao("Tipo de consulta inválido");
		insert( N );
		System.out.println("1603 Tipo de consulta inválido");
end

rule "1604_TipoDeSaidaInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1604);
		N.setNegacao("Tipo de saída inválido");
		insert( N );
		System.out.println("1604 Tipo de saída inválido");
end

rule "1605_IntervencaoAnteriorAAdmissao"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1605);
		N.setNegacao("Intervenção anterior a admissão");
		insert( N );
		System.out.println("1605 Intervenção anterior a admissão");
end

rule "1606_FinalDaIntervencaoAnteriorAoInicioDaIntervencao"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1606);
		N.setNegacao("Final da intervenção anterior ao início da intervenção");
		insert( N );
		System.out.println("1606 Final da intervenção anterior ao início da intervenção");
end

rule "1607_AltaHospitalarAnteriorAoFinalDaIntervencao"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1607);
		N.setNegacao("Alta hospitalar anterior ao final da intervenção");
		insert( N );
		System.out.println("1607 Alta hospitalar anterior ao final da intervenção");
end

rule "1608_AltaAnteriorADataDeInternacao"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1608);
		N.setNegacao("Alta anterior à data de internação");
		insert( N );
		System.out.println("1608 Alta anterior à data de internação");
end

rule "1609_MotivoSaidaInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1609);
		N.setNegacao("Motivo saída inválido");
		insert( N );
		System.out.println("1609 Motivo saída inválido");
end

rule "1610_ObitoMulherInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1610);
		N.setNegacao("Óbito mulher inválido");
		insert( N );
		System.out.println("1610 Óbito mulher inválido");
end

rule "1611_IntervencaoAnteriorAInternacao"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1611);
		N.setNegacao("Intervenção anterior a internação");
		insert( N );
		System.out.println("1611 Intervenção anterior a internação");
end

rule "1612_ServicoNaoPodeSerRealizadoNoLocalEspecificado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1612);
		N.setNegacao("Serviço não pode ser realizado no local especificado");
		insert( N );
		System.out.println("1612 Serviço não pode ser realizado no local especificado");
end

rule "1613_ConsultaNaoAutorizada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1613);
		N.setNegacao("Consulta não autorizada");
		insert( N );
		System.out.println("1613 Consulta não autorizada");
end

rule "1614_ServicoAmbulatorialNaoAutorizado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1614);
		N.setNegacao("Serviço ambulatorial não autorizado");
		insert( N );
		System.out.println("1614 Serviço ambulatorial não autorizado");
end

rule "1615_InternacaoNaoAutorizada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1615);
		N.setNegacao("Internação não autorizada");
		insert( N );
		System.out.println("1615 Internação não autorizada");
end

rule "1701_CobrancaForaDoPrazoDeValidade"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1701);
		N.setNegacao("Cobrança fora do prazo de validade");
		insert( N );
		System.out.println("1701 Cobrança fora do prazo de validade");
end

rule "1702_CobrancaDeProcedimentoEmDuplicidade"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1702);
		N.setNegacao("Cobrança de procedimento em duplicidade");
		insert( N );
		System.out.println("1702 Cobrança de procedimento em duplicidade");
end

rule "1703_HorarioDoAtendimentoNaoEstaNaFaixaDeUrgenciaEmergencia"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1703);
		N.setNegacao("Horário do atendimento não está na faixa de urgência/emergência");
		insert( N );
		System.out.println("1703 Horário do atendimento não está na faixa de urgência/emergência");
end

rule "1704_ValorCobradoSuperiorAoAcordadoEmPacote"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1704);
		N.setNegacao("Valor cobrado superior ao acordado em pacote");
		insert( N );
		System.out.println("1704 Valor cobrado superior ao acordado em pacote");
end

rule "1705_ValorApresentadoAMaior"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1705);
		N.setNegacao("Valor apresentado a maior");
		insert( N );
		System.out.println("1705 Valor apresentado a maior");
end

rule "1706_ValorApresentadoAMenor"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1706);
		N.setNegacao("Valor apresentado a menor");
		insert( N );
		System.out.println("1706 Valor apresentado a menor");
end

rule "1707_NaoExisteInformacaoSobreATabelaQueSeraUtilizadaNaValoracao.VerifiqueOContratoDoPrestador"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1707);
		N.setNegacao("Não existe informação sobre a tabela que será utilizada na valoração. verifique o contrato do prestador");
		insert( N );
		System.out.println("1707 Não existe informação sobre a tabela que será utilizada na valoração. verifique o contrato do prestador");
end

rule "1708_NaoExisteValorParaOProcedimentoRealizado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1708);
		N.setNegacao("Não existe valor para o procedimento realizado");
		insert( N );
		System.out.println("1708 Não existe valor para o procedimento realizado");
end

rule "1709_FaltaPrescricaoMedica"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1709);
		N.setNegacao("Falta prescrição médica");
		insert( N );
		System.out.println("1709 Falta prescrição médica");
end

rule "1710_FaltaVistoDaEnfermagem"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1710);
		N.setNegacao("Falta visto da enfermagem");
		insert( N );
		System.out.println("1710 Falta visto da enfermagem");
end

rule "1711_ProcedimentoPertenceAUmPacoteAcordadoEJaCobrado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1711);
		N.setNegacao("Procedimento pertence a um pacote acordado e já cobrado");
		insert( N );
		System.out.println("1711 Procedimento pertence a um pacote acordado e já cobrado");
end

rule "1712_AssinaturaDoMedicoResponsavelPeloExameInexistente"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1712);
		N.setNegacao("Assinatura do médico responsável pelo exame inexistente");
		insert( N );
		System.out.println("1712 Assinatura do médico responsável pelo exame inexistente");
end

rule "1713_FaturamentoInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1713);
		N.setNegacao("Faturamento inválido");
		insert( N );
		System.out.println("1713 Faturamento inválido");
end

rule "1714_ValorDoServicoSuperiorAoValorDeTabela"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1714);
		N.setNegacao("Valor do serviço superior ao valor de tabela");
		insert( N );
		System.out.println("1714 Valor do serviço superior ao valor de tabela");
end

rule "1715_ValorDoServicoInferiorAoValorDeTabela"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1715);
		N.setNegacao("Valor do serviço inferior ao valor de tabela");
		insert( N );
		System.out.println("1715 Valor do serviço inferior ao valor de tabela");
end

rule "1716_PercentualDeReducaoAcrescimoForaDosValoresDefinidosEmTabela"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1716);
		N.setNegacao("Percentual de redução/acréscimo fora dos valores definidos em tabela");
		insert( N );
		System.out.println("1716 Percentual de redução/acréscimo fora dos valores definidos em tabela");
end

rule "1717_PagoConformeRelatorioDeAuditoriaExterna-ContaInicial"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1717);
		N.setNegacao("Pago  conforme relatório de auditoria externa - conta inicial");
		insert( N );
		System.out.println("1717 Pago  conforme relatório de auditoria externa - conta inicial");
end

rule "1718_ReanaliseNegada,PagoConformeRelatorioAuditoria"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1718);
		N.setNegacao("Reanálise negada, pago conforme relatório auditoria");
		insert( N );
		System.out.println("1718 Reanálise negada, pago conforme relatório auditoria");
end

rule "1719_ReanaliseNegada,AnaliseConformeTabelaAcordada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1719);
		N.setNegacao("Reanálise negada, análise conforme tabela acordada");
		insert( N );
		System.out.println("1719 Reanálise negada, análise conforme tabela acordada");
end

rule "1720_Liberados150%DeVideo,SemCoberturaParaAdicionalDeAcomodacao"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1720);
		N.setNegacao("Liberados 150% de vídeo, sem cobertura para adicional de acomodação");
		insert( N );
		System.out.println("1720 Liberados 150% de vídeo, sem cobertura para adicional de acomodação");
end

rule "1721_CodigoCobradoSubstituidoPeloCodigoPago"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1721);
		N.setNegacao("Código cobrado substituído pelo código pago");
		insert( N );
		System.out.println("1721 Código cobrado substituído pelo código pago");
end

rule "1722_PagoConformeNegociacao"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1722);
		N.setNegacao("Pago conforme negociação");
		insert( N );
		System.out.println("1722 Pago conforme negociação");
end

rule "1723_AdicionalDeUrgenciaNaoPrevistoParaAtendimentoClinico"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1723);
		N.setNegacao("Adicional de urgência não previsto para atendimento clínico");
		insert( N );
		System.out.println("1723 Adicional de urgência não previsto para atendimento clínico");
end

rule "1724_VisitaMedicaCobradaPelaEquipeCirurgicaIncluidaNoPeriodoDe10DiasAposRealizacaoDoProcedimentoCirurgico"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1724);
		N.setNegacao("Visita médica cobrada pela equipe cirúrgica incluída no período de 10 dias após realização do procedimento cirúrgico");
		insert( N );
		System.out.println("1724 Visita médica cobrada pela equipe cirúrgica incluída no período de 10 dias após realização do procedimento cirúrgico");
end

rule "1725_ValorPagoAMaiorReferenteATaxaAdministrativa"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1725);
		N.setNegacao("Valor pago a maior referente à taxa administrativa");
		insert( N );
		System.out.println("1725 Valor pago a maior referente à taxa administrativa");
end

rule "1726_ValorApresentadoAMaior-PlanoIndividual"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1726);
		N.setNegacao("Valor apresentado a maior - plano individual");
		insert( N );
		System.out.println("1726 Valor apresentado a maior - plano individual");
end

rule "1727_PagoValorCompativelComOProcedimento"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1727);
		N.setNegacao("Pago valor compativel com o procedimento");
		insert( N );
		System.out.println("1727 Pago valor compativel com o procedimento");
end

rule "1728_CobrancaDeMaterialInclusoNoProcedimentoExameRealizado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1728);
		N.setNegacao("Cobrança de material incluso no procedimento / exame realizado");
		insert( N );
		System.out.println("1728 Cobrança de material incluso no procedimento / exame realizado");
end

rule "1729_CobrancaDeMaterialComValorAcimaDoPermitidoParaProcedimentoExameRealizado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1729);
		N.setNegacao("Cobrança de material com valor acima do permitido para procedimento/exame realizado");
		insert( N );
		System.out.println("1729 Cobrança de material com valor acima do permitido para procedimento/exame realizado");
end

rule "1730_FilmeInclusoNoExameRealizado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1730);
		N.setNegacao("Filme incluso no exame realizado");
		insert( N );
		System.out.println("1730 Filme incluso no exame realizado");
end

rule "1731_TaxaIncompativelParaAtendimentoAmbulatorial"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1731);
		N.setNegacao("Taxa incompativel para atendimento ambulatorial");
		insert( N );
		System.out.println("1731 Taxa incompativel para atendimento ambulatorial");
end

rule "1732_QtComDataDeEventoDivergenteDaLiberada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1732);
		N.setNegacao("Qt com data de evento divergente da liberada");
		insert( N );
		System.out.println("1732 Qt com data de evento divergente da liberada");
end

rule "1733_RecuperacaoDeValoresPorPagamentoIndevido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1733);
		N.setNegacao("Recuperação de valores por pagamento indevido");
		insert( N );
		System.out.println("1733 Recuperação de valores por pagamento indevido");
end

rule "1734_CobradoContaAberta,PagoOPacoteConformeNegociacao"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1734);
		N.setNegacao("Cobrado conta aberta, pago o pacote conforme negociação");
		insert( N );
		System.out.println("1734 Cobrado conta aberta, pago o pacote conforme negociação");
end

rule "1735_CobrancaDePacoteNaoNegociadoComOPrestador"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1735);
		N.setNegacao("Cobrança de pacote não negociado com o prestador");
		insert( N );
		System.out.println("1735 Cobrança de pacote não negociado com o prestador");
end

rule "1736_ContaAguardandoNegociacaoParaPagamento"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1736);
		N.setNegacao("Conta aguardando negociação para pagamento");
		insert( N );
		System.out.println("1736 Conta aguardando negociação para pagamento");
end

rule "1737_DiferencaDeveSerCobradaDoBeneficiarioPeloPrestadorComoFranquia"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1737);
		N.setNegacao("Diferença deve ser cobrada do beneficiário pelo prestador como franquia");
		insert( N );
		System.out.println("1737 Diferença deve ser cobrada do beneficiário pelo prestador como franquia");
end

rule "1738_DocumentoFiscalNaoEnviado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1738);
		N.setNegacao("Documento fiscal não enviado");
		insert( N );
		System.out.println("1738 Documento fiscal não enviado");
end

rule "1739_DuplicidadeDeContaDevidoAPeriodoCobradoJaEfetuadoEmOutraParcial"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1739);
		N.setNegacao("Duplicidade de conta devido a periodo cobrado já efetuado em outra parcial");
		insert( N );
		System.out.println("1739 Duplicidade de conta devido a periodo cobrado já efetuado em outra parcial");
end

rule "1740_EstornoDoValorDeProcedimentoPago"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1740);
		N.setNegacao("Estorno do valor de procedimento pago");
		insert( N );
		System.out.println("1740 Estorno do valor de procedimento pago");
end

rule "1741_HonorarioOuProcedimentoJaPagoAOutroPrestador"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1741);
		N.setNegacao("Honorário ou procedimento já pago a outro prestador");
		insert( N );
		System.out.println("1741 Honorário ou procedimento já pago a outro prestador");
end

rule "1742_HonorarioOuProcedimentoJaPagoPorReembolsoAoBeneficiario"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1742);
		N.setNegacao("Honorário ou procedimento já pago por reembolso ao beneficiário");
		insert( N );
		System.out.println("1742 Honorário ou procedimento já pago por reembolso ao beneficiário");
end

rule "1743_NaoHaNegociacaoParaCobrancaDoKit,DiscriminarPorItens"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1743);
		N.setNegacao("Não há negociação para cobrança do kit, discriminar por itens");
		insert( N );
		System.out.println("1743 Não há negociação para cobrança do kit, discriminar por itens");
end

rule "1744_NegociacaoDiferenciadaDevidoALiminar"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1744);
		N.setNegacao("Negociação diferenciada devido a liminar");
		insert( N );
		System.out.println("1744 Negociação diferenciada devido a liminar");
end

rule "1745_PagamentoDaEquipeConformeRelatorioDoCirurgiao"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1745);
		N.setNegacao("Pagamento da equipe conforme relatório do cirurgião");
		insert( N );
		System.out.println("1745 Pagamento da equipe conforme relatório do cirurgião");
end

rule "1746_PercentualDeAcrescimoDiferenteDoNegociado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1746);
		N.setNegacao("Percentual de acréscimo diferente do negociado");
		insert( N );
		System.out.println("1746 Percentual de acréscimo diferente do negociado");
end

rule "1747_PlanoDoBeneficiarioEOTipoDeAcomodacaoNaoPermitemAcrescimoDeHonorarios"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1747);
		N.setNegacao("Plano do beneficiário e o tipo de acomodação não permitem acréscimo de honorários");
		insert( N );
		System.out.println("1747 Plano do beneficiário e o tipo de acomodação não permitem acréscimo de honorários");
end

rule "1748_ProcedimentoNaoCaracterizaUrgenciaEmergencia"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1748);
		N.setNegacao("Procedimento não caracteriza urgência/emêrgencia");
		insert( N );
		System.out.println("1748 Procedimento não caracteriza urgência/emêrgencia");
end

rule "1749_RelatorioDeAuditoriaNaoEnviadoNaConta."
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1749);
		N.setNegacao("Relatório de auditoria não enviado na conta.");
		insert( N );
		System.out.println("1749 Relatório de auditoria não enviado na conta.");
end

rule "1801_ProcedimentoInvalido"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1801);
		N.setNegacao("Procedimento inválido");
		insert( N );
		System.out.println("1801 Procedimento inválido");
end

rule "1802_ProcedimentoIncompativelComOSexoDoBeneficiario"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1802);
		N.setNegacao("Procedimento incompatível com o sexo do beneficiário");
		insert( N );
		System.out.println("1802 Procedimento incompatível com o sexo do beneficiário");
end

rule "1803_IdadeDoBeneficiarioIncompativelComOProcedimento"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1803);
		N.setNegacao("Idade do beneficiário incompatível com o procedimento");
		insert( N );
		System.out.println("1803 Idade do beneficiário incompatível com o procedimento");
end

rule "1804_NumeroDeDiasLiberadosSessoesAutorizadasNaoInformadas"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1804);
		N.setNegacao("Número de dias liberados / sessões autorizadas não informadas");
		insert( N );
		System.out.println("1804 Número de dias liberados / sessões autorizadas não informadas");
end

rule "1805_ValorTotalDoProcedimentoDiferenteDoValorProcessado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1805);
		N.setNegacao("Valor total do procedimento diferente do valor processado");
		insert( N );
		System.out.println("1805 Valor total do procedimento diferente do valor processado");
end

rule "1806_QuantidadeDeProcedimentoDeveSerMaiorQueZero"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1806);
		N.setNegacao("Quantidade de procedimento deve ser maior que zero");
		insert( N );
		System.out.println("1806 Quantidade de procedimento deve ser maior que zero");
end

rule "1807_ProcedimentosMedicosDuplicados"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1807);
		N.setNegacao("Procedimentos médicos duplicados");
		insert( N );
		System.out.println("1807 Procedimentos médicos duplicados");
end

rule "1808_ProcedimentoNaoConformeComCid"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1808);
		N.setNegacao("Procedimento não conforme com cid");
		insert( N );
		System.out.println("1808 Procedimento não conforme com cid");
end

rule "1809_CobrancaDeProcedimentoNaoExecutado"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1809);
		N.setNegacao("Cobrança de procedimento não executado");
		insert( N );
		System.out.println("1809 Cobrança de procedimento não executado");
end

rule "1810_CobrancaDeProcedimentoNaoSolicitadoPeloMedico"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1810);
		N.setNegacao("Cobrança de procedimento não solicitado pelo médico");
		insert( N );
		System.out.println("1810 Cobrança de procedimento não solicitado pelo médico");
end

rule "1811_ProcedimentoSemRegistroDeExecucao"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1811);
		N.setNegacao("Procedimento sem registro de execução");
		insert( N );
		System.out.println("1811 Procedimento sem registro de execução");
end

rule "1812_CobrancaDeProcedimentoNaoCorrelacionadoAoRelatorioEspecifico"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1812);
		N.setNegacao("Cobrança de procedimento não correlacionado ao relatório específico");
		insert( N );
		System.out.println("1812 Cobrança de procedimento não correlacionado ao relatório específico");
end

rule "1813_CobrancaDeProcedimentoSemJustificativaParaRealizacaoOuComJustificativaInsuficiente."
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1813);
		N.setNegacao("Cobrança de procedimento sem justificativa para realização ou com justificativa insuficiente.");
		insert( N );
		System.out.println("1813 Cobrança de procedimento sem justificativa para realização ou com justificativa insuficiente.");
end

rule "1814_CobrancaDeProcedimentoComDataDeAutorizacaoPosteriorADoAtendimento."
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1814);
		N.setNegacao("Cobrança de procedimento com data de autorização posterior à do atendimento.");
		insert( N );
		System.out.println("1814 Cobrança de procedimento com data de autorização posterior à do atendimento.");
end

rule "1816_CobrancaDeProcedimentoEmQuantidadeIncompativelComOProcedimentoEvolucaoClinica"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1816);
		N.setNegacao("Cobrança de procedimento em quantidade incompatível com o procedimento/evolução clínica");
		insert( N );
		System.out.println("1816 Cobrança de procedimento em quantidade incompatível com o procedimento/evolução clínica");
end

rule "1817_CobrancaDeProcedimentoInclusoNoProcedimentoPrincipal"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1817);
		N.setNegacao("Cobrança de procedimento incluso no procedimento principal");
		insert( N );
		System.out.println("1817 Cobrança de procedimento incluso no procedimento principal");
end

rule "1818_CobrancaDeProcedimentoQueExigeAutorizacaoPrevia"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1818);
		N.setNegacao("Cobrança de procedimento que exige autorização prévia");
		insert( N );
		System.out.println("1818 Cobrança de procedimento que exige autorização prévia");
end

rule "1819_CobrancaDeProcedimentoComHistoriaClinicaHipoteseDiagnosticaNaoCompativel"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1819);
		N.setNegacao("Cobrança de procedimento com história clínica/hipótese diagnóstica não compatível");
		insert( N );
		System.out.println("1819 Cobrança de procedimento com história clínica/hipótese diagnóstica não compatível");
end

rule "1820_CobrancaDeProcedimentoEmQuantidadeAcimaDaMaximaPermitidaAutorizada"
	ruleflow-group "GrupoElegibilidade"
	dialect "mvel"
	when
		S:solicitacao()	
	then
		negacao N = new negacao();
		N.setCodigo(1820);
		N.setNegacao("Cobrança de procedimento em quantidade acima da máxima permitida/autorizada");
		insert( N );
		System.out.println("1820 Cobrança de procedimento em quantidade acima da máxima permitida/autorizada");
end

