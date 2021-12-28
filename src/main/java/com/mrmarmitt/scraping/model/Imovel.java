package com.mrmarmitt.scraping.model;

import java.net.URI;

import com.mrmarmitt.scraping.entity.ImovelEntity;

public class Imovel {
  
  private URI uri;

  private String uf;

  private String cidade;
  private String bairro;
  private String endereco;

  public Imovel(URI uri, String uf){
    this.uri = uri;
    this.uf = uf;
  }

  public void enriquecerImovel(String cidade, String bairro, String endereco){
    this.cidade = cidade;
    this.bairro = bairro;
    this.endereco = endereco;
  }

  public ImovelEntity construirEntidade(){
    ImovelEntity imovelEntity = new ImovelEntity();
    imovelEntity.setCidade(cidade);
    imovelEntity.setBairro(bairro);
    imovelEntity.setEndereco(endereco);

    return imovelEntity;
  }

}
