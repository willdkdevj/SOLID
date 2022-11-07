## Conceito aplicado em projeto sobre SOLID
> SOLID é um acronomo de um CONCEITO que ao aplicado a um projeto de código permite estruturá-lo de modo mais coeso, com estruturas encapsuladas e fracamente acopladas, tornando-o mais fácil de manter, modificar e estender seu escopo original.

[![Maven Badge](https://img.shields.io/badge/-Maven-000?style=flat-square&logo=Apache-Maven&logoColor=white&link=https://maven.apache.org/)](https://maven.apache.org/)
[![Solid Badge](https://img.shields.io/badge/-SOLID-blue?style=flat-square&logo=SOLID&logoColor=white&link=https://pt.wikipedia.org/wiki/SOLID)](https://pt.wikipedia.org/wiki/SOLID)


<img align="right" width="400" height="300" src="https://github.com/willdkdevj/assets/blob/main/Spring/solid.png">

## Descrição da Aplicação
A aplicação consiste em um simples projeto que simula algumas classes de um sistema de Recursos Humanos (RH), na qual necessita administrar os cadastros de seus funcionários e seus terceirizados e suas respectivas particularidades, como promoções, alterações de salários e regras de negócios.

## Programação Orientada a Objeto e o SOLID
Na programação de computadores orientada a objetos, o termo SOLID é um acrônimo para cinco postulados de design, destinados a facilitar a compreensão, o desenvolvimento e a manutenção de software.

A principal da aplicação do SOLID é tornar o processo de desenvolvimento mais fácil de evoluir.

### [S] Single Responsability Principle
Uma casse (componente, entidade) deve ter uma única responsabilidade;

### [O] Open-Closed Principle
Uma classe (componente, entidade) pode ser extender novas funções desde que não a modifique;

### [L] Liskov Substitution Principle
Cuidado com herança para não causar exceções em funcionalidades herdadas devido a função que não fazem sentido ao seu escopo;

### [I] Interface Segregation Principle
Clientes não devem ser forçados a utilizar métodos que não necessitam;

### [D] Dependency Inversion Principle
Clientes não deve ser obrigados a implementar um método que eles não precisam;


## Aplicação dos Conceitos ao Projeto
Abaixo é descrito o que foi aplicado para utlizar os conceitos do SOLID.

Retirar a responsabilidade de analisar a possibilidade de ajuste no salário do funcionário através da *Classe Funcionario* transferindo tal responsabilidade para a *Classe ReajusteService*. Desta forma, utilizando o conceito do **Single responsability Principle**.
```java
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
```
Utilizado classes para colocar cada regra de negócio correspondente a reajustes em classes e como as mesmas utlizam os mesmos parâmetros para relizarem a modificações é criada uma interface para padronizar como uma regra de negócio ligada a reajuste deve se comportar.
```java
public interface ValidacaoReajuste {

    void validar(Funcionario funcionario, BigDecimal aumento);
}
```

É possível notar que na *Classe ReajusteService* utiliza a interface para verificar as regras referente aos reajustes, aplicando outro conceito, o de **polimorfísmo**, a fim de interar com todas as regras que a implementam. 

Desta forma, é aplicado os conceito de **Open-Closed Principle**, onde a classe não precisa realizar qualquer tipo de modificação caso outra regra de reajuste seja inserida ou retirada, mantendo sua estrutura intacta como quando foi criada. Além disso, é aplicado o conceito do **Dependency Inversion Principle**, onde aplicamos a dependencia a uma interface que padroniza a entrada do dado a ser fatorado.

Outro conceito utilizado foi o **Liskov Substitution Principle**, que trata que uma classe não deve herdar funções de sua antecessora que não fazem sentido para seu contexto na aplicação. Neste caso, foi utilizado o conceito de **composição**, ao invés, de utilizar o conceito de **herança**. Isso foi pensado devido a possibilidade de surgir a regra de controlar os funcionários tercerizados pela aplicação.
Desta forma, pensando que um profissional terceirizado também é um funcionário é possível extender a *Classe Funcionario* na *Classe Terceirizado* ficando da seguinte forma:
```java
public class Terceirizado extends Funcionario {}
```

O problema em realizar esta implementação é que toda a regra aplicada para um funcionário da empresa também será aplicada a funcionários terceirizados, sendo que os mesmos podem seguir outras regras aplicadas pela empresa contratada que estão os alocando. Desta forma, para evitar esta herança e também a duplicação de código, devido que grande parte dos atributos utilizados por uma classe são similares a que a outra utilizará, foi criada a *Classe DadosPessoais* para compor estas classe com estes atributos de forma encapsulada.
```java
public class DadosPessoais {

    private String nome;
    private String cpf;
    private Cargo cargo;
    private BigDecimal salario;

    public DadosPessoais(String nome, String cpf, Cargo cargo, BigDecimal salario) {
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.salario = salario;
    }

    \\Getter and Setter
}
```

Quanto ao conceito do **Interface Segregation Principle**, não foi implementado no projeto, mas diz respeito que uma classe não deve implementar uma interface que possuem métodos que não fazem sentido para o seu contexto na aplicação, onde poderia existir uma nova regra de negócio associada a promoções e a impostos que utilizasse os atributos valor, cargo e valor da tributação, sendo que poderia ser pensado que os três atributos poderiam ser associados a uma única interface. E esta aplicada as classes para implementar a lógica aplicada para as regras, mas promoção não tem sentido utilizar o parâmetro **valor de tributação**, onde poderia ser retirada da interface e utilizada por uma nova interface que estaria voltada para as regras associadas aos impostos. Desta forma, as classes só implementariam as interfaces que fazem sentido para o contexto de promoções ou para impostos.
Legal, mas seria necessário copiar os atributos de promoções para refleti-las também em impostos já que são comuns entre as interfaces?
```java
    public interface Impostos extends Promocoes
```

Não! É possível fazer que a **Interface Impostos** extenda a **Interface Promocoes** e que por herança possua os mesmos comportamento de sua antecessora.

Legal, néh?

## Agradecimentos
Obrigado por ter acompanhado aos meus esforços para aplicar os conceitos do SOLID ao Projeto :octocat:

Como diria um velho mestre:
> *"Cedo ou tarde, você vai aprender, assim como eu aprendi, que existe uma diferença entre CONHECER o caminho e TRILHAR o caminho."*
>
> *Morpheus - The Matrix*