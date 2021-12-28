package com.mrmarmitt.scraping.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.mrmarmitt.scraping.dto.ImovelDto;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CaixaListaImoveisService {
  private final static Logger LOGGER = LoggerFactory.getLogger(CaixaListaImoveisService.class);

  private final String URL_PRINCIPAL = "https://venda-imoveis.caixa.gov.br/sistema/download-lista.asp";
  private final String URL_POR_UF = "https://venda-imoveis.caixa.gov.br/listaweb/Lista_imoveis_{uf}.htm?{codigoRequisicao}";

  private final JsoapService jsoapService;

  public CaixaListaImoveisService(JsoapService jsoapService) {
    this.jsoapService = jsoapService;
  }

  public ImovelDto obtemImovelDto(final URI uri) throws Exception {
    final Document documento = jsoapService.getDocument(uri.toString());

    // System.out.println(getTitulo(documento));
    System.out.println(uri.toString());
    System.out.println(getValorDeAvaliacao(documento));
    return null;
  }
  
  public String obtemCodigoObrigatorioParaRequisicao() throws Exception {
    final Element funcoesJS = obtemFuncoesJsDoHtml();

    return extraiCodigoRequisicaoDaFuncaoJs(funcoesJS);
  }

  private Element obtemFuncoesJsDoHtml() throws Exception {
    LOGGER.info("Obtendo funcoes javascript do html.");
    final Document documento = jsoapService.getDocument(URL_PRINCIPAL);

    final Evaluator scriptTag = new Evaluator.Tag("script");
    final Evaluator nomeFuncao = new Evaluator.ContainsData("BaixarLista");

    return documento.select(new Evaluator.AllElements()).stream()
        .filter(e -> e.is(scriptTag))
        .filter(e -> e.is(nomeFuncao))
        .findFirst()
        .orElseThrow(() -> new Exception("Nao foi possivel encontrar a funcao com o codigo da requisicao."));

  }

  private String extraiCodigoRequisicaoDaFuncaoJs(final Element funcoes) throws Exception {
    LOGGER.info("Extraindo codigo obrigatorio de requisicao.");
    final int POSICAO_CODIGO_REQUISICAO = 2;
    /*
     * Retorna dois grupos:
     * O primeiro é o .htm? necessario para encontrar o codigo principal da
     * requisicao.
     * O segundo é o codigo da requisicao.
     */
    final Pattern padrao = Pattern.compile("(.htm\\?)(\\d*)");
    final Matcher combinacao = padrao.matcher(funcoes.toString());

    if (combinacao.find())
      return combinacao.group(POSICAO_CODIGO_REQUISICAO);

    throw new Exception("Nao foi possivel encontrar o codigo da requisicao.");
  }

  public Set<URI> obtemUrisDeImoveis(final String codigoRequisicao, final String uf) throws Exception {
    LOGGER.info("Obtendo todas as urls dos imoveis para a UF {}", uf);

    final String url = criarUriDeRequisicaoPorUf(codigoRequisicao, uf);
    final Document documento = jsoapService.getDocument(url);

    return extraiUris(documento);
  }

  private String criarUriDeRequisicaoPorUf(final String codigoRequisicao, final String uf) {
    return URL_POR_UF.replaceFirst("\\{codigoRequisicao\\}", codigoRequisicao).replaceFirst("\\{uf\\}", uf);
  }

  private Set<URI> extraiUris(final Document documento) {
    final Evaluator tagTr = new Evaluator.Tag("a");
    final Evaluator atributoHref = new Evaluator.Attribute("href");
    
    final Set<Element> elementosUri = documento.select(new Evaluator.AllElements()).stream()
        .filter(e -> e.is(tagTr))
        .filter(e -> e.is(atributoHref))
        .collect(Collectors.toSet());

    return elementosUri.stream().map(e -> converteStringParaURI(e.attr("href"))).collect(Collectors.toSet());
  }

  private URI converteStringParaURI(final String uri){
    try {
      return new URI(StringUtils.trimAllWhitespace(uri));
    } catch (URISyntaxException e) {
      LOGGER.error("Nao foi possivel converter string em uri, {}", uri, e);
      return null;
    }
  }
}