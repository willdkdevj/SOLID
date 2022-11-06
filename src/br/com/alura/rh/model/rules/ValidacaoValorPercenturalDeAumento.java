package br.com.alura.rh.model.rules;

import br.com.alura.rh.ValidacaoException;
import br.com.alura.rh.model.Funcionario;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ValidacaoValorPercenturalDeAumento implements ValidacaoReajuste {
    @Override
    public void validar(Funcionario funcionario, BigDecimal aumento) {
        BigDecimal salario = funcionario.getDadosPessoais().getSalario();
        BigDecimal percentualReajuste = aumento.divide(salario, RoundingMode.HALF_UP);
        if (percentualReajuste.compareTo(new BigDecimal("0.4")) > 0) {
            throw new ValidacaoException("Reajuste nao pode ser superior a 40% do salario!");
        }
    }
}
