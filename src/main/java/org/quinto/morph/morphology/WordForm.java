package org.quinto.morph.morphology;

import java.util.Set;

public class WordForm {
  public CompressedLemma lemma;
  public Set< Grammeme > form;

  public WordForm( CompressedLemma lemma, Set< Grammeme > form ) {
    this.lemma = lemma;
    this.form = form;
  }
}