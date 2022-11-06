## API REST para o Consulta e Geolocalização de Cidades do Brasil
> A API REST consiste de uma ferramenta de consulta utilizando técnica de paginação para performance e realizando cálculos de geolocalização.

[![Spring Badge](https://img.shields.io/badge/-Spring-brightgreen?style=flat-square&logo=Spring&logoColor=white&link=https://spring.io/)](https://spring.io/)
[![Maven Badge](https://img.shields.io/badge/-Maven-000?style=flat-square&logo=Apache-Maven&logoColor=white&link=https://maven.apache.org/)](https://maven.apache.org/)
[![Docker Badge](https://img.shields.io/badge/-Docker-blue?style=flat-square&logo=Docker&logoColor=white&link=https://www.docker.com/products/docker-hub/)](https://www.docker.com/products/docker-hub/)
[![PostgreSQL Badge](https://img.shields.io/badge/-PostgreSQL-informational?style=flat-square&logo=PostgreSQL&logoColor=white&link=https://www.postgresql.org/)](https://www.postgresql.org/)
[![Heroku Badge](https://img.shields.io/badge/-Heroku-blueviolet?style=flat-square&logo=Heroku&logoColor=white&link=https://id.heroku.com/)](https://id.heroku.com/)


<img align="right" width="400" height="300" src="https://github.com/willdkdevj/assets/blob/main/Spring/spring-framework.png">

## Descrição da Aplicação
A aplicação consiste em uma API (*Application Programming Interface*) REST (*Representational State Transfer*), sendo aplicado o modelo cliente/servidor na qual tem a função de enviar e receber dados através do protocolo HTTP, sendo o seu principal objetivo permitir a interoperabilidade entre aplicações distintas. Mas nesta aplicação, o intuito é emula um serviço web que interage com um serviço de banco de dados a fim de consultar registro sobre países, estados, e cidades. Quanto a consulta a estados e a cidades, estão disponíveis apenas os registros do Brasil. Além disso, é possível realizar cálculos de distância entre dois parâmetros de localização, neste caso, entre duas cidades para obter a distância entre ambos.

Referente a consulta, como ela pode retornar um volume considerável de registros é utilizado o recurso de paginação a fim de obter um ganho de performance ao obter e na apresentação dos dados. Já os cálculos estão disponíveis por meio de dois modelos, o primeiro é habilitado através de extensões disponíves através do SGBD PostgreSQL, que fornecem funções que automatizam o cálculo, já o segundo, foi a partir do desenvolvimento de métodos que recebem o diâmetro do planeta Terra e realiza o cálculo através da passagem de Pontos de Localização, possível ao implementar a classe Point do springframework.

No decorrer deste documento é apresentado com mais detalhes sua implementação, descrevendo como foi desenvolvida a estrutura da API, suas dependências e como foi colocado em prática a implementação dos cálculos e listagem por paginação. Além disso, como foi implementado o Spring Boot, para agilizar a construção do código e sua configuração, conforme os *starters* e as suas dependências. Bem como, o Spring Data JPA, que nos dá diversas funcionalidades permitindo uma melhor dinâmica nas operações com bancos de dados e sua manutenção. Até o seu deploy na plataforma Heroku para disponibilizá-la pela nuvem ao cliente.

## Principais Frameworks
Os frameworks são pacotes de códigos prontos que facilita o desenvolvimento de aplicações, desta forma, utilizamos estes para obter funcionalidades para agilizar a construção da aplicação. Abaixo segue os frameworks utilizados para o desenvolvimento este projeto:

**Pré-Requisito**: Java 11 (11.0.10 2021-01-19 LTS) / Maven 3.6.3 / Docker 20 (20.10.6 build 370c289)

| Framework       | Versão | Função                                                                                            |
|-----------------|:------:|---------------------------------------------------------------------------------------------------|
| Spring Boot     | 2.4.4  | Permite agilizar o processo de configuração e publicação de aplicações do ecossistema Spring      |
| Spring Actuator | 2.4.4  | Fornece endpoints que permite verificar o estado da aplicação através de métricas                 |
| Spring Data JPA | 2.4.4  | Facilita na interação com database permitindo uma fluídez na persistência dos dados de modo geral |
| Hibernate       | 6.1.7  | Permite automatizar as tarefas com o banco de dados facilitando o código da aplicação             |
| Lombok          | 1.18.18| Permite reduzir a verbosidade do código através de anotações                                      |
| MapStruct       | 1.4.1  | Permite o mapeamento entre bean Java com base de uma abordagem de conversão sobre configuração    |
| JUnit 	        | 5.7.1  | Permite a realização de testes unitários de componentes da aplicação Java                         |
| Mockito         | 3.6.28 | Permite criar objetos dublês a fim de realizar testes de unidade em aplicações Java               |
| Swagger         | 2.9.2  | Possibilita a definição e a criação de modo estruturado a documentação de API REST                | 

### Utilizando Docker para Disponibilizar o PostgreSQL
Pode ser um Sistema de Gerenciamento de Banco de Dados (SGBD) PostgreSQL instalado na máquina, mas para o projeto foi construído um container Docker personalizado (através de um *Dockerfile*) com o sistema encapsulado, na qual ao executá-lo, é criado uma database já com todas as tabelas necessárias para utilizar na API. Mas caso exista um SGBD PostgreSQL instalado no host, no diretório ``docker/scriptSQL`` estão scripts SQL para serem executados no sistema.

Para utilizar o SGBD através do Docker digite o *snippet* abaixo para configurar o container e deixá-lo apto para uso da API.
```bash
docker run --name postgres-cities -d -p 5432:5432 -e POSTGRES_USER=postgres_supernova_user  -e POSTGRES_PASSWORD=supernova_pass -e POSTGRES_DB=citiesBrazil willdkdev/postgres-cities:latest
```

O comando run tentará encontrar a imagem no repositório local, onde não encontrará, desta forma, ele acessará o Docker Hub para encontrar e baixar a imagem **postgres-cities**, fornendo uma *alias* para invocá-lo ao invés de utilizar o container ID. Também é exposta a porta 5432 atrelando-a a porta de mesmo número no host, além disso, são fornecidas variáveis (enviroment (-e)) para passar os parãmetros (usuário, senha, database) para configuração do PostgreSQL. Enquanto o parâmetro willdkdev/postgres-cities:latest referente ao proprietário, imagem e versão.

## Utilizando o Pageable para Paginação de Grandes Volumes de Dados
A paginação é utilizida ao realizar uma requisição de consulta a um grande volume de dados, ela possibilita filtrar a quantidade de registros que serão retornados informando mais parâmetros que funcionam como filtros especificos para a *Query*. Desta forma, é possível restringir a quantidade de registros que serão apresentados por intervalo de páginas. É possível realizar estes "filtros" na própria query, mas a paginação permite que seja passado como parâmetro da própria **URI** utilizando os recursos do Spring Data, através da dependência ``spring-boot-starter-data-jpa``.

Agora, se faz necessário que a interface *Repository* extenda a também interface JpaRepository. Pois a interface JpaRepository também extende a **PagingAndSortingRepository**, sendo ela que possibilita interpretar o **Pageable** recebido por parâmetro através de um *Resource* utilizando o tipo de requisição GET, desta forma, utilizando as vantagens do fator de multi-herança.
```java
@GetMapping("/cities")
  public Page<City> searchForCities(final Pageable page) {
      return serviceCity.returnCatalogCities(page);
  }
```

![Pageable - ListAll](https://github.com/willdkdevj/assets/blob/main/Spring/api-citiesBrazil/api-cities-list.png)

Também é possível passar parâmetros padrões (*default*) a fim de serem aplicados assim que o *resouce* é invocado, ao utilizar a anotação **@PageableDefault**. Para isso, se faz necessário passar alguns atributos para sua validação, são eles:
* **Page** - identifica qual a página a ser retornada de uma lista;
* **Size** - identifica a quantidade de registros a serem apresentados na página;
* **Sort** - define a ordenação dos registros através do nome do campo;
* **Direction** - define o tipo de ordenação a ser aplicada a paginação (Crescente (ASC) / Decrescente (DESC)).
```java
@GetMapping("/cities")
  public ResponseEntity searchForCities(
         @PageableDefault(sort = "name",
                 direction = Sort.Direction.ASC,
                 page = 0,
                 size = 10) Pageable page){

      return ResponseEntity.ok(serviceCity.returnCatalogCities(page));
  }
```

Assim como, direto na **URI** conforme é apresentado na imagem, através da ferramenta Insomnia que apresenta uma requisição do tipo GET.

![PageableDefault - ListAll](https://github.com/willdkdevj/assets/blob/main/Spring/api-citiesBrazil/api-cities-list-param.png)

## Utilizando Cálculos para Retornar a Distância entre Dois Pontos
Umas das funções da API é retornar a distância entre dois pontos para obter seu valor em **Milhas** e **Metros**, o cálculo da distância entre dois pontos no espaço é um assunto discutido na *Geometria Analítica* e tem suas bases no teorema de Pitágoras. A distância entre dois pontos no espaço é o comprimento do menor segmento de reta que liga esses dois pontos, para isso, é necessário calcular antes a distância entre dois pontos no plano. 

### Habilitando Extensões para Uso de Funções do PostgreSQL para Geolocalização
Além do uso de cálculo para encontrarmos a distância entre dois pontos, na tabela **cidade** temos o atributo *lat_lon* que é do tipo Point, este atribuito em especial permite inserir valores para passagem de parâmetros de localização, conhecidos como pontos, através deles é possível para determinar a geolocalização. Geolocalização é um recurso que permite determinar a posição geográfica de um individuo com base em um sistema de coordenadas.

O PostgreSQL possui suporte para trabalhar com *Geolocalização* ao habilitar extensões especificas do SGBD para permitir que ele reconheça este tipo de atributo e suas funções para realizar cálculos a partir dos mesmos. Estas extensões já estão habilitadas em nosso container Docker ao criarmos uma instância no daemon. Elas também estão presentes no script ``3_cidade.sql``, caso tenha o SGBD instalado no host e deseja utilizá-lo. Abaixo segue os snippet's responsável por habilitá-los.
```sql
CREATE EXTENSION cube;
CREATE EXTENSION earthdistance;
```
A partir destas extensões podemos invocar funções e operadores do próprio PostgreSQL para realizarmos o cálculo de distância entre dos pontos, onde uma delas é a função distanceByPoints(), na qual podemos invocá-la em nosso serviço graças a **JPA Data**. O retorno deste método é em *milhas*.
```java
public MessageResponse distanceByLocationInMilesPostgre(String city1, String city2) 
                                                        throws UrbeNotFoundException {
    City foundCity1 = checkedCityByName(city1);
    City foundCity2 = checkedCityByName(city2);

    Double obtainedDistance = repository.distanceByPoints(foundCity1.getId(), foundCity2.getId());

    return createMessageResponse(obtainedDistance, 
                      "The distance in miles obtained by PostgreSQL between the two points is: ");
}
```

![DistanceByPoints - CityLocation](https://github.com/willdkdevj/assets/blob/main/Spring/api-citiesBrazil/distance-in-miles.png)

Já a função *Cube* permite o uso do método distanceByCube(), que permite passarmos dois pontos de geolocalização que constite na passagem de latitude e longitude de dois pontos, onde é obtida o retorno da distância entre estes dois pontos em *metros*.

```java
public MessageResponse distanceInMetersPostgre(String city1, String city2) 
                                               throws UrbeNotFoundException {
    City foundCity1 = checkedCityByName(city1);
    City foundCity2 = checkedCityByName(city2);

    Point point1 = foundCity1.getLocation();
    Point point2 = foundCity2.getLocation();

    Double obtainedDistance = repository.distanceByCube(point1.getX(), point1.getY(),
                                                        point2.getX(), point2.getY());

    return createMessageResponse(obtainedDistance,
                      "The distance in meters obtained by PostgreSQL between the two points is: ");
}
```

![PageableDefault - ListAll](https://github.com/willdkdevj/assets/blob/main/Spring/api-citiesBrazil/distance-in-meters.png)

### Desenvolvimento de Lógica para Cálculo de Distância
Através da circunferência terrestre é possível passar ao raio da Terra (por meio de medidas de distância) assim como Eratóstenes fez para realizar o cálculo entre Siena e Alexandria, mas com muito mais recursos que ele teve. Para isso, foi utilizado a trigonometria para realizar o cálculo do **Seno** e o **Cosseno** referente a *latitude* e *longitude* dos pontos, sobreponduas para retornar a distância entre as mesmas.

Eratóstenes calculou o raio da Terra com uma margem de erro de centímetros há mais de 2.000 anos atrás só com uma estaca como ferramenta de trabalho. *Eratóstenes de Cirene* (**Eratosthéni̱s; Cirene, 276 a.C. — Alexandria, 194 a.C.**) foi além de matemático, gramático, poeta, geógrafo, bibliotecário e astrônomo da Grécia Antiga. Nasceu em Cirene, na África, e morreu em Alexandria. 
Já nós conseguimos estas medidas facilmente utilizando o *Google*, desta forma, através desta ferramentas foram obtidas as medidas em metros, kilometros e milhas, e criado um ``Enum`` para criarmos constantes com estes valores.
```java
@Getter
@AllArgsConstructor
public enum EarthRadius {
    METERS("m", 6371182f),
    KILOMETERS("km", 6371.182f),
    MILES("mi",3963.799824f);

    private String unit;
    private Float value;
}
```

Quanto a lógica utilizada para o cálculo, o método distanceByLocationByRadius() recebe como parâmetros o nome de duas cidades e qual o tipo de medida, referente ao raio da Terra, deve ser retornado ao solicitante. Ele realiza a checagem se existem as cidades informadas como parâmetro e obtem suas coordenadas (latitude e longitude) invocando um segundo método (*convertGeo()*) que são convertidas como números flutuantes, desta maneira, é repassado para um terceiro método, onde este tem a propriedade de realizar o cálculo com as latitudes e longitudes obtidas, além do tipo de medida para determinar o tipo de distância.
```java
/*
 * METHOD INVOKED BY THE CONTROLLER: This method receives the names of two cities plus the type of measurement
 * through the Earth's radius, which checks the existence of the cities in the database and converts the location
 * into a double to perform the calculation.
 */
public MessageResponse distanceByLocationByRadius(String city1, String city2, EarthRadius radius)
            throws UrbeNotFoundException {
    City foundCity1 = checkedCityByName(city1);
    City foundCity2 = checkedCityByName(city2);

    List<City> cities = repository.findAllById(Arrays.asList(foundCity1.getId(),
                                                             foundCity2.getId()));

    Double[] location1 = convertGeo(cities.get(0).getGeolocation());
    Double[] location2 = convertGeo(cities.get(1).getGeolocation());

    Double obtainedDistance = calculateDistance(location1[0],
                                                location1[1],
                                                location2[0],
                                                location2[1], radius);

    MessageResponse message = createMessageResponse(obtainedDistance, "The distance between the two points is: ");
    message.setMessage(message.getMessage() + " " + radius.getUnit());

    return message;
}

/* 
 * RESPONSIBLE METHOD OF CONVERTING LOCATION: This method takes the location as a string and converts it into two 
 * double parameters, which are the location points of the city.
 */
private Double[] convertGeo(String value){
    String obtainedString = value.replace("(", "")
                                 .replace(")", "");
    String[] values = obtainedString.trim().split(",");
    return new Double[] {Double.valueOf(values[0]), Double.valueOf(values[1])};
}

/*
 * METHOD RESPONSIBLE FOR PERFORMING THE CALCULATION: This method takes as a parameter the latitudes and longitude 
 * of two cities and the radius of the Earth, which can be passed as Miles, Kilometers or Meters.
 */
private Double calculateDistance(Double latitude1, Double longitude1,
                                 Double latitude2, Double longitude2, EarthRadius radius){
    Double differenceBetweenLatitudes = toRadians(latitude2 - latitude1);
    Double differenceBetweenLongitudes = toRadians(longitude2 - longitude1);
    Double obtainedArea = sin(differenceBetweenLatitudes / 2) * sin(differenceBetweenLatitudes / 2) +
                          cos(toRadians(latitude1)) * cos(toRadians(latitude2)) * sin(differenceBetweenLongitudes /2) * sin(differenceBetweenLongitudes / 2);
    Double factor = 2 * atan2(sqrt(obtainedArea), sqrt(1 - obtainedArea));

    return radius.getValue() * factor;
}
```

![Distance Calculation](https://github.com/willdkdevj/assets/blob/main/Spring/api-citiesBrazil/distance-calculation-kilo.png)

### Tirando a Contra-Prova com o Google Maps
Não tenho ideia como o Google cálcula a distância entre dois pontos, mas utilizaremos a API e seus métodos para compararmos os retornos obtidos por ela e também comparar os retornos obtidos pelas funções do PostgreSQL.

Como foi apresentado nas imagens que apresentam o retorno dos cálculos de cada método, utilizamos para análise as cidades de Atibaia e Guarulhos, ambas do estado de São Paulo. No Google Maps passamos as coordenadas (*geolocation*) obtidas no consultá-las em nossa API, desta forma obtemos os seguintes resultados.

![Atibaia City](https://github.com/willdkdevj/assets/blob/main/Spring/api-citiesBrazil/maps-Atibaia.png)
> A imagem abaixo apresenta o resultado obtido da cidade de Atibaia - SP.

![Guarulhos City](https://github.com/willdkdevj/assets/blob/main/Spring/api-citiesBrazil/maps-Guarulhos.png)
> A imagem abaixo apresenta o resultado obtido da cidade de Guarulhos - SP.

Depois aplicamos para que fosse feita o cálculo de distância entre as duas cidades, na qual por padrão, ele assume como premissa um meio de locomoção entre vias de tráfego possível para se chegar ao destino, deste exemplo foi utilizado um veículo.

![Google Maps](https://github.com/willdkdevj/assets/blob/main/Spring/api-citiesBrazil/between-cities.png)

Mas foi utilizado um recurso no Google Maps que permite traçarmos uma reta entre estes dois pontos para obtermos qual a distância entre dois pontos em uma reta. Sendo esta a aritmética aplicada em nosso cálculo para determinar a distância entre os polos. Note que abaixo é apresentado um *Card*, nele é apresentado os valores obtidos pela reta traçada. 

Na tabela abaixo são apresentados os resultados obtidos por cada aplicação:

|                     | Kilometros | Milhas |
|---------------------|------------|--------|
| Google Maps         | 37,90km    | 23,55mi|
| Function PostgreSQL | 37,55km    | 16,08mi|
| Logic REST API      | 37,51km    | 23,33mi|

Para obtermos paridade entre as comparações convertemos a saída obtida pela função PostgreSQL ``distanceByCube``, pois ela retorna o retorno em metros, desta forma, dividimos o valor por 1000 que corresponde a quantidade de metros que há em um Kilometro. Já com a função ``distanceByPoints`` não foi necessária realizar esta medida pois ela retorna o valor em milhas.

Por fim, podemos notar que a diferença entre as aplicações é mínima, na qual pode ser que o valor do *raio* da Terra aplicado para o cálculo tenha diferença de décimos, ocasionando esta disparidade.

## A Hospedagem na Plataforma Heroku
Para hospedar nosso código na plataforma **Heroḱu** é necessário criar uma conta e atrelá-la a conta no **GitHub**, desta forma, ao logar no *Dashboard* do Heroku é criado um novo aplicativo apontando a conta do GitHub informando o nome do repositório em que está o projeto. Além disso, é habilitado a opção de *deploy* automático, para que todas as vezes que for realizado um *PUSH* para o repositório seja realizado o deploy da aplicação.

Como a aplicação está com a versão 11 do Java é necessário passar um parâmetro de configuração ao Heroku, pois por padrão, o Heroku suporta aplicações com a versão 8. Desta forma, no diretório raiz do projeto é criado o arquivo de configuração ``system.properties`` com o seguinte snippet
```sh
java.runtime.version=11
```

Este processo de criação foi realizado antes de "subir" a aplicação para a plataforma, desta maneira, ela reconhece a aplicação com a versão informada.

![Deploy Heroku](https://github.com/willdkdevj/assets/blob/main/Heroku/deploy_heroku_person.png)

Para acessar a aplicação diponibilizada em *cloud*, acesse o seguinte link <https://apipeople-dio.herokuapp.com/api/v1/people>. Desta forma, é possível realizar as interações com a ferramenta das requisições HTTP.

## Como Está Documentado o Projeto
O framework ``Swagger UI`` auxilia na criação da documentação do projeto, por possuir uma variedade de ferramentas que permite modelar a descrição, consumo e visualização de serviços da API REST. No projeto foi incluída suas dependências (Swagger-UI, Swagger-Core) para habilitá-lo para uso na aplicação, desta forma, no *snippet* abaixo é apresentado o Bean principal para sua configuração, presente na classe SwaggerConfig.

```java
@Bean
public Docket api() {
return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(apis())
        .paths(PathSelectors.any())
        .build()
        .apiInfo(constructorApiInfo());
}
```

A especificação da API consiste na determinação de parâmetros de identificação e os modelos de dados que serão aplicados pela API, além de suas funcionalidades. Entretanto, o Swagger por padrão lista todos os endpoints retornando os códigos 200, 201, 204,401,403 e 404, mas é possível especificar quais são os códigos do protocolo HTTP que sua aplicação utiliza ao utilizar a anotação @ApiResponses.

![Framework Project - Test](https://github.com/willdkdevj/assets/blob/main/Spring/swagger_panel_person.png)

O método apis() permite utilizar a classe **RequestHandlerSelectors** para filtrar qual é o pacote base (*basePackage*) a fim de ser listado apenas os seus endpoints. Já o método apiInfo() possibilita inserir parâmetros para descrever dados de informação sobre a aplicação e seu desenvolvedor. Desta forma, o framework Swagger possibilita documentar a API REST de um modo ágil de eficiente as suas funcionalidades. Sua exposição é feita através do link <http://localhost:8080/swagger-ui.html>

## Como Executar o Projeto

```bash
# Para clonar o repositório do projeto, através do terminal digite o comando abaixo
git clone https://github.com/willdkdevj/RESTAPI_PERSONS.git

# Para acessar o diretório do projeto digite o comando a seguir
cd /RESTAPI_PERSONS

# Executar projeto via terminal, digite o seguinte comando
./mvnw spring-boot:run

# Para Executar a suíte de testes desenvolvidas, basta executar o seguinte comando
./mvnw clean test
```

Para testar a API, como a aplicação consome e produz JSON, é necessário utilizar uma ferramenta que permite realizar requisições HTTP neste formato, como o Postman, Insomnia, entre outras. Na diretório ``JSON-TEST-HTTP/``  há um arquivo que pode ser importado a qualquer uma destas ferramentas, onde já estarão formatados os tipos de requisições suportadas pela API para a realização dos testes.

## Agradecimentos
Obrigado por ter acompanhado aos meus esforços para desenvolver este Projeto utilizando a estrutura do Spring para criação de uma API REST 100% funcional, utilizando os recursos do Spring data JPA para facilitar as consultas, o padrão DTO para inclusão e atualização dos dados, além de, listar grandes quantidades de dados paginas, com ordenação e busca, utilizando os conceitos do TDD para implementar testes de integração para validar nossos endpoints com o MockMVC e gerar a documentação de forma automática através do Swagger! :octocat:

Como diria um velho mestre:
> *"Cedo ou tarde, você vai aprender, assim como eu aprendi, que existe uma diferença entre CONHECER o caminho e TRILHAR o caminho."*
>
> *Morpheus - The Matrix*