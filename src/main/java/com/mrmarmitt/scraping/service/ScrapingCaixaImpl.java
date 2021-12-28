package com.mrmarmitt.scraping.service;

import java.net.URI;
import java.util.Set;

import com.mrmarmitt.scraping.dto.ImovelDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ScrapingCaixaImpl {

  private final static Logger LOGGER = LoggerFactory.getLogger(ScrapingCaixaImpl.class);

  private final CaixaListaImoveisService caixaService;

  public ScrapingCaixaImpl(CaixaListaImoveisService caixaService){
    this.caixaService = caixaService;
  }

  public ImovelDto obtemImovelDto(final URI uri) {
    return null;
  }

  public Set<URI> obtemUrisDeImoveisPorUf(final String uf) throws Exception {
    LOGGER.info("Obtendo lista de urls da Caixa Economica Federal para a UF {}", uf);

    final String codigoRequisicao = caixaService.obtemCodigoObrigatorioParaRequisicao();
    final Set<URI> urisItensLeilao = caixaService.obtemUrisDeImoveis(codigoRequisicao, uf);

    if(urisItensLeilao.isEmpty())
      throw new Exception("Nenhum item encontrado para a UF " + uf);

    LOGGER.info("Execucao obteve {} item(ns).", urisItensLeilao.size());
    return urisItensLeilao;
  }

}
