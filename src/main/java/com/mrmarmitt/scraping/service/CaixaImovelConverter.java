package com.mrmarmitt.scraping.service;

import java.util.Optional;

import org.jsoup.nodes.Document;
import org.jsoup.select.Evaluator;

public class CaixaImovelConverter {

  private final Document documento;

  public CaixaImovelConverter(final Document documento) {
    this.documento = documento;
  }

  public String getTitulo() throws Exception {
    final Evaluator tagH5 = new Evaluator.Tag("h5");

    return this.documento.select(new Evaluator.AllElements()).stream()
        .filter(e -> e.is(tagH5))
        .map(e -> e.text())
        .findFirst()
        .orElseThrow(() -> new Exception("Nao foi encontrado o titulo do imovel."));
  }

  public String getValorDeAvaliacao() throws Exception {
    Optional<String> valorDeAvaliacaoTagH = getValorDeAvaliacaoTagH();
    Optional<String> valorDeAvaliacaoTagP = getValorDeAvaliacaoTagP();

    if (valorDeAvaliacaoTagH.isPresent())
      return valorDeAvaliacaoTagH.get();

    if (valorDeAvaliacaoTagP.isPresent())
      return valorDeAvaliacaoTagP.get();

    throw new Exception("Nao foi encontrado o valor de avaliacao.");
  }

  private Optional<String> getValorDeAvaliacaoTagP() {
    final Evaluator tagP = new Evaluator.Tag("p");
    final Evaluator textoValor = new Evaluator.ContainsText("Valor de avaliação");

    return documento.select(new Evaluator.AllElements()).stream()
        .filter(e -> e.is(tagP))
        .filter(e -> e.is(textoValor))
        .map(e -> e.text())
        .findFirst();
  }

  private Optional<String> getValorDeAvaliacaoTagH() {
    final Evaluator tagH4 = new Evaluator.Tag("h4");
    final Evaluator textoValor = new Evaluator.ContainsText("Valor de avaliação");

    return documento.select(new Evaluator.AllElements()).stream()
        .filter(e -> e.is(tagH4))
        .filter(e -> e.is(textoValor))
        .map(e -> e.text())
        .findFirst();
  }
}
