package br.com.alura.rh.model;

import br.com.alura.rh.model.rules.ValidacaoReajuste;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ReajusteService {

    private List<ValidacaoReajuste> validacoes;

    public ReajusteService(List<ValidacaoReajuste> validacoes) {
        this.validacoes = validacoes;
    }

    public void reajustarSalario(Funcionario funcionario, BigDecimal aumento) {

        validacoes.forEach(validacao -> validacao.validar(funcionario, aumento));

        funcionario.getDadosPessoais().getSalario().add(aumento);
        funcionario.setDataUltimoReajuste(LocalDate.now());
    }
}
