package br.com.alura.rh.model.rules;

import br.com.alura.rh.ValidacaoException;
import br.com.alura.rh.model.Funcionario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ValidacaoPeriodicidadeEntreReajustes implements ValidacaoReajuste {

    public void validar(Funcionario funcionario, BigDecimal aumento){
        LocalDate dataUltimoReajuste = funcionario.getDataUltimoReajuste();
        LocalDate dataAtual = LocalDate.now();
        if (ChronoUnit.MONTHS.between(dataUltimoReajuste, dataAtual) < 6) {
            throw new ValidacaoException("Intervalo de reajustes deve ser maior que 6 meses!");
        }
    }
}
