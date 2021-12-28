package com.mrmarmitt.scraping.service;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class JsoapService {

  public Document getDocument(String url) throws Exception {
    try {
      return Jsoup.connect(url).get();
    } catch (IOException e) {
      throw new Exception("Nao foi possivel conectar na url: " + url, e);
    }
  }

}
