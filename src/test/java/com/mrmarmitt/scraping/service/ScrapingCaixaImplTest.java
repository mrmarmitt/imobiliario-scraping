package com.mrmarmitt.scraping.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ScrapingCaixaImplTest {
  
  @InjectMocks
  private ScrapingCaixaImpl scrapingCaixaImpl;

  @Mock 
  private CaixaListaImoveisService caixaService;

  
}
