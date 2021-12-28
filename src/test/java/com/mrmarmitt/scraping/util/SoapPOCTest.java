package com.mrmarmitt.scraping.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.mrmarmitt.scraping.service.CaixaImovelService;
import com.mrmarmitt.scraping.service.CaixaListaImoveisService;
import com.mrmarmitt.scraping.service.JsoapService;
import com.mrmarmitt.scraping.service.ScrapingCaixaImpl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Evaluator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class SoapPOCTest {

  private CaixaListaImoveisService caixaService = new CaixaListaImoveisService(new JsoapService());

  @Test
  void connectionToWikipedia_test() throws IOException {
    Document doc = Jsoup.connect("https://www.wikipedia.org/").get();

    assertEquals("Wikipedia", doc.title());
  }

  @Test
  void conectarCaixa_paginaPrincipalPorUF_test() throws IOException {
    Document doc = Jsoup.connect("https://venda-imoveis.caixa.gov.br/sistema/download-lista.asp").get();

    assertEquals("Caixa - Imóvel à venda", doc.title());
  }

  @Test
  void conectarCaixa_requisitarListPorUF_test() throws IOException {
    Document doc = Jsoup.connect("https://venda-imoveis.caixa.gov.br/sistema/download-lista.asp").get();

    Evaluator scriptTag = new Evaluator.Tag("script");
    Evaluator functionName = new Evaluator.ContainsData("BaixarLista");

    Optional<Element> target = doc.select(new Evaluator.AllElements()).stream()
        .filter(e -> e.is(scriptTag))
        .filter(e -> e.is(functionName))
        .findFirst();

    System.out.println(target);
  }

  @Test
  void conectarCaixa_obterListaDeUrls_test() throws Exception {
    ScrapingCaixaImpl caixa = new ScrapingCaixaImpl(caixaService);
    CaixaImovelService caixaImovelService = new CaixaImovelService(new JsoapService());

    Set<URI> uris = caixa.obtemUrisDeImoveisPorUf("RS");

    uris.stream().forEach(uri -> {
      try {
        caixaImovelService.obtemImovelDto(uri);
      } catch (Exception e) {
        // e.printStackTrace();
      }
    });
  }

  @Test
  void conectarCaixa_obterImovel_test() throws Exception {
    CaixaImovelService caixa = new CaixaImovelService(new JsoapService());
    caixa.obtemImovelDto(new URI("https://venda-imoveis.caixa.gov.br/sistema/detalhe-imovel.asp?hdnOrigem=index&hdnimovel=1444406170315"));
  }

  @Test
  void string_valor_parse_test(){
    String valor = "Valor de avaliação: R$ 170.000,00 Valor mínimo de venda: R$ 109.820,01 ( desconto de 35,4%)";
    valor = StringUtils.remove(valor, ".");
    valor = StringUtils.replace(valor, ",", ".");

    List.of(valor.split("Valor")).stream().forEach(System.out::println);
    // List.of(valor.split(" ")).stream().filter(NumberUtils::isParsable).forEach(System.out::println);
    // System.out.println(NumberUtils.isParsable("170000.00"));
  }
}